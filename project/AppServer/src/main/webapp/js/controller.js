function copyTo(obj) {
	console.log('copyTo');
	document.getElementById("terminalNameLabel").textContent = obj.value;
}

function fetchTerminals(obj){
	console.log(obj.value);
	var orgSelected = obj.value;
}

(function($){
	var cntActiveFlows = 0;
	var cntPrgFlows = 0;
	var cntActiveTerms = 0;
	//var userOrgId = '';

	//call serializeObject to convert the form inputs to an object 
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


	//passing data to ASSIGN ORG TO TERMINAL modal
	$(document).on("click", ".assignationModal", function () {
		//we get the object with the selected data
		var objectToAssign = $(this).data();
		console.log(objectToAssign);

		var terminalId = objectToAssign.id; 
		var orgId = objectToAssign.orgid;
		var orgName = objectToAssign.orgname;
		var ipAddress = objectToAssign.terminaladdress;
		var mac = objectToAssign.terminalmac;
		var ifaceSpeed = objectToAssign.terminaliface;
		var hostName = objectToAssign.terminalname;

		$(".modal-body #terminalId").val( terminalId );
		$(".modal-body #orgId").val( orgId );
		$(".modal-body #orgName").val( orgName );
		$(".modal-body #terminalIP").val( ipAddress );
		$(".modal-body #terminalMac").val( mac );
		$(".modal-body #terminalSpeed").val( ifaceSpeed );
		$(".modal-body #terminalName").val( hostName );
		//$(".modal-body #assignedOrgId").val( assignedOrgId );
		document.getElementById("terminalIdLabel").textContent= terminalId;
		document.getElementById("terminalOrgLabel").textContent= orgName;
	});


//	Models

	//Alarm model example
	//{"timestamp":1390671712892,"event":"SWITCH_ADDED","updates":[{"inventoryId":"00:01:d4:ca:6d:b5:f4:0f","propertyId":"enabled","legacyValue":"false","newValue":"true","message":""}]}

	//TOrg data model  
	var Organization = Backbone.Model.extend({
		idAttribute: "identifier",
		urlRoot:'/AppServer/webapi/manager/org',
		defaults:{
			name: "",
			NIF: "",
			bankAccount: "",
			//identifier: "", // if we inicialize de ID, backbone detects all models as NO NEW 
			telephone: "",
			OAM: ""
		}
	});

	//flow model  
	var Flow = Backbone.Model.extend({
		idAttribute: "identifier",
		defaults:{
			//identifier: "",
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
		idAttribute: "identifier",
		urlRoot:'/AppServer/webapi/manager/terminal',
		defaults:{
			//identifier: "",
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
		idAttribute: "identifier",
		defaults:{
			//identifier: "",
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
		url:'/AppServer/webapi/manager/org/all',
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
		//url:'/AppServer/webapi/manager/terminal/all',
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
				terminalObject.assigned = currentValues.assigned;
				terminalObject.assignedOrgId = currentValues.assignedOrgId;
				terminalObject.description = currentValues.description;
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

//	ADMIN VIEWS
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
			var template = _.template($('#admin-sidebar-template').html(), {btnHL: options.btnHL , alarmCounting: getalarmCounter()});
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
			var thesession = getSession();
			console.log(thesession);
			loginOrg = session.orgId;
			loginOrgName = session.orgName;
			loginUserName = session.userName;
			var template = _.template($('#admin-overview-template').html());
			that.$el.html(template);
			//SlimScroll HEIGHTS
			$('#GS-alerts').slimScroll({
				height: '180px'
			});
		}
	});

	var adminOverviewView = new AdminOverviewView();
	// /GLOBAL VIEW #admin-overview-template

	//ALARMS



	var AdminAlarmsView = Backbone.View.extend({
		el: '.page',
		render: function () {
			var that = this;
			var template = _.template($('#admin-alarms-template').html());
			that.$el.html(template);
		}
	});

	var adminAlarmsView = new AdminAlarmsView();

	// /ALARMS

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

	// NEW & EDIT ORG view
	var NewOrgView = Backbone.View.extend({
		el: '.page',
		render: function (options) {
			var that = this;
			if(options.identifier){ //edit
				// "identifier" is what backbone takes to GET REST path
				that.org = new Organization({identifier: options.identifier});
				console.log(that.org.isNew());
				that.org.fetch({
					success: function (org){
						var template = _.template($('#edit-org-template').html(), {organization: org, admin: options.admin});
						that.$el.html(template);
					}
				});
			} else { //create
				var template = _.template($('#edit-org-template').html(), {organization: null, admin: options.admin});
				that.$el.html(template);
			} 

		},
		events: {
			'submit .edit-org-form': 'saveOrg',
			'click #delete-org': 'deleteOrg'
		},
		saveOrg: function (ev){
			var orgDetails = $(ev.currentTarget).serializeObject();
			//changing 'on' by true 
			if(orgDetails.OAM=='on'){
				orgDetails.OAM=true;
			}else{
				orgDetails.OAM=false;
			};
			console.log(orgDetails);
			var org = new Organization();
			org.save(orgDetails, {
				//type: "POST",
				contentType: "application/vmd.dxat.appserver.manager.org.collection+json",
				success: function (ev) {
					if(ev.attributes.identifier == "") alert("this org already exists");
					else router.navigate('adminOrgs/'+ev.attributes.identifier, {trigger: true});
				},
				error: function(model, response) {
					alert('wrong');
				}
			});
			return false;
		},
		deleteOrg: function (ev){
			var orgDetails = $(ev.currentTarget).serializeObject();
			if(this.org.id == loginOrg){
				alert("You are not able to delete your organization! are you all right?");
			}else{
				this.org.destroy({
					success: function (response) {
						if(response != ""){
							router.navigate('adminOrgs',{trigger: true});
							$('#confirmation').modal('hide');
							$('body').removeClass('modal-open');
							$('.modal-backdrop').remove();
						}
						else alert('DELETE ERROR');
					},
					error: function(response) {
						router.navigate('adminOrgs',{trigger: true});
						$('#confirmation').modal('hide');
						$('body').removeClass('modal-open');
						$('.modal-backdrop').remove();
					}
				});
				return false;	
			}
			return false;
		}
	});

	var newOrgView = new NewOrgView();

	// OrgData View
	var OrgDataView = Backbone.View.extend({
		el: '.page',
		render: function (options) { 
			var that = this;
			that.organization = new Organization({identifier: options.identifier});
			that.organization.fetch({
				success: function (organization) {  
					activeOrgName = organization.get('name');
					console.log(activeOrgName);
					var template = _.template($('#organizations-data-template').html(), {organization: organization});
					that.$el.html(template); 
				}
			});

		}
	});

	var orgDataView = new OrgDataView();


	// OrgUsers View
	var OrgUsersView = Backbone.View.extend({
		el: '.page',
		render: function (options) { 
			var that = this;
			that.users = new Users();
			that.users.url = '/AppServer/webapi/manager/user/'+options.identifier+'/all';
			that.users.fetch({
				success: function (users) {  
					var template = _.template($('#organizations-users-template').html(), {users: users.models, orgId: options.identifier, orgName: activeOrgName});
					that.$el.html(template); 
					//SlimScroll
					$('#OM-users').slimScroll({
						height: '400px'
					});
				}
			});

		}
	});

	var orgUsersView = new OrgUsersView();

	// org TERMINAL
	var OrgTerminalsView = Backbone.View.extend({
		el: '.page',
		render: function (options) { 
			var that = this;
			//if exists fetch details
			if(options.identifier) {
				that.terminals = new Terminals();
				that.terminals.url = '/AppServer/webapi/manager/terminal/'+options.identifier+'/all';
				that.terminals.fetch({
					success: function (terminals) {  
						var template = _.template($('#organizations-terminals-template').html(), {terminals: terminals.models, orgId: options.identifier, orgName: activeOrgName});
						that.$el.html(template); 
						//SlimScroll
						$('#OM-ap').slimScroll({
							height: '400px'
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
				that.activeFlows.url = '/AppServer/webapi/manager/flow/'+options.identifier+'/all';
				that.activeFlows.fetch({
					success: function (flows) {  
						var template = _.template($('#organizations-flows-template').html(), {flows: flows.models, orgId: options.identifier, active: options.active, orgName: activeOrgName, admin: options.admin});
						that.$el.html(template); 
						//SlimScroll
						$('#FLW-prg').slimScroll({
							height: '400px'
						});
					}
				});
			} else {
				var template = _.template($('#organizations-flows-template').html(), {flows: null, admin: options.admin});
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
			if(options.all==true){flows.url = '/AppServer/webapi/manager/flow/all';}
			if(options.all==false){flows.url = '/AppServer/webapi/manager/flow/'+options.identifier+'/all';}			
			flows.fetch({
				success: function (flows) {
					var template = _.template($('#flows-template').html(), {flows: flows.models, admin: options.all, orgId: options.identifier});
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

			//obtain organizations list to make a dropdown to assign org to terminals
			organizations = new Organizations();
			organizations.url = '/AppServer/webapi/manager/org/all';
			organizations.fetch();

			var that = this;
			var terminals = new Terminals();
			//to use same template with both [admin(all=true) and org(all=false)] views
			if(options.all==true){terminals.url = '/AppServer/webapi/manager/terminal/all';}
			if(options.all==false){terminals.url = '/AppServer/webapi/manager/terminal/'+options.identifier+'/all';}
			terminals.fetch({
				success: function (terminals) {
					var template = _.template($('#terminals-template').html(), {terminals: terminals.models, organizations: organizations.models, all: options.all});
					that.$el.html(template);
					$('#un-AP').slimScroll({
						height: '250px'
					});
					$('#AP').slimScroll({
						height: '250px'
					});
					$('.listScroll').slimScroll({
						height: '150px'
					});
				}
			});
		},
		events: {
			'submit .assign-term-form': 'assign'
		},
		assign: function (ev){
			var termDetails = $(ev.currentTarget).serializeObject();
			console.log(termDetails);
			var terminal = new Terminal();
			terminal.url = '/AppServer/webapi/manager/terminal/'+termDetails.assignedOrgId;

			/* terminal.orgId = null;
			terminal.orgName = null;
			console.log(termDetails);*/

			terminal.save(termDetails, {
				//type: "POST",
				contentType: "application/vmd.dxat.appserver.manager.terminal.collection+json",
				success: function (ev) {
					console.log(ev);
					if(ev.attributes.identifier == "") alert("this terminal already exists");
					else{
						$('#confirmationOrgAssign').modal('hide');
						$('body').removeClass('modal-open');
						$('.modal-backdrop').remove();
						router.navigate('adminTerminals/'+termDetails.assignedOrgId, {trigger: true});
					}
				},
				error: function(model, response) {
					alert('wrong');
				}
			});
			return false;
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
			setTrafficMatrix();
		}
	});

	var trafficView = new TrafficView();
	// /TRAFFIC VIEW #traffic-template

//	CLIENT VIEWS
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
			that.organization = new Organization({identifier: options.identifier});
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
			that.organization = new Organization({identifier: options.identifier});
			that.organization.fetch({
				success: function (organization) {
					var template = _.template($('#client-data-template').html(), {organization: organization, activeOrgName: activeOrgName, orgId: options.identifier});
					that.$el.html(template); 
					//SlimScroll
					$('#client-data').slimScroll({
						height: '380px'
					});
					return false;

				}
			});
			return false;
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
			that.users.url = '/AppServer/webapi/manager/user/'+options.identifier+'/all';
			that.users.fetch({
				success: function (users) {  
					var template = _.template($('#client-users-template').html(), {users: users.models, orgId: options.identifier, orgName: activeOrgName, admin: options.admin});
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

//	SHARED VIEWS
	// FLOW EDIT VIEW
	var NewFlowView = Backbone.View.extend({
		el: '.page',
		render: function (options) {
			console.log('options id: '+options.identifier);
			console.log('options org: '+options.orgId);
			console.log('options admin: '+options.admin);
			var that = this;
			if(options.identifier && options.admin){ //EDIT FLOW

				that.flow = new Flow({identifier: options.identifier});
				that.flow.urlRoot = '/AppServer/webapi/manager/flow/'+options.orgId;
				that.flow.fetch({
					success: function (flow){
						var terminals = new Terminals ();
						if(options.admin==true){
							console.log('admin true');
							terminals.url = '/AppServer/webapi/manager/terminal/all';
							//if is ADMIN we fetch ALL terminals and organizations
							var organizations = new Organizations();
							organizations.url = '/AppServer/webapi/manager/org/all';
							organizations.fetch();
						};
						if(options.admin==false){
							console.log('admin false');
							//if is NO ADMIN we fetch just organization terminals
							terminals.url = '/AppServer/webapi/manager/terminal/'+options.identifier+'/all';
							var organizations = new Organizations();
						};
						terminals.fetch();

						var template = _.template($('#new-flow-template').html(), {terminals: terminals.models, orgId: options.identifier, organizations: organizations.models, admin: options.admin, flow: flow});
						that.$el.html(template);
						$('.listScroll').slimScroll({
							height: '150px'
						});
					}
				});
			} else { //NEW FLOW
				var terminals = new Terminals ();
				if(options.admin==true){
					terminals.url = '/AppServer/webapi/manager/terminal/all';
					//if is ADMIN we fetch the organizations
					var organizations = new Organizations();
					organizations.url = '/AppServer/webapi/manager/org/all';
					organizations.fetch();
				};
				if(options.admin==false){
					terminals.url = '/AppServer/webapi/manager/terminal/'+options.identifier+'/all';
					var organizations = new Organizations();
				}
				terminals.fetch({
					success: function (terminals){
						var template = _.template($('#new-flow-template').html(), {terminals: terminals.models, orgId: options.identifier, organizations: organizations.models, admin: options.admin, flow: null});
						that.$el.html(template);
						$('.listScroll').slimScroll({
							height: '150px'
						});
					}
				});
			}

		},
		events: {
			//'clickButton': 'doAction'
			'submit .new-flow-form': 'createFlow'
		},
		createFlow: function (ev) { //event object to have access to the event just happened 
			var flowDetails = $(ev.currentTarget).serializeObject();
			console.log(flowDetails);
			var flow = new Flow();
			flow.urlRoot = '/AppServer/webapi/manager/flow/orgId9';//'+flowDetails.orgId+'/';
			flow.save(flowDetails, { //SEND object to the server
				contentType: "application/vmd.dxat.appserver.manager.flow.collection+json",
				success: function (flow) {
					console.log(flow);
					return false;
				}
			});
			return false; //Avoid refresh after submit
		}
	});

	var newFlowView = new NewFlowView();


	// USER EDIT view
	var EditUserView = Backbone.View.extend({
		el: '.page',
		selectedOrg: '',
		selectedUser: '',
		render: function (options) {
			console.log(options);
			var that = this;
			if(options.identifier){ //edit 
				// "identifier" is what backbone takes to GET REST path
				that.user = new User({identifier: options.identifier});
				var userPath = options.orgId+'/';
				console.log(userPath);
				that.user.urlRoot = '/AppServer/webapi/manager/user/'+userPath;
				that.user.fetch({
					success: function (user){
						console.log('orgId inside success: '+options.orgId);
						selectedOrg = options.orgId;
						selectedUser = options.identifier;
						var template = _.template($('#edit-user-template').html(), {user: user, orgId: options.orgId, admin: options.admin});
						that.$el.html(template);
					}
				});
			} else { //create
				var template = _.template($('#edit-user-template').html(), {user: null, orgId: options.orgId, admin: options.admin});
				that.$el.html(template);
			} 
		},
		events: {
			'submit .edit-user-form': 'saveUser',
			'click #delete-user': 'deleteUser'
		},
		saveUser: function (ev){
			var userDetails = $(ev.currentTarget).serializeObject();
			console.log(userDetails);
			//changing 'on' by true 
			if(userDetails.admin=='on'){
				userDetails.admin=true;
			}else{
				userDetails.admin=false;
			};
			console.log(userDetails);
			console.log(ev.currentTarget.orgId.value);
			userOrgId = ev.currentTarget.orgId.value;
			userId = userDetails.identifier;
			console.log(userOrgId+' '+userId);
			var user = new User();
			user.urlRoot = '/AppServer/webapi/manager/user/'+userOrgId;
			user.save(userDetails, {
				//type: "POST",
				contentType: "application/vmd.dxat.appserver.manager.user.collection+json",
				success: function (ev) {
					console.log('success saveUser');
					console.log(ev);
					if(ev.attributes.identifier == "") alert("this user already exists");
					else router.navigate('adminUsers/'+userOrgId, {trigger: true});
				},
				error: function(model, response) {
					alert('wrong');
				}
			});
			return false;
		},
		deleteUser: function (ev){
			if(selectedUser == loginUser){
				alert("You are not able to delete your organization! are you all right?");
			}else{		
				this.user.destroy({
					success: function () {
						router.navigate('adminUsers/'+selectedOrg, {trigger: true});
						$('#confirmationUser').modal('hide');
						$('body').removeClass('modal-open');
						$('.modal-backdrop').remove();
					},
					error: function () {
						router.navigate('adminUsers/'+selectedOrg, {trigger: true});
						$('#confirmationUser').modal('hide');
						$('body').removeClass('modal-open');
						$('.modal-backdrop').remove();
					}
				});
			}
			return false;
		}
	});

	var editUserView = new EditUserView();

	// TERMINAL EDIT view
	var EditTerminalView = Backbone.View.extend({
		el: '.page',
		render: function (options) {
			console.log(options);
			var that = this;
			organizations = new Organizations();
			organizations.url = '/AppServer/webapi/manager/org/all';
			organizations.fetch();
			if(options.identifier){ //edit 
				// "identifier" is what backbone takes to GET REST path
				that.terminal = new Terminal({identifier: options.identifier});
				var terminalPath = options.orgId+'/';
				console.log(terminalPath);
				that.terminal.urlRoot = '/AppServer/webapi/manager/terminal/'+terminalPath;
				that.terminal.fetch({
					success: function (terminal){
						console.log('orgId inside edit terminal fetch success: '+options.orgId);
						var template = _.template($('#edit-terminal-template').html(), {terminal: terminal, organizations: organizations.models, orgId: options.orgId, admin: options.admin});
						that.$el.html(template);
					}
				});
			} else { //create
				var template = _.template($('#edit-terminal-template').html(), {terminal: null, organizations: organizations.models, orgId: options.orgId});
				that.$el.html(template);
			} 
		},
		events: {
			'submit .edit-terminal-form': 'saveTerminal',
			'click #unassign-terminal': 'unassignTerminal'
		},
		saveTerminal: function (ev){
			var terminalDetails = $(ev.currentTarget).serializeObject();
			console.log(terminalDetails);
			console.log(ev.currentTarget.orgId.value);
			terminalOrgId = ev.currentTarget.orgId.value;
			terminalId = terminalDetails.identifier;
			console.log(terminalOrgId+' '+terminalId);
			var terminal = new Terminal();
			terminal.urlRoot = '/AppServer/webapi/manager/terminal/'+terminalOrgId;
			terminal.save(terminalDetails, {
				//type: "POST",
				contentType: "application/vmd.dxat.appserver.manager.terminal.collection+json",
				success: function (ev) {
					console.log('success saveTerminal');
					console.log(ev);
					if(ev.attributes.identifier == "") alert("this terminal already exists");
					else router.navigate('adminTerminals/'+ev.attributes.identifier, {trigger: true});
				},
				error: function(model, response) {
					alert('wrong');
				}
			});
			return false;
		},
		unassignTerminal: function (ev){
			this.terminal.destroy({
				success: function () {
					$('#confirmationTerminal').modal('hide');
					$('body').removeClass('modal-open');
					$('.modal-backdrop').remove();
					router.navigate('adminOrgs/', {trigger: true});
				}
			});
			return false;
		}
	});

	var editTerminalView = new EditTerminalView();

//	ROUTES 
	var Router = Backbone.Router.extend({
		routes: {
			"": "login", //LOGIN view
			//ADMIN ROUTES
			"adminOverview" : "adminOverview", //Admin First View
			"adminAlarms" : "adminAlarms", //Admin ALARMS
			"adminOrgs": "adminOrgs", //Organizations list
			"adminOrgs/:identifier": "orgData", //Org informtion
			"newOrg": "editOrg", // CREATE Org
			"editOrg/:identifier": "editOrg", //EDIT Org template (same as CREATE)
			"adminUsers/:identifier": "orgUsers", //Org users
			"adminFlows/:identifier": "orgFlows", //active flows of specipic org
			"adminFlows": "flows", //active flows of specipic org
			"adminPrgFlows/:identifier": "orgPrgFlows",	//programmed flows of specific org	
			"adminTerminals/:identifier": "orgTerminals", //terminals of specific org
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
			"newFlowAdmin/:identifier": "newFlowAdmin",
			"newFlow/:identifier": "newFlow",
			"editFlowAdmin/:orgId/:identifier": "editFlowAdmin",
			"editFlowOrg/:orgId/:identifier": "editFlowOrg",
			"newUser/:orgId": "editUser", //NEW USER template
			"newUserAdmin/:orgId": "editUserAdmin", //NEW USER template
			"editUser/:orgId/:identifier": "editUser", //EDIT USER template
			"editUserAdmin/:orgId/:identifier": "editUserAdmin", //EDIT USER template
			"editTerminal/:orgId/:identifier": "editTerminal", //EDIT terminal
			"editTerminalOrg/:orgId/:identifier": "editTerminalOrg" //EDIT terminal
		}
	});

	var router = new Router;

	//EVENTS FROM ROUTES

	//ADMIN
	router.on('route:login', function() {
		loginView.render();
	});

	router.on('route:adminOverview', function() {
		$('#modal-loading').modal('show');
		adminSidebarView.render({btnHL: 1 , alarmCounting: getalarmCounter()});		// render global view
		adminOverviewView.render();
		setAlarmView();
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		stopTrafficMatrix();
		initStatusOverview();
	});

	router.on('route:adminAlarms', function() {
		//resetAlarmView();
		$('#alarmDetails').hide();
		adminSidebarView.render({btnHL: 7});
		adminAlarmsView.render();
		setTabAlarmView();
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		resetAlarmView();
		stopTrafficMatrix();
		setAlarmView();
	});

	router.on('route:adminOrgs', function() {
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		adminSidebarView.render({btnHL: 2 , alarmCounting: getalarmCounter()});
		orgsListBSView.render();  
		stopTrafficMatrix();
		setAlarmView();
	});

	router.on('route:editOrgAdmin', function(id) {
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		newOrgView.render({identifier: id, admin: true});
		stopTrafficMatrix();
		setAlarmView();
	});

	router.on('route:editOrg', function(id) {
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		newOrgView.render({identifier: id, admin: false});
		stopTrafficMatrix();
		setAlarmView();
	});

	router.on('route:orgData', function(id) {
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		orgDataView.render({identifier: id});
		stopTrafficMatrix();
		setAlarmView();
	});

	router.on('route:orgUsers', function(id) {
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		orgUsersView.render({identifier: id});
		stopTrafficMatrix();
		setAlarmView();
	});

	router.on('route:orgFlows', function(id) {	
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		orgFlowsView.render({identifier: id, active: true, admin: true});
		stopTrafficMatrix();
		setAlarmView();
	});

	router.on('route:orgPrgFlows', function(id) {
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		orgFlowsView.render({identifier: id, active: false, admin: true});
		stopTrafficMatrix();
		setAlarmView();
	});

	router.on('route:orgTerminals', function(id) {
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		orgTerminalsView.render({identifier: id});
		stopTrafficMatrix();
		setAlarmView();
	});

	router.on('route:flows', function(id) {
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		adminSidebarView.render({btnHL: 3 , alarmCounting: getalarmCounter()});
		flowsView.render({identifier: id, all: true});
		stopTrafficMatrix();
		setAlarmView();
	});

	router.on('route:terminals', function() {
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		adminSidebarView.render({btnHL: 4 , alarmCounting: getalarmCounter()});
		terminalsView.render({all: true});
		stopTrafficMatrix();
		setAlarmView();
	});


	router.on('route:traffic', function() {
		loadDefaultStatValues();
		StopSwitchStats();
		stopTopoWeatherMapRefresh();
		adminSidebarView.render({btnHL: 5 , alarmCounting: getalarmCounter()});
		setAlarmView();
		startTrafficMatrix();
		trafficView.render();
	});

	//CLIENT
	router.on('route:clientOverview', function(id) {
		clientSidebarView.render({btnHL: 1});
		flows = new Flows();
		flows.url = '/AppServer/webapi/manager/flow/'+id+'/all';
		flows.fetch({});
		terminals = new Terminals();
		terminals.url = '/AppServer/webapi/manager/terminal/'+id+'/all';
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
		clientUsersView.render({identifier: id, admin: false});
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
	router.on('route:newFlowAdmin', function(id) {
		newFlowView.render({orgId: id.orgId, identifier: id.flowId, admin: true});
		console.log(id);
	});

	router.on('route:newFlow', function(id) {
		newFlowView.render({identifier: id, admin: false});
	});

	router.on('route:editFlowAdmin', function(orgId, identifier) {
		newFlowView.render({identifier: identifier, admin: true, orgId: orgId});
	});

	router.on('route:editFlowOrg', function(orgId, identifier) {
		newFlowView.render({identifier: identifier, admin: false, orgId: orgId});
	});


	router.on('route:editUser', function(orgId, identifier) {
		loadDefaultStatValues();
		StopSwitchStats();
		console.log(orgId+' '+identifier)
		editUserView.render({orgId: orgId, identifier: identifier, admin: false});
	});

	router.on('route:editUserAdmin', function(orgId, identifier) {
		loadDefaultStatValues();
		StopSwitchStats();
		console.log(orgId+' '+identifier)
		editUserView.render({orgId: orgId, identifier: identifier, admin: true});
	});

	router.on('route:editTerminal', function(orgId, identifier) {
		console.log('editTerminal route trigged');
		loadDefaultStatValues();
		StopSwitchStats();
		if(identifier==null){
			console.log('id '+identifier);
			identifier=orgId;
			console.log('id '+identifier);
			orgId=undefined;
		}
		console.log(orgId+' '+identifier)
		editTerminalView.render({orgId: orgId, identifier: identifier, admin: true});
	});

	router.on('route:editTerminalOrg', function(orgId, identifier) {
		console.log('editTerminalOrg route trigged');
		loadDefaultStatValues();
		StopSwitchStats();
		if(identifier==null){
			console.log('id '+identifier);
			identifier=orgId;
			console.log('id '+identifier);
			orgId=undefined;
		}
		console.log(orgId+' '+identifier)
		editTerminalView.render({orgId: orgId, identifier: identifier, admin: false});
	});

	Backbone.history.start();

	var loginUser = '';
	var loginOrg = '';
	var activeOrgName = '';
	var loginOrgName = '';;
	var loginUserName = '';


})(jQuery);