var opts = {
		lines: 13, // The number of lines to draw
		length: 20, // The length of each line
		width: 10, // The line thickness
		radius: 30, // The radius of the inner circle
		corners: 1, // Corner roundness (0..1)
		rotate: 0, // The rotation offset
		direction: 1, // 1: clockwise, -1: counterclockwise
		color: '#000', // #rgb or #rrggbb or array of colors
		speed: 1, // Rounds per second
		trail: 60, // Afterglow percentage
		shadow: false, // Whether to render a shadow
		hwaccel: false, // Whether to use hardware acceleration
		className: 'spinner', // The CSS class to assign to the spinner
		zIndex: 2e9, // The z-index (defaults to 2000000000)
		top: 'auto', // Top position relative to parent in px
		left: 'auto' // Left position relative to parent in px
};
//var target = $("#preview");
//var targetspin = target.parent().spin();

$(document).ready(function(){
	//$('#preview').hide();
	//$('#preview').after(new Spinner(opts).spin().el);
	//$('#preview').removeClass('spinner');
	//spin.spin(false);
	//$('#preview').after(new Spinner(opts).spin().el);
	//var spinner = new Spinner(opts).spin(target);

	//	var spinner = new Spinner(opts).spin();
	//	$(".body").appendChild(spinner.el);
	//initStatusOverview();

});

function initStatusOverview(){
		
	$("#statistics").hide();
	createTopologyGraph();
	initializeControllerStats();
	
	
	//HIDE LOADING MODAL
	
	
	
}

//TOPOLOGY GRAPH
var switchesURL = "/AppServer/webapi/topology/switches";
var terminalsURL = "/AppServer/webapi/topology/terminals";
var linksURL = "/AppServer/webapi/topology/links";
var dataSwitches;
var dataTerminals;
var dataLinks;
var nodes = [];
var links = [];
var refreshIntervalTWM;
var txResponseLinks;
var rxResponseLinks;
var txResponseTerminals;
var rxResponseTerminals;

function stopTopoWeatherMapRefresh(){
	clearInterval(refreshIntervalTWM);
	dataSwitches ={};
	dataTerminals ={};
	dataLinks ={};
	nodes = [];
	links = [];
}

