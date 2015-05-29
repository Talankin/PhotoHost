package ru.tds.start.resources;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.dropwizard.auth.Auth;
import io.dropwizard.auth.AuthenticationException;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

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
		
		System.err.println(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,   я в ресурсе LoadImage fileDetail = " + 
				fileDetail.getFileName() + "  " + fileDetail.getName());
		
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

}
