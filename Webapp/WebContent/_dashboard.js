jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/dash", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleDash(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

function  handleDash(resultData) {
    if(resultData['status']!=="success") window.location.replace("login.html");
}




function handleResult(resultData){
    let meta_info= jQuery("#metadata");
    for (let i = 0; i < resultData.length; i++){
        let meta_html="";
        meta_html += "<h3>Table Name:" + resultData[i]['table_name'] +"</h3>";
        meta_html += "<table id=genre_table style=\"text-align:center;\">\n" +
            "    <thead>\n" +
            "    <tr>\n" +
            "        <th>Attribute</th>\n" +
            "        <th>Type</th>\n" +
            "    </tr>\n" +
            "    </thead>\n" +
            "    <tbody>";
        for (let j = 0; j < resultData[i]['attributes'].length; j++){
            meta_html+= "<tr>";
            meta_html+= "<th>" +resultData[i]['attributes'][j]+"</th>";
            meta_html+= "<th>" +resultData[i]['types'][j]+"</th>";
            meta_html+= "</tr>";
        }
        meta_html +=    "</tbody></table>";
        meta_info.append(meta_html);
    }
}
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/metadata", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});

let star_form = $("#star_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleStarResult(resultData) {
    //let resultData = JSON.parse(eval(resultDataString));
    console.log(resultData);
    let star_message = jQuery("#star_message");
    alert(resultData['message']);
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitStarForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/addstar", {
            method: "Get",
            // Serialize the login form to the data sent by POST request
            data: star_form.serialize(),
            success: (resultData) => handleStarResult(resultData)
        }
    );
}

// Bind the submit action of the form to a handler function
star_form.submit(submitStarForm);

let movie_form = $("#movie_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleMovieResult(resultData) {
    alert(resultData['message']);
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitMovieForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/addmovie", {
            method: "Get",
            // Serialize the login form to the data sent by POST request
            data: movie_form.serialize(),
            success: (resultData) => handleMovieResult(resultData)
        }
    );
}

// Bind the submit action of the form to a handler function
movie_form.submit(submitMovieForm);