function getTopoWeatherMap(){
	
	
	
	//SHOW LOADING MODAL
	
	
	
	$.ajaxSetup({
		async : false
	}); //execute synchronously

	$.ajax({
		type: "GET",
		url: switchesURL,
		//contentType: 'application/json',
		//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//		headers: {
//		Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//		},
		success : function(result) {
			dataSwitches = result;
		}
	});
	$.ajax({
		type: "GET",
		url: terminalsURL,
		//contentType: 'application/json',
		//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//		headers: {
//		Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//		},
		success : function(result) {
			dataTerminals = result;
		}
	});
	$.ajax({
		type: "GET",
		url: linksURL,
		//contentType: 'application/json',
		//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//		headers: {
//		Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//		},
		success : function(result) {
			dataLinks = result;
		}
	});


//	Tractament de dades obtingudes
	$.ajaxSetup({
		async : true
	}); //execute asynchronously
	var terminals =[];
	for (var i=0; i < dataTerminals.terminals.length; i++){
		if ((dataTerminals.terminals[i].portAPId != "00:00:00:00:00:00:00:00:0")&&(dataTerminals.terminals[i].ipv4!="0.0.0.0")){
			terminals.push(dataTerminals.terminals[i]);
		}
	}
	nodes = dataSwitches.switches;
	//var rawLinks = dataLinks.links;
	var src,trg;

	var new_obj = {"colorTx":0, "colorRx":0};
	dataLinks.links.push( new_obj );
	dataTerminals.terminals.push( new_obj );

	for (var i=0; i<dataLinks.links.length; i++){
		if(dataLinks.links[i].dstPortId==undefined){}else if(dataLinks.links[i].dstPortId != "00:00:00:00:00:00:00:00:0"){
			var reqUriRx ="/AppServer/webapi/statistics/port/"+dataLinks.links[i].dstPortId+"/receiveBytes/AVERAGE/minute";
			$.ajaxSetup({
				async : false
			}); //execute synchronously

			$.ajax({
				type: "GET",
				url: reqUriRx,
				//contentType: 'application/json',
				//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//				headers: {
//				Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//				},
				success : function(result) {
					rxResponseLinks = result;
					//console.log("LINKS RX RESULT:");
					//console.log(result);
				},
				error: function(xhr, msg) {
					//console.log(xhr.responseText);
					var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
					//console.log(rplcd);
					rxResponseLinks = JSON.parse(rplcd);
				}
			});
			$.ajaxSetup({
				async : true
			}); //execute synchronously	
		}else console.log("undefined!!!");

		if(dataLinks.links[i].srcPortId==undefined){}else if(dataLinks.links[i].srcPortId != "00:00:00:00:00:00:00:00:0"){
			var reqUriTx ="/AppServer/webapi/statistics/port/"+dataLinks.links[i].srcPortId+"/transmitBytes/AVERAGE/minute";
			$.ajaxSetup({
				async : false
			}); //execute synchronously
			$.ajax({
				type: "GET",
				url: reqUriTx,
				//contentType: 'application/json',
				//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//				headers: {
//				Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//				},
				success : function(result) {
					txResponseLinks = result;
					//console.log("LINKS TX RESULT:");
					//console.log(result);
				},
				error: function(xhr, msg) {
					//console.log(xhr.responseText);
					var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
					//console.log(rplcd);
					txResponseLinks = JSON.parse(rplcd);
				}
			});
			$.ajaxSetup({
				async : true
			}); //execute synchronously
		}else console.log("undefined!!!");

		var sumRx=0;
		var sumTx=0;

		if(dataLinks.links[i].dstPortId==undefined){}else if(dataLinks.links[i].dstPortId != "00:00:00:00:00:00:00:00:0"){
			if(dataLinks.links[i].srcPortId==undefined){}else if(dataLinks.links[i].srcPortId != "00:00:00:00:00:00:00:00:0"){
				for (var j=0; j<rxResponseLinks.valueAxxis.length;j++){
					if(rxResponseLinks.valueAxxis[j]==null){}else if(txResponseLinks.valueAxxis[j]!=null){
						sumRx+=rxResponseLinks.valueAxxis[j];
						sumTx+=txResponseLinks.valueAxxis[j];
						//console.log("sumRX = "+sumRx+"  -- sumTX = "+sumTx);
					}
				}
			}
		}
		sumRx = ((sumRx/60)*8/100000).toFixed(2);
		sumTx = ((sumTx/60)*8/100000).toFixed(2);
		//alert("REC: "+sumRx+" TRANSM: "+sumTx);
		var colorRx = getGreenToRed(sumRx);
		var colorTx = getGreenToRed(sumTx);
		dataLinks.links[i].colorRx=colorRx;
		dataLinks.links[i].colorTx=colorTx;
	}



	for (var i=0;i<dataLinks.links.length;i++){
		for(var j=0;j<nodes.length;j++){
			for(var k=0;k<nodes[j].ports.length;k++){
				if(dataLinks.links[i].srcPortId == nodes[j].ports[k].portId){
					src = j;
				}
				if(dataLinks.links[i].dstPortId == nodes[j].ports[k].portId){
					trg = j;
				}
			}
			if (!((src==null)||(trg==null))){
				links.push({"source":src,"target":trg,"value":8,"type":"suit","color":dataLinks.links[i].colorTx});
				links.push({"source":trg,"target":src,"value":8,"type":"suit","color":dataLinks.links[i].colorRx});
				src= null;
				trg=null;
			}
		}	
	}


	for (var i=0; i<dataTerminals.terminals.length; i++){
		if(dataTerminals.terminals[i].portAPId==undefined){}else if(dataTerminals.terminals[i].portAPId != "00:00:00:00:00:00:00:00:0"){
			var reqUriRx ="/AppServer/webapi/statistics/port/"+dataTerminals.terminals[i].portAPId+"/receiveBytes/AVERAGE/minute";
			$.ajaxSetup({
				async : false
			}); //execute synchronously
			$.ajax({
				type: "GET",
				url: reqUriRx,
				//contentType: 'application/json',
				//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//				headers: {
//				Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//				},
				success : function(result) {
					rxResponseTerminals = result;
				},
				error: function(xhr, msg) {
					var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
					rxResponseTerminals = JSON.parse(rplcd);
				}
			});
			$.ajaxSetup({
				async : true
			}); //execute synchronously
		}
		if(dataTerminals.terminals[i].portAPId==undefined){}else if(dataTerminals.terminals[i].portAPId != "00:00:00:00:00:00:00:00:0"){
			var reqUriTx ="/AppServer/webapi/statistics/port/"+dataTerminals.terminals[i].portAPId+"/transmitBytes/AVERAGE/minute";
			$.ajaxSetup({
				async : false
			}); //execute synchronously
			$.ajax({
				type: "GET",
				url: reqUriTx,
				//contentType: 'application/json',
				//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//				headers: {
//				Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//				},
				success : function(result) {
					txResponseTerminals = result;
				},
				error: function(xhr, msg) {
					var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
					txResponseTerminals = JSON.parse(rplcd);
				}
			});	//Transmitted bytes
			$.ajaxSetup({
				async : true
			}); //execute synchronously
		}
		var sumRx=0;
		var sumTx=0;
		if(dataTerminals.terminals[i].portAPId==undefined){}else if(dataTerminals.terminals[i].portAPId != "00:00:00:00:00:00:00:00:0"){
			for (var j=0; j<rxResponseTerminals.valueAxxis.length;j++){
				if(rxResponseLinks.valueAxxis[j]==null){}else if(txResponseLinks.valueAxxis[j]!=null){
					sumRx+=rxResponseTerminals.valueAxxis[j];
					sumTx+=txResponseTerminals.valueAxxis[j];
					//console.log("sumRX = "+sumRx+"  -- sumTX = "+sumTx);
				}
			}
		}
		sumRx = ((sumRx/60)*8/100000).toFixed(2);
		sumTx = ((sumTx/60)*8/100000).toFixed(2);
		//alert("REC: "+sumRx+" TRANSM: "+sumTx);
		var colorRx = getGreenToRed(sumRx);
		var colorTx = getGreenToRed(sumTx);
		dataTerminals.terminals[i].colorRx=colorRx;
		dataTerminals.terminals[i].colorTx=colorTx;
	}

	for (var i=0; i<terminals.length;i++){
		nodes.push(terminals[i]);
	} 
	//alert(nodes.length);
	for(var i=0;i<nodes.length;i++){
		if(nodes[i].portAPId !=null){
			//alert("STEP1: "+nodes[i].portAPId);
			for(var j=0;j<nodes.length;j++){
				if(nodes[j].portAPId == null){
					for(var k=0;k<nodes[j].ports.length;k++){
						//alert("STEP2: "+nodes[j].ports[k].portId);
						if (nodes[i].portAPId==nodes[j].ports[k].portId){
							src = i;
							trg =j;	
							//alert("Link Pushed: "+nodes[j].swId+ " TO "+nodes[i].terminalId);
							links.push({"source":src,"target":trg,"value":8,"type":"suit","color":nodes[i].colorRx});
							links.push({"source":trg,"target":src,"value":8,"type":"suit","color":nodes[i].colorTx});
						}
					}
				}
			}
		}	
	}

	function getGreenToRed(percent){
		//console.log(percent);
		g = percent<50 ? 255 : Math.floor(255-(percent*2-100)*255/100);
		r = percent>50 ? 255 : Math.floor((percent*2)*255/100);
		return 'rgb('+r+','+g+',0)';
	}


}//END GET TOPO WEATHER MAP


