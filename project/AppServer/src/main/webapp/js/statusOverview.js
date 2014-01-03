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
	var width = 600, height = 300;

	//var color = d3.scale.category20();

	var force = d3.layout.force().charge(-1120).linkDistance(70).size(
			[ width, height ]);

	var svg = d3.select("#topo").append("svg").attr("width", width).attr(
			"height", height);
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
			portList += "<li class='list-group-item' id='port' onclick='showPortStats();'>"+ d.ports[i].portId+"</li>";
		}
		var packetCount = "45";
		var byteCount = "1.2k";
		var flowCount = "2";
		$("#switchInfo").html(switchInfo);
		$("#packetCount").html(packetCount);
		$("#byteCount").html(byteCount);
		$("#flowCount").html(flowCount);
		$("#portList").html(portList);
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

function loadDefaultStatValues(){
	selectedPort = "";
	selectedParam = "receiveBytes"; //default value
	selectedValueType = "AVERAGE";  //devault value
	selectedTimeInterval = "hour";  //devault value
	$("#bparam").html('Received <span class="caret"></span>');
	$("#bvaluetype").html('AVERAGE <span class="caret"></span>');
	$("#btinterval").html('Last hour <span class="caret"></span>');
}

function printPortGraph(){
	if(selectedPort != ""){
		console.log("PORT STATS");
		console.log("Port ID: ");
		console.log(selectedPort);
		console.log("Selected port stats: "+ selectedParam+ " with "+ selectedValueType+" "+selectedTimeInterval);
		//HERE THE CODE BY ALEX



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


