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
     model:Organization
  });
  
//HARDCODED MODELS
    //ORGANIZATIONS LIST HARDCODED
    var organizations = [{id:'1',name:'Ikea',cif:'111111111111',telephone:'+34 911111111',bankAccount:'1111111111111-01',isOAM:false},
        {id:'2',name:'Danone',cif:'222222222222',telephone:'+34 922222222',bankAccount:'22222222222222-02',isOAM:false},
        {id:'3',name:'National Instruments',cif:'333333333333',telephone:'+34 933333333',bankAccount:'333333333333-03',isOAM:false},
        {id:'4',name:'Kraft',cif:'444444444444',telephone:'+34 944444444',bankAccount:'444444444444-04',isOAM:false},
        {id:'5',name:'Nestle',cif:'555555555555',telephone:'+34 955555555',bankAccount:'555555555555-05',isOAM:false},
        {id:'6',name:'Inditex',cif:'666666666666',telephone:'+34 966666666',bankAccount:'666666666666-06',isOAM:false},
        {id:'7',name:'Danone',cif:'222222222222',telephone:'+34 922222222',bankAccount:'22222222222222-02',isOAM:false},
        {id:'8',name:'National Instruments',cif:'333333333333',telephone:'+34 933333333',bankAccount:'333333333333-03',isOAM:false},
        {id:'9',name:'Kraft',cif:'444444444444',telephone:'+34 944444444',bankAccount:'444444444444-04',isOAM:false},
        {id:'10',name:'Nestle',cif:'555555555555',telephone:'+34 955555555',bankAccount:'555555555555-05',isOAM:false},
        {id:'11',name:'Danone',cif:'222222222222',telephone:'+34 922222222',bankAccount:'22222222222222-02',isOAM:false},
        {id:'12',name:'National Instruments',cif:'333333333333',telephone:'+34 933333333',bankAccount:'333333333333-03',isOAM:false},
        {id:'13',name:'Kraft',cif:'444444444444',telephone:'+34 944444444',bankAccount:'444444444444-04',isOAM:false},
        {id:'14',name:'Nestle',cif:'555555555555',telephone:'+34 955555555',bankAccount:'555555555555-05',isOAM:false},
        {id:'15',name:'Inditex',cif:'666666666666',telephone:'+34 966666666',bankAccount:'666666666666-06',isOAM:false}];

    var Flows = Backbone.Collection.extend({
       model:Flow
    });
    //FlOWS LIST HARDCODED
    var flows = [{id: '1',name: 'flow 1',srcOTidentifier: ' ',dstOTidentifier: ' ',srcPort: '1111',dstPort: '1111',qos: 'unknown',bandwidth: '100Mbps',protocol: 'UDP',active: true},
        {id: '2',name: 'flow 2',srcOTidentifier: ' ',dstOTidentifier: ' ',srcPort: '2222',dstPort: '2222',qos: 'unknown',bandwidth: '1Gbps',protocol: 'TCP',active: true},
        {id: '3',name: 'flow 3',srcOTidentifier: ' ',dstOTidentifier: ' ',srcPort: '3333',dstPort: '3333',qos: 'unknown',bandwidth: '10Mbps',protocol: 'TCP',active: false}];

    var Users = Backbone.Collection.extend({
       model:User
    });
    //USERS LIST HARDCODED
    var users = [{id: '1',password: 'pwd1',email: 'user1@org1.com',telephone: '911 111 111',isAdmin: true,active: true},
        {id: '2',password: 'pwd2',email: 'user2@org2.com',telephone: '922 222 222',isAdmin: false,active: true},
        {id: '3',password: 'pwd3',email: 'user3@org3.com',telephone: '933 333 333',isAdmin: false,active: true}];

    var Terminals = Backbone.Collection.extend({
       model:Terminal
    });
    //TERMINALS LIST HARDCODED
    var terminals = [{id: '1',hostName: 'Host 1',ipAddress: '192.168.1.1',mac: '11:11:11:11:11:11',ifaceSpeed: '1Gbps',description: 'host 1 located in Barcelona',active: true},
        {id: '2',hostName: 'Host 2',ipAddress: '192.168.1.2',mac: '22:22:22:22:22:22',ifaceSpeed: '100Mbps',description: 'host 2 located in Madrid',active: true},
        {id: '3',hostName: 'Host 3',ipAddress: '192.168.1.3',mac: '33:33:33:33:33:33',ifaceSpeed: '10Mbps',description: 'host 3 located in Valencia',active: true}];


// Views
window.OrganizationListView = Backbone.View.extend({
 
    tagName:'ul',
 
    initialize:function () {
        this.model.bind("reset", this.render, this);
    },
 
    render:function (eventName) {
        _.each(this.model.models, function (organization) {
            $(this.el).append(new OrganizationListItemView({model:organization}).render().el);
        }, this);
        return this;
    }
 
});
 
window.OrganizationListItemView = Backbone.View.extend({
 
    tagName:"li",
 
    template:_.template($('#organizations-template').html()),
 
    render:function (eventName) {
        $(this.el).html(this.template(this.model.toJSON()));
        return this;
    }
 
});

// Router
var AppRouter = Backbone.Router.extend({

    routes:{
        "":"list",
        "data/:id":"orgData"
    },

    list:function () {
        this.organizationList = new OrganizationCollection();
        this.organizationListView = new OrganizationListView({model:this.organizationList});
        //this.organizationList.fetch();
        $('#organizations-container').html(this.organizationListView.render().el);
    },

    orgData:function (id){
        this.organization = this.organizationList.get(id);
        this.organ
    }
});

var app = new AppRouter();
Backbone.history.start();

})(jQuery);