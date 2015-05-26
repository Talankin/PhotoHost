package ru.tds.start.db;

import org.bson.Document;
import ru.tds.start.core.User;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.*;

import static com.mongodb.client.model.Filters.eq;

public class UserDB {
	private static MongoClient mongo;
	private static MongoDatabase db;
	
	public static Document getUserDocByLoginPassword(String login, String password) {
		mongo = new MongoClient("localhost");
		db = mongo.getDatabase("photodb");
		MongoCollection<Document> collection = db.getCollection("users");
		Document document = collection.find(eq("login", login)).first();
		mongo.close();
		
		if (document != null && document.getString("password").equals(password)) {
			return document;
		}
		
		return null;
	}
	
	public static void createUser(Document doc) {
		mongo = new MongoClient("localhost");
		db = mongo.getDatabase("photodb");
		MongoCollection<Document> collection = db.getCollection("users");
		collection.insertOne(doc);
		mongo.close();
	}
	
	public static void updateUser(Document doc) {
		String login = doc.getString("login");
		if (login == null) 
			return;
		String fullname = doc.getString("fullname");
		String password = doc.getString("password");
		
		System.out.println("***********************     void updateUser : " + login + "  " + fullname + "  " + password);
		System.out.println("***********************     doc.toJson()" + doc.toJson());
		
		mongo = new MongoClient("localhost");
		db = mongo.getDatabase("photodb");
		MongoCollection<Document> collection = db.getCollection("users");
		Document documentDetected = collection.find(eq("login", login)).first();
		collection.updateOne(documentDetected, new BasicDBObject("$set", doc));
		mongo.close();
	}

	public static void deleteUser(Document doc) {
		String login = doc.getString("login");
		
		System.out.println("  ---------------------------- я в deleteUser() - удаляем юзера " + login);
		
		if (login == null) 
			return;

		mongo = new MongoClient("localhost");
		db = mongo.getDatabase("photodb");
		MongoCollection<Document> collection = db.getCollection("users");
		Document documentDetected = collection.find(eq("login", login)).first();
		collection.deleteOne(documentDetected);
		mongo.close();
		
		System.out.println("  ++++++++++++++++++++ я в deleteUser() - юзера удалили. возвращаемся в ресурс ");
	}

	public static User getUserById(String userId) {
		mongo = new MongoClient("localhost");
		db = mongo.getDatabase("photodb");
		MongoCollection<Document> collection = db.getCollection("users");
		Document document = collection.find(eq("_id", userId)).first();
		mongo.close();
		
		if (document == null) {
			System.out.println("User not found");
			return null;
		}
		
		User user = new User(
				document.getString("_id"),
				document.getString("login"),
				document.getString("password"),
				document.getString("fullname") );
		
		return user;
	}
	
	public static Document getUserDocById(String userId) {
		mongo = new MongoClient("localhost");
		db = mongo.getDatabase("photodb");
		MongoCollection<Document> collection = db.getCollection("users");
		Document document = collection.find(eq("_id", userId)).first();
		mongo.close();
		
		if (document == null) {
			System.out.println("User not found");
			return null;
		}
		
		return document;
	}
}