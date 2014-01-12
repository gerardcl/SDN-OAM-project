$(document).ready(function(){

	//initStatusOverview();

	//ONCE REST LOADED THEN OPEN WEBSOCKET FOR REALTIME CHANGES
});

function initStatusOverview(){
	$("#statistics").hide();
	createTopologyGraph();
	initializeControllerStats();
}

//TOPOLOGY GRAPH
function createTopologyGraph(){
	var width = 400, height = 300;

	//var color = d3.scale.category20();

	var force = d3.layout.force().charge(-1120).linkDistance(70).size(
			[ width, height ]);

	var svg = d3.select("#topo")
	.append("svg")
	.attr({
		"width": "100%",
		"height": height
	})
	.attr("viewBox", "0 0 " + width + " " + height )
	.attr("preserveAspectRatio", "xMidYMid meet")
	.attr("pointer-events", "all")
	.call(d3.behavior.zoom().on("zoom", redraw));

	var pict = svg
	.append('svg:g');

	function redraw() {
		pict.attr("transform",
				"translate(" + d3.event.translate + ")"
				+ " scale(" + d3.event.scale + ")");
	}

	var switchesURL = "/AppServer/webapp/topology/switches";
	var terminalsURL = "/AppServer/webapp/topology/terminals";
	var linksURL = "/AppServer/webapp/topology/links";
	var dataSwitches;
	var dataTerminals;
	var dataLinks;
	var nodes = [];
	var links = [];

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
	var terminals = dataTerminals.terminals;
	nodes = dataSwitches.switches;
	var rawLinks = dataLinks.links;
	var src, trg;

	for (var i = 0; i < rawLinks.length; i++) {
		for (var j = 0; j < nodes.length; j++) {
			for (var k = 0; k < nodes[j].ports.length; k++) {
				if (rawLinks[i].srcPortId == nodes[j].ports[k].portId) {
					src = j;
				}
				if (rawLinks[i].dstPortId == nodes[j].ports[k].portId) {
					trg = j;
				}
			}
			if (!((src == null) || (trg == null))) {
				//alert(src+" "+trg);
				links.push({
					"source" : src,
					"target" : trg,
					"value" : 8
				});
				src = null;
				trg = null;
			}
		}
	}

	for (var i = 0; i < terminals.length; i++) {
		nodes.push(terminals[i]);
	}
	for (var i = 0; i < nodes.length; i++) {
		if (nodes[i].portAPId != null) {
			//alert("STEP1: "+nodes[i].portAPId);
			for (var j = 0; j < nodes.length; j++) {
				if (nodes[j].portAPId == null) {
					for (var k = 0; k < nodes[j].ports.length; k++) {
						//alert("STEP2: "+nodes[j].ports[k].portId);
						if (nodes[i].portAPId == nodes[j].ports[k].portId) {
							src = i;
							trg = j;
							links.push({
								"source" : src,
								"target" : trg,
								"value" : 8
							});
						}
					}
				}
			}
		}
	}

//	Represenació grafic
	force.nodes(nodes).links(links).start();

	var link = svg.selectAll(".link").data(links).enter().append("line")
	.attr("class", "link")
//	.style("stroke", "#F00")
	.style("stroke-width", function(d) {
		return Math.sqrt(d.value);
	});

	var node = svg.selectAll(".node").data(nodes).enter().append("circle")
	.attr("class", (function(d) {
		if (d.enabled == true) {
			return "nodeGREEN";
		} else {
			return "nodeRED";
		}
	})).attr("r", 25).call(force.drag);

	var term = svg.selectAll(".node").data(terminals).enter().append(
	"circle").attr("class", (function(d) {
		if (d.enabled == true) {
			return "nodeBLUE";
		} else {
			return "nodeRED";
		}
	})).attr("r", 25).call(force.drag);

	node.append("title").text(
			function(d) {
				if (d.portAPId == null) {
					var ports = d.ports.length;

					for (var i = 0; i < d.ports.length; i++) {
						//alert(d.ports[j].mac);
						ports += "\n   Port " + i + ":\n     MAC: "
						+ d.ports[i].mac + "\n     Name: "
						+ d.ports[i].name + "\n     PortId: "
						+ d.ports[i].portId;
						//alert(ports);
					}
				}

				return ("switch ID: " + d.swId + "\n Hardware: "
						+ d.hardware + "\n Software: " + d.software
						+ "\n Manufacturer: " + d.manufacturer
						+ "\n Total ports: " + ports);
			});

	term.append("title").text(
			function(d) {
				return ("Terminal Id: " + d.terminalId + "\n IPv4: "
						+ d.ipv4 + "\n MAC: " + d.mac
						+ "\n SDN Access port: " + d.portAPId);
			});

	force.on("tick", function() {
		link.attr("x1", function(d) {
			return d.source.x;
		}).attr("y1", function(d) {
			return d.source.y;
		}).attr("x2", function(d) {
			return d.target.x;
		}).attr("y2", function(d) {
			return d.target.y;
		});

		node.attr("cx", function(d) {
			return d.x;
		}).attr("cy", function(d) {
			return d.y;
		});

		term.attr("cx", function(d) {
			return d.x;
		}).attr("cy", function(d) {
			return d.y;
		});

	});

	node.on("click", function(d) {
		console.log("node " + d.swId + " was clicked");
		var switchInfo = "<h4> Switch info</h4>";
		switchInfo += "<b>ID:</b>  "+d.swId+"<p>";
		switchInfo += "<b>Manufacturer:</b>  "+d.manufacturer;
		var portList ="<h4>Port list by id</h4>";
		for (var i = d.ports.length-1 ; i >= 0 ; i--) {
			portList += "<li class='list-group-item' id='port' onclick='showPortStats();' style='width: 100%; height: 42px; margin: 0 auto; font-size:100%;' >"+ d.ports[i].portId+"</li>";
		}
		var packetCount = "45";
		var byteCount = "1.2k";
		var flowCount = "2";
		$("#switchInfo").html(switchInfo);
		$("#packetCount").html(packetCount);
		$("#byteCount").html(byteCount);
		$("#flowCount").html(flowCount);
		$("#portList").html(portList);
		loadDefaultStatValues();
		$("#statistics").show();
	});

	term.on("click", function(d) {
		console.log("terminal " + d.terminalId + " was clicked");
	});
}





