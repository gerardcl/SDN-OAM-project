var session ={};

function getSession(){
	return session;
}

(function($){
	$(document).ready(function(){
		$("#msg").hide();
		$("#loading").hide();
		$("#username").focus();
	});

	//$("#submit").click($("#msg").show());
	$("#msg").click(function(){
		$("#msg").hide();
	});



	$('#check').click(function() {
		$("#msg").hide();
		$("#loading").show();

		console.log("submitting user and password to server...");
		var requestUrl =
			"/AppServer/webapi/manager/user/auth?username=" + $('#username').val() +
			"&password=" + $('#password').val();
		//console.log(requestUrl); //firebug console output
//		$.getJSON(requestUrl,
//		function(data) {
//		console.log(this.data); //firebug console output
//		$("#msg").html(data.username);
//		$("#admin").show();
//		});
		$.ajax({
			type: "GET",
			url: requestUrl,
//			dataType: "json",
//			data: "{" +
//			"\"session\" : {" +
//			"\"username\" : [\"$('#username').val()\"]," +
//			"\"password\" : \"$('#password').val()\"" +
//			"}" +
//			"}",
			//contentType: "application/vmd.dxat.appserver.manager.user.collection+json",
			success: function(msg){
				//console.log(msg);
				session = msg;
				if(!msg){
					$("#loading").hide();
					$("#msg").show();
				}
				else if(msg.msg == "1"){
					location.href = "/AppServer/#/adminOverview";
					loginUser = msg.userId;
					loginOrg = msg.orgId;
					loginOrgName = msg.orgName;
					loginUserName = msg.userName;
				} 
				else if(msg.msg == "2") {
					location.href = "/AppServer/#/clientOverview/"+msg.orgId;
					loginUser = msg.userId;
					loginOrg = msg.orgId;
					loginOrgName = msg.orgName;
					loginUserName = msg.userName;
				} 
			},
			error: function(xhr, msg) { 
				console.log(msg + '\n' + xhr.responseText);
				session = xhr.responseText;
				if(!xhr.responseText){
					$("#loading").hide();
					$("#msg").show();
				}
				else if(xhr.responseText.msg == "1"){
					location.href = "/AppServer/#/adminOverview";
					loginUser = xhr.responseText.userId;
					loginOrg = xhr.responseText.orgId;
					loginOrgName = xhr.responseText.orgName;
					loginUserName = xhr.responseText.userName;
				} 
				else if(xhr.responseText.msg == "2"){
					location.href = "/AppServer/#/clientOverview/"+xhr.responseText.orgId;
					loginUser = xhr.responseText.userId;
					loginOrg = xhr.responseText.orgId;
					loginOrgName = xhr.responseText.orgName;
					loginUserName = xhr.responseText.userName;
				} 
			}
		});
	});
})(jQuery);



