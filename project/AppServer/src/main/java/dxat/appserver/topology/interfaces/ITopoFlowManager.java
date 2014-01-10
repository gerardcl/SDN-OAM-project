package dxat.appserver.topology.interfaces;

import dxat.appserver.topology.pojos.DeployedFlow;
import dxat.appserver.topology.pojos.DeployedFlowCollection;

public interface ITopoFlowManager {
	public DeployedFlowCollection getFlows();

	public DeployedFlow getFlow(String flowId);
}
