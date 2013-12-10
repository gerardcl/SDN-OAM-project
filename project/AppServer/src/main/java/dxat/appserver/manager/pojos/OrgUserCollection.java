package dxat.appserver.manager.pojos;

import java.util.ArrayList;
import java.util.List;

public class OrgUserCollection {
	private List<OrgUser> orgUsers = null;
	
	public OrgUserCollection(){
		orgUsers = new ArrayList<OrgUser>();
	}

	public List<OrgUser> getOrgUsers() {
		return orgUsers;
	}

	public void setOrgUsers(List<OrgUser> orgUsers) {
		this.orgUsers = orgUsers;
	}
}
