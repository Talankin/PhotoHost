//---------------  initialization  ------------------------------
$('#formMetadataImg').hide();
var rootURL = "http://localhost:8080/photohost";
var rootClientsURL = "http://localhost:8080/assets";
getLatestImage();

var src = "";
var imageId = "";


//---------------  working on clicks  ------------------------------
function getLatestImage() {
    $.ajax({
        url: rootURL + "/latestimage",
        dataType: "text",
        success: function(response){
        $('#latestImage').html(response);
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('getLatestImage error: ' + errorThrown + "  " + jqXHR.responseText);
    }
    });
}

$('body').on('click', '.img_mini', function(){	
    src = $(this).attr('src');
    imageId = getImgIdFromUrl(src); 
    getMetaData("");
    $('#divMetadataImg').html('<img class="img_meta" src=' + src + '>');
    $('#formMetadataImg').show();
    return false;
})


$('#imgLikes').click(function() {
    likeIncrement();
    return false;
});


$('body').on('click', '.img_meta', function(){
    src = $(this).attr('src');
    imageId = getImgIdFromUrl(src);
    getNextImageId(1);
    return false;
});


//---------------  functions, called by clicks and not only  ------------------------------
function getMetaData(paramImgId) {
    if ($.trim(paramImgId).length > 0) {
        imageId = paramImgId
    }
    $.ajax({
        url: rootURL + "/metadata?id=" + imageId,
        dataType: "json",
        success: function(response){
        var imageName = response.imageName;
        var uploadDate = response.uploadDate;
        var likes = response.likes;
        var author = response.author;
        var description = response.description;

        // fill elements of metadata
        if ($.trim(imageName).length > 0) {
            $('#imageName').text(imageName);
        } else {
            $('#imageName').text("");
        }
        if ($.trim(author).length > 0) {
            $('#author').text(author);
        }
        if ($.trim(description).length > 0) {
            $('#description').text(description);
        } else {
            $('#description').text("");
        }
        $('#uploadDate').text(uploadDate);
        $('#likes').text(likes);

    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('getMetaData error: ' + errorThrown + "  " + jqXHR.responseText);
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

function getNextImageId(isForward) {
    // isForward = 1 : forward, 0 : back
    $.ajax({
        contentType: 'application/json',
        url: rootURL + "/nextimage?id="  + imageId,
        dataType: "text",
        success: function(response){
        imageId = response;
        getMetaData(imageId);
    },
    error: function(jqXHR, textStatus, errorThrown){
        alert('setLike error: ' + errorThrown + "  " + jqXHR.responseText);
    },
    }).done(function (jqXHR){
        $('#divMetadataImg').html('<img class="img_meta" src=' + rootURL + '/imagebyid?id=' + imageId + '>');
    });
}


//---------------  util functions  ------------------------------
function getImgIdFromUrl(url) {
    var pos = url.indexOf("id=") + 3;
    var imgId = url.substring(pos);
    return imgId;
}
