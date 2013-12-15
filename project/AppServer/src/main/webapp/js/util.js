(function($){
	$(document).ready(function(){
		$("#msg").hide();//.append("for each here, per mac and so on!<p></p>");
		$("#admin").hide();
		$("#client").hide();

	});

	//$("#submit").click($("#msg").show());

	$('#check').click(function() {
		$("#msg").hide();//.append("for each here, per mac and so on!<p></p>");
		$("#admin").hide();
		$("#client").hide();
		console.log("submitting user and password to server...");
		var requestUrl =
			"/AppServer/webapp/manager/user/auth?username=" + $('#username').val() +
			"&password=" + $('#password').val();
		console.log(requestUrl); //firebug console output
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
		        console.log(msg);
		        console.log("success");
		        if(msg == "0") $("#msg").show();
		    	else if(msg == "1") $("#admin").show();
		    	else if(msg == "2") $("#client").show();
		    },
		    error: function(xhr, msg) { 
		    	console.log(msg + '\n' + xhr.responseText);
		    	if(xhr.responseText == "0") $("#msg").show();
		    	else if(xhr.responseText == "1") $("#admin").show();
		    	else if(xhr.responseText == "2") $("#client").show();
		    }
		});
	});




})(jQuery);



