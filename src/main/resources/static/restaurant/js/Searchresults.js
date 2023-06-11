 //載入搜尋結果
 var searchResult = sessionStorage.getItem("searchResult");
 const resultObj = JSON.parse(searchResult);
 var bbb = document.getElementById("aaa");

 for (let i = 0; i < resultObj.length; i++) {
   const obj = resultObj[i];
   const restaurantId=obj.restaurantId;
   const resName = obj.resName;
   const resAdd = obj.resAdd;
   const resAcc = obj.resAcc;
   const resStartTime = obj.resStartTime;
   const resEndTime = obj.resEndTime;
   const resSeatNumber = obj.resSeatNumber;
   const resTotalScore = obj.resTotalScore;
   const resTel = obj.resTel;
   const resPhoto=obj.resPhoto;

   // 圖片轉B64編碼字串
   const photoBase64 = arrayBufferToBase64(resPhoto);
   const imageSrc = `data:image/jpeg;base64,${photoBase64}`;
   var newDiv = document.createElement("div");
   newDiv.classList.add("card-body");
   newDiv.id = "aaa";
   newDiv.innerHTML = ` 
   <div id="fff">
   <img src="${imageSrc}" alt="我是餐廳圖片" />
   </div>
   <div class="card-p">
 	 <p class="card-c">${resName}<span style="float: right"
		   ><i value="${restaurantId}" class="far fa-bookmark" style="margin-right: 10px;"></i></span></p>
	 <p class="card-a">${resAdd}</p>
	 <p class="card-b">今日營業時間 : ${resStartTime}-${resEndTime}</p>
	 <p class="card-d">連絡電話 : ${resTel}</p>
	 <div class="ellipse" >
	 ${resTotalScore}
	 <i class="fa fa-star"></i>
	  <div>
		<p>席位數 : ${resSeatNumber}</p>
	  </div>
	  <div>
	   
		 <p id="resdatail" value="${resAcc}">前往餐廳>></p> 
	 </div>
	 
	 </div>
   </div>	
	`;
   bbb.appendChild(newDiv);
 }
// 将字节数组转换为 Base64 编码的字符串
function arrayBufferToBase64(buffer) {
	var binary = '';
	var bytes = new Uint8Array(buffer);
	var len = bytes.byteLength;
	for (var i = 0; i < len; i++) {
	  binary += String.fromCharCode(bytes[i]);
	}
	return window.btoa(binary);
  }

  
 // 前往餐廳詳細頁面功能
 var datail = document.querySelectorAll("#resdatail");
 datail.forEach(function (datail) {
   datail.addEventListener("click", function () {
	 let resAcc = this.getAttribute("value");
	 $.ajax({
	   url: "forwardsearchresultsdetail",
	   method: "get",
	   data: {
		 abcde: resAcc,
	   },
	   dataType: "json",
	   success: function (response) {
		 var resultJson = JSON.stringify(response);

		 sessionStorage.setItem("searchResult", resultJson);

		 window.location.href = "Searchresultsdetail.html";
	   },
	   error: function (error) {
		 console.log(error);
	   },
	 });
   });
 });

 // GOOGLE 地圖API 相關功能
 function initMap() {
   // 在網頁上創建一個地圖
   var map = new google.maps.Map(document.getElementById("map"), {
	 center: { lat: 25.033964, lng: 121.564472 }, // 預設中心點經緯度
	 zoom: 12, // 預設縮放級別
   });

   // 監聽所有具有 card-a 類別的元素點擊事件
   var cardElements = document.querySelectorAll(".card-a");
   cardElements.forEach(function (cardElement) {
	 cardElement.addEventListener("click", function (event) {
	   var address = cardElement.textContent;

	   if (address) {
		 // 建立新的地圖標記
		 var geocoder = new google.maps.Geocoder();
		 geocoder.geocode(
		   { address: address },
		   function (results, status) {
			 if (status === "OK") {
			   // 移動地圖至標記位置
			   map.setCenter(results[0].geometry.location);
			   var marker = new google.maps.Marker({
				 map: map,
				 position: results[0].geometry.location,
			   });
			 } else {
			   console.log("地理編碼失敗，原因：" + status);
			 }
		   }
		 );
	   }
	 });
   });
 }
 //搜尋餐廳標籤功能
 $(".tag").on("click", function (event) {   
   var tag = $(this).attr("value");
   var data = { tag: tag };
   $.ajax({
	 url: "searchrestaurantbytag",
	 method: "get",
	 data: data,
	 dataType: "json",
	 success: function (response) {
	   var resultJson = JSON.stringify(response);
	   sessionStorage.setItem("searchResult", resultJson);
	   window.location.href = "Searchresults.html";
	 },
	 error: function (error) {
	   console.log(error);
	 },
   });
 });

 //搜尋餐廳名稱功能
 $("#search_icon").click(function (event) {
   event.preventDefault();
   $("form").submit();
 });

 $("form").on("submit", function (event) {
   event.preventDefault();

   var searchValue = $("#search_input").val();
   var url =
	 "searchrestaurantbyname?search_input=" +
	 encodeURIComponent(searchValue);
   $.ajax({
	 url: url,
	 method: "get",
	 data: $(this).serialize(),
	 dataType: "json",
	 success: function (response) {
	   var resultJson = JSON.stringify(response);
	   sessionStorage.setItem("searchResult", resultJson);
	   window.location.href = "Searchresults.html";
	 },
	 error: function (error) {
	   console.log(error);
	 },
   });
 });

 //添加到我的最愛功能	
$(document).ready(function() {  
	$(document).on('click', '.card-c i.far.fa-bookmark', function() {
			var loginReq = sessionStorage.getItem("loginReq");	
		if (loginReq) {			
			var restaurantId = $(this).attr('value');			
			var accobj = JSON.parse(loginReq);
			var acc_id=accobj.acc_id	
			var clickedIcon = $(this); // 被點擊的<i>標籤  
			$.ajax({
			   url: "restaurantaddmylove",
			   method: "post",
			   data: {
				restaurantId: restaurantId,
				  accId: acc_id,
			   },
			   dataType: "json",
			   success: function(response) {
				clickedIcon.css("background-color", "#FFDEB9");
				
			   },
			   error: function(error) {
				  console.log(error);
			   },
			});
		 } 
		else {
		alert("無法添加到我的最愛,請登入會員")
		window.location.href = "../account/login.html";
		}
	});
});
  
 
  