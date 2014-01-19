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
var switchesURL = "/AppServer/webapi/topology/switches";
var terminalsURL = "/AppServer/webapi/topology/terminals";
var linksURL = "/AppServer/webapi/topology/links";
var dataSwitches;
var dataTerminals;
var dataLinks;
var nodes = [];
var links = [];
var refreshIntervalTWM;

function stopTopoWeatherMapRefresh(){
	clearInterval(refreshIntervalTWM);
}

function getTopoWeatherMap(){
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
	var terminals =[];// abans era... dataTerminals.terminals;
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
		//alert(i);
		//Dummy request to..
		var reqUriRx ="/AppServer/webapi/statistics/port/"+dataLinks.links[i].dstPortId+"/receiveBytes/AVERAGE/minute";
		//alert (reqUriRx);
		//Obtained DUMMYajax response
		var response1;// = {"idObject":"00:01:d4:ca:6d:c4:44:1e:3","parameter":"receiveBytes","timeAxxis":[1389907545,1389907546,1389907547,1389907548,1389907549,1389907550,1389907551,1389907552,1389907553,1389907554,1389907555,1389907556,1389907557,1389907558,1389907559,1389907560,1389907561,1389907562,1389907563,1389907564,1389907565,1389907566,1389907567,1389907568,1389907569,1389907570,1389907571,1389907572,1389907573,1389907574,1389907575,1389907576,1389907577,1389907578,1389907579,1389907580,1389907581,1389907582,1389907583,1389907584,1389907585,1389907586,1389907587,1389907588,1389907589,1389907590,1389907591,1389907592,1389907593,1389907594,1389907595,1389907596,1389907597,1389907598,1389907599,1389907600,1389907601,1389907602,1389907603,1389907604],"valueAxxis":[0.0,0.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,695.0,0.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]};
		$.ajax({
			type: "GET",
			url: reqUriRx,
			//contentType: 'application/json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//			headers: {
//			Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//			},
			success : function(result) {
				response1 = result;
			}
		});
		
		//Dummy request to...
		var reqUriTx ="/AppServer/webapi/statistics/port/"+dataLinks.links[i].srcPortId+"/transmitBytes/AVERAGE/minute";
		//alert (reqUriTx);
		//Obtained DUMMYajax response
		var response2;//={"idObject":"00:01:d4:ca:6d:c4:44:1e:3","parameter":"transmitBytes","timeAxxis":[1389908038,1389908039,1389908040,1389908041,1389908042,1389908043,1389908044,1389908045,1389908046,1389908047,1389908048,1389908049,1389908050,1389908051,1389908052,1389908053,1389908054,1389908055,1389908056,1389908057,1389908058,1389908059,1389908060,1389908061,1389908062,1389908063,1389908064,1389908065,1389908066,1389908067,1389908068,1389908069,1389908070,1389908071,1389908072,1389908073,1389908074,1389908075,1389908076,1389908077,1389908078,1389908079,1389908080,1389908081,1389908082,1389908083,1389908084,1389908085,1389908086,1389908087,1389908088,1389908089,1389908090,1389908091,1389908092,1389908093,1389908094,1389908095,1389908096,1389908097],"valueAxxis":[12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0]};
		$.ajax({
			type: "GET",
			url: reqUriTx,
			//contentType: 'application/json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//			headers: {
//			Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//			},
			success : function(result) {
				response2 = result;
			}
		});
		//Transmitted bytes
		//Recieved bytes
		var sumRx=0;
		var sumTx=0;

		for (var j=0; j<response1.valueAxxis.length;j++){
			sumRx+=response1.valueAxxis[j];
			sumTx+=response2.valueAxxis[j];
		}

		sumRx = ((sumRx/60)*8/1000000).toFixed(2);
		sumTx = ((sumTx/60)*8/1000000).toFixed(2);
		//alert("REC: "+sumRx+" TRANSM: "+sumTx);
		var colorRx = getGreenToRed(sumRx);
		var colorTx = getGreenToRed(sumTx);
		dataLinks.links[i].colorRx=colorRx;
		dataLinks.links[i].colorTx=colorTx;

		//alert("RECIBIDO AQUI: "+dataLinks.links[i].colorRx+" TRANSM AQUI: "+dataLinks.links[i].colorTx);

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

				//alert(src+" "+trg);


				links.push({"source":src,"target":trg,"value":8,"type":"suit","color":dataLinks.links[i].colorTx});
				links.push({"source":trg,"target":src,"value":8,"type":"suit","color":dataLinks.links[i].colorRx});
				src= null;
				trg=null;
			}
		}	
	}


	for (var i=0; i<dataTerminals.terminals.length; i++){
		//alert(i);
		//Dummy request to..
		var reqUriRx ="/AppServer/webapi/statistics/port/"+dataTerminals.terminals[i].portAPId+"/receiveBytes/AVERAGE/minute";
		//alert (reqUriRx);
		//Obtained DUMMYajax response
		var response1;// = {"idObject":"00:01:d4:ca:6d:c4:44:1e:3","parameter":"receiveBytes","timeAxxis":[1389907545,1389907546,1389907547,1389907548,1389907549,1389907550,1389907551,1389907552,1389907553,1389907554,1389907555,1389907556,1389907557,1389907558,1389907559,1389907560,1389907561,1389907562,1389907563,1389907564,1389907565,1389907566,1389907567,1389907568,1389907569,1389907570,1389907571,1389907572,1389907573,1389907574,1389907575,1389907576,1389907577,1389907578,1389907579,1389907580,1389907581,1389907582,1389907583,1389907584,1389907585,1389907586,1389907587,1389907588,1389907589,1389907590,1389907591,1389907592,1389907593,1389907594,1389907595,1389907596,1389907597,1389907598,1389907599,1389907600,1389907601,1389907602,1389907603,1389907604],"valueAxxis":[0.0,0.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,695.0,0.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,61.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0]};
		$.ajax({
			type: "GET",
			url: reqUriRx,
			//contentType: 'application/json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//			headers: {
//			Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//			},
			success : function(result) {
				response1 = result;
			}
		});	
		//Dummy request to...
		var reqUriTx ="/AppServer/webapi/statistics/port/"+dataTerminals.terminals[i].portAPId+"/transmitBytes/AVERAGE/minute";
		//alert (reqUriTx);
		//Obtained DUMMYajax response
		var response2;//={"idObject":"00:01:d4:ca:6d:c4:44:1e:3","parameter":"transmitBytes","timeAxxis":[1389908038,1389908039,1389908040,1389908041,1389908042,1389908043,1389908044,1389908045,1389908046,1389908047,1389908048,1389908049,1389908050,1389908051,1389908052,1389908053,1389908054,1389908055,1389908056,1389908057,1389908058,1389908059,1389908060,1389908061,1389908062,1389908063,1389908064,1389908065,1389908066,1389908067,1389908068,1389908069,1389908070,1389908071,1389908072,1389908073,1389908074,1389908075,1389908076,1389908077,1389908078,1389908079,1389908080,1389908081,1389908082,1389908083,1389908084,1389908085,1389908086,1389908087,1389908088,1389908089,1389908090,1389908091,1389908092,1389908093,1389908094,1389908095,1389908096,1389908097],"valueAxxis":[12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0,12500000.0]};
		$.ajax({
			type: "GET",
			url: reqUriTx,
			//contentType: 'application/json',
			//datatype: "application/vmd.dxat.appserver.topology.switches.collection+json",
//			headers: {
//			Accept : "application/vmd.dxat.appserver.topology.switches.collection+json"
//			},
			success : function(result) {
				response2 = result;
			}
		});	//Transmitted bytes
		//Recieved bytes
		var sumRx=0;
		var sumTx=0;

		for (var j=0; j<response1.valueAxxis.length;j++){
			sumRx+=response1.valueAxxis[j];
			sumTx+=response2.valueAxxis[j];
		}

		sumRx = ((sumRx/60)*8/1000000).toFixed(2);
		sumTx = ((sumTx/60)*8/1000000).toFixed(2);
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
		g = percent<50 ? 255 : Math.floor(255-(percent*2-100)*255/100);
		r = percent>50 ? 255 : Math.floor((percent*2)*255/100);
		return 'rgb('+r+','+g+',0)';
	}


}//END GET TOPO WEATHER MAP


