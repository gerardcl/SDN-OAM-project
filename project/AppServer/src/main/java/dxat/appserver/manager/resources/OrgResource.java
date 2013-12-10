package dxat.appserver.manager.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import dxat.appserver.manager.OrgManager;
import dxat.appserver.manager.pojos.OrgCollection;

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
	@Path("/all/orgs")
	@Produces(MediaType.ORG_COLLECTION)
	public OrgCollection getAllOrgs() {
		//System.out.println(orgManager.getAllOrgs());
		return orgManager.getAllOrgs();
	}
	
	@GET
	@Path("/flows/{orgId}")
	@Produces(MediaType.ORG_COLLECTION)
	public OrgCollection getAllOrgFlows(@PathParam("orgId") String orgId) {
		return (OrgCollection) orgManager.getAllOrgs();
	}

}
