const MILE_TO_METER = 1609.34; // Conversion factor from mile to meter
const OK = 200; // Stataus Code
// Google Place API KEY
const API_KEY = "AIzaSyA54N1G3zEKpF_yXHfOexbfIA-r_mz0MwQ";
// Yelp API KEY
const YELP_KEY = "dLJxivcvnZx3DBQcxHngURmUuTm6or_XcrxLNZVvc6ST6FR3xCR4HUiZB4SY7N4NwR6y2O_nbUZc7dQyB3BfRmj81hT1D-a_X_fQrLYJfB-jY_EN1oIY1jkdC9TFWnYx";
// Google GeoCode
const GEOCODE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
// Goolge Place nearyby url for maps and locations
const NEARBY_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
// Google Place detail url for reviews and photos
const DETAIL_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
// Google Photo url
const HDPHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?";

// Yelp Best Match
const YBEST_MATCH_URL = "https://api.yelp.com/v3/businesses/matches/best?";
// Yelp Reviews
const YREVIEW_URL = "https://api.yelp.com/v3/businesses/";

var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');
var morgan = require('morgan');
var cors = require('cors');
var request = require('request');
var yelp = require('yelp-fusion');
//var Yelp = require('yelpv3');
var port = process.env.PORT || '3000';

var app = express();
var yelpClient = yelp.client(YELP_KEY);

// Corss Origin Control 
app.use(cors());

// Log stauts
app.use(morgan('dev'));
// Parse file
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: false}));

// Set Static Path
app.use(express.static(path.join(__dirname, 'public')));

function errorMsg(process){
	return "Error in receiving " + process + " information from API."
}

function getContentFromServer(api_url, qryStr, callback){
	var req_url = api_url + qryStr;
	request(req_url, function(err, response, body){
		if(err == null && response.statusCode == OK){
			return callback(null, body);
		}
		else{
			return callback(err, null);
		}	 
	});
}

// Get basic page data information 
function getPageData(inData){
	var outData = new Object();
	var inDataResult = inData.results == undefined ? inData : inData.results;
	// next page token
	outData.nextPage = inData.next_page_token == undefined ? null : inData.next_page_token;

	// each location nearby info
	for (var i in inDataResult){
		var basicInfo = new Object ();
		basicInfo.mapLat = inDataResult[i].geometry.location.lat;
		basicInfo.mapLon = inDataResult[i].geometry.location.lng;
		basicInfo.icon = inDataResult[i].icon;
		basicInfo.name = (inDataResult[i].name);
		basicInfo.vicinity = inDataResult[i].vicinity;
		basicInfo.place_id = inDataResult[i].place_id;

		//		.replace(/\'/g,'&#39')
		outData[i] = basicInfo;
	}

	return outData;
}

// get lat and lon
function getLoc(location, callback){
	try{ // searched from here
		return callback(JSON.parse(location));
	}
	catch (e){ // searched from a specify location
		var geoQryStr = "address="+location+"&key="+API_KEY;
		var res = new Object();
		getContentFromServer(GEOCODE_URL, geoQryStr, function(err, data){
			try{
				var geoInfo = JSON.parse(data);
				var spcLoc = geoInfo.results[0].geometry.location;
				res.lat = spcLoc.lat;
				res.lon = spcLoc.lng;

				return callback(res);
			}
			catch(e){
				return callback(false);
			}
		});
	}
}

// get nearby info
function getNearBy(lat, lon, distance, category, keyword, callback){
	var nearByQryStr = "location="+lat+","+lon+"&radius="+distance+"&type="+category+"&keyword="+keyword+"&key="+API_KEY;
	//	console.log("NearByQryStr = " + nearByQryStr);
	getContentFromServer(NEARBY_URL, nearByQryStr, function(err, data){
		//		console.log("Receive Error: " + err);
		//		console.log("Receive Result: " + data);
		try{
			var nearByInfo = JSON.parse(data);
			var nearByData = getPageData(nearByInfo);

			return callback(nearByData);
		}
		catch(e){
			return callback(false);
		}
	});
}

function getPage(pageToken, callback){
	var pageQryStr = "pagetoken=" + pageToken + "&key=" + API_KEY;
	var res = new Object();
	getContentFromServer(NEARBY_URL, pageQryStr, function(err, data){
		try{
			var pageInfo = JSON.parse(data);
			var pageData = getPageData(pageInfo);
			return callback(pageData);
		}
		catch(e){
			return callback(false);
		}
	});
}


// Get basics information
function getBascis(){
	app.get('/basics', function(req, res){
		res.header("Content-Type", "application/json");
		var result = new Object();
		var keyword = req.query.keyword;
		var category = req.query.category == "Default" ? "default" : req.query.category;
		var distance = req.query.distance * MILE_TO_METER;
		var location = req.query.location;

		try{
			// get basics info
			getLoc(location, function(locData){
				if(locData == false){
					res.send(errorMsg("location"));
				}
				else{			
					result.geoLoc = locData; // lat and lon data
					// get nearBy info
					getNearBy(result.geoLoc.lat, result.geoLoc.lon, distance, category, keyword, function(nearByData){
						if(nearByData == false){
							res.send(errorMsg("nearby"));
						}
						else{
							result.nearBy = nearByData;
							res.send(JSON.stringify(result, null, 4));
						}
					});
				}

			});
		}
		catch (error){
			res.send("API Server Error.");
		}
	});
}

function getPageResult(){
	app.get('/page', function(req, res){
		res.header("Content-Type", "application/json");
		var result = new Object();
		var pageToken = req.query.pageToken;

		console.log("pageToken: " + pageToken);

		try{
			//get page data
			getPage(pageToken, function(pageData){
				if(pageData == false){
					res.send(errorMsg("page"));
				}
				else{
					console.log("page: " + pageData);
					res.send(JSON.stringify(pageData, null, 4));
				}
			})
		}
		catch(error){
			res.send("API Server Error.");
		}
	});
}

function getYelpBestMatch(){
	app.get('/bestMatch', function(req, res){
		console.log("entered yelp best match search");
		res.header("Content-Type", "application/json");
		var yelpRequest = {
			name: req.query.name,
			address1: req.query.addr, 
			city: req.query.city,
			state: req.query.state,
			country: req.query.country,
			latitude: req.query.latitude,
			longtitude: req.query.longtitude
		};

		console.log(yelpRequest);

		yelpClient.businessMatch('best', yelpRequest).then(response => {
			try{
				console.log("getting business Info");
				console.log(response.jsonBody.businesses[0]);
				var businessID = response.jsonBody.businesses[0].id;
				// getting yelp reivew
				yelpClient.reviews(businessID).then(reviewRes => {
					console.log("getting review Info");
					var yelpReviewRes = reviewRes.jsonBody.reviews;
					console.log(yelpReviewRes);
					res.send(yelpReviewRes);
				}).catch(reviewErr => {
					console.log(reviewErr);
					res.send(null);
				});
			}
			catch(err){
				res.send(null);
			}
		}).catch(e => {
			console.log(e);
			res.send(null);
		});
	});
}

// Ajax Calls
try{
	getBascis();
	getPageResult();
	getYelpBestMatch();
}
catch (error) {
	console.log("Ajax Request Reaches Short.");
}

// Listen port
app.listen(port, function(){
	console.log("Server is running on port: " + port);
});

