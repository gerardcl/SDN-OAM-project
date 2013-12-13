package dxat.appserver.manager.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import dxat.appserver.manager.OrgManager;
import dxat.appserver.manager.pojos.Org;
import dxat.appserver.manager.pojos.OrgCollection;
import dxat.appserver.manager.pojos.TOrg;
import dxat.appserver.manager.pojos.TOrgCollection;

@Path("/manager")
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
	@Produces(MediaType.ORG_COLLECTION)
	public OrgCollection getAllOrgs() {
		//System.out.println(orgManager.getAllOrgs());
		return orgManager.getAllOrgs();
	}
	
	@GET
	@Path("/fullorg/{orgId}")
	@Produces(MediaType.ORG_COLLECTION)
	public Org getOrg(@PathParam("orgId") String orgId) {
		return orgManager.getOrg(orgId);
	}
	
	@GET
	@Path("/org/all")
	@Produces(MediaType.ORG_COLLECTION)
	public TOrgCollection getAllTOrgs() {
		//System.out.println(orgManager.getAllOrgs());
		return orgManager.getAllTOrgs();
	}
	
	@GET
	@Path("/org/{orgId}")
	@Produces(MediaType.ORG_COLLECTION)
	public TOrg getTOrg(@PathParam("orgId") String orgId) {
		return orgManager.getOrg(orgId).getTorg();
	}

}
