//---------------  инициализация  ------------------------------
var rootURL = "http://localhost:8080/photohost";
var rootClientsURL = "http://localhost:8080/assets";
var rootTokenURL = "http://localhost:8080/token";
var login = "";
var fullname = "";

//---------------  отрабатываем клики  ------------------------------
$('#btnSignIn').click(function() {
	signInGetToken();
	return false;
});

$('#btnUserCreate').click(function() {
	if (confirmPasswordIsEqual() == true) 
		createUser();
	else alert("Введенные пароли не совпадают");
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


//---------------  функции, вызываемые кликом и не только  ------------------------------
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

function confirmPasswordIsEqual() {
	if ($('#password').val() == $('#confirmpassword').val()) return true;
	else return false;
}
