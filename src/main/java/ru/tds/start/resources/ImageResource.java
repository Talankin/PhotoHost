package ru.tds.start.resources;

import java.io.InputStream;
import java.util.List;

import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import ru.tds.start.core.User;
import ru.tds.start.db.ImageDB;

@Path("/photohost")
public class ImageResource {
	private final static String TAG_TABLE_OPEN = "<table><tr>";
	private final static String TAG_TABLE_CLOSE = "</tr></table>";
	private final static String TAG_TR_OPEN = "<tr>";
	private final static String TAG_TR_CLOSE = "</tr>";
	private final static String TAG_TD_OPEN = "<td>";
	private final static String TAG_TD_CLOSE = "</td>";
	
	
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
		
		ImageDB.loadImageToDB(inputStreamImages, fileDetail.getFileName(), user);
		return true;
	}

	
	@GET
	@Path("/latestimage")
	@Produces("image/jpeg")
	@Consumes(MediaType.APPLICATION_JSON) 
	public Response getLatestImage(@Auth User user)
			throws AuthenticationException {
		// если не прошли через Authenticator
		if (user.isNull()==true) 
			return Response.noContent().build(); 
		
		return Response.ok(ImageDB.getLatestImage()).build();
	}

	
	@GET
	@Path("/myimages")
	@Consumes(MediaType.APPLICATION_JSON) 
	public Response generateUrlsOfMyImages(@Auth User user)
			throws AuthenticationException {
		// если не прошли через Authenticator
		if (user.isNull()==true) 
			return Response.ok().build(); 

		List<String> listImageId = ImageDB.getListImageIdByUserId(user.get_Id());
		// если не получили массив id картинок
		if (listImageId == null)
			return Response.ok().build();
		if (listImageId.isEmpty())
			return Response.ok().build();
		
		// определяем размер массива
		int listSize = listImageId.size();

		// понеслась генерация URL-ов
		String htmlRespons = TAG_TABLE_OPEN;
		int i = 1;
		for (String imageId : listImageId) {
			// делаем по четыре фотки в строку
			if ((i % 4) != 0) {
				htmlRespons += TAG_TD_OPEN + 
						"<img class='img_mini' src='http://localhost:8080/photohost/imagebyid?id=" + imageId + "'>" +
						TAG_TD_CLOSE;
			// если фотка уже четвертая в строке
			} else {
				htmlRespons += TAG_TD_OPEN + 
						"<img class='img_mini' src='http://localhost:8080/photohost/imagebyid?id=" + imageId + "'>" +
						TAG_TD_CLOSE +
						TAG_TR_CLOSE;
				// если будут еще фотки, то начинаем новую строку 
				if (i < listSize) {
					htmlRespons += TAG_TR_OPEN; 
				}
			}
			i++;
		}
		htmlRespons += TAG_TABLE_CLOSE;
		
		return Response.ok(htmlRespons).build();
	}

	
	@GET
	@Path("/imagebyid")
	@Produces("image/jpeg")
	@Consumes(MediaType.APPLICATION_JSON) 
	public Response getImageById(@QueryParam("id") String id) {
		InputStream stream = ImageDB.getImageById(id); 
		if (stream != null) 
			return Response.ok(stream).build();
		else
			return Response.ok("картинка не найдена").build();
	}
	
	
	@GET
	@Path("/metadata")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String getMetaDataByImageId(@QueryParam("id") String id) {
		String metadata = ImageDB.getMetaDataByImageId(id);
		return metadata;
	}

	
	@POST
	@Path("/updatemetadata")
	@Produces(MediaType.APPLICATION_JSON) 
	@Consumes(MediaType.APPLICATION_JSON) 
	public boolean updateMetadata(Document doc, @QueryParam("id") String id) {
		return ImageDB.updateMetadata(doc, id);
	}

	
	@GET
	@Path("/deletephoto")
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean deletePhotoByImageId(@Auth User user, @QueryParam("id") String id) 
			throws AuthenticationException {
		// если не прошли через Authenticator
		if (user.isNull()==true) 
			return false; 

		return ImageDB.deletePhotoByImageId(id);
	}

	
	@GET
	@Path("/like")
	@Consumes(MediaType.APPLICATION_JSON)
	public int likeIncrement(@Auth User user, @QueryParam("id") String id) 
			throws AuthenticationException {
		// если не прошли через Authenticator
		if (user.isNull()==true) 
			return -1; 

		return ImageDB.likeIncrement(id);
	}

}