//STATISTIC'S GENERATION
var selectedPort = "";
var selectedParam = "";
var selectedValueType = "";
var selectedTimeInterval = "";
var refreshIntervalId;

function replaceOneChar(s,c,n){
	console.log("replacing char ( "+ c+") of string:");
	console.log(s);
	var re = new RegExp('^(.{'+ --n +'}).(.*)$','');
	return s.replace(re,'$1'+c+'$2');
};

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
			text: data.parameter+' on port: ' +data.idObject
		},
		legend: {
			x: 0,
			y: 300,
		},
		xAxis: {
			tickPixelInterval: 1500,
			categories: timeAxis,
		},
		yAxis: {
			title: {
				text: data.parameter
			}
		},
		tooltip: {
			shared: true,
			valueSuffix: ' Bytes/s',
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
	refreshIntervalId = setInterval(function() {
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
		var datastatsuri = "/AppServer/webapp/statistics/port/"+selectedPort+"/"+selectedParam+"/"+selectedValueType+"/hour";
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
		return datastats;
	}

	function getXAxis(axis){
		var timeUTC=[];
		var timeAxis=[];
		for (var i=0; i<axis.length;i++){
			timeUTC.push(new Date(axis[i]*1000));
			timeAxis[i]= timeUTC[i].getHours()+":"+timeUTC[i].getMinutes()+":"+timeUTC[i].getSeconds();
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
			text: data.parameter+' on port: ' +data.idObject
		},
		legend: {
			x: 0,
			y: 300,
		},
		xAxis: {
			tickPixelInterval: 1500,
			categories: timeAxis,
		},
		yAxis: {
			title: {
				text: data.parameter
			}
		},
		tooltip: {
			shared: true,
			valueSuffix: ' Bytes/s',
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
	refreshIntervalId = setInterval(function() {
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
		var datastatsuri = "/AppServer/webapp/statistics/port/"+selectedPort+"/"+selectedParam+"/"+selectedValueType+"/minute";
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
		return datastats;
	}

	function getXAxis(axis){
		var timeUTC=[];
		var timeAxis=[];
		for (var i=0; i<axis.length;i++){
			timeUTC.push(new Date(axis[i]*1000));
			timeAxis[i]= timeUTC[i].getHours()+":"+timeUTC[i].getMinutes()+":"+timeUTC[i].getSeconds();
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
	for (var i=0;i<timeData.length;i++){
		timeUTC[i] = new Date(timeData[i]);
		timeAxis[i]= timeUTC[i].getHours()+":"+timeUTC[i].getMinutes()+":"+timeUTC[i].getSeconds();
		//alert(timeAxis[i]);
	}

	var lastTime = timeData[(timeData.length-1)*1000];

	$('#statisticsGraph').highcharts({
		chart: {
			type: 'areaspline',
			animation: false,
		},
		title: {
			text: data.parameter+' on port: ' +data.idObject
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
			valueSuffix: ' Bytes/s',
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
	lastTime=data.timeAxxis[0];
	refreshIntervalId = setInterval(function() {
		refresh();
	},1000);


	function refresh() {
		//PART A: PART QUAN ESTIGUI INTEGRAT ES ESBORRABLE
		var data2 = getData();
		lastTime = data2.timeAxxis[0];
		//FINAL PART A

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

		for (var i=0;i<timeData.length;i++){
			timeUTC[i] = new Date(timeData[i]);
			timeAxis[i]= timeUTC[i].getHours()+":"+timeUTC[i].getMinutes()+":"+timeUTC[i].getSeconds();

		}
		chart.series[0].setData(valueData);
		chart.xAxis[0].setCategories(timeAxis);
	}

	function getData(){
		var datastats = {};
		var datastatsuri = "/AppServer/webapp/statistics/port/"+selectedPort+"/"+selectedParam+"/"+selectedValueType+"/second";
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
		$.ajaxSetup({
			async : true
		}); //execute asynchronously
		return datastats;
	}
}

function loadDefaultStatValues(){
	clearInterval(refreshIntervalId);
	$('#textgraph').show();
	$('#statisticsGraph').hide();
	selectedPort = "";
	selectedParam = "receiveBytes"; //default value
	selectedValueType = "AVERAGE";  //devault value
	selectedTimeInterval = "hour";  //devault value
	$("#bparam").html('receiveBytes <span class="caret"></span>');
	$("#bvaluetype").html('AVERAGE <span class="caret"></span>');
	$("#btinterval").html('Last hour <span class="caret"></span>');
}

function printPortGraph(){
	clearInterval(refreshIntervalId);

	if(selectedPort != ""){
		$('#textgraph').hide();
		$('#statisticsGraph').show();
		console.log("PORT STATS");
		console.log("Port ID: ");
		console.log(selectedPort);
		console.log("Selected port stats: "+ selectedParam+ " with "+ selectedValueType+" "+selectedTimeInterval);

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
		var value = getRandomValue(gauges[key]);
		gauges[key].redraw(value);
	}
}

//TODO CHANGE THE RANDOM VALUE TO AJAX REQUEST!!!
function getRandomValue(gauge)
{
	var overflow = 0; //10;
	return gauge.config.min - overflow + (gauge.config.max - gauge.config.min + overflow*2) *  Math.random();
}

function initializeControllerStats()
{
	createGauges();
	setInterval(updateGauges, 2000);
}