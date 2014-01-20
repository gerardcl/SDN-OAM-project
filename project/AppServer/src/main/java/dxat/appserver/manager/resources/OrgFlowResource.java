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
import javax.ws.rs.core.MediaType;

import dxat.appserver.manager.OrgFlowManager;
import dxat.appserver.manager.OrgManager;
import dxat.appserver.manager.pojos.Org;
import dxat.appserver.manager.pojos.OrgCollection;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgFlowCollection;

@Path("/")
public class OrgFlowResource {
//	- orgFlowManager: OrgFlowManager
//	+ getAllOrgFlow(String):List<OrgFlow>
//	+ addFlow(String, OrgFlow)
//	+ getOrgFlow(String, String):OrgFlow
//	+ deleteOrgFlow(String, String)
//	+ updateOrgFlow(String, OrgFlow)
	private OrgFlowManager orgFlowManager = OrgFlowManager.getInstance();
	
	//AQUESTA ES PASSARÀ AL ORGMANAGER QUE TINDRÀ TOTES LES ORGS ALLÀ
	//EL QUE FAREM AQUÍ SERÀ TRACTARO JA COM FLOWS D'UNA ORG
	//LA INTENCIÓ ÉS PASSAR AQUÍ SEMPRE LES ORGID
	//EL MOTIU ÉS PER LA DEFINICIÓ COEHERENT DE URLS I SEGURETAT
	@GET
	@Path("/flow/all")	
	@Produces(AppServerMediaType.ORG_FLOW_COLLECTION) 
	public OrgFlowCollection getAllFlows() {
		List<OrgFlow> orgFlowList = new ArrayList<OrgFlow>(orgFlowManager.orgManager.getInstance().getFlows().values());
		OrgFlowCollection orgFlows = new OrgFlowCollection();
		orgFlows.setOrgFlows(orgFlowList);
		return orgFlows;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}
	
	@GET
	@Path("/flow/{orgId}/all")	
	@Produces(AppServerMediaType.ORG_FLOW_COLLECTION) 
	public OrgFlowCollection getAllOrgFlows(@PathParam("orgId") String orgId) {
		List<OrgFlow> orgFlowList = new ArrayList<OrgFlow>(orgFlowManager.orgManager.getInstance().getOrg(orgId).getFlows().values());
		OrgFlowCollection orgFlows = new OrgFlowCollection();
		orgFlows.setOrgFlows(orgFlowList);
		return orgFlows;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}	
	
	@GET
	@Path("/flow/{orgId}/{flowId}") 
	@Produces(AppServerMediaType.ORG_FLOW_COLLECTION)
	public OrgFlow getOrgFlow(@PathParam("orgId") String orgId, @PathParam("flowId") String flowId) {
		return orgFlowManager.orgManager.getOrg(orgId).getFlows().get(flowId);
	}

//	@POST
//	@Path("/routers")
//	@Consumes(MediaType.ROUTER)
//	@Produces(MediaType.ROUTER)
//	public Router insertRouter(Router router){
//		repo.insertRouter(router);
//		return router;
//	}
//	
//	@DELETE
//	@Path("/routers/{inventoryId}")
//	public void deleteRouter(@PathParam("inventoryId") String inventoryId){
//		repo.deleteRouter(inventoryId);
//	}
//	
//	@PUT
//	@Path("/routers/{inventoryId}")
//	@Consumes(MediaType.ROUTER)
//	@Produces(MediaType.ROUTER)
//	public Router updateRouter(Router router){
//		return repo.updateRouter(router);
//	}
}
