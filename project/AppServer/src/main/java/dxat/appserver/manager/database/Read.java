package dxat.appserver.manager.database;

import java.util.HashMap;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import dxat.appserver.manager.pojos.Org;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgTerminal;
import dxat.appserver.manager.pojos.OrgUser;
import dxat.appserver.manager.pojos.TOrg;

public class Read {

	private static final String OCOLLECTION   = "ORG_COLLECTION";

	DBAccess dbaccess = new DBAccess();
	DB db = dbaccess.setDb();

	public boolean existsOrg(DB db, String id){
		return true;
	}

	public boolean existsUser(){
		return true;
	}
	public boolean existsTerminal(){
		return true;
	}
	public boolean existsFlow(){
		return true;
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

	public HashMap<String, Org> getDBOrgs() {
		HashMap<String, Org> orgs = new HashMap<String, Org>();
		DBCollection collection = db.getCollection(OCOLLECTION);
		DBCursor cursor;

		cursor = collection.find();
		while(cursor.hasNext()) {
			Org org = new Org();
			DBObject o = cursor.next();
			String identifier = (String) o.get("identifier") ; 
			String name = (String) o.get("name") ; 
			String NIF = (String) o.get("NIF") ; 
			String telephone = (String) o.get("telephone") ; 
			String bankAccount = (String) o.get("bankAccount") ; 
			String isOAM_s = (String) o.get("isOAM").toString() ;
			boolean isOAM = (isOAM_s.equals("true")? true : false);
			//torg
			TOrg torg = new TOrg();
			torg.setIdentifier(identifier);
			torg.setName(name);
			torg.setNIF(NIF);
			torg.setTelephone(telephone);
			torg.setBankAccount(bankAccount);
			torg.setOAM(isOAM);
			//users
			HashMap<String, OrgUser> users = getDBOrgUser(identifier);
			org.setUsers(users);
			//flows
			HashMap<String, OrgFlow> flows = getDBOrgFlow(identifier);
			org.setFlows(flows);
			//terminals
			HashMap<String, OrgTerminal> terminals = getDBOrgTerminal(identifier);
			org.setTerminals(terminals);

			org.setIdentifier(identifier);
			org.setName(name);
			org.setNIF(NIF);
			org.setTelephone(telephone);
			org.setBankAccount(bankAccount);
			org.setOAM(isOAM);
			org.setTorg(torg);

			orgs.put(identifier, org);
		}
		return orgs;
	}
	public HashMap<String, TOrg> getDBTOrgs() {
		HashMap<String, TOrg> torgs = new HashMap<String, TOrg>();
		DBCollection collection = db.getCollection(OCOLLECTION);
		DBCursor cursor;

		cursor = collection.find();
		while(cursor.hasNext()) {
			TOrg torg = new TOrg();
			DBObject o = cursor.next();
			String identifier = (String) o.get("identifier") ; 
			String name = (String) o.get("name") ; 
			String NIF = (String) o.get("NIF") ; 
			String telephone = (String) o.get("telephone") ; 
			String bankAccount = (String) o.get("bankAccount") ; 
			String isOAM_s = (String) o.get("isOAM").toString() ;
			boolean isOAM = (isOAM_s.equals("true")? true : false);

			torg.setIdentifier(identifier);
			torg.setName(name);
			torg.setNIF(NIF);
			torg.setTelephone(telephone);
			torg.setBankAccount(bankAccount);
			torg.setOAM(isOAM);

			torgs.put(identifier, torg);
		}
		return torgs;
	}
	public HashMap<String, OrgUser> getDBOrgUser(String idOrg) {
		HashMap<String, OrgUser> usersHM = new HashMap<String, OrgUser>();
		DBCollection collection = db.getCollection(OCOLLECTION);
		BasicDBObject updateOrg = new BasicDBObject("identifier", idOrg);
		DBObject org = collection.findOne(updateOrg);
		BasicDBList usersOrg = (BasicDBList) org.get("users");
		for (int i =0;i<usersOrg.size();i++){
			OrgUser orgUser = new OrgUser();
			DBObject user = (BasicDBObject) usersOrg.get(i);

			String identifier = (String) user.get("identifier");
			String name = (String) user.get("name");
			String email = (String) user.get("email");
			String password = (String) user.get("password");
			int telephone = (Integer) user.get("telephone");
			boolean isAdmin = (Boolean) user.get("isAdmin");
			boolean isActive = (Boolean) user.get("isActive");

			orgUser.setIdentifier(identifier);
			orgUser.setName(name);
			orgUser.setEmail(email);
			orgUser.setPassword(password);
			orgUser.setTelephone(telephone);
			orgUser.setAdmin(isAdmin);
			orgUser.setActive(isActive);

			usersHM.put(identifier, orgUser);
		}
		return usersHM;
	}
	public HashMap<String, OrgUser> getDBUsers() {
		HashMap<String, OrgUser> usersHM = new HashMap<String, OrgUser>();
		DBCollection collection = db.getCollection(OCOLLECTION);
		DBCursor cursor;

		cursor = collection.find();
		while (cursor.hasNext()) {
			BasicDBList users = (BasicDBList) cursor.next().get("users");
			for (int i = 0; i < users.size(); i++) {
				OrgUser orgUser = new OrgUser();
				DBObject user = (BasicDBObject) users.get(i);

				String identifier = (String) user.get("identifier");
				String name = (String) user.get("name");
				String email = (String) user.get("email");
				String password = (String) user.get("password");
				int telephone = (Integer) user.get("telephone");
				boolean isAdmin = (Boolean) user.get("isAdmin");
				boolean isActive = (Boolean) user.get("isActive");

				orgUser.setIdentifier(identifier);
				orgUser.setName(name);
				orgUser.setEmail(email);
				orgUser.setPassword(password);
				orgUser.setTelephone(telephone);
				orgUser.setAdmin(isAdmin);
				orgUser.setActive(isActive);

				usersHM.put(identifier, orgUser);

			}
		}
		return usersHM;

	}
	public HashMap<String, OrgFlow> getDBOrgFlow(String idOrg) {
		HashMap<String, OrgFlow> flowsHM = new HashMap<String, OrgFlow>();
		DBCollection collection = db.getCollection(OCOLLECTION);
		BasicDBObject updateOrg = new BasicDBObject("identifier", idOrg);
		DBObject org = collection.findOne(updateOrg);
		BasicDBList flowsOrg = (BasicDBList) org.get("flows");
		for (int i =0;i<flowsOrg.size();i++){
			OrgFlow orgFlow = new OrgFlow();
			DBObject flow = (BasicDBObject) flowsOrg.get(i);

			String identifier = (String) flow.get("identifier");
			String name = (String) flow.get("name");
			String srcOTidentifier = (String) flow.get("srcOTidentifier");
			String dstOTidentifier = (String) flow.get("dstOTidentifier");
			int srcPort = (Integer) flow.get("srcPort");
			int dstPort = (Integer) flow.get("dstPort");
			int qos = (Integer) flow.get("qos");
			double bandwidth = (Double) flow.get("bandwidth");
			String protocol = (String) flow.get("protocol");
			boolean active = (Boolean) flow.get("active");

			orgFlow.setIdentifier(identifier);
			orgFlow.setName(name);
			orgFlow.setSrcOTidentifier(srcOTidentifier);
			orgFlow.setDstOTidentifier(dstOTidentifier);
			orgFlow.setSrcPort(srcPort);
			orgFlow.setDstPort(dstPort);
			orgFlow.setQos(qos);
			orgFlow.setBandwidth(bandwidth);
			orgFlow.setProtocol(protocol);
			orgFlow.setActive(active);

			flowsHM.put(identifier, orgFlow);
		}
		return flowsHM;
	}
	public HashMap<String, OrgFlow> getDBFlows() {
		HashMap<String, OrgFlow> flowsHM = new HashMap<String, OrgFlow>();
		DBCollection collection = db.getCollection(OCOLLECTION);
		DBCursor cursor;

		cursor = collection.find();
		while (cursor.hasNext()) {
			BasicDBList flows = (BasicDBList) cursor.next().get("flows");
			for (int i = 0; i < flows.size(); i++) {
				OrgFlow orgFlow = new OrgFlow();
				DBObject flow = (BasicDBObject) flows.get(i);

				String identifier = (String) flow.get("identifier");
				String name = (String) flow.get("name");
				String srcOTidentifier = (String) flow.get("srcOTidentifier");
				String dstOTidentifier = (String) flow.get("dstOTidentifier");
				int srcPort = (Integer) flow.get("srcPort");
				int dstPort = (Integer) flow.get("dstPort");
				int qos = (Integer) flow.get("qos");
				double bandwidth = (Double) flow.get("bandwidth");
				String protocol = (String) flow.get("protocol");
				boolean active = (Boolean) flow.get("active");

				orgFlow.setIdentifier(identifier);
				orgFlow.setName(name);
				orgFlow.setSrcOTidentifier(srcOTidentifier);
				orgFlow.setDstOTidentifier(dstOTidentifier);
				orgFlow.setSrcPort(srcPort);
				orgFlow.setDstPort(dstPort);
				orgFlow.setQos(qos);
				orgFlow.setBandwidth(bandwidth);
				orgFlow.setProtocol(protocol);
				orgFlow.setActive(active);

				flowsHM.put(identifier, orgFlow);
			}
		}
		return flowsHM;
	}
	public HashMap<String, OrgTerminal> getDBOrgTerminal(String idOrg) {
		HashMap<String, OrgTerminal> terminalsHM = new HashMap<String, OrgTerminal>();
		DBCollection collection = db.getCollection(OCOLLECTION);
		BasicDBObject updateOrg = new BasicDBObject("identifier", idOrg);
		DBObject org = collection.findOne(updateOrg);
		BasicDBList terminalsOrg = (BasicDBList) org.get("terminals");
		for (int i =0;i<terminalsOrg.size();i++){
			OrgTerminal orgTerminal = new OrgTerminal();
			DBObject terminal = (BasicDBObject) terminalsOrg.get(i);

			String identifier = (String) terminal.get("identifier");
			String hostName = (String) terminal.get("hostName");
			String ipAddress = (String) terminal.get("ipAddress");
			String mac = (String) terminal.get("mac");
			double ifaceSpeed = (Double) terminal.get("ifaceSpeed");
			String description = (String) terminal.get("description");
			boolean active = (Boolean) terminal.get("active");
			boolean assigned = (Boolean) terminal.get("assigned");
			String assignedOrgId = (String) terminal.get("assignedOrgId");
			String portApiID = (String) terminal.get("portApiID");
			
			orgTerminal.setIdentifier(identifier);
			orgTerminal.setHostName(hostName);
			orgTerminal.setIpAddress(ipAddress);
			orgTerminal.setMac(mac);
			orgTerminal.setIfaceSpeed(ifaceSpeed);
			orgTerminal.setDescription(description);
			orgTerminal.setActive(active);
			orgTerminal.setAssigned(assigned);
			orgTerminal.setAssignedOrgId(assignedOrgId);
			orgTerminal.setPortApiID(portApiID);

			terminalsHM.put(identifier, orgTerminal);
		}
		return terminalsHM;
	}
	public HashMap<String, OrgTerminal> getDBTerminals() {
		HashMap<String, OrgTerminal> terminalsHM = new HashMap<String, OrgTerminal>();
		DBCollection collection = db.getCollection(OCOLLECTION);
		DBCursor cursor;

		cursor = collection.find();
		while (cursor.hasNext()) {
			BasicDBList terminals = (BasicDBList) cursor.next()
					.get("terminals");
			for (int i = 0; i < terminals.size(); i++) {
				OrgTerminal orgTerminal = new OrgTerminal();
				DBObject terminal = (BasicDBObject) terminals.get(i);

				String identifier = (String) terminal.get("identifier");
				String hostName = (String) terminal.get("hostName");
				String ipAddress = (String) terminal.get("ipAddress");
				String mac = (String) terminal.get("mac");
				double ifaceSpeed = (Double) terminal.get("ifaceSpeed");
				String description = (String) terminal.get("description");
				boolean active = (Boolean) terminal.get("active");
				boolean assigned = (Boolean) terminal.get("assigned");
				String assignedOrgId = (String) terminal.get("assignedOrgId");
				String portApiID = (String) terminal.get("portApiID");

				orgTerminal.setIdentifier(identifier);
				orgTerminal.setHostName(hostName);
				orgTerminal.setIpAddress(ipAddress);
				orgTerminal.setMac(mac);
				orgTerminal.setIfaceSpeed(ifaceSpeed);
				orgTerminal.setDescription(description);
				orgTerminal.setActive(active);
				orgTerminal.setAssigned(assigned);
				orgTerminal.setAssignedOrgId(assignedOrgId);
				orgTerminal.setPortApiID(portApiID);

				terminalsHM.put(identifier, orgTerminal);
			}
		}
		return terminalsHM;
	}
}
