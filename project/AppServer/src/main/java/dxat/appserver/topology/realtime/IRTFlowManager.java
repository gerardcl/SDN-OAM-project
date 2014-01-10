package dxat.appserver.topology.realtime;

import dxat.appserver.topology.exceptions.FlowNotFoundException;
import dxat.appserver.topology.pojos.DeployedFlow;

public interface IRTFlowManager {
	public void addFlow(DeployedFlow flow);

	public void updateFlow(DeployedFlow flow) throws FlowNotFoundException;

	public void disableFlow(String flowId) throws FlowNotFoundException;

	public void enableFlow(String flowId) throws FlowNotFoundException;
}
