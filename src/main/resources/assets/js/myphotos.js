//---------------  инициализация  ------------------------------
// прячем DIV с метадатой
$('#formMetadataImg').hide();
var rootClientsURL = "http://localhost:8080/assets";
var formUpload = document.getElementById("formLoadImage");
var inputUpload = document.getElementById("inputLoadImage");
var buttonUpload = document.getElementById("btnLoadImage");

// адрес фотки, с метаданными которой будем работать
var src = "";
var imageId = "";
// сразу грузим на страницу myphotos фотки пользователя
getImagesList();

//---------------  отрабатываем клики  ------------------------------
//$('.metadata').click(function() { 			// работает только для существующих элементов
$('body').on('click', '.img_mini', function(){	// работает для всех (и для динамечески подгружаемых тоже)
	src = $(this).attr('src');
	imageId = getImgIdFromUrl(src); 
	getMetaData(src);
	$('#divMetadataImg').html('<img class="img_meta" src=' + src + '>');
	$('#formMetadataImg').show();
	return false;
})

$('#btnUpdateMetadata').click(function() {
	//if ($.trim(fieldsMetadataToJson()).length > 2) {
	updateMetadata();
	//}
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


//---------------  функции  ------------------------------

//---------------  upload img to server  ------------------------------
formUpload.onsubmit = function(event) {
  event.preventDefault();
  
  // берем выделенные файлы из input формы
  var files = inputUpload.files;
  var fLength = files.length;
  
  if (fLength < 1 ) {
	    alert("Выберите файлы для загрузки на сервер");
  } else {
	  // изменить текст кнопки
	  buttonUpload.innerHTML="Загрузка...";

	  // создаем объект FormData
	  var formData = new FormData();

	  var countUnLimitFiles = 0;
	  var maxSizeAlarm = "Размер файла не должен превышать 2 Мб. Эти файлы не загружены на сервер: \n";
	  // каждый файл массива files добавить в request FormData
	  for (i = 0; i < fLength; i++) {
	    var file = files[i];
	  
	    // проверяем тип файла
	    if (!file.type.match('image.*')) {
	      alert("тип файла не соответствует image");
	      continue;
	    }

	    // нельзя грузить файлы больше 2 МБ (2*1048576)
	    if (file.size > (2*1048576)) {
	      maxSizeAlarm += file.name + "\n";
	      countUnLimitFiles ++;
	      continue;
	    }
	    
	    // добавляем файл в request
	    //formData.append('images', file, file.name);
	    formData.append('images', file);
	  }  
	  
	  // выводим список файлов, которые не были загружены
	  if (countUnLimitFiles > 0) {
	    alert(maxSizeAlarm);
	  }
	  
	  // REST код тут...
	  var xmlHttpRequest = new XMLHttpRequest();
	  // открываем соединение, 3-й параметр true - значит асинхронный запрос
	  xmlHttpRequest.open('POST', 'http://localhost:8080/photohost/loadimage', true);
	  
	  // отправляем запрос
	  xmlHttpRequest.send(formData);
	  
	  // обработчик, когда запрос завершен
	  xmlHttpRequest.onload = function(response) {
	    if (xmlHttpRequest.status == 200) {
	      // файлы загружены на сервер
	      buttonUpload.innerHTML="Добавить";
	      alert("файлы загружены");
	      document.location.href = rootClientsURL + "/myphotos.html";
	    } else if (xmlHttpRequest.status == 401) {
			alert("Для работы с приложением PhotoHost необходимо ввести логин и пароль");
			document.location.href = rootClientsURL + "/index.html";
	    } else {
	      alert("Ошибка загрузки файлов на сервер" + response);
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

			// заполняем элементы метаданными
			if ($.trim(imageName).length > 0) {
				$('#imageName').val(imageName);
			} else {
				$('#imageName').val("Редактировать название");
			}
			if ($.trim(author).length > 0) {
				$('#author').text(author);
			}
			if ($.trim(description).length > 0) {
				$('#description').val(description);
			} else {
				$('#description').val("Редактировать описание");
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
				alert("Изменения сохранены");
			}
			else alert("Что-то пошло не так. Повторите попытку позже");
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
				//alert("Фото удалено");
				document.location = rootClientsURL + "/myphotos.html";
			}
			else alert("Что-то пошло не так. Повторите попытку позже");
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


//---------------  вспомогательные функции  ------------------------------
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
	
	/* // проверка на пустоту полей. отправляем на сервер только заполненные 
	if ( imageName.length > 0 && */
	if (imageName != "Редактировать название") {
		str += '"imageName":' + '"' + imageName + '"';
		isNeedComma = true;
	}
	
	//if ( description.length > 0 &&
	if (description != "Редактировать описание") {
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
