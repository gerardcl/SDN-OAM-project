var msgSwitch = "SWITCH";
var msgTerminal = "TERMINAL";
var msgLink = "LINK";
var msgFlow = "FLOW";
var msgPort = "PORT";
var msgTunel = "TUNEL";
var newMessage = {};
var webSocket = null;
var alarmCounter=0;
var alarmsHistory = [];


function getalarmCounter(){
	return alarmCounter;
}

//SWITCH
function checkEventSwitch(){
	var minLength = Math.min(newMessage.event.length, msgSwitch.length);
	var counter = 0;
	for(var i = 0; i < minLength; i++)
	{
		if (newMessage.event[i] == msgSwitch[i])
		{
			counter++;    
		}
	}
	if(counter == msgSwitch.length)return msgSwitch;
	else return null;
}
function filterSwitchEvent(){
	switch(newMessage.event){
		case "SWITCHES_REQUEST": return true;
		default: break;
	}
	return false;
}
//FLOWS
function checkEventFlow(){
	var minLength = Math.min(newMessage.event.length, msgFlow.length);
	var counter = 0;
	for(var i = 0; i < minLength; i++)
	{
		if (newMessage.event[i] == msgFlow[i])
		{
			counter++;    
		}
	}
	if(counter == msgFlow.length) return msgFlow;
	else return null;
}
function filterFlowEvent(){
	switch(newMessage.event){
	case "SWITCHES_REQUEST": return true;
	default: break;
	}
	return false;
}
//TERMINALS
function checkEventTerminal(){
	var minLength = Math.min(newMessage.event.length, msgTerminal.length);
	var counter = 0;
	for(var i = 0; i < minLength; i++)
	{
		if (newMessage.event[i] == msgTerminal[i])
		{
			counter++;    
		}
	}
	if(counter == msgTerminal.length) return msgTerminal;
	else return null;
}
function filterTerminalEvent(){
	switch(newMessage.event){
	case "TERMINALS_REQUEST": return true;
	default: break;
	}
	return false;
}
//LINKS
function checkEventLink(){
	var minLength = Math.min(newMessage.event.length, msgLink.length);
	var counter = 0;
	for(var i = 0; i < minLength; i++)
	{
		if (newMessage.event[i] == msgLink[i])
		{
			counter++;    
		}
	}
	if(counter == msgLink.length) return msgLink;
	else return null;
}
function filterLinkEvent(){
	switch(newMessage.event){
	case "LINKS_REQUEST": return true;
	default: break;
	}
	return false;
}

//SET ALARM STATS
function getLIsucceeded(alarm){
	return '<li><a href="#/adminAlarms"><span class="label label-success"> '+alarm+' </span></a></li>';
}
function getLIinfo(alarm){
	return '<li><a href="#/adminAlarms"><span class="label label-info"> '+alarm+' </span></a></li>';
}
function getLIwarning(alarm){
	return '<li><a href="#/adminAlarms"><span class="label label-warning"> '+alarm+' </span></a></li>';
}
function getLIdanger(alarm){
	return '<li><a href="#/adminAlarms"><span class="label label-danger"> '+alarm+' </span></a></li>';
}

function setAlarmView(){

	//TODO check view properly
	
	var dropDoWn ="";
	if(getalarmCounter()>0) dropDoWn = '<a href="#" class="dropdown-toggle" data-toggle="dropdown" style="color:red"><i class="fa fa-bell"></i> Alarms <span class="badge" > '+getalarmCounter()+' </span> <b class="caret"></b></a><ul class="dropdown-menu" id="alarmsList"> </ul>';
	else dropDoWn = '<a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="fa fa-bell"></i> Alarms <b class="caret"></b></a><ul class="dropdown-menu" id="alarmsList"> </ul>';

    $("#alarmsDropDown").html(dropDoWn);
    //console.log(alarmCounting)
	var acount = getalarmCounter();
	console.log(acount);
	//$("#alarmsDropDown").html(getLIsucceeded());
	var l=0;
	if(getalarmCounter()>0) $("#alarmsList").append('<li><a href="#/adminAlarms">View All</a></li><li class="divider"></li>');
	else $("#alarmsList").append('<li><a href="#/adminAlarms">View All</a></li>');
	for(l=alarmsHistory.length-1;l>=alarmsHistory.length-acount;l--){
		$("#alarmsList").append(getLIsucceeded(alarmsHistory[l].event));	
	}
}
function resetAlarmView(){
	alarmCounter=0;
}


function checkAlarm(){
	var event;
	event = checkEventSwitch();
	if(event==null) event = checkEventTerminal();
	if(event==null) event = checkEventFlow();
	if(event==null) event = checkEventLink();
	
	if(event != null){
		switch(event){
		case msgSwitch:
			console.log("NEW SWITCH ALARM");
			if(filterSwitchEvent()) break;
			alarmCounter++;
			alarmsHistory.push(newMessage);
			setAlarmView();
			break;
		case msgTerminal:
			console.log("NEW TERMINAL ALARM");
			if(filterTerminalEvent()) break;
			alarmCounter++;
			alarmsHistory.push(newMessage);
			setAlarmView();
			break;
		case msgLink:
			console.log("NEW LINK ALARM");
			if(filterLinkEvent()) break;
			alarmCounter++;
			alarmsHistory.push(newMessage);
			setAlarmView();
			break;
		case msgFlow:
			console.log("NEW FLOW ALARM");
			if(filterFlowEvent()) break;
			alarmCounter++;
			alarmsHistory.push(newMessage);
			setAlarmView();
			break;
		default: console.log('NO MESSAGE RECOGNIZED.....');
		break;
		}
		//SHOW ALL ALARM HISTORY ARRAY
		console.log("SHOWING ALL ALARM HISTORY!!!!!!!!!!!!!!!!!!!")
		var k;
		for(k=0; k<alarmsHistory.length;k++){
			console.log(alarmsHistory[k]);
		}
		//
		//SET MESSAGE FORMAT
		//APPEND MESSAGE
		//SET +1 ALARMS MESSAGE

	}
}

function onOpen(eventObject) {
	console.log("WS open");
}



//PARSE ALL EVENTS!!!!!!!!!!!!!!!!!!!!!!!!!!
function onMessage(eventObject) {
	console.log("WS message");
	newMessage = JSON.parse(eventObject.data);
	console.log(newMessage);
	checkAlarm();
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