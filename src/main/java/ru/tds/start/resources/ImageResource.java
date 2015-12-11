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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        // if Authenticator not authenticates
        if (user.isNull() == true)
            return false;

        ImageDB.loadImageToDB(inputStreamImages, fileDetail.getFileName(), user);
        return true;
    }

    @GET
    @Path("/latestimage")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUrlOfLatestImage(@Auth User user)
            throws AuthenticationException {
        if (user.isNull() == true)
            return Response.ok().build();

        // to generate url for image to the main page  
        String imageId = ImageDB.getIdOfLatestImage();
        String htmlRespons;
        if (imageId != null) {
            htmlRespons = "<img class='img_mini' src='http://localhost:8080/photohost/imagebyid?id="
                    + imageId + "'>";
        } else {
            htmlRespons = "<p>The database does not contain images</p>";
        }
        return Response.ok(htmlRespons).build();
    }

    @GET
    @Path("/myimages")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateUrlsOfMyImages(@Auth User user)
            throws AuthenticationException {
        if (user.isNull() == true)
            return Response.ok().build();

        List<String> listImageId = ImageDB
                .getListImageIdByUserId(user.get_Id());
        if (listImageId == null)
            return Response.ok().build();
        if (listImageId.isEmpty())
            return Response.ok().build();

        // to calculate length of array
        int listSize = listImageId.size();

        // generation urls of the images
        String htmlRespons = TAG_TABLE_OPEN;
        int i = 1;
        for (String imageId : listImageId) {
            // to put 4 images in a row on the page  
            if ((i % 4) != 0) {
                htmlRespons += TAG_TD_OPEN
                        + "<img class='img_mini' src='http://localhost:8080/photohost/imagebyid?id="
                        + imageId + "'>" + TAG_TD_CLOSE;
                // if the image already #4 in the row 
            } else {
                htmlRespons += TAG_TD_OPEN
                        + "<img class='img_mini' src='http://localhost:8080/photohost/imagebyid?id="
                        + imageId + "'>" + TAG_TD_CLOSE + TAG_TR_CLOSE;
                // if will be one more images, the to begin new row  
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
            return Response.ok().build();
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
    public boolean deletePhotoByImageId(@Auth User user,
            @QueryParam("id") String id) throws AuthenticationException {
        if (user.isNull() == true)
            return false;

        return ImageDB.deletePhotoByImageId(id);
    }

    @GET
    @Path("/like")
    @Consumes(MediaType.APPLICATION_JSON)
    public int likeIncrement(@Auth User user, @QueryParam("id") String id)
            throws AuthenticationException {
        if (user.isNull() == true)
            return -1;

        return ImageDB.likeIncrement(id, user.get_Id());
    }

    @GET
    @Path("/nextimage")
    @Consumes(MediaType.APPLICATION_JSON)
    public String getNextImage(@Auth User user, @QueryParam("id") String id)
            throws AuthenticationException {
        if (user.isNull() == true)
            return null;

        return ImageDB.getNextImageId(id);
    }

}
