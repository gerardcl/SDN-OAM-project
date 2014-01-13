package dxat.appserver.manager;

import java.util.List;

import dxat.appserver.manager.pojos.OrgSession;
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
	public OrgSession checkPassword(String username, String password){
		OrgSession session = OrgManager.getInstance().existUser(username);
		if(session!=null){
			System.out.println("USER EXIST");
			if(OrgManager.getInstance().getUsers().get(session.getUserId()).getPassword().equals(password)){
				System.out.println("CORRECT PASSWORD");
				if(OrgManager.getInstance().getUsers().get(session.getUserId()).isAdmin()){
					session.setMsg("1");
					return session;
				}
				else{
					session.setMsg("2");
					return session;
				}
			}else System.out.println("INCORRECT PASSWORD");
		}else System.out.println("USER DOES NOT EXIST");
		return null;
	}
}
