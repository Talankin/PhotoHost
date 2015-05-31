//---------------  инициализация  ------------------------------
var rootURL = "http://localhost:8080/photohost";
var rootClientsURL = "http://localhost:8080/assets";
var rootTokenURL = "http://localhost:8080/token";
var login = "";
var fullname = "";
$('#formMyProfile').hide();
$('#formUpdateProfile').hide();

// на страницу /photohost/ сразу грузим последнюю добавленную в БД картинку
getLatestImage();

//---------------  отрабатываем клики  ------------------------------
$('#btnSignIn').click(function() {
	signInGetToken();
	return false;
});

$('#btnToPageCreate').click(function() {
	//sendGetRequest();
	document.location.href = rootClientsURL + "/register.html";
	return false;
});

$('#btnUserCreate').click(function() {
	if (confirmPasswordIsEqual() == true) 
		createUser();
	else alert("Введенные пароли не совпадают");
	return false;
});

$('#btnToPageUpdate').click(function() {
	//прячем форму профиля и показываем форму update профиля
	$('#formMyProfile').hide();
	signInWithToken("");
	$('#formUpdateProfile').show();
	return false;
});

$('#btnUpdate').click(function() {
	if (confirmPasswordIsEqual() == true) { 
		updateUser();
		// показываем исходную форму май фотохост, остальные прячем
		$('#formMyProfile').hide();
		$('#formUpdateProfile').hide();
		$('#formPhotohost').show();
	}
	else alert("Введенные пароли не совпадают");
	return false;
});

$('#btnCancelUpdate').click(function() {
	returnToPagePhotohost();
	return false;
});

$('#btnToPageLogin').click(function() {
	document.location.href = rootClientsURL + "/signin.html";
	return false;
});

$('#urlToMyProfile').click(function() {
	//прячем форму Photohost и показываем MyProfile
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
	// остаемся на этой же странице photohost.html, просто меняем видимость форм
	returnToPagePhotohost();
	return false;
});

$('#urlReturnToPhotohost').click(function() {
	// возвращаемся с другого файла html на главную
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

//---------------  функции, вызываемые кликом  ------------------------------
function signInGetToken() {
	$('#formSignIn').ajaxSubmit({
		type: 'POST',
		url: rootTokenURL,
		dataType: "text",
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
			// заполняем элементы на странице
			$('#login').text(login);
			$('#fullname').text(fullname);
			$('#loginup').text(login);
			$('#fullnameup').val(fullname);
		},
		error: function(jqXHR, textStatus, errorThrown){
			//alert('signInWithToken error: ' + errorThrown + "  " + jqXHR.responseText, + "  " + textStatus);
			alert("Для работы с приложением PhotoHost необходимо ввести логин и пароль");
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
				alert("Профиль зарегистрирован");
			}
			else alert("Что-то пошло не так. Повторите попытку позже");
			
			signInWithToken(rootClientsURL + "/photohost.html");
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('createUser error: ' + errorThrown + "  " + jqXHR.responseText);
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
				alert("Изменения приняты");
			}
			else alert("Что-то пошло не так. Повторите попытку позже");
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
				alert("Профиль удален");
				document.location = rootClientsURL + "/index.html";
			}
			else alert("Что-то пошло не так. Повторите попытку позже");
		},
		error: function(jqXHR, textStatus, errorThrown){
			alert('При удалении профиля сервер вернул ошибку: ' + errorThrown + "  " + jqXHR.responseText + " " + textStatus);
			document.location = rootClientsURL + "/index.html";
		}
	});
}

function getLatestImage() {
	// заливаем в div картинку в виде inputstream (предварительно раскодируем из base64 если нужно)
	//$('#latestImage').html('<img src="data:image/jpeg;base64,' + response + '"/>');
	$('#latestImage').html('<img src="' + rootURL + "/latestimage" + 
			'" alt="Тут самое последнее добавленное фото"/>');
}

function returnToPagePhotohost() {
	// показываем исходную форму фотохост, остальные прячем
	$('#formMyProfile').hide();
	$('#formUpdateProfile').hide();
	$('#formPhotohost').show();
}

//---------------  вспомогательные функции  ------------------------------

//сериализация значений полей формы в Json (новый юзер)
function formUserCreateToJson() {
	var _id = $('#login').val();
	//JSON.stringify() преобразует значение JavaScript в строку JSON
	return JSON.stringify({
		"_id": _id,
		"login": $('#login').val(), 
		"password": $('#password').val(),
		"fullname": $('#fullname').val()
		});
}

function formsUserToJson(jLogin, jFullname, jPassword) {
	var str = '{';
	/* // проверка на пустоту полей. отправляем на сервер только заполненные */
	if ( $.trim(jLogin).length > 0 ) {
		str += '"login":' + '"' + $.trim(jLogin) + '"';
	} else {
		return null;
	}
	
	//if ( $.trim(jFullname).length > 0 ) {
	str += ',"fullname":' + '"' + $.trim(jFullname) + '"';
	//}

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
