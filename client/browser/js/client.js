(function ($) {

    var Device = Backbone.Model.extend({
        defaults:{
            id:"1",
            name:"Device1",
            status:"UP",
            portsConnected:"3",
            totalPorts:"4",
            mac1:"11:11:11:11:11:11",
            mac2:"12:12:12:12:12:12",
            mac3:"13:13:13:13:13:13",
            mac4:"14:14:14:14:14:14"
        }
    });

  var DeviceView = Backbone.View.extend({
        tagName:"div",
        className:"device-container",
        template:$("#device-template").html(),

        render:function () {
            var tmpl = _.template(this.template); //tmpl is a function that takes a JSON object and returns html

            this.$el.html(tmpl(this.model.toJSON())); //this.el is what we defined in tagName. use $el to get access to jQuery html() function
            return this;
        }
  });


 var device = new Device({
            id:"1",
            name:"Device1",
            status:"UP",
            portsConnected:"3",
            totalPorts:"4",
            mac1:"11:11:11:11:11:11",
            mac2:"12:12:12:12:12:12",
            mac3:"13:13:13:13:13:13",
            mac4:"14:14:14:14:14:14"
    });


    deviceView = new DeviceView({
        model: device
    });

    $("#devices").html(deviceView.render().el);
})(jQuery);