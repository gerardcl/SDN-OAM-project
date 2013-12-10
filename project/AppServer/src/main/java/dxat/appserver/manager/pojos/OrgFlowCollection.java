package dxat.appserver.manager.pojos;

import java.util.ArrayList;
import java.util.List;

public class OrgFlowCollection {
	private List<OrgFlow> orgFlows = null;
	
	public OrgFlowCollection(){
		orgFlows = new ArrayList<OrgFlow>();
	}

	public List<OrgFlow> getOrgFlows() {
		return orgFlows;
	}

	public void setOrgFlows(List<OrgFlow> orgFlows) {
		this.orgFlows = orgFlows;
	}
}
