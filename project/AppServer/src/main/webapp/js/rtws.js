var webSocket = null;

function onOpen(eventObject) {
	console.log("WS open");
}

function onMessage(eventObject) {
	console.log("WS message");
	console.log(eventObject);

	//var message = "<br/> " + eventObject.data;
	//document.getElementById('log').innerHTML = message + document.getElementById('log').innerHTML; //Add the message recieved from the server 
	console.log(eventObject.data);
}

function onClose(eventObject) {
	console.log("WS close");
	console.log(eventObject);
}

function onError(eventObject) {
	console.log("WS error");
	console.log(eventObject);
}

webSocket = new WebSocket("ws://" + window.location.host
		+ "/AppServer/realtimewebsocket");
webSocket.onopen = onOpen;
webSocket.onmessage = onMessage;
webSocket.onclose = onClose;
webSocket.onerror = onError;

function sendCustomMessage(message) {
	if (webSocket != null) {
		webSocket.send(message);
	}
}

function sendMessage() {
	if (webSocket != null) {
		var message = document.getElementById('message').value;
		webSocket.send(message);
	}
}