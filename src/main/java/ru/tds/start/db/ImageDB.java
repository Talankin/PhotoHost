package ru.tds.start.db;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
	public static void loadImageToDB(InputStream inputStream, String fileName) {
		mongo = new MongoClient(SERVER);
		db = mongo.getDB(DBNAME);

		// создаем объект GridFS
		GridFS gridFS = new GridFS(db); 
		
		// сохраняем фото в mongodb
		GridFSInputFile gridFSInputFile;
		try {
			gridFSInputFile = gridFS.createFile(inputStream);
			gridFSInputFile.setFilename(fileName);
			//gridFSInputFile.setMetaData(new DBObject("owner":"girl"));
			gridFSInputFile.save();
		} catch (Exception e) {
			System.out.println("=============================== Exception. Не удалось сохранить файл в mongodb\n" + e.getMessage());
		} finally {
			mongo.close();
		}
	}

	@SuppressWarnings("deprecation")
	public static void loadImageToDBFromHDD(String fileWithPath) {
		mongo = new MongoClient(SERVER);
		db = mongo.getDB(DBNAME);

		// загружаем нашу картинку
		File image = new File(fileWithPath);
		
		// создаем объект GridFS
		GridFS gridFS = new GridFS(db); 
		
		// сохраняем фото в mongodb
		GridFSInputFile gridFSInputFile;
		try {
			gridFSInputFile = gridFS.createFile(image);
			gridFSInputFile.setFilename("siski");
			gridFSInputFile.save();
		} catch (FileNotFoundException e) {
			System.out.println("=============================== FileNotFoundException");
		} catch (IOException e) {
			System.out.println("=============================== IOException. Не удалось сохранить файл в mongodb\n");
		} catch (Exception e) {
			System.out.println("=============================== Exception. Не удалось сохранить файл в mongodb\n" + e.getMessage());
		} finally {
			mongo.close();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static InputStream getLatestImageFromDB() {
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
			System.out.println("::::::::::::::::::::::::::::: " + imageGFS);
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
	public static InputStream getImageByNameFromDB (String imageName) {
		mongo = new MongoClient(SERVER);
		db = mongo.getDB(DBNAME);
		GridFS gridFS = new GridFS(db);
		
		// ищем картинку в mongodb
		GridFSDBFile imageGFS = gridFS.findOne(imageName);
		//mongo.close();
		// !!!!!!!!! сделать проверку на imageGFS == null 
		
		/* читаем поток байтов из картинки
		*  при этом используем буфферизированный поток BufferedInputStream - так быстрее,
		*/
		InputStream inputStream = new BufferedInputStream(imageGFS.getInputStream());
		
		return inputStream;
	}
}
