//Backbone
(function($){

//COLLESCTIONS

  //TOrg COLLECTION
    var Organizations = Backbone.Collection.extend({
          url:'http://localhost:8080/AppServer/webapp/manager/org/all'
    });

    var Organization = Backbone.Model.extend({
      urlRoot: 'http://localhost:8080/AppServer/webapp/manager/org/all'
    });

//VIEWS
  //GLOBAL VIEW #global-template
    var GlobalView = Backbone.View.extend({
      el: '.page',
      render: function () {
    	  console.log('ENTRA AL RENDER de GlobalView');
        var that = this;
        var template = _.template($('#global-template').html());
        that.$el.html(template);
      }
    });

    var globalView = new GlobalView();
  // /GLOBAL VIEW #global-template


  //ORGANIZATIONS VIEW #organizations-template
    var OrganizationsView = Backbone.View.extend({
      el: '.page',
      render: function () {
        var organizations = new Organizations();
        organizations.fetch()
        console.log('ENTRA AL RENDER de OrganizationsView');
        var that = this;
        var template = _.template($('#organizations-template').html());
        that.$el.html(template);
      }
    });

    var organizationsView = new OrganizationsView();

    var OrganizationsListView = Backbone.View.extend({
      el: '.page',
      render: function () {
        var that = this;
        var organizations = new Organizations();
        organizations.fetch({
          success: function (organizations) {                          // , {organizations: organizations.models}
            var template = _.template($('#organizations-list-template').html());
            that.$el.html(that.template(that.model.toJSON()));
            return that;

          }
        })
      }
    });


    var organizationsListView = new OrganizationsListView();
  // /ORGANIZATIONS VIEW #organizations-template

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
          "": "global", 
          "organizations": "organizations",
          "flows": "flows",
          "terminals": "terminals",
          "traffic": "traffic",
          "orgData":"orgData",
        }
    });

    var router = new Router;

  //EVENTS FROM ROUTES
    router.on('route:global', function() {
      // render global view
      globalView.render();
      //SlimScroll HEIGHTS
        $('#GS-alerts').slimScroll({
            height: '200px'
        });
        $('#GS-topo').slimScroll({
            height: '200px'
        });
    })

    router.on('route:organizations', function() {
      // render organizations view
      organizationsView.render();
      //SlimScroll HEIGHTS
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
    })

    router.on('route:orgid', function(id) {
      userEditView.render({id: id}); 
      //SlimScroll HEIGHTS
    })

    router.on('route:flows', function() {
      // render global view
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
      terminalsView.render();
      //SlimScroll HEIGHTS
        $('#AP').slimScroll({
            height: '500px'
        });
    })

    router.on('route:traffic', function() {
      // render global view
      trafficView.render();
      //SlimScroll HEIGHTS
        $('#TA-matrix').slimScroll({
            height: '520px'
        });
    })

    router.on('route:orgData', function() {
      // render global view
      console.log('entra a router.on orgData');
      organizationsListView.render();
      
    })

    Backbone.history.start();


})(jQuery);