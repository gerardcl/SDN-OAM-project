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
import dxat.appserver.manager.pojos.TOrg;
import dxat.appserver.manager.pojos.OrgUser;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgTerminal;

public class Update {
	private static final String OCOLLECTION   = "ORG_COLLECTION";
	
	DBAccess dbaccess = new DBAccess();
	DB db = dbaccess.setDb();
	
	public void updateOrg(TOrg torg) throws OrgNotFoundException {

		DBCollection collection = db.getCollection(OCOLLECTION);
		if (existsOrg(collection, torg.getIdentifier())) {

			BasicDBObject updateOrg = new BasicDBObject("identifier",
					torg.getIdentifier());
			DBObject org = collection.findOne(updateOrg);
			DBObject userdoc = new BasicDBObject();

			userdoc.put("identifier", torg.getIdentifier());
			userdoc.put("name", torg.getName());
			userdoc.put("NIF", torg.getNIF());
			userdoc.put("telephone", torg.getTelephone());
			userdoc.put("bankAccount", torg.getBankAccount());
			userdoc.put("isOAM",torg.isOAM());

			BasicDBObject update = new BasicDBObject("$set", userdoc);
			collection.update(org, update);

		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ torg.getIdentifier() + "does not exists.");
		}

	}

	public void updateUser(OrgUser user, String idOrg) throws UserNotFoundException, OrgNotFoundException {

		DBCollection collection = db.getCollection(OCOLLECTION);

		if (existsOrg(collection, idOrg)) {
			if (existsElement(collection, idOrg, user.getIdentifier(), "users")) {

				BasicDBObject query = new BasicDBObject("identifier", idOrg);
				query.put("users.identifier", user.getIdentifier());
				DBObject userdoc = new BasicDBObject();

				userdoc.put("users.$.identifier", user.getIdentifier());
				userdoc.put("users.$.name", user.getName());
				userdoc.put("users.$.email", user.getEmail());
				userdoc.put("users.$.password", user.getPassword());
				userdoc.put("users.$.telephone", user.getTelephone());
				userdoc.put("users.$.isActive", user.isActive());
				userdoc.put("users.$.isAdmin", user.isAdmin());

				BasicDBObject update = new BasicDBObject("$set", userdoc);
				collection.update(query, update);
			} else {
				throw new UserNotFoundException("User with identifier "
						+ user.getIdentifier() + " does not exists.");
			}
		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + "does not exists.");
		}
	}
	
	public void updateTerminal(OrgTerminal terminal, String idOrg)
			throws TerminalNotFoundException, OrgNotFoundException {

		DBCollection collection = db.getCollection(OCOLLECTION);

		if (existsOrg(collection, idOrg)) {
			if (existsElement(collection, idOrg, terminal.getIdentifier(),
					"terminals")) {

				BasicDBObject query = new BasicDBObject("identifier", idOrg);
				query.put("terminals.identifier", terminal.getIdentifier());
				DBObject terminaldoc = new BasicDBObject();

				terminaldoc.put("terminals.$.identifier",
						terminal.getIdentifier());
				terminaldoc.put("terminals.$.hostName", terminal.getHostName());
				terminaldoc.put("terminals.$.ipAddress",
						terminal.getIpAddress());
				terminaldoc.put("terminals.$.mac", terminal.getMac());
				terminaldoc.put("terminals.$.ifaceSpeed",
						terminal.getIfaceSpeed());
				terminaldoc.put("terminals.$.description",
						terminal.getDescription());
				terminaldoc.put("terminals.$.active",
						terminal.isActive());
				terminaldoc.put("terminals.$.assigned",
						terminal.isAssigned());
				terminaldoc.put("terminals.$.assignedOrgId",
						terminal.isAssigned());
				terminaldoc.put("terminals.$.portApiID",
						terminal.isAssigned());
				
				BasicDBObject update = new BasicDBObject("$set", terminaldoc);
				collection.update(query, update);
			} else {
				throw new TerminalNotFoundException("Terminal with identifier "
						+ terminal.getIdentifier() + "does not exists.");
			}
		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + "does not exists.");
		}

	}
	
	public void updateFlow(OrgFlow flow, String idOrg)
			throws FlowNotFoundException, OrgNotFoundException {

		DBCollection collection = db.getCollection(OCOLLECTION);

		if (existsOrg(collection, idOrg)) {
			if (existsElement(collection, idOrg, flow.getIdentifier(), "flows")) {

				BasicDBObject query = new BasicDBObject("identifier", idOrg);
				query.put("flows.identifier", flow.getIdentifier());
				DBObject flowdoc = new BasicDBObject();

				flowdoc.put("flows.$.identifier", flow.getIdentifier());
				flowdoc.put("flows.$.name", flow.getName());
				flowdoc.put("flows.$.srcOTidentifier",
						flow.getSrcOTidentifier());
				flowdoc.put("flows.$.dstOTidentifier",
						flow.getDstOTidentifier());
				flowdoc.put("flows.$.srcPort", flow.getSrcPort());
				flowdoc.put("flows.$.dstPort", flow.getDstPort());
				flowdoc.put("flows.$.qos", flow.getQos());
				flowdoc.put("flows.$.bandwidth", flow.getBandwidth());
				flowdoc.put("flows.$.protocol", flow.getProtocol());

				BasicDBObject update = new BasicDBObject("$set", flowdoc);
				collection.update(query, update);
			} else {
				throw new FlowNotFoundException("Flow with identifier "
						+ flow.getIdentifier() + " does not exists.");
			}
		} else {
			throw new OrgNotFoundException("Organization with identifier "
					+ idOrg + "does not exists.");
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
}
