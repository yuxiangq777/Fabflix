function handleResult(resultData) {
    let movie_t = jQuery("#movie_table_body");
    for(let i=0;i<resultData['shopping_cart'].length; i++ ) {
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>" + resultData['sale_list'][i]+ "</th>";
        rowHTML += "<th>" + resultData['shopping_cart'][i]['title']+ "</th>";
        rowHTML += "<th>" + "</th>";
        rowHTML += "<th>" + resultData['shopping_cart'][i]['quan']+ "</th>";
        rowHTML += "<th>" + resultData['shopping_cart'][i]['price']+ "</th>";
        rowHTML += "</tr>";
        movie_t.append(rowHTML);
    }
    let total = jQuery("#total");
    total.append("Total Price: $"+resultData["total_price"]);
}
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/session?type=clear", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});