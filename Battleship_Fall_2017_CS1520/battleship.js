// battleship.js

var player1Name = "";
var player2Name = "";
var nameCount = 0;
var playTurn = 1;
var play1Points = [0, 0, 0, 0];
var play2Points = [0, 0, 0, 0];
var topNamesArr = ["", "", "", "", "", "", "", "", "", ""];
var topScoresArr = ["", "", "", "", "", "", "", "", "", ""];

function enterName() {
	var patt = /[A-Za-z]{1,10}/i;
	if (nameCount == 0) {
		player1Name = document.getElementById("userInText").value;
		
		if (patt.test(player1Name)) {
			loadLeaderboard();
			document.getElementById("userDisp").innerHTML = "Where do you want to place your ships? <br> Aircraft Carrier(5 spaces), Battleship(4 spaces), & Submarine(3 spaces)";
			document.getElementById("playSet").style.display = "block";
			document.getElementById("userIn").reset();
	    	document.getElementById("userIn").onsubmit = function(){event.preventDefault(); enterShips();};
	    	document.getElementById("syntaxDesc").innerHTML = "EX: A(A1-A5); B(B6-E6); S(H3-J3);";
			nameCount++;
		}
		else {
			alert("Invalid Input!");
			document.getElementById("userIn").reset();
		}
	}
	else {
		player2Name = document.getElementById("userInText").value;

		if (patt.test(player2Name)) {
			document.getElementById("userDisp").innerHTML = "Where do you want to place your ships? <br> Aircraft Carrier(5 spaces), Battleship(4 spaces), & Submarine(3 spaces)";
			document.getElementById("playSet").style.display = "block";
			document.getElementById("userIn").reset();
	    	document.getElementById("userIn").onsubmit = function(){event.preventDefault(); enterShips();};
	    	document.getElementById("syntaxDesc").innerHTML = "EX: A(A1-A5); B(B6-E6); S(H3-J3);";
			nameCount++;
		}

	}
}

function enterShips() {
	var patt1 = /([ABS][:(][A-J]\d[0]?-[A-J]\d[0]?[)]?[;]?\s?){3}/i;
	var input = document.getElementById("userInText").value;

	if (patt1.test(input)) {
		var battleship = "", aircraft = "", submarine = "";
		input = input.replace(/[-:()\s]/g, "");
		var ships = input.split(";");

		for (var i=0; i<3; i++) {
			
			if (ships[i][0] == "A" && aircraft == "") {
				aircraft = ships[i];
			}
			else if(ships[i][0] == "B" && battleship == "") {
				battleship = ships[i]; 
			}
			else if(ships[i][0] == "S" && submarine == "") {
				submarine = ships[i];
			}
		}
		
		if (aircraft == "" || battleship == "" || submarine == "") {
			alert("Invalid Input!");
			document.getElementById("userIn").reset();
			return;
		}
		else {
			testInput(aircraft, "A", 4);
			
			testInput(battleship, "B", 3);
			
			testInput(submarine, "S", 2);
			

			if(nameCount == 1) {
				document.getElementById("userDisp").innerHTML = "Enter Player 2's name:";
				document.getElementById("playSet").style.display = "none";
				document.getElementById("gameBoard1").style.display = "none";
				document.getElementById("gameBoard2").style.display = "none";
				document.getElementById("gameBoard3").style.display = "block";
				document.getElementById("gameBoard4").style.display = "block";
				document.getElementById("userIn").reset();
		    	document.getElementById("userIn").onsubmit = function(){event.preventDefault(); enterName();};
		    	document.getElementById("syntaxDesc").innerHTML = "";
		    	nameCount++;
			}
			else {
				document.getElementById("userDisp").innerHTML = player1Name + "'s turn click Ok to continue.";
				document.getElementById("playSet").style.display = "none";
				document.getElementById("userIn").style.display = "none";
				document.getElementById("theButton").style.display = "block";
				document.getElementById("userIn").reset();
		    	document.getElementById("syntaxDesc").innerHTML = "";
		    	var buttons = document.getElementsByClassName("playButton");
				for (var i = 0; i<buttons.length;i++) {
					buttons[i].style.display = "block";
				}
			}
		}
	}
	else {
		alert("Invalid Input!");
		document.getElementById("userIn").reset();
		return;
	}
}