function createTopologyGraph(){
	getTopoWeatherMap();

	//Represenació grafic 
	var width = 400,
	height = 300;

	var force = d3.layout.force()
	.nodes(nodes)
	//.gravity(.05)
	.links(links)
	.size([width, height])
	.linkDistance(110)
	.charge(-2200)
	.on("tick", tick)
	.start();

	var svg = d3.select("#topo").append("svg")
	.attr("width", "100%")
	.attr("height", height);

	svg.append("defs").selectAll("marker")
	.data(["suit", "licensing", "resolved"])
	.enter().append("marker")
	.attr("id", function(d) {return d; })
	.attr("viewBox", "0 -5 10 10")
	.attr("refX", 29)
	.attr("refY", -3)
	.attr("markerWidth", 8)
	.attr("markerHeight", 8)
	.attr("orient", "auto")
	.append("path")
	.attr("d", "M0,-5L10,0L0,5");

	var path = svg.append("g").selectAll("path")
	.data(links)
	.enter().append("path")
	.attr("class", function(d) { return "link " + d.type; })
	.attr("marker-end", function(d) { return "url(#" + d.type + ")"; })
	.style("stroke",  function(d) {return d.color;}); 

	var node = 	svg.selectAll(".node")
	.data(nodes)
	.enter().append("g")
	.attr("class", "node")
	.on("tick",  function (d){return path.attr("d", linkArc);})
	.call(force.drag);



	node.append("title").text(function(d) {
		if(d.portAPId ==null){
			var ports= d.ports.length;
			for(var i=0;i<d.ports.length;i++){						
				ports+= "\n   Port "+i+":\n     MAC: "+d.ports[i].mac+"\n     Name: "+d.ports[i].name+"\n     PortId: "+d.ports[i].portId;
			}
			return ("switch ID: "+d.swId+"\n Hardware: "+d.hardware+"\n Software: "+d.software+"\n Manufacturer: "+d.manufacturer+
					"\n Total ports: "+ports);
		}else{
			return ("Terminal Id: "+d.terminalId+"\n IPv4: "+d.ipv4+"\n MAC: "+d.mac+"\n SDN Access port: "+d.portAPId);
		}
	});

	node.append("image")
	.attr("xlink:href", function (d)	{	if (d.portAPId ==null){
		return "/AppServer/img/switch.svg";
	}else{
		return "/AppServer/img/terminal.svg";
	}
	})
	.attr("x", function (d)	{	if (d.portAPId ==null){
		return -50;
	}else{
		return -20;
	}
	})
	.attr("y", function (d)	{	if (d.portAPId ==null){
		return -50;
	}else{
		return -20;
	}
	})
	.attr("width", function (d)	{	if (d.portAPId ==null){
		return 100;
	}else{
		return 50;
	}
	})
	.attr("height", function (d)	{	if (d.portAPId ==null){
		return 100;
	}else{
		return 50;
	}
	});

	function tick() {
		path.attr("d", linkArc);
		node.attr("transform", transform);
	}

	function linkArc(d) {
		var dx = d.target.x - d.source.x,
		dy = d.target.y - d.source.y,
		dr = Math.sqrt(dx * dx + dy * dy);
		return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
	}

	function transform(d) {
		return "translate(" + d.x + "," + d.y + ")";
	}

	refreshIntervalTWM = setInterval(function() {
		getTopoWeatherMap();
	},60000);

	node.on("click", function(d) {
		StopSwitchStats();
		
		$('#advisementClick').hide();
		
		console.log("node " + d.swId + " was clicked");
		var switchInfo = "<h4> Switch info</h4>";
		switchInfo += "<b>ID:</b>  "+d.swId+"<p>";
		switchInfo += "<b>Manufacturer:</b>  "+d.manufacturer;
		var portList ="<h4>Port list by id</h4>";
		for (var i = d.ports.length-1 ; i >= 0 ; i--) {
			portList += "<li class='list-group-item' id='port' onclick='showPortStats();' style='width: 100%; height: 42px; margin: 0 auto; font-size:100%;' >"+ d.ports[i].portId+"</li>";
		}
		var packetCount = "loading...";
		var byteCount = "loading...";
		var flowCount = "loading...";
		$("#switchInfo").html(switchInfo);
		$("#packetCount").html(packetCount);
		$("#byteCount").html(byteCount);
		$("#flowCount").html(flowCount);
		$("#portList").html(portList);
		loadDefaultStatValues();
		$("#statistics").show();
		InitSwitchStats(d.swId);
	});
}





