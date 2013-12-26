package dxat.appserver.manager.database;

import java.lang.reflect.Array;
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
			//TODO cargar hashmap users de dentro de org <-- getDBOrgUser(orgId/identifier) 
			//flows
			//TODO cargar hashmap flows de dentro de org <-- getDBOrgFlows(orgId/identifier) 
			//terminals
			//TODO cargar hashmap terminals de dentro de org <-- getDBOrgTerminals(ororgId/identifiergId) 

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
	//RETURN ORG USERS
	public HashMap<String, OrgUser> getDBOrgUser(String idOrg) {
		return null;
	}
	//RETURN ALL USERS
	public HashMap<String, OrgUser> getDBUsers() {
		HashMap<String, OrgFlow> usersHM = null;
		DBCollection collection = db.getCollection(OCOLLECTION);
		DBCursor cursor;

		cursor = collection.find();
		while(cursor.hasNext()) {
			DBObject o = cursor.next();
			Object users = o.get("users");

			//users[i] 
			System.out.println("GETTING USERS");
			System.out.println(users);
			//TODO RECORRER EL ARRAY USERS (mira consola cuando haces llamada a la web
			//para que veas que efectivamente users és una array (confirmado): 
			//http://localhost:8080/AppServer/#/adminOrgs
			//para tener una db inicializada como hacemos tienes primero que poner el
			//booleano en OrgManager a true (dbExists)
			//luego cuando ya tengas la db montada (comprueba con robomongo)
			//podras volverla a false y a jugar
			//NOTA: esta implementació serà la clave para getDBOrgUser luego



		}
		return null;
	}
	public HashMap<String, OrgFlow> getDBFlows() {
		// TODO Auto-generated method stub
		return null;
	}
	public HashMap<String, OrgTerminal> getDBTerminals() {
		// TODO Auto-generated method stub
		return null;
	}
}
