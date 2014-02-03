package dxat.appserver.manager.pojos;

import java.util.ArrayList;
import java.util.List;

public class OrgCollection {
	private List<Org> orgCollection = null;
	
	public OrgCollection(){
		orgCollection = new ArrayList<Org>();
	}

	public List<Org> getOrgCollection() {
		return orgCollection;
	}

	public void setOrgCollection(List<Org> orgCollection) {
		this.orgCollection = orgCollection;
	}
}
