


let titleElement= jQuery("#title_body");
var astring= "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ*";
let rowHTML2 = "";
for (let i = 0; i < astring.length; i++) {
    if(i % 10==0) rowHTML2 +="<tr>";
    rowHTML2 += "<th>";
    rowHTML2 += '<a href="movie-list.html?by=browse&title='+astring[i]+'&year=&director=&starname=&genre=&order=&page=1&ipp=10'+'">'+astring[i]+'</a>';
    rowHTML2 += "</th>";
    if(i % 10==9) rowHTML2 +="</tr>";
}
titleElement.append(rowHTML2);

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let search_form = $("#search_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleSearchResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("movie-list.html?by=search&title="+resultDataJson["title"]+"&year="+resultDataJson["year"]+"&director="+resultDataJson["director"]+"&starname="+resultDataJson["starname"]+"&genre=&order=&page=1&ipp=10");
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitSearchForm(formSubmitEvent) {
    console.log("submit Search form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/main", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: search_form.serialize(),
            success: handleSearchResult
        }
    );
}

function handleResult(resultData) {
    let genreElement = jQuery("#genre_body");
// Concatenate the html tags with resultData jsonObject to create table rows
    //var genre_list= ['Action','Adult','Adventure','Animation','Biography','Comedy','Crime','Documentary','Drama','Family','Fantasy','History','Horror','Music','Musical','Mystery','Reality-TV','Romance','Sci-Fi','Sport','Thriller','War','Western'];
    let rowHTML1 = "";
    for (let i = 0; i < resultData.length; i++) {
        if(i % 6==0) rowHTML1 +="<tr>";
        rowHTML1 += "<th>";
        rowHTML1 += '<a href="movie-list.html?by=browse&title=&year=&director=&starname=&genre='+resultData[i]+'&order=&page=1&ipp=10'+'">'+resultData[i]+'</a>';
        rowHTML1 += "</th>";
        if(i % 6==5) rowHTML1 +="</tr>";
        // Append the row created to the table body, which will refresh the page
    }
    genreElement.append(rowHTML1);
}
// Bind the submit action of the form to a handler function
search_form.submit(submitSearchForm);

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/genre", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

/*
 * This function is called by the library when it needs to lookup a query.
 *
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */
var cache={};
function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated");

    // TODO: if you want to check past query results first, you can do it here
    if(query in cache){
        console.log("Suggestion list is coming from Front-end cache");
        console.log(JSON.stringify(cache[query]));
        doneCallback( { suggestions: cache[query] } );
    }
    else {
        // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
        // with the query data
        jQuery.ajax({
            "method": "GET",
            // generate the request url from the query.
            // escape the query string to avoid errors caused by special characters
            "url": "api/autocomplete?query=" + escape(query),
            "success": function (data) {
                // pass the data, query, and doneCallback function into the success handler
                handleLookupAjaxSuccess(data, query, doneCallback)
            },
            "error": function (errorData) {
                console.log("lookup ajax error");
                console.log(errorData);
            }
        })
    }
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 *
 * data is the JSON data string you get from your Java Servlet
 *
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
    // parse the string into JSON
    //var jsonData = JSON.parse(eval('[' + data + ']'));


    // TODO: if you want to cache the result into a global variable you can do it here
    if(!(query in cache)){
        cache[query]=data;
    }
    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    console.log("Suggestion list is coming from Back-end server");
    console.log(JSON.stringify(data));
    doneCallback( { suggestions: data } );
}


/*
 * This function is the select suggestion handler function.
 * When a suggestion is selected, this function is called by the library.
 *
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion
    console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["id"]);
    window.location.replace("single-movie.html?id="+suggestion["data"]["id"]);
}


/*
 * This statement binds the autocomplete library with the input box element and
 *   sets necessary parameters of the library.
 *
 * The library documentation can be find here:
 *   https://github.com/devbridge/jQuery-Autocomplete
 *   https://www.devbridge.com/sourcery/components/jquery-autocomplete/
 *
 */
// $('#autocomplete') is to find element by the ID "autocomplete"
$('#autocomplete').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
        handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    minChars: 3,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});


/*
 * do normal full text search if no suggestion is selected
 */
function handleNormalSearch(query) {
    console.log("doing normal search with query: " + query);
    if (query!=="") {
        window.location.replace("movie-list.html?by=main&title="+query+"&year=&director=&starname=&genre=&order=&page=1&ipp=10");
    }
    // TODO: you should do normal search here
}

// bind pressing enter key to a handler function
$('#autocomplete').keypress(function(event) {
    // keyCode 13 is the enter key
    if (event.keyCode == 13) {
        // pass the value of the input box to the handler function
        handleNormalSearch($('#autocomplete').val())
    }
})

// TODO: if you have a "search" button, you may want to bind the onClick event as well of that button
let main_search_form= $("#main_search_form");
function handleMainResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("movie-list.html?by=main&title="+resultDataJson["title"]+"&year=&director=&starname=&genre=&order=&page=1&ipp=10");
    }
}
function submitMainForm(formSubmitEvent) {
    console.log("submit Main form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/main", {
            method: "GET",
            // Serialize the login form to the data sent by POST request
            data: main_search_form.serialize(),
            success: handleMainResult
        }
    );
}
main_search_form.submit(submitMainForm);