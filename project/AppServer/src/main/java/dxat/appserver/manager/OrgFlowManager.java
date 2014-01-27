package dxat.appserver.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import dxat.appserver.manager.database.Create;
import dxat.appserver.manager.database.Delete;
import dxat.appserver.manager.database.Update;
import dxat.appserver.manager.exceptions.FlowAlreadyExistsException;
import dxat.appserver.manager.exceptions.FlowNotFoundException;
import dxat.appserver.manager.exceptions.OrgNotFoundException;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgFlowCollection;
import dxat.appserver.manager.pojos.Org;

public class OrgFlowManager {
	private static OrgFlowManager instance;
	public OrgManager orgManager;
	private HashMap<String, OrgFlow> flows;
	private Create dbcreate;
	private Update dbupdate;
	private Delete dbdelete;

	private OrgFlowManager(){
		//TODO initialize!
		dbcreate = new Create();
		dbupdate = new Update();
		dbdelete = new Delete();
		orgManager = OrgManager.getInstance();
	}
	public static OrgFlowManager getInstance(){
		if(instance == null)
			instance = new OrgFlowManager();
		return instance;
	}

	public OrgFlowCollection getAllFlows(){
		List<OrgFlow> flowList = new ArrayList<OrgFlow>(flows.values());
		OrgFlowCollection orgFlows = new OrgFlowCollection();
		orgFlows.setOrgFlows(flowList);
		return orgFlows;
	}
	
	public OrgFlowCollection getAllOrgFlows(String orgId){
		List<OrgFlow> flowList = new ArrayList<OrgFlow>(flows.values());
		OrgFlowCollection orgFlows = new OrgFlowCollection();
		orgFlows.setOrgFlows(flowList);
		return orgFlows;
	}
	
	public OrgFlow addOrgFlow(String orgId, OrgFlow flow){
		OrgFlow newflow = new OrgFlow();
		newflow.setDstOTidentifier(flow.getDstOTidentifier());
		newflow.setSrcOTidentifier(flow.getSrcOTidentifier());
		newflow.setSrcPort(flow.getSrcPort());
		newflow.setDstPort(flow.getDstPort());
		newflow.setName(flow.getName());
		newflow.setProtocol(flow.getProtocol());
		newflow.setQos(flow.getQos());
		String id = Integer.toString((flow.getName().hashCode()));
		id.concat(Integer.toString((Integer.toString(flow.getSrcPort()).hashCode())));
		id.concat(Integer.toString((flow.getDstOTidentifier().hashCode())));
		newflow.setIdentifier(Integer.toString((int) Math.abs(id.hashCode()*Math.random()*-10)));		
		newflow.setBandwidth(flow.getBandwidth());

//TO CHANGE IT DINAMICALLY WHEN GETTING FLOW EVENTS!!!!!!!!!!!!!!!!!!!!
		newflow.setActive(true); 
		
		try {
			dbcreate.createFlow(newflow, orgId);
			orgManager.getOrg(orgId).getFlows().put(newflow.getIdentifier(), newflow);
			orgManager.getFlows().put(newflow.getIdentifier(), newflow);
			return newflow;
		} catch (FlowAlreadyExistsException | OrgNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public OrgFlow updateOrgFlow(String orgId, OrgFlow flow){
		try {
			dbupdate.updateFlow(flow, orgId);
			orgManager.getOrg(orgId).getFlows().put(flow.getIdentifier(), flow);
			orgManager.getFlows().put(flow.getIdentifier(), flow);
			return flow;
		} catch (FlowNotFoundException | OrgNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public String deleteOrgFlow(String orgId, String flowId){
		try {
			dbdelete.deleteFlow(orgId, flowId);
			orgManager.getOrg(orgId).getFlows().remove(flowId);
			
//TODO REMOVE ONCE RECEIVED SOCKET EVENT CONFIRMATION FROM CONTROLLER
			orgManager.getFlows().remove(flowId);
			return flowId;
		} catch (FlowNotFoundException | OrgNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public OrgFlow getOrgFlow(String orgId, String flowId){
		//TODO
		return null;
	}

	public boolean existFlowInOrg(OrgFlow flow, String flowId, String orgId){
		for(Entry<String, OrgFlow> entry1 : orgManager.getOrg(orgId).getFlows().entrySet()){
			OrgFlow cflow = entry1.getValue();
			if(cflow.getIdentifier().equals(flow.getIdentifier())&&cflow.getIdentifier().equals(flowId))return true;
		}
		return false;
	}
}
