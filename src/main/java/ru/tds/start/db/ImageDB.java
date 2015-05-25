package ru.tds.start.db;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.codec.binary.Base64InputStream;
import com.mongodb.DB;
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
	public static void loadImageToDB(String fileWithPath) {
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
			
			//gridFSInputFile.setMetaData(new DBObject("owner":"girl"));
			gridFSInputFile.save();
			mongo.close();
		} catch (FileNotFoundException e) {
			System.out.println("=============================== FileNotFoundException");
			mongo.close();
		} catch (IOException e) {
			System.out.println("=============================== IOException. Не удалось сохранить файл в mongodb\n");
			mongo.close();
		} catch (Exception e) {
			System.out.println("=============================== Exception. Не удалось сохранить файл в mongodb\n" + e.getMessage());
			mongo.close();
		}
	}
	
	@SuppressWarnings("deprecation")
	public static InputStream getImageFromDB (String imageName) {
		mongo = new MongoClient(SERVER);
		db = mongo.getDB(DBNAME);
		GridFS gridFS = new GridFS(db);
		// ищем картинку в mongodb
		GridFSDBFile imageGFS = gridFS.findOne(imageName);
		//mongo.close();
		// !!!!!!!!! сделать проверку на imageGFS == null 
		System.out.println("::::::::::::::::::::::::::::: " + imageName + "   " + imageGFS);
		//System.out.println("::::::::::::::::::::::::::::: взяли имя файла из монго : " + imageGFS.getFilename());
		
		/* читаем поток байтов из картинки
		*  при этом используем буфферизированный поток BufferedInputStream - так быстрее,
		*/
		InputStream inputStream = new BufferedInputStream(imageGFS.getInputStream());
		
		// кодируем поток в base64, иначе залить в html не получилось
		//Base64InputStream inputStreamBase64 = new Base64InputStream(inputStream, true);
		
		return inputStream;
	}
}
