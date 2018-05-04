<!Doctype html>
<html>
    <head>
        <meta charset="utf-8">
        <title>Place</title> 
<!--        Form Format-->
        <style type="text/css">
            /*            Entire Search Box*/
            .search{
                width: 650px;
                height: 215px;
                background-color: #F9F9F9;
                border: solid 3px #C1C1C1;
                margin: 20px auto;
            }
            /*            Search Box Title and Line          */
            #search_title{
                font-family: "Times New Roman", Times, serif;
                font-style: italic;
                text-align: center;
                margin: 0px;
            }

            #horizontal_line{
                width: 630px;
                background-color: #C1C1C1;
            }
            
            /*           Other Text*/
            #search_key, #search_opt{
                font-family: "Times New Roman", Times, serif;
                font-size: 16px;
                word-spacing: 1px;
                font-weight: 900;
                left: 10px;
                position: relative;
            }
            
            #search_opt{
                font-weight: 500;
            }
            
            /*            Input and Select Display  */
            input, select{
                left: 10px;
                margin: 2px;
                border: solid 1px #D4D4D4;
                position: relative;
            }
            
            .distance, .from{
                display: inline;
            }
            
            .radio_options{
                display: inline-block;
                vertical-align: top;
            }
            
            .formControl{
                left: 60px;
                position: relative;
            }
            
        </style>
<!--        Functions-->
        <script type="text/javascript">            
            // Geolocation Values
            var latitude;
            var longtitude;
            var hereIP;
                        
            // Handles all changes after clicking location options
            // inputObj: inputed element
            function handleChange(inputObj){
                var inputId = inputObj.id;
                var searchHere = 'search_from_here';
                var searchSpc = 'search_from_spc';
                var searchLocation = 'search_location';
                var searchButton = 'search_button'
                
                if(inputId == searchHere){ // from here radio button
                    if(inputObj.checked == true){
                        getGeoLocation(searchButton);
                        toggleRequire(searchLocation, false);
                    }
                }
                else if(inputId == searchSpc){ // from location radio button
                    if(inputObj.checked == true){
                        toggleRequire(searchLocation, true);
                        toggleDisable(searchButton, false);
                       }
                }
                else if(inputId == searchLocation){ // from location text field
                    setChecked(searchSpc);
                    toggleRequire(searchLocation, true);
                    toggleDisable(searchButton, false);
                }
                else{ // clear button
                    setChecked(searchHere);
                    toggleRequire(searchLocation, false);
                }
            }
            
            // Set the element with an id of inputID to isRequired
            // inputId: inputed element id
            // isRequired: boolean value indicating required or not
            function toggleRequire(inputId, isRequired){
                document.getElementById(inputId).required = isRequired;
            }
            
            // Set the element with an id of inputID to isDisabled
            // inputId: inputed element id
            // isRequired: boolean value indicating disabled or not
            function toggleDisable(inputId, isDisabled){
                document.getElementById(inputId).disabled = isDisabled;
            }
            
            // Set the associated radio button to checked and set
            // the associated element to be required
            // inputId: the id of radio button to be set as checked
            function setChecked(inputId){
                document.getElementById(inputId).checked = true;
            }
             
            function getGeoLocation(inputId){
                console.log("Fetching Geo Location.");
                toggleDisable(inputId, true); // set disabled
                getIPAddress();
                sendIPToPHP(); // send the ip address to php
                toggleDisable(inputId, false); // reset disabled
            }
            
            function getIPAddress(){
                // Getting IP Address
                console.log("Fetching Geo and IP Information.");
//                var url = "http://ip-api.com/json";
//                var request = new XMLHttpRequest();
//                request.open('GET', url, false);
//                request.send();
//
//                var jsonFile = request.responseText;
//                var data = JSON.parse(jsonFile);
//                latitude = data.lat;
//                longtitude = data.lon;
//                hereIP = data.query;
                latitude = "1";
                longtitude = "2";
                hereIP = "127.0.0.1";
                console.log("Received IP Address: " + hereIP);
            }
            
            function sendIPToPHP(){
                const COMPLETE = 4;
                const OK = 200;
                var request = new XMLHttpRequest();
                var url = "<?php echo $_SERVER['PHP_SELF'];?>";
                var queryString = "?latitude="+latitude+"&longtitude="+longtitude+"&hereIp="+hereIP;
                
                request.onreadystatechange = function(){
                    if(request.readyState == COMPLETE || request.status == OK){
                        //console.log(request.responseText);
                    }
                    else{
                        console.log('Error: ' + request.status);
                    }
                }
                
                console.log("URL + Query String: " + url+queryString);
                console.log("Send Geo and IP Information to PHP.");
                request.open("GET", url+queryString, false);
                request.send();
            }
        </script>
    </head>
    <body>
