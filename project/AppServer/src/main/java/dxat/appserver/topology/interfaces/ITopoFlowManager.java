package dxat.appserver.topology.interfaces;

import dxat.appserver.topology.pojos.Flow;
import dxat.appserver.topology.pojos.FlowCollection;

public interface ITopoFlowManager {
	public FlowCollection getFlows();

	public Flow getFlow(String flowId);
}
