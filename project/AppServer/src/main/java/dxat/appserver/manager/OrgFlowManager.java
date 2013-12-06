package dxat.appserver.manager;

import java.util.List;

import dxat.appserver.manager.pojos.OrgFlow;

public class OrgFlowManager {
	private static OrgFlowManager instance;
	private OrgManager orgManager;
	private OrgFlowManager(){
		//TODO initialize!
		
	}
	public static OrgFlowManager getInstance(){
		if(instance == null)
			instance = new OrgFlowManager();
		return instance;
	}
	public List<OrgFlow> getAllOrgFlows(String orgId){
		//TODO
		return null;
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
