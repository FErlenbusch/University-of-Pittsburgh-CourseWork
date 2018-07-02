var curCat = 0, holder = 0;
var white = /[\S]+/g;
var today = new Date();
var catArr = [{id:0, name:"All", amount:0}];
var purArr = [];

function setup() {
	document.getElementById("year").value = today.getFullYear();
	document.getElementById("month").value = today.getMonth()+1;
	document.getElementById("datePur").value = today.getFullYear() + "-" + (today.getMonth()+1) + "-" + today.getDate();
	document.getElementById("modDate").value = today.getFullYear() + "-" + (today.getMonth()+1) + "-" + today.getDate();

	document.getElementById("catBut").addEventListener("click", addCat, true);
	document.getElementById("purBut").addEventListener("click", addPur, true);
	document.getElementById("delCatBut").addEventListener("click", delCat, true);
	document.getElementById("delPurBut").addEventListener("click", delPur, true);
	document.getElementById("modCatBut").addEventListener("click", modCat, true);
	document.getElementById("modPurBut").addEventListener("click", modPur, true);
	document.getElementById("year").addEventListener("change", fillPurs, true);
	document.getElementById("month").addEventListener("change", fillPurs, true);
	document.getElementById("modCat").addEventListener("change", modCatChange, true);
	document.getElementById("modPur").addEventListener("change", modPurChange, true);

	catPoller();
	purPoller();  
}

function handle(e, i){
	var func = [addCat, modCat, delCat, addPur, modPur, delPur];
	if(e.keyCode === 13) {
		e.preventDefault();
		func[i]();
	}
}

function makeReq(method, target, retCode, action, data) {
	var httpRequest = new XMLHttpRequest();

	if (!httpRequest) {
		alert('Error: Cannot create an XMLHTTP instance');
		return false;
	}

	httpRequest.onreadystatechange = makeHandler(httpRequest, retCode, action);
	httpRequest.open(method, target);
	
	if (data){
		httpRequest.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		httpRequest.send(data);
	}
	else {
		httpRequest.send();
	}
}

function makeHandler(httpRequest, retCode, action) {
	function handler() {
		if (httpRequest.readyState === XMLHttpRequest.DONE) {
			if (httpRequest.status === retCode) {
				console.log("recieved response text:  " + httpRequest.responseText);
				action(httpRequest.responseText);
			} 
			else {
				alert("There was a problem with the request.  you'll need to refresh the page!");
			}
		}
	}
	return handler;
}

function catPoller() {
	makeReq("GET", "/cats", 200, repopCat);
}

function purPoller() {
	makeReq("GET", "/purchases", 200, repopPur);
}

function repopCat(responseText) {
	console.log("Repopulating Categories!");
	var cats;

	if (responseText != "") {
		cats = JSON.parse(responseText);
		
		if (cats.length) {
			for (var c = 0; c < cats.length; c++) {
				catArr.push({id:cats[c]["cat_id"], name:cats[c]["name"], amount:cats[c]["amount"]});
			}
		}
		else {
			catArr.push({id:cats["cat_id"], name:cats["name"], amount:cats["amount"]});
		}
	}

	catArr.forEach(fillCats);
	fillPurs();
}

function repopPur(responseText) {
	console.log("Repopulating Purchases!");

	if (responseText != "") {
		var purs = JSON.parse(responseText);

		if (purs.length) {
			for (var p = 0; p < purs.length; p++) {
				purArr.push({
					id:purs[p]["pur_id"], 
					desc:purs[p]["description"], 
					amount:purs[p]["amount"], 
					cat:purs[p]["category"], 
					date:purs[p]["date"]
				});
			}
		}
		else {
			purArr.push({
				id:purs["pur_id"], 
				desc:purs["description"], 
				amount:purs["amount"], 
				cat:purs["category"], 
				date:purs["date"]
			});
		}
	}

	fillPurs();
}

