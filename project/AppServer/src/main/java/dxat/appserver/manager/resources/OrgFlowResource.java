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

import dxat.appserver.flows.pojos.Flow;
import dxat.appserver.manager.OrgFlowManager;
import dxat.appserver.manager.OrgManager;
import dxat.appserver.manager.pojos.Org;
import dxat.appserver.manager.pojos.OrgCollection;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgFlowCollection;
import dxat.appserver.realtime.RealTimeManager;

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
		List<OrgFlow> orgFlowList = new ArrayList<OrgFlow>(orgFlowManager.orgManager.getFlows().values());
		OrgFlowCollection orgFlows = new OrgFlowCollection();
		orgFlows.setOrgFlows(orgFlowList);
		return orgFlows;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}
	
	@GET
	@Path("/flow/{orgId}/all")	
	@Produces(AppServerMediaType.ORG_FLOW_COLLECTION) 
	public OrgFlowCollection getAllOrgFlows(@PathParam("orgId") String orgId) {
		List<OrgFlow> orgFlowList = new ArrayList<OrgFlow>(orgFlowManager.orgManager.getOrg(orgId).getFlows().values());
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
	
	@GET
	@Path("/flowpusher/{flowId}")
	@Produces(MediaType.TEXT_PLAIN)
	public String pushFlow(@PathParam("flowId") String flowId) {
		OrgFlow orgFlow =  orgFlowManager.orgManager.getFlows().get(flowId);
		Flow flow = new Flow();
		flow.setBandwidth(orgFlow.getBandwidth());
		flow.setDstIpAddr(orgFlow.getDstOTidentifier());
		flow.setDstPort(orgFlow.getDstPort());
		flow.setFlowId(orgFlow.getIdentifier());
		flow.setProtocol((byte)0); //TODO
		flow.setQos(orgFlow.getQos());
		flow.setSrcIpAddr(orgFlow.getSrcOTidentifier());
		flow.setSrcPort(orgFlow.getSrcPort());
		
		RealTimeManager.getInstance().pushFlow(flow);
		return flowId;
	}
	
	
	@DELETE
	@Path("/flowpusher/{flowId}") 
	@Produces(MediaType.TEXT_PLAIN)
	public String deleteFlow(@PathParam("flowId") String flowId) {
		OrgFlow orgFlow =  orgFlowManager.orgManager.getFlows().get(flowId);
		Flow flow = new Flow();
		flow.setFlowId(orgFlow.getIdentifier());
		if(flowId.equals("all")) RealTimeManager.getInstance().deleteAllFlows();
		RealTimeManager.getInstance().deleteFlow(flow);
		return flowId;
	}

	//FLOWS ALWAYS ASSIGNED TO A ORG
	@POST
	@Path("/flow/{orgId}")
	@Consumes(AppServerMediaType.ORG_FLOW_COLLECTION)
	@Produces(AppServerMediaType.ORG_FLOW_COLLECTION)
	public OrgFlow insertRouter(@PathParam("orgId") String orgId, OrgFlow flow){
		if(!orgFlowManager.orgManager.existOrg(orgId)) return null;
		if(orgFlowManager.existFlowInOrg(flow, flow.getIdentifier(), orgId)) return null;
		
		return orgFlowManager.addOrgFlow(orgId, flow);
	}
	
	@PUT
	@Path("/flow/{orgId}/{flowId}")
	@Consumes(AppServerMediaType.ORG_FLOW_COLLECTION)
	@Produces(AppServerMediaType.ORG_FLOW_COLLECTION)
	public OrgFlow updateRouter(@PathParam("orgId") String orgId, @PathParam("flowId") String flowId, OrgFlow flow){
		if(!orgFlowManager.orgManager.existOrg(orgId)) return null;
		if(!orgFlowManager.existFlowInOrg(flow, flowId, orgId)) return null;
		if(!flow.getIdentifier().equals(flowId)) return null;
		return orgFlowManager.updateOrgFlow(orgId, flow);
	}
	
	@DELETE
	@Path("/flow/{orgId}/{flowId}")
	public String deleteRouter(@PathParam("orgId") String orgId, @PathParam("flowId") String flowId){
		if(!orgFlowManager.orgManager.existOrg(orgId)) return null;
		if(!orgFlowManager.orgManager.getOrg(orgId).getFlows().containsKey(flowId)) return null;
		if(orgFlowManager.deleteOrgFlow(orgId, flowId)!= null) return flowId;
		return null;
	}

}
