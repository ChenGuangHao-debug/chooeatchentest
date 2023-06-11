package chooeat.restaurant.controller;

import java.io.IOException;
import java.sql.Time;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import chooeat.Utils.CustomTimeSerializer;
import chooeat.restaurant.model.vo.RestaurantVO;
import chooeat.restaurant.service.RestaurantService;
@WebServlet("/restaurant/restaurantlogin")
public class RestaurantLogin extends HttpServlet {
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
        
        String a=request.getParameter("account");
        String b=request.getParameter("password");    
        String c=request.getParameter("remember");       
                        
    	List<RestaurantVO> restaurantList =RestaurantService.login(a,b);
    	
    	if(c.equals("on")) {    		
    		  Cookie cookie = new Cookie("account", a);
    		  Cookie cookie1 = new Cookie("password", b);    	      
    	        cookie.setMaxAge(3600);
    	        cookie.setPath("/");
    	        cookie.setDomain((request.getServerName()));
    	        response.addCookie(cookie);
    	        cookie1.setMaxAge(3600);
    	        cookie1.setPath("/");
    	        cookie1.setDomain((request.getServerName())); 	              
    	        response.addCookie(cookie1);   
    	}else {
    		  Cookie cookie = new Cookie("account", "");
    		  Cookie cookie1 = new Cookie("password","");  
    		  cookie.setMaxAge(3600);
  	        cookie.setPath("/");
  	        cookie.setDomain((request.getServerName()));
  	        response.addCookie(cookie);
  	        cookie1.setMaxAge(3600);
  	        cookie1.setPath("/");
  	        cookie1.setDomain((request.getServerName())); 	              
  	        response.addCookie(cookie1);   
    		  
		}
        
    	Gson gson = new GsonBuilder().registerTypeAdapter(Time.class, new CustomTimeSerializer()).create();
		String jsonStr = gson.toJson(restaurantList);		
		response.getWriter().write(jsonStr);
   }
}
