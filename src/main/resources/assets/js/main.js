//---------------  initialization  ------------------------------
var rootURL = "http://localhost:8080/photohost";
var rootClientsURL = "http://localhost:8080/assets";
var rootTokenURL = "http://localhost:8080/token";
var login = "";
var fullname = "";
$('#formMyProfile').hide();
$('#formUpdateProfile').hide();


//---------------  working on clicks  ------------------------------
$('#btnToPageCreate').click(function() {
    document.location.href = rootClientsURL + "/register.html";
    return false;
});


$('#btnToPageUpdate').click(function() {
    $('#formMyProfile').hide();
    signInWithToken("");
    $('#formUpdateProfile').show();
    return false;
});

$('#btnUpdate').click(function() {
    if (confirmPasswordIsEqual() == true) { 
        updateUser();
        $('#formMyProfile').hide();
        $('#formUpdateProfile').hide();
        $('#formPhotohost').show();
    }
    else alert("The entered passwords do not match");
    return false;
});

$('#btnCancelUpdate').click(function() {
    returnToPagePhotohost();
    return false;
});

$('#urlToMyProfile').click(function() {
    $('#formMetadataImg').hide();
    $('#formPhotohost').hide();
    signInWithToken("");
    $('#formMyProfile').show();
    return false;
});

$('#btnDelProfile').click(function() {
    if (confirm("Удалить ваш профиль ?")) {
        deleteUser();
    } else return false;

    return false;
});

$('#btnReturnToPhotohost').click(function() {
    returnToPagePhotohost();
    return false;
});

$('#urlReturnToPhotohost').click(function() {
    signInWithToken(rootClientsURL + "/photohost.html");
    return false;
});

$('#btnCancelUserCreate').click(function() {
    signInWithToken(rootClientsURL + "/photohost.html");
    return false;
});

$('#urlToMyPhotos').click(function() {
    signInWithToken(rootClientsURL + "/myphotos.html");
    return false;
});


$('#closeMetadata').click(function() {
    $('#formMetadataImg').hide();
    return false;
});


$(document).keydown(function(e){
    // Hit ESC and close the metadata pictures
    if (e.keyCode == 27) {
        $('#formMetadataImg').hide();
    }
})


//---------------  functions, called by clicks and not only  ------------------------------
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
        // fill elements on the page
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

function updateUser() {
    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: rootURL + "/update",
        dataType: "text",
        data: formsUserToJson( document.getElementById('loginup').innerHTML,
                $('#fullnameup').val(),
                $('#password').val()),
                success: function(response){
        if (response == "true") {
            alert("Changes saved");
        }
        else alert("Something error. Please try again later");
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('UpdateUser error: ' + errorThrown + "  " + jqXHR.responseText);
    }
    });
    $('#password').val("");
    $('#confirmpassword').val("");
}

function deleteUser() {
    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: rootURL + "/delete",
        dataType: "text",
        data: formsUserToJson(document.getElementById('login').innerHTML, 
                "", 
                ""),
                success: function(response){
        if (response == "true") {
            alert("Profile is deleted");
            document.location = rootClientsURL + "/index.html";
        }
        else alert("Something error. Please try again later");
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('Prifile is not deleted : ' + errorThrown + "  " + jqXHR.responseText + " " + textStatus);
        document.location = rootClientsURL + "/index.html";
    }
    });
}


function returnToPagePhotohost() {
    $('#formMyProfile').hide();
    $('#formUpdateProfile').hide();
    $('#formPhotohost').show();
}

//---------------  util functions   ------------------------------

function formsUserToJson(jLogin, jFullname, jPassword) {
    var str = '{';
    if ( $.trim(jLogin).length > 0 ) {
        str += '"login":' + '"' + $.trim(jLogin) + '"';
    } else {
        return null;
    }

    str += ',"fullname":' + '"' + $.trim(jFullname) + '"';

    if ( $.trim(jPassword).length > 0 ) {
        str += ',"password":' + '"' + $.trim(jPassword) + '"}';
    } else {
        str += '}';
    }

    var jsonData = JSON.parse(str);
    return JSON.stringify(jsonData);
}

function confirmPasswordIsEqual() {
    if ($('#password').val() == $('#confirmpassword').val()) return true;
    else return false;
}
