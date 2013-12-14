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
 (function($){

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


        var OrganizationsList = Backbone.Collection.extend({
                model: Organization,
                url:'http://localhost:8080/device-manager-api/webapi/organizations/routers',
        parse:function (response) {
            console.log(response);
            //response.id = response.inventoryId;
            // Parse the response and construct models
            for ( var i = 0, length = response.routers.length; i < length; i++) {

                    var currentValues = response.routers[i];
                    var orgObject = {};
                    console.log(currentValues);
//                    for ( var j = 0, valuesLength = currentValues.length; j < valuesLength; j++) {
//                            console.log(j);
//                            orgObject[keys[j]] = currentValues[j];
//                    }
                        orgObject.NIF = currentValues.NIF;
                        orgObject.OAM = currentValues.OAM;
                        orgObject.bankAccount = currentValues.bankAccount;
                        orgObject.identifier = currentValues.identifier;
                        orgObject.name = currentValues.name;
                        orgObject.telephone = currentValues.telephone;
                    // push the model object
                    this.push(orgObject);
            }
                        console.log(this.toJSON());
                        
                        //return models
                        return this.models;
            //return response;
        }
    });
        //var numOrganizations = 0; TODO: control device telephone in order to create new organizations properly...
        var organizations = new OrganizationsList();

        var OrganizationView = Backbone.View.extend({
                model: new Organization(),
                tagName: 'div',
                className:"organizations-container",
                events: {
                        'click .name': 'updateName'
                },
                initialize: function(){
                        this.template = _.template($('#organizations-template').html()); // using
                        // underscore
                        // template
                },
                render: function(){
                        this.$el.html(this.template(this.model.toJSON()));
                        return this;
                }
        });

        var OrganizationsView = Backbone.View.extend({
                model: organizations,
                el: $('#organizations-container'),
                initialize: function(){
                        var self = this;
                        this.model.on('add', this.render, this);
                        this.model.on('remove', this.render,this);
                        this.model.bind('request', this.ajaxStart, this);
                        this.model.bind('sync', this.ajaxComplete, this);
                        // get all organizations (Backbone.sync powah!!!)
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
                        var self = this;
                        self.$el.html('');
                        _.each(this.model.toArray(),function(organization,i){
                                self.$el.append((new OrganizationView({model: organization})).render().$el);
                        });
                        return this;
                }

        });

        var Router = Backbone.Router.extend({
                routes: {
                        '': 'index',
                        'organizations/:id': 'show'
                },
                index: function(){
                        // enable Add Organization
                        console.log("INDEX");
                        // disable show organization
                },
                show: function(id){
                        // disable add organization
                        console.log("SHOW");
                        // enable show organization
                }
        });

        $(document).ready(function(){
                $('#add-organization').submit(function(ev){
                        //var the_id= "RT-"+ ++numOrganizations;
                        //var organization = new Organization({id:the_id,name:$('#organization-name').val(),ip:$('#organization-ip').val(),description:$('#organization-description').val()});
                        var organization = new Organization({telephone:$('#organization-telephone').val(),name:$('#organization-name').val(),NIF:$('#organization-orgObject').val(),OAM:$('#organization-OAM').val()});
                        organizations.add(organization);
                        // var organization2 = new Organization({id:'RT-2',name:'Router
                        // 2',ip:'192.168.1.12',description:'second router'});
                        // var organization3 = new Organization({id:'RT-3',name:'Router
                        // 3',ip:'192.168.1.13',description:'third router'});
                        // console.log(organization.get('description'));
                        console.log(organizations.toJSON());
//                        organization.save({id:organization.get('id'),name:$('#organization-name').val(),ip:$('#organization-ip').val(),description:$('#organization-description').val()},{
//                        succes: function(){ consol.log("successfully saved organization!");},
//                        error: function(){ console.log("error saving organization!");}
//                        })
                        return false;        
                });

                var appView = new OrganizationsView();
        });
        


})(jQuery);


$(function(){
    $("#loading").spin({lines: 9, // The number of lines to draw
              length: 24, // The length of each line
              width: 7, // The line thickness
              radius: 32, // The radius of the inner circle
              corners: 1, // Corner roundness (0..1)
              rotate: 65, // The rotation offset
              direction: 1, // 1: clockwise, -1: counterclockwise
              color: '#000', // #rgb or #rrggbb or array of colors
              speed: 0.9, // Rounds per second
              trail: 76, // Afterglow percentage
              shadow: true, // Whether to render a shadow
              hwaccel: true, // Whether to use hardware acceleration
              className: 'spinner', // The CSS class to assign to the spinner
              zIndex: 2e9, // The z-index (defaults to 2000000000)
              top: 'auto', // Top position relative to parent in px
              left: 'auto' // Left position relative to parent in px
    }).hide();
    $('#loading').ajaxStart(function(){ 
            $(this).fadeIn(); 
    });
    $('#loading').ajaxComplete(function(){ 
            $(this).fadeOut();
    });
});






//MODELS


  var Organization = Backbone.Model.extend({
      defaults: function(){
        return{
          id: ' ',
          name: ' ',
          cif: ' ',
          telephone: ' ',
          bankAccount: ' ',
          isOAM: ' '
        }
      }
  });

  var Flow = Backbone.Model.extend({
      defaults: function(){
        return{
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
      }
  });

  var User = Backbone.Model.extend({
      defaults: function(){
        return{
          id: ' ',
          password: ' ',
          email: ' ',
          telephone: ' ',
          isAdmin: ' ',
          active: ' '
        }
      }
  });

  var Terminal = Backbone.Model.extend({
      defaults: function(){
        return{
          id: ' ',
          hostName: ' ',
          ipAddress: ' ',
          mac: ' ',
          ifaceSpeed: ' ',
          description: ' ',
          active: ' '
        }
      }
  });


  var OrganizationCollection = Backbone.Collection.extend({
     model:Organization,
     url:'http://localhost:8080/AppServer/webapp/manager/org/all'
  });
  
