function handleSessionResult(resultData){
    let totalcomponent=jQuery("#total");
    totalcomponent.append("Total Price: $"+resultData["total_price"]);
}
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/session",// Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleSessionResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

let pay_form = $("#pay_form");

/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handlePayResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle pay response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("confirm-pay.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#pay_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPayForm(formSubmitEvent) {

    formSubmitEvent.preventDefault();
    $.ajax(
        "api/pay", {
            method: "Post",
            // Serialize the login form to the data sent by POST request
            data: pay_form.serialize(),
            success: handlePayResult
        }
    );
}

// Bind the submit action of the form to a handler function
pay_form.submit(submitPayForm);