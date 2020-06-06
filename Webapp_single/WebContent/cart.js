
function update_movie(id){
    let quan= document.getElementById(id).value;
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/cart?type=update&id="+id+"&price="+"10"+"&quan="+quan,
        success: window.location.reload() // Setting callback function to handle data returned successfully by the StarsServlet
});
}
function delete_movie(id){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/cart?type=delete&id="+id+"&price="+"10"+"&quan=0",
        success: window.location.reload() // Setting callback function to handle data returned successfully by the StarsServlet
    });
}
function handleSessionResult(resultData){
    let cartTableBodyElement = jQuery("#cart_table_body");
    for(let i=0;i<resultData['shopping_cart'].length; i++ ){
        let rowHTML= "";
        rowHTML += "<tr>";
        rowHTML+= "<th>"+resultData['shopping_cart'][i]['title']+"</th>";
        rowHTML+= "<th>"+"</th>";
        rowHTML+= "<th>$"+resultData['shopping_cart'][i]['price']+"</th>";
        rowHTML+= "<th>"+"<form action=\"#\" onsubmit=\"update_movie('"+resultData['shopping_cart'][i]['id']+"')\">" +
            "  <input id=\""+resultData['shopping_cart'][i]['id']+"\" type=\"text\" name=\"quantity\" value=\""+resultData['shopping_cart'][i]['quan']+"\">" +
            "  <input type=\"submit\" value=\"update\">" +
            "</form>"+"</th>";
        rowHTML +="<th>" + "<button type=\"button\" onclick=\"delete_movie('"+resultData['shopping_cart'][i]['id']+"')\">delete</button>" + "</th>";
        cartTableBodyElement.append(rowHTML);
    }
}





jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/session",// Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleSessionResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});