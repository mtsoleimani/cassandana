/*
 *  Copyright 2019 Mohammad Taqi Soleimani
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 */

require('http').globalAgent.maxSockets = Infinity
require('https').globalAgent.maxSockets = Infinity
var express = require('express');
var app = express();

var bodyParser = require('body-parser');
app.use(bodyParser.json());

const HTTP_SERVER_PORT = 9999;

const debug = true;

var server = app.listen(HTTP_SERVER_PORT, function () {
	var host = server.address().address;
	var port = server.address().port;
	if(debug) console.log('http server is listening at http://%s:%s', host, port);
});


app.post('/mqtt/auth', function (request, response) {
	if(debug) console.log(request.body);
	response.contentType('text/plain');
	response.sendStatus(200).end();
});


app.post('/mqtt/acl', function (request, response) {
	if(debug) console.log(request.body);
	response.contentType('text/plain');
	response.sendStatus(200).end();
});	


