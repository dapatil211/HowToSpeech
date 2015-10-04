/**
 * File: main.js
 * Author: cookiesui
 * Date: 03 September 2015
 * Description: JS to call backend
 */

var BASE_URL = "http://localhost";
var START_INSTRUCTIONS_HTML_ID = "start-instructions";
var STOP_INSTRUCTIONS_HTML_ID = "stop-instructions";
var id;

function startRecord ()
{
	$.get(BASE_URL, {action:"start"}, function(data){id = data["user_id"]});

	document.getElementById(START_INSTRUCTIONS_HTML_ID).style.display = "none";
	document.getElementById(STOP_INSTRUCTIONS_HTML_ID).style.display = "block";
}

function stopRecord ()
{
	$.get(BASE_URL, {action:"stop", user_id:id}, function(data){console.log("Stopped"); writeResults(data)});

	document.getElementById(STOP_INSTRUCTIONS_HTML_ID).style.display = "none";
	document.getElementById(START_INSTRUCTIONS_HTML_ID).style.display = "block";
}

function writeResults (data)
{

}