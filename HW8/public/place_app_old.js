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
	
	$scope.testBoolL = true;
	$scope.testBoolR = false;
	
	$scope.master = {};
	$scope.awsURL = 'http://place.pmx3jxpte5.us-west-1.elasticbeanstalk.com';
	//	$scope.awsURL = 'http://localhost:3000';
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
	$scope.yelpReview = {}; // Yelp reivew
	$scope.suggestedRoute = {}// all suggested route
	$scope.routeDetail = {}; // all route info
	$scope.yelpReviews = []; // yelp reviews
	//	$scope.response = {};
	$scope.hereLoc; // here location
	$scope.nearByData; // Nearby Data from server
	$scope.placeDetails; // Place details from server
	$scope.detailIndex; 
	$scope.fullStars = 5; // Full Star for Ratings
	$scope.entryMax = 20; // Max entries per page
	$scope.pageNumber = 0;
	$scope.favPageNumber = 0;
	$scope.favPageMax = 0;
	$scope.favEntryNum = 0;
	$scope.favSaveToken = 0;
	$scope.selectedEntry;
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
//		var url = "http://ip-api.com/json";
//		var request = new XMLHttpRequest();
//		request.open('POST', url, false);
//		request.send();
//
//		var jsonFile = request.responseText;
//		var data = JSON.parse(jsonFile);
//		document.getElementById('lat').value = data.lat;
//		document.getElementById('lon').value = data.lon;
//		$scope.hereLoc = {lat: data.lat, lon: data.lon};
				document.getElementById('lat').value = "34.0523";
				document.getElementById('lon').value = "-118.3852";
				$scope.hereLoc = {lat: "34.0523", lon: "-118.3852"};
		$scope.hereLocFound = true;
		console.log("Here Loction is Found: " + $scope.hereLocFound);
		console.log("hereLoc: " + $scope.hereLoc.lat +", "+ $scope.hereLoc.lon)
	};

	$scope.getHereLocation();

	// AJAX to Nodejs
	$scope.send = function(form){
		console.log("Submitting Form to Server");
		$scope.bottomPage = true;
		$scope.resultsShow = true;
		$scope.favShow = false;
		$scope.noRecordMsg = false;
		$scope.errorMsg =false;
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
	$scope.yelpReview = function(){
		console.log("Submitting Form to Yelp Server");
		//		console.log($scope.placeDetails);
		$scope.name = $scope.placeDetails.name;
		$scope.city; $scope.state; $scope.country;
		$scope.yelpLat = $scope.nearByData[$scope.detailIndex].mapLat;
		$scope.yelpLon = $scope.nearByData[$scope.detailIndex].mapLon;

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
				console.log(response);
				if(response == null || response == undefined){
					$scope.noRecordMsg = true;
				}
				else{
					$scope.yelpReviews = response;
					$scope.yelpReviewShow = true;
					$scope.googleReviewShow = false;
					$scope.showReviews();
				}
				//				$scope.allPageInfo[0] = response;
				//				console.log("All Page Info: ");
				//				console.log($scope.allPageInfo[0]);
				//				$scope.listBasics(0, 0); // list first page data for Results
			},
			function (error){
				console.log("Error in sending request or recieving response");
				$scope.progressBar = false;
				$scope.errorMsg = true;
			});
	}

	$scope.googleReview = function(){
		$scope.googleReviewShow = true;
		$scope.yelpReviewShow = false;
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
			if($scope.allPageInfo[page] != undefined){
				$scope.detailsInfo = false; // hidding detailsInfo
				$scope.basicViewInfo = false; // hidding old basicViewInfo
				$scope.favShow = false; // hidding fav
				$scope.progressBar = false; // hidding progress bar
				$scope.nearByData = $scope.allPageInfo[page].data.nearBy == undefined ? $scope.allPageInfo[page].data : $scope.allPageInfo[page].data.nearBy;

				if($scope.nearByData[0] == undefined){
					$scope.noRecordMsg = true;
					$scope.basicViewInfo = false;
				}
				else{
					$scope.nextAvaliable = $scope.nearByData.nextPage == undefined ? false : true;
					$scope.previousAvaliable = page < 1 ? false : true;

					$scope.basicViewInfo = true; // showing basicViewInfo
				}}
			else{
				$scope.noRecordMsg = true;
			}}
		else{ // Fav
			$scope.detailsInfo = false; // hidding detailsInfo
			$scope.basicViewInfo = false; // hidding old basicViewInfo
			$scope.favInfo = false; // hidding old favInfo

			console.log($scope.favEntries);
			console.log($scope.favEntries[0]);

			if($scope.favEntries == 0 || $scope.favEntries[0] == undefined){
				$scope.noRecordMsg = true;
				$scope.favInfo = false;
				//				console.log("favInfo: " + $scope.favInfo + "; " + "basicsViewInfo: " + $scope.basicViewInfo);
				//				console.log("favShow: " + $scope.favShow + "; " + "reviewShow: " + $scope.reviewShow);
				console.log("No showing fav entry");
			}
			else{
				$scope.nextAvaliable = $scope.favPageMax > page ? true : false;
				$scope.previousAvaliable = page > 1 ? true : false;

				$scope.favInfo = true; 
				console.log("Showing fav entry");
			}
		}
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
						//					console.log(response);
						$scope.allPageInfo[$scope.pageNumber] = response;
						//					console.log("All Page Info: ");
						//					console.log($scope.allPageInfo[$scope.pageNumber]);
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
		$scope.detailIndex = i;
		$scope.detailEntry = i; // detail entry that's been searched
		$scope.detailBtnTrap = false; // Release Detail Button Disabled

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
		$scope.turnOffOthers(0);
		$scope.showPriceLevel($scope.categoryInfoAttr[2]);
		$scope.showRatings($scope.categoryInfoAttr[3]);
		$scope.showHours($scope.categoryInfoAttr[6]);
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
		$scope.photoShow = true;
		$scope.turnOffOthers(1);
		if($scope.placeDetails.photos == undefined || $scope.placeDetails.photos.length == 0){
			$scope.noRecordMsg = true;
		}
		else{
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
		$scope.mapShow = true;
		$scope.turnOffOthers(2);

		$scope.from_addr = form.location.$viewValue == null ? "Your location" : (form.location.$viewValue);
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
	$scope.showReviews = function(){
		console.log("Showing Reivews");
		$scope.reviewShow = true;
		$scope.turnOffOthers(3);

		if($scope.placeDetails.reviews == undefined || $scope.placeDetails.reviews.length == 0){
			$scope.noRecordMsg = true;
		}
		else{
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

	// Order by Obj
	$scope.orderBy = function(order){
		$scope.sortOrder = $scope.sortingOrder[order];
		$scope.orderReverse = order % 2 == 0 ? false : true;

		$scope.orderField = order == 1 || order == 2 ? "rating" : "time";

		//		console.log($scope.sortOrder);
		//		console.log($scope.orderReverse);
	}

	// Store Favorite
	$scope.storeFav = function(rstPage, rstEntry){
		console.log("stored in fav");
		$scope.favSaved = true;

		$scope.allPageInfo[rstPage][rstEntry].saveToken = $scope.favSaveToken;
		$scope.savingEntry = $scope.allPageInfo[rstPage][rstEntry];
		localStorage.setItem($scope.favSaveToken, $scope.savingEntry);

		$scope.favEntryNum++;
		$scope.favSaveToken++;

		$scope.sortFav($scope.favTemp);
	}

	$scope.deleteFav = function(page, entry, mode){
		console.log("delete from fav");
		$scope.favSaved = false;

		if(mode == 0){ // deleted from result
			$scope.favSaveToken = $scope.allPageInfo[page][entry].saveToken;
			localStorage.removeItem($scope.favSaveToken);
		}
		else{// deleted from fav page
			$scope.num = page * $scope.entryMax + entry;
			$scope.favTmp[$scope.num] = null;
		}

		$scope.favEntryNum--;
		$scope.sortFav($scope.favTmp);
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
			document.getElementById('basicInfo').innerHTML = "";
		}

		$scope.entry = angular.copy($scope.master);
	};
});


//placeApp.filter('range', function(){
//	return function(val, range){
//		range = parseInt(range);
//		for(var i=0; i < range; i++){
//			val.push(i);
//			return val;
//		};
//	};
//});


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
