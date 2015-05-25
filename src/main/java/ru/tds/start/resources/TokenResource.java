package ru.tds.start.resources;


import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.bson.Document;

import ru.tds.start.core.Token;
import ru.tds.start.db.TokenDB;
import ru.tds.start.db.UserDB;

import com.google.common.collect.ImmutableList;

@Path("/token")
@Produces(MediaType.APPLICATION_JSON)
public class TokenResource {
	private ImmutableList<String> grantTypes;
	
	public TokenResource (
			ImmutableList<String> grantTypes) {
		this.grantTypes = grantTypes;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response tokenPOST (
			//@FormParam("grant_type") String grantType,
			@FormParam("login") String login,
			@FormParam("password") String password,
			@FormParam("client_id") String clientId ) {

		// проверка, что grant Type разрешен
		/*if (!grantTypes.contains(grantType)) {
			throw new WebApplicationException(Response.status(Response.Status.METHOD_NOT_ALLOWED).build());
		}*/
		
		// пытаюсь найти юзера с введенными учетными данными
		Document document = UserDB.getUserDocByLoginPassword(login, password);
		
		if (document == null) {
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
		}
		
		// берем userId, полученный из mongodb, для создания токена
		String userId = document.getString("_id");

		System.out.println("***************Нашли юзера в базе " + document.toJson());
		
		// генерим и возвращаем токен
		Token token = null;
		try {
			System.out.println("*************** пробуем создать токен ");
			token = TokenDB.createToken(userId);
		} catch (Exception e) {
			System.out.println("*************** не получилось создать токен ");
			throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
		}
		
		Cookie cookie = new Cookie("tokenId",token.getTokenId().toString());
		NewCookie newCookie = new NewCookie(cookie);
		
		System.out.println("******************* возвращаем куки токен на страницу ");
		
		//return Response.ok(tokenDoc.toJson()).type(MediaType.APPLICATION_JSON).build();
		return Response.ok("response Ok").cookie(newCookie).build();
	}
	
}
