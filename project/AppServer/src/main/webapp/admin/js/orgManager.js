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
(function ($) {
    var organizations = [{id:'1',name:'Ikea',cif:'111111111111',telephone:'+34 911111111',bankAccount:'1111111111111-01',isOAM:'false'},
        {id:'2',name:'Danone',cif:'222222222222',telephone:'+34 922222222',bankAccount:'22222222222222-02',isOAM:'false'},
        {id:'3',name:'National Instruments',cif:'333333333333',telephone:'+34 933333333',bankAccount:'333333333333-03',isOAM:'false'},
        {id:'4',name:'Kraft',cif:'444444444444',telephone:'+34 944444444',bankAccount:'444444444444-04',isOAM:'false'},
        {id:'5',name:'Nestle',cif:'555555555555',telephone:'+34 955555555',bankAccount:'555555555555-05',isOAM:'false'},
        {id:'6',name:'Inditex',cif:'666666666666',telephone:'+34 966666666',bankAccount:'666666666666-06',isOAM:'false'},
        {id:'7',name:'Danone',cif:'222222222222',telephone:'+34 922222222',bankAccount:'22222222222222-02',isOAM:'false'},
        {id:'8',name:'National Instruments',cif:'333333333333',telephone:'+34 933333333',bankAccount:'333333333333-03',isOAM:'false'},
        {id:'9',name:'Kraft',cif:'444444444444',telephone:'+34 944444444',bankAccount:'444444444444-04',isOAM:'false'},
        {id:'10',name:'Nestle',cif:'555555555555',telephone:'+34 955555555',bankAccount:'555555555555-05',isOAM:'false'},
        {id:'11',name:'Danone',cif:'222222222222',telephone:'+34 922222222',bankAccount:'22222222222222-02',isOAM:'false'},
        {id:'12',name:'National Instruments',cif:'333333333333',telephone:'+34 933333333',bankAccount:'333333333333-03',isOAM:'false'},
        {id:'13',name:'Kraft',cif:'444444444444',telephone:'+34 944444444',bankAccount:'444444444444-04',isOAM:'false'},
        {id:'14',name:'Nestle',cif:'555555555555',telephone:'+34 955555555',bankAccount:'555555555555-05',isOAM:'false'},
        {id:'15',name:'Inditex',cif:'666666666666',telephone:'+34 966666666',bankAccount:'666666666666-06',isOAM:'false'}];
  
    var Organization = Backbone.Model.extend({
        defaults:{
			id: ' ',
			name: ' ',
			cif: ' ',
			telephone: ' ',
			bankAccount: ' ',
			isOAM: ' '
        }
    });

    var Organizations = Backbone.Collection.extend({
       model:Organization
    });

    var OrganizationView = Backbone.View.extend({
        tagName:"div",
        className:"organization-container",
        template:$("#organization-template").html(),

        render:function () {
            var tmpl = _.template(this.template); //tmpl is a function that takes a JSON object and returns html

            this.$el.html(tmpl(this.model.toJSON())); //this.el is what we defined in tagName. use $el to get access to jQuery html() function
            return this;
        }
    });


 var OrganizationsView = Backbone.View.extend({
        el:$("#organizations"),

        initialize:function(){
            this.collection = new Organizations(organizations);
            this.render();
        },

        render: function(){
            var that = this;
            _.each(this.collection.models, function(item){
                that.renderOrganization(item);
            }, this);
        },

        renderOrganization:function(item){
            var organizationView = new OrganizationView({
                model: item
            });
            this.$el.append(organizationView.render().el);
        }
    });

    var organizationsView = new OrganizationsView();



    var OrganizationDataView = Backbone.View.extend({
        tagName:"div",
        className:"orgData-container",
        template:$("#organization-data-template").html(),

        render:function () {
            var tmpl = _.template(this.template); //tmpl is a function that takes a JSON object and returns html

            this.$el.html(tmpl(this.model.toJSON())); //this.el is what we defined in tagName. use $el to get access to jQuery html() function
            return this;
        }
    });


	// Router
	var AppRouter = Backbone.Router.extend({

	    routes:{
	        "":"list",
	        "organizations/:id":"orgData"
	    },

	    list:function () {
	        this.wineList = new WineCollection();
	        this.wineListView = new WineListView({model:this.wineList});
	        this.wineList.fetch();
	        $('#organizations-container').html(this.wineListView.render().el);
	    },

	    orgData:function (id) {
	        this.wine = this.wineList.get(id);
	        this.wineView = new WineView({model:this.wine});
	        $('#orgData-container').html(this.wineView.render().el);
	    }
	});

	var app = new AppRouter();
	Backbone.history.start();

})(jQuery);