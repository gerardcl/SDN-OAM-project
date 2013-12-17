package dxat.appserver.topology.realtime;

import dxat.appserver.topology.exceptions.FlowNotFoundException;
import dxat.appserver.topology.pojos.Flow;

public interface IRTFlowManager {
	public void addFlow(Flow flow);

	public void updateFlow(Flow flow) throws FlowNotFoundException;

	public void disableFlow(String flowId) throws FlowNotFoundException;

	public void enableFlow(String flowId) throws FlowNotFoundException;
}
