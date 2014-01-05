package dxat.appserver.topology.pojos;

import java.util.ArrayList;
import java.util.List;

public class FlowCollection {
	private List<Flow> flows = null;

	public FlowCollection() {
		flows = new ArrayList<Flow>();
	}

	public List<Flow> getFlows() {
		return flows;
	}

	public void setFlows(List<Flow> flows) {
		this.flows = flows;
	}

}