function transition() {
	document.getElementById("userDisp").innerHTML = "Click where you want to Attack";
	document.getElementById("playSet").style.display = "block";
	document.getElementById("theButton").style.display = "none";


	if (playTurn == 1) {
		document.getElementById("gameBoard1").style.display = "block";
		document.getElementById("gameBoard2").style.display = "block";
		document.getElementById("gameBoard3").style.display = "none";
		document.getElementById("gameBoard4").style.display = "none";
		document.getElementById("syntaxDesc").innerHTML = player1Name + "'s score: " + play1Points[0] + "\t" + player2Name + "'s score: " + play2Points[0];
	}

	else if (playTurn == 2) {
		document.getElementById("gameBoard1").style.display = "none";
		document.getElementById("gameBoard2").style.display = "none";
		document.getElementById("gameBoard3").style.display = "block";
		document.getElementById("gameBoard4").style.display = "block";
		document.getElementById("syntaxDesc").innerHTML = player2Name + "'s score: " + play2Points[0] + "\t" + player1Name + "'s score: " + play1Points[0];
	}
}

function playGame(move) {

	if (playTurn == 1) {
		var check = 3+move;
		move = 2+move;
		if (document.getElementById(check).innerHTML != "") {
			if (document.getElementById(check).innerHTML == "A") {
				play1Points[1]++;
				if (play1Points[1] == 5) {
					alert("You sunk their Aircraft Carrier!");
				}
				else {
					alert("HIT!");
				}
			}
			else if(document.getElementById(check).innerHTML == "B") {
				play1Points[2]++;
				if(play1Points[2] == 4) {
					alert("You sunk their Battleship!");
				}
				else {
					alert("HIT!");
				}
			}
			else if(document.getElementById(check).innerHTML == "S") {
				play1Points[3]++;
				if(play1Points[3] == 3) {
					alert("You sunk their Submarine!");
				}
				else {
					alert("HIT!");
				}
			}
			document.getElementById(move).style.backgroundColor = "red";
			document.getElementById(move).innerHTML = document.getElementById(check).innerHTML;
			document.getElementById(check).style.backgroundColor = "red";
			
			play1Points[0]+=2;
			if (play1Points[0] == 24) {
				play1Points[0] -= play2Points[0];
				alert(player1Name + " Wins with " + play1Points[0] + " points!");
				saveLeaderboard(player1Name, play1Points[0]);
				location.reload();
				return;
			}
			else {
				document.getElementById("userDisp").innerHTML = player2Name + "'s turn click Ok to continue.";
				document.getElementById("playSet").style.display = "none";
				document.getElementById("theButton").style.display = "block";
				document.getElementById("syntaxDesc").innerHTML = "";
		    	playTurn++;
			}
		}
		else {
			alert("MISS!");
			document.getElementById(move).style.backgroundColor = "white";
			document.getElementById(move).innerHTML = document.getElementById(check).innerHTML;
			document.getElementById(check).style.backgroundColor = "white";
			document.getElementById("userDisp").innerHTML = player2Name + "'s turn click Ok to continue.";
			document.getElementById("playSet").style.display = "none";
			document.getElementById("theButton").style.display = "block";
			document.getElementById("syntaxDesc").innerHTML = "";
	    	playTurn++;
		}
	}
	else if (playTurn == 2) {
		var check = 1+move;
		move = 4+move;
		if (document.getElementById(check).innerHTML != "") {
			if (document.getElementById(check).innerHTML == "A") {
				play2Points[1]++;
				if (play2Points[1] == 5) {
					alert("You sunk their Aircraft Carrier!");
				}
				else {
					alert("HIT!");
				}
			}
			else if(document.getElementById(check).innerHTML == "B") {
				play2Points[2]++;
				if(play2Points[2] == 4) {
					alert("You sunk their Battleship!");
				}
				else {
					alert("HIT!");
				}
			}
			else if(document.getElementById(check).innerHTML == "S") {
				play2Points[3]++;
				if(play2Points[3] == 3) {
					alert("You sunk their Submarine!");
				}
				else {
					alert("HIT!");
				}
			}
			document.getElementById(move).style.backgroundColor = "red";
			document.getElementById(move).innerHTML = document.getElementById(check).innerHTML;
			document.getElementById(check).style.backgroundColor = "red";
			
			play2Points[0]+=2;
			if (play2Points[0] == 24) {
				play2Points[0] -= play1Points[0];
				alert(player2Name + " Wins with " + play2Points[0] + " points!");
				saveLeaderboard(player2Name, play2Points[0]);
				location.reload();
				return;
			}
			else {
				document.getElementById("userDisp").innerHTML = player1Name + "'s turn click Ok to continue.";
				document.getElementById("playSet").style.display = "none";
				document.getElementById("theButton").style.display = "block";
				document.getElementById("syntaxDesc").innerHTML = "";
		    	playTurn--;
			}
		}
		else {
			alert("MISS!");
			document.getElementById(move).style.backgroundColor = "white";
			document.getElementById(move).innerHTML = document.getElementById(check).innerHTML;
			document.getElementById(check).style.backgroundColor = "white";
			document.getElementById("userDisp").innerHTML = player1Name + "'s turn click Ok to continue.";
			document.getElementById("playSet").style.display = "none";
			document.getElementById("theButton").style.display = "block";
			document.getElementById("syntaxDesc").innerHTML = "";
	    	playTurn--;
		}
	}
}

