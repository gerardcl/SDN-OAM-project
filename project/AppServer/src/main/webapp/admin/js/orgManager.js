//SlimScroll sizes 2
$(function(){
	$('#OM-orgColumn').slimScroll({
		height: '455px'
	});
	$('#OM-data').slimScroll({
		height: '190px'
	});
	$('#OM-users').slimScroll({
		height: '135px'
	});
	$('#OM-ap').slimScroll({
		height: '135px'
	});
	$('#OM-flows').slimScroll({
		height: '135px'
	});
	$('#OM-prgFlows').slimScroll({
		height: '135px'
	});
	$('#OM-topo').slimScroll({
		height: '230px'
	});
});

//BACKBONE
(function () {


	//MODELS
	var Organization = Backbone.Model.extend({
		defaults:{
			NIF: "",
			OAM: "",
			bankAccount: "",
			identifier: "",
			name: "",
			telephone: ""
		}
	});


	var Flow = Backbone.Model.extend({
		defaults:{
			id: ' ',
			name: ' ',
			srcOTidentifier: ' ',
			dstOTidentifier: ' ',
			srcPort: ' ',
			dstPort: ' ',
			qos: ' ',
			bandwidth: ' ',
			protocol: ' ',
			active: ' '
		}
	});

	var User = Backbone.Model.extend({
		defaults:{
			id: ' ',
			password: ' ',
			email: ' ',
			telephone: ' ',
			isAdmin: ' ',
			active: ' '
		}
	});

	var Terminal = Backbone.Model.extend({
		defaults:{
			id: ' ',
			hostName: ' ',
			ipAddress: ' ',
			mac: ' ',
			ifaceSpeed: ' ',
			description: ' ',
			active: ' '
		}
	});


//	COLLECTIONS HARDCODED
	var Organizations = Backbone.Collection.extend({
		model:Organization,
		url:'http://localhost:8080/AppServer/webapp/manager/org/all',
		parse:function (response) {
			console.log('PARSEEEEEEE');
			console.log(response.orgCollection);
//			//response.id = response.inventoryId;
//			// Parse the response and construct models
			for ( var i = 0, length = response.orgCollection.length; i < length; i++) {

				var currentValues = response.orgCollection[i];
				var devObject = {};
				devObject.NIF = currentValues.NIF;
				devObject.OAM = currentValues.OAM;
				devObject.bankAccount = currentValues.bankAccount;
				devObject.identifier = currentValues.identifier;
				devObject.name = currentValues.name;
				devObject.telephone = currentValues.telephone;
				// push the model object
				this.push(devObject);
			}
//			console.log(this.toJSON());

//			//return models
			return this.models;
//			//return response;
		}
	});

	var organizations = new Organizations();

	var OrganizationView = Backbone.View.extend({
		model: new Organization(),
		tagName:"div",
		className:"organizations-container",

		initialize: function(){
			this.template = _.template($('#organizations-template').html()); // using
			// underscore
			// template
		},

		render:function () {
			this.$el.html(this.template(this.model.toJSON()));
//			var tmpl = _.template(this.template); //tmpl is a function that takes a JSON object and returns html
//			this.$el.html(tmpl(this.model.toJSON())); //this.el is what we defined in tagName. use $el to get access to jQuery html() function
			return this;
		}
	});




	var OrganizationsView = Backbone.View.extend({
		model: organizations,
		el:$("#organizations"),

//		initialize:function(){
//		this.collection = new Organizations(organizations);
//		this.render();
//		//console.log(this.collection.toJSON());
//		},

		initialize: function(){
			var self = this;
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
			console.log('ENTRA AL RENDER (OrganizationsView)');
			console.log(this.organizations);
			var that = this;
			_.each(this.collection.models, function(item){
				that.renderOrganization(item);
			}, this);
		},
//		render: function(){
//		var self = this;
//		self.$el.html('');
//		_.each(this.model.toArray(),function(organization,i){
//		self.$el.append((new OrganizationView({model: organization})).render().$el);
//		});
//		return this;
//		},

		renderOrganization:function(item){
			var organizationView = new OrganizationView({
				model: item
			});
			this.$el.append(organizationView.render().el);
		}
	});



	var OrgDataView = Backbone.View.extend({
		tagName:"div",
		className:"orgData-container",
		template:_.template($("#orgData-template").html()),



		render:function () {
			$(this.el).html(this.template(this.model.toJSON()));
			return this;
		}
	});

	var OrgNameView = Backbone.View.extend({
		tagName:"h3",
		className:"organization-name-container",
		template:_.template($("#organization-name-template").html()),

		render:function () {
			var tmpl = _.template(this.template); //tmpl is a function that takes a JSON object and returns html
			this.$el.html(tmpl(this.model.toJSON())); //this.el is what we defined in tagName. use $el to get access to jQuery html() function
			return this;
		}
	});

//	Router
	var AppRouter = Backbone.Router.extend({

		routes:{
			"":"list",
			"orgData/:identifier":"orgData"
		},

		list:function () {
			this.collection = new Organizations(organizations);
			this.organizationsView = new OrganizationsView({model:this.collection});
			this.collection.fetch();
			$('#organizations-container').html(this.organizationsView.render().el);
		},

		orgData:function (identifier) {
			this.organization = this.collection.get(identifier);
			this.organizationView = new OrgDataView({model:this.organization});
			$('#orgData-container').html(this.organizationView.render().el);

			this.organization = this.collection.get(name);
			this.orgNameView = new OrgNameView({model:this.organization});
			$('#organization-name-container').html(this.orgNameView.render().el);
		}
	});

	var appView = new OrganizationsView();
	
	var app = new AppRouter();
	Backbone.history.start();


})(jQuery);