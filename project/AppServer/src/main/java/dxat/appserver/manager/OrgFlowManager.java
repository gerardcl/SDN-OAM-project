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
	public OrgManager orgManager;
	private HashMap<String, OrgFlow> flows;

	private OrgFlowManager(){
		//TODO initialize!
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
