/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let movieInfoElement = jQuery("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Name: " + resultData[0]["movie_title"] + "</p>" +
        "<p>Year: " + resultData[0]["year"] + "</p>"+
        "<p>Director: " + resultData[0]["director"] + "</p>"+
        "<p>Rating: " + resultData[0]["rating"] + "</p>");
    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let genreTableBodyElement = jQuery("#genre_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[1]["genre_list"].length; i++) {
        let rowHTML1 = "";
        rowHTML1 += "<tr>";
        rowHTML1 += "<th>" + '<a href="movie-list.html?by=browse&title=&year=&director=&starname=&genre='+resultData[1]["genre_list"][i]+'&order=&page=1&ipp=10'+'">'+resultData[1]["genre_list"][i]+'</a>' + "</th>";
        rowHTML1 += "</tr>";

        // Append the row created to the table body, which will refresh the page
        genreTableBodyElement.append(rowHTML1);
    }
    let starTableBodyElement = jQuery("#star_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData[2]["star_list"].length; i++) {
        let rowHTML2 = "";
        rowHTML2 += "<tr>";
        rowHTML2 += "<th>" +'<a href="single-star.html?id=' + resultData[2]['starid_list'][i] + '">'+ resultData[2]["star_list"][i] + "</th>";
        rowHTML2 += "<th>" + resultData[2]["count_list"][i]+"</th>";
        rowHTML2 += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML2);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */
function handleback(resultData){
    if(resultData['search_result']!==""){
        window.location.replace("movie-list.html?"+resultData['search_result']);
    }
}
function back(){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/session",
        success: (resultData) => handleback(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    });
}
function handleAddCartResult(resultData){
    if(resultData["status"]=="success"){
        alert(resultData["message"]);
    }
    else alert("Fail");
}
function add_to_cart(id){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/cart?type=add&id="+id+"&price="+"10"+"&quan=1",
        success: (resultData) => handleAddCartResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
}

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

let add_part=jQuery("#add");
let add_html="";
add_html+="<button type=\"button\" onclick=\"add_to_cart('"+movieId+"')\">add to cart</button>";
add_part.append(add_html);