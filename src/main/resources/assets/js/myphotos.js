//---------------  initialization  ------------------------------
$('#formMetadataImg').hide();
var rootURL = "http://localhost:8080/photohost";
var rootClientsURL = "http://localhost:8080/assets";
var formUpload = document.getElementById("formLoadImage");
var inputUpload = document.getElementById("inputLoadImage");
var buttonUpload = document.getElementById("btnLoadImage");

// address of image of metadata
var src = "";
var imageId = "";
// just to push photos of User to the page myphotos 
getImagesList();

//---------------  working on clicks  ------------------------------
$('body').on('click', '.img_mini', function(){	// for dinamic and static elements of the page
    src = $(this).attr('src');
    imageId = getImgIdFromUrl(src); 
    getMetaData();
    $('#divMetadataImg').html('<img class="img_meta" src=' + src + '>');
    $('#formMetadataImg').show();
    return false;
})

$('#btnUpdateMetadata').click(function() {
    updateMetadata();
    return false;
});

$('#btnCancelMetaChange').click(function() {
    $('#formMetadataImg').hide();
    return false;
});

$('#imgDelete').click(function() {
    if (confirm("Удалить фото ?")) {
        deletePhoto();
    } else return false;

    return false;
});

$('#imgLikes').click(function() {
    likeIncrement();
    return false;
});

//---------------  functions  ------------------------------

//---------------  upload img to server  ------------------------------
formUpload.onsubmit = function(event) {
    event.preventDefault();

    // to get selected files from upload form
    var files = inputUpload.files;
    var fLength = files.length;

    if (fLength < 1 ) {
        alert("Not selected files");
    } else {
        // изменить текст кнопки
        buttonUpload.innerHTML="Upload...";

        // создаем объект FormData
        var formData = new FormData();

        var countUnLimitFiles = 0;
        var maxSizeAlarm = "File size must not exceed 400 Kb. These files are not uploaded to the server: \n";
        // each file of files array to add to the request FormData
        for (i = 0; i < fLength; i++) {
            var file = files[i];

            // check file type
            if (!file.type.match('image.*')) {
                alert("file type does not match with image");
                continue;
            }

            // File size must not exceed 400 Kb (1 Mb 1048576)
            if (file.size > (400000)) {
                maxSizeAlarm += file.name + "\n";
                countUnLimitFiles ++;
                continue;
            }

            // add file to request
            //formData.append('images', file, file.name);
            formData.append('images', file);
        }  

        // print list files, wich not loaded 
        if (countUnLimitFiles > 0) {
            alert(maxSizeAlarm);
        }

        // REST code TO DO...
        var xmlHttpRequest = new XMLHttpRequest();
        // open connection, 3-rd parameter is true - is asinchron request
        xmlHttpRequest.open('POST', 'http://localhost:8080/photohost/loadimage', true);
        // send request
        xmlHttpRequest.send(formData);

        // when the request is completed
        xmlHttpRequest.onload = function(response) {
            if (xmlHttpRequest.status == 200) {
                // files is uploaded to server 
                buttonUpload.innerHTML="Add images";
                document.location.href = rootClientsURL + "/myphotos.html";
            } else if (xmlHttpRequest.status == 401) {
                alert("For use PhotoHost application you must to enter your login and password");
                document.location.href = rootClientsURL + "/index.html";
            } else {
                alert("Error upload files to the server " + response);
            }
        };
    };
} //---------------  end function upload img to server  ------------------------------


function getImagesList() {
    $.ajax({
        url: rootURL + "/myimages",
        dataType: "text",
        success: function(response){
        $('#myImagesList').html(response);
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('getImagesList error: ' + errorThrown + "  " + jqXHR.responseText);
    }
    });
}

function getMetaData() {
    $.ajax({
        url: rootURL + "/metadata?id=" + imageId,
        dataType: "json",
        success: function(response){
        var imageName = response.imageName;
        var uploadDate = response.uploadDate;
        var likes = response.likes;
        var author = response.author;
        var description = response.description;

        // fill elements by metadata
        if ($.trim(imageName).length > 0) {
            $('#imageName').val(imageName);
        } else {
            $('#imageName').val("Edit name");
        }
        if ($.trim(author).length > 0) {
            $('#author').text(author);
        }
        if ($.trim(description).length > 0) {
            $('#description').val(description);
        } else {
            $('#description').val("Edit description");
        }
        $('#uploadDate').text(uploadDate);
        $('#likes').text(likes);

    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('getMetaData error: ' + errorThrown + "  " + jqXHR.responseText);
    }
    });
}


function updateMetadata() {
    $.ajax({
        type: 'POST',
        contentType: 'application/json',
        url: rootURL + "/updatemetadata?id="  + imageId,
        dataType: "text",
        data: fieldsMetadataToJson(),
        success: function(response){
        if (response == "true") {
            alert("Changes saved");
        }
        else alert("Something error. Please try again later");
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('updateMetadata error: ' + errorThrown + "  " + jqXHR.responseText);
    }
    });
}


function deletePhoto() {
    $.ajax({
        contentType: 'application/json',
        url: rootURL + "/deletephoto?id="  + imageId,
        dataType: "text",
        success: function(response){
        if (response == "true") {
            document.location = rootClientsURL + "/myphotos.html";
        }
        else alert("Something error. Please try again later");
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('deletePhoto error: ' + errorThrown + "  " + jqXHR.responseText);
    }
    });
}


function likeIncrement() {
    $.ajax({
        contentType: 'application/json',
        url: rootURL + "/like?id="  + imageId,
        dataType: "text",
        success: function(response){
        if (response != "-1") {
            $('#likes').text(response);
        } else 
            return null;
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('setLike error: ' + errorThrown + "  " + jqXHR.responseText);
    }
    });
}


//---------------  util functions  ------------------------------
function getImgIdFromUrl(url) {
    var pos = url.indexOf("id=") + 3;
    imgId = url.substring(pos);
    return imgId;
}


function fieldsMetadataToJson() {
    var imageName = $.trim($('#imageName').val());
    var description = $.trim($('#description').val());
    var str = '{';
    var isNeedComma = false;

    if (imageName != "Edit name") {
        str += '"imageName":' + '"' + imageName + '"';
        isNeedComma = true;
    }

    if (description != "Edit description") {
        if (isNeedComma){
            str += ','
        } 
        str += '"description":' + '"' + description + '"}';
    } else {
        str += '}';
    }

    var jsonData = JSON.parse(str);
    return JSON.stringify(jsonData);
}
