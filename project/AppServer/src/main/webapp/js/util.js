(function($){
	$(document).ready(function(){
		$("#msg").show();//.append("for each here, per mac and so on!<p></p>");
		$("#admin").hide();
		$("#client").hide();

		//var appView = new DevicesView();
	});

	var Session = Backbone.Model.extend({
		defaults: 
		{
			user: ' ',
			password: ' ',
			session: ' ',
			msg: ' '
		}
	});

	var SessionList = Backbone.Collection.extend({
		model: Session
	});
	var numDevices = 0;
	var devices = new SessionList();

	var DeviceView = Backbone.View.extend({
		model: new Session(),
		tagName: 'div',
		events: {
			'click .msg': 'updateName',
			'click .ip': 'updateIp',
			'click .description': 'updateDesc',
//			'click .delete': 'delete',
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
			this.$('.msg').html("helloworld");
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
			this.model.set('ip', ip);
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
//		delete: function(ev){
//			ev.preventDefault();
//			devices.remove(this.model);
//		},
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



})(jQuery);



