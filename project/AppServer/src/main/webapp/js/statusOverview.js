$(document).ready(function(){

	//initStatusOverview();

	//ONCE REST LOADED THEN OPEN WEBSOCKET FOR REALTIME CHANGES
});

function initStatusOverview(){
	$("#statistics").hide();
	createTopologyGraph();
	initializeControllerStats();


//	ANOTHER METHOD TO CALL PORT LIST WITH EVENTLISTENERS <<-- NOT USING IT NOW (see showPortStats() method)

//	document.querySelector('body').addEventListener('click', function(event) {
//	if (event.target.tagName.toLowerCase() === 'li') {
//	// do your action on your 'li' or whatever it is you're listening for
//	if(event.target.id == "port"){ 
//	console.log("PORT CLICK!" );
//	console.log($(event.target).text());
//	}
//	}
//	});
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
//	var RESTapi = "http://localhost:7474/db/data/node/1/traverse/node";
//	var switchesURL = "http://147.83.113.109:8080/AppServer/webapi/topology/all/switches";
	var nodes = [];
	var links = [];

	$.ajaxSetup({
		async : false
	}); //execute synchronously

	/*$.ajax({
			type: "GET",
			url: switchesURL,
			//contentType: 'application/json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
		    headers: { 		
		        Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"				
		    },	
		    success : function(result) {
		    	//EL CODE TINDRIA QUE ANAR AQUI
		    }
		});*/
//	Dummy petició postman http://147.83.113.109:8080/AppServer/webapi/topology/switches
	var dataSwitches = {
			"switches" : [ {
				"enabled" : true,
				"hardware" : "",
				"manufacturer" : "MikroTik",
				"ports" : [ {
					"enabled" : true,
					"mac" : "d4:ca:6d:d4:be:a5",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:d4:be:a1:3"
				}, {
					"enabled" : true,
					"mac" : "d4:ca:6d:d4:be:a4",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:d4:be:a1:2"
				}, {
					"enabled" : true,
					"mac" : "d4:ca:6d:d4:be:a3",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:d4:be:a1:1"
				} ],
				"software" : "",
				"swId" : "00:01:d4:ca:6d:d4:be:a1"
			}, {
				"enabled" : true,
				"hardware" : "",
				"manufacturer" : "MikroTik",
				"ports" : [ {
					"enabled" : true,
					"mac" : "d4:ca:6d:d4:4f:6f",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:d4:4f:6b:3"
				}, {
					"enabled" : true,
					"mac" : "d4:ca:6d:d4:4f:6e",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:d4:4f:6b:2"
				}, {
					"enabled" : true,
					"mac" : "d4:ca:6d:d4:4f:6d",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:d4:4f:6b:1"
				} ],
				"software" : "",
				"swId" : "00:01:d4:ca:6d:d4:4f:6b"
			}, {
				"enabled" : true,
				"hardware" : "",
				"manufacturer" : "MikroTik",
				"ports" : [ {
					"enabled" : true,
					"mac" : "d4:ca:6d:b5:f4:13",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:b5:f4:0f:7"
				}, {
					"enabled" : true,
					"mac" : "d4:ca:6d:b5:f4:12",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:b5:f4:0f:6"
				}, {
					"enabled" : true,
					"mac" : "d4:ca:6d:b5:f4:11",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:b5:f4:0f:5"
				} ],
				"software" : "",
				"swId" : "00:01:d4:ca:6d:b5:f4:0f"
			}, {
				"enabled" : true,
				"hardware" : "",
				"manufacturer" : "MikroTik",
				"ports" : [ {
					"enabled" : false,
					"mac" : "d4:ca:6d:c4:44:22",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:c4:44:1e:3"
				}, {
					"enabled" : true,
					"mac" : "d4:ca:6d:c4:44:21",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:c4:44:1e:2"
				}, {
					"enabled" : true,
					"mac" : "d4:ca:6d:c4:44:20",
					"name" : "",
					"portId" : "00:01:d4:ca:6d:c4:44:1e:1"
				} ],
				"software" : "",
				"swId" : "00:01:d4:ca:6d:c4:44:1e"
			} ]
	};
//	Petició de tots els links amb postman http://147.83.113.109:8080/AppServer/webapi/topology/links
	var dataLinks = {
			"links" : [ {
				"dstPortId" : "00:01:d4:ca:6d:d4:be:a1:1",
				"enabled" : true,
				"srcPortId" : "00:01:d4:ca:6d:b5:f4:0f:5"
			}, {
				"dstPortId" : "00:01:d4:ca:6d:d4:4f:6b:3",
				"enabled" : true,
				"srcPortId" : "00:01:d4:ca:6d:c4:44:1e:2"
			}, {
				"dstPortId" : "00:01:d4:ca:6d:d4:4f:6b:1",
				"enabled" : true,
				"srcPortId" : "00:01:d4:ca:6d:d4:be:a1:2"
			}, {
				"dstPortId" : "00:01:d4:ca:6d:c4:44:1e:2",
				"enabled" : true,
				"srcPortId" : "00:01:d4:ca:6d:d4:4f:6b:3"
			}, {
				"dstPortId" : "00:01:d4:ca:6d:d4:be:a1:2",
				"enabled" : true,
				"srcPortId" : "00:01:d4:ca:6d:d4:4f:6b:1"
			}, {
				"dstPortId" : "00:01:d4:ca:6d:c4:44:1e:1",
				"enabled" : true,
				"srcPortId" : "00:01:d4:ca:6d:b5:f4:0f:7"
			}, {
				"dstPortId" : "00:01:d4:ca:6d:b5:f4:0f:6",
				"enabled" : true,
				"srcPortId" : "00:01:d4:ca:6d:d4:4f:6b:2"
			}, {
				"dstPortId" : "00:01:d4:ca:6d:b5:f4:0f:5",
				"enabled" : true,
				"srcPortId" : "00:01:d4:ca:6d:d4:be:a1:1"
			}, {
				"dstPortId" : "00:01:d4:ca:6d:b5:f4:0f:7",
				"enabled" : true,
				"srcPortId" : "00:01:d4:ca:6d:c4:44:1e:1"
			}, {
				"dstPortId" : "00:01:d4:ca:6d:d4:4f:6b:2",
				"enabled" : true,
				"srcPortId" : "00:01:d4:ca:6d:b5:f4:0f:6"
			} ]
	};
//	Petició Postman de teminals http://147.83.113.109:8080/AppServer/webapi/topology/terminals
	var dataTerminals = {
			"terminals" : [ {
				"enabled" : true,
				"ipv4" : "192.168.0.66",
				"mac" : "b8:27:eb:ce:9b:33",
				"portAPId" : "00:01:d4:ca:6d:d4:be:a1:3",
				"terminalId" : "T-1"
			}, {
				"enabled" : true,
				"ipv4" : "0.0.0.0",
				"mac" : "00:13:49:19:31:33",
				"portAPId" : "00:01:d4:ca:6d:d4:be:a1:3",
				"terminalId" : "T-0"
			} ]
	};
	var terminals = dataTerminals.terminals;

//	Tractament de dades obtingudes
	$.ajaxSetup({
		async : true
	}); //execute asynchronously

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
var refreshIntervalIdS,refreshIntervalIdM,refreshIntervalIdH;

function byMinuteGraph(){
	//Petició REST de les dades inicials
	var data = getData();
	//DATE PARSER
	var labelXaxis = minuteFormat(data.timeAxxis);

	/*for (var i = 0; i<result.timeAxxis.length; i++) {
		result.timeAxxis[i]=result.timeAxxis[i]*1000;
	}*/
	//FINAL DATE PARSER
	var j=0;
	var count =0;
	var iSegment =0;
	var lastPos=0;
	var finish =false;
	var segmentX=[];
	var segmentY=[];

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
			/*type: 'datetime',
        	dateTimeLabelFormats: {
                //milisecond: '%H:%M:%S'
				OPTIONS
                {
                	millisecond: '%H:%M:%S.%L',
                	second: '%H:%M:%S',
                	minute: '%H:%M',
                	hour: '%H:%M',
                	day: '%e. %b',
                	week: '%e. %b',
                	month: '%b \'%y',
                	year: '%Y'
                }                        
            },*/
			tickPixelInterval: 1500,
			categories: labelXaxis,
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
			/*series: {
               pointStart: result.timeAxxis[0],
               pointInterval: 1 // one day
            }*/
		},
		series: [{
			name: data.parameter,
			data: data.valueAxxis
		}]

	});

	refreshIntervalIdM = setInterval(function() {
		console.log("refreshing stats graph");
		refresh();  
	},5000);

