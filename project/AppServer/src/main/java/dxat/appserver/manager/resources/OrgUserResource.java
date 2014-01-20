package dxat.appserver.manager.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import dxat.appserver.manager.OrgManager;
import dxat.appserver.manager.OrgUserManager;
import dxat.appserver.manager.pojos.OrgSession;
import dxat.appserver.manager.pojos.OrgUser;
import dxat.appserver.manager.pojos.OrgUserCollection;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import org.json.JSONObject;
//import com.google.gson.Gson;



import dxat.appserver.manager.pojos.TOrg;

import java.util.ArrayList;
import java.util.List;


@Path("/")
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
		List<OrgUser> orgUserList = new ArrayList<OrgUser>(OrgManager.getInstance().getUsers().values());
		OrgUserCollection orgUsers = new OrgUserCollection();
		orgUsers.setOrgUsers(orgUserList);
		return orgUsers;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}
	
	@GET
	@Path("/user/{orgId}/all")	
	@Produces(AppServerMediaType.ORG_USER_COLLECTION) 
	public OrgUserCollection getAllOrgUsers(@PathParam("orgId") String orgId) {
		List<OrgUser> orgUserList = new ArrayList<OrgUser>(OrgManager.getInstance().getOrg(orgId).getUsers().values());
		OrgUserCollection orgUsers = new OrgUserCollection();
		orgUsers.setOrgUsers(orgUserList);
		return orgUsers;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}	
	
	@GET
	@Path("/user/{orgId}/{userId}") 
	@Produces(AppServerMediaType.ORG_USER_COLLECTION)
	public OrgUser getOrgUser(@PathParam("orgId") String orgId, @PathParam("userId") String userId) {
		return orgUserManager.orgManager.getOrg(orgId).getUsers().get(userId);
	}	

	//INSERT USER -> CREATE USER IF POSSIBLE
	@POST
	@Path("/user/{orgId}")
	@Consumes(AppServerMediaType.ORG_COLLECTION)
	@Produces(AppServerMediaType.ORG_COLLECTION)
	public OrgUser insertUser(@PathParam("orgId") String orgId, OrgUser user){
		System.out.println("trying to insert new user");
		if(!orgUserManager.orgManager.existOrg(orgId)) return null;
		if(orgUserManager.existUser(user))return null;
		return orgUserManager.addOrgUser(orgId,user);
	}
	
	@PUT
	@Path("/user/{orgId}/{userId}")
	@Consumes(AppServerMediaType.ORG_COLLECTION)
	@Produces(AppServerMediaType.ORG_COLLECTION)
	public OrgUser updateOrg(@PathParam("orgId") String orgId, @PathParam("userId") String userId, OrgUser user){
		System.out.println("trying to update user");	
		if(!orgUserManager.orgManager.existOrg(orgId)) return null;
		if(!orgUserManager.existUser(user))return null;
		if(!orgUserManager.existUser(userId))return null;
		if(!orgUserManager.existUser(user.getIdentifier()))return null;
		System.out.println("updating...");
		return orgUserManager.updateOrgUser(orgId, user);	
	}
	
	@DELETE
	@Path("/user/{orgId}/{userId}")
	public String deleteOrg(@PathParam("orgId") String orgId, @PathParam("userId") String userId){
		if(!orgUserManager.orgManager.existOrg(orgId)) return "this org does not exists!";
		if(!orgUserManager.existUser(userId))return null;
		if(orgUserManager.deleteOrgUser(orgId,userId)==null) System.out.println("something went wrong when deleting user: "+userId);
		return userId;
	}
	
	//CHECK USER PASSWORD...
	@GET
	@Path("/user/auth")
	@Produces(MediaType.APPLICATION_JSON)
	public OrgSession checkUserAuth(@QueryParam("username") String username,@QueryParam("password") String password){
		System.out.println("Checking login for :"+username);
		OrgSession session = orgUserManager.checkPassword(username, password);
		if(session==null){
			//ELIMINAR SESSIÓ TEMPORAL CREADA PQ EL PASSWORD 
			//ERA ERRONI, GESTIÓ QUE S'HA DE FER PQ SINO ES
			//DUPLIQUEN LES SESSIONS
		}else{
			//AQUÍ COMENÇA GESTIÓ DE TOKENS, NEXT STEPS...
		}
		return session;
	}

}
