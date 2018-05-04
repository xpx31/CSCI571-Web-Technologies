// Initializing autocomplete
// Autocomplete
function initAutocomplete() {
	var autocomplete1 = new google.maps.places.Autocomplete(
		(document.getElementById('search_location')),
		{types:['geocode']});
	var autocomplete1 = new google.maps.places.Autocomplete(
		(document.getElementById('mapOrigin')),
		{types:['geocode']});
};

var placeApp = angular.module('formInputs', ['ngAnimate']);
placeApp.constant("moment", moment);

// Controller
placeApp.controller('formController', function($scope, $http, $compile, $timeout, $window, moment){
	$scope.master = {};
	$scope.awsURL = 'http://place.pmx3jxpte5.us-west-1.elasticbeanstalk.com';
////		$scope.awsURL = 'http://localhost:3000';
	$scope.MAP_LOGO = "http://cs-server.usc.edu:45678/hw/hw8/images/Map.png";
	$scope.PEGMAN_LOGO = "http://cs-server.usc.edu:45678/hw/hw8/images/Pegman.png";
	$scope.TWITTER_LOGO = "http://cs-server.usc.edu:45678/hw/hw8/images/Twitter.png";

	$scope.allPageInfo = []; // all basics info
	$scope.allDetailsInfo = {}; // all details info of an entry
	$scope.favEntries = []; // all favorites 
	$scope.favTemp = []; 
	$scope.openHoursInfo = {}; // open hours
	$scope.photoURL = {}; // photo urls
	$scope.reviewDetail = []; // all reviews info
	$scope.yelpReviewDetail = [];
	$scope.yelpReview = {}; // Yelp reivew
	$scope.suggestedRoute = {}// all suggested route
	$scope.routeDetail = {}; // all route info
	$scope.yelpReviews = []; // yelp reviews
	$scope.storedFav = {};
	$scope.favShowingEntry = {};

	$scope.hereLoc; // here location
	$scope.nearByData; // Nearby Data from server
	$scope.placeDetails; // Place details from server
	$scope.fullStars = 5; // Full Star for Ratings
	$scope.entryMax = 20; // Max entries per page
	$scope.pageNumber = 0;
	$scope.favPageNumber = 0;
	$scope.favPageMax = 0;
	$scope.favEntryNum = 0;
	$scope.favSaveToken = 0;
	$scope.detailEntry;
	$scope.nextAvaliable;
	$scope.previousAvaliable;
	$scope.entryLat;
	$scope.entryLon;
	$scope.entryPlaceID;
	$scope.priceLevel = "";
	$scope.starPercent = "";
	$scope.hours = "";
	$scope.from_addr = "";
	$scope.to_addr = "";
	$scope.dirServices;
	$scope.dirDisplay;
	$scope.mapMarker;
	$scope.panorama;
	$scope.pegmanLogo;
	$scope.mapLogo;
	$scope.reviewSource = "Google Reviews"; // Default review source value
	$scope.sortOrder = "Default Order"; // Default sorting order 
	$scope.hereLocFound = false;
	$scope.detailBtnTrap = true;
	$scope.favSaved = false;


	$scope.categoryInfo = ["Address", "Phone Number", "Price Level", 
						   "Rating", "Google Page", "Website", "Hours"];
	$scope.categoryInfoAttr = ["formatted_address", "international_phone_number", "price_level", 
							   "rating", "url", "website", "opening_hours"];
	for($scope.i = 0 ; $scope.i < $scope.categoryInfo.length; $scope.i++){
		$scope.allDetailsInfo[$scope.i] = {"title": $scope.categoryInfo[$scope.i],
										   "attr": $scope.categoryInfoAttr[$scope.i]};
	}

	$scope.reviewPlatform = ["Google Reviews", "Yelp Reviews"];
	$scope.sortingOrder = ["Default Order", "Highest Rating", "Lowest Rating", "Most Recent", "Least Recent"]

	$scope.travelModeOptions = [{
		value: '0',
		label: 'Driving'
	},{
		value: '1',
		label: 'Bicycling'
	},{
		value: '2',
		label: 'Transit'
	},{
		value: '3',
		label: 'Walking'
	}];

	$scope.categoryOptions = [{
		value: '0',
		label: 'Default'
	},{
		value: '1',
		label: 'Airport'
	},{
		value: '2',
		label: 'Amusement Park'
	},{
		value: '3',
		label: 'Aquarium'
	},{
		value: '4',
		label: 'Art Gallery'
	},{
		value: '5',
		label: 'Bakery'
	},{
		value: '6',
		label: 'Bar'
	},{
		value: '7',
		label: 'Beauty Salon'
	},{
		value: '8',
		label: 'Bowling Alley'
	},{
		value: '9',
		label: 'Bus Station'
	},{
		value: '10',
		label: 'Cafe'
	},{
		value: '11',
		label: 'Campground'
	},{
		value: '12',
		label: 'Car Rental'
	},{
		value: '13',
		label: 'Casino'
	},{
		value: '14',
		label: 'Lodging'
	},{
		value: '15',
		label: 'Movie Theater'
	},{
		value: '16',
		label: 'Museum'
	},{
		value: '17',
		label: 'Night Club'
	},{
		value: '18',
		label: 'Park'
	},{
		value: '19',
		label: 'Parking'
	},{
		value: '20',
		label: 'Restaurant'
	},{
		value: '21',
		label: 'Shopping Mall'
	},{
		value: '22',
		label: 'Stadium'
	},{
		value: '23',
		label: 'Subway Station'
	},{
		value: '24',
		label: 'Taxi Stand'
	},{
		value: '25',
		label: 'Train Station'
	},{
		value: '26',
		label: 'Transit Station'
	},{
		value: '27',
		label: 'Travel Agency'
	},{
		value: '28',
		label: 'Zoo'
	}];

	$scope.category = $scope.categoryOptions[0];

	// update entry
	$scope.update = function(entry){
		$scope.master = angular.copy(entry);
	};

	// Get Here Location 
	$scope.getHereLocation = function(){
		// Getting IP Address
		try{
			var url = "http://ip-api.com/json";
			var request = new XMLHttpRequest();
			request.open('POST', url, false);
			request.send();

			var jsonFile = request.responseText;
			var data = JSON.parse(jsonFile);
			document.getElementById('lat').value = data.lat;
			document.getElementById('lon').value = data.lon;
			$scope.hereLoc = {lat: data.lat, lon: data.lon};
			$scope.locErr = false;}
		catch(e){
			$scope.locErr = true;
			$scope.buttomPage = false;
			$scope.errorMsg = true; 
		}
		//		document.getElementById('lat').value = "34.0523";
		//		document.getElementById('lon').value = "-118.3852";
		//		$scope.hereLoc = {lat: "34.0523", lon: "-118.3852"};
		//		$scope.hereLocFound = true;
		//		console.log("Here Loction is Found: " + $scope.hereLocFound);
		//		console.log("hereLoc: " + $scope.hereLoc.lat +", "+ $scope.hereLoc.lon)
	};

	$scope.getHereLocation();

	// AJAX to Nodejs
	$scope.send = function(form){
		console.log("Submitting Form to Server");
		$scope.bottomPage = true;
		$scope.resultsShow = true;
		$scope.resultInfo = false;
		$scope.favShow = false;
		$scope.noRecordMsg = false;
		$scope.errorMsg =false;
		$scope.detailEntry = 100; // reset Deatil Entry bg color
		$scope.favSaved = false;
		$scope.openHoursInfo = {}; // open hours
		$scope.photoURL = {}; // photo urls
		$scope.reviewDetail = []; // all reviews info
		$scope.yelpReviewDetail = [];
		$scope.yelpReview = {}; // Yelp reivew
		$scope.suggestedRoute = {}// all suggested route
		$scope.routeDetail = {}; // all route info
		$scope.yelpReviews = []; // yelp reviews
		$scope.allPageInfo = []; // Initialize page info
		$scope.nearByData; // Initialize nearby data

		$scope.search_keyword = (form.keyword.$viewValue).replace(/\s/g, '_');
		$scope.search_category = form.category.$viewValue.label === undefined ? $scope.categoryOptions[0] : (form.category.$viewValue.label).replace(/\s/g, '_');
		$scope.search_distance = form.distance.$viewValue === undefined ? "10" : form.distance.$viewValue;
		$scope.search_location = form.location.$viewValue == null ? $scope.hereLoc : (form.location.$viewValue).replace(/\s/g, '_');

		$scope.progressBar = true;

		console.log("All Page Info Length: ");
		console.log($scope.allPageInfo.length);

		$http({
			method: 'GET',
			url: $scope.awsURL + '/basics',
			params: {keyword : $scope.search_keyword,
					 category: $scope.search_category,
					 distance: $scope.search_distance,
					 location: $scope.search_location}
		}).then(
			function (response){
				console.log("Response received successfully");
				$scope.allPageInfo[0] = response;
				angular.forEach($scope.allPageInfo[0].data.nearBy, function(value, key){
					if(value != undefined){
						value.isInFav = false; // Initilize Fav Data
					}
				});
				console.log("All Page Info: ");
				console.log($scope.allPageInfo[0]);
				$scope.listBasics(0, 0); // list first page data for Results
			},
			function (error){
				console.log("Error in sending request or recieving response");
				$scope.progressBar = false;
				$scope.errorMsg = true;
			});
	}

	// AJAX to Nodejs
	$scope.getYelpReview = function(){
		console.log("Submitting Form to Yelp Server");
		//		console.log($scope.placeDetails);
		$scope.name = $scope.placeDetails.name;
		$scope.city; $scope.state; $scope.country;
		$scope.yelpLat = $scope.nearByData[$scope.detailEntry].mapLat;
		$scope.yelpLon = $scope.nearByData[$scope.detailEntry].mapLon;

		$scope.addrCmp = $scope.placeDetails.address_components;
		for($scope.i = 0 ; $scope.i < $scope.addrCmp.length ; $scope.i++){
			if($scope.addrCmp[$scope.i].types[0] == "locality"){
				$scope.city = $scope.addrCmp[$scope.i].long_name;
			}
			else if($scope.addrCmp[$scope.i].types[0] == "administrative_area_level_1"){
				$scope.state = $scope.addrCmp[$scope.i].short_name;
			}
			else if($scope.addrCmp[$scope.i].types[0] == "country"){
				$scope.country = $scope.addrCmp[$scope.i].short_name;
			}
		}

		$scope.addr = $scope.placeDetails.formatted_address == undefined ? "": ($scope.placeDetails.formatted_address).split(',')[0];
		console.log($scope.addr);

		$http({
			method: 'GET',
			url: $scope.awsURL + '/bestMatch',
			params: {name : $scope.name,
					 addr : $scope.addr,
					 city: $scope.city,
					 state: $scope.state,
					 country: $scope.country,
					 latitude: $scope.yelpLat,
					 longtitude: $scope.yelpLon}
		}).then(
			function (response){
				console.log("Response received successfully");
				if(response == null || response == undefined || response.data == ""){
					$scope.yelpReviews.data = undefined;
				}
				else{
					$scope.yelpReviews = response.data;
					console.log($scope.yelpReviews);
				}
			},
			function (error){
				console.log("Error in sending request or recieving response");
				$scope.yelpReview = undefined;
			});
	}

	$scope.showYelpReview = function(){
		$scope.reviewContentShow = false; // fade out review content
		$scope.yelpReviewShow = true;
		$scope.googleReviewShow = false;
		$scope.reviewContentShow = true; // fade in review content
		$scope.showReviews(1); // Yelp Review
	}

	$scope.showGoogleReview = function(){
		$scope.reviewContentShow = false; // fade out review content
		$scope.googleReviewShow = true;
		$scope.yelpReviewShow = false;
		$scope.reviewContentShow = true;
		$scope.showReviews(0); // Google Review;
	}

	// List the Basics Information
	$scope.listBasics = function(mode, page){
		console.log("Listing Basics");
		console.log("All Page Info ");
		console.log($scope.allPageInfo);
		console.log("mode: " + mode);
		console.log("page: " + page);

		$scope.noRecordMsg = false;
		$scope.errorMsg = false;

		if(mode == 0){ // Results
			$scope.listResults(page);
		}
		else{ // Fav
			$scope.listFav(page);
		}
	}

	// list all results
	$scope.listResults = function(page){
		if($scope.allPageInfo[page] != undefined){
			$scope.detailsInfo = false; // hidding detailsInfo
			$scope.basicViewInfo = false; // hidding old basicViewInfo
			$scope.favShow = false; // hidding fav
			$scope.progressBar = false; // hidding progress bar
			$scope.resultInfo = true; // show results page
			// Define NearByData
			$scope.nearByData = $scope.allPageInfo[page].data.nearBy == undefined ? $scope.allPageInfo[page].data : $scope.allPageInfo[page].data.nearBy;

			console.log("NearByData");
			console.log($scope.nearByData);

			if($scope.nearByData[0] == undefined){ // no record
				$scope.noRecordMsg = true;
				$scope.basicViewInfo = false;
			}
			else{ // show basics info
				$scope.nextAvaliable = $scope.nearByData.nextPage == undefined ? false : true;
				$scope.previousAvaliable = page < 1 ? false : true;

				$scope.basicViewInfo = true; // showing basicViewInfo
			}
		}
		else{ // no record 
			$scope.noRecordMsg = true;
		}
	}

	// list all favs
	$scope.listFav = function(page){
		$scope.detailsInfo = false; // hidding detailsInfo
		$scope.basicViewInfo = false; // hidding old basicViewInfo
		$scope.favInfo = false; // hidding old favInfo
		$scope.hasFav = $scope.getEntries(); // Get all entries from localstorage and return true if there is an entry

		if($scope.hasFav == false){
			$scope.noRecordMsg = true;
			$scope.favInfo = false;
			console.log("No showing fav entry");
		}
		else{
			$scope.favShowingEntry = $scope.favEntries[page]; // showing fav page
			$scope.nextAvaliable = $scope.favPageMax > page ? true : false;
			$scope.previousAvaliable = page < 1 ? false : true;

			$scope.favInfo = true; 
			console.log("Showing fav entry");
			console.log($scope.favEntries);
			$scope.update($scope.favEntries);
		}
	}

	// get all fav entries 
	$scope.getEntries = function(){
		$scope.hasFav = false;
		$scope.entryCount = 0; 
		$scope.j = -1; 

		for($scope.i = 0 ; $scope.i < $scope.favSaveToken; $scope.i++){
			$scope.favItem = JSON.parse(localStorage.getItem($scope.i));
			if($scope.favItem != undefined){
				$scope.hasFav = true;
				if($scope.entryCount % $scope.entryMax == 0){
					$scope.j++;
					$scope.favEntries[$scope.j] = [];
					$scope.favEntries[$scope.j][0] = $scope.favItem;
				}
				else{
					$scope.favEntries[$scope.j][$scope.entryCount % $scope.entryMax] = $scope.favItem;
				}
				$scope.entryCount++;
			}
		}

		$scope.favPageMax = $scope.j;
		return $scope.hasFav; 
	}

	// Go Back a Page
	$scope.prePage = function(mode){
		if(mode == 0){ // Resluts
			//		console.log("At page: " + $scope.pageNumber);
			$scope.pageNumber--;
			//		console.log("Turning to page: " + $scope.pageNumber);

			$scope.listBasics(mode, $scope.pageNumber);
		}else{ // Fav
			//		console.log("At page: " + $scope.favPageNumber);
			$scope.favPageNumber--;
			//		console.log("Turning to page: " + $scope.favPageNumber);

			$scope.listBasics(mode, $scope.favPageNumber);
		}
	}

	// Get Next Page
	$scope.nextPage = function(mode){
		if(mode == 0){ // Results
			//		console.log("At page: " + $scope.pageNumber);
			$scope.pageNumber++;
			//		console.log("Turning to page: " + $scope.pageNumber);
			$scope.nextPageToken = $scope.getNextPageToken($scope.pageNumber);
			//		console.log($scope.nextPageToken);

			if($scope.nextPageToken == false){
				$scope.listBasics(mode, $scope.pageNumber);
			}
			else{
				$http({
					method: 'GET',
					url: $scope.awsURL + '/page',
					params: {pageToken : $scope.nextPageToken}
				}).then(
					function (response){
						console.log("next page recieved.");
						console.log($scope.pageNumber);
						console.log(response);
						$scope.allPageInfo[$scope.pageNumber] = response;
						//					console.log("All Page Info: ");
						//					console.log($scope.allPageInfo[$scope.pageNumber]);
						angular.forEach($scope.allPageInfo[$scope.pageNumber].data, function(value, key){
							if(value != undefined){
								value.isInFav = false; // Initilize Fav Data
							}
						});
						$scope.listBasics(mode, $scope.pageNumber);
					},
					function (error){
						console.log("Error in sending request or recieving response");
						$scope.errorMsg = true;
					}
				)
			}
		}
		else{ // Fav
			$scope.favPageNumber++;
			$scope.listBasics(mode, $scope.favPageNumber);
		}
	}

	// Get next page token if page is not stored yet
	$scope.getNextPageToken = function(reqPageNum){
		console.log("All Page Info Length: ");
		console.log($scope.allPageInfo.length);
		console.log("reqPageNum: ");
		console.log(reqPageNum);

		$scope.nextPageToken;
		if(reqPageNum >= $scope.allPageInfo.length){
			$scope.nextPageToken = 
				$scope.allPageInfo[reqPageNum - 1].data.nearBy == undefined ? $scope.allPageInfo[reqPageNum - 1].data.nextPage : $scope.allPageInfo[reqPageNum - 1].data.nearBy.nextPage;
		}
		else{
			$scope.nextPageToken = false;
		}

		return  $scope.nextPageToken;
	}

	// Get Detail Info for the i'th Entry
	$scope.getDetail = function(i){
		console.log("showing details: " + i);
		console.log($scope.nearByData[i]);
		$scope.detailEntry = i; // detail entry that's been searched
		$scope.detailBtnTrap = false; // Release Detail Button Disabled
		$scope.openHoursInfo = {}; // open hours
		$scope.photoURL = {}; // photo urls
		$scope.reviewDetail = []; // all reviews info
		$scope.yelpReviewDetail = [];
		$scope.yelpReview = {}; // Yelp reivew
		$scope.suggestedRoute = {}// all suggested route
		$scope.routeDetail = {}; // all route info
		$scope.yelpReviews = []; // yelp reviews
		document.getElementById('nav-info-tab').click(); // Initialize Info tab active

		// Geo Location of the requested querry
		$scope.entryLat = $scope.nearByData[i].mapLat;
		$scope.entryLon = $scope.nearByData[i].mapLon;
		$scope.entryPlaceID = $scope.nearByData[i].place_id;

		$scope.nearByMap = new google.maps.Map(document.getElementById('map'),{
			center: {lat: $scope.entryLat, lng: $scope.entryLon},
			zoom: 14
		});

		$scope.nearByMapRequest = {
			placeId: $scope.entryPlaceID
		};

		$scope.progressBar = true;
		$scope.errorMsg = false;
		$scope.noRecordMsg = false;
		$scope.nearByService = new google.maps.places.PlacesService($scope.nearByMap);
		$scope.nearByService.getDetails($scope.nearByMapRequest, function callback(place, status){
			if(status == google.maps.places.PlacesServiceStatus.OK){
				$scope.placeDetails = place;
				$scope.showDetail();
				console.log("Details");
				console.log(place);
				console.log("Listed all Details");
				// Buffering Photos
				$scope.getPhotos();
				// Buffering Yelp Review
				$scope.getYelpReview();

			}
			else{
				$scope.errorMsg = true;
			}
		});
	}

	// Main View
	// Display Detail Information
	$scope.showDetail = function(){
		$scope.basicViewInfo = false; // hidding basicViewInfo
		$scope.progressBar = false;
		$scope.errorMsg = false;
		$scope.noRecordMsg = false;

		angular.forEach($scope.allDetailsInfo, function(value, key){
			if(angular.isObject(value)){
				value.data = $scope.placeDetails[value.attr];
			}
		});
		$scope.showInfoTable();
		$scope.detailsInfo = true; // showing detailsInfo

		// VERY IMPORTANT! Apply all the ng-show functions 
		$scope.$apply();
	}

	// Showing one of the four navtabs content
	$scope.turnOffOthers = function(showing){
		$scope.showingTags = [$scope.infoTableShow, $scope.photoShow, $scope.mapShow, $scope.reviewShow];
		for($scope.i = 0; $scope.i < $scope.showingTags.length; $scope.i++){
			if($scope.i == showing){
				$scope.showingTags[$scope.i] = true;
			}
			else{
				$scope.showingTags[$scope.i] = false;
			}
		}
		$scope.infoTableShow = $scope.showingTags[0];
		$scope.photoShow = $scope.showingTags[1];
		$scope.mapShow = $scope.showingTags[2];
		$scope.reviewShow = $scope.showingTags[3];
	}
	// Info Table
	// Display Info Table Details - Index 0
	$scope.showInfoTable = function(){
		$scope.infoTableShow = true;
		$scope.errorMsg = false;
		$scope.noRecordMsg = false;
		if($scope.placeDetails == undefined){
			$scope.noRecordMsg = true;
		}
		else{
			$scope.turnOffOthers(0);
			$scope.showPriceLevel($scope.categoryInfoAttr[2]);
			$scope.showRatings($scope.categoryInfoAttr[3]);
			$scope.showHours($scope.categoryInfoAttr[6]);
		}
	}

	// Display Price Level 
	$scope.showPriceLevel = function(attr){
		console.log("Showing Price Level");
		$scope.priceLevel = "";
		// Print $ sign according to the price level
		for($scope.i = 0 ;  $scope.i < $scope.placeDetails[attr]; $scope.i++){
			$scope.priceLevel += "$";
		}
	}

	// Display Ratings
	$scope.showRatings = function(attr){
		console.log("Showing Ratings");
		$scope.starCount = $scope.placeDetails[attr];
		$scope.starPercent = $scope.starCount / $scope.fullStars * 100 + "%";
		console.log("Star Percent: " + $scope.starPercent);
	}

	// Display Hours
	$scope.showHours = function(attr){
		console.log("Showing Hours");
		$scope.noRecordMsg = false;
		$scope.hours = "";
		if($scope.placeDetails.opening_hours != undefined){
			// Time Variables
			$scope.nowUTC = moment().utc();
			$scope.utc_offset = $scope.placeDetails.utc_offset;
			$scope.nowLocal = $scope.nowUTC.utcOffset($scope.utc_offset);
			$scope.nowLocalDay = $scope.nowLocal.day();
			$scope.nowOpenAndClose = $scope.getOpenAndCloseTime($scope.nowLocal);
			$scope.nowOpenStart = $scope.nowOpenAndClose.open;
			$scope.nowOpenClose = $scope.nowOpenAndClose.close;

			//				console.log("UTC_OFFSET: " + $scope.utc_offset);
			//				console.log("Current Time UTC: " + $scope.nowUTC.format("MM/DD/YYYY HH:mm:ss"));
			//				console.log("Current Time Local: " + $scope.nowLocal.format("MM/DD/YYYY HH:mm:ss"));
			//				console.log("Current Day: " + $scope.nowLocalDay);
			//				console.log("nowOpenStart: " + $scope.nowOpenStart);
			//				console.log("nowOpenEnd: " + $scope.nowOpenEnd);
			//		console.log("nowOpenStart: " + moment($scope.nowOpenStart).format("MM/DD HH:mm"));
			//		console.log("nowOpenEnd: " + moment($scope.nowOpenEnd).format("MM/DD HH:mm"));

			// Table HTML
			// Currently Closed
			if($scope.placeDetails[attr].open_now == false)	{
				$scope.hours += "Closed";
			}
			else{ // Currently Open
				$scope.hours += "Open now: ";
				$scope.hours += moment($scope.nowOpenStart).format("hh:mm A");
				$scope.hours += " - ";
				$scope.hours += moment($scope.nowOpenEnd).format("hh:mm A");
			}

			$scope.hours += "  " // Space in b/w

			$scope.saveDailyHour($scope.nowLocal);
		}
	}

	// Get Open and Close Time
	$scope.getOpenAndCloseTime = function(nowLocal){
		console.log("Getting Open and Close Time");
		$scope.nowOpenPeriods = $scope.placeDetails.opening_hours.periods;
		$scope.keepSearching = true;
		$scope.nowOpenStart;
		$scope.nowOpenEnd;
		$scope.entryIndex;

		//		for($scope.i in $scope.nowOpenPeriods){
		//			console.log("close on: " + moment($scope.nowOpenPeriods[$scope.i].close).format("ddd MM/DD HHmm"));
		//			console.log("open on: " + moment($scope.nowOpenPeriods[$scope.i].open).format("ddd MM/DD HHmm"));
		//			console.log("close next Date on: " + moment($scope.nowOpenPeriods[$scope.i].close.nextDate).format("ddd MM/DD HHmm"));
		//			console.log("open next Date on: " + moment($scope.nowOpenPeriods[$scope.i].open.nextDate).format("ddd MM/DD HHmm"));
		//		}

		for($scope.i in $scope.nowOpenPeriods){ // Loop through all opening and closing times			
			// Open 24 Hours
			if($scope.keepSearching == true && $scope.nowOpenPeriods[$scope.i].close == undefined){
				$scope.nowOpenStart = $scope.nowOpenPeriods[$scope.i].open;
				$scope.nowOpenEnd = $scope.nowOpenPeriods[$scope.i].open;
				$scope.entryIndex = $scope.i;
				$scope.keepSearching = false;
			}
			else if($scope.keepSearching == true && 
					nowLocal.isBefore($scope.nowOpenPeriods[$scope.i].close)){
				// Find opening time
				$scope.nowOpenStart = $scope.nowOpenPeriods[$scope.i].open;
				$scope.nowOpenEnd = $scope.nowOpenPeriods[$scope.i].close;
				$scope.entryIndex = $scope.i;
				$scope.keepSearching = false;
			}
		}

		return {"open": $scope.nowOpenStart, "close": $scope.nowOpenEnd};
	}

	// Save Daily Hours
	$scope.saveDailyHour = function(now){
		console.log("Showing Daily Hours");
		$scope.nowOpenPeriods = $scope.placeDetails.opening_hours.periods;

		for($scope.i in $scope.nowOpenPeriods){
			$scope.iterDay = moment(now).add($scope.i, "day").format("dddd");
			try{
				$scope.thisWeekOpen = $scope.nowOpenPeriods[$scope.i].open;
				$scope.thisWeekClose = $scope.nowOpenPeriods[$scope.i].close;
				$scope.nextWeekOpen = $scope.nowOpenPeriods[$scope.i].open.nextDate;
				$scope.nextWeekClose = $scope.nowOpenPeriods[$scope.i].close.nextDate;
			}
			catch (err){
				$scope.thisWeekOpen = $scope.nowOpenPeriods[$scope.i].open;
				$scope.thisWeekClose = $scope.nowOpenPeriods[$scope.i].open;
				$scope.nextWeekOpen = $scope.nowOpenPeriods[$scope.i].open;
				$scope.nextWeekClose = $scope.nowOpenPeriods[$scope.i].open;
			}
			$scope.openHoursInfo[$scope.i] = {};

			if(now.isAfter($scope.thisWeekOpen, "sec") && 
			   now.isBefore($scope.thisWeekClose, "sec")){
				$scope.time = moment($scope.thisWeekOpen).format("hh:mm A");
				$scope.time += " - ";
				$scope.time += moment($scope.thisWeekClose).format("hh:mm A");
			}
			else{
				$scope.time = moment($scope.nextWeekOpen).format("hh:mm A");
				$scope.time += " - ";
				$scope.time += moment($scope.nextWeekClose).format("hh:mm A");
			}

			$scope.openHoursInfo[$scope.i].order = $scope.i;
			$scope.openHoursInfo[$scope.i].day = $scope.iterDay 
			$scope.openHoursInfo[$scope.i].hour = $scope.time;
		}
		//		console.log($scope.openHoursInfo);
	}

	// Photo
	// Display Photos - Index 1
	$scope.showPhotos = function(){
		console.log("Showing Photos");
		$scope.errorMsg = false;
		$scope.noRecordMsg = false;
		$scope.photoShow = true;
		$scope.turnOffOthers(1);

		if($scope.placeDetails.photos == undefined || $scope.placeDetails.photos.length == 0){
			console.log("No Photo");
			$scope.noRecordMsg = true;
		}
		else{
			$scope.noRecordMsg = false;
		}
	}

	$scope.getPhotos = function() {
		if($scope.placeDetails.photos != undefined && $scope.placeDetails.photos.length != 0){
			$scope.photos = $scope.placeDetails.photos;

			console.log($scope.placeDetails.photos);

			for($scope.i in $scope.photos){
				$scope.photoURL[$scope.i] = {};
				$scope.photoURL[$scope.i].order = $scope.i;
				$scope.photoURL[$scope.i].link = $scope.photos[$scope.i].getUrl({'maxWidth': $scope.photos[$scope.i].width, 'maxHeight': $scope.photos[$scope.i].height});
			}

			console.log($scope.photoURL);
		}
	}

	// Map
	// Display Map - Index 2
	$scope.showMap = function(){
		console.log("Showing Map");
		$scope.errorMsg = false;
		$scope.noRecordMsg = false;
		$scope.mapShow = true;
		$scope.turnOffOthers(2);

		$scope.from_addr = angular.isObject($scope.search_location) ? "Your location" : $scope.search_location.replace(/_/g, ' ');
		$scope.to_addr = $scope.placeDetails.name + ", " + $scope.placeDetails[$scope.categoryInfoAttr[0]];

		$scope.initMap();
	}

	$scope.initMap = function(){
		console.log("drawing initial map");
		$scope.entryLoc = {lat: $scope.entryLat, lng: $scope.entryLon};
		$scope.dirServices = new google.maps.DirectionsService();
		$scope.dirDisplay = new google.maps.DirectionsRenderer();
		$scope.pegmanLogo = true;
		$scope.mapOptions = {
			zoom: 14,
			center: $scope.entryLoc
		}

		$scope.initialMap = new google.maps.Map(document.getElementById('map'), $scope.mapOptions);

		$scope.mapMarker = new google.maps.Marker({
			position: $scope.entryLoc,
			map: $scope.initialMap
		});

		$scope.panorama = $scope.initialMap.getStreetView();
		$scope.panorama.setPosition($scope.entryLoc);

		$scope.dirDisplay.setMap($scope.initialMap);
	}

	// Get Directions
	$scope.getDir = function(mapForm){
		console.log("Submitting Dir to Google Map API");

		$scope.mapOrigin = (mapForm.mapFrom.$viewValue).toLowerCase() == "my location" ? new google.maps.LatLng($scope.hereLoc.lat, $scope.hereLoc.lon) : (mapForm.mapFrom.$viewValue).replace(/\s/g, '+');
		$scope.mapDestine = $scope.to_addr;
		$scope.travelMode = mapForm.travelMode.$viewValue.label === undefined ? $scope.travelModeOptions[0] : (mapForm.travelMode.$viewValue.label);

		console.log("Map Origin: " + $scope.mapOrigin);
		console.log("Map To: " + $scope.mapDestine);
		console.log("Travel Mode: " + $scope.travelMode);

		$scope.request = {
			origin: $scope.mapOrigin,
			destination: $scope.mapDestine,
			travelMode: ($scope.travelMode).toUpperCase(),
			provideRouteAlternatives: true
		};

		$scope.dirServices.route($scope.request, function(result, status){
			if(status == 'OK'){
				$scope.mapMarker.setMap(null);
				$scope.dirDisplay.setDirections(result);
				$scope.dirDisplay.setPanel(document.getElementById("routes"));

				console.log(result);
				//				$scope.displayRoutes(result);
			}
		});
	}

	// Toggle Views between Street and Map View
	$scope.toggleView = function(reqView){
		if(reqView == "street"){ // Street View, Show Map LOGO
			$scope.pegmanLogo = false;
			$scope.mapLogo = true;

			$scope.panorama.setVisible(true);
		}
		else{ // Map View, Show Pegman LOGO
			$scope.pegmanLogo = true;
			$scope.mapLogo = false;

			$scope.panorama.setVisible(false);
		}
	}

	// Review
	// Display Reviews - Index 3
	$scope.showReviews = function(mode){
		console.log("Showing Reivews");
		$scope.errorMsg = false;
		$scope.noRecordMsg = false;
		$scope.turnOffOthers(3);

		if(mode == 0) { // Google Review
			console.log("Showing Google Review");
			$scope.reviewSource = "Google Review";
			$scope.reviewShow = true;
			$scope.reviewContentShow = false;
			$scope.googleReviewShow = true;
			$scope.yelpReviewShow = false; 

			$scope.progressBar = false;
			if($scope.placeDetails.reviews == undefined || $scope.placeDetails.reviews == undefined){
				$scope.noRecordMsg = true;
			}
			else{
				$scope.reviewShow = true;
				$scope.reviewContentShow = true;
				$scope.reviews = $scope.placeDetails.reviews;

				console.log($scope.reviews);

				for($scope.i in $scope.reviews){
					$scope.time = $scope.reviews[$scope.i].time * 1000;
					$scope.reviews[$scope.i].time = moment($scope.time).format("YYYY-MM-DD HH:mm:ss");
					console.log("Reviews: " +  $scope.reviews[$scope.i].rating);
				}

				$scope.reviewDetail = $scope.reviews;
				console.log($scope.reviewDetail);
			}
		}
		else{ // Yelp Reivew
			console.log("Showing Yelp Review");
			$scope.reviewSource = "Yelp Review";
			$scope.googleReviewShow = false;
			$scope.yelpReviewShow = true; 
			console.log($scope.yelpReviews);

			if($scope.yelpReviews == undefined){
				$scope.progressBar = false;
				$scope.errorMsg = true;
			}
			else if($scope.yelpReviews[0] == undefined){
				$scope.noRecordMsg = true;
			}
			else{
				for($scope.i = 0 ; $scope.i < $scope.yelpReviews.length ; $scope.i++){
					$scope.yelpReviewDetail[$scope.i] = {};
					$scope.yelpReviewDetail[$scope.i].author_url = $scope.yelpReviews[$scope.i].url;
					$scope.yelpReviewDetail[$scope.i].author_name = $scope.yelpReviews[$scope.i].user.name;
					$scope.yelpReviewDetail[$scope.i].profile_photo_url = $scope.yelpReviews[$scope.i].user.image_url;
					$scope.yelpReviewDetail[$scope.i].time = $scope.yelpReviews[$scope.i].time_created;
					$scope.yelpReviewDetail[$scope.i].text = $scope.yelpReviews[$scope.i].text;
					$scope.yelpReviewDetail[$scope.i].rating = $scope.yelpReviews[$scope.i].rating;
				}
				console.log("Yelp Review Detail");
				console.log($scope.yelpReviewDetail);
			}
		}
	}

	// Order by Obj
	$scope.orderBy = function(order){
		$scope.sortOrder = $scope.sortingOrder[order];
		$scope.orderReverse = order % 2 == 0 ? false : true;
		$scope.orderField = order == 1 || order == 2 ? "rating" : "time";
	}

	$scope.toggleFav = function(entry){
		$scope.stored = $scope.nearByData[entry].isInFav;
		//		$scope.stored = $scope.isStored($scope.pageNumber, entry);
		if($scope.stored == true){
			$scope.deleteFav($scope.pageNumber, entry, 0);
		}
		else{
			$scope.storeFav(entry);
		}
		console.log("favSaved: " + $scope.favSaved);
	}

	// Store Favorite
	$scope.storeFav = function(rstEntry){
		console.log("stored in fav");

		$scope.nearByData[rstEntry].isInFav = true;
		$scope.nearByData[rstEntry].saveToken = $scope.favSaveToken;
		$scope.savingEntry = $scope.nearByData[rstEntry];
		// local storage needs to be saved in string format 
		localStorage.setItem($scope.favSaveToken, JSON.stringify($scope.savingEntry),null, 4);

		$scope.favEntryNum++;
		$scope.favSaveToken++;
	}

	$scope.deleteFav = function(page, entry, mode){
		console.log("delete from fav: " + page + " entry: " + entry);
		$scope.favStar = false;

		if(mode == 0){ // deleted from result
			$scope.favSaveTokenEntry = $scope.allPageInfo[page].data.nearBy[entry].saveToken;
			localStorage.removeItem($scope.favSaveTokenEntry);
			if(page > 0){ // page 1+
				$scope.allPageInfo[page].data[entry].isInFav = false;
				$scope.allPageInfo[page].data[entry].saveToken = undefined;
			}
			else{ // page 0
				$scope.allPageInfo[page].data.nearBy[entry].isInFav = false;
				$scope.allPageInfo[page].data.nearBy[entry].saveToken = undefined;
			}
		}
		else{// deleted from fav page
			$scope.favSaveTokenEntry = $scope.favEntries[page][entry].saveToken;
			$scope.deletFromRst($scope.favSaveTokenEntry);
			localStorage.removeItem($scope.favSaveTokenEntry);
			$scope.listBasics(1, 0);
		}

		$scope.favEntryNum--;
	}

	$scope.deletFromRst = function(token){
		$scope.favEntryFound = false;
		console.log("Token number: " + token);
		console.log("all Page nearby length: " );
		console.log($scope.allPageInfo[0].data.nearBy);

		// find in the first page
		angular.forEach($scope.allPageInfo[0].data.nearBy, function(value, key){
			if(angular.isObject(value)){
				console.log("page info token: " + value.saveToken);
				if(value.saveToken == token){
					value.isInFav = false;
					value.saveToken = undefined;
					$scope.favEntryFound = true;
				}
			}
		});

		// find in later pages
		if($scope.favEntryFound == false){
			for($scope.page = 1; $scope.page < 2; $scope.page++){ // max result page = 3
				for($scope.entry = 0; $scope.entry < $scope.allPageInfo[$scope.page].data.length; $scope.entry++){
					if($scope.allPageInfo[$scope.page].data[entry].saveToken == token){
						$scope.allPageInfo[$scope.page].data[entry].isInFav = false;
						$scope.allPageInfo[$scope.page].data[entry].saveToken = undefined;
						$scope.favEntryFound = true;
					}
				}
			}
		}
		else{
			console.log("Entry was saved in other searches.");
		}

	}

	$scope.showFav = function(){
		$scope.resultsShow = false;
		$scope.favShow = true;

		$scope.listBasics(1, 0); // mode: fav, page: 0
	}

	$scope.showResult = function(){
		$scope.resultsShow = true;
		$scope.favShow = false;

		$scope.listBasics(0, 0); // mode: result, page: 0
	}

	// Open Tweeter
	$scope.tweet = function(){
		$scope.twitterURL = "https://twitter.com/intent/tweet/?";
		$scope.tweetQrt = "";
		// Check out <NAME> located at <ADDR>. Website: <URL>#TravelAndEntertainmentSearch
		$scope.tweetQrt += "text=Check out " + $scope.placeDetails.name;
		$scope.tweetQrt += " located at " + $scope.placeDetails.formatted_address + ". ";
		$scope.tweetQrt += "Website: ";
		$scope.tweetURL = $scope.placeDetails.website == undefined ? $scope.placeDetails.url : $scope.placeDetails.website;
		$scope.tweetURL = $scope.tweetURL.replace(/\//g, '%2F');
		$scope.tweetURL = $scope.tweetURL.replace(/:/g, '%3A');
		$scope.tweetQrt += $scope.tweetURL + " &hashtags=TravelAndEntertainmentSearch%2C";
		$scope.tweetQrt.replace(/\s/g, '%20');

		$scope.tweetReq = $scope.twitterURL + $scope.tweetQrt;

		$window.open($scope.tweetReq, "", "wdith=100%, height=100%");
	}


	// Reset From Spc Text Field
	$scope.resetFromSpc = function(form){
		console.log("resetting from Spc");
		form.location.$setPristine();
		form.location.$setUntouched();
		form.location.$invalid = false;
		$scope.scope.location = null;
	};

	// Reset
	$scope.reset = function(form){
		console.log("resetting form");
		if(form){
			form.$setPristine();
			form.$setUntouched();
			form.$invalid = true;
			$scope.from = 1;
			$scope.bottomPage = false;
			$scope.category = $scope.categoryOptions[0];
			$scope.favSaveToken = 0; 
			document.getElementById('basicInfo').innerHTML = "";
		}

		$scope.favEntries = angular.copy($scope.master);
	};
});


placeApp.filter('orderObjBy', function(){
	return function(items, field, reverse){
		var filtered = [];
		angular.forEach(items, function(item){
			filtered.push(item);
		});
		filtered.sort(function(a, b){
			return (a[field] > b[field] ? 1 : -1);
		});
		if(reverse) filtered.reverse();
		return filtered;
	};
});
