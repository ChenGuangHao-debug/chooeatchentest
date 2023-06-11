package chooeat.restaurant.daoimpl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import chooeat.restaurant.dao.RestaurantDAO;
import chooeat.restaurant.model.vo.AccountVO;
import chooeat.restaurant.model.vo.AdVO;
import chooeat.restaurant.model.vo.ProdVO;
import chooeat.restaurant.model.vo.ResTypeVO;
import chooeat.restaurant.model.vo.ReservationVO;
import chooeat.restaurant.model.vo.RestaurantVO;
@Repository
@Import(DataSourceAutoConfiguration.class)
public class RestaurantDaoImpl implements RestaurantDAO {
	@Autowired
	private DataSource dataSource;
	@Override
	public int restaurantregister(List<String> values) {		

		String sql = "INSERT INTO restaurant (res_acc, res_pass, res_state, res_name, res_add, res_tel, res_email, res_start_time, res_end_time, res_seat_number, single_meal, res_max_num) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);

			for (int i = 0; i < values.size(); i++) {
				pstmt.setString(i + 1, values.get(i));
			}

			pstmt.executeUpdate();
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;
	}

	@Override
	public List<RestaurantVO> searchrestaurantbyname(String searchString) {
		String sql = "SELECT * FROM restaurant WHERE res_name like ?";
		List<RestaurantVO> restaurantList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, searchString);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				RestaurantVO restaurant = new RestaurantVO();
				restaurant.setRestaurantId(rs.getInt("restaurant_id"));
				restaurant.setResAcc(rs.getString("res_acc"));
				restaurant.setResPass(rs.getString("res_pass"));
				restaurant.setResState(rs.getInt("res_state"));
				restaurant.setResName(rs.getString("res_name"));
				restaurant.setResAdd(rs.getString("res_add"));
				restaurant.setResTel(rs.getString("res_tel"));
				restaurant.setResEmail(rs.getString("res_email"));
				restaurant.setResWeb(rs.getString("res_web"));
				restaurant.setResTimestamp(rs.getTimestamp("res_timestamp"));
				restaurant.setResStartTime(rs.getTime("res_start_time"));
				restaurant.setResEndTime(rs.getTime("res_end_time"));
				restaurant.setResTexId(rs.getString("res_tex_id"));
				restaurant.setResOwnerName(rs.getString("res_owner_name"));
				restaurant.setResSeatNumber(rs.getInt("res_seat_number"));
				restaurant.setResIntro(rs.getString("res_intro"));
				restaurant.setSingleMeal(rs.getBoolean("single_meal"));
				restaurant.setResTotalScore(rs.getInt("res_total_score"));
				restaurant.setResTotalNumber(rs.getInt("res_total_number"));
				restaurant.setResMaxNum(rs.getInt("res_max_num"));
				byte[] photoBytes = rs.getBytes("res_photo");
				  if (photoBytes != null && photoBytes.length > 0) {
		                Byte[] photoWrapper = new Byte[photoBytes.length];
		                for (int i = 0; i < photoBytes.length; i++) {
		                    photoWrapper[i] = photoBytes[i];
		                }
		                restaurant.setResPhoto(photoWrapper);
		            } else {
		                restaurant.setResPhoto(null);
		            }		
				restaurantList.add(restaurant);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return restaurantList;
	}

	@Override
	public List<RestaurantVO> login(String account, String password) {
		String sql = "SELECT * FROM restaurant WHERE res_acc=? && res_pass=?";
		List<RestaurantVO> restaurantList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, account);
			pstmt.setString(2, password);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				RestaurantVO restaurant = new RestaurantVO();
				restaurant.setRestaurantId(rs.getInt("restaurant_id"));
				restaurant.setResAcc(rs.getString("res_acc"));
				restaurant.setResPass(rs.getString("res_pass"));
				restaurant.setResState(rs.getInt("res_state"));
				restaurant.setResName(rs.getString("res_name"));
				restaurant.setResAdd(rs.getString("res_add"));
				restaurant.setResTel(rs.getString("res_tel"));
				restaurant.setResEmail(rs.getString("res_email"));
				restaurant.setResWeb(rs.getString("res_web"));
				restaurant.setResTimestamp(rs.getTimestamp("res_timestamp"));
				restaurant.setResStartTime(rs.getTime("res_start_time"));
				restaurant.setResEndTime(rs.getTime("res_end_time"));
				restaurant.setResTexId(rs.getString("res_tex_id"));
				restaurant.setResOwnerName(rs.getString("res_owner_name"));
				restaurant.setResSeatNumber(rs.getInt("res_seat_number"));
				restaurant.setResIntro(rs.getString("res_intro"));
				restaurant.setSingleMeal(rs.getBoolean("single_meal"));
				restaurant.setResTotalScore(rs.getInt("res_total_score"));
				restaurant.setResTotalNumber(rs.getInt("res_total_number"));
				restaurant.setResMaxNum(rs.getInt("res_max_num"));
				byte[] photoBytes = rs.getBytes("res_photo");
				  if (photoBytes != null && photoBytes.length > 0) {
		                Byte[] photoWrapper = new Byte[photoBytes.length];
		                for (int i = 0; i < photoBytes.length; i++) {
		                    photoWrapper[i] = photoBytes[i];
		                }
		                restaurant.setResPhoto(photoWrapper);
		            } else {
		            	restaurant.setResPhoto(null);
		            }			
				restaurantList.add(restaurant);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return restaurantList;

	}

	@Override
	public List<Object> findfollow(String resAcc) {
		String sql = "select DISTINCT account.acc_id,acc_name from restaurant join \r\n" + "saved_res on \r\n"
				+ "restaurant.restaurant_id=saved_res.restaurant_id join account on\r\n"
				+ "saved_res.acc_id=account.acc_id where res_acc=?";
		List<Object> resultList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resAcc);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				AccountVO account = new AccountVO();
				account.setAccId(rs.getInt("account.acc_id"));
				account.setAccName(rs.getString("acc_name"));
				resultList.add(account);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public List<ReservationVO> restaurantfindreservation(String resAcc) {
		String sql = "SELECT reservation_id, reservation_number, reservation_date_starttime, " +
                "reservation_date_endtime, COALESCE(reservation_note, '') AS reservation_note, reservation_state " +
                "FROM restaurant " +
                "JOIN reservation ON restaurant.restaurant_id = reservation.restaurant_id " +
                "JOIN account ON reservation.acc_id = account.acc_id " +
                "WHERE res_acc = ?";
		List<ReservationVO> resultList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resAcc);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ReservationVO reservation = new ReservationVO();
				reservation.setReservationId(rs.getInt("reservation_id"));
				reservation.setReservationNumber(rs.getInt("reservation_number"));				
				reservation.setReservationStartTime(rs.getTimestamp("reservation_date_starttime"));
				reservation.setReservationEndTime(rs.getTimestamp("reservation_date_endtime"));
				reservation.setReservationNote(rs.getString("reservation_note"));
				reservation.setReservationState(rs.getInt("reservation_state"));
				resultList.add(reservation);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public List<Object> findrestype(String resAcc) {
		String sql = "SELECT  res_acc,res_type_name FROM restaurant \r\n"
				+ "JOIN res_type_detail ON restaurant.restaurant_id = res_type_detail.restaurant_id \r\n"
				+ "JOIN res_type ON res_type_detail.res_type_id = res_type.res_type_id\r\n" + "WHERE res_acc =?";
		List<Object> restypeList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resAcc);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ResTypeVO restypeVO = new ResTypeVO();
				restypeVO.setResTypeName(rs.getString("res_type_name"));
				restypeList.add(restypeVO);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return restypeList;
	}

	@Override
	public List<Object> findprod(String resAcc) {
		String sql = "SELECT DISTINCT  prod_name, prod_price FROM restaurant				\r\n"
				+ "JOIN prod ON restaurant.restaurant_id = prod.restaurant_id \r\n" + "WHERE res_acc = ?";
		List<Object> prodList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resAcc);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ProdVO prodVO = new ProdVO();
				prodVO.setProdName(rs.getString("prod_name"));
				prodVO.setProdPrice(rs.getInt("prod_price"));
				prodList.add(prodVO);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return prodList;
	}

	@Override
	public List<Object> findcomment(String resAcc) {
		String sql = "SELECT DISTINCT restaurant_comment_score,restaurant_comment_text,COALESCE(restaurant_comment_reply_text, '') AS restaurant_comment_reply_text, restaurant_comment_datetime, restaurant_comment_reply_datetime, acc_name FROM restaurant \r\n"
				+ "JOIN reservation ON restaurant.restaurant_id = reservation.restaurant_id \r\n"
				+ "JOIN account ON reservation.acc_id = account.acc_id WHERE res_acc = ? and restaurant_comment_text IS NOT NULL";
		List<Object> commentList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resAcc);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ReservationVO reservationVO = new ReservationVO();
				reservationVO.setRestaurantCommentScore(rs.getInt("restaurant_comment_score"));
				reservationVO.setRestaurantCommentText(rs.getString("restaurant_comment_text"));
				reservationVO.setRestaurantCommentReplyText(rs.getString("restaurant_comment_reply_text"));
				reservationVO.setRestaurantCommentDatetime(rs.getTimestamp("restaurant_comment_datetime"));
				Timestamp replyDatetime = rs.getTimestamp("restaurant_comment_reply_datetime");
		        reservationVO.setRestaurantCommentReplyDatetime(replyDatetime != null ? replyDatetime : Timestamp.valueOf(LocalDateTime.now()));
				commentList.add(reservationVO);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return commentList;
	}

	@Override
	public List<Object> findmyself(String resAcc) {
		String sql = "SELECT DISTINCT restaurant_id,res_photo,res_acc,res_name, res_add, res_start_time, res_end_time, \r\n"
				+ "res_total_score, res_intro FROM restaurant \r\n" + "			WHERE res_acc = ?";
		List<Object> findmyselfList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resAcc);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				RestaurantVO restaurantVO = new RestaurantVO();
				restaurantVO.setRestaurantId(rs.getInt("restaurant_id"));
				restaurantVO.setResAcc(rs.getString("res_acc"));
				restaurantVO.setResName(rs.getString("res_name"));
				restaurantVO.setResAdd(rs.getString("res_add"));				
				restaurantVO.setResStartTime(rs.getTime("res_start_time"));
				restaurantVO.setResEndTime(rs.getTime("res_end_time"));
				restaurantVO.setResTotalScore(rs.getInt("res_total_score"));
				restaurantVO.setResIntro(rs.getString("res_intro"));
				byte[] photoBytes = rs.getBytes("res_photo");
				  if (photoBytes != null && photoBytes.length > 0) {
		                Byte[] photoWrapper = new Byte[photoBytes.length];
		                for (int i = 0; i < photoBytes.length; i++) {
		                    photoWrapper[i] = photoBytes[i];
		                }
		                restaurantVO.setResPhoto(photoWrapper);
		            } else {
		            	restaurantVO.setResPhoto(null);
		            }					
				findmyselfList.add(restaurantVO);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return findmyselfList;
	}

	@Override
	public List<ProdVO> findpprod(String resAcc) {
		String sql = "SELECT DISTINCT prod_id,prod_name,prod_text,prod_userguide,prod_price,prod_qty,prod_state from restaurant\r\n"
				+ "JOIN prod ON restaurant.restaurant_id = prod.restaurant_id WHERE res_acc = ?";
		List<ProdVO> prodList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resAcc);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				ProdVO prodVO = new ProdVO();
				prodVO.setProdId(rs.getInt("prod_id"));
				prodVO.setProdName(rs.getString("prod_name"));
				prodVO.setProdText(rs.getString("prod_text"));
				prodVO.setProdUserGuide(rs.getString("prod_userguide"));
				prodVO.setProdPrice(rs.getInt("prod_price"));
				prodVO.setProdQty(rs.getInt("prod_qty"));
				prodVO.setProdState(rs.getInt("prod_state"));
				prodList.add(prodVO);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return prodList;
	}

	@Override
	public List<Object> findaccount(String resAcc) {
		String sql = "SELECT DISTINCT  acc_name,acc_pic FROM restaurant \r\n"
				+ "			JOIN reservation ON restaurant.restaurant_id = reservation.restaurant_id \r\n"
				+ "		JOIN account ON reservation.acc_id = account.acc_id WHERE res_acc =?";
		List<Object> accountList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resAcc);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				AccountVO accountVO = new AccountVO();
				accountVO.setAccName(rs.getString("acc_name"));
				byte[] accPicBytes = rs.getBytes("acc_pic");
				Byte[] accPic = null;
				if (accPicBytes != null) {
					accPic = new Byte[accPicBytes.length];
					for (int i = 0; i < accPicBytes.length; i++) {
						accPic[i] = accPicBytes[i];
					}
				}
				accountVO.setAccPic(accPic);
				accountList.add(accountVO);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return accountList;
	}

	@Override
	public int restaurantuploadprod(String key, String prodname, String prodprice, String prodnumber, String prodruler,
			String proddiscribe, String prodstatus, InputStream prodimageStream) {
		String sql = "INSERT INTO prod(restaurant_id, prod_name, prod_text, prod_userguide, prod_price, prod_qty, prod_state, prod_pic) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);

			// 檢查資料流是否有效
			if (prodimageStream != null && prodimageStream.available() > 0) {
				// 設定參數值
				pstmt.setString(1, key);
				pstmt.setString(2, prodname);
				pstmt.setString(3, proddiscribe);
				pstmt.setString(4, prodruler);
				pstmt.setInt(5, Integer.parseInt(prodprice));
				pstmt.setInt(6, Integer.parseInt(prodnumber));
				pstmt.setInt(7, Integer.parseInt(prodstatus));
				pstmt.setBlob(8, prodimageStream);
				pstmt.executeUpdate();
				pstmt.close();
				conn.close();
			} else {
				return 2;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		} catch (IOException e) {
			e.printStackTrace();
			return 2;
		}

		return 1;
	}

	@Override
	public int restaurantdeleteprod(String prodName, String restaurantId) {
		String sql = "delete  from prod\r\n" + "where restaurant_id=? and prod_name=?";
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, restaurantId);
			pstmt.setString(2, prodName);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;

	}

	@Override
	public int restaurantdeletefollow(String accId, String restaurantId) {
		String sql = "delete  from saved_res\r\n" + "where acc_id=? and restaurant_id=? ";
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, accId);
			pstmt.setString(2, restaurantId);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;
	}

	@Override
	public int restaurantforgetpassword(String account, String mail) {
		String sql = "SELECT restaurant_id FROM restaurant \r\n" + "where res_acc=? and res_email=?";
		int result = 0;
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, account);
			pstmt.setString(2, mail);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				result = 1;
			}
			rs.close();
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int restaurantforgetpasswordupdatepassword(int b ,String account) {
		String sql = "UPDATE restaurant SET res_pass = ?\r\n" + "WHERE res_acc = ?";
		int result = 0;
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, b);
			pstmt.setString(2, account);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				result = 1;
			}
		
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int restaurantdeletereservation(String reservationId, String restaurantId) {
		String sql = "delete  from reservation where reservation_id=? and restaurant_id=?";
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, reservationId);
			pstmt.setString(2, restaurantId);
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;
	}

	@Override
	public List<Object> restauranthomepagemyself(String resAcc) {		
		String sql = "SELECT DISTINCT * FROM restaurant WHERE res_acc = ?";
		List<Object> restauranthomepagemyselfList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resAcc);
			ResultSet rs = pstmt.executeQuery();		
			while(rs.next()) {				
				RestaurantVO restaurantVO = new RestaurantVO();
				restaurantVO.setRestaurantId(rs.getInt("restaurant_id"));
				restaurantVO.setResAcc(rs.getString("res_acc"));
				restaurantVO.setResPass(rs.getString("res_pass"));
				restaurantVO.setResState(rs.getInt("res_state"));
				restaurantVO.setResName(rs.getString("res_name"));
				restaurantVO.setResAdd(rs.getString("res_add"));
				restaurantVO.setResTel(rs.getString("res_tel"));
				restaurantVO.setResEmail(rs.getString("res_email"));
				restaurantVO.setResWeb(rs.getString("res_web"));
				restaurantVO.setResTimestamp(rs.getTimestamp("res_timestamp"));
				restaurantVO.setResStartTime(rs.getTime("res_start_time"));
				restaurantVO.setResEndTime(rs.getTime("res_end_time"));
				restaurantVO.setResTexId(rs.getString("res_tex_id"));
				restaurantVO.setResOwnerName(rs.getString("res_owner_name"));
				restaurantVO.setResSeatNumber(rs.getInt("res_seat_number"));
				restaurantVO.setResIntro(rs.getString("res_intro"));
				restaurantVO.setSingleMeal(rs.getBoolean("single_meal"));
				restaurantVO.setResTotalScore(rs.getInt("res_total_score"));
				restaurantVO.setResTotalNumber(rs.getInt("res_total_number"));
				restaurantVO.setResMaxNum(rs.getInt("res_max_num"));	
				byte[] photoBytes = rs.getBytes("res_photo");
				if (photoBytes != null && photoBytes.length > 0) {
	                Byte[] photoWrapper = new Byte[photoBytes.length];
	                for (int i = 0; i < photoBytes.length; i++) {
	                    photoWrapper[i] = photoBytes[i];
	                }
	                restaurantVO.setResPhoto(photoWrapper);
	            } else {
	            	restaurantVO.setResPhoto(null);
	            }		
				restauranthomepagemyselfList.add(restaurantVO);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return restauranthomepagemyselfList;
	}

	@Override
	public int restauranthomepageupdatebasic(String resAcc, String resPass, String resName, String resAdd,
			String resTel, String resEmail, String resSeatNumber, String resStartTime, String resEndTime) {
			String sql = "UPDATE restaurant SET res_pass =?,res_name=?,res_add=?,res_tel=?,res_email=?,res_seat_number=?,res_start_time=?,res_end_time=? WHERE res_acc = ?";
		int result = 0;
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, resPass);
			pstmt.setString(2, resName);
			pstmt.setString(3, resAdd);
			pstmt.setString(4, resTel);
			pstmt.setString(5, resEmail);
			pstmt.setString(6, resSeatNumber);
			pstmt.setString(7, resStartTime);
			pstmt.setString(8, resEndTime);
			pstmt.setString(9, resAcc);			
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				result = 1;
			}
		
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int restaurantuploaddayoff(String restaurantId, String date) {		
		String sql = "INSERT INTO restaurant_dayoff(restaurant_id,dayoff)\r\n"
				+ "VALUES (?, ?)";
		int result = 0;
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, restaurantId);
			pstmt.setString(2, date);			
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				result = 1;
			}			
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;
	}

	@Override
	public int restaurantdeletedayoff(String restaurantId) {
		String sql = "delete FROM restaurant_dayoff\r\n"
				+ "where restaurant_id=?";	
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, restaurantId);	
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();	
			return 2;
		}			
		return 1;
	}

	@Override
	public int restaurantddeletetype(String restaurantId) {
		String sql = "delete FROM res_type_detail\r\n"
				+ "		where restaurant_id=?";	
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, restaurantId);	
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();	
			return 2;
		}			
		return 1;
	}

	@Override
	public int restaurantuploadtype(String restaurantId, String date) {
		String sql = "INSERT INTO res_type_detail(restaurant_id,res_type_id)\r\n"
				+ "			VALUES (?, ?)";
		int result = 0;
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, restaurantId);
			pstmt.setString(2, date);			
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				result = 1;
			}			
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;
	}

	@Override
	public int restaurantuploadintro(String restaurantId, String intro) {
		String sql = "update restaurant\r\n"
				+ "set res_intro=?\r\n"
				+ "where restaurant_id=?";
		int result = 0;
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,intro);
			pstmt.setString(2,restaurantId);			
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				result = 1;
			}			
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;
	}

	@Override
	public int restaurantuploadimage(String restaurantId,  byte[] image) {
		String sql = "update restaurant\r\n"
				+ "		set res_photo=?\r\n"
				+ "		where restaurant_id=?";
		int result = 0;
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setBytes(1,image);
			pstmt.setString(2,restaurantId);			
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				result = 1;
			}			
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;
	}
	
	@Override
	public int restaurantuploadad(String restaurantId, String adplan, String adprice, String adstarttime,
			String adendtime,String strTimestamp,String adcheck) {
		String sql = "INSERT INTO ad(restaurant_id,ad_plan,ad_amount,ad_start_date,ad_end_date,ad_apply_timestamp,ad_check)\r\n"
				+ "VALUES (?,?,?,?,?,?,?)";
		int result = 0;
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,restaurantId);
			pstmt.setString(2,adplan);	
			pstmt.setString(3,adprice);	
			pstmt.setString(4,adstarttime);	
			pstmt.setString(5,adendtime);	
			pstmt.setString(6,strTimestamp);
			pstmt.setString(7,adcheck);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				result = 1;
			}			
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;
	}

	@Override
	public List<AdVO> restaurantfindad(String restaurantId) {
		String sql = "SELECT ad_id,ad_apply_timestamp,ad_start_date,ad_end_date,ad_amount,ad_plan,ad_check FROM ad		\r\n"
				+ "where restaurant_id=?";
		List<AdVO> adList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, restaurantId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				AdVO adVO = new AdVO();
				adVO.setAdId(rs.getInt("ad_id"));
				adVO.setAdApplyTimestamp(rs.getTimestamp("ad_apply_timestamp"));
				adVO.setAdStartDate(rs.getDate("ad_start_date"));
				adVO.setAdEndDate(rs.getDate("ad_end_date"));
				adVO.setAdAmount(rs.getInt("ad_amount"));
				adVO.setAdPlan(rs.getInt("ad_plan"));
				adVO.setAdCheck(rs.getInt("ad_check"));						
				adList.add(adVO);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return adList;
	}

	@Override
	public int restaurantdeletead(String adId, String restaurantId) {
		String sql = "delete FROM ad\r\n"
				+ "where ad_id=? and restaurant_id=?";	
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, adId);	
			pstmt.setString(2, restaurantId);	
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();	
			return 2;
		}			
		return 1;
	}
	@Override
	public int restaurantupdatecomment(String accName, String restaurantId, String commentInput) {
		String sql = "UPDATE reservation\r\n"
				+ "JOIN account ON reservation.acc_id = account.acc_id\r\n"
				+ "SET reservation.restaurant_comment_reply_datetime = CURRENT_TIMESTAMP,\r\n"
				+ "    reservation.restaurant_comment_reply_text = ?\r\n"
				+ "WHERE reservation.restaurant_id = ? AND account.acc_name = ?;\r\n"
				+ "";	
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, commentInput);	
			pstmt.setString(2, restaurantId);	
			pstmt.setString(3, accName);				
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();	
			return 2;
		}			
		return 1;
	}

	@Override
	public int restaurantupdateprod(String restaurantId, String prodName, String prodPrice, String prodQty,
			String prodUserGuide, String prodText, String prodState,String prodId) {
		String sql = "UPDATE prod\r\n"
				+ "set prod_name= ?, prod_text=?,prod_userguide=?,prod_price=?,prod_qty=?\r\n"
				+ "WHERE restaurant_id = ?and prod_id=?";
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);			
			pstmt.setString(1, prodName);	
			pstmt.setString(2, prodText);	
			pstmt.setString(3, prodUserGuide);	
			pstmt.setString(4, prodPrice);	
			pstmt.setString(5, prodQty);	
			pstmt.setString(6, restaurantId);	
			pstmt.setString(7, prodId);				
			pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();	
			return 2;
		}			
		return 1;
	}

	@Override
	public 	List<RestaurantVO>  getcarousel(int var1, int var2, int var3, int var4, int var5) {
		String sql = "SELECT restaurant_id,res_photo,res_acc FROM restaurant WHERE \r\n"
				+ "restaurant_id = ? or restaurant_id = ? or restaurant_id = ? or restaurant_id = ? or restaurant_id = ?";
		List<RestaurantVO> restaurantlList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, var1);
			pstmt.setLong(2, var2);
			pstmt.setLong(3, var3);
			pstmt.setLong(4, var4);
			pstmt.setLong(5, var5);			
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				RestaurantVO restaurantVO = new RestaurantVO();
				restaurantVO.setRestaurantId(rs.getInt("restaurant_id"));
				restaurantVO.setResAcc(rs.getString("res_acc"));
				byte[] photoBytes = rs.getBytes("res_photo");
				  if (photoBytes != null && photoBytes.length > 0) {
		                Byte[] photoWrapper = new Byte[photoBytes.length];
		                for (int i = 0; i < photoBytes.length; i++) {
		                    photoWrapper[i] = photoBytes[i];
		                }
		                restaurantVO.setResPhoto(photoWrapper);
		            } else {
		            	restaurantVO.setResPhoto(null);
		            }					
				  restaurantlList.add(restaurantVO);
			}
			rs.close();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return restaurantlList;
	}

	@Override
	public int restaurantaddmylove(String restaurantId, String accId) {
		
		String sql = "INSERT INTO saved_res (restaurant_id, acc_id)\r\n"
				+ "VALUES (?, ?);";
		int result = 0;
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,restaurantId);
			pstmt.setString(2,accId);				
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected > 0) {
				result = 1;
			}			
			pstmt.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			return 2;
		}
		return 1;
	}
}
