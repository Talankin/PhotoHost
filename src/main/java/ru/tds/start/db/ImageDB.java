package ru.tds.start.db;

import static com.mongodb.client.model.Filters.eq;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.tds.start.core.User;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class ImageDB {
    final static Logger logger = LoggerFactory.getLogger(ImageDB.class);
    private final static String SERVER = "localhost";
    private final static String DBNAME = "photodb";
    //private final static String SERVER = config.getDBServer();
    //private final static String DBNAME = PhotoHostConfiguration.getDBName();;
    private static MongoClient mongo = new MongoClient(SERVER);
    private static DB db;
    private static MongoDatabase mDb;
/*
    public ImageDB (String server, String dbName) {
        this.server = server;
        this.dbName = dbName;
        logger.info("============================" + server + "  " + dbName);
    }
*/
    @SuppressWarnings("deprecation")
    public static void loadImageToDB(InputStream inputStream, String fileName,
            User user) {
        db = mongo.getDB(DBNAME);
        GridFS gridFS = new GridFS(db);

        // save image in mongodb
        GridFSInputFile gridFSInputFile;
        try {
            // create fields of metadata in mongodb
            int likes = 0;
            DBObject metadata = new BasicDBObject("userId", user.get_Id());
            metadata.put("imageName", "");
            metadata.put("description", "");
            metadata.put("likes", likes);
            List<BasicDBObject> likesListUserId = new ArrayList<>();
            metadata.put("likesListUserId", likesListUserId);

            gridFSInputFile = gridFS.createFile(inputStream);
            gridFSInputFile.setFilename(fileName);
            gridFSInputFile.setMetaData(metadata);
            gridFSInputFile.save();
        } catch (Exception e) {
            logger.error("===== Exception. Unable to save file in mongodb\n"
                    + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public static void loadImageToDBFromHDD(String fileWithPath) {
        db = mongo.getDB(DBNAME);

        File image = new File(fileWithPath);
        GridFS gridFS = new GridFS(db);

        GridFSInputFile gridFSInputFile;
        try {
            gridFSInputFile = gridFS.createFile(image);
            gridFSInputFile.setFilename("siski");
            gridFSInputFile.save();
        } catch (FileNotFoundException e) {
            logger.error("===== FileNotFoundException");
        } catch (IOException e) {
            logger.error("===== IOException. Unable to save file in mongodb\n");
        } catch (Exception e) {
            logger.error("===== IOException. Unable to save file in mongodb\n"
                    + e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    public static String getIdOfLatestImage() {
        db = mongo.getDB(DBNAME);
        GridFS gridFS = new GridFS(db);
        GridFSDBFile imageGFS = null;

        // to create rule of sort by field "uploadDate" (in up is latest)
        DBObject sort = new BasicDBObject("uploadDate", -1);
        DBObject dbObject = new BasicDBObject();

        try {
            // getting the first image from sort array  
            imageGFS = gridFS.find(dbObject, sort).get(0);
            String imageId = imageGFS.getId().toString();

            return imageId;
        } catch (IndexOutOfBoundsException e) {
            logger.error("===== IndexOutOfBoundsException : No pictures in the database");
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public static InputStream getImageById(String id) {
        // to check HEX validation of id
        if (!ObjectId.isValid(id))
            return null;
        // getting ObjectId from id  
        ObjectId objectId = new ObjectId(id);

        db = mongo.getDB(DBNAME);
        GridFS gridFS = new GridFS(db);
        GridFSDBFile imageGFS = null;

        // search image in mongodb by objectId
        imageGFS = gridFS.findOne(objectId);

        if (imageGFS != null) {
            /* reading stream of bytes from image
             * using buffered stream - is faster
             */
            InputStream inputStream = new BufferedInputStream(
                    imageGFS.getInputStream());

            return inputStream;
        } else
            return null;
    }

    @SuppressWarnings("deprecation")
    public static List<String> getListImageIdByUserId(String userId) {
        db = mongo.getDB(DBNAME);
        GridFS gridFS = new GridFS(db);
        List<String> listImageId = new ArrayList<String>();

        DBObject sort = new BasicDBObject("uploadDate", -1);
        DBObject query = new BasicDBObject("metadata.userId", userId);

        List<GridFSDBFile> listGridFS = gridFS.find(query, sort);
        if (!listGridFS.isEmpty()) {
            // to create array of image id 
            for (GridFSDBFile imageGFS : listGridFS) {
                listImageId.add(imageGFS.getId().toString());
            }
            return listImageId;

        } else {
            logger.info("===== No pictures in the array");
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public static String getMetaDataByImageId(String id) {
        if (!ObjectId.isValid(id))
            return null;
        ObjectId objectId = new ObjectId(id);

        db = mongo.getDB(DBNAME);
        GridFS gridFS = new GridFS(db);
        GridFSDBFile imageGFS = null;

        imageGFS = gridFS.findOne(objectId);
        if (imageGFS != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            // to get metadata document 
            DBObject metadataObject = imageGFS.getMetaData();
            String userId = metadataObject.get("userId").toString();
            String author = UserDB.getFullnameById(userId);
            String uploadDate = dateFormat.format(imageGFS.getUploadDate());
            // добавляем к коллекции метадата еще данные для страницы
            metadataObject.put("author", author);
            metadataObject.put("uploadDate", uploadDate);

            return metadataObject.toString();
        } else {
            logger.info("===== imageGFS for imageId = {} in mongodb is null",
                    id);
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean updateMetadata(Document doc, String id) {
        if (!ObjectId.isValid(id))
            return false;
        ObjectId objectId = new ObjectId(id);

        db = mongo.getDB(DBNAME);
        GridFS gridFS = new GridFS(db);
        GridFSDBFile imageGFS = null;

        // to find the document in mongodb for updating 
        imageGFS = gridFS.findOne(objectId);

        // to get list of fields for updating  
        JSONObject jsonObject = new JSONObject(doc.toJson());
        Iterator<String> listKeys = jsonObject.keys();

        if (imageGFS != null) {
            DBObject metadata = imageGFS.getMetaData();

            // to delete the fields, which will to update 
            while (listKeys.hasNext()) {
                String key = listKeys.next();
                metadata.removeField(key);
            }
            // to add the fields for updating
            metadata.putAll(doc);
            imageGFS.setMetaData(metadata);
            imageGFS.save();

            return true;
        } else {
            return false;
        }
    }

    @SuppressWarnings("deprecation")
    public static boolean deletePhotoByImageId(String id) {
        if (!ObjectId.isValid(id))
            return false;
        ObjectId objectId = new ObjectId(id);

        db = mongo.getDB(DBNAME);
        GridFS gridFS = new GridFS(db);

        try {
            gridFS.remove(objectId);
        } catch (MongoException e) {
            logger.error("===== Image can not be removed from the database gridfs "
                    + e.getMessage());
            return false;
        }
        return true;
    }

    
    @SuppressWarnings("deprecation")
    public static boolean deletePhotosByUserId(String userId) {
        db = mongo.getDB(DBNAME);
        GridFS gridFS = new GridFS(db);
        DBObject query = new BasicDBObject("metadata.userId", userId);

        List<GridFSDBFile> listGridFS = gridFS.find(query);

        if (listGridFS.isEmpty()) {
            logger.info("===== No pictures in the array for removing");
            return false;
        }

        try {
            // to delete each image in array  
            for (GridFSDBFile imageGFS : listGridFS) {
                gridFS.remove(imageGFS);
            }
            return true;
        } catch (MongoException e) {
            logger.error("===== error removing images from mongodb gridfs "
                    + e.getMessage());
            return false;
        }
    }

    public static int likeIncrement(String id, String userId) {
        if (!ObjectId.isValid(id))
            return -1;
        ObjectId objectId = new ObjectId(id);

        mDb = mongo.getDatabase(DBNAME);
        MongoCollection<Document> collection = mDb.getCollection("fs.files");

        int likes = -1;
        try {
            // to find user, who set the like
            Document query = new Document("_id", objectId);
            query.put("metadata.likesListUserId", userId);
            Document document = collection.find(query).first();

            // if user already put the like, then to exit 
            if (document != null) {
                return -1;
            }

            document = collection.find(eq("_id", objectId)).first();
            // double update: to put the liked user and increment of like
            Document updateQuery = new Document("$push", new BasicDBObject(
                    "metadata.likesListUserId", userId));
            updateQuery.put("$inc", new BasicDBObject("metadata.likes", 1));
            collection.updateOne(document, updateQuery);

            // to return the count of likes from mongodb 
            document = collection.find(eq("_id", objectId)).first();
            Document metadata = (Document) document.get("metadata");
            likes = metadata.getInteger("likes");

            return likes;
        } catch (Exception e) {
            logger.error("===== Failed to update the like\n" + e.getMessage());
            return -1;
        }
    }

    @SuppressWarnings("deprecation")
    public static String getNextImageId(String id) {
        if (!ObjectId.isValid(id))
            return null;

        db = mongo.getDB(DBNAME);
        GridFS gridFS = new GridFS(db);
        List<String> listImageId = new ArrayList<String>();

        DBObject sort = new BasicDBObject("uploadDate", -1);
        DBObject query = new BasicDBObject();

        List<GridFSDBFile> listGridFS = gridFS.find(query, sort);
        if (!listGridFS.isEmpty()) {
            for (GridFSDBFile imageGFS : listGridFS) {
                listImageId.add(imageGFS.getId().toString());
            }

            // to calculate length of array of image 
            int lengthOfList = listImageId.size();
            // to get the index current of imageId in array 
            int indexOfImage = listImageId.indexOf(id);

            // to get the next element of array and to check out of range
            if (indexOfImage == (lengthOfList - 1)) {
                indexOfImage = 0;
            } else {
                indexOfImage++;
            }
            String nextImageId = listImageId.get(indexOfImage);
            return nextImageId;

        } else {
            logger.info("===== No pictures in the array");
            return id;
        }
    }

}
