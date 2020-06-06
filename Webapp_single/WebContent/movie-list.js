/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
var by= getParameterByName('by');
var title= getParameterByName('title');
var year= getParameterByName('year');
var director= getParameterByName('director');
var starname= getParameterByName('starname');
var genre= getParameterByName('genre');
var order= getParameterByName('order');
var page= getParameterByName('page');
var ipp= getParameterByName('ipp');
function t_asc(){
    let new_order="";
    if (order==="r_asc"||order==="r_dsc") new_order= order+"_t_asc";
    else new_order="t_asc";
    if(order!=="t_asc") window.location.replace("movie-list.html?by="+by+"&title="+title+"&year="+year+"&director="+director+"&starname="+starname+"&genre="+genre+"&order="+new_order+"&page=1"+"&ipp="+ipp);
}
function t_dsc(){
    let new_order="";
    if (order==="r_asc"||order==="r_dsc") new_order= order+"_t_dsc";
    else new_order="t_dsc";
    if(order!=="t_dsc") window.location.replace("movie-list.html?by="+by+"&title="+title+"&year="+year+"&director="+director+"&starname="+starname+"&genre="+genre+"&order="+new_order+"&page=1"+"&ipp="+ipp);
}
function r_asc(){
    let new_order="";
    if (order==="t_asc"||order==="t_dsc") new_order= order+"_r_asc";
    else new_order="r_asc";
    if(order!=="r_asc") window.location.replace("movie-list.html?by="+by+"&title="+title+"&year="+year+"&director="+director+"&starname="+starname+"&genre="+genre+"&order="+new_order+"&page=1"+"&ipp="+ipp);
}
function r_dsc(){
    let new_order="";
    if (order==="t_asc"||order==="t_dsc") new_order= order+"_r_dsc";
    else new_order="r_dsc";
    if(order!=="r_dsc") window.location.replace("movie-list.html?by="+by+"&title="+title+"&year="+year+"&director="+director+"&starname="+starname+"&genre="+genre+"&order="+new_order+"&page=1"+"&ipp="+ipp);
}
function prev(){
    if (page!=="1"){
        let new_page_i= parseInt(page)-1;
        let new_page= new_page_i.toString();
        window.location.replace("movie-list.html?by="+by+"&title="+title+"&year="+year+"&director="+director+"&starname="+starname+"&genre="+genre+"&order="+order+"&page="+new_page+"&ipp="+ipp);
    }
}
function handleNextResult(resultData){
    if(resultData.length!== 0){
        let new_page_i= parseInt(page)+1;
        let new_page= new_page_i.toString();
        window.location.replace("movie-list.html?by="+by+"&title="+title+"&year="+year+"&director="+director+"&starname="+starname+"&genre="+genre+"&order="+order+"&page="+new_page+"&ipp="+ipp);
    }
}
function next() {
    let new_page_i= parseInt(page)+1;
    let new_page= new_page_i.toString();
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/movie-list?by="+by+"&title="+title+"&year="+year+"&director="+director+"&starname="+starname+"&genre="+genre+"&order="+order+"&page="+new_page+"&ipp="+ipp, // Setting request url, which is mapped by StarsServlet in Stars.java
        success: (resultData) => handleNextResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
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
function handleStarResult(resultData) {
    console.log("handleStarResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
            + resultData[i]["movie_title"] +
            '</a>' +
            "</th>";
        rowHTML +="<th>" + "<button type=\"button\" onclick=\"add_to_cart('"+resultData[i]['movie_id']+"')\">add to cart</button>" + "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>";
        for (let j = 0; j < resultData[i]["genre_list"].length; j++) {
            rowHTML += '<a href="movie-list.html?by=browse&title=&year=&director=&starname=&genre='+resultData[i]["genre_list"][j]+'&order=&page=1&ipp=10'+'">'+resultData[i]["genre_list"][j]+'</a>';
            if(j+1 !=  resultData[i]["genre_list"].length) rowHTML += ", ";
        }
        rowHTML += "</th>";
        rowHTML += "<th>";
        for (let j = 0; j < resultData[i]["star_list"].length; j++) {
            rowHTML += '<a href="single-star.html?id=' + resultData[i]['starid_list'][j] + '">'+ resultData[i]["star_list"][j] +'</a>';
            if(j+1 !=  resultData[i]["star_list"].length) rowHTML += ", ";
        }
        rowHTML += "</th>";
        rowHTML += "<th>" + resultData[i]["rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

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
function deal_ipp(button_id){
    if(!(button_id===ipp && page==="1")){
        window.location.replace("movie-list.html?by="+by+"&title="+title+"&year="+year+"&director="+director+"&starname="+starname+"&genre="+genre+"&order="+order+"&page=1"+"&ipp="+button_id);
    }
}
/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */
// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movie-list?by="+by+"&title="+title+"&year="+year+"&director="+director+"&starname="+starname+"&genre="+genre+"&order="+order+"&page="+page+"&ipp="+ipp, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
let current_p=jQuery("#current_p");
current_p.append("Page "+page);
let current_ipp=jQuery("#current_ipp");
current_ipp.append("Items per page: (currently "+ipp+" items)");
let drop_down = jQuery("#drop_down");
let drop_html = "";
drop_html += "<option>"+ipp+"</option>";
drop_html +='<option value ="10" onclick="deal_ipp(this.value)">10</option>';
drop_html += '<option value ="25" onclick="deal_ipp(this.value)">25</option>';
drop_html += '<option value="50" onclick="deal_ipp(this.value)">50</option>';
drop_html += '<option value="100" onclick="deal_ipp(this.value)">100</option>';
drop_down.append(drop_html);