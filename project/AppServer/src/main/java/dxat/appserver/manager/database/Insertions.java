package dxat.appserver.manager.database;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import dxat.appserver.manager.exceptions.FlowAlreadyExistsException;
import dxat.appserver.manager.exceptions.OrgAlreadyExistsException;
import dxat.appserver.manager.exceptions.TerminalAlreadyExistsException;
import dxat.appserver.manager.exceptions.UserAlreadyExistsException;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgTerminal;
import dxat.appserver.manager.pojos.OrgUser;
import dxat.appserver.manager.pojos.TOrg;

public class Insertions {

	private static final String OCOLLECTION = "ORG_COLLECTION";
	private static final String UCOLLECTION = "USER_COLLECTION";
	private static final String TCOLLECTION = "TERMINAL COLLECTION";
	private static final String FCOLLECTION = "FLOW_COLLECTION";

	DBAccess dbaccess = new DBAccess();
	DB db = dbaccess.setDb();

	public void insertOrg(TOrg torg) throws OrgAlreadyExistsException {

		DBCollection collection = getCollection(OCOLLECTION);

		if (!existsElement(collection, torg.getIdentifier())) {
			DBObject doc = new BasicDBObject();
			DBObject dbObject = (DBObject) JSON.parse(new Gson().toJson(torg));
			doc.putAll(dbObject);
			collection.insert(doc);
		} else {
			dbaccess.closeConn();
			throw new OrgAlreadyExistsException("Organization with identifier "
					+ torg.getIdentifier() + " already exists.");
		}
		dbaccess.closeConn();
	}

	public void insertUser(OrgUser user) throws UserAlreadyExistsException {
		DBCollection collection = getCollection(UCOLLECTION);

		if (!existsElement(collection, user.getIdentifier())) {
			DBObject doc = new BasicDBObject();
			DBObject dbObject = (DBObject) JSON.parse(new Gson().toJson(user));
			doc.putAll(dbObject);
			collection.insert(doc);
		} else {
			dbaccess.closeConn();
			throw new UserAlreadyExistsException("User with identifier "
					+ user.getIdentifier() + " already exists.");
		}
		dbaccess.closeConn();
	}

	public void insertFlow(OrgFlow flow) throws FlowAlreadyExistsException {
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

	public void insertTerminal(OrgTerminal terminal)
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

	public boolean existsElement(DBCollection collection, String id) {
		BasicDBObject query = new BasicDBObject("identifier", id);
		DBObject result = collection.findOne(query);
		if (result != null)
			return true;
		else
			return false;
	}

	public DBCollection getCollection(String colName) {
		if (db.collectionExists(colName)) {
			return db.getCollection(colName);
		} else {
			return db.createCollection(colName, null);
		}
	}
}