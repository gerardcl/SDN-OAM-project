package dxat.appserver.manager.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import dxat.appserver.manager.OrgManager;
import dxat.appserver.manager.OrgTerminalManager;
import dxat.appserver.manager.pojos.OrgTerminal;
import dxat.appserver.manager.pojos.OrgTerminalCollection;
import dxat.appserver.manager.pojos.OrgUser;

@Path("/")
public class OrgTerminalResource {
//	- orgTerminalManager: OrgTerminalManager
//	+ getAllOrgTerminal(String):List<OrgTerminal>
//	+ addOrgTerminal(String, OrgTerminal):OrgTerminal
//	+ getOrgTerminal(String, String):AccessPoint
//	+ deleteOrgTerminal(String, String)
//	+ updateOrgTerminal(String, OrgTerminal)
	private OrgTerminalManager orgTerminalManager = OrgTerminalManager.getInstance();
	
	//AQUESTA ES PASSARÀ AL ORGMANAGER QUE TINDRÀ TOTES LES ORGS ALLÀ
	//EL QUE FAREM AQUÍ SERÀ TRACTARO JA COM FLOWS D'UNA ORG
	//LA INTENCIÓ ÉS PASSAR AQUÍ SEMPRE LES ORGID
	//EL MOTIU ÉS PER LA DEFINICIÓ COEHERENT DE URLS I SEGURETAT
	@GET
	@Path("/terminal/all")	
	@Produces(AppServerMediaType.ORG_TERMINAL_COLLECTION) 
	public OrgTerminalCollection getAllTerminals() {
		List<OrgTerminal> orgTerminalList = new ArrayList<OrgTerminal>(orgTerminalManager.orgManager.getInstance().getTerminals().values());
		OrgTerminalCollection orgTerminals = new OrgTerminalCollection();
		orgTerminals.setOrgTerminals(orgTerminalList);
		return orgTerminals;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}
	
	@GET
	@Path("/terminal/{orgId}/all")	
	@Produces(AppServerMediaType.ORG_TERMINAL_COLLECTION) 
	public OrgTerminalCollection getAllOrgTerminals(@PathParam("orgId") String orgId) {
		List<OrgTerminal> orgTerminalList = new ArrayList<OrgTerminal>(orgTerminalManager.orgManager.getInstance().getOrg(orgId).getTerminals().values());
		OrgTerminalCollection orgTerminals = new OrgTerminalCollection();
		orgTerminals.setOrgTerminals(orgTerminalList);
		return orgTerminals;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}	
	
	@GET
	@Path("/terminal/{orgId}/{terminalId}") 
	@Produces(AppServerMediaType.ORG_TERMINAL_COLLECTION)
	public OrgTerminal getOrgTerminal(@PathParam("orgId") String orgId, @PathParam("terminalId") String terminalId) {
		return orgTerminalManager.orgManager.getOrg(orgId).getTerminals().get(terminalId);
	}	
//	//INSERT TERMINAL -> CREATE TERMINAL IF POSSIBLE
//	@POST
//	@Path("/user/{orgId}")
//	@Consumes(AppServerMediaType.ORG_USER_COLLECTION)
//	@Produces(AppServerMediaType.ORG_USER_COLLECTION)
//	public OrgUser insertUser(@PathParam("orgId") String orgId, OrgUser user){
//		System.out.println("trying to insert new user");
//		if(!orgTerminalManager.orgManager.existOrg(orgId)) return null;
//		if(orgTerminalManager.existUserInOrg(user, user.getIdentifier(), orgId)) return null;
//		if(orgTerminalManager.existUser(user))return null;
//		System.out.println("creating...user "+user.getName());
//		return orgTerminalManager.addOrgUser(orgId,user);
//	}
//	
//	@PUT
//	@Path("/user/{orgId}/{userId}")
//	@Consumes(AppServerMediaType.ORG_USER_COLLECTION)
//	@Produces(AppServerMediaType.ORG_USER_COLLECTION)
//	public OrgUser updateOrg(@PathParam("orgId") String orgId, @PathParam("userId") String userId, OrgUser user){
//		System.out.println("trying to update user");	
//		if(!orgTerminalManager.orgManager.existOrg(orgId)) return null;
//		if(!orgTerminalManager.existUser(user))return null;  //here checking name
//		if(!orgUserManager.existUser(userId))return null;
//		if(!orgUserManager.existUserInOrg(user, userId, orgId))return null;
//		if(!orgUserManager.existUser(user.getIdentifier()))return null;
//		System.out.println("updating...user "+user.getName());
//		return orgUserManager.updateOrgUser(orgId, user);	
//	}
//	
//	@DELETE
//	@Path("/user/{orgId}/{userId}")
//	public String deleteOrg(@PathParam("orgId") String orgId, @PathParam("userId") String userId){
//		if(!orgUserManager.orgManager.existOrg(orgId)) return "this org does not exists!";
//		if(!orgUserManager.existUser(userId))return null;
//		if(!orgUserManager.existUserInOrg(OrgManager.getInstance().getOrg(orgId).getUsers().get(userId), userId, orgId))return null;
//		if(orgUserManager.deleteOrgUser(orgId,userId)==null) System.out.println("something went wrong when deleting user: "+userId);
//		return userId;
//	}
}