<!--        Server-->
        <div class="PHP">
            <?php
                // Google Place API KEY
                define("API_KEY", "AIzaSyCj3iADFyrPT7aOrGQiFG_m8PxzGFzuowI");
                // Goolge Place nearyby url for maps and locations
                define("NEARBY_URL", "https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                // Google Place detail url for reviews and photos
                define("DETAIL_URL", "https://maps.googleapis.com/maps/api/place/details/json?");
            
                $lat = isset($_GET['latitude']) ? $_GET['latitude']: "";
                $lon = isset($_GET['longtitude']) ? $_GET['longtitude']: "";
                $ip  = isset($_GET['hereIp']) ? $_GET['hereIp']: "";

                echo $lat . "; " . $lon . "; " . $ip;
            
                // Form variables
                if(isset($_POST)){
                    $keyword = $_POST['keyword'];
                    $category = $_POST['category'];
                    $distance = $_POST['distance'];
                    $location = $_POST['location'];
                    
                    $location = str_replace(' ', '+', $location);
                    $nearByQryStr = "address=".$location."&key=".API_KEY;
                    $nearByRequest = NEARBY_URL.$nearByQryStr;
                    $geoInfoJSON = file_get_contents($nearByRequest);
                    $geoInfo = json_decode($geoInfoJSON, true);
                    
                    echo $geoInfo;
                }
            ?>
        </div>
<!--        Form-->
        <div class="search">
            <!--Title-->
            <h1 id="search_title">Travel and Entertainment Search</h1>
            <hr id="horizontal_line"></hr>
            <!--Form-->
            <form id="search_form" name="TESearch" method="post" action="<?php echo $_SERVER['PHP_SELF'];?>">
                <!--Keyword-->
                <div class="keyword">
                    <label id="search_key">Keyword</label>
                    <input type="text" id="search_keyword" name="keyword" size="17" required autofocus><br>
                </div>
                <!--Category-->
                <div class="category">
                    <label id="search_key">Category</label>
                    <select id="search_category" name="category" autofocus>
                        <option selected>default</option>
                        <option>cafe</option>
                        <option>bakery</option>
                        <option>restaurant</option>
                        <option>beauty salon</option>
                        <option>casino</option>
                        <option>movie theater</option>
                        <option>lodging</option>
                        <option>airport</option>
                        <option>train station</option>
                        <option>subway station</option>
                        <option>bus station</option>
                    </select><br>
                </div>
                <!--Distance-->
                <div class="distance">
                    <label id="search_key">Distance (miles)</label>
                    <input type="text" id="search_distance" name="distance" size="17" placeholder="10" pattern="(\d+(\.\d+)?)?" title="Please enter a number." autofocus>
                </div>
                <!--from-->
                <div class="from">
                    <label id="search_key">from</label>
                    <div class="radio_options">
                        <!--Search from Here-->
                        <input type="radio" id="search_from_here" name="from" value="here" onclick="handleChange(this)" checked>
                        <label id="search_opt">Here</label>
                        <br>
                        
                        <!--Search from Specific Location-->
                        <input type="radio" id="search_from_spc" name="from" onclick="handleChange(this)">
                        <input type="text" id="search_location" name="location" onclick="handleChange(this)" placeholder="location">
                    </div>
                </div>
                <br><br>
                <!--Submit-->
                <div class="formControl">
                    <input type="submit" id="search_button" value="Search">
                    <input type="reset" id="clear_button" value="Clear" onclick="handleChange(this)">
                </div>
            </form>
        </div>
        <!--Onloading Action-->
        <script>getGeoLocation('search_button');</script>
    </body>
</html>