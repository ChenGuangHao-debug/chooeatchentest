package chooeat.reservation.controller;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import chooeat.reservation.dao.ReservationDao;
import chooeat.reservation.dao.RestaurantRepository;
import chooeat.reservation.model.EmailDetails;
import chooeat.reservation.model.EmailInfo;
import chooeat.reservation.model.HourlySeat;
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
	@Autowired
	ReservationDao reservationDao;
	@Autowired
	RestaurantRepository restaurantRepository;

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
			//設定回傳物件
		    Result result = new Result();
		    //設定要放進redis的map
		    HashMap<String, String> data = new HashMap<>();
		    //接前端參數
		    String dateTime = (String) map.get("date_time");
		    Integer reservationNumber = Integer.valueOf((String) map.get("ppl"));
		    String text = (String) map.get("text");
		    Double acc_id = (Double) map.get("acc_id");
		    Double restaurantId = (Double) map.get("restaurantId");
		    System.out.println("acc_id + " + acc_id);
		    System.out.println("restaurantId + " + restaurantId);
		    System.out.println(dateTime);
		    //建立jedis連線
		    Jedis jedis = new Jedis();
 //		    jedis.del("reservationLock");
		    String lockKey = "", lockValue = "";
		    
		    try {
		    	//設定暫存的剩餘座位數變數（已經存進資料庫的剩餘座位數 - 還沒結帳的預約人數）
		    	Integer remainSeat = restaurantRepository.findById(new Integer(restaurantId.intValue())).get().getResMaxNum();
		    	
		    	//redis上鎖，拿到鎖才可以進入下面判斷
		    	
		        lockKey = "reservationLock";
		        lockValue = UUID.randomUUID().toString();
		        String key = "checkout";
		        boolean isLocked = jedis.setnx(lockKey, lockValue) == 1;
		        if (!isLocked) {
		            result.setStatus("error");
		            System.out.println("這時間有人在結帳");
		            return result;
		        }
		        //選擇2號資料庫
		        jedis.select(2);
		        //if判斷這邊，好像可以不用~"~?
		        //因為redis結帳完就砍了，鎖定別人也進不來，但沒關係先留著
		        boolean exists = jedis.exists(index);
		        if (exists) {
//		        	int checkingPeople = Integer.valueOf(jedis.get(key));
//		            if (checkingPeople <= 0) {
//		                result.setStatus("error");
//		                return result;
//		            } else {
//		                int rms = Integer.valueOf(jedis.get(key)) - reservationNumber;
//		                if (rms >= 0) {
//		                    data.put(key, remainSeat.toString());
//		                    data.put("accId", new Integer(acc_id.intValue()).toString());
//		                    data.put("restaurantId", new Integer(restaurantId.intValue()).toString());
//		                    data.put("reservationNumber", (String) map.get("ppl"));
//		                    data.put("reservationDateStartTime", (String) map.get("date_time"));
//		                    if (!text.isEmpty()) {
//		                        data.put("reservationNote", text);
//		                    } else {
//		                        data.put("reservationNote", "");
//		                    }
//
//		                    jedis.hmset(index, data);
//
//		                    result.setStatus("success");
//		                }
//		            }
		        } else {
		            try {
		            	//先把前端傳進來的預約日期字串轉換日期時間格式
		                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		                java.util.Date utildate = dateFormat.parse(dateTime);
		                //抓出選擇的日期
		                Date sqlDate = new Date(utildate.getTime());
		                List<HourlySeat> list = reservationDao.selectall(new Integer(restaurantId.intValue()), sqlDate);
		                //用前端資料抓出選擇的小時
		                int hour = utildate.getHours();
		                System.out.println("當前小時是" + hour);

		            

		                if (list.size() == 0) {
		                	//用餐廳id去抓座位數量，去扣預約人數，剩下的存進redis
		                    remainSeat = restaurantRepository.findById(new Integer(restaurantId.intValue())).get().getResMaxNum() - reservationNumber;
		                    data.put(key, remainSeat.toString());
		                } else {
		                    for (HourlySeat hourlySeat : list) {
		                        if (hourlySeat.getHour() == hour) {
		                            remainSeat = hourlySeat.getRemainSeat();
		                            System.out.println("當前時段有" + hourlySeat.getRemainSeat() + "個座位");
		                            break;
		                        }
		                    }
		                    System.out.println("剩餘座位111" + remainSeat);
		                    System.out.println("預約數量111" + reservationNumber);
		                    
		                    remainSeat = remainSeat - reservationNumber;
		                    System.out.println("更新後的數量是" + remainSeat);
		                    data.put(key, remainSeat.toString());
		                }

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
		                System.out.println("存儲至 Redis 中的 key: " + index);
		            } catch (Exception e) {
		                System.out.println("error: " + e.getMessage());
		                result.setStatus("error");
		            }
		        }
		    } finally {
		    	jedis.setnx(lockKey, lockValue);
		        jedis.del("reservationLock");
		        System.out.println("解鎖啦");
		        jedis.close();
		    }

		    return result;
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
		
		System.out.println(reservationVO.toString());
		
		

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
		jedis.close();
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
