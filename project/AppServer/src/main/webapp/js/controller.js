//Backbone aaaa
(function($){

	var loginUser = '';
	var loginOrg = '';

	$.ajaxPrefilter( function( options, originalOptions, jqXHR ) {
		options.url = '/AppServer/webapp' + options.url;
	});


//	Models
	//TOrg data model  
	var Organization = Backbone.Model.extend({
		urlRoot:'/manager/org',
		defaults:{
			name: "",
			NIF: "",
			bankAccount: "",
			identifier: "",
			telephone: "",
			OAM: ""
		}
	});

	//flow model  
	var Flow = Backbone.Model.extend({
		//urlRoot:'/manager/flow/all?orgId=',
		defaults:{
			identifier: "",
			active: "",
			bandwidth: "",
			dstOTidentifier: "",
			dstPort: "",
			name: "",
			protocol: "",
			qos: "",
			srcOTidentifier: "",
			srcPort: ""
		}
	});

	//terminal model  
	var Terminal = Backbone.Model.extend({
		//urlRoot:'/manager/terminal/all?orgId=',
		defaults:{
			identifier: "",
			active: "",
			description: "",
			hostName: "",
			ifaceSpeed: "",
			ipAddress: "",
			mac: ""
		}
	});

	//user model 
	var User = Backbone.Model.extend({
		//urlRoot:'/user/all?orgId=',
		urlRoot:'/manager/user',
		defaults:{
			identifier: "",
			name: "",
			active: "",
			admin: "",
			email: "",
			password: "",
			telephone: ""
		}
	});

//	COLLESCTIONS
	//TOrg COLLECTION
	var Organizations = Backbone.Collection.extend({
		model: Organization,
		url:'/manager/org/all',
		parse:function (response) {
			// Parse the response and construct models
			for ( var i = 0, length = response.torgs.length; i < length; i++) {
				var currentValues = response.torgs[i];
				var orgObject = {};
				orgObject.name = currentValues.name;
				orgObject.NIF = currentValues.NIF;
				orgObject.bankAccount = currentValues.bankAccount;
				orgObject.identifier = currentValues.identifier;
				orgObject.telephone = currentValues.telephone;
				orgObject.OAM = currentValues.OAM;
				// push the model object
				this.push(orgObject);
			}
			console.log(this.toJSON());
			//return models
			return this.models;
			//return response;
		}
	});

	var organizations = new Organizations();

	//Flows COLLECTION
	var Flows = Backbone.Collection.extend({
		model: Flow,
		parse:function (response) {
			for ( var i = 0, length = response.orgFlows.length; i < length; i++) {
				var currentValues = response.orgFlows[i];
				var flowObject = {};
				flowObject.identifier = currentValues.identifier;
				flowObject.active = currentValues.active;
				flowObject.bandwidth = currentValues.bandwidth;
				flowObject.dstOTidentifier = currentValues.dstOTidentifier;
				flowObject.dstPort = currentValues.dstPort;
				flowObject.name = currentValues.name;
				flowObject.protocol = currentValues.protocol;
				flowObject.qos = currentValues.qos;
				flowObject.srcOTidentifier = currentValues.srcOTidentifier;
				flowObject.srcPort = currentValues.srcPort;
				this.push(flowObject);
			}
			console.log(this.toJSON());
			return this.models;
		}
	});

	var flows = new Flows();

	//Terminals COLLECTION
	var Terminals = Backbone.Collection.extend({
		model: Terminal,
		//url:'/manager/terminal/all',
		parse:function (response) {
			for ( var i = 0, length = response.orgTerminals.length; i < length; i++) {
				var currentValues = response.orgTerminals[i];
				var terminalObject = {};
				terminalObject.identifier = currentValues.identifier;
				terminalObject.active = currentValues.active;
				terminalObject.hostName = currentValues.hostName;
				terminalObject.ifaceSpeed = currentValues.ifaceSpeed;
				terminalObject.ipAddress = currentValues.ipAddress;
				terminalObject.mac = currentValues.mac;
				this.push(terminalObject);
			}
			console.log(this.toJSON());
			return this.models;
		}
	});

	var terminals = new Terminals();

	//Users COLLECTION
	var Users = Backbone.Collection.extend({
		model: User,
		/*url: function(orgId){
			if(orgId!=null){
				var aux = orgId.orgId;
				console.log('Entra a la funcio URL i treu com a uri:');
				var uri = '/user/all?orgId=' + aux;
				console.log(uri);
				return uri;
			}else{
				console.log('Entra lelse');
				return '/user/all';

			}

			var aux = JSON.stringify(orgId)
			console.log(aux);
			var uri = '/manager/user/all?orgId=' + aux;
			console.log(uri);
			return uri;
		},*/
		parse:function (response) {
			for ( var i = 0, length = response.orgUsers.length; i < length; i++) {
				var currentValues = response.orgUsers[i];
				var userObject = {};
				userObject.identifier = currentValues.identifier;
				userObject.name = currentValues.name;
				userObject.active = currentValues.active;
				userObject.admin = currentValues.admin;
				userObject.email = currentValues.email;
				userObject.password = currentValues.password;
				userObject.telephone = currentValues.telephone;
				this.push(userObject);
			}
			console.log(this.toJSON());
			return this.models;
		}
	});

	var users = new Users();

//	VIEWS

	//LOGIN VIEW #login-template
	var LoginView = Backbone.View.extend({
		el: '.sidebar-container',
		render: function () {
			var that = this;
			var template = _.template($('#login-template').html());
			that.$el.html(template);
		}
	});

	var loginView = new LoginView();
	// /LOGIN VIEW #login-template

	//ADMIN SIDEBAR #admin-sidebar-template
	var AdminSidebarView = Backbone.View.extend({
		el: '.sidebar-container',
		updateClass: function() {
			this.$el.toggleClass('active');
		},
		render: function (options) {
			var that = this;
			var template = _.template($('#admin-sidebar-template').html(), {btnHL: options.btnHL});
			that.$el.html(template);
		}
	});

	var adminSidebarView = new AdminSidebarView();
	// /ADMIN SIDEBAR #admin-sidebar-template

	//CLIENT SIDEBAR #client-sidebar-template
	var ClientSidebarView = Backbone.View.extend({
		el: '.sidebar-container',
		updateClass: function() {
			this.$el.toggleClass('active');
		},
		render: function (options) {
			var that = this;
			var template = _.template($('#client-sidebar-template').html(), {btnHL: options.btnHL});
			that.$el.html(template);
		}
	});

	var clientSidebarView = new ClientSidebarView();
	// /CLIENT SIDEBAR #client-sidebar-template

	//GLOBAL VIEW #admin-overview-template
	var AdminOverviewView = Backbone.View.extend({
		el: '.page',
		render: function () {
			var that = this;
			var template = _.template($('#admin-overview-template').html());
			that.$el.html(template);
			//SlimScroll HEIGHTS
			$('#GS-alerts').slimScroll({
				height: '100px'
			});
			$('#GS-topo').slimScroll({
				height: '200px'
			});
			$('#GS-controller').slimScroll({
				height: '200px'
			});
			$('#GS-stats').slimScroll({
				height: '200px'
			});
		}
	});

	var adminOverviewView = new AdminOverviewView();
	// /GLOBAL VIEW #admin-overview-template

	//ORGANIZATIONS

	//Orgs List Bootstrap View
	var OrgsListBSView = Backbone.View.extend({
		el: '.page',
		render: function () {
			var that = this;
			var organizations = new Organizations();
			organizations.fetch({
				success: function (organizations) {
					var template = _.template($('#organizations-list-template').html(), {organizations: organizations.models});
					that.$el.html(template);
					$('#OM-orgColumn').slimScroll({
						height: '455px'
					});
				}
			})
		}
	});

	var orgsListBSView = new OrgsListBSView();
	// /Orgs List Bootstrap View

	//OrgData View
	var OrgDataView = Backbone.View.extend({
		el: '.page',
		render: function (options) { 
			var that = this;
			//if exists fetch details
			if(options.identifier) {
				that.organization = new Organization({id: options.identifier});
				that.organization.fetch({
					success: function (organization) {  
						var activeOrgName = organization.get('name');
						console.log(activeOrgName);
						var template = _.template($('#organizations-data-template').html(), {organization: organization});
						that.$el.html(template); 
						//SlimScroll
						$('#OM-data').slimScroll({
							height: '190px'
						});
					}
				})
			} else {
				var template = _.template($('#organizations-data-template').html(), {organization: null});
				that.$el.html(template);
				//SlimScroll
				$('#OM-data').slimScroll({
					height: '190px'
				});       
			}
		}
	});

	var orgDataView = new OrgDataView();


	//OrgUsers View
	var OrgUsersView = Backbone.View.extend({
		el: '.page',
		render: function (options) { 
			var that = this;
			//if exists fetch details
			if(options.identifier) {
				that.users = new Users();
				that.users.url = '/manager/user/'+options.identifier+'/all';
				that.users.fetch({
					success: function (users) {  
						var template = _.template($('#organizations-users-template').html(), {users: users.models, orgId: options.identifier});
						that.$el.html(template); 
						//SlimScroll
							$('#OM-users').slimScroll({
								height: '135px'
							});
					}
				});
			} else {
				var template = _.template($('#organizations-users-template').html(), {users: null});
				that.$el.html(template);
				//SlimScroll
			}
		}
	});

	var orgUsersView = new OrgUsersView();


	//org TERMINAL
	var OrgTerminalsView = Backbone.View.extend({
		el: '.page',
		render: function (options) { 
			var that = this;
			//if exists fetch details
			if(options.identifier) {
				that.terminals = new Terminals();
				that.terminals.url = '/manager/terminal/'+options.identifier+'/all';
				that.terminals.fetch({
					success: function (terminals) {  
						var template = _.template($('#organizations-terminals-template').html(), {terminals: terminals.models, orgId: options.identifier});
						that.$el.html(template); 
						//SlimScroll
							$('#OM-ap').slimScroll({
								height: '135px'
							});
					}
				});
			} else {
				var template = _.template($('#organizations-terminals-template').html(), {terminals: null});
				that.$el.html(template);
				//SlimScroll
			}
		}
	});

	var orgTerminalsView = new OrgTerminalsView();

	//Org FLOWS
	var OrgFlowsView = Backbone.View.extend({
		el: '.page',
		render: function (options) { 
			var that = this;
			//if exists fetch details
			if(options.identifier) {
				that.activeFlows = new Flows();
				that.activeFlows.url = '/manager/flow/'+options.identifier+'/all';
				that.activeFlows.fetch({
					success: function (flows) {  
						var template = _.template($('#organizations-flows-template').html(), {flows: flows.models, orgId: options.identifier, active: options.active});
						that.$el.html(template); 
						//SlimScroll
							$('#FLW-prg').slimScroll({
								height: '135px'
							});
					}
				});
			} else {
				var template = _.template($('#organizations-flows-template').html(), {flows: null});
				that.$el.html(template);
				//SlimScroll
			}
		}
	});

	var orgFlowsView = new OrgFlowsView();

	// /ORGANIZATIONS

	//FLOWS VIEW #flows-template
	var FlowsView = Backbone.View.extend({
		el: '.page',
		render: function () {
			var that = this;
			var flows = new Flows();
			flows.url = '/manager/flow/all';
			flows.fetch({
				success: function (flows) {
					var template = _.template($('#flows-template').html(), {flows: flows.models});
					that.$el.html(template);
					//SlimScroll
					$('#FLW-active').slimScroll({
						height: '190px'
					});
					$('#FLW-prg').slimScroll({
						height: '190px'
					});
				}
			})
		}
	});

	var flowsView = new FlowsView();
	// /FLOWS VIEW #flows-template

	//TERMINALS VIEW #terminals-template
	var TerminalsView = Backbone.View.extend({
		el: '.page',
		render: function () {
			var that = this;
			var terminals = new Terminals();
			terminals.url = '/manager/terminal/all',
			terminals.fetch({
				success: function (terminals) {
					var template = _.template($('#terminals-template').html(), {terminals: terminals.models});
					that.$el.html(template);
					$('#AP').slimScroll({
						height: '500px'
					});
				}
			})
		}
	});

	var terminalsView = new TerminalsView();
	// /TERMINALS VIEW #terminals-template

	//TRAFFIC VIEW #traffic-template
	var TrafficView = Backbone.View.extend({
		el: '.page',
		render: function () {
			var that = this;
			var template = _.template($('#traffic-template').html());
			that.$el.html(template);
			//SlimScroll HEIGHTS
			$('#TA-matrix').slimScroll({
				height: '520px'
			});
		}
	});

	var trafficView = new TrafficView();
	// /TRAFFIC VIEW #traffic-template

//	ROUTES 
	var Router = Backbone.Router.extend({
		routes: {
			"": "login", 
			//ADMIN ROUTES
			"adminOverview" : "adminOverview",
			"adminOrgs": "adminOrgs",
			"adminOrgs/:identifier": "orgData",
			"adminUsers/:identifier": "orgUsers",
			"adminFlows/:identifier": "orgFlows", //active flows of specipic org
			"adminPrgFlows/:identifier": "orgPrgFlows",	//programmed flows of specific org	
			"adminTerminals/:identifier": "orgTerminals", //terminals of specific org
			"adminFlows": "flows", //all the flows	
			"adminTerminals": "terminals",//all the terminals
			"adminTraffic": "traffic",
			//CLIENT ROUTES
			"clientOverview" : "clientOverview",
			"clientOrgData": "clientOrgData",
			"clientOrgUsers": "clientOrgUsers",
			"clientFlows": "clientFlows",
			"clientTerminals": "clientTerminals",
			"clientTraffic": "clientTraffic"
		}
	});

	var router = new Router;

	//EVENTS FROM ROUTES

	//ADMIN
		router.on('route:login', function() {
			loginView.render();
		})

		router.on('route:adminOverview', function() {
			adminSidebarView.render({btnHL: 1});
			adminOverviewView.render();
		})

		router.on('route:adminOrgs', function() {
			adminSidebarView.render({btnHL: 2});
			orgsListBSView.render();  
		})

		router.on('route:orgData', function(identifier) {
			orgDataView.render({identifier: identifier});
		})

		router.on('route:orgUsers', function(identifier) {	
			orgUsersView.render({identifier: identifier});
		})

		router.on('route:orgFlows', function(identifier) {	
			orgFlowsView.render({identifier: identifier, active: true});
		})

		router.on('route:orgPrgFlows', function(identifier) {	
			orgFlowsView.render({identifier: identifier, active: false});
		})

		router.on('route:orgTerminals', function(identifier) {
			orgTerminalsView.render({identifier: identifier});
		})

		router.on('route:flows', function() {
			adminSidebarView.render({btnHL: 3});
			flowsView.render();
		})

		router.on('route:terminals', function() {
			adminSidebarView.render({btnHL: 4});
			terminalsView.render();
		})


		router.on('route:traffic', function() {
			adminSidebarView.render({btnHL: 5});
			trafficView.render();
		})

	//CLIENT
		router.on('route:clientOverview', function() {
			clientSidebarView.render({btnHL: 1});
			//clientOverviewView.render();
		})

		router.on('route:clientOrgData', function() {
			clientSidebarView.render({btnHL: 2});
			//clientOverviewView.render();
		})

		router.on('route:clientOrgUsers', function() {
			//clientOverviewView.render();
		})

		router.on('route:clientFlows', function() {
			clientSidebarView.render({btnHL: 3});
			//clientOverviewView.render();
		})

		router.on('route:clientTerminals', function() {
			clientSidebarView.render({btnHL: 4});
			//clientOverviewView.render();
		})

		router.on('route:clientTraffic', function() {
			clientSidebarView.render({btnHL: 5});
			//clientOverviewView.render();
		})
	Backbone.history.start();

	

})(jQuery);