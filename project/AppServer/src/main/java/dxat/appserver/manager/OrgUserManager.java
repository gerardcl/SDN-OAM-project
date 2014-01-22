package dxat.appserver.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import dxat.appserver.manager.database.Create;
import dxat.appserver.manager.database.Delete;
import dxat.appserver.manager.database.Read;
import dxat.appserver.manager.database.Update;
import dxat.appserver.manager.exceptions.OrgNotFoundException;
import dxat.appserver.manager.exceptions.UserAlreadyExistsException;
import dxat.appserver.manager.exceptions.UserNotFoundException;
import dxat.appserver.manager.pojos.OrgSession;
import dxat.appserver.manager.pojos.OrgUser;
import dxat.appserver.manager.pojos.TOrg;

public class OrgUserManager {
	private static OrgUserManager instance;
	public OrgManager orgManager;
	private Create dbcreate;
	private Read dbread;
	private Update dbupdate;
	private Delete dbdelete;

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
	public OrgUser addOrgUser(String orgId, OrgUser user){
		dbcreate = new Create();
		OrgUser newuser = new OrgUser();
		newuser.setActive(true); //default new user to active
		newuser.setAdmin(orgManager.getOrg(orgId).isOAM()); //set admin if org is OAM
		newuser.setName(user.getName());
		newuser.setPassword(user.getPassword());
		newuser.setTelephone(user.getTelephone());
		newuser.setEmail(user.getEmail());
		String id = Integer.toString((user.getName().hashCode()));
		id.concat(Integer.toString((user.getPassword().hashCode())));
		id.concat(Integer.toString((user.getEmail().hashCode())));
		newuser.setIdentifier(Integer.toString((int) Math.abs(id.hashCode()*Math.random()*-10)));
		try {
			dbcreate.createUser(newuser, orgId);
			orgManager.getOrg(orgId).getUsers().put(newuser.getIdentifier(), newuser);
			orgManager.getUsers().put(newuser.getIdentifier(), newuser);
			return newuser;
		} catch (OrgNotFoundException | UserAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public OrgUser updateOrgUser(String orgId, OrgUser user){
		dbupdate = new Update();
		OrgUser uuser = new OrgUser();
		uuser.setIdentifier(user.getIdentifier());
		uuser.setActive(true); //TODO update properly in controller.js on to true and off to false
		uuser.setAdmin(orgManager.getOrg(orgId).isOAM()); //set admin if org is OAM
		uuser.setName(user.getName());
		uuser.setPassword(user.getPassword());
		uuser.setTelephone(user.getTelephone());
		uuser.setEmail(user.getEmail());
		try {
			dbupdate.updateUser(uuser, orgId);
			orgManager.getOrg(orgId).getUsers().put(uuser.getIdentifier(), uuser);
			orgManager.getUsers().put(uuser.getIdentifier(), uuser);
			return uuser;
		} catch (UserNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OrgNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}	
	public String deleteOrgUser(String orgId, String userId){
		dbdelete = new Delete();
		try {
			dbdelete.deleteUser(orgId, userId);
			orgManager.getOrg(orgId).getUsers().remove(userId);
			orgManager.getUsers().remove(userId);
			return userId;
		} catch (UserNotFoundException | OrgNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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

	//check name
	public boolean existUser(OrgUser user) {
		for(Entry<String, OrgUser> entry1 : orgManager.getInstance().getUsers().entrySet()){
			OrgUser cuser = entry1.getValue();
			if(user.getName().equals(cuser.getName())) return true; 
		}
		return false;
	}
	public boolean existUser(String userId) {
		for(Entry<String, OrgUser> entry1 : orgManager.getInstance().getUsers().entrySet()){
			OrgUser cuser = entry1.getValue();
			if(cuser.getIdentifier().equals(userId)) return true; 
		}
		return false;
	}
	public boolean existUserInOrg(OrgUser user, String userId, String orgId){
		for(Entry<String, OrgUser> entry1 : orgManager.getInstance().getOrg(orgId).getUsers().entrySet()){
			OrgUser cuser = entry1.getValue();
			if(cuser.getName().equals(user.getName())&&cuser.getIdentifier().equals(userId)&&cuser.getIdentifier().equals(user.getIdentifier()) )return true;
		}
		return false;
	}
}
