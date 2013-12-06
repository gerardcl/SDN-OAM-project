package dxat.appserver.manager.resources;

import javax.ws.rs.Path;

import dxat.appserver.manager.OrgManager;

@Path("/manager")
public class OrgResource {
//	- orgCollection: OrgCollection
//	+ getOrgIdNameList():HashMap<String, String>
//	+ addOrg(TOrganization):TOrganization
//	+ getOrg(String):TOrganization
//	+ deleteOrg(String)
//	+ updateOrg(TOrganization):TOrganization

	private OrgManager orgManager = OrgManager.getInstance();


}
