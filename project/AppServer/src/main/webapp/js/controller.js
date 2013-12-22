//Backbone aaaa
(function($){

        $('#OM-orgColumn').slimScroll({
          height: '55px'
        });

  $.ajaxPrefilter( function( options, originalOptions, jqXHR ) {
    options.url = 'http://localhost:8080/AppServer/webapp/manager' + options.url;
  });

//COLLESCTIONS

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

  //TOrg COLLECTION
    var Organizations = Backbone.Collection.extend({
        model: Organization,
          url:'/org/all',
      parse:function (response) {
            //console.log(response);
            //response.id = response.inventoryId;
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
            console.log(options.identifier);
            that.organization = new Organization({id: options.identifier});
            console.log(that.organization.url);
            that.organization.fetch({
              success: function (organization) {    
                var template = _.template($('#organizations-data-template').html(), {organization: organization});
                that.$el.html(template);
              }
            })
          } else {
            var template = _.template($('#organizations-data-template').html(), {organization: null});
            that.$el.html(template);
          }
        }
      });

      var orgDataView = new OrgDataView();
    // /OrgData View

  // /ORGANIZATIONS

  //FLOWS VIEW #flows-template
    var FlowsView = Backbone.View.extend({
      el: '.page',
      render: function () {
        var that = this;
        var template = _.template($('#flows-template').html());
        that.$el.html(template);
      }
    });

    var flowsView = new FlowsView();
  // /FLOWS VIEW #flows-template

  //TERMINALS VIEW #terminals-template
    var TerminalsView = Backbone.View.extend({
      el: '.page',
      render: function () {
        var that = this;
        var template = _.template($('#terminals-template').html());
        that.$el.html(template);
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
      console.log('entra al route:login');
      loginView.render();
    })

    router.on('route:adminOverview', function() {
      // render global view
      console.log('entra al route:admin');
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
        $('#OM-orgColumn').slimScroll({
          height: '55px'
        });
      adminSidebarView.render();
      orgsListBSView.render(); 
      //SlimScroll HEIGHTS

      
    })

    router.on('route:orgData', function(identifier) {
      adminSidebarView.render();
      //orgsListBSView.render(); 
      orgDataView.render({identifier: identifier}); 
      //SlimScroll HEIGHTS
    })

    router.on('route:flows', function() {
      console.log('entra al route:flows');
      // render global view
      adminSidebarView.render();
      flowsView.render();
      
      //SlimScroll HEIGHTS
        $('#FLW-active').slimScroll({
            height: '170px'
        });
        $('#FLW-prg').slimScroll({
            height: '170px'
        });
    })

    router.on('route:terminals', function() {
      // render global view
      adminSidebarView.render();
      terminalsView.render();
      //SlimScroll HEIGHTS
        $('#AP').slimScroll({
            height: '500px'
        });
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

    router.on('route:orgData', function() {
      // render global view
      console.log('entra a router.on orgData');
      //organizationsListView.render();
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
    })

    Backbone.history.start();


})(jQuery);