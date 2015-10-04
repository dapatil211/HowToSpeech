/**
 * File: main.js
 * Author: cookiesui
 * Date: 03 September 2015
 * Description: JS to call backend
 */

var BASE_URL = "http://localhost/";
var START_P_ID = "start-instructions";
var STOP_P_ID = "stop-instructions";
var START_BTN_ID = "start-btn";
var STOP_BTN_ID = "stop-btn";

var id;


$(document).ready(function(){
	$("#"+START_BTN_ID).click(function(){startRecord()})
});
$(document).ready(function(){
	$("#"+STOP_BTN_ID).click(function(){stopRecord()})
});


function startRecord ()
{
	$.get(BASE_URL+"record", {action:"start"}, function(data){id = data["user_id"]});

	$("#"+START_P_ID)[0].style.display = "none";
	$("#"+STOP_P_ID)[0].style.display = "block";
}

function stopRecord ()
{
	$.get(BASE_URL+"/record", {action:"stop", user_id:id}, function(data){console.log("Stopped"); writeResults(data)});

	$("#"+STOP_P_ID)[0].style.display = "none";
	$("#"+START_P_ID)[0].style.display = "block";
}

function writeResults (data)
{

}