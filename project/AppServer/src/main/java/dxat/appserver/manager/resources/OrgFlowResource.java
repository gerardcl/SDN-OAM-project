package dxat.appserver.manager.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import dxat.appserver.manager.OrgFlowManager;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgFlowCollection;

@Path("/manager")
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
	@Produces(MediaType.ORG_FLOW_COLLECTION)
	public OrgFlowCollection getAllFlows() {
		return (OrgFlowCollection) orgFlowManager.getAllFlows();
	}
	
//	@GET
//	@Path("/flow/{flowId}")
//	@Produces(MediaType.ORG_FLOW_COLLECTION)
//	public OrgFlowCollection getOrgFlow(@PathParam("flowId") String flowId) {
//		return (OrgFlowCollection) orgFlowManager.getAllOrgFlows(flowId);
//	}
//	
//	@GET
//	@Path("/flow/all")
//	@Produces(MediaType.ORG_FLOW_COLLECTION)
//	public OrgFlowCollection getAllOrgFlows(@PathParam("flowId") String flowId) {
//		return (OrgFlowCollection) orgFlowManager.getAllOrgFlows(flowId);
//	}
	
	
	
//	@GET
//	@Path("/flow/{flowId}")
//	//@Produces(MediaType.SWITCHES_COLLECTION)
//	public OrgFlow getOrgFlow(String flowId) {
//		return orgFlowManager.getOrgFlow(orgId, flowId)
//	}
//	@GET
//	@Path("/flow/{}")
//	@Produces(MediaType.ROUTER)
//	public Router getRouter(@PathParam("inventoryId") String inventoryId){
//		return repo.getRouter(inventoryId);
//	}
//	@GET
//	@Path("/switches/{inventoryId}")
//	@Produces(MediaType.SWITCH)
//	public Switch getSwitch(@PathParam("inventoryId") String inventoryId){
//		return repo.getSwitch(inventoryId);
//	}
//	
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
