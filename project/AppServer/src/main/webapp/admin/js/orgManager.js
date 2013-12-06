(function($){
	var Device = Backbone.Model.extend({
		defaults: function(){
			return {
				id: ' ',
				ip: ' ',
				name: ' ',
				description: ' '
			}
		}
	});
	
	var DevicesList = Backbone.Collection.extend({
		model: Device
	});
	var numDevices = 0;
	var devices = new DevicesList();

	var DeviceView = Backbone.View.extend({
		model: new Device(),
		tagName: 'div',
		events: {
			'click .name': 'updateName',
			'click .ip': 'updateIp',
			'click .description': 'updateDesc',
			'click .delete': 'delete',
			'blur .name': 'close',
			'blur .ip': 'close',
			'blur .description': 'close',
			'keypress .name': 'onEnterUpdate',
			'keypress .ip': 'onEnterUpdate',
			'keypress .description': 'onEnterUpdate'
		},
		initialize: function(){
			this.template = _.template($('#device-template').html()); //using underscore template
		},
		updateName: function(ev){
			ev.preventDefault();
			this.$('.name').attr('contenteditable',true).focus();
		},
		updateIp: function(ev){
			ev.preventDefault();
			this.$('.ip').attr('contenteditable',true).focus();
		},
		updateDesc: function(ev){
			ev.preventDefault();
			this.$('.description').attr('contenteditable',true).focus();
		},
		close: function(ev){
			var name = this.$('.name').text();
			var ip = this.$('.ip').text();
			var description = this.$('.description').text();
			this.model.set('name', name);
			
			var self=this;

			console.log("ip: "+ip);


			this.model.set('ip', ip);
/*
			//Testing ip update pattern
			var ipformat = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/; 		
			
			if(ip.match(ipformat)){
				this.model.set('ip', ip);  
				}
			
			else{
				var ip2=prompt("Please enter a valid IP","192.168.1.1");
				console.log("ip2 promt: "+ip2);
				if(ip2.match(ipformat)){
					self.model.set('ip', ip2);
					ip2=null;
				}
			}

*/
			this.model.set('description', description);
			this.$('.name').removeAttr('contenteditable');
			this.$('.ip').removeAttr('contenteditable');
			this.$('.description').removeAttr('contenteditable');
		},
		onEnterUpdate: function(ev){
			var self = this;
			if(ev.keyCode==13){
				this.close();
				_.delay(function(){ self.$('.name').blur()},100);
				_.delay(function(){ self.$('.ip').blur()},100);
				_.delay(function(){ self.$('.description').blur()},100);
			}
		},
		delete: function(ev){
			ev.preventDefault();
			devices.remove(this.model);
		},
		render: function(){
			this.$el.html(this.template(this.model.toJSON()));
			return this;
		}
	});
	
	var DevicesView = Backbone.View.extend({
		model: devices,
		el: $('#devices-container'),
		initialize: function(){
			this.model.on('add', this.render, this);
			this.model.on('remove', this.render,this);
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
	
	$(document).ready(function(){
		$('#add-device').submit(function(ev){
			var device = new Device({id:++numDevices,name:$('#device-name').val(),ip:$('#device-ip').val(),description:$('#device-description').val()});
			devices.add(device);
			//var device2 = new Device({id:'RT-2',name:'Router 2',ip:'192.168.1.12',description:'second router'});
			//var device3 = new Device({id:'RT-3',name:'Router 3',ip:'192.168.1.13',description:'third router'});
			//console.log(device.get('description'));
			console.log(devices.toJSON());


			return false;	
		});
		
		var appView = new DevicesView();
	});
	
	
})(jQuery);