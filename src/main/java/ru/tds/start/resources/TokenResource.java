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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.tds.start.core.Token;
import ru.tds.start.db.TokenDB;
import ru.tds.start.db.UserDB;

@Path("/token")
@Produces(MediaType.APPLICATION_JSON)
public class TokenResource {
    final static Logger logger = LoggerFactory.getLogger(TokenResource.class);

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response tokenPOST(@FormParam("login") String login,
            @FormParam("password") String password,
            @FormParam("client_id") String clientId) {

        // to find the user by login and password in mongodb 
        Document document = UserDB.getUserDocByLoginPassword(login, password);

        if (document == null) {
            throw new WebApplicationException(Response.status(
                    Response.Status.UNAUTHORIZED).build());
        }

        // to get userId from mongodb for generation token
        String userId = document.getString("_id");

        // generate and return token
        Token token = null;
        try {
            token = TokenDB.createToken(userId);
        } catch (Exception e) {
            logger.error("===== unable to create token");
            throw new WebApplicationException(Response.status(
                    Response.Status.UNAUTHORIZED).build());
        }

        Cookie cookie = new Cookie("tokenId", token.getTokenId().toString());
        NewCookie newCookie = new NewCookie(cookie);

        return Response.ok().cookie(newCookie).build();
    }

}
