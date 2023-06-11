package chooeat.restaurant.daoimpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Repository;

import chooeat.restaurant.dao.ResTypeDAO;
import chooeat.restaurant.model.vo.RestaurantVO;

@Repository
@Import(DataSourceAutoConfiguration.class)
public class ResTypeDAOImpl implements ResTypeDAO  {
	   @Autowired
	    private DataSource dataSource;
	 
	@Override
	public List<RestaurantVO> searchrestaurantbytag(String tag) {		
		
		String sql = "SELECT * FROM res_type join res_type_detail\r\n"
				+ "on  res_type.res_type_id=res_type_detail.res_type_id\r\n"
				+ "join restaurant on  res_type_detail.restaurant_id =restaurant.restaurant_id\r\n"
				+ "where res_type_name=?";
		List<RestaurantVO> restaurantList = new ArrayList<>();
		try {
			Connection conn = dataSource.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, tag);
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
	
	
	

}
