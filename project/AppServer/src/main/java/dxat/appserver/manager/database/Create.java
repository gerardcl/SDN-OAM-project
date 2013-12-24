package dxat.appserver.manager.database;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dxat.appserver.manager.exceptions.FlowAlreadyExistsException;
import dxat.appserver.manager.exceptions.OrgAlreadyExistsException;
import dxat.appserver.manager.exceptions.OrgNotFoundException;
import dxat.appserver.manager.exceptions.TerminalAlreadyExistsException;
import dxat.appserver.manager.exceptions.UserAlreadyExistsException;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgTerminal;
import dxat.appserver.manager.pojos.OrgUser;
import dxat.appserver.manager.pojos.TOrg;

public class Create {

	private static final String OCOLLECTION = "ORG_COLLECTION";
	//private static final String UCOLLECTION = "USER_COLLECTION";
	//private static final String TCOLLECTION = "TERMINAL COLLECTION";
	//private static final String FCOLLECTION = "FLOW_COLLECTION";

	DBAccess dbaccess = new DBAccess();
	DB db = dbaccess.setDb();

	public void createOrg(TOrg torg) throws OrgAlreadyExistsException {

		DBCollection collection = getCollection(OCOLLECTION);

		if (!existsOrg(collection, torg.getIdentifier())) {
			DBObject doc = new BasicDBObject();
			List<BasicDBObject> users 	  = new ArrayList<BasicDBObject>();
			List<BasicDBObject> terminals = new ArrayList<BasicDBObject>();
			List<BasicDBObject> flows	  = new ArrayList<BasicDBObject>();
			users.add(null);
			terminals.add(null);
			flows.add(null);
			
			doc.put("identifier",torg.getIdentifier());
			doc.put("name", torg.getName());
			doc.put("NIF", torg.getNIF());
			doc.put("bankAccount", torg.getBankAccount());
			doc.put("telephone", torg.getTelephone());
			doc.put("users", users);
			doc.put("terminals", terminals);
			doc.put("flows", flows);

			collection.insert(doc);
			
		} else {
			dbaccess.closeConn();
			throw new OrgAlreadyExistsException("Organization with identifier "
					+ torg.getIdentifier() + " already exists.");
		}
		dbaccess.closeConn();
	}
	/*
	public void CreateUser(OrgUser user, String idOrg) throws OrgNotFoundException {
		DBCollection collection = getCollection(OCOLLECTION);

		if (!existsOrg(collection, idOrg)) {
			
			
			
			DBObject doc = new BasicDBObject();
			DBObject dbObject = (DBObject) JSON.parse(new Gson().toJson(user));
			doc.putAll(dbObject);
			collection.insert(doc);
		} else {
			dbaccess.closeConn();
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + " does not exists.");
		}
		dbaccess.closeConn();
	}
	
	public void insertFlow(OrgFlow flow, String idOrg) throws FlowAlreadyExistsException {
		DBCollection collection = getCollection(FCOLLECTION);

		if (!existsElement(collection, flow.getIdentifier())) {
			DBObject doc = new BasicDBObject();
			DBObject dbObject = (DBObject) JSON.parse(new Gson().toJson(flow));
			doc.putAll(dbObject);
			collection.insert(doc);
		} else {
			dbaccess.closeConn();
			throw new FlowAlreadyExistsException("Flow with identifier "
					+ flow.getIdentifier());
		}
		dbaccess.closeConn();
	}

	public void insertTerminal(OrgTerminal terminal, String idOrg)
			throws TerminalAlreadyExistsException {
		DBCollection collection = getCollection(TCOLLECTION);

		if (!existsElement(collection, terminal.getIdentifier())) {
			DBObject doc = new BasicDBObject();
			DBObject dbObject = (DBObject) JSON.parse(new Gson()
					.toJson(terminal));
			doc.putAll(dbObject);
			collection.insert(doc);
		} else {
			dbaccess.closeConn();
			throw new TerminalAlreadyExistsException(
					"Terminal with identifier " + terminal.getIdentifier());
		}
		dbaccess.closeConn();
	}
	
	public DBCursor getOrg(DBCollection collection, String id) throws OrgNotFoundException {
		BasicDBObject query = new BasicDBObject("identifier", id);
		DBCursor cursor  	= collection.find(query);
		if (!existsOrg(collection, id)) {

		}else{
			
		}
	}
	*/
	
	public boolean existsOrg(DBCollection collection, String id) {
		BasicDBObject query = new BasicDBObject("identifier", id);
		DBCursor cursor 	= collection.find(query);
		if(cursor.count()!=0) return true;
		else return false;
		
	}
	
	public DBCollection getCollection(String colName) {
		if (db.collectionExists(colName)) {
			return db.getCollection(colName);
		} else {
			return db.createCollection(colName, null);
		}
	}
	
}