package dxat.appserver.manager.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dxat.appserver.manager.exceptions.OrgNotFoundException;

public class Delete {
	
	private static final String OCOLLECTION   = "ORG_COLLECTION";
	private static final String SOCOLLECTION  = "SAVED_ORG_COLLECTION";
	private static final String SUCOLLECTION  = "SAVED_USER_COLLECTION";
	private static final String STCOLLECTION  = "SAVED_TERMINAL COLLECTION";
	private static final String SFCOLLECTION  = "SAVED_FLOW_COLLECTION";

	DBAccess dbaccess = new DBAccess();
	DB db = dbaccess.setDb();
	
	public void deleteOrg(String idOrg) throws OrgNotFoundException {
		DBCollection collection = db.getCollection(OCOLLECTION);
		if (existsOrg(collection, idOrg)) {
			saveOrg(idOrg);
			BasicDBObject query = new BasicDBObject("identifier", idOrg);
			collection.remove(query);
		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + " does not exists.");
		}

	}
	
	public void deleteUser(String idOrg){
		
	}
	public void deleteTerminal(String idOrg){
	
	}
	public void deleteFlow(String idOrg){
		
	}

	public void saveOrg(String idOrg) {
		
		DBCollection collection = db.getCollection(OCOLLECTION);
		DBCollection collectionSave = db.getCollection(SOCOLLECTION);
		BasicDBObject query = new BasicDBObject("identifier", idOrg);
		DBObject documentOrg = collection.findOne(query);
		collectionSave.insert(documentOrg);
	}
	public void saveUser(String idOrg){
		
	}
	public void saveTerminal(String idOrg){
		
	}
	public void saveFlow(String idOrg){
		
	}
	
	public boolean existsOrg(DBCollection collection, String id) {
		BasicDBObject query = new BasicDBObject("identifier", id);
		DBCursor cursor 	= collection.find(query);
		if(cursor.count()!=0) return true;
		else return false;
		
	}
	
}
