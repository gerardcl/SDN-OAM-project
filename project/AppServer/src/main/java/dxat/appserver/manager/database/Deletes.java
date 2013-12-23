package dxat.appserver.manager.database;

import com.mongodb.DB;

public class Deletes {
	
	private static final String OCOLLECTION  = "ORG_COLLECTION";
	private static final String UCOLLECTION  = "USER_COLLECTION";
	private static final String TCOLLECTION  = "TERMINAL COLLECTION";
	private static final String FCOLLECTION  = "FLOW_COLLECTION";
	private static final String DOCOLLECTION = "ORG_REMOVED_COLLECTION";
	private static final String DUCOLLECTION = "USER_REMOVED_COLLECTION";
	private static final String DTCOLLECTION = "TERMINAL_REMOVED_COLLECTION";
	private static final String DFCOLLECTION = "FLOW_REMOVED_COLLECTION";

	DBAccess dbaccess = new DBAccess();
	DB db = dbaccess.setDb();
	
	public void removeOrg(String id){
		
	}
	
	public void removeUser(String id){
		
	}
	public void removeTerminal(String id){
	
	}
	public void removeFlow(String id){
		
	}
	public void saveOrg(String id){
		
	}
	public void saveUser(String id){
		
	}
	public void saveTerminal(String id){
		
	}
	public void saveFlow(String id){
		
	}
	
}
