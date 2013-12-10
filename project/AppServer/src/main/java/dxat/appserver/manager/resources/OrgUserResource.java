package dxat.appserver.manager.resources;

import javax.ws.rs.Path;

import dxat.appserver.manager.OrgUserManager;
import dxat.appserver.manager.pojos.OrgUser;
import java.util.List;


@Path("/manager")
public class OrgUserResource {
//- OrgUserManager: OrgUserManager
//+ getAllOrgUser(String):List<OrgUser>
//+ addOrgUser(String, OrgUser)
//+ getOrgUser(String, String): OrgUser
//+ deleteOrgUser(String, String)
//+ updateOrgUser(String, User)
//+ CheckPassword(String, String): Boolean
	private OrgUserManager orgUserManager = OrgUserManager.getInstance();
	
	
	public List<OrgUser> getAllOrgUsers(){
		//TODO
		return null;
	}
	public void addOrgUser(String orgId, OrgUser user){
		
	}
}
