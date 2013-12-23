//Backbone aaaa
(function($){

  $.ajaxPrefilter( function( options, originalOptions, jqXHR ) {
    options.url = 'http://localhost:8080/AppServer/webapp/manager' + options.url;
  });


//Models
  //TOrg data model  
    var Organization = Backbone.Model.extend({
      urlRoot:'/org',
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
      //urlRoot:'/flow/all?orgId=',
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
      //urlRoot:'/terminal/all?orgId=',
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
      urlRoot:'/user/all?orgId=',
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

//COLLESCTIONS
  //TOrg data COLLECTION
    var Organizations = Backbone.Collection.extend({
        model: Organization,
          url:'/org/all',
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
          url:'/fullflow/all',
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
          url:'/fullterminal/all',
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
          url:'/fulluser/all',
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

//VIEWS

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

  //ADMIN SIDEBAR #login-template
    var AdminSidebarView = Backbone.View.extend({
      el: '.sidebar-container',
      updateClass: function() {
        this.$el.toggleClass('active');
      },
      render: function () {
        var that = this;
        var template = _.template($('#admin-sidebar-template').html());
        that.$el.html(template);
      }
    });

    var adminSidebarView = new AdminSidebarView();
  // /ADMIN SIDEBAR #login-template

  //GLOBAL VIEW #admin-overview-template
    var AdminOverviewView = Backbone.View.extend({
      el: '.page',
      render: function () {
        var that = this;
        var template = _.template($('#admin-overview-template').html());
        that.$el.html(template);
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
              //if there is TOrg, we fetch its users
                this.users = new Users({id: organization.identifier});
                this.users.fetch();
                var template = _.template($('#organizations-data-template').html(), {organization: organization, users: users.models});
                that.$el.html(template);
                //SlimScroll
                  $('#OM-data').slimScroll({
                      height: '190px'
                  });
                  $('#OM-topo').slimScroll({
                      height: '230px'
                  });
                  $('#OM-flows').slimScroll({
                      height: '135px'
                  });
                  $('#OM-prgFlows').slimScroll({
                      height: '135px'
                  });
                  $('#OM-users').slimScroll({
                      height: '135px'
                  });
                  $('#OM-ap').slimScroll({
                      height: '135px'
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
              $('#OM-topo').slimScroll({
                  height: '230px'
              });
              $('#OM-flows').slimScroll({
                  height: '135px'
              });
              $('#OM-prgFlows').slimScroll({
                  height: '135px'
              });
              $('#OM-users').slimScroll({
                  height: '135px'
              });
              $('#OM-ap').slimScroll({
                  height: '135px'
              });
          }
        }
      });

      var orgDataView = new OrgDataView();

  // /ORGANIZATIONS

  //FLOWS VIEW #flows-template
      var FlowsView = Backbone.View.extend({
        el: '.page',
        render: function () {
          var that = this;
          var flows = new Flows();
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
      }
    });

    var trafficView = new TrafficView();
  // /TRAFFIC VIEW #traffic-template

//ROUTES 
    var Router = Backbone.Router.extend({
        routes: {
          "": "login", 
          "adminOverview" : "adminOverview",
          "adminOrgs": "adminOrgs",
          "adminOrgs/:identifier": "orgData",
          "adminFlows": "flows",
          "adminTerminals": "terminals",
          "adminTraffic": "traffic"
        }
    });

    var router = new Router;

  //EVENTS FROM ROUTES

    router.on('route:login', function() {
      loginView.render();
    })

    router.on('route:adminOverview', function() {
      // render global view
      adminSidebarView.render();
      adminOverviewView.render();
      //SlimScroll HEIGHTS
        $('#GS-alerts').slimScroll({
            height: '200px'
        });
        $('#GS-topo').slimScroll({
            height: '200px'
        });
    })

    router.on('route:adminOrgs', function() {
      // render organizations view
      adminSidebarView.render();
      orgsListBSView.render();  
    })

    router.on('route:orgData', function(identifier) {
      adminSidebarView.render();
      //orgsListBSView.render(); 
      orgDataView.render({identifier: identifier});
      //orgUsersView.render({identifier: identifier}); 
    })

    router.on('route:flows', function() {
      // render global view
      adminSidebarView.render();
      flowsView.render();
    })

    router.on('route:terminals', function() {
      // render global view
      adminSidebarView.render();
      terminalsView.render();
    })

    router.on('route:traffic', function() {
      // render global view
      adminSidebarView.render();
      trafficView.render();
      
      //SlimScroll HEIGHTS
        $('#TA-matrix').slimScroll({
            height: '520px'
        });
    })


    Backbone.history.start();


})(jQuery);