//STATISTIC'S GENERATION
var selectedPort = "";
var selectedParam = "";
var selectedValueType = "";
var selectedTimeInterval = "";
var valueSuffix = " Bytes/s";
var refreshIntervalIdPort;
var refreshIntervalIdSwitch;
var refreshIntervalController;
var refreshLoadingSSTATS;

function StopSwitchStats(){
	clearInterval(refreshIntervalIdSwitch);
	clearInterval(refreshLoadingSSTATS);
}


//TODO mix getData methods!!!

function InitSwitchStats(idSwitch){
	//http://147.83.113.109:8080/AppServer/webapi/statistics/switch/00:01:d4:ca:6d:c4:44:1e/packetCount/MAX/second
	//http://147.83.113.109:8080/AppServer/webapi/statistics/switch/00:01:d4:ca:6d:c4:44:1e/byteCount/MAX/second
	//http://147.83.113.109:8080/AppServer/webapi/statistics/switch/00:01:d4:ca:6d:c4:44:1e/flowCount/MAX/second
	var sitchTimeIntervalUpdate = 5000; //1 second per update
	refreshIntervalIdSwitch = setInterval(function() {
		clearInterval(refreshLoadingSSTATS);
		$("#packetCount").html(getpacketCountData());
		$("#byteCount").html(getbyteCountData());
		$("#flowCount").html(getflowCountData());
		refreshLoadingSSTATS = setInterval(function() {
			$("#packetCount").html("loading...");
			$("#byteCount").html("loading...");
			$("#flowCount").html("loading...");
		},sitchTimeIntervalUpdate-1000);
	},sitchTimeIntervalUpdate);


	function getpacketCountData(){
		var datastats = {};
		var datastatsuri = "/AppServer/webapi/statistics/switch/"+idSwitch+"/packetCount/MAX/second";
		$.ajaxSetup({
			async : false
		}); //execute synchronously

		console.log("GETTING STATS");
		$.ajax({
			type: "GET",
			url: datastatsuri,
			//contentType: 'json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
			//headers: {
			//	Accept : "application/vmd.dxat.appserver.stats+json"
			//},
			success : function(result) {
				datastats = result;
			},
			error: function(xhr, msg) {
				var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
				datastats = JSON.parse(rplcd);
			}
		});
		//		Tractament de dades obtingudes
		$.ajaxSetup({
			async : true
		}); //execute asynchronously
		return datastats.valueAxxis[0];
	}
	function getbyteCountData(){
		var datastats = {};
		var datastatsuri = "/AppServer/webapi/statistics/switch/"+idSwitch+"/byteCount/MAX/second";
		$.ajaxSetup({
			async : false
		}); //execute synchronously

		//console.log("GETTING STATS");
		$.ajax({
			type: "GET",
			url: datastatsuri,
			//contentType: 'json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
			//headers: {
			//	Accept : "application/vmd.dxat.appserver.stats+json"
			//},
			success : function(result) {
				datastats = result;
			},
			error: function(xhr, msg) {
				var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
				datastats = JSON.parse(rplcd);
			}
		});
		//		Tractament de dades obtingudes
		$.ajaxSetup({
			async : true
		}); //execute asynchronously
		return datastats.valueAxxis[0];
	}
	function getflowCountData(){
		var datastats = {};
		var datastatsuri = "/AppServer/webapi/statistics/switch/"+idSwitch+"/flowCount/MAX/second";
		$.ajaxSetup({
			async : false
		}); //execute synchronously

		//console.log("GETTING STATS");
		$.ajax({
			type: "GET",
			url: datastatsuri,
			//contentType: 'json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
			//headers: {
			//	Accept : "application/vmd.dxat.appserver.stats+json"
			//},
			success : function(result) {
				datastats = result;
			},
			error: function(xhr, msg) {
				var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
				datastats = JSON.parse(rplcd);
			}
		});
		//		Tractament de dades obtingudes
		$.ajaxSetup({
			async : true
		}); //execute asynchronously
		return datastats.valueAxxis[0];
	}

}

