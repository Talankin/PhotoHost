package ru.tds.start.resources;

import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import org.bson.Document;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import ru.tds.start.core.User;
import ru.tds.start.db.UserDB;

@Path("/photohost")
public class UserResource {
	@POST
	@Path("/create")
	@Produces(MediaType.APPLICATION_JSON) 
	@Consumes(MediaType.APPLICATION_JSON) 
	public boolean createUser(Document doc) {
		UserDB.createUser(doc);
		return true;
	}
	
	@GET
	@Path("/auth")
	@Produces(MediaType.APPLICATION_JSON)
	public String signInAuthenticated(@Auth User user) throws AuthenticationException {
		// если не прошли через Authenticator
		if (user.isNull()==true) 
			return null; 

		return user.toJson();
	}

	@POST
	@Path("/update")
	@Produces(MediaType.APPLICATION_JSON) 
	@Consumes(MediaType.APPLICATION_JSON) 
	public boolean updateUser(@Auth User user, Document doc) throws AuthenticationException {
		// если не прошли через Authenticator
		if (user.isNull()==true) 
			return false; 
		
		UserDB.updateUser(doc);
		return true;
	}

	@POST
	@Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON) 
	public boolean deleteUser(@Auth User user, Document doc) throws AuthenticationException {
		// если не прошли через Authenticator
		if (user.isNull()==true) 
			return false; 
		
		UserDB.deleteUser(doc);
		return true;
	}
}
