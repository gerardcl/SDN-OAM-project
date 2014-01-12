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
			//Et poso aqui la posibilitat de colors
			color: '#00AA00'
		}]

	});
	//j es podra esborrar
	var j=0;
	refreshIntervalId = setInterval(function() {
		refresh();  
	},90000);

	function refresh() {
		//PART A: Aquesta part es podra esborrar en integrarse completament
		j++;
		if((j % 2)==0){
			var data2= nextData();

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
		var result ={"idObject":"00:01:d4:ca:6d:c4:44:1e:1","parameter":"receiveBytes","timeAxxis":[1389478620,1389478680,1389478740,1389478800,1389478860,1389478920,1389478980,1389479040,1389479100,1389479160,1389479220,1389479280,1389479340,1389479400,1389479460,1389479520,1389479580,1389479640,1389479700,1389479760,1389479820,1389479880,1389479940,1389480000,1389480060,1389480120,1389480180,1389480240,1389480300,1389480360,1389480420,1389480480,1389480540,1389480600,1389480660,1389480720,1389480780,1389480840,1389480900,1389480960,1389481020,1389481080,1389481140,1389481200,1389481260,1389481320,1389481380,1389481440,1389481500,1389481560,1389481620,1389481680,1389481740,1389481800,1389481860,1389481920,1389481980,1389482040,1389482100,1389482160,1389482220,1389482280],"valueAxxis":[0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,695.0,695.0,695.0,695.0,695.0,695.0,695.0,695.0,0]}
		return result;
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
	/*funció de segona petició REST (ESBORRABLE) quan les peticions rest estiguin operatives*/
	function nextData(){
		var result ={
				"idObject":"00:01:d4:ca:6d:c4:44:1e:1",
				"parameter":"receiveBytes",
				"timeAxxis":[1389482281,1389482282,1389482283,1389482284,1389482285,1389482286,1389482287,1389482288,1389482289,1389482290,1389482291,1389482292,1389482293,1389482294,1389482295,1389482296,1389482297,1389482298,1389482299,1389482300,1389482301,1389482302,1389482303,1389482304,1389482305,1389482306,1389482307,1389482308,1389482309,1389482310,1389482311,1389482312,1389482313,1389482314,1389482315,1389482316,1389482317,1389482318,1389482319,1389482320,1389482321,1389482322,1389482323,1389482324,1389482325,1389482326,1389482327,1389482328,1389482329,1389482330,1389482331,1389482332,1389482333,1389482334,1389482335,1389482336,1389482337,1389482338,1389482339,1389482340,1389482341,1389482342],
				"valueAxxis":[20.0,52.0,340.0,456.0,42.0,789.3,50.4,553.3,251.3,123.4,20.0,52.0,340.0,456.0,42.0,789.3,50.4,553.3,251.3,123.4,20.0,52.0,340.0,456.0,42.0,789.3,50.4,553.3,251.3,123.4,20.0,52.0,340.0,456.0,42.0,789.3,50.4,553.3,251.3,123.4,20.0,52.0,340.0,456.0,42.0,789.3,50.4,553.3,251.3,123.4,589.2,25.4,789.2,695.0,695.0,695.0,695.0,695.0,695.0,695.0,695.0,2000.2]
		};

		return result;		
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
            data: data.valueAxxis
        }]
		      
    });
    //j es podra esborrar
	var j=0;
	refreshIntervalId = setInterval(function() {
	    refresh();  
	},5000);

	function refresh() {
		//PART A: Aquesta part es podra esborrar en integrarse completament
		j++;
		if((j % 2)==0){
			var data2= nextData();
			
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
		var result ={
				"idObject":"00:01:d4:ca:6d:d4:4f:6b:3",
				"parameter":"receiveBytes",
				"timeAxxis":[1388686905,1388686906,1388686907,1388686908,1388686909,1388686910,1388686911,1388686912,1388686913,1388686914,1388686915,1388686916,1388686917,1388686918,1388686919,1388686920,1388686921,1388686922,1388686923,1388686924,1388686925,1388686926,1388686927,1388686928,1388686929,1388686930,1388686931,1388686932,1388686933,1388686934,1388686935,1388686936,1388686937,1388686938,1388686939,1388686940,1388686941,1388686942,1388686943,1388686944,1388686945,1388686946,1388686947,1388686948,1388686949,1388686950,1388686951,1388686952,1388686953,1388686954,1388686955,1388686956,1388686957,1388686958,1388686959,1388686960,1388686961,1388686962,1388686963,1388686964,1388686965],
				"valueAxxis":[61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,462.0,60.0,0.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,60.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,60.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,60.0,0.0,0.0,0.0,0.0,61.0,1500.0]
				};
		return result;
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
	/*funció de segona petició REST (ESBORRABLE) quan les peticions rest estiguin operatives*/
	function nextData(){
		var result ={
			"timeAxxis":[1388686965,1388686966,1388686967,1388686968,1388686969,1388686970,1388686971,1388686972,1388686973,1388686974,1388686975,1388686976,1388686977,1388686978,1388686979,1388686980,1388686981,1388686982,1388686983,1388686984,1388686985,1388686986,1388686987,1388686988,1388686989,1388686990,1388686991,1388686992,1388686993,1388686994,1388686995,1388686996,1388686997,1388686998,1388686999,1388687000,1388687001,1388687002,1388687003,1388687004,1388687005,1388687006,1388687007,1388687008,1388687009,1388687010,1388687011,1388687012,1388687013,1388687014,1388687015,1388687016,1388687017,1388687018,1388687019,1388687020,1388687021,1388687022,1388687023,1388687024,1388687025],
			"valueAxxis":[561.0,254.0,1100.0,0.0,10.0,50.0,5.0,700.0,462.0,160.0,10.0,20.0,30.0,40.0,0.0,61.0,120.0,1500.0,120.0,10.0,0.0,0.0,0.0,0.0,60.0,0.0,1540.0,120.0,0.0,61.0,10.0,20.0,90.0,0.0,2440.0,0.0,9900.0,0.0,0.0,60.0,0.0,10.0,0.0,250.0,61.0,0.0,0.0,0.0,0.0,2530.0,0.0,1250.0,0.0,0.0,60.0,0.0,250.0,0.0,0.0,61.0,1500.0]
			};
		return result;		
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
			data: valueData
		}]

	});

	var j=0;
	var RefreshChart = 60;
	lastTime=data.timeAxxis[1]
	setInterval(function() {
		refresh();  
	},2000);


	function refresh() {			
		//PART A: PART QUAN ESTIGUI INTEGRAT ES ESBORRABLE
		var data2 = getData();
		lastTime = data2.timeAxxis[1];
		//FINAL PART A

		var chart = $('#statisticsGraph').highcharts();

		if (timeData.length>=RefreshChart){
			timeData.reverse();
			valueData.reverse();
			timeData.pop();
			timeData.pop();
			valueData.pop();
			valueData.pop();
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
		var datastatsuri = "/AppServer/webapp/statistics/port/00:01:d4:ca:6d:c4:44:1e:1/receiveBytes/MAX/second";
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
				console.log("AND THE WINNER IS....");
				console.log(result);
				datastats = result;
			},
			error: function(xhr, msg) {
				console.log("PARSING ERROR MSG");
				console.log(xhr.responseText);
				datastats = JSON.parse(xhr.responseText);
				//console.log(msg + '\n' + xhr.responseText);
				console.log("RX DATA STATS");
				console.log(datastats);
				if(!datastats){
					console.log("getting stats!!");
					//datastats = $.parseJSON(xhr.responseText);
				}
			}
		});
		//		Tractament de dades obtingudes
		$.ajaxSetup({
			async : true
		}); //execute asynchronously

		console.log(datastats);
		return datastats;
	}
	/*funció de segona petició REST (ESBORRABLE) quan les peticions rest estiguin operatives*/
	function nextData(timeData){
		var num1,num2;
		num1= Math.random()*1000;
		num2= Math.random()*2000;

		var result ={
				"timeAxxis":[timeData+1,timeData+2],
				"valueAxxis":[num1,num2]
		};
		return result;		
	}
}