function byHourGraph(){
	//Petició REST de les dades inicials
	var data = getData();
	var timeAxis=[];
	timeAxis=getXAxis(data.timeAxxis);

	$('#statisticsGraph').highcharts({
		chart: {
			type: 'areaspline',
			animation: false,
		},
		title: {
			text: data.parameter+' by '+selectedValueType+' on port: ' +data.idObject
		},
		legend: {
			x: 0,
			y: 300,
		},
		xAxis: {
			tickPixelInterval: 1920,
			categories: timeAxis,
		},
		yAxis: {
			title: {
				text: data.parameter
			}
		},
		tooltip: {
			shared: true,
			valueSuffix: valueSuffix,
			crosshairs: [true, true]
		},
		credits: {
			enabled: false
		},
		plotOptions: {
			areaspline: {
				fillOpacity: 0.5
			},

		},
		series: [{
			name: data.parameter,
			data: data.valueAxxis,
			color: '#00AA00'
		}]

	});
	//j es podra esborrar
	var j=0;
	refreshIntervalIdPort = setInterval(function() {
		refresh();
	},62000);

	function refresh() {
		//PART A: Aquesta part es podra esborrar en integrarse completament
		j++;
		if((j % 2)==0){
			var data2= getData();

		}else{
			//FINAL PART A
			var data2= getData();

		}
		var chart = $('#statisticsGraph').highcharts();
		timeAxis=getXAxis(data2.timeAxxis);
		chart.xAxis[0].setCategories(timeAxis);
		chart.series[0].setData(data2.valueAxxis);
	}

	/*funció de petició (no passa)REST. Per ara es static*/
	function getData(){
		var datastats = {};
		var datastatsuri = "/AppServer/webapi/statistics/port/"+selectedPort+"/"+selectedParam+"/"+selectedValueType+"/hour";
		$.ajaxSetup({
			async : false
		}); //execute synchronously

		//console.log("GETTING STATS");
		$.ajax({
			type: "GET",
			url: datastatsuri,
			//contentType: 'json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
			//headers: {
			//	Accept : "application/vmd.dxat.appserver.stats+json"
			//},
			success : function(result) {
				datastats = result;
			},
			error: function(xhr, msg) {
				var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
				datastats = JSON.parse(rplcd);
			}
		});
		//		Tractament de dades obtingudes
		$.ajaxSetup({
			async : true
		}); //execute asynchronously
		return datastats;
	}

	function getXAxis(axis){
		var timeUTC=[];
		var timeAxis=[];
		var hours;
		var minutes;
		var seconds;

		for (var i=0; i<axis.length;i++){
			timeUTC.push(new Date(axis[i]*1000));
			hours =timeUTC[i].getHours();
			minutes =timeUTC[i].getMinutes();
			seconds=timeUTC[i].getSeconds();

			if ((hours >= 0)&&(hours <= 9)){ 
				hours="0"+hours; 
			}

			if ((minutes >= 0)&&(minutes <= 9)){ 
				minutes="0"+minutes; 
			}

			if ((seconds >= 0)&&(seconds <= 9)){ 
				seconds="0"+seconds; 
			}
			timeAxis[i]= hours+":"+minutes+":"+seconds;
		}
		return timeAxis;
	}
}

function byMinuteGraph(){
	//Petició REST de les dades inicials
	var data = getData();
	var timeAxis=[];
	timeAxis=getXAxis(data.timeAxxis);

	$('#statisticsGraph').highcharts({
		chart: {
			type: 'areaspline',
			animation: false,
		},
		title: {
			text: data.parameter+' by '+selectedValueType+' on port: ' +data.idObject
		},
		legend: {
			x: 0,
			y: 300,
		},
		xAxis: {
			tickPixelInterval: 1920,
			categories: timeAxis,
		},
		yAxis: {
			title: {
				text: data.parameter
			}
		},
		tooltip: {
			shared: true,
			valueSuffix: valueSuffix,
			crosshairs: [true, true]
		},
		credits: {
			enabled: false
		},
		plotOptions: {
			areaspline: {
				fillOpacity: 0.5
			},

		},
		series: [{
			name: data.parameter,
			data: data.valueAxxis,
			color: '#AA0000'
		}]

	});
	//j es podra esborrar
	var j=0;
	refreshIntervalIdPort = setInterval(function() {
		refresh();
	},62000);

	function refresh() {
		//PART A: Aquesta part es podra esborrar en integrarse completament
		j++;
		if((j % 2)==0){
			var data2= getData();

		}else{
			//FINAL PART A
			var data2= getData();

		}
		var chart = $('#statisticsGraph').highcharts();
		timeAxis=getXAxis(data2.timeAxxis);
		chart.xAxis[0].setCategories(timeAxis);
		chart.series[0].setData(data2.valueAxxis);
	}

	/*funció de petició (no passa)REST. Per ara es static*/
	function getData(){
		var datastats = {};
		var datastatsuri = "/AppServer/webapi/statistics/port/"+selectedPort+"/"+selectedParam+"/"+selectedValueType+"/minute";
		$.ajaxSetup({
			async : false
		}); //execute synchronously

		//console.log("GETTING STATS");
		$.ajax({
			type: "GET",
			url: datastatsuri,
			//contentType: 'json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
			//headers: {
			//	Accept : "application/vmd.dxat.appserver.stats+json"
			//},
			success : function(result) {
				datastats = result;
			},
			error: function(xhr, msg) {
				var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
				datastats = JSON.parse(rplcd);
			}
		});
		//		Tractament de dades obtingudes
		$.ajaxSetup({
			async : true
		}); //execute asynchronously
		return datastats;
	}

	function getXAxis(axis){
		var timeUTC=[];
		var timeAxis=[];
		var hours;
		var minutes;
		var seconds;

		for (var i=0; i<axis.length;i++){
			timeUTC.push(new Date(axis[i]*1000));
			hours =timeUTC[i].getHours();
			minutes =timeUTC[i].getMinutes();
			seconds=timeUTC[i].getSeconds();

			if ((hours >= 0)&&(hours <= 9)){ 
				hours="0"+hours; 
			}

			if ((minutes >= 0)&&(minutes <= 9)){ 
				minutes="0"+minutes; 
			}

			if ((seconds >= 0)&&(seconds <= 9)){ 
				seconds="0"+seconds; 
			}
			timeAxis[i]= hours+":"+minutes+":"+seconds;
		}
		return timeAxis;
	}
}

