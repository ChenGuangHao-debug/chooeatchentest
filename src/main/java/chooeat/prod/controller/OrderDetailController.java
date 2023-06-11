package chooeat.prod.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import chooeat.prod.dao.OrderDao;
import chooeat.prod.dao.OrderDetailDao;
import chooeat.prod.dao.ProdOrderDetailDao;
import chooeat.prod.dao.impl.OrderDaoImpl;
import chooeat.prod.dao.impl.OrderDetailDaoImpl;
import chooeat.prod.dao.impl.ProdOrderDetailDaoImpl;
import chooeat.prod.model.vo.Order;
import chooeat.prod.model.vo.OrderDetail;
import chooeat.prod.model.vo.ProdOrderDetail;
import chooeat.prod.service.impl.OrderDetailServiceImpl;
import redis.clients.jedis.Jedis;

@WebServlet("/prod/order")
public class OrderDetailController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	@Autowired
	private OrderDaoImpl orderDaoImpl;
	@Autowired
	private ProdOrderDetailDaoImpl prodOrderDetailDaoImpl;
	@Autowired
	private OrderDetailDaoImpl orderDetailDaoImpl;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// 設置接收格式
		req.setCharacterEncoding("utf-8");
		// 設置跨域
		res.setHeader("Access-Control-Allow-Origin", "*"); // 允許來自所有網域的請求
		res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE"); // 允許的 HTTP 方法
		res.setHeader("Access-Control-Allow-Headers", "Content-Type"); // 允許的請求頭
		res.setHeader("Access-Control-Allow-Credentials", "true"); // 是否允許帶有憑證的請求
		// 設置返回格式
		res.setContentType("application/json; charset=utf-8");

		// 讀取請求
		BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
		StringBuilder requestBody = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {
			requestBody.append(line);
		}
		reader.close();

		// 解析 JSON 字串
		Gson gson = new Gson();
		JsonObject jsonObject = gson.fromJson(requestBody.toString(), JsonObject.class);

		// 取值
		JsonObject jsonData = jsonObject.getAsJsonObject("cartData");
		int accId = jsonObject.get("memberId").getAsInt();
		String originalAmountStr = jsonObject.get("originalAmount").getAsString().replaceAll("[^0-9]", "");
		String checkoutAmountStr = jsonObject.get("checkoutAmount").getAsString().replaceAll("[^0-9]", "");
		int originalAmount = Integer.parseInt(originalAmountStr);
		int checkoutAmount = Integer.parseInt(checkoutAmountStr);

		// 訂單物件
		Order order = new Order();
		order.setAccId(accId);
		order.setOrderState(0);
		order.setAmountBeforeCoupon(originalAmount);
		order.setFinalAmount(checkoutAmount);

		// OrderDAOImpl
		int generatedOrderId = orderDaoImpl.insert(order);
		if (generatedOrderId != -1) {
			System.out.println("訂單建立成功！ORDER_ID為：" + generatedOrderId);
		} else {
			System.out.println("無法建立訂單。");
		}
		// 訂單明細物件
		for (String productId : jsonData.keySet()) {
			JsonObject productData = jsonData.getAsJsonObject(productId);
			int qty = productData.get("qty").getAsInt();
			String unipriceStr = productData.get("uniprice").getAsString().replaceAll("[^0-9]", "");
			int uniprice = Integer.parseInt(unipriceStr);
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrderId(generatedOrderId);
			orderDetail.setProdId(Integer.parseInt(productId));
			orderDetail.setProdPrice(uniprice);
			orderDetail.setOrderProdQty(qty);
			// OrderDetailDAOImpl

			int generatedOrderDetailId = orderDetailDaoImpl.insert(orderDetail);

			if (generatedOrderDetailId != -1) {
				System.out.println("訂單明細建立成功！ORDER_DETAIL_ID為：" + generatedOrderDetailId);
			} else {
				System.out.println("無法建立訂單明細。");
			}
			// ProdOrderDetailDAOImpl
			// 訂單明細物件
			ProdOrderDetail prodOrderDetail = new ProdOrderDetail();
			prodOrderDetail.setOrderDetailId(generatedOrderDetailId);
			prodOrderDetail.setProdOrderCode(OrderDetailServiceImpl.generateRandomCode());
			prodOrderDetail.setProdOrderState(0);

			int generatedProdOrderDetailId = prodOrderDetailDaoImpl.insert(prodOrderDetail);

			if (generatedProdOrderDetailId != -1) {
				System.out.println("訂單明細建立成功！ORDER_DETAIL_ID為：" + generatedProdOrderDetailId);
			} else {
				System.out.println("無法建立訂單明細。");
			}
		}

		// 刪除Redis相應資料
		Jedis jedis = null;
		try {
			jedis = new Jedis("localhost", 6379);
			String cartKey = "cart:user"+accId; // 實際情況+ memberId
			jedis.select(1);

			JsonParser parser = new JsonParser();
			JsonObject cartData = parser.parse(jsonData.toString()).getAsJsonObject();

			for (String productId : cartData.keySet()) {
				String redisKey = "productId:" + productId;
				jedis.hdel(cartKey, redisKey);
				System.out.println("已從Redis刪除資料，鍵值：" + redisKey);
			}

			res.getWriter().println("{\"status\": \"success\"}");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}

		// 返回回應結果
		res.setContentType("text/plain");
		res.getWriter().write("Request processed successfully.");
	}

}