function createTopologyGraph(){

	//Represenació grafic 
	var width = 400,
	height = 300

	var force = d3.layout.force()
	.nodes(nodes)
	//.gravity(.05)
	.links(links)
	.size([width, height])
	.linkDistance(110)
	.charge(-1000)
	.on("tick", tick)
	.start();

	/*var force = d3.layout.force()
    .gravity(.05)
    .distance(100)
    .charge(-1000)
    .linkDistance(80)
    .size([width, height]);

  force
      .nodes(nodes)
      .links(links)
      .start();*/

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
	.style("stroke",  function(d) {return d.color;/*	var color="#000";
    								switch (d.color){
    									case 2:
    										return d.color;
    									case 10:
    										return color = "#F00";
        								default:
            								var color="#00F";
        								return color;
    									}*/}); //Amb aixó canviarem el color!


	/* var link = svg.selectAll(".link")
     .data(links)
     .enter().append("line")
     .attr("class", "link")
     //.style("stroke", "#F00") //Amb aixo es canvia el color del link
     .style("stroke-width", function(d) { return Math.sqrt(d.value); });*/

	var node = 	svg.selectAll(".node")
	.data(nodes)
	.enter().append("g")
	.attr("class", "node")
	.on("tick",  function (d){return path.attr("d", linkArc)})
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
		return "/img/switch.svg"
	}else{
		return "/img/terminal.svg"
	}
	})
	.attr("x", function (d)	{	if (d.portAPId ==null){
		return -50
	}else{
		return -20
	}
	})
	.attr("y", function (d)	{	if (d.portAPId ==null){
		return -50
	}else{
		return -20
	}
	})
	.attr("width", function (d)	{	if (d.portAPId ==null){
		return 100
	}else{
		return 50
	}
	})
	.attr("height", function (d)	{	if (d.portAPId ==null){
		return 100
	}else{
		return 50
	}
	});

	/*force.on("tick", function() {
		link.attr("x1", function(d) { return d.source.x; })
		    .attr("y1", function(d) { return d.source.y; })
		    .attr("x2", function(d) { return d.target.x; })
		    .attr("y2", function(d) { return d.target.y; });
	    node.attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });	
		});*/
	function tick() {
		path.attr("d", linkArc);
		node.attr("transform", transform);
		// text.attr("transform", transform);
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



	//TODO SET INTERVAL WEATHER MAP HERE
	
	refreshIntervalTWM = setInterval(function() {
		getTopoWeatherMap();
	},60000);

	node.on("click", function(d) {
		StopSwitchStats();

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
	function getflowCountData(){
		var datastats = {};
		var datastatsuri = "/AppServer/webapi/statistics/switch/"+idSwitch+"/flowCount/MAX/second";
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

	var lastTime = timeData[(timeData.length-1)*1000];

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
	lastTime=data.timeAxxis[0];
	refreshIntervalIdPort = setInterval(function() {
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
		var datastatsuri = "/AppServer/webapi/statistics/port/"+selectedPort+"/"+selectedParam+"/"+selectedValueType+"/second";
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
		$('#textgraph').hide();
		$('#statisticsGraph').show();
		console.log("PORT STATS");
		console.log("Port ID: ");
		console.log(selectedPort);
		console.log("Selected port stats: "+ selectedParam+ " with "+ selectedValueType+" "+selectedTimeInterval);
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
		console.log("updating gauge:");
		console.log(key);
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
	return datastats.valueAxxis[0];;
}
////TODO CHANGE THE RANDOM VALUE TO AJAX REQUEST!!!
//function getRandomValue(gauge)
//{
//var overflow = 0; //10;
//return gauge.config.min - overflow + (gauge.config.max - gauge.config.min + overflow*2) *  Math.random();
//}

function initializeControllerStats()
{
	createGauges();
	refreshIntervalController = setInterval(updateGauges, 3000);
}