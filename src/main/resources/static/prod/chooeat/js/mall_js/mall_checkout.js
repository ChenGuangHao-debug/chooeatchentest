const cartData = [];
let accId ;
let account = JSON.parse(sessionStorage.getItem("loginReq"));

//=================================================================
// 拿到會員icon
let accountIcon = $("a.accountIcon");
// console.log(accountIcon);
// 會員中心的判斷
if (account != null) {
  accountIcon.attr("href", "../account/usercenter.html");
} else {
  accountIcon.attr("href", "../account/login.html");
}
//============================================================== 
if (sessionStorage.getItem("loginReq") != null) {
	document.getElementById("sname").innerHTML = account.acc_name;
	accId = account.acc_id;
}
// ================================== 後端 ===================================
// 獲取購物車內容並顯示在畫面上
function displayCart() {
	var tbodyContainer = document.getElementById("tbody");
	var tfootContainer = document.getElementById("tfoot");
	tbodyContainer.innerHTML = "";
	tfootContainer.innerHTML = "";
	const url = "checkout?accId="+accId;
	fetch(url)
		.then(function(response) { return response.json(); })
		.then(data => {
			//		console.log(data);
			let total = 0;
			for (const key in data) {
				if (data.hasOwnProperty(key)) {
					total += data[key].price * data[key].qty;
				}
			}
			const totalPrice = total.toLocaleString();
			const productsMap = data;
			for (let [productId, value] of Object.entries(productsMap)) {
				const key = productId;
				const productName = value.productName;
				const productPrice = value.price.toLocaleString();
				const productqty = value.qty;
				const producttotalPrice = (value.price * productqty).toLocaleString();
				const uint8Array = new Uint8Array(value.prodPic);
				let blob = new Blob([uint8Array], { type: "image/*" });
				let imageUrl = URL.createObjectURL(blob);
				tbodyContainer.innerHTML += `
									<tr data-product-id="${key}" class="key">
										<td style="width: 120px;"><img src="${imageUrl}"
											class="pord_img" /></td>
										<td><h5>${productName}</h5>
											<span style="color: gray;">單人｜平日晚餐</span></td>
										<td class="price">NT $ ${productPrice}</td>
										<td class="qty">${productqty}</td>
										<td>NT ${producttotalPrice}</td>
									</tr>
									<tr>
										<td colspan="5" style="padding: 0;"><hr></td>
									</tr>
						`;
				tfootContainer.innerHTML = `
						<tfoot>
							<tr>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td id="totalPrice">NT $${totalPrice}</td>
							</tr>
						</tfoot>
								`;

			};
		})
		.catch(function(error) {
			console.log(error);
		});
}

document.addEventListener("DOMContentLoaded", function() {
	displayCart();
});

function sendCartDataToBackend() {
	const url = "order";

	var cartData = {};
	//	var memberId = document.getElementById("memberId").innerText;
	var originalAmount = document.getElementById("totalPrice").innerText;
	var checkoutAmount = document.getElementById("totalPrice").innerText;

	var rows = document.querySelectorAll("#tbody tr");
	console.log(rows);
	for (var i = 0; i < rows.length; i += 2) {
		var row = rows[i];
		var productId = row.getAttribute("data-product-id");
		var qty = row.querySelector(".qty").innerText;
		var uniprice = row.querySelector(".price").innerText;
		cartData[productId] = {
			qty: qty,
			uniprice: uniprice
		};
		console.log(cartData);
	}

	var requestData = {
		cartData: cartData,
		memberId: accId,
		originalAmount: originalAmount,
		checkoutAmount: checkoutAmount
	};


	fetch(url, {
		method: "POST",
		headers: {
			"Content-Type": "application/json"
		},
		body: JSON.stringify(requestData)
	})
		.then(response => {
			if (response.ok) {
				//				console.log("d");
				window.location.href = 'mall_pay_successfully.html';
			} else {

				console.log("哀");
			}
		})
		.catch(error => {
			console.log("哀哀哀：" + error);
		});
}

document.getElementById("pay_check_btn").addEventListener("click", function() {

	// 判斷信用卡卡號欄位
	const creditNumberInputs = document.getElementsByClassName("credit_number");
	let isCreditNumberValid = true;

	for (let i = 0; i < creditNumberInputs.length; i++) {
		const input = creditNumberInputs[i];
		if (input.value.length !== input.maxLength || !/^\d+$/.test(input.value)) {
			isCreditNumberValid = false;
			break;
		}
	}

	// 判斷信用卡到期日欄位
	const creditDateInput = document.getElementsByClassName("credit_date")[0];
	const creditDateValue = creditDateInput.value;
	const creditDateParts = creditDateValue.split("/");
	const isCreditDateValid = creditDateParts.length === 2 && /^\d{2}$/.test(creditDateParts[0]) && /^\d{2}$/.test(creditDateParts[1]);

	// 判斷安全碼欄位
	const creditSecurityInput = document.getElementsByClassName("credit_security")[0];
	const isCreditSecurityValid = /^\d{3}$/.test(creditSecurityInput.value);


	const creditNumberValue = document.querySelector(".credit_number").value;
	const creditSecurityValue = document.querySelector(".credit_security").value;
	// 判斷結果
	const creditCardRadio = document.querySelector('input[type="radio"]');
	if (creditCardRadio.checked) {
		if (isCreditNumberValid && isCreditDateValid && isCreditSecurityValid) {
			console.log("所有欄位都已輸入數字並且數量符合預期");
			sendCartDataToBackend();
		} else if (creditNumberValue.trim() === "" && creditDateValue.trim() === "" && creditSecurityValue.trim() === "") {
			alert("請輸入信用卡資訊");
			return;
		} else if (!isCreditNumberValid) {
			alert("信用卡卡號有誤，請重新輸入");
			return;
		} else if (!isCreditDateValid) {
			alert("信用卡到期日有誤，請重新輸入");
			return;
		} else if (!isCreditSecurityValid) {
			alert("信用卡安全碼有誤，請重新輸入");
			return;
		}
	} else {
		// 信用卡未被選中
		alert("請選擇信用卡付款方式（目前僅提供使用信用卡付款）");
	}
});

function jumpToNextInput(event) {
	const input = event.target;
	const maxLength = input.getAttribute('maxlength');
	const currentLength = input.value.length;

	if (currentLength >= maxLength) {
		const nextInput = input.nextElementSibling;
		if (nextInput && nextInput.tagName === 'INPUT') {
			nextInput.focus();
		}
	}
}
function insertSlash(input) {
	const value = input.value;
	let formattedValue = value.replace(/\D/g, ""); // 移除非數字字符

	if (formattedValue.length > 4) {
		formattedValue = formattedValue.substr(0, 4); // 限制最大長度為 4
	}

	if (formattedValue.length >= 2) {
		const firstTwoDigits = formattedValue.substr(0, 2);
		if (parseInt(firstTwoDigits) > 12) {
			formattedValue = "12" + formattedValue.substr(2); // 前兩個數字大於 12，將其設置為 12
		}
	}

	if (formattedValue.length >= 4) {
		const lastTwoDigits = formattedValue.substr(2, 2);
		if (parseInt(lastTwoDigits) < 23) {
			formattedValue = formattedValue.substr(0, 2) + "23"; // 後兩個數字小於 23，將其設置為 23
		}
	}

	const formattedInputValue = formattedValue.replace(/(\d{2})(\d{2})/, "$1/$2"); // 在第二個和第三個數字之間插入斜線

	input.value = formattedInputValue;
}