function bySecondGraph(){
	//Petició REST de les dades inicials
	var data = getData();
	var timeData=[];
	var valueData=[];
	for (var i=0; i<data.timeAxxis.length;i++){
		timeData.push(data.timeAxxis[i]*1000);
		valueData.push(data.valueAxxis[i]);
	}

	var timeUTC=[];
	var timeAxis=[];
	timeAxis = getXAxis(timeData);

	$('#statisticsGraph').highcharts({
		chart: {
			type: 'areaspline',
			animation: false,
		},
		title: {
			text: data.parameter+' by '+selectedValueType+' on port: ' +data.idObject
		},
		legend: {
			x: 0,
			y: 300,
		},
		xAxis: {
			type: 'datetime',
			dateTimeLabelFormats: {

			},
			tickPixelInterval: 1920,
			categories: timeAxis,
		},
		yAxis: {
			title: {
				text: data.parameter
			}
		},
		tooltip: {
			shared: true,
			valueSuffix: valueSuffix,
			crosshairs: [true, true],
			valueDecimals: 2
		},
		credits: {
			enabled: false
		},
		plotOptions: {
			areaspline: {
				fillOpacity: 0.5
			},
		},
		series: [{
			name: data.parameter,
			data: valueData,
			color: '#0000AA'
		}]

	});

	var j=0;
	var RefreshChart = 60;
	refreshIntervalIdPort = setInterval(function() {
		refresh();
	},1000);


	function refresh() {
		var data2 = getData();

		var chart = $('#statisticsGraph').highcharts();

		if (timeData.length>=RefreshChart){
			timeData.reverse();
			valueData.reverse();
			timeData.pop();
			//timeData.pop();
			valueData.pop();
			//valueData.pop();
			timeData.reverse();
			valueData.reverse();
		}

		for (var i=0; i<data2.timeAxxis.length;i++){
			timeData.push((data2.timeAxxis[i]*1000));
			valueData.push(data2.valueAxxis[i]);
		}

		timeAxis = getXAxis(timeData);

		chart.series[0].setData(valueData);
		chart.xAxis[0].setCategories(timeAxis);
	}

	function getXAxis(axis){
		var timeUTC=[];
		var timeAxis=[];
		var hours;
		var minutes;
		var seconds;

		for (var i=0; i<axis.length;i++){
			timeUTC.push(new Date(axis[i]*1000));
			hours =timeUTC[i].getHours();
			minutes =timeUTC[i].getMinutes();
			seconds=timeUTC[i].getSeconds();

			if ((hours >= 0)&&(hours <= 9)){ 
				hours="0"+hours; 
			}

			if ((minutes >= 0)&&(minutes <= 9)){ 
				minutes="0"+minutes; 
			}

			if ((seconds >= 0)&&(seconds <= 9)){ 
				seconds="0"+seconds; 
			}
			timeAxis[i]= hours+":"+minutes+":"+seconds;
		}
		return timeAxis;
	}

	function getData(){
		var datastats = {};
		//console.log('PORT:'+selectedPort+' PARAM:'+selectedParam+' TYPE:'+selectedValueType);
		var datastatsuri = "/AppServer/webapi/statistics/port/"+selectedPort+"/"+selectedParam+"/"+selectedValueType+"/second";
		$.ajaxSetup({
			async : false
		}); //execute synchronously

		//console.log("GETTING STATS");
		$.ajax({
			type: "GET",
			url: datastatsuri,
			//contentType: 'json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
			//headers: {
			//	Accept : "application/vmd.dxat.appserver.stats+json"
			//},
			success : function(result) {
				datastats = result;
			},
			error: function(xhr, msg) {
				var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
				datastats = JSON.parse(rplcd);
			}
		});
		$.ajaxSetup({
			async : true
		}); //execute asynchronously
		return datastats;
	}
}

function loadDefaultStatValues(){
	clearInterval(refreshIntervalIdPort);
	clearInterval(refreshIntervalController);
	$('#textgraph').show();
	$('#statisticsGraph').hide();
	selectedPort = "";
	selectedParam = "receiveBytes"; //default value
	selectedValueType = "AVERAGE";  //devault value
	selectedTimeInterval = "hour";  //devault value
	valueSuffix = " Bytes/s";
	$("#bparam").html('receiveBytes <span class="caret"></span>');
	$("#bvaluetype").html('AVERAGE <span class="caret"></span>');
	$("#btinterval").html('Last hour <span class="caret"></span>');
}

function printPortGraph(){
	clearInterval(refreshIntervalIdPort);
	clearInterval(refreshIntervalController);

	if(selectedPort != ""){
		$('#advisementClick').hide();
		$('#textgraph').hide();
		$('#statisticsGraph').show();
		//console.log("PORT STATS");
		//console.log("Port ID: ");
		//console.log(selectedPort);
		//console.log("Selected port stats: "+ selectedParam+ " with "+ selectedValueType+" "+selectedTimeInterval);
		switch(selectedParam){
		case "receivePackets": valueSuffix = " Packets/s";
		break;
		case "transmitPackets": valueSuffix = " Packets/s";
		break;
		case "receiveBytes": valueSuffix = " Bytes/s";
		break;
		case "transmitBytes": valueSuffix = " Bytes/s";
		break;
		case "receiveDropped": valueSuffix = " Packets/s";
		break;
		case "transmitDropped": valueSuffix = " Packets/s";
		break;
		case "receiveErrors": valueSuffix = " Packets/s";
		break;
		case "transmitErrors": valueSuffix = " Packets/s";
		break;
		case "receiveFrameErrors": valueSuffix = " Packets/s";
		break;
		case "receiveOverrunErrors": valueSuffix = " Packets/s";
		break;
		case "receiveCRCErrors": valueSuffix = " Packets/s";
		break;
		case "collisions": valueSuffix = " Packets/s";
		break;
		default : valueSuffix = " Bytes/s";
		break;
		}

		if(selectedTimeInterval == "hour") byHourGraph();
		if(selectedTimeInterval == "minute") byMinuteGraph();
		if(selectedTimeInterval == "second") bySecondGraph();


		//NO PORT SPECIFIED!!!
	}else alert("Please, select a port from the list");
}

