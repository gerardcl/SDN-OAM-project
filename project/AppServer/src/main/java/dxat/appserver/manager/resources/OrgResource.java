package dxat.appserver.manager.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import dxat.appserver.manager.OrgManager;
import dxat.appserver.manager.pojos.Org;
import dxat.appserver.manager.pojos.OrgCollection;
import dxat.appserver.manager.pojos.TOrg;
import dxat.appserver.manager.pojos.TOrgCollection;

@Path("/")
public class OrgResource {
//	- orgCollection: OrgCollection
//	+ getOrgIdNameList():HashMap<String, String>
//	+ addOrg(TOrganization):TOrganization
//	+ getOrg(String):TOrganization
//	+ deleteOrg(String)
//	+ updateOrg(TOrganization):TOrganization

	private OrgManager orgManager = OrgManager.getInstance();

	@GET
	@Path("/fullorg/all")
	@Produces(AppServerMediaType.ORG_COLLECTION)
	public OrgCollection getAllOrgs() {
		//System.out.println(orgManager.getAllOrgs());
		return orgManager.getAllOrgs();
	}
	
	@GET
	@Path("/fullorg/{orgId}")
	@Produces(AppServerMediaType.ORG_COLLECTION)
	public Org getOrg(@PathParam("orgId") String orgId) {
		return orgManager.getOrg(orgId);
	}
	
	@GET
	@Path("/org/all")
	@Produces(AppServerMediaType.ORG_COLLECTION)
	public TOrgCollection getAllTOrgs() {
		//System.out.println(orgManager.getAllOrgs());
		return orgManager.getAllTOrgs();
	}
	
	@GET
	@Path("/org/{orgId}")
	@Produces(AppServerMediaType.ORG_COLLECTION)
	public TOrg getTOrg(@PathParam("orgId") String orgId) {
		return orgManager.getOrg(orgId).getTorg();
	}
	
	//INSERT TORG -> CREATE ORG IF POSSIBLE
	@POST
	@Path("/org")
	@Consumes(AppServerMediaType.ORG_COLLECTION)
	@Produces(AppServerMediaType.ORG_COLLECTION)
	public TOrg insertTOrg(TOrg torg){
		System.out.println("trying to insert new org");
		if(orgManager.existOrg(torg))return null;
		return orgManager.addTOrg(torg);
	}
	
	@PUT
	@Path("/org/{orgId}")
	@Consumes(AppServerMediaType.ORG_COLLECTION)
	@Produces(AppServerMediaType.ORG_COLLECTION)
	public TOrg updateOrg(@PathParam("orgId") String orgId, TOrg torg){
		System.out.println("trying to update org");	
		if(!orgManager.existOrg(orgId)) return null;
		if(!torg.getIdentifier().equals(orgId)) return null;
		System.out.println("updating...");
		return orgManager.updateTOrg(orgId, torg);	
	}
	
	@DELETE
	@Path("/org/{orgId}")
	public String deleteOrg(@PathParam("orgId") String orgId){
		if(!orgManager.existOrg(orgId)) return "this org does not exists!";
		if(orgManager.deleteOrg(orgId)==null) System.out.println("something went wrong when deleting org: "+orgId);
		return orgId;
	}
}
