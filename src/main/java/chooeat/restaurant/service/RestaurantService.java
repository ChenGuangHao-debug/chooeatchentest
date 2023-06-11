package chooeat.restaurant.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import chooeat.restaurant.model.vo.AdVO;
import chooeat.restaurant.model.vo.ProdVO;
import chooeat.restaurant.model.vo.ReservationVO;
import chooeat.restaurant.model.vo.RestaurantVO;

public interface RestaurantService {
    List<RestaurantVO> searchrestaurantbyname(String searchInput);
    List<RestaurantVO> searchRestaurantsbytag(String tag);
    int restaurantregister(List<String> values);
    List<RestaurantVO> login(String account,String password);
    public void sendMail(String to, String subject, String messageText) ;
    List<Object> findfollow(String resAcc);
	List<ReservationVO> restaurantfindreservation(String resAcc);
	Map<String, Object> forwardresdetail(String resAcc);
	List<ProdVO> findprod(String resAcc);
	int restaurantuploadprod(String key, String prodname, String prodprice, String prodnumber, String prodruler, String proddiscribe, String prodstatus, InputStream prodimageStream);
	int restaurantdeleteprod(String prodName,String restaurantId);
	int restaurantdeletefollow(String accId, String restaurantId);
	Map<String, Object> restaurantfindcomment(String resAcc);
	int restaurantforgetpassword(String account, String mail);
	int restaurantdeletereservation(String reservationId, String restaurantId);
	Map<String, Object> restauranthomepage(String resAcc);
	int restauranthomepageupdatebasic(String resAcc,String resPass, String resName, String resAdd, String resTel, String resEmail, String resSeatNumber, String resStartTime, String resEndTime);
	int restaurantuploaddayoff(String restaurantId, String dates);
	int restaurantuploadtype(String restaurantId, String type);
	int restaurantuploadintro(String restaurantId, String intro);
	int restaurantuploadimage(String restaurantId, byte[] image);
	int restaurantuploadad(String restaurantId, String adplan, String adprice, String adstarttime, String adendtime, String strTimestamp, String adcheck);
	List<AdVO> restaurantfindad(String restaurantId);
	int restaurantdeletead(String adId, String restaurantId);
	int restaurantupdatecomment(String accName, String restaurantId, String commentInput);
	int restaurantupdateprod(String restaurantId, String prodName, String prodPrice, String prodQty, String prodUserGuide, String prodText, String prodState, String prodId);
	List<RestaurantVO> getcarousel(Set<Integer> randomNumbers);
	int restaurantaddmylove(String resAcc, String accId);
}