//GRAPH STATS CALL TO NEW STATISTIC'S GENERATION
function showPortStats(){
	if(event.target.id == "port") selectedPort = $(event.target).text();
	if(event.target.id == "param"){
		console.log("selected param: "+ $(event.target).text());
		selectedParam = $(event.target).text();
		$("#bparam").html($(event.target).text()+' <span class="caret"></span>');
	}
	if(event.target.id == "valuetype"){
		console.log("selected valuetype: "+$(event.target).text());
		selectedValueType = $(event.target).text();
		$("#bvaluetype").html($(event.target).text()+' <span class="caret"></span>');
	}
	if(event.target.id == "tinterval"){
		console.log("selected time interval: "+$(event.target).text());
		switch ($(event.target).text()){
		case "Per minute": selectedTimeInterval = "minute";
		$("#btinterval").html('Per minute <span class="caret"></span>');
		break;
		case "Per hour": selectedTimeInterval = "hour";
		$("#btinterval").html('Per hour <span class="caret"></span>');
		break;
		case "Per second": selectedTimeInterval = "second";
		$("#btinterval").html('Per second <span class="caret"></span>');
		break;
		default:
			break;
		}
	}

	printPortGraph();

}






//CONTROLLER STATS
var gauges = [];

function createGauge(name, label, min, max)
{
	var config =
	{
			size: 120,
			label: label,
			min: undefined != min ? min : 0,
					max: undefined != max ? max : 100,
							minorTicks: 5
	};

	var range = config.max - config.min;
	config.yellowZones = [{ from: config.min + range*0.75, to: config.min + range*0.9 }];
	config.redZones = [{ from: config.min + range*0.9, to: config.max }];

	gauges[name] = new Gauge(name + "GaugeContainer", config);
	gauges[name].render();
}

function createGauges()
{
	createGauge("memory", "Memory");
	createGauge("cpu", "CPU");
	//createGauge("network", "Network");
}

function updateGauges()
{
	for (var key in gauges)
	{
		//console.log("updating gauge:");
		//console.log(key);
		var value = getControllerData(key);//getRandomValue(gauges[key]);
		gauges[key].redraw(value);
	}
}

function getControllerData(key){
	var datastats = {};
	//http://147.83.113.109:8080/AppServer/webapi/statistics/controller/MemoryPCT/AVERAGE/second
	//http://147.83.113.109:8080/AppServer/webapi/statistics/controller/CpuAvg/AVERAGE/second
	var param = (key == "memory" ? "MemoryPCT" : "CpuAvg");
	var datastatsuri = "/AppServer/webapi/statistics/controller/"+param+"/AVERAGE/second";
	$.ajaxSetup({
		async : false
	}); //execute synchronously

	//console.log("GETTING STATS");
	$.ajax({
		type: "GET",
		url: datastatsuri,
		//contentType: 'json',
		//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
		//headers: {
		//	Accept : "application/vmd.dxat.appserver.stats+json"
		//},
		success : function(result) {
			datastats = result;
		},
		error: function(xhr, msg) {
			var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
			datastats = JSON.parse(rplcd);
		}
	});
	//		Tractament de dades obtingudes
	$.ajaxSetup({
		async : true
	}); //execute asynchronously
	return datastats.valueAxxis[0];;
}
////OLD RANDOM DUMMY VALUE
//function getRandomValue(gauge)
//{
//var overflow = 0; //10;
//return gauge.config.min - overflow + (gauge.config.max - gauge.config.min + overflow*2) *  Math.random();
//}

function initializeControllerStats()
{
	createGauges();
	updateGauges();
	refreshIntervalController = setInterval(updateGauges, 3000);
}



//TRAFFIC MATRIX
var refreshIntervalTrafficMatrix;
function stopTrafficMatrix(){
	clearInterval(refreshIntervalTrafficMatrix);
}

function startTrafficMatrix(){
	refreshIntervalTrafficMatrix = setInterval(function() {
		setTrafficMatrix();
	},60000);
}