//	function refresh() {
//	//alert(newTimeS);
//	var data2= nextData();
//	var labelX2= minuteFormat(data2.timeAxxis);
//	var chart = $('#statisticsGraph').highcharts();

//	//alert(labelXaxis);
//	chart.xAxis[0].setCategories(labelX2);
//	chart.series[0].setData(data2.valueAxxis);

//	}
	function refresh() {
		j++;
		if((j % 2)==0){
			var data2= nextData();

		}else{
			var data2= getData();

		}
		var labelX2= minuteFormat(data2.timeAxxis);
		var chart = $('#statisticsGraph').highcharts();

		chart.xAxis[0].setCategories(labelX2);
		chart.series[0].setData(data2.valueAxxis);


	}
	
	function minuteFormat(xAxis){
		for (var i = 0; i<xAxis.length; i++) {
			var date = new Date(parseInt(xAxis[i]*1000));
			// using the excellent dateFormat code from Steve Levithan
			//axisItems[i].attr("text", dateFormat(date, "h:MM:ss"));//"dd/mm, htt"

			if ((i==0) || (i==(xAxis.length-1))){
				xAxis[i] = (dateFormat(date, "h:MM:ss"));//"dd/mm, htt"
			}
			else{
				xAxis[i] = (dateFormat(date, "ss"));//"dd/mm, htt"
			}
		}

		return xAxis;
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
	valueData=[];
	timeData.push(data.timeAxxis[0]);
	valueData.push(data.valueAxxis[0]);
	
	for (var i=1; i<60; i++){
		timeData.push(timeData[0]+i);
		valueData.push(0);
	}
	var lastTime =timeData[59];
	//alert(timeData[0]);
	//alert(timeData[59]);
	
	//DATE PARSER
	var labelXaxis = minuteFormat(timeData);	
	//alert(labelXaxis[0]);
	//alert(labelXaxis[59]);

	/*for (var i = 0; i<result.timeAxxis.length; i++) {
		result.timeAxxis[i]=result.timeAxxis[i]*1000;
	}*/
	//FINAL DATE PARSER

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
        	/*type: 'datetime',
        	dateTimeLabelFormats: {
                //milisecond: '%H:%M:%S'
				OPTIONS
                {
                	millisecond: '%H:%M:%S.%L',
                	second: '%H:%M:%S',
                	minute: '%H:%M',
                	hour: '%H:%M',
                	day: '%e. %b',
                	week: '%e. %b',
                	month: '%b \'%y',
                	year: '%Y'
                }                        
            },*/
        	tickPixelInterval: 1500,
            categories: labelXaxis,
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
           /* series: {
               pointStart: data.timeAxxis[0],
               pointInterval: 1 // one day
            }*/
        },
        series: [{
            name: data.parameter,
            data: valueData
        }]
		      
    });
    
	var j=0;
	var l=0;
	setInterval(function() {
	    refresh();  
	},500);
	
	
	function refresh() {
		l++;
		var data2 = nextData();
		var k;
		if (j%2==0){
			k=0;
		}else{
			k=1;
		}
		
		//var labelX2= minuteFormat(data2.timeAxxis);
		var chart = $('#statisticsGraph').highcharts();
		if (l==chart.series[0].data.length){
			//alert("S'HA DE REFRESCAR! I'm working on it");
			l=0;
			var timeData2=[];
			timeData2.push(lastTime);
			//alert(timeData2[0]);
			for (var i=1; i<60; i++){
				timeData2.push(lastTime+i);	
				//alert(timeData2[i]);
				chart.series[0].data[i].update(0);
			}
			lastTime=timeData2[59];
			//alert(timeData2[0]);
			//alert(timeData2[59]);
			labelXaxis = minuteFormat(timeData2);
			//alert(labelXaxis[0]);
			//alert(labelXaxis[59]);
			chart.xAxis[0].setCategories(labelXaxis);
			//chart.series[0].data[l].update(data2.valueAxxis[k]);
			
		}
		//chart.xAxis[0].setCategories(labelX2);
		chart.series[0].data[l].update(data2.valueAxxis[k]);
		

	}
	
	function minuteFormat(xAxis){
		for (var i = 0; i<xAxis.length; i++) {
			var date = new Date(parseInt(xAxis[i]*1000));
			// using the excellent dateFormat code from Steve Levithan
			//axisItems[i].attr("text", dateFormat(date, "h:MM:ss"));//"dd/mm, htt"
			
			if ((i==0) || (i==(xAxis.length-1))){
				xAxis[i] = (dateFormat(date, "h:MM:ss"));//"dd/mm, htt"
			}
			else{
				xAxis[i] = (dateFormat(date, "ss"));//"dd/mm, htt"
			}
		}
		
		return xAxis;
	}
	/*funció de petició (no passa)REST. Per ara es static*/

	function getData(){
		var result ={
				"idObject":"00:01:d4:ca:6d:d4:4f:6b:3",
				"parameter":"receiveBytes",
				//"timeAxxis":[1388686905,1388686906,1388686907,1388686908,1388686909,1388686910,1388686911,1388686912,1388686913,1388686914,1388686915,1388686916,1388686917,1388686918,1388686919,1388686920,1388686921,1388686922,1388686923,1388686924,1388686925,1388686926,1388686927,1388686928,1388686929,1388686930,1388686931,1388686932,1388686933,1388686934,1388686935,1388686936,1388686937,1388686938,1388686939,1388686940,1388686941,1388686942,1388686943,1388686944,1388686945,1388686946,1388686947,1388686948,1388686949,1388686950,1388686951,1388686952,1388686953,1388686954,1388686955,1388686956,1388686957,1388686958,1388686959,1388686960,1388686961,1388686962,1388686963,1388686964,1388686965],
				//"value2Axxis":[61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,462.0,60.0,0.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,60.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,60.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,60.0,0.0,0.0,0.0,0.0,61.0,1500.0],
				"timeAxxis":[1388686905,1388686906],
				"valueAxxis":[61.0,199.0]
				};
		
		return result;
	}
	/*funció de segona petició REST (ESBORRABLE) quan les peticions rest estiguin operatives*/
	function nextData(){
		var num1,num2;
		num1= Math.random()*1000;
		num2= Math.random()*2000;
		
		var result ={
			//"timeAxxis":[1388686965,1388686966],
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
	clearInterval(refreshIntervalIdS);
	clearInterval(refreshIntervalIdM);
	clearInterval(refreshIntervalIdH);

	if(selectedPort != ""){
		$('#textgraph').hide();
		$('#statisticsGraph').show();
		console.log("PORT STATS");
		console.log("Port ID: ");
		console.log(selectedPort);
		console.log("Selected port stats: "+ selectedParam+ " with "+ selectedValueType+" "+selectedTimeInterval);
		
		if(selectedTimeInterval == "hour") byMinuteGraph();
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


