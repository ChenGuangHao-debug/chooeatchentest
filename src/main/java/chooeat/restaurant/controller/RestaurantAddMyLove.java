package chooeat.restaurant.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;

import chooeat.restaurant.service.RestaurantService;

@WebServlet("/restaurant/restaurantaddmylove")
public class RestaurantAddMyLove extends HttpServlet {
	 @Autowired
	    private RestaurantService RestaurantService;

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// 設置跨域
		response.setHeader("Access-Control-Allow-Origin", "*"); // 允許來自所有網域的請求
		response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE"); // 允許的 HTTP 方法
		response.setHeader("Access-Control-Allow-Headers", "Content-Type"); // 允許的請求頭
		response.setHeader("Access-Control-Allow-Credentials", "true"); // 是否允許帶有憑證的請求
		// 設置返回格式
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("utf-8");
		// 取得請求參數

		String a = request.getParameter("restaurantId");
		String b = request.getParameter("accId");	
		int c=RestaurantService.restaurantaddmylove(a,b);
		Gson gson = new Gson();
		String jsonStr = gson.toJson(c);
		response.getWriter().write(jsonStr);
	}
}
