//SlimScroll sizes
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

var Organizations = Backbone.Collection.extend({

});

var OrganizationList = Backbone.View.extend({
    el: '.page',
    render: function() {
        var that = this;
        var organizations = Organizations();
        organizations.fetch({
            success: function(organizations){
                var template = _.template($('#organizations-template').html(),{organizations: organizations.model})
                that.$el.html('CONTENT WILL BE HERE')
            }
        })
        
    }
});


var Router = Backbone.Router.extend({
    routes: {
        '': 'home'
    }
});

var organizationList = new OrganizationList();

var router = new Router();
router.on('router:home', function(){
    console.log('HAY ALGUIEN AHÍ???');
    organizationList.render();
});

Backbone.history.start();