function loadDefaultStatValues(){
	$('#textgraph').show();
	$('#statisticsGraph').hide();
	selectedPort = "";
	selectedParam = "receiveBytes"; //default value
	selectedValueType = "AVERAGE";  //devault value
	selectedTimeInterval = "hour";  //devault value
	$("#bparam").html('Received <span class="caret"></span>');
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
		switch ($(event.target).text()){
		case "Received":
			selectedParam = "receiveBytes";
			$("#bparam").html('Received <span class="caret"></span>');
			break;
		case "Transmitted":
			selectedParam = "transmitBytes";
			$("#bparam").html('Transmitted <span class="caret"></span>');
			break;
		case "Drops": selectedParam = "receiveDropped";
		$("#bparam").html('Drops <span class="caret"></span>');
		break;
		case "Errors": selectedParam = "receiveErrors";
		$("#bparam").html('Errors <span class="caret"></span>');
		break;
		case "Collisions": selectedParam = "collisions";
		$("#bparam").html('Collisions <span class="caret"></span>');
		break;
		default:
			break;
		}
	}
	if(event.target.id == "valuetype"){
		console.log("selected valuetype: "+$(event.target).text());
		switch ($(event.target).text()){
		case "MAX": selectedValueType = "MAX";
		$("#bvaluetype").html('MAX <span class="caret"></span>');
		break;
		case "MIN": selectedValueType = "MIN";
		$("#bvaluetype").html('MIN <span class="caret"></span>');
		break;
		case "AVERAGE": selectedValueType = "AVERAGE";
		$("#bvaluetype").html('AVERAGE <span class="caret"></span>');
		break;
		default:
			break;
		}
	}
	if(event.target.id == "tinterval"){
		console.log("selected time interval: "+$(event.target).text());
		switch ($(event.target).text()){
		case "Last minute": selectedTimeInterval = "minute";
		$("#btinterval").html('Last minute <span class="caret"></span>');
		break;
		case "Last hour": selectedTimeInterval = "hour";
		$("#btinterval").html('Last hour <span class="caret"></span>');
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