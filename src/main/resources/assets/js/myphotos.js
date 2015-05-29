//---------------  инициализация  ------------------------------
$('#formMetadataImg').hide();
var rootClientsURL = "http://localhost:8080/assets";
var formUpload = document.getElementById("formLoadImage");
var inputUpload = document.getElementById("inputLoadImage");
var buttonUpload = document.getElementById("btnLoadImage");

// сразу грузим на страницу myphotos фотки пользователя
getImagesList();

//---------------  отрабатываем клики  ------------------------------
//$('.metadata').click(function() { 			// работает только для существующих элементов
$('body').on('click', '.img_mini', function(){	// работает для всех (и для динамечески подгружаемых тоже)
	var src = $(this).attr('src');
	$('#divMetadataImg').html('<img class="img_meta" src=' + src + '>');
	$('#formMetadataImg').show();
})

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
	      alert("файлы загружены " + response);
	    } else if (xmlHttpRequest.status == 401) {
			alert("Для работы с приложением PhotoHost необходимо ввести логин и пароль");
			document.location.href = rootClientsURL + "/index.html";
	    } else {
	      alert("Ошибка загрузки файлов на сервер" + response);
	    }
	  };
	};
}


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
