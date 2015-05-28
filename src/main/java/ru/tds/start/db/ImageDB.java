package ru.tds.start.db;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import ru.tds.start.core.User;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

public class ImageDB {
	private final static String SERVER = "localhost";
	private final static String DBNAME = "photodb";
	private static MongoClient mongo;
	private static DB db;
	
	@SuppressWarnings("deprecation")
	public static void loadImageToDB(InputStream inputStream, String fileName, User user) {
		mongo = new MongoClient(SERVER);
		db = mongo.getDB(DBNAME);

		// создаем объект GridFS
		GridFS gridFS = new GridFS(db); 
		
		// сохраняем фото в mongodb
		GridFSInputFile gridFSInputFile;
		try {
			// создаем поля метаданных
			int likes = 3;
			DBObject metadata = new BasicDBObject("userId",user.get_Id());
			metadata.put("imageName","");
			metadata.put("description","");
			metadata.put("likes", likes);
			
			gridFSInputFile = gridFS.createFile(inputStream);
			gridFSInputFile.setFilename(fileName);
			gridFSInputFile.setMetaData(metadata);
			gridFSInputFile.save();
		} catch (Exception e) {
			System.err.println("=============================== Exception. Не удалось сохранить файл в mongodb\n" + e.getMessage());
		} finally {
			mongo.close();
		}
	}

	@SuppressWarnings("deprecation")
	public static void loadImageToDBFromHDD(String fileWithPath) {
		mongo = new MongoClient(SERVER);
		db = mongo.getDB(DBNAME);

		File image = new File(fileWithPath);
		GridFS gridFS = new GridFS(db); 
		
		// сохраняем фото в mongodb
		GridFSInputFile gridFSInputFile;
		try {
			gridFSInputFile = gridFS.createFile(image);
			gridFSInputFile.setFilename("siski");
			gridFSInputFile.save();
		} catch (FileNotFoundException e) {
			System.err.println("=============================== FileNotFoundException");
		} catch (IOException e) {
			System.err.println("=============================== IOException. Не удалось сохранить файл в mongodb\n");
		} catch (Exception e) {
			System.err.println("=============================== Exception. Не удалось сохранить файл в mongodb\n" + e.getMessage());
		} finally {
			mongo.close();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static InputStream getLatestImage() {
		mongo = new MongoClient(SERVER);
		db = mongo.getDB(DBNAME);
		GridFS gridFS = new GridFS(db);
		GridFSDBFile imageGFS = null;
		
		// создаем правило сортировки по полю "uploadDate" (наверху самые свежие)
		DBObject sort = new BasicDBObject("uploadDate", -1);
		DBObject dbObject = new BasicDBObject();

		// берем самую первую картинку из выборки с учетом сортировки
		imageGFS = gridFS.find(dbObject, sort).get(0);
		if (imageGFS != null) {
			/* читаем поток байтов из картинки
			 * при этом используем буфферизированный поток BufferedInputStream - так быстрее,
			 */
			InputStream inputStream = new BufferedInputStream(imageGFS.getInputStream());
			return inputStream;
		} else 
			return null;
	}
	
	@SuppressWarnings("deprecation")
	public static InputStream getImageById (String id) {
		// проверяем HEX валидность id
		if (!ObjectId.isValid(id))
			return null;
		// получаем из id - ObjectId
		ObjectId objectId = new ObjectId(id);
		
		mongo = new MongoClient(SERVER);
		db = mongo.getDB(DBNAME);
		GridFS gridFS = new GridFS(db);
		GridFSDBFile imageGFS = null;
		
		// ищем картинку в mongodb по ее objectId 
		imageGFS = gridFS.findOne(objectId);
		
		if (imageGFS != null) {
			//mongo.close();

			/* читаем поток байтов из картинки
			 * при этом используем буфферизированный поток BufferedInputStream - так быстрее,
			 */
			InputStream inputStream = new BufferedInputStream(imageGFS.getInputStream());
			
			return inputStream;
		} else 
			return null;
	}

	@SuppressWarnings("deprecation")
	public static List<String> getListImageIdByUserId(String userId) {
		mongo = new MongoClient(SERVER);
		db = mongo.getDB(DBNAME);
		GridFS gridFS = new GridFS(db);
		List<String> listImageId = new ArrayList<String>();
		
		// создаем правило сортировки по полю "uploadDate" (наверху самые свежие)
		DBObject sort = new BasicDBObject("uploadDate", -1);
		DBObject query = new BasicDBObject("metadata.userId", userId);
		
		// получаем выборку картинок по запросу
		List<GridFSDBFile> listGridFS = gridFS.find(query, sort);
		
		if (!listGridFS.isEmpty()) {
			// создаем массив id картинок
			for (GridFSDBFile imageGFS : listGridFS) {
				listImageId.add(imageGFS.getId().toString());
			}
			return listImageId;
			
		} else {
			System.err.println("ээээээээээээээээээээээээээээээээээээээ  говорит getListImageId() : массив картинок нулевой.");
			return null;
		}
	}

}
