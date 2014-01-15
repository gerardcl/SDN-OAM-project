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
			doc.put("identifier",torg.getIdentifier());
			doc.put("name", torg.getName());
			doc.put("NIF", torg.getNIF());
			doc.put("bankAccount", torg.getBankAccount());
			doc.put("telephone", torg.getTelephone());
			doc.put("users", users);
			doc.put("terminals", terminals);
			doc.put("flows", flows);
			doc.put("isOAM",torg.isOAM());

			collection.insert(doc);
			
		} else {
			
			throw new OrgAlreadyExistsException("Organization with identifier "
					+ torg.getIdentifier() + " already exists.");
		}
		
	}
	
	public void createUser(OrgUser user, String idOrg)
			throws OrgNotFoundException, UserAlreadyExistsException {
		DBCollection collection = getCollection(OCOLLECTION);

		if (existsOrg(collection, idOrg)) {
			if (!existsElement(collection, idOrg, user.getIdentifier(), "users")) {
				BasicDBObject updateOrg = new BasicDBObject("identifier", idOrg);
				DBObject org = collection.findOne(updateOrg);
				DBObject userdoc = new BasicDBObject();
				userdoc.put("identifier", user.getIdentifier());
				userdoc.put("name", user.getName());
				userdoc.put("email", user.getEmail());
				userdoc.put("password", user.getPassword());
				userdoc.put("telephone", user.getTelephone());
				userdoc.put("isAdmin", user.isAdmin());
				userdoc.put("isActive",user.isActive());

				BasicDBObject update = new BasicDBObject("$push",
						new BasicDBObject("users", userdoc));
				collection.update(org, update);
			} else {
				throw new UserAlreadyExistsException("User with identifier "
						+ user.identifier + "already exists.");
			}
		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + " does not exists.");
		}
	}
	
	public void createFlow(OrgFlow flow, String idOrg)
			throws FlowAlreadyExistsException, OrgNotFoundException {
		DBCollection collection = db.getCollection(OCOLLECTION);

		if (existsOrg(collection, idOrg)) {
			if (!existsElement(collection, idOrg, flow.getIdentifier(), "flows")) {
				BasicDBObject updateOrg = new BasicDBObject("identifier", idOrg);
				DBObject org = collection.findOne(updateOrg);
				DBObject userdoc = new BasicDBObject();
				userdoc.put("identifier", flow.getIdentifier());
				userdoc.put("name", flow.getName());
				userdoc.put("srcOTidentifier", flow.getSrcOTidentifier());
				userdoc.put("dstOTidentifier", flow.getDstOTidentifier());
				userdoc.put("srcPort", flow.getSrcPort());
				userdoc.put("dstPort", flow.getDstPort());
				userdoc.put("qos", flow.getQos());
				userdoc.put("bandwidth", flow.getBandwidth());
				userdoc.put("protocol", flow.getProtocol());
				userdoc.put("active", flow.isActive());

				BasicDBObject update = new BasicDBObject("$push",
						new BasicDBObject("flows", userdoc));
				collection.update(org, update);
			} else {
				throw new FlowAlreadyExistsException("Flow with identifier " + flow.getIdentifier() + "already exists.");
			}
		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + " does not exists.");
		}

	}
	
	public void createTerminal(OrgTerminal terminal, String idOrg)
			throws TerminalAlreadyExistsException, OrgNotFoundException {

		DBCollection collection = db.getCollection(OCOLLECTION);

		if (existsOrg(collection, idOrg)) {
			if (!existsElement(collection, idOrg, terminal.getIdentifier(),
					"terminals")) {
				BasicDBObject updateOrg = new BasicDBObject("identifier", idOrg);
				DBObject org = collection.findOne(updateOrg);

				DBObject userdoc = new BasicDBObject();
				userdoc.put("identifier", terminal.getIdentifier());
				userdoc.put("hostName", terminal.getHostName());
				userdoc.put("ipAddress", terminal.getIpAddress());
				userdoc.put("mac", terminal.getMac());
				userdoc.put("ifaceSpeed", terminal.getIfaceSpeed());
				userdoc.put("description", terminal.getDescription());
				userdoc.put("active",terminal.isActive());
				userdoc.put("assigned",terminal.isAssigned());
				
				BasicDBObject update = new BasicDBObject("$push",
						new BasicDBObject("terminals", userdoc));
				collection.update(org, update);
			} else {
				throw new TerminalAlreadyExistsException("Terminal with identifier " + terminal.getIdentifier() + "already exists.");
			}
		} else {
				throw new OrgNotFoundException("Organization with identifier " + idOrg + " does not exists.");
		}

	}
	
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
	
	
	public DBCollection getCollection(String colName) {
		if (db.collectionExists(colName)) {
			return db.getCollection(colName);
		} else {
			return db.createCollection(colName, null);
		}
	}
	
}