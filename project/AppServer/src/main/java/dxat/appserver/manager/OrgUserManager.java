package dxat.appserver.manager;

import java.util.List;

import dxat.appserver.manager.pojos.OrgUser;

public class OrgUserManager {
	private static OrgUserManager instance;
	public OrgManager orgManager;
	private OrgUserManager(){
		//TODO initialize!
		orgManager = OrgManager.getInstance();
	}
	public static OrgUserManager getInstance(){
		if(instance == null)
			instance = new OrgUserManager();
		return instance;
	}
	public List<OrgUser> getAllOrgUsers(String orgId){
		//TODO
		return null;
	}
	public void addOrgUser(String orgId, OrgUser user){
		//TODO
	}
	public OrgUser getOrgUser(String orgId, String userId){
		//TODO
		return null;
	}
	public void deleteOrgUser(String orgId, String userId){
		//TODO
	}
	public void updateOrgUser(String orgId, OrgUser user){
		//TODO
	}
	public int checkPassword(String userId, String password){
		//TODO
		if(orgManager.getInstance().getUsers().get(userId) != null){
			System.out.println("USER EXISTS");
			if(orgManager.getInstance().getUsers().get(userId).getPassword().equals(password)){
				System.out.println("CORRECT PASSWORD");
				if(orgManager.getInstance().getUsers().get(userId).isAdmin()) 
					return 1;
				else return 2;
			}
		}
		return 0;
	}
}
