(function ($) {

	var Device = Backbone.Model.extend({
		defaults:{
			id:"its unique id",
			name:"device name",
			status:"actual state",
			portsConnected:"number of active ports",
			totalPorts:"total number of ports",
			mac1:"mac1",
			mac2:"mac2",
			mac3:"mac3",
			mac4:"mac4"
		}
	});

	var DevicesList = Backbone.Collection.extend({
		model: Device,
		//url:'http://localhost:8080/device-manager-api/webapi/devices/routers',
		url:'http://ec2-54-229-220-96.eu-west-1.compute.amazonaws.com:7474/db/data/node/1',
		parse:function (response) {
			console.log(response);
			//response.id = response.inventoryId;
			// Parse the response and construct models
//			for ( var i = 0, length = response.routers.length; i < length; i++) {
//				var currentValue = response.routers[i];
//				var devObject = {};
//				console.log(currentValue);
//				devObject.interfaces = currentValue.interfaces;
//				devObject.routingTable = currentValue.routingTable;
//				devObject.ports = currentValue.ports;
//				devObject.inventoryId = currentValue.inventoryId;
//				// push the model object
//				this.push(devObject);
//			}
//			console.log(this.toJSON());

			//return models
			return this.models;
			//return response;
		}
	});
	
	var devices = new DevicesList();
	
	var DeviceView = Backbone.View.extend({
		model: new Device(),
		tagName:"div",
		className:"device-container",
		template:$("#device-template").html(),

		render:function () {
			var tmpl = _.template(this.template); //tmpl is a function that takes a JSON object and returns html

			this.$el.html(tmpl(this.model.toJSON())); //this.el is what we defined in tagName. use $el to get access to jQuery html() function
			return this;
		}
	});


//	var device = new Device({
//	id:"1",
//	name:"Device1",
//	status:"UP",
//	portsConnected:"3",
//	totalPorts:"4",
//	mac1:"11:11:11:11:11:11",
//	mac2:"12:12:12:12:12:12",
//	mac3:"13:13:13:13:13:13",
//	mac4:"14:14:14:14:14:14"
//	});


//	deviceView = new DeviceView({
//		model: Device
//	});

	//Global View if needed, now not used
	//Maybe to be used for nodes association on graph topology
	var DevicesView = Backbone.View.extend({
		model: devices,
		el: $('#devices-container'),
		initialize: function(){
			var self = this;
			this.model.on('add', this.render, this);
			this.model.on('remove', this.render,this);
			// get all devices (Backbone.sync powah!!!)
			this.model.fetch({
				success: function(response,xhr) {
					console.log("Success fetchingg");
					self.render();
				},
				error:function () {
					console.log(arguments);
				}	
			});

		},	

		render: function(){
			var self = this;
			self.$el.html('');
			_.each(this.model.toArray(),function(device,i){
				self.$el.append((new DeviceView({model: device})).render().$el);
			});
			return this;
		}

	});
	var deviceView = new DeviceView();
	var devicesView = new DevicesView();

	$(document).ready(function(){
		//$("#devices").html(devicesView.render().el);
		$("#devices").html(deviceView.render().el);
		$("#devices").append("for each here, per mac and so on!<p></p>");
		
		$('#add-device').submit(function(ev){
			//var the_id= "RT-"+ ++numDevices;
			//var device = new Device({id:the_id,name:$('#device-name').val(),ip:$('#device-ip').val(),description:$('#device-description').val()});
			var device = new Device();//{inventoryId:$('#device-inventoryId').val(),ports:$('#device-ports').val(),interfaces:$('#device-interfaces').val(),routingTable:$('#device-routingTable').val()});
			devices.add(device);
			console.log(devices.toJSON());
//			device.save({id:device.get('id'),name:$('#device-name').val(),ip:$('#device-ip').val(),description:$('#device-description').val()},{
//			succes: function(){ consol.log("successfully saved device!");},
//			error: function(){ console.log("error saving device!");}
//			})
			return false;	
		});

	});
	//put the name of the Device selected on the button
	$(function(){  
	  $(".dropdown-menu li a").click(function(){
	    $(".btn.dropdown-toggle:first-child").text($(this).text());
	    $(".btn.dropdown-toggle:first-child:first-child").val($(this).text()+ ' <span class="caret"></span>');
	  });
	});
})(jQuery);