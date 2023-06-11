var result = JSON.parse(sessionStorage.getItem("searchResult")); // 將字串轉換為物件
var resTypeNames = result.restype.map(function (item) {
  return item.resTypeName;
});
var restaurantId = result["myself"][0].restaurantId;
var resName = result["myself"][0].resName;
var resAdd = result["myself"][0].resAdd;
var resStartTime = result["myself"][0].resStartTime;
var resEndTime = result["myself"][0].resEndTime;
var resTotalScore = result["myself"][0].resTotalScore;
var resIntro = result["myself"][0].resIntro;


// 圖片轉B64編碼字串
function arrayBufferToBase64(buffer) {
	var binary = '';
	var bytes = new Uint8Array(buffer);
	var len = bytes.byteLength;
	for (var i = 0; i < len; i++) {
	  binary += String.fromCharCode(bytes[i]);
	}
	return window.btoa(binary);
}


//餐廳圖片
const photoBase64 = arrayBufferToBase64(result["myself"][0].resPhoto);
const imageSrc = `data:image/jpeg;base64,${photoBase64}`;
var ddd = document.getElementById("ddd");
var newDiv = document.createElement("div");
newDiv.innerHTML = ` 
<div class="col-sm-12">
<img
  src="${imageSrc}"
  style="width: 100%; height: 300px"
/>
</div>
</div>
`;
ddd.appendChild(newDiv);


// 餐廳詳細資料
var newDiv = document.createElement("div");
newDiv.innerHTML = `
  <div>
  <h1>
	${resName}<span style="float: right"><i  value="${restaurantId}" class="far fa-bookmark"></i></span>
  </h1>
  <p>餐廳評分 : ${resTotalScore}<i class="fa fa-star" style="color: yellow"></i></p> 
  <p>餐廳種類 : ${resTypeNames}</p>
  <p>餐廳地址 : ${resAdd}</p>
  <p>營業時間 : ${resStartTime}-${resEndTime}</p>
  <div>    
	<h1>餐廳簡介</h1>
	<p>
	${resIntro}
	</p>
  </div>
  </div>
  `;
var parentElement = document.getElementById("parentElement");
parentElement.appendChild(newDiv);

//廣告
for (let i = 0; i < result["comment"].length; i++) {

  const photoBase64acc = arrayBufferToBase64(result["account"][i].accPic);
  const imageSrcacc = `data:image/jpeg;base64,${photoBase64acc}`;
 
  result["account"][i].accName;
  result["comment"][i].restaurantCommentDatetime;
  result["comment"][i].restaurantCommentReplyDatetime;
  result["comment"][i].restaurantCommentReplyText;
  result["comment"][i].restaurantCommentScore;
  result["comment"][i].restaurantCommentText;

  var ccc = document.createElement("div");
  ccc.innerHTML = `   
   <div class="row">
  <div class="col-sm-2">
  <img decoding="async" src="${imageSrcacc}" height="50px"/>
  </div>
  <div class="col-sm-10">
  <p>用戶 : ${result["account"][i].accName}</p>
  <p>評價 : ${result["comment"][i].restaurantCommentScore}<i class="fa fa-star"style="color: yellow"></i></p>
  <p>日期 : ${result["comment"][i].restaurantCommentDatetime}</p>
  <p>留言 : ${result["comment"][i].restaurantCommentText}</p>
  <p style="text-align: right">餐廳回復日期 : ${result["comment"][i].restaurantCommentReplyText ? result["comment"][i].restaurantCommentReplyDatetime : ''}</p>
  <p style="text-align: right">餐廳留言 : ${result["comment"][i].restaurantCommentReplyText}</p>
  <hr />
  </div>	
  </div>
  </div>
   `;

  var aaaaa = document.getElementById("abcde");
  aaaaa.appendChild(ccc);
}

for (let i = 0; i < result.prod.length; i++) {
  var prodName = result.prod[i].prodName;
  var prodPrice = result.prod[i].prodPrice;

  var ccc = document.createElement("li");
  ccc.innerHTML = ` 
   ${prodName} NT$${prodPrice}</li>
	
	`;

  var tt = document.getElementById("zzz");
  tt.appendChild(ccc);
}


// GOOGLE 地圖 API 相關功能
function initMap() {
  // 在網頁上創建一個地圖
  var map = new google.maps.Map(document.getElementById("map"), {
	center: { lat: 25.033964, lng: 121.564472 }, // 預設中心點經緯度
	zoom: 12, // 預設縮放級別
  });

  var address = resAdd;

  if (address) {
	// 建立新的地圖標記
	var geocoder = new google.maps.Geocoder();
	geocoder.geocode({ address: address }, function (results, status) {
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
	});
  }
}

$(document).ready(function() {  
	$(document).on('click', 'i.far.fa-bookmark', function() {
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
  