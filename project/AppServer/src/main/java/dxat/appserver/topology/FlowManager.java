package dxat.appserver.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dxat.appserver.topology.exceptions.FlowNotFoundException;
import dxat.appserver.topology.interfaces.ITopoFlowManager;
import dxat.appserver.topology.pojos.Flow;
import dxat.appserver.topology.pojos.FlowCollection;
import dxat.appserver.topology.realtime.IRTFlowManager;

public class FlowManager implements ITopoFlowManager, IRTFlowManager {
	private static FlowManager instance = null;
	private HashMap<String, Flow> flows = null;

	private FlowManager() {
		flows = new HashMap<String, Flow>();
	}

	public static FlowManager getInstance() {
		if (instance == null)
			instance = new FlowManager();
		return instance;
	}

	@Override
	public void addFlow(Flow flow) {
		try {
			this.updateFlow(flow);
		} catch (FlowNotFoundException e) {
			flows.put(flow.getFlowId(), flow);
		}

	}

	@Override
	public void updateFlow(Flow updateFlow) throws FlowNotFoundException {
		if (!flows.containsKey(updateFlow.getFlowId()))
			throw new FlowNotFoundException("The flow with id '"
					+ updateFlow.getFlowId() + "' not found");
		Flow flow = flows.get(updateFlow.getFlowId());
		flow.setBandwidth(updateFlow.getBandwidth());
		flow.setDstPort(updateFlow.getDstPort());
		flow.setDstTerminalId(updateFlow.getDstTerminalId());
		flow.setEnabled(updateFlow.getEnabled());
		flow.setProtocol(updateFlow.getProtocol());
		flow.setQos(updateFlow.getQos());
		flow.setSrcPort(updateFlow.getSrcPort());
		flow.setSrcTerminalId(updateFlow.getSrcTerminalId());

	}

	@Override
	public void disableFlow(String flowId) throws FlowNotFoundException {
		if (!flows.containsKey(flowId))
			throw new FlowNotFoundException("Flow with id '" + flowId
					+ "' not found");
		flows.get(flowId).setEnabled(true);

	}

	@Override
	public void enableFlow(String flowId) throws FlowNotFoundException {
		if (!flows.containsKey(flowId))
			throw new FlowNotFoundException("Flow with id '" + flowId
					+ "' not found");
		flows.get(flowId).setEnabled(false);
	}

	@Override
	public FlowCollection getFlows() {
		List<Flow> flowList = new ArrayList<Flow>(flows.values());
		FlowCollection flowCollection = new FlowCollection();
		flowCollection.setFlows(flowList);
		return flowCollection;
	}

	@Override
	public Flow getFlow(String flowId) {
		return flows.get(flowId);
	}

}
