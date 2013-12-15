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
        
        var OrganizationsList = Backbone.Collection.extend({
                model: Organization,
                url:'http://localhost:8080/AppServer/webapp/manager/org/all'
        });
        var numOrganizations = 0;
        var organizations = new OrganizationsList();

        var OrganizationView = Backbone.View.extend({
                model: new Organization(),
                tagName: 'div',
                events: {
                        'click .name': 'orgSelected'
                },
                initialize: function(){
                  console.log('INITIALIZE');
                        console.log(this.model.toJSON()); 
                        console.log(organizations.toJSON());
                        this.template = _.template($('#organizations-template').html()); //using underscore template
                },
                orgSelected: function(ev){
                        ev.preventDefault();
                        this.$('.name').attr('contenteditable',true).focus();
                },
                render: function(){
                        this.$el.html(this.template(this.model.toJSON()));
                        return this;
                }
        });
        
        var OrganizationsView = Backbone.View.extend({
                model: organizations,
                tagName: 'div',
                el: $('#organizations-container'),
                initialize: function(){
                        console.log('INITIALIZE OrganizationsView');
                        this.template = _.template($('#organizations-template').html());
                        this.model.on('add', this.render, this);
                        this.model.on('remove', this.render,this);
                },                                        
                render: function(){
                  console.log('RENDER OrganizationsView');
                        var self = this;
                        self.$el.html('');
                        _.each(this.model.toArray(),function(organization,i){
                                self.$el.append((new OrganizationView({model: organization})).render().$el);
                        });
                        return this;
                }
                
        });
        
        $(document).ready(function(){
                $('#add-organization').submit(function(ev){
                        var organization = new Organization({id:++numOrganizations,name:$('#organization-name').val(),ip:$('#organization-ip').val(),description:$('#organization-description').val()});
                        organizations.add(organization);
                        //var organization2 = new Organization({id:'RT-2',name:'Router 2',ip:'192.168.1.12',description:'second router'});
                        //var organization3 = new Organization({id:'RT-3',name:'Router 3',ip:'192.168.1.13',description:'third router'});
                        //console.log(organization.get('description'));
                        console.log(organizations.toJSON());

                        return false;        
                });
                
                var appView = new OrganizationsView();
        });
        
        
})(jQuery);



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
  
