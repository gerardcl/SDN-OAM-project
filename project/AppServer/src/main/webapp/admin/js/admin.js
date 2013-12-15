//SlimScroll sizes AAAAA
$(function(){
  $('#GS-alerts').slimScroll({
      height: '200px'
  });
  $('#GS-topo').slimScroll({
      height: '200px'
  });
});

//Backbone
(function($){


    var GlobalView = Backbone.View.extend({
      el: '.page',
      render: function () {
        var that = this;
        var template = _.template($('#global-template').html());
        that.$el.html(template);
      }
    });

    var globalView = new GlobalView();


    var Router = Backbone.Router.extend({
        routes: {
          "": "global", 
          "organizations": "organizations",
            "organizations/:id": "orgid",
          "flows": "flows",
          "terminals": "terminals",
          "traffic": "traffic",

        }
    });

    var router = new Router;

    router.on('route:global', function() {
      // render user list
      globalView.render();
    })

    router.on('route:orgid', function(id) {
      userEditView.render({id: id});
    })

    Backbone.history.start();


})(jQuery);