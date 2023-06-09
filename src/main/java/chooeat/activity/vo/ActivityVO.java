package chooeat.activity.vo;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "activity")
public class ActivityVO {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "activity_id", updatable = false)
	private Integer activityId;
	@Column(name = "activity_name")
	private String activityName;
	@Column(name = "activity_number")
	private Integer activityNumber;
	@Column(name = "min_number")
	private Integer minNumber;
	@Column(name = "max_number")
	private Integer maxNumber;
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@Column(name = "activity_date")
	private Date activityDate;
	@Column(name = "acc_id")
	private Integer accId;
	@Column(name = "restaurant_id")
	private Integer restaurantId;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "regesteration_starting_time")
	private Timestamp regesterationStartingTime;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	@Column(name = "regesteration_ending_time")
	private Timestamp regesterationEndingTime;
	@JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
	@Column(name = "activity_starting_time")
	private Time activityStartingTime;
	@JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
	@Column(name = "activity_ending_time")
	private Time activityEndingTime;
	@Column(name = "activity_text")
	private String activityText;
	@Column(name = "activity_status")
	private Integer activityStatus;
	@Lob
	@Column(name = "activity_photo", columnDefinition = "longblob")
	private byte[] activityPhoto;

	// 用來接收前端的base64圖片字串
	@Transient
	private String activityPhotoBase64;

	// 使用Spring Data JPA進行外來鍵對應
	@OneToOne
	@JoinColumn(name = "restaurant_id", insertable = false, updatable = false)
	private ActivityRestaurantVO activityrestaurantVO;

	public ActivityVO() {
		super();
	}

	public ActivityVO(Integer activityId, String activityName, Integer activityNumber, Integer minNumber,
			Integer maxNumber, Date activityDate, Integer accId, Integer restaurantId,
			Timestamp regesterationStartingTime, Timestamp regesterationEndingTime, Time activityStartingTime,
			Time activityEndingTime, String activityText, Integer activityStatus, byte[] activityPhoto,
			String activityPhotoBase64, ActivityRestaurantVO activityrestaurantVO) {
		super();
		this.activityId = activityId;
		this.activityName = activityName;
		this.activityNumber = activityNumber;
		this.minNumber = minNumber;
		this.maxNumber = maxNumber;
		this.activityDate = activityDate;
		this.accId = accId;
		this.restaurantId = restaurantId;
		this.regesterationStartingTime = regesterationStartingTime;
		this.regesterationEndingTime = regesterationEndingTime;
		this.activityStartingTime = activityStartingTime;
		this.activityEndingTime = activityEndingTime;
		this.activityText = activityText;
		this.activityStatus = activityStatus;
		this.activityPhoto = activityPhoto;
		this.activityPhotoBase64 = activityPhotoBase64;
		this.activityrestaurantVO = activityrestaurantVO;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Integer getActivityNumber() {
		return activityNumber;
	}

	public void setActivityNumber(Integer activityNumber) {
		this.activityNumber = activityNumber;
	}

	public Integer getMinNumber() {
		return minNumber;
	}

	public void setMinNumber(Integer minNumber) {
		this.minNumber = minNumber;
	}

	public Integer getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(Integer maxNumber) {
		this.maxNumber = maxNumber;
	}

	public Date getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(Date activityDate) {
		this.activityDate = activityDate;
	}

	public Integer getAccId() {
		return accId;
	}

	public void setAccId(Integer accId) {
		this.accId = accId;
	}

	public Integer getRestaurantId() {
		return restaurantId;
	}

	public void setRestaurantId(Integer restaurantId) {
		this.restaurantId = restaurantId;
	}

	public Timestamp getRegesterationStartingTime() {
		return regesterationStartingTime;
	}

	public void setRegesterationStartingTime(Timestamp regesterationStartingTime) {
		this.regesterationStartingTime = regesterationStartingTime;
	}

	public Timestamp getRegesterationEndingTime() {
		return regesterationEndingTime;
	}

	public void setRegesterationEndingTime(Timestamp regesterationEndingTime) {
		this.regesterationEndingTime = regesterationEndingTime;
	}

	public Time getActivityStartingTime() {
		return activityStartingTime;
	}

	public void setActivityStartingTime(Time activityStartingTime) {
		this.activityStartingTime = activityStartingTime;
	}

	public Time getActivityEndingTime() {
		return activityEndingTime;
	}

	public void setActivityEndingTime(Time activityEndingTime) {
		this.activityEndingTime = activityEndingTime;
	}

	public String getActivityText() {
		return activityText;
	}

	public void setActivityText(String activityText) {
		this.activityText = activityText;
	}

	public Integer getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
	}

	public byte[] getActivityPhoto() {
		return activityPhoto;
	}

	public void setActivityPhoto(byte[] activityPhoto) {
		this.activityPhoto = activityPhoto;
	}

	public String getActivityPhotoBase64() {
		return activityPhotoBase64;
	}

	public void setActivityPhotoBase64(String activityPhotoBase64) {
		this.activityPhotoBase64 = activityPhotoBase64;
	}


	public ActivityRestaurantVO getActivityrestaurantVO() {
		return activityrestaurantVO;
	}

	public void setActivityrestaurantVO(ActivityRestaurantVO activityrestaurantVO) {
		this.activityrestaurantVO = activityrestaurantVO;
	}

	@Override
	public String toString() {
		return "ActivityVO [activityId=" + activityId + ", activityName=" + activityName + ", activityNumber="
				+ activityNumber + ", minNumber=" + minNumber + ", maxNumber=" + maxNumber + ", activityDate="
				+ activityDate + ", accId=" + accId + ", restaurantId=" + restaurantId + ", regesterationStartingTime="
				+ regesterationStartingTime + ", regesterationEndingTime=" + regesterationEndingTime
				+ ", activityStartingTime=" + activityStartingTime + ", activityEndingTime=" + activityEndingTime
				+ ", activityText=" + activityText + ", activityStatus=" + activityStatus + ", activityPhoto="
				+ Arrays.toString(activityPhoto) + ", restaurantVO=" + activityrestaurantVO + "]";
	}
}
