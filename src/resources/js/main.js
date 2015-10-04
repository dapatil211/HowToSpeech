/**
 * File: main.js
 * Author: cookiesui
 * Date: 03 September 2015
 * Description: JS to call backend
 */

var BASE_URL = "http://localhost:8080/";
var START_P_ID = "start-instructions";
var STOP_P_ID = "stop-instructions";
var START_BTN_ID = "start-btn";
var STOP_BTN_ID = "stop-btn";

var id = 0;


$(document).ready(function(){
	$("#"+START_BTN_ID).click(function(){startRecord()})
});
$(document).ready(function(){
	$("#"+STOP_BTN_ID).click(function(){stopRecord()})
});


function startRecord ()
{
	$.get(BASE_URL+"record", {action:"start"}, function(data){
    	id = data["user_id"];
  	}, "json");

	$("#"+START_P_ID)[0].style.display = "none";
	$("#"+STOP_P_ID)[0].style.display = "block";
}

function stopRecord ()
{
	$.get(BASE_URL+"record", {action:"stop", user_id:id}, function(data){
    	writeResults(data);
	}, "json");

	$("#"+STOP_P_ID)[0].style.display = "none";
	$("#"+START_P_ID)[0].style.display = "block";
}

function writeResults (data)
{
	$("#speech_txt").val(data["speech"]);
	$("#movements_img").attr("src", data["movement_graph"]);
	$("#volume_img").attr("src", data["volume_graph"]);
}
