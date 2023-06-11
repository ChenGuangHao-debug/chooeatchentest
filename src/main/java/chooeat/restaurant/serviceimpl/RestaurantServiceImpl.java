package chooeat.restaurant.serviceimpl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import chooeat.restaurant.dao.ResTypeDAO;
import chooeat.restaurant.dao.RestaurantDAO;
import chooeat.restaurant.model.vo.AdVO;
import chooeat.restaurant.model.vo.ProdVO;
import chooeat.restaurant.model.vo.ReservationVO;
import chooeat.restaurant.model.vo.RestaurantVO;
import chooeat.restaurant.service.RestaurantService;
@Service
public class RestaurantServiceImpl<K> implements RestaurantService {
	@Autowired
	private RestaurantDAO dao;
	@Autowired
	private ResTypeDAO ddao;
	@Override
	public List<RestaurantVO> searchrestaurantbyname(String searchInput) {
		String searchString = "%" + searchInput + "%";
		return dao.searchrestaurantbyname(searchString);
	}

	@Override
	public void sendMail(String to, String subject, String messageText) {

		try {
			// 設定使用SSL連線至 Gmail smtp Server
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");

			// ●設定 gmail 的帳號 & 密碼 (將藉由你的Gmail來傳送Email)
			// ●1) 登入你的Gmail的:
			// ●2) 點選【管理你的 Google 帳戶】
			// ●3) 點選左側的【安全性】

			// ●4) 完成【兩步驟驗證】的所有要求如下:
			// ●4-1) (請自行依照步驟要求操作之.....)

			// ●5) 完成【應用程式密碼】的所有要求如下:
			// ●5-1) 下拉式選單【選取應用程式】--> 選取【郵件】
			// ●5-2) 下拉式選單【選取裝置】--> 選取【Windows 電腦】
			// ●5-3) 最後按【產生】密碼
			final String myGmail = "m128643750@gmail.com";
			final String myGmail_password = "bglsqzyvhtxdjytc";
			Session session = Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(myGmail, myGmail_password);
				}
			});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(myGmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

			// 設定信中的主旨

			message.setSubject(subject);
			// 設定信中的內容
			message.setText(messageText);

			Transport.send(message);
			System.out.println("傳送成功!");
		} catch (MessagingException e) {
			System.out.println("傳送失敗!");
			e.printStackTrace();
		}

	}

	@Override
	public List<RestaurantVO> login(String account, String password) {
		List<RestaurantVO> restaurantList = dao.login(account, password);

		return restaurantList;

	}

	@Override
	public int restaurantregister(List<String> values) {

		return dao.restaurantregister(values);
	}

	@Override
	public List<Object> findfollow(String resAcc) {

		return dao.findfollow(resAcc);
	}

	@Override
	public List<ReservationVO> restaurantfindreservation(String resAcc) {

		return dao.restaurantfindreservation(resAcc);
	}

	@Override
	public Map<String, Object> forwardresdetail(String resAcc) {
		Map<String, Object> resultMap = new HashMap<>();	
		List<Object> restypeList = dao.findrestype(resAcc);
		List<Object> prodList = dao.findprod(resAcc);
		List<Object> commentList = dao.findcomment(resAcc);
		List<Object> myselfList = dao.findmyself(resAcc);
		List<Object> accountList = dao.findaccount(resAcc);
		addToResultMap(resultMap, "restype", restypeList);
		addToResultMap(resultMap, "prod", prodList);
		addToResultMap(resultMap, "comment", commentList);
		addToResultMap(resultMap, "myself", myselfList);
		addToResultMap(resultMap, "account", accountList);
		return resultMap;
	}

	private void addToResultMap(Map<String, Object> resultMap, String key, List<Object> valueList) {
		List<Object> flattenedList = new ArrayList<>();
		for (Object value : valueList) {
			if (value instanceof List<?>) {
				flattenedList.addAll((List<?>) value);
			} else {
				flattenedList.add(value);
			}
		}
		if (flattenedList.isEmpty()) {
		    resultMap.put(key, new Object[0]);
		} else if (flattenedList.size() == 1) {
		    resultMap.put(key, new Object[] { flattenedList.get(0) });
		} else {
		    resultMap.put(key, flattenedList.toArray());
		}
	}

	@Override
	public List<ProdVO> findprod(String resAcc) {
		return dao.findpprod(resAcc);
	}

	@Override
	public List<RestaurantVO> searchRestaurantsbytag(String tag) {
		return ddao.searchrestaurantbytag(tag);
	}

	@Override
	public int restaurantuploadprod(String key, String prodname, String prodprice, String prodnumber, String prodruler,
			String proddiscribe, String prodstatus, InputStream prodimageStream) {
		return dao.restaurantuploadprod(key, prodname, prodprice, prodnumber, prodruler, proddiscribe, prodstatus,
				prodimageStream);
	}

	@Override
	public int restaurantdeleteprod(String prodName, String restaurantId) {

		return dao.restaurantdeleteprod(prodName, restaurantId);
	}

	@Override
	public int restaurantdeletefollow(String accId, String restaurantId) {

		return dao.restaurantdeletefollow(accId, restaurantId);
	}

	@Override
	public Map<String, Object> restaurantfindcomment(String resAcc) {
		Map<String, Object> resultMap = new HashMap<>();

		List<Object> commentList = dao.findcomment(resAcc);

		List<Object> accountList = dao.findaccount(resAcc);

		addToResultMap(resultMap, "comment", commentList);

		addToResultMap(resultMap, "account", accountList);
		return resultMap;
	}

	@Override
	public int restaurantforgetpassword(String account, String mail) {
		int a = dao.restaurantforgetpassword(account, mail);
		int d = 0;
		if (a == 1) {
			int b = 5487;
			d = dao.restaurantforgetpasswordupdatepassword(b, account);			
		}		
		return d;
	}

	@Override
	public int restaurantdeletereservation(String reservationId, String restaurantId) {
	
		return dao.restaurantdeletereservation(reservationId, restaurantId);
	}

	@Override
	public Map<String, Object> restauranthomepage(String resAcc) {
		Map<String, Object> resultMap = new HashMap<>();
		
		List<Object> restauranthomepagemyselfList=dao.restauranthomepagemyself(resAcc);
		System.out.println(restauranthomepagemyselfList);		
		
		addToResultMap(resultMap, "restauranthomepagemyselfList",restauranthomepagemyselfList);

		return resultMap;	
		
	}

	@Override
	public int restauranthomepageupdatebasic(String resAcc,String resPass, String resName, String resAdd, String resTel,
			String resEmail, String resSeatNumber, String resStartTime, String resEndTime) {
		
		
		return dao.restauranthomepageupdatebasic(resAcc,resPass,resName,  resAdd,  resTel,resEmail, resSeatNumber, resStartTime,  resEndTime)
			
		;
	}

	@Override
	public int restaurantuploaddayoff(String restaurantId, String dates) {
		int a=	dao.restaurantdeletedayoff(restaurantId);		
		 dates = dates.replaceAll("\\[", "").replaceAll("\\]", "");
		 dates = dates.replaceAll("\"", "").replaceAll("'", "");
		 String[] dateArray = dates.split(",");
		    for (String date : dateArray) {		    	 
		        int result = dao.restaurantuploaddayoff(restaurantId,date);		      
		    }		 		    
		    return dateArray.length;
	}

	@Override
	public int restaurantuploadtype(String restaurantId, String type) {
		 int a=	dao.restaurantddeletetype(restaurantId);		
		 type = type.replaceAll("\\[", "").replaceAll("\\]", "");
		 type = type.replaceAll("\"", "").replaceAll("'", "");
		 String[] dateArray =  type.split(",");
		    for (String date : dateArray) {		    	 
		        int result = dao.restaurantuploadtype(restaurantId,date);		      
		    }		 		    
		    return dateArray.length;
	}

	@Override
	public int restaurantuploadintro(String restaurantId, String intro) {
			return dao.restaurantuploadintro(restaurantId,intro);
	
	}

	@Override
	public int restaurantuploadimage(String restaurantId,  byte[] image) {
		
		return dao.restaurantuploadimage(restaurantId,image);
	}

	@Override
	public int restaurantuploadad(String restaurantId, String adplan, String adprice, String adstarttime,
			String adendtime,String strTimestamp,String adcheck) {
		
		return dao.restaurantuploadad(restaurantId,adplan,adprice,adstarttime,adendtime,strTimestamp,adcheck);
		
	}

	@Override
	public List<AdVO> restaurantfindad(String restaurantId) {
		
		return dao.restaurantfindad(restaurantId);
	}

	@Override
	public int restaurantdeletead(String adId, String restaurantId) {
	
		return dao.restaurantdeletead(adId, restaurantId);
	}
	@Override
	public int restaurantupdatecomment(String accName, String restaurantId, String commentInput) {
		
		return dao.restaurantupdatecomment(accName, restaurantId,commentInput);
	}

	@Override
	public int restaurantupdateprod(String restaurantId, String prodName, String prodPrice, String prodQty,
			String prodUserGuide, String prodText, String prodState,String prodId) {
		return dao.restaurantupdateprod(restaurantId,prodName,prodPrice,prodQty,prodUserGuide,prodText,prodState,prodId);
	}

	@Override
	public List<RestaurantVO> getcarousel(Set<Integer> randomNumbers) {
		
        Iterator<Integer> iterator = randomNumbers.iterator();
        int[] variables = new int[5];
        int index = 0;
        while (iterator.hasNext() && index < 5) {
            int randomNumber = iterator.next();
            variables[index] = randomNumber;
            index++;
        }
        int var1 = variables[0];
        int var2 = variables[1];
        int var3 = variables[2];
        int var4 = variables[3];
        int var5 = variables[4];
    	
   	

		return dao.getcarousel(var1,var2,var3,var4,var5);
	}

	@Override
	public int restaurantaddmylove(String restaurantId, String accId) {
		
		return dao.restaurantaddmylove(restaurantId,accId);
	}

	

}
