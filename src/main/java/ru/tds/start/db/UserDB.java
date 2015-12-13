package ru.tds.start.db;

import org.bson.Document;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.tds.start.core.User;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.*;

import static com.mongodb.client.model.Filters.eq;

public class UserDB {
    final static Logger logger = LoggerFactory.getLogger(UserDB.class);
    private static MongoClient mongo = new MongoClient("localhost");
    private static MongoDatabase db;

    public static Document getUserDocByLoginPassword(Document doc) {
        JSONObject jsonObject = new JSONObject(doc.toJson());
        String login = jsonObject.getString("login");
        String password = jsonObject.getString("password");
        db = mongo.getDatabase("photodb");
        MongoCollection<Document> collection = db.getCollection("users");
        Document document = collection.find(eq("login", login)).first();

        if (document != null && document.getString("password").equals(password)) {
            return document;
        }
        return null;
    }

    public static void createUser(Document doc) {
        db = mongo.getDatabase("photodb");
        MongoCollection<Document> collection = db.getCollection("users");
        collection.insertOne(doc);
    }

    public static void updateUser(Document doc) {
        String login = doc.getString("login");
        if (login == null)
            return;

        db = mongo.getDatabase("photodb");
        MongoCollection<Document> collection = db.getCollection("users");
        Document documentDetected = collection.find(eq("login", login)).first();
        collection.updateOne(documentDetected, new BasicDBObject("$set", doc));
    }

    public static void deleteUser(Document doc) {
        String login = doc.getString("login");
        if (login == null)
            return;

        db = mongo.getDatabase("photodb");
        MongoCollection<Document> collection = db.getCollection("users");
        Document documentDetected = collection.find(eq("login", login)).first();
        collection.deleteOne(documentDetected);
    }

    public static User getUserById(String userId) {
        db = mongo.getDatabase("photodb");
        MongoCollection<Document> collection = db.getCollection("users");
        Document document = collection.find(eq("_id", userId)).first();

        if (document == null) {
            logger.warn("===== User not found");
            return null;
        }

        User user = new User(document.getString("_id"),
                document.getString("login"), document.getString("password"),
                document.getString("fullname"));

        return user;
    }

    public static Document getUserDocById(String userId) {
        db = mongo.getDatabase("photodb");
        MongoCollection<Document> collection = db.getCollection("users");
        Document document = collection.find(eq("_id", userId)).first();

        if (document == null) {
            logger.error("===== User not found");
            return null;
        }

        return document;
    }

    public static String getFullnameById(String userId) {
        db = mongo.getDatabase("photodb");
        MongoCollection<Document> collection = db.getCollection("users");
        Document document = collection.find(eq("_id", userId)).first();

        if (document == null) {
            logger.error("===== User not found");
            return null;
        }

        return document.getString("fullname");
    }

}