function setTrafficMatrix(){
	//DUMMY STATIC TEST REQUEST
	var trafficMatrixData;
	//http://147.83.113.109:8080/AppServer/webapi/statistics/trafficmatrix	
	//TODO IF NOT TRAFFIC MATRIX SHOW ALTERNATE MESSAGE
	var trafficMatrixURI = "/AppServer/webapi/statistics/trafficmatrix";
	$.ajaxSetup({
		async : false
	}); //execute synchronously

	//console.log("GETTING STATS");
	$.ajax({
		type: "GET",
		url: trafficMatrixURI,
		//contentType: 'json',
		//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
		//headers: {
		//	Accept : "application/vmd.dxat.appserver.stats+json"
		//},
		success : function(result) {
			trafficMatrixData = result;
		},
		error: function(xhr, msg) {
			var rplcd = xhr.responseText.replace(/\bNaN\b/g, "null");
			trafficMatrixData = JSON.parse(rplcd);
		}
	});
	//		Tractament de dades obtingudes
	$.ajaxSetup({
		async : true
	}); //execute asynchronously
	
	var matrix = trafficMatrixData;
//	{
//			"matrix": [
//			           {
//			        	   "dst": "10.0.0.4",
//			        	   "src": "10.0.0.1",
//			        	   "traffic": 1250000
//			           },
//			           {
//			        	   "dst": "10.0.0.3",
//			        	   "src": "10.0.0.2",
//			        	   "traffic": 625000
//			           },
//			           {
//			        	   "dst": "10.0.0.2",
//			        	   "src": "10.0.0.3",
//			        	   "traffic": 312500
//			           },
//			           {
//			        	   "dst": "10.0.0.1",
//			        	   "src": "10.0.0.4",
//			        	   "traffic": 900000
//			           }
//			           ]
//	};
	var nodes=[];
	var links=[];
	var pushSrc=true;
	var pushDst=true;

	//alert(matrix.matrix.length);
	for (var i=0; i<matrix.matrix.length;i++){
		for(var j=0; j<nodes.length;j++){
			if(matrix.matrix[i].src==nodes[j].name){
				pushSrc=false;	
			}
		}

		if (pushSrc){
			nodes.push({"name":matrix.matrix[i].src});
			pushSrc=true;
		}

		//alert(nodes[0].name);
		for(var j=0; j<nodes.length;j++){
			if(matrix.matrix[i].dst==nodes[j].name){
				pushDst=false;	
			}
		}

		if (pushDst){
			nodes.push({"name":matrix.matrix[i].dst});
			pushDst=true;
		}
	}

	sorting(nodes,'name');
	for (var i=0; i<matrix.matrix.length;i++){
		for(var j=0;j<nodes.length;j++){
			if (matrix.matrix[i].src==nodes[j].name){
				var src = j;
			}
			if(matrix.matrix[i].dst==nodes[j].name){
				var dst = j;
			}
		}

		links.push({"source":src,"target":dst,"value":matrix.matrix[i].traffic});

	}





	function getGreenToRed(percent){
		g = percent<50 ? 255 : Math.floor(255-(percent*2-100)*255/100);
		r = percent>50 ? 255 : Math.floor((percent*2)*255/100);
		//var col = "#"+r+""+g+"0";
		//alert(col);
		return 'rgb('+r+','+g+',0)';
	}

	function sorting(json_object, key_to_sort_by) {
		function sortByKey(a, b) {
			var x = a[key_to_sort_by];
			var y = b[key_to_sort_by];
			return ((x < y) ? -1 : ((x > y) ? 1 : 0));
		}

		json_object.sort(sortByKey);
	}

	var color = pv.Colors.category19();//.by(function(d) d.group);
	var h = $("#tMatrixTAB").width()/2;
	var w = $("#tMatrixTAB").width()/2;
	//console.log("NEW SIZE: "+w+" x "+h);
	var vis = new pv.Panel()
	.canvas('Tmatrix')
	.width(w)
	.height(h)
	.top(90)
	.left(90);

	var layout = vis.add(pv.Layout.Matrix)
	.nodes(nodes)
	.directed(true)
	.links(links)
	.sort(function(a, b) {return b.name;});
	//.sort(function(a, b) {alert(b.name);return b.group - a.group});

	layout.link.add(pv.Bar)
	.fillStyle(function(l){ return getGreenToRed(l.linkValue*8/100000);})
	.anchor("center").add(pv.Label)
	.text(function(l) {return (l.linkValue*8/100000).toFixed(2)+" %"} )	
	.antialias(false)
	.lineWidth(8);

	layout.label.add(pv.Label)
	.textStyle(color)
	.text(function(d){return d.name;});

	vis.render();

	$('#Tmatrix').bind('resize', function(){
		//console.log('Tmatrix resized');
	});
	$(window).resize(function(){
		var color = pv.Colors.category19();//.by(function(d) d.group);

		var h = $("#tMatrixTAB").width()/2;
		var w = $("#tMatrixTAB").width()/2;
		//console.log("NEW SIZE: "+w+" x "+h);
		var vis = new pv.Panel()
		.canvas('Tmatrix')
		.width(w)
		.height(h)
		.top(90)
		.left(90);

		var layout = vis.add(pv.Layout.Matrix)
		.nodes(nodes)
		.directed(true)
		.links(links)
		.sort(function(a, b) {return b.name;});
		//.sort(function(a, b) {alert(b.name);return b.group - a.group});

		layout.link.add(pv.Bar)
		.fillStyle(function(l){ return getGreenToRed(l.linkValue*8/100000);})
		.anchor("center").add(pv.Label)
		.text(function(l) {return (l.linkValue*8/100000).toFixed(2)+" %"} )
		.antialias(false)
		.lineWidth(8);

		layout.label.add(pv.Label)
		.textStyle(color)
		.text(function(d){return d.name;});

		vis.render();		
	});
}