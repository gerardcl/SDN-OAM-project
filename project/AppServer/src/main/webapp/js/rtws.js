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

function setTabAlarmView(){
	var q=0;
	var k=0;
	if(alarmsHistory.length>0){
		//$("#alertsTable").append('<table class="table table-bordered table-hover table-striped tablesorter">');
		//$("#alertsTable").append('<thead><tr><th>Alert </th><th>Date </th><th>Time </th><th>Details </th></tr></thead><tbody>');
		for(q=alarmsHistory.length-1;q>=0;q--){
			var date = alarmsHistory[q].timestamp;
			$('#alertsTable tr:last').after('<tr><td id="event"><span class="label label-success">'+ alarmsHistory[q].event+'</span></td><td id="timestamp">'+getStrDate(date)+'</td><td>'+getStrHour(date)+'</td><td><center><button type="button" class="btn btn-primary" data-toggle="collapse" data-target=".alertDetail'+q+'">Details</button></center></td></tr>');
			$('#alertsTable tr:last').after('<tr class="collapse out alertDetail'+q+'"><th>Inventory ID</th><th>Property Id</th><th>Legacy</th><th>New value</th><th>Message</th></tr>');
			if(alarmsHistory[q].updates.length>0){
				for(k=0;k<alarmsHistory[q].updates.length;k++){
					$('#alertsTable tr:last').after('<tr class="collapse out alertDetail'+q+'"><td id="inventoryId">'+alarmsHistory[q].updates[k].inventoryId+'</td><td id="propertyId">'+alarmsHistory[q].updates[k].propertyId+'</td><td id="legacyValue">'+alarmsHistory[q].updates[k].legacyValue+'</td><td id="newValue">'+alarmsHistory[q].updates[k].newValue+'</td><td id="message">'+alarmsHistory[q].updates[k].message+'</td></tr>');
				}
			}else{
				$('#alertsTable tr:last').after('<tr class="collapse out alertDetail'+q+'"><td id="inventoryId">no id</td><td id="propertyId"></td><td id="legacyValue">no legacy value</td><td id="newValue">no new value</td><td id="message">no updates here</td></tr>');
			}
		}
		//$("#alertsTable").append('</tbody></table>');
	}
	else{
		$("#alertsTable").html('<center> <h1> <p class="text-muted"> No alarms yet... keep on the right track </p> <h1> </center>');
	}
}

function getStrDate(tStamp){
	var timeUTC =(new Date (tStamp));
	var year;
	var month;
	var day;

	year= timeUTC.getFullYear();
	month=timeUTC.getMonth()+1;
	day=timeUTC.getDate();

    
	if ((month >= 0)&&(month <= 9)){ 
	    month="0"+month; 
    }
    
	if ((day >= 0)&&(day <= 9)){ 
	    day="0"+day; 
    }
     
	return day+"/"+month+"/"+year;
}

function getStrHour(tStamp){
	var timeUTC =(new Date (tStamp));
	var hours;
	var minutes;
	var seconds;

	hours =timeUTC.getHours();
	minutes =timeUTC.getMinutes();
	seconds=timeUTC.getSeconds();

		
	if ((hours >= 0)&&(hours <= 9)){ 
		hours="0"+hours; 
    }
	if ((minutes >= 0)&&(minutes <= 9)){ 
		minutes="0"+minutes; 
	}
	
     if ((seconds >= 0)&&(seconds <= 9)){ 
	    seconds="0"+seconds; 
     }
     
	return hours+":"+minutes+":"+seconds;
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