function fillCats(cat) {
	var catDisp = document.getElementById("catDisp");
	var catPur = document.getElementById("catPur");
	var modCatPur = document.getElementById("modCatPur");
	var delCat = document.getElementById("delCat");
	var modCat = document.getElementById("modCat");
	var listI = document.createElement("li");
	var button = document.createElement("input");

	if (cat.id == 0){
		catDisp.innerHTML = "";
		catPur.innerHTML = "";
		modCatPur.innerHTML = "";

		delCat.innerHTML = "<option value=''>&nbsp;</option>";
		modCat.innerHTML = "<option value=''>&nbsp;</option>";
	}
	else {
		catPur.innerHTML = catPur.innerHTML + "&#10" + "<option value='" + cat.id + "'>" + cat.name + "</option>";
		modCatPur.innerHTML = modCatPur.innerHTML + "&#10" + "<option value='" + cat.id + "'>" + cat.name + "</option>";

		if (cat.id != 1) {
			delCat.innerHTML = delCat.innerHTML + "&#10" + "<option value='" + cat.id + "'>" + cat.name + "</option>";
			modCat.innerHTML = modCat.innerHTML + "&#10" + "<option value='" + cat.id + "'>" + cat.name + "</option>";
		}
	}

	button.setAttribute("type", "button");
	button.setAttribute("value", cat.name);
	button.setAttribute("onclick", "changeCat(" + cat.id + ", '" + cat.name + "')");

	listI.appendChild(button);
	catDisp.appendChild(listI);
}

function fillPurs() {
	var frame = document.getElementById("purDisp");
	var delPur = document.getElementById("delPur");
	var modPur = document.getElementById("modPur");

	frame.innerHTML = "";
	delPur.innerHTML = "<option value=''> </option>";
	modPur.innerHTML = "<option value=''> </option>";

	var purs = purArr.filter(filterByDate);

	if (curCat == 0) { 
		for (var c = catArr.length-1; c > 0; c--) { 
			var unord = document.createElement("ul");
			var title = document.createElement("h3");
			var info = document.createElement("span");
			var left = document.createElement("span");
			var leftOver = document.createElement("span");
			var over = document.createElement("span");
			var total = 0;

			curCat = catArr[c].id;
			var pursFor = purs.filter(filterByCategory);
			pursFor = pursFor.sort(sortByDate);

			if (pursFor.length > 0) {
				total = pursFor.reduce(getSum, 0);
			}

			title.innerHTML = catArr[c].name;

			if (catArr[c].id == 1){
				info.innerHTML = "<b>Total Spent: " + parseFloat(total).toFixed(2) + "</b>";
				left.innerHTML = "";
				leftOver.innerHTML = "";
				over.innerHTML = "";
			}
			else {
				over.innerHTML = (parseFloat(catArr[c].amount) - parseFloat(total)).toFixed(2);
				left.innerHTML = (parseFloat(total) / parseFloat(catArr[c].amount) * 100).toFixed(2);

				if (parseFloat(over.innerHTML) < 0) {
					over.setAttribute("style", "color:red; font-weight:bold;");
					left.setAttribute("style", "color:red; font-weight:bold;");
				}
				else {
					over.setAttribute("style", "font-weight:bold;");
					left.setAttribute("style", "font-weight:bold;");
				}
				left.innerHTML = left.innerHTML + "%";
				info.innerHTML = "<b>Budget: " + parseFloat(catArr[c].amount).toFixed(2) + " \u00A0\u00A0 - \u00A0\u00A0 Total Spent: " + parseFloat(total).toFixed(2) + " \u00A0\u00A0 - \u00A0\u00A0 Used: <b/>";
				leftOver.innerHTML = "<b> \u00A0\u00A0 - \u00A0\u00A0 Left: </b>"
			}

			for (var p = 0; p < pursFor.length; p++) {
				var listI = document.createElement("li");
				var text = document.createTextNode(pursFor[p].date + ": \u00A0\u00A0 " + pursFor[p].desc + "\u00A0\u00A0\u00A0 - \u00A0\u00A0\u00A0" + parseFloat(pursFor[p].amount).toFixed(2));

				listI.appendChild(text);
				unord.appendChild(listI);

				delPur.innerHTML = delPur.innerHTML + "&#10" + "<option value='" + pursFor[p].id + "'>" + pursFor[p].date + ": \u00A0" + pursFor[p].desc + "\u00A0 - \u00A0" + parseFloat(pursFor[p].amount).toFixed(2) + "</option>";
				modPur.innerHTML = modPur.innerHTML + "&#10" + "<option value='" + pursFor[p].id + "'>" + pursFor[p].date + ": \u00A0" + pursFor[p].desc + "\u00A0 - \u00A0" + parseFloat(pursFor[p].amount).toFixed(2) + "</option>";
			}

			if (!pursFor.length) {
				unord.innerHTML = "<li>No Purchases in this Category.</li>";
			}

			frame.appendChild(title);
			frame.appendChild(info);
			frame.appendChild(left);
			frame.appendChild(leftOver);
			frame.appendChild(over);
			frame.appendChild(unord);
			frame.innerHTML = frame.innerHTML + "<br />";
		}

		curCat = 0;
	}
	else {
		var unord = document.createElement("ul");
		var title = document.createElement("h3");
		var info = document.createElement("span");
		var left = document.createElement("span");
		var leftOver = document.createElement("span");
		var over = document.createElement("span");
		var total = 0;

		cat = catArr.find(findObj);
		purs = purs.filter(filterByCategory);
		purs = purs.sort(sortByDate);

		if (purs.length > 0) {
			total = purs.reduce(getSum, 0);
		}

		title.innerHTML = cat.name;

		if (cat.id == 1){
			info.innerHTML = "<b>Total Spent: " + parseFloat(total).toFixed(2) + "</b>";
			left.innerHTML = "";
			leftOver.innerHTML = "";
			over.innerHTML = "";
		}
		else {
			over.innerHTML = (parseFloat(cat.amount) - parseFloat(total)).toFixed(2);
			left.innerHTML = (parseFloat(total) / parseFloat(cat.amount) * 100).toFixed(2);

			if (parseFloat(over.innerHTML) < 0) {
				over.setAttribute("style", "color:red; font-weight:bold;");
				left.setAttribute("style", "color:red; font-weight:bold;");
			}
			else {
				over.setAttribute("style", "font-weight:bold;");
				left.setAttribute("style", "font-weight:bold;");
			}

			info.innerHTML = "<b>Budget: " + parseFloat(cat.amount).toFixed(2) + " \u00A0\u00A0 - \u00A0\u00A0 Total Spent: " + parseFloat(total).toFixed(2) + " \u00A0\u00A0 - \u00A0\u00A0 Used: <b/>";
			leftOver.innerHTML = "<b>% \u00A0\u00A0 - \u00A0\u00A0 Left: </b>"
		}

		for (var p = 0; p < purs.length; p++) {
			var listI = document.createElement("li");
			var text = document.createTextNode(purs[p].date + ": \u00A0\u00A0 " + purs[p].desc + "\u00A0\u00A0\u00A0 - \u00A0\u00A0\u00A0" + parseFloat(purs[p].amount).toFixed(2));

			listI.appendChild(text);
			unord.appendChild(listI);

			delPur.innerHTML = delPur.innerHTML + "&#10" + "<option value='" + purs[p].id + "'>" + purs[p].date + ": \u00A0" + purs[p].desc + "\u00A0 - \u00A0" + parseFloat(purs[p].amount).toFixed(2) + "</option>";
			modPur.innerHTML = modPur.innerHTML + "&#10" + "<option value='" + purs[p].id + "'>" + purs[p].date + ": \u00A0" + purs[p].desc + "\u00A0 - \u00A0" + parseFloat(purs[p].amount).toFixed(2)+ "</option>";
		}

		if (!purs.length) {
			unord.innerHTML = "<li>No Purchases in this Category.</li>";
		}

		frame.appendChild(title);
		frame.appendChild(info);
		frame.appendChild(left);
		frame.appendChild(leftOver);
		frame.appendChild(over);
		frame.appendChild(unord);
	}
}

function changeCat(cat, name) {
	document.getElementById("catTitle").innerHTML = name;

	curCat = cat;

	fillPurs();
}

function addCat() {
	var newCat = document.getElementById("newCat").value;
	var newBud = document.getElementById("newBud").value;

	newBud = notZero(newBud);

	var data = "name=" + newCat + "&amount=" + newBud;

	if (white.test(newCat)) {
		makeReq("POST", "/cats", 201, repopCat, data);
	}
	else {
		alert("Must Enter a Name for your new Category!");
	}

	document.getElementById("newCat").value = "Name";
	document.getElementById("newBud").value = newBud;
}

