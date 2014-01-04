package dxat.appserver.manager.pojos;

import java.util.ArrayList;
import java.util.List;

public class OrgTerminalCollection {
	private List<OrgTerminal> orgTerminals = null;
	
	public OrgTerminalCollection(){
		orgTerminals = new ArrayList<OrgTerminal>();
	}

	public List<OrgTerminal> getOrgTerminals() {
		return orgTerminals;
	}

	public void setOrgTerminals(List<OrgTerminal> orgTerminals) {
		this.orgTerminals = orgTerminals;
	}
}
