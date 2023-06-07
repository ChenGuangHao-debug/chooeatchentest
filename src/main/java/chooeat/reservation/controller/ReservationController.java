package chooeat.reservation.controller;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chooeat.reservation.model.EmailDetails;
import chooeat.reservation.model.EmailInfo;
import chooeat.reservation.model.ReservationVO;
import chooeat.reservation.model.RestaurantVO;
import chooeat.reservation.model.Result;
import chooeat.reservation.service.ReservationService;
import redis.clients.jedis.Jedis;

@RestController
public class ReservationController {
	// 注入service物件
	@Autowired
	ReservationService reservationService;
//	@Autowired
//	Result result;
//	@Autowired
//	ReservationVO reservationVO;
	@Autowired
	EmailDetails details;
	@Autowired
	RestaurantVO restaurantVO;

	int count = 1;
	String index = "res:" + count;

	// 選擇日期時下判斷的controller
	@GetMapping("/getBusinessDay")
	public Result getBusinessDay(@RequestParam("date") String date, @RequestParam("acc_id") int acc_id,
			@RequestParam("restaurantId") int restaurantId) {
		Result result = new Result();
		System.out.println(date);

		// 如果是公休日，就回傳"status", "dayoff"
		if (reservationService.isDayOff(restaurantId, date)) {
			result.setStatus("dayoff");
			System.out.println("dayoff");
		} else {
			result.setStatus("BusinessDay");
			System.out.println("BusinessDay");

			// 以下抓出營業時間並回傳前端
			restaurantVO = reservationService.selectResInfo(restaurantId).get();
			result.setResStartTime(restaurantVO.getResStartTime().toString());
			result.setResEndTime(restaurantVO.getResEndTime().toString());

			// 以下檢查當天每個時段的剩餘座位數
			result.setHourlySeatlist(reservationService.findHourlySeats(restaurantId, date));

			// 檢查當天該會員是否有預約
			result.setReservedList(reservationService.reservedData(acc_id, restaurantId, date));

			// 以下抓出該餐廳的剩餘座位數並回傳前端
			result.setRemainSeat(restaurantVO.getResMaxNum());
		}

		return result;
	}

	// 訂位輸入成功，傳入redis，進入結帳頁面
	@PostMapping("/reservationRedis")
	@Transactional
	public Result reservationRedis(@RequestBody Map<String, Object> map) {
		
		synchronized (this) {
			Result result = new Result();

			// redis連線-接3號DB
			Jedis jedis = new Jedis();
			jedis.select(3);
			
			//設判斷，判斷redis是否有指定的資料，如果沒有，就新增，如果有，就抓出來，如果小於等於0，就擋掉

			// 接前端的參數
			String dateTime = (String) map.get("date_time");
			Integer reservationNumber = Integer.valueOf((String) map.get("ppl"));
			String text = (String) map.get("text");
			Double acc_id = (Double) map.get("acc_id");
			Double restaurantId = (Double) map.get("restaurantId");
			System.out.println("acc_id + " + acc_id);
			System.out.println("restaurantId + " + restaurantId);
			System.out.println(dateTime);
			
			//查詢當天該時段的剩餘座位數 - 訂位人數 是否等於0
			
			
			
			

			try {
				// 以下存入redis
				HashMap<String, String> data = new HashMap<>();
				data.put("accId", new Integer(acc_id.intValue()).toString());
				data.put("restaurantId", new Integer(restaurantId.intValue()).toString());
				data.put("reservationNumber", (String) map.get("ppl"));
				data.put("reservationDateStartTime", (String) map.get("date_time"));
				if (!text.isEmpty()) {
					data.put("reservationNote", text);
				} else {
					data.put("reservationNote", "");
				}

				jedis.hmset(index, data);

				result.setStatus("success");
				jedis.close();
			} catch (Exception e) {
				result.setStatus("error");
			}

			return result;

		}
	}

