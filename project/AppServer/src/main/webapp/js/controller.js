(function($){
	var cntActiveFlows = 0;
	var cntPrgFlows = 0;
	var cntActiveTerms = 0;

	//call serializeObject to convert to convert the form inputs to an object 
	$.fn.serializeObject = function() {
	  var o = {};
	  var a = this.serializeArray();
	  $.each(a, function() {
	      if (o[this.name] !== undefined) {
	          if (!o[this.name].push) {
	              o[this.name] = [o[this.name]];
	          }
	          o[this.name].push(this.value || '');
	      } else {
	          o[this.name] = this.value || '';
	      }
	  });
	  return o;
	};

//	Models
	//TOrg data model  
	var Organization = Backbone.Model.extend({
		urlRoot:'/AppServer/webapp/manager/org',
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
		//urlRoot:'/AppServer/webapp/manager/terminal/all?orgId=',
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
		urlRoot:'/AppServer/webapp/manager/user',
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
		url:'/AppServer/webapp/manager/org/all',
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
				if(currentValues.active) cntActiveFlows++;
				if(!currentValues.active) cntPrgFlows++;
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
		//url:'/AppServer/webapp/manager/terminal/all',
		parse:function (response) {
			for ( var i = 0, length = response.orgTerminals.length; i < length; i++) {
				var currentValues = response.orgTerminals[i];
				var terminalObject = {};
				terminalObject.identifier = currentValues.identifier;
				terminalObject.active = currentValues.active;
				if(currentValues.active) cntActiveTerms++;
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
			var uri = '/AppServer/webapp/manager/user/all?orgId=' + aux;
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

// ADMIN VIEWS
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
			});
		}
	});

	var orgsListBSView = new OrgsListBSView();
	// /Orgs List Bootstrap View


	var NewOrgView = Backbone.View.extend({
		el: '.page',
		events: {
			'submit .edit-org-form': 'saveOrg',
			'click .delete': 'deleteOrg'
		},
		saveOrg: function (ev){
			var orgDetails = $(ev.currentTarget).serializeObject();
			console.log(orgDetails);
			var org = new Organization();
			org.urlRoot = '/AppServer/webapp/manager/org';
			org.save(orgDetails, {
				type: "POST",
			    contentType: "application/vmd.dxat.appserver.manager.org.collection+json",
				success: function (ev) {
					router.navigate('adminOrgs/'+ev.orgId, {trigger: true});
				}
			});
			return false;
		},
		deleteOrg: function (ev){
			this.org.destroy({
				success: function () {
					router.navigate('adminOrgs',{triggq: true});
				}
			});
			return false;
		},
		render: function (options) {
			var that = this;
			if(options.orgId){
				that.org = new Organization({orgId: options.orgId});
				that.org.fetch({
					success: function (org){
						var template = _.template($('#edit-org-template').html(), {organization: org});
          				that.$el.html(template);
					}
				});
			} else {
	          var template = _.template($('#edit-org-template').html(), {organization: null});
	          that.$el.html(template);
	        } 

		}
	});

	var newOrgView = new NewOrgView();
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
						activeOrgName = organization.get('name');
						console.log(activeOrgName);
						var template = _.template($('#organizations-data-template').html(), {organization: organization});
						that.$el.html(template); 
						//SlimScroll
						$('#OM-data').slimScroll({
							height: '190px'
						});
					}
				});
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
			that.users = new Users();
			that.users.url = '/AppServer/webapp/manager/user/'+options.identifier+'/all';
			that.users.fetch({
				success: function (users) {  
					var template = _.template($('#organizations-users-template').html(), {users: users.models, orgId: options.identifier, orgName: activeOrgName});
					that.$el.html(template); 
					//SlimScroll
						$('#OM-users').slimScroll({
							height: '135px'
						});
				}
			});
			
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
				that.terminals.url = '/AppServer/webapp/manager/terminal/'+options.identifier+'/all';
				that.terminals.fetch({
					success: function (terminals) {  
						var template = _.template($('#organizations-terminals-template').html(), {terminals: terminals.models, orgId: options.identifier, orgName: activeOrgName});
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
				that.activeFlows.url = '/AppServer/webapp/manager/flow/'+options.identifier+'/all';
				that.activeFlows.fetch({
					success: function (flows) {  
						var template = _.template($('#organizations-flows-template').html(), {flows: flows.models, orgId: options.identifier, active: options.active, orgName: activeOrgName});
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
		render: function (options) {
			var that = this;
			var flows = new Flows();
			if(options.all==true){flows.url = '/AppServer/webapp/manager/flow/all';}
			if(options.all==false){flows.url = '/AppServer/webapp/manager/flow/'+options.identifier+'/all';}			
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
			});
		}
	});

	var flowsView = new FlowsView();
	// /FLOWS VIEW #flows-template

	//TERMINALS VIEW #terminals-template
	var TerminalsView = Backbone.View.extend({
		el: '.page',
		render: function (options) {
			var that = this;
			var terminals = new Terminals();
			if(options.all==true){terminals.url = '/AppServer/webapp/manager/terminal/all';}
			if(options.all==false){terminals.url = '/AppServer/webapp/manager/terminal/'+options.identifier+'/all';}
			terminals.fetch({
				success: function (terminals) {
					var template = _.template($('#terminals-template').html(), {terminals: terminals.models});
					that.$el.html(template);
					$('#AP').slimScroll({
						height: '500px'
					});
				}
			});
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

// CLIENT VIEWS
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

	//CLIENT OVERVIEW #client-overview-template
	var ClientOverviewView = Backbone.View.extend({
		el: '.page',
		render: function (options) {
			var that = this;
			that.organization = new Organization({id: options.identifier});
			that.organization.fetch({
				success: function (organization) {  
					activeOrgName = organization.get('name');
					loginOrg = options.identifier;
					console.log('success fetch organization');
					console.log(loginOrg);
					console.log(activeOrgName);
					console.log(cntActiveFlows);
					console.log(cntPrgFlows);
					var template = _.template($('#client-overview-template').html(), {organization: organization, cntActiveFlows: cntActiveFlows, cntPrgFlows: cntPrgFlows, cntActiveTerms: cntActiveTerms});
					that.$el.html(template); 

					//SlimScroll
					$('#client-OV').slimScroll({
				height: '500px'
			});
				}
			});
		}
	});

	var clientOverviewView = new ClientOverviewView();
	// /GLOBAL VIEW #admin-overview-template

	//CLIENT ORG DATA VIEW
	var ClientOrgDataView = Backbone.View.extend({
		el: '.page',
		render: function (options) { 
			var that = this;
			that.organization = new Organization({id: options.identifier});
			that.organization.fetch({
				success: function (organization) {
						var template = _.template($('#client-data-template').html(), {organization: organization, activeOrgName: activeOrgName});
						that.$el.html(template); 
						//SlimScroll
						$('#client-data').slimScroll({
				            height: '380px'
				        });
				  
				    
				}
			});
		}
	});

	var clientOrgDataView = new ClientOrgDataView();
	// /CLIENT ORG DATA VIEW

	//ClientUsersView View
	var ClientUsersView = Backbone.View.extend({
		el: '.page',
		render: function (options) { 
			var that = this;
			that.users = new Users();
			that.users.url = '/AppServer/webapp/manager/user/'+options.identifier+'/all';
			that.users.fetch({
				success: function (users) {  
					var template = _.template($('#client-users-template').html(), {users: users.models, orgId: options.identifier, orgName: activeOrgName});
					that.$el.html(template); 
					//SlimScroll
						$('#client-users').slimScroll({
				            height: '380px',
				        });
				}
			});
			
		}
	});

	var clientUsersView = new ClientUsersView();

// SHARED VIEWS
	var NewFlowView = Backbone.View.extend({
		el: '.page',
		render: function (options) {
			var that = this;
			var terminals = new Terminals ();
			terminals.url = '/manager/terminal/all';
			terminals.fetch({
				success: function (terminals){
					var template = _.template($('#new-flow-template').html(), {terminals: terminals.models, orgId: options.identifier});
					that.$el.html(template);
					$('#list-sele-terminals').slimScroll({
					height: '100px'
					});
				}
			});
			
		},
		events: {
			//'clickButton': 'doAction'
			'submit .new-flow-form': 'createFlow'
		},
		createFlow: function (ev) { //event object to have access to the event just happened 
			var flowDetails = $(ev.currentTarget).serializeObject();
			var flow = new Flow();
			flow.urlRoot = '/manager/flow/'+flowDetails.orgId+'/';
			flow.save(flowDetails, { //SEND object to the server
				success: function (flow) {
					console.log(flow);
					return false;
				}
			});
			return false; //Avoid refresh after submit
		}
	});

	var newFlowView = new NewFlowView();

//	ROUTES 
	var Router = Backbone.Router.extend({
		routes: {
			"": "login", //LOGIN view
			//ADMIN ROUTES
			"adminOverview" : "adminOverview", //Admin First View
			"adminOrgs": "adminOrgs", //Organizations list
			"adminOrgs/:identifier": "orgData", //Org informtion
			"newOrg": "editOrg",
			"adminUsers/:identifier": "orgUsers", //Org users
			"adminFlows/:identifier": "orgFlows", //active flows of specipic org
			"adminPrgFlows/:identifier": "orgPrgFlows",	//programmed flows of specific org	
			"adminTerminals/:identifier": "orgTerminals", //terminals of specific org
			"adminFlows": "flows", //all the flows				
			"adminFlows/:identifier": "flows", //all the flows	
			"adminTerminals": "terminals",//all the terminals
			"adminTraffic": "traffic",
			//CLIENT ROUTES
			"clientOverview/:identifier" : "clientOverview", //Client First View
			"clientOrgData/:identifier": "clientOrgData", 
			"clientOrgUsers/:identifier": "clientOrgUsers",
			"clientFlows/:identifier": "clientFlows",
			"clientTerminals/:identifier": "clientTerminals",
			"clientTraffic/:identifier": "clientTraffic",
			//SHARED ROUTES
			"newFlow/:identifier": "newFlow"
		}
	});

	var router = new Router;

	//EVENTS FROM ROUTES

	//ADMIN
		router.on('route:login', function() {
			loginView.render();
		});

		router.on('route:adminOverview', function() {
			adminSidebarView.render({btnHL: 1});
			// render global view
			adminOverviewView.render();
			loadDefaultStatValues();
			initStatusOverview();
			//SlimScroll HEIGHTS
			$('#GS-alerts').slimScroll({
				height: '100px'
			});
		});

		router.on('route:adminOrgs', function() {
			adminSidebarView.render({btnHL: 2});
			orgsListBSView.render();  
		});

		router.on('route:editOrg', function (id){
			newOrgView.render({identifier: id});
		});

		router.on('route:orgData', function(id) {
			orgDataView.render({identifier: id});
		});

		router.on('route:orgUsers', function(id) {	
			orgUsersView.render({identifier: id});
		});

		router.on('route:orgFlows', function(id) {	
			orgFlowsView.render({identifier: id, active: true});
		});

		router.on('route:orgPrgFlows', function(id) {	
			orgFlowsView.render({identifier: id, active: false});
		});

		router.on('route:orgTerminals', function(id) {
			orgTerminalsView.render({identifier: id});
		});

		router.on('route:flows', function(id) {
			adminSidebarView.render({btnHL: 3});
			flowsView.render({identifier: id, all: true});
		});

		router.on('route:terminals', function() {
			adminSidebarView.render({btnHL: 4});
			terminalsView.render({all: true});
		});


		router.on('route:traffic', function() {
			adminSidebarView.render({btnHL: 5});
			trafficView.render();
		});

	//CLIENT
		router.on('route:clientOverview', function(id) {
			clientSidebarView.render({btnHL: 1});
			flows = new Flows();
			flows.url = '/AppServer/webapp/manager/flow/'+id+'/all';
			flows.fetch({});
			terminals = new Terminals();
			terminals.url = '/AppServer/webapp/manager/terminal/'+id+'/all';
			terminals.fetch({});
			//console.log(cntActiveFlows);
			clientOverviewView.render({identifier: id});
			cntPrgFlows=0;
			cntActiveFlows=0;
			cntActiveTerms=0;
		});

		router.on('route:clientOrgData', function(id) {
			clientSidebarView.render({btnHL: 2});
			clientOrgDataView.render({identifier: id});
		});

		router.on('route:clientOrgUsers', function(id) {
			clientUsersView.render({identifier: id});
		});

		router.on('route:clientFlows', function(id) {
			clientSidebarView.render({btnHL: 3});
			//we call the same view as ADMIN but giving orgId which will change the collection url
			flowsView.render({all: false, identifier: id});
		});

		router.on('route:clientTerminals', function(id) {
			clientSidebarView.render({btnHL: 4});
			terminalsView.render({all: false, identifier: id});
		});

		router.on('route:clientTraffic', function(id) {
			clientSidebarView.render({btnHL: 5});
			//clientTrafficView.render({identifier: id});
		});

	//SHARED
		router.on('route:newFlow', function(id) {
			newFlowView.render({identifier: id});
			
		});

	Backbone.history.start();

	var loginUser = '';
	var loginOrg = '';
	var activeOrgName = '';

	

})(jQuery);