$(document).ready(function(){
		//$("#devices").html(devicesView.render().el);
		//$("#devices").html(deviceView.render().el);
		renderDevice();
		$("#msg").hide();//.append("for each here, per mac and so on!<p></p>");
		
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