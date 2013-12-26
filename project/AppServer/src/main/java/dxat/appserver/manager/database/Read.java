package dxat.appserver.manager.database;

import java.util.HashMap;

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
			TOrg torg = new TOrg();
			torg.setIdentifier(identifier);
			torg.setName(name);
			torg.setNIF(NIF);
			torg.setTelephone(telephone);
			torg.setBankAccount(bankAccount);
			torg.setOAM(isOAM);
			//users
			//flows
			//terminals
			
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

	public HashMap<String, OrgFlow> getDBFlows() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, OrgUser> getDBUsers() {
		// TODO Auto-generated method stub
		return null;
	}

	public HashMap<String, OrgTerminal> getDBTerminals() {
		// TODO Auto-generated method stub
		return null;
	}
}