function addPur() {
	var descPur = document.getElementById("descPur").value;
	var amountPur = document.getElementById("amountPur").value;
	var catPur = document.getElementById("catPur").value;
	var datePur = document.getElementById("datePur").value;

	amountPur = notZero(amountPur);
   
	var data = "description=" + descPur + "&amount=" + amountPur + "&category=" + catPur + "&date=" + datePur;

	if (white.test(descPur)) {
		makeReq("POST", "/purchases", 201, repopPur, data);
	}
	else {
		alert("Must add a Descrition for your new Purchase!");
	}

	document.getElementById("descPur").value = "Description";
	document.getElementById("amountPur").value = amountPur;
	document.getElementById("catPur").value = catPur;
	datePur = document.getElementById("datePur").value = datePur;
}

function modCat() {
	var cat = document.getElementById("modCat").value;
	var amount = document.getElementById("modBud").value;

	amount = notZero(amount);

	holder = cat;
	var i = catArr.findIndex(findObj);

	var data = "name=" + cat + "&amount=" + amount;

	if (cat != "") {
		catArr[i].amount = amount;
		makeReq("PUT", "/cats", 201, fillPurs, data);
	}
	else {
		alert("Must Select a Category to Modify!");
	}

	document.getElementById("modCat").value = cat;
	document.getElementById("modBud").value = amount;
}

function modPur() {
	var pur = document.getElementById("modPur").value;
	var cat = document.getElementById("modCatPur").value;
	var amount = document.getElementById("modAmnt").value;
	var date = document.getElementById("modDate").value;

	amount = notZero(amount);

	holder = pur;
	var i = purArr.findIndex(findObj);

	var data = "description=" + pur  + "&amount=" + amount + "&category=" + cat + "&date=" + date;

	if (pur != "") {
		purArr[i].cat = cat;
		purArr[i].amount = amount;
		purArr[i].date = date;
		makeReq("PUT", "/purchases", 201, fillPurs, data);
	}
	else {
		alert("Must Select a Purchase to Modify!");
	}

	document.getElementById("modPur").value = pur;
	document.getElementById("modCatPur").value = cat;
	document.getElementById("modAmnt").value = amount;
	document.getElementById("modDate").value = date;
}

function delCat() {
	var cat = document.getElementById("delCat").value;

	var data = "name=" + cat + "&amount=" + 0;

	if (cat != "") {
		holder = cat;
		catArr = catArr.filter(filterOut);

		purArr.forEach(toUncat);

		makeReq("DELETE", "/cats", 204, repopCat, data);
	}
	else {
		alert("Must Select a Category to Delete!");
	}
}

function delPur() {
	var pur = document.getElementById("delPur").value;

	var data = "description=" + pur  + "&amount=" + 0 + "&category=" + 0 + "&date=" + 0;

	if (pur != "") {
		holder = pur;
		purArr = purArr.filter(filterOut);

		makeReq("DELETE", "/purchases", 204, fillPurs, data);
	}
	else {
		alert("Must Select a Purchase to Delete!");
	}
}

function modCatChange() {
	cat = document.getElementById("modCat").value;
	box = document.getElementById("modBud");

	holder = cat;
	var i = catArr.findIndex(findObj);

	box.value = catArr[i].amount;
}

function modPurChange() {
	pur = document.getElementById("modPur").value;
	amountBox = document.getElementById("modAmnt");
	catBox = document.getElementById("modCatPur");
	dateBox = document.getElementById("modDate");

	holder = pur;
	var i = purArr.findIndex(findObj);

	amountBox.value = purArr[i].amount;
	catBox.value = purArr[i].cat;
	dateBox.value = purArr[i].date;
}

function filterByDate(pur) {
	if (pur.date) {
		var year = document.getElementById("year").value;
		var month = document.getElementById("month").value;
		var date = pur.date.split("-");

		return date[0] == year && date[1] == month;
	}
}

function filterByCategory(pur) {
	return pur.cat == curCat;
}

function toUncat(pur) {
	if (pur.cat == holder) {
		pur.cat = 1;
	}
}

function getSum(total, pur) {
	total = parseFloat(total) + parseFloat(pur.amount);
	return total;
}

function findObj(obj) {
	return obj.id == holder;
}

function filterOut(obj) {
	return obj.id != holder;
}

function sortByDate(a, b) {
	return new Date(a.date) - new Date(b.date);
}

function notZero(amt) {
	if (amt == 0) {
		amt = 0.01
	}
	return amt;
}

window.addEventListener("load", setup, true);
