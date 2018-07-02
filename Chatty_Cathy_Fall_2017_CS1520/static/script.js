var timeoutID;
var timeout = 1000;
var msgTime = 0;
var selection = 1000;


function setup() {
    document.getElementById("theButton").addEventListener("click", addMsg, true);
    poller();
}

function handle(e){
    if(e.keyCode === 13){
        e.preventDefault();
        addMsg();
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
            } else {
                alert("There was a problem with the request.  you'll need to refresh the page!");
            }
        }
    }
    return handler;
}

function addMsg() {
    var newMsg = document.getElementById("newMsg").value
    var chat_id = document.getElementById("theDisplay").placeholder
    var data;
    data = "msg=" + newMsg;
    makeReq("POST", "/chatroom/" + chat_id + "/" + msgTime, 201, poller, data);
    document.getElementById("newMsg").value = "";
}

function poller() {
    var chat_id = document.getElementById("theDisplay").placeholder
    makeReq("GET", "/chatroom/" + chat_id + "/" + msgTime, 200, repopulate);
}

function repopulate(responseText) {
    console.log("repopulating!");
    var msgs = JSON.parse(responseText);
    var display = document.getElementById("theDisplay");
    var m;

    for (m in msgs) {
        display.innerHTML = display.innerHTML + "&#10" + msgs[m]["contents"] + "  -  " + msgs[m]["author"];
        msgTime = msgs[m]["time"];
        selection = display.textLength;
        display.setSelectionRange(selection, selection);
    }
    
    timeoutID = window.setTimeout(poller, timeout);
}

window.addEventListener("load", setup, true);
window.addEventListener("visibilitychange", function(){window.location.href = "/"}, true)
window.addEventListener("beforeunload", function(){window.location.href = "/"}, true)