	// 結帳確認後發出請求，如果成功，先將資料insert進資料庫，後刪除redis內數據
	@GetMapping("/reservation")
	@Transactional
	public Result reserve() {
		Result result = new Result();
		// 從redis抓出暫存的預約資料，設定給vo
		Jedis jedis = new Jedis();
		jedis.select(2);
		Map<String, String> retrievedData = jedis.hgetAll(index);
		ReservationVO reservationVO = new ReservationVO();
		reservationVO.setAccId(Integer.valueOf(retrievedData.get("accId")));
		reservationVO.setRestaurantId(Integer.valueOf(retrievedData.get("restaurantId")));
		reservationVO.setReservationNumber(Integer.valueOf(retrievedData.get("reservationNumber")));

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime dateTime = LocalDateTime.parse(retrievedData.get("reservationDateStartTime"), formatter);
		Timestamp timestamp = Timestamp.valueOf(dateTime);

		reservationVO.setReservationDateStartTime(timestamp);
		reservationVO.setReservationNote(retrievedData.get("reservationNote"));

		// 預約成功，回傳訂單編號
		int reservationId = reservationService.reservation(reservationVO);
		// 刪除redis存放的資料
		jedis.del(index);
		if (reservationId != 0) {
			result.setStatus("success");
			reservationService.sendMail(reservationVO.getAccId(), reservationId);
			result.setReservationId(reservationId);
		} else {
			result.setStatus("");
		}

		return result;
	}

	// 取得餐廳名稱，print在畫面上（訂位頁跟訂位成功頁）
	@GetMapping("/restaurantName")
	public String getRestaurantName(@RequestParam("restaurantId") String restaurantId) {

		return reservationService.selectResInfo(Integer.valueOf(restaurantId)).get().getResName();

	}

	// 取得餐廳地址，串接地圖導航功能（訂位成功頁）
	@GetMapping("/restaurantAddress")
	public String getRestaurantAddress(@RequestParam("restaurantId") String restaurantId) {

		return reservationService.selectResInfo(Integer.valueOf(restaurantId)).get().getResAdd();
	}

	// 查詢該會員所有預約紀錄，print在會員頁面上
	@GetMapping("/getAllreservation")
	public List<EmailInfo> getAllreservation(@RequestParam("accId") int accId) {
		return reservationService.getAllreservation(accId);

	}

	// 用訂位編號取得會員名稱、訂位時間、訂位人數，訂位成功/修改成功用
	@GetMapping("/getReservationInfo")
	public Result getReservationInfo(@RequestParam("reservationId") String reservationId) {
		System.out.println("訂位成功，訂位編號：" + reservationId);
		return reservationService.reservationInfo(Integer.valueOf(reservationId)).get(0);

	}
	
	// 從會員中心連結到修改頁，用預約編號去查餐廳名稱
	@GetMapping("/getRestaurantNameByReservation")
	public String getRestaurantNameByReservation(@RequestParam("reservationId") String reservationId) {
		System.out.println("修改頁的預約編號" + reservationId);
		return reservationService.getRestaurantNameByReservation(Integer.valueOf(reservationId));
}

	// 修改訂位資料
	@PutMapping("/reservationUpdate")
	public Result reservationUpdate(@RequestBody Map<String, Object> map) {
		Result result = new Result();
		
		String dateTime = (String) map.get("date_time");
		Integer reservationNumber = Integer.valueOf((String) map.get("ppl"));
		String text = (String) map.get("text");
		String reservationId = (String)map.get("reservationId");
//		Double acc_id = (Double) map.get("acc_id");
//		Double restaurantId = (Double) map.get("restaurantId");
		
	
		System.out.println("修改頁日期"+dateTime);
		System.out.println("修改頁訂單編號"+reservationId);
		
		ReservationVO reservationVO = new ReservationVO();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime resStartTime = LocalDateTime.parse(dateTime, formatter);
		Timestamp timestamp = Timestamp.valueOf(resStartTime);
		reservationVO.setReservationDateStartTime(timestamp);
		
		reservationVO.setReservationNumber(reservationNumber);
		
		// 如果備註項目不是空值，就存進vo
		if (!text.isEmpty()) {
			reservationVO.setReservationNote(text);
		}else {
			reservationVO.setReservationNote("");
		}

		if (reservationService.reservationUpdate(reservationVO, Integer.valueOf(reservationId))) {
			result.setStatus("success");
		} else {
			result.setStatus("");
		}

		return result;
	}

	// 刪除訂位資料
	// reservationId應該要在這裡作為參數傳進update，先寫死
//		@DeleteMapping("/reservationDelete")
//		public Result reservationDelete() {
//			Result result = new Result();
//			if (reservationService.reservationDelete(2)) {
//				result.setStatus("success");
//			} else {
//				result.setStatus("");
//			}
	//
//			return result;
//		}

	// 用會員編號取得會員名稱，print在訂位取消的畫面上
	// 會員編號固定1號
//	@GetMapping("/getMemberName")
//	public String getMemberName() {
//
//		return reservationService.memberName(1);
//
//	}

}
