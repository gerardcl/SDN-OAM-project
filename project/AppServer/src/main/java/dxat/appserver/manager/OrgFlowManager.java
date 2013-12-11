package dxat.appserver.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgFlowCollection;
import dxat.appserver.manager.pojos.Org;

public class OrgFlowManager {
	private static OrgFlowManager instance;
	private OrgManager orgManager;
	private HashMap<String, OrgFlow> flows;

	private OrgFlowManager(){
		//TODO initialize!
		flows = new HashMap<String, OrgFlow>();
		for(int i=1; i<11; i++){
			OrgFlow flow = new OrgFlow();
			String id = "flouu";
			String name = "namee";
			String orgS = "src";
			String orgD = "dst";
			id += Integer.toString(i);
			name += Integer.toString(i);
			orgS += Integer.toString(i);
			orgD += Integer.toString(i);
			flow.setIdentifier(id);
			flow.setBandwidth(5000000);
			flow.setDstPort(5000);
			flow.setSrcPort(6000);
			flow.setProtocol("TCP");
			flow.setName(name);
			flow.setQos(2000);
			flow.setActive(true);
			flow.setDstOTidentifier(orgD);
			flow.setDstOTidentifier(orgS);
			flows.put(flow.identifier, flow);
			System.out.println("flow fakes");
		}
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
	
	public void addOrgFlow(String orgId, OrgFlow flow){
		//TODO
	}
	public OrgFlow getOrgFlow(String orgId, String flowId){
		//TODO
		return null;
	}
	public void deleteOrgFlow(String orgId, String flowId){
		//TODO
	}
	public void updateOrgFlow(String orgId, OrgFlow flow){
		//TODO
	}
}
