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
			"/manager/user/auth?username=" + $('#username').val() +
			"&password=" + $('#password').val();
		//console.log(requestUrl); //firebug console output
//		$.getJSON(requestUrl,
//				function(data) {
//					console.log(this.data); //firebug console output
//					$("#msg").html(data.username);
//					$("#admin").show();
//		});
		$.ajax({
		    type: "GET",
		    url: requestUrl,
//		    dataType: "json",
//		    data: "{" +
//		        "\"session\" : {" +
//		            "\"username\" : [\"$('#username').val()\"]," +
//		            "\"password\" : \"$('#password').val()\"" +
//		        "}" +
//		    "}",
//		    contentType: "application/json; charset=UTF-8",
		    success: function(msg){
		        //console.log(msg);
		        //console.log("success");
		        if(!msg){
		        	$("#loading").hide();
		        	$("#msg").show();
		        }
		    	else if(msg.msg == "1") location.href = "/AppServer/#/adminOverview";
		    	else if(msg.msg == "2") location.href = "/AppServer/#/clientOverview";
		    },
		    error: function(xhr, msg) { 
		    	console.log(msg + '\n' + xhr.responseText);
		    	if(!xhr.responseText){
		        	$("#loading").hide();
		        	$("#msg").show();
		        }
		    	else if(xhr.responseText.msg == "1") location.href = "/AppServer/#/adminOverview";
		    	else if(xhr.responseText.msg == "2") location.href = "/AppServer/#/clientOverview";
		    }
		});
	});
})(jQuery);



