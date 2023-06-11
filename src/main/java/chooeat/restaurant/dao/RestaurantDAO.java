
package chooeat.restaurant.dao;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

import chooeat.restaurant.model.vo.AdVO;
import chooeat.restaurant.model.vo.ProdVO;
import chooeat.restaurant.model.vo.ReservationVO;
import chooeat.restaurant.model.vo.RestaurantVO;

public interface RestaurantDAO {
	int restaurantregister(List<String> values);		
	List<RestaurantVO> searchrestaurantbyname(String searchString);	
	List<RestaurantVO> login(String account,String password);	
	List<Object> findfollow(String resAcc);	
	List<ReservationVO> restaurantfindreservation(String resAcc);	
	List<Object> findrestype(String resAcc);	
	List<Object> findprod(String resAcc);	
	List<Object> findcomment(String resAcc);	
	List<Object> findmyself(String resAcc);	
	List<ProdVO> findpprod(String resAcc);
	List<Object> findaccount(String resAcc);
	int restaurantuploadprod(String key, String prodname, String prodprice, String prodnumber, String prodruler,
			String proddiscribe, String prodstatus, InputStream prodimageStream);
	int restaurantdeleteprod(String prodName, String restaurantId);
	int restaurantdeletefollow(String accName, String restaurantId);
	int restaurantforgetpassword(String account, String mail);
	int restaurantforgetpasswordupdatepassword(int b ,String account);
	int restaurantdeletereservation(String reservationId, String restaurantId);
	List<Object> restauranthomepagemyself(String resAcc);
	int restauranthomepageupdatebasic(String resAcc,String resPass, String resName, String resAdd, String resTel, String resEmail,
			String resSeatNumber, String resStartTime, String resEndTime);
	int restaurantuploaddayoff(String restaurantId, String date);
	int restaurantdeletedayoff(String restaurantId);
	int restaurantddeletetype(String restaurantId);
	int restaurantuploadtype(String restaurantId, String date);
	int restaurantuploadintro(String restaurantId, String intro);
	int restaurantuploadimage(String restaurantId, byte[] image);
	int restaurantuploadad(String restaurantId, String adplan, String adprice, String adstarttime, String adendtime, String strTimestamp, String adcheck);
	List<AdVO> restaurantfindad(String restaurantId);
	int restaurantdeletead(String adId, String restaurantId);
	int restaurantupdatecomment(String accName, String restaurantId, String commentInput);
	int restaurantupdateprod(String restaurantId, String prodName, String prodPrice, String prodQty,
			String prodUserGuide, String prodText, String prodState, String prodId);
	List<RestaurantVO> getcarousel(int var1, int var2, int var3, int var4, int var5);
	int restaurantaddmylove(String resAcc, String accId);
	
	
}