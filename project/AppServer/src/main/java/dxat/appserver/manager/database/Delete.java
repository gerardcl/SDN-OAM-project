package dxat.appserver.manager.database;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dxat.appserver.manager.exceptions.FlowNotFoundException;
import dxat.appserver.manager.exceptions.OrgNotFoundException;
import dxat.appserver.manager.exceptions.TerminalNotFoundException;
import dxat.appserver.manager.exceptions.UserNotFoundException;

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
			saveElement(idOrg,"orgs");
			BasicDBObject query = new BasicDBObject("identifier", idOrg);
			collection.remove(query);
		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + " does not exists.");
		}

	}
	
	public void deleteUser(String idOrg, String id)
			throws UserNotFoundException, OrgNotFoundException {

		DBCollection collection = db.getCollection(OCOLLECTION);
		if (existsOrg(collection, idOrg)) {
			if (existsElement(collection, idOrg, id, "users")) {
				saveElement(idOrg, "users");
				DBObject match = new BasicDBObject("identifier", idOrg);
				DBObject update = new BasicDBObject("users", new BasicDBObject(
						"identifier", id));
				collection.update(match, new BasicDBObject("$pull", update));
			} else {
				throw new UserNotFoundException("User with identifier " + id
						+ "does not exists.");
			}
		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + "does not exists.");
		}

	}

	public void deleteTerminal(String idOrg, String id)
			throws TerminalNotFoundException, OrgNotFoundException {

		DBCollection collection = db.getCollection(OCOLLECTION);
		if (existsOrg(collection, idOrg)) {
			if (existsElement(collection, idOrg, id, "terminals")) {
				saveElement(idOrg, "terminals");
				DBObject match = new BasicDBObject("identifier", idOrg);
				// DBObject documentOrg = collection.findOne(query);
				DBObject update = new BasicDBObject("terminals",
						new BasicDBObject("identifier", id));
				collection.update(match, new BasicDBObject("$pull", update));
			} else {
				throw new TerminalNotFoundException("Terminal with identifier "
						+ id + "does not exists.");
			}
		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + "does not exists.");
		}

	}

	public void deleteFlow(String idOrg, String id)
			throws FlowNotFoundException, OrgNotFoundException {

		DBCollection collection = db.getCollection(OCOLLECTION);
		if (existsOrg(collection, idOrg)) {
			if (existsElement(collection, idOrg, id, "flows")) {
				saveElement(idOrg, "flows");
				DBObject match = new BasicDBObject("identifier", idOrg);
				// DBObject documentOrg = collection.findOne(query);
				DBObject update = new BasicDBObject("flows", new BasicDBObject(
						"identifier", id));
				collection.update(match, new BasicDBObject("$pull", update));
			} else {
				throw new FlowNotFoundException("Flow with identifier " + id
						+ "does not exists.");
			}
		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + "does not exists.");
		}

	}
	
	public void saveElement(String idOrg, String type) {
		DBCollection collection = db.getCollection(OCOLLECTION);
		DBCollection collectionSave = null;

		if (type.equals("orgs")) {
			collectionSave = db.getCollection(SOCOLLECTION);
		} else if (type.equals("users")) {
			collectionSave = db.getCollection(SUCOLLECTION);
		} else if (type.equals("flows")) {
			collectionSave = db.getCollection(SFCOLLECTION);
		} else if (type.equals("terminals")) {
			collectionSave = db.getCollection(STCOLLECTION);

		}
		BasicDBObject query = new BasicDBObject("identifier", idOrg);
		DBObject documentOrg = collection.findOne(query);

		collectionSave.insert(documentOrg);

	}
	/*
	public void saveUser(String idOrg,String id){
		DBCollection collection = db.getCollection(OCOLLECTION);
		DBCollection collectionSave = db.getCollection(SUCOLLECTION);
		//BasicDBObject query = new BasicDBObject("identifier", idOrg);
		//DBObject documentOrg = collection.findOne(query);
		DBObject query = new BasicDBObject();//("identifier", idOrg);
		query.put("users.identifier", id);
		DBObject subarray = new BasicDBObject("users.$", 1);
		DBObject userdata = collection.findOne(query,subarray);
		
		System.out.println("userdata: " + userdata);
		collectionSave.insert(userdata);
		
	}
	public void saveUser(String idOrg){
		
	}
	public void saveTerminal(String idOrg){
		
	}
	public void saveFlow(String idOrg){
		
	}
	*/
	public boolean existsOrg(DBCollection collection, String id) {
		BasicDBObject query = new BasicDBObject("identifier", id);
		DBCursor cursor 	= collection.find(query);
		if(cursor.count()!=0) return true;
		else return false;
		
	}
	public boolean existsElement(DBCollection collection, String idOrg, String id, String type){
		BasicDBObject query = new BasicDBObject("identifier", idOrg);
		query.put("users.identifier", id);
		DBCursor cursor = collection.find(query);
		if(cursor.count()!=0) return true;
		else return false;
	}
}
