//---------------  инициализация  ------------------------------
// прячем DIV с метадатой
$('#formMetadataImg').hide();
var rootURL = "http://localhost:8080/photohost";
var rootClientsURL = "http://localhost:8080/assets";
getLatestImage();


//---------------  отрабатываем клики  ------------------------------
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

//работает для всех (и для динамечески подгружаемых тоже)
$('body').on('click', '.img_mini', function(){	
	src = $(this).attr('src');
	imageId = getImgIdFromUrl(src); 
	getMetaData(src);
	$('#divMetadataImg').html('<img class="img_meta" src=' + src + '>');
	$('#formMetadataImg').show();
	return false;
})


$('#imgLikes').click(function() {
	likeIncrement();
	return false;
});


//---------------  функции, вызываемые кликом и не только  ------------------------------
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

			// заполняем элементы метаданными
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


//---------------  вспомогательные функции  ------------------------------
function getImgIdFromUrl(url) {
	  var pos = url.indexOf("id=") + 3;
	  imgId = url.substring(pos);
	  return imgId;
	}
