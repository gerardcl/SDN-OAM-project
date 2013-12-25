package dxat.appserver.manager.database;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class DBAccess {
	
	private  MongoClient  conn;
	private  DB db;

	public DB setDb(){
		try {
			MongoClient conn = new MongoClient("localhost", 27017);
			db = conn.getDB("manager");
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return db;
	}	

}