function reset() {
	for (var i = 1; i <=4; i++) {
		for (var j = 1; j<=10; j++) {
			for (var k = "A".charCodeAt(0); k <= "J".charCodeAt(0); k++) {
				var putPiece = i + String.fromCharCode(k) + j;
				document.getElementById(putPiece).innerHTML = "";
			}
		}
	}
}

function testInput(ship, symbol, sLength) {
	if ((ship.charCodeAt(4)-ship.charCodeAt(2) == sLength &&
		ship.charCodeAt(3)-ship.charCodeAt(1) == 0) ||
		(ship.charCodeAt(4)-ship.charCodeAt(2) == 0 &&
		ship.charCodeAt(3)-ship.charCodeAt(1) == sLength)) {
		
		if (ship.charCodeAt(4)-ship.charCodeAt(2) == sLength) {
			var start = parseInt(ship.charAt(2));
			var end = parseInt(ship.charAt(4));
			for (var i = start; i<=end; i++) {
				var putPiece = nameCount+ship.charAt(1)+i;
				if (!document.getElementById(putPiece).innerHTML == ""){
					alert("Invalid Input!");
					reset();
					document.getElementById("userIn").reset();
					return;
				}
				document.getElementById(putPiece).innerHTML = symbol;
			}
		}
		else {
			var start = ship.charCodeAt(1);
			var end = ship.charCodeAt(3);
			for (var i = start; i<=end; i++) {
				var putPiece = nameCount+String.fromCharCode(i)+ship.charAt(2);
				if (!document.getElementById(putPiece).innerHTML == ""){
					alert("Invalid Input!");
					reset();
					document.getElementById("userIn").reset();
					return;
				}
				document.getElementById(putPiece).innerHTML = symbol;
			}
		}
	}
	else if (ship.length == 6) {
		var start = parseInt(ship.charAt(2));
		var end = 10;
		for (var i = start; i<=end; i++) {
			var putPiece = nameCount+ship.charAt(1)+i;
			if (!document.getElementById(putPiece).innerHTML == ""){
				alert("Invalid Input!");
				reset();
				document.getElementById("userIn").reset();
				return;
			}
			document.getElementById(putPiece).innerHTML = symbol;
		}
	}
	else if (ship.length == 7){
		var start = ship.charCodeAt(1);
		var end = ship.charCodeAt(4);
		for (var i = start; i<=end; i++) {
			var putPiece = nameCount+String.fromCharCode(i)+"10";
			if (!document.getElementById(putPiece).innerHTML == ""){
				alert("Invalid Input!");
				reset();
				document.getElementById("userIn").reset();
				return;
			}
			document.getElementById(putPiece).innerHTML = symbol;
		}
	}
	else { 
		alert("Invalid Input!"); 
		document.getElementById("userIn").reset();
		return;
	}
}

function loadLeaderboard() {
	var names = localStorage.getItem("topNames");
	var scores = localStorage.getItem("topScores");
	
	if (!names == "") {
		topNamesArr = names.split(",");
		topScoresArr = scores.split(",");
	}
	if (!topNamesArr[0] == "") {
		for(var i=0; i<topNamesArr.length; i++) {
			var spot = "n" + i;
			document.getElementById(spot).innerHTML = "#" + (i+1) + ": " + topNamesArr[i] + "\t" + topScoresArr[i];
		}
	}

}

function saveLeaderboard (player, score) {
	if(!topNamesArr[0] == ""){	
		for (var i=0; i<topScoresArr.length; i++) {
			if(score > topScoresArr[i]) {
				for (var j=topScoresArr.length-1; j>i; j--) {
					if (!topScoresArr == "") {
						topNamesArr[j] = topNamesArr[j-1];
						topScoresArr[j] = topScoresArr[j-1];
					}
				}
				topNamesArr[i] = player;
				topScoresArr[i] = score;
				break;
			}
			if (topScoresArr[i] == "") {
				topNamesArr[i] = player;
				topScoresArr[i] = score;
			}

		}
	}
	else {
		topNamesArr[0] = player;
		topScoresArr[0] = score;
	}
	localStorage.setItem("topNames", topNamesArr.toString());
	localStorage.setItem("topScores", topScoresArr.toString());
}



