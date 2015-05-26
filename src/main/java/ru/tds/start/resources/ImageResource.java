package ru.tds.start.resources;

import java.io.InputStream;
import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import ru.tds.start.core.User;
import ru.tds.start.db.ImageDB;

@Path("/photohost")
public class ImageResource {
	@POST
	@Path("/loadimage")
	@Consumes(MediaType.MULTIPART_FORM_DATA) 
	public boolean loadImage(@Auth User user, 
			@FormDataParam("images") InputStream inputStreamImages,
			@FormDataParam("images") FormDataContentDisposition fileDetail)
			throws AuthenticationException {
		// если не прошли через Authenticator
		if (user.isNull()==true) 
			return false; 
		
		System.out.println(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,   я в ресурсе LoadImage fileDetail = " + 
				fileDetail.getFileName() + "  " + fileDetail.getName() + "  " + fileDetail.getSize());
		
		ImageDB.loadImageToDB(inputStreamImages, fileDetail.getFileName());
		return true;
	}

	@GET
	@Path("/getimage")
	@Produces("image/jpeg")
	@Consumes(MediaType.APPLICATION_JSON) 
	public Response getLatestImage(@Auth User user)
			throws AuthenticationException {
		// если не прошли через Authenticator
		if (user.isNull()==true) 
			return Response.noContent().build(); 
		
		return Response.ok(ImageDB.getLatestImageFromDB()).build();
	}

}
