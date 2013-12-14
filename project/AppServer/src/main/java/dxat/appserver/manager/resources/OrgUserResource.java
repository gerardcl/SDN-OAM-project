package dxat.appserver.manager.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import dxat.appserver.manager.OrgUserManager;
import dxat.appserver.manager.pojos.OrgUser;
import dxat.appserver.manager.pojos.OrgUserCollection;

import java.util.ArrayList;
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
	
	//AQUESTA ES PASSARÀ AL ORGMANAGER QUE TINDRÀ TOTES LES ORGS ALLÀ
	//EL QUE FAREM AQUÍ SERÀ TRACTARO JA COM FLOWS D'UNA ORG
	//LA INTENCIÓ ÉS PASSAR AQUÍ SEMPRE LES ORGID
	//EL MOTIU ÉS PER LA DEFINICIÓ COEHERENT DE URLS I SEGURETAT
	@GET
	@Path("/fulluser/all")	
	@Produces(AppServerMediaType.ORG_USER_COLLECTION) 
	public OrgUserCollection getAllUsers() {
		List<OrgUser> orgUserList = new ArrayList<OrgUser>(orgUserManager.orgManager.getInstance().getUsers().values());
		OrgUserCollection orgUsers = new OrgUserCollection();
		orgUsers.setOrgUsers(orgUserList);
		return orgUsers;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}
	
	@GET
	@Path("/user/all")	
	@Produces(AppServerMediaType.ORG_USER_COLLECTION) 
	public OrgUserCollection getAllOrgUsers(@QueryParam("orgId") String orgId) {
		System.out.println("\n\nORG ID RECEIVED IS: "+orgId+"\n\n");
		List<OrgUser> orgUserList = new ArrayList<OrgUser>(orgUserManager.orgManager.getInstance().getOrg(orgId).getUsers().values());
		OrgUserCollection orgUsers = new OrgUserCollection();
		orgUsers.setOrgUsers(orgUserList);
		return orgUsers;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}	
	
	@GET
	@Path("/user/{userId}") 
	@Produces(AppServerMediaType.ORG_USER_COLLECTION)
	public OrgUser getOrgUser(@QueryParam("orgId") String orgId, @PathParam("userId") String userId) {
		return orgUserManager.orgManager.getOrg(orgId).getUsers().get(userId);
	}	


}
