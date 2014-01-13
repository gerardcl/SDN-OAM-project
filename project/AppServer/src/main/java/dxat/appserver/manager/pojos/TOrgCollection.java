package dxat.appserver.manager.pojos;

import java.util.ArrayList;
import java.util.List;

public class TOrgCollection {
	private List<TOrg> torgs = null;
	
	public TOrgCollection() {
		torgs = new ArrayList<TOrg>();
	}

	public List<TOrg> getTorgs() {
		return torgs;
	}

	public void setTorgs(List<TOrg> torgs) {
		this.torgs = torgs;
	}
}
