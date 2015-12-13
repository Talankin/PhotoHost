//---------------  initialization  ------------------------------
var rootURL = "http://localhost:8080/photohost";
var rootClientsURL = "http://localhost:8080/assets";
var rootTokenURL = "http://localhost:8080/token";
var login = "";
var fullname = "";

//---------------  working on clicks  ------------------------------
$('#btnSignIn').click(function() {
    signInGetToken();
    return false;
});

$('#btnUserCreate').click(function() {
    if (confirmPasswordIsEqual() == true) 
        createUser();
    else alert("The entered passwords do not match");
    return false;
});

$('#btnToPageLogin').click(function() {
    document.location.href = rootClientsURL + "/signin.html";
    return false;
});


$('#btnCancelUserCreate').click(function() {
    signInWithToken(rootClientsURL + "/photohost.html");
    return false;
});


//---------------  functions, called by clicks and not only  ------------------------------
function signInGetToken() {
    $.ajax({
        type: 'POST',
        url: rootTokenURL,
        contentType: 'application/json',
        dataType: "json",
        data: JSON.stringify({"login": $('#login').val(),"password": $('#password').val()}),
        success: function(response){
        signInWithToken(rootClientsURL + "/photohost.html");
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('signInGetToken error: ' + errorThrown + "  " + jqXHR.responseText, + "  " + textStatus);
    }
    });
}

function signInWithToken(redirectUrl) {
    $.ajax({
        url: rootURL + "/auth",
        dataType: "json",
        success: function(response){
        login = response.login;
        fullname = response.fullname;
        if (redirectUrl != "" && redirectUrl != null ) {
            window.location.href = redirectUrl;
        }
        // fill elements at the page
        $('#login').text(login);
        $('#fullname').text(fullname);
        $('#loginup').text(login);
        $('#fullnameup').val(fullname);
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert("For use PhotoHost application you must to enter your login and password");
        document.location.href = rootClientsURL + "/index.html";
    }
    });
}

function createUser() {
    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: rootURL + "/create",
        dataType: "text",
        data: formUserCreateToJson(),
        success: function(response){
        if (response == "true") {
            alert("Profile created");
        }
        else alert("Something error. Please try again later");

        signInWithToken(rootClientsURL + "/photohost.html");
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('createUser error: ' + errorThrown + "  " + jqXHR.responseText);
    }
    });
}

//---------------  util functions  ------------------------------

//field to Json (create user)
function formUserCreateToJson() {
    var _id = $('#login').val();
    //JSON.stringify() convert JavaScript value to JSON string
    return JSON.stringify({
        "_id": _id,
        "login": $('#login').val(), 
        "password": $('#password').val(),
        "fullname": $('#fullname').val()
    });
}

function confirmPasswordIsEqual() {
    if ($('#password').val() == $('#confirmpassword').val()) return true;
    else return false;
}
