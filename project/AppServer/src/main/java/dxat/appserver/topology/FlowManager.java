package dxat.appserver.topology;

import com.google.gson.Gson;
import dxat.appserver.realtime.interfaces.IFlowEvents;
import dxat.appserver.realtime.interfaces.IRTFlowManager;
import dxat.appserver.realtime.pojos.ControllerEvent;
import dxat.appserver.topology.db.DbUpdate;
import dxat.appserver.topology.exceptions.FlowNotFoundException;
import dxat.appserver.topology.interfaces.ITopoFlowManager;
import dxat.appserver.topology.pojos.DeployedFlow;
import dxat.appserver.topology.pojos.DeployedFlowCollection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FlowManager implements ITopoFlowManager, IRTFlowManager {
    private static FlowManager instance = null;
    private HashMap<String, DeployedFlow> flows = null;

    private FlowManager() {
        flows = new HashMap<String, DeployedFlow>();
    }

    public static FlowManager getInstance() {
        if (instance == null)
            instance = new FlowManager();
        return instance;
    }

    public List<DbUpdate> processEvent(ControllerEvent controllerEvent) {
        String eventStr = controllerEvent.getEvent();
        List<DbUpdate> updateList = new ArrayList<DbUpdate>();
        if (eventStr.equals(IFlowEvents.PUSH_FLOW_SUCCESS)) {
            DeployedFlow deployedFlow = new Gson().fromJson(controllerEvent.getObject(),
                    DeployedFlow.class);
            if (!flows.containsKey(deployedFlow.getFlowId())) {
                DbUpdate update = new DbUpdate();
                updateList.add(update);
                update.setInventoryId(deployedFlow.getFlowId());
                update.setLegacyValue("false");
                update.setNewValue("true");
                update.setPropertyId("enabled");
                flows.put(deployedFlow.getFlowId(), deployedFlow);
            }
        } else if (eventStr.equals(IFlowEvents.DELETE_FLOW_SUCCESS)) {
            DeployedFlow deployedFlow = new Gson().fromJson(controllerEvent.getObject(),
                    DeployedFlow.class);
            if (flows.containsKey(deployedFlow.getFlowId())) {
                DbUpdate update = new DbUpdate();
                updateList.add(update);
                update.setInventoryId(deployedFlow.getFlowId());
                update.setLegacyValue("true");
                update.setNewValue("false");
                update.setPropertyId("enabled");
                flows.remove(deployedFlow.getFlowId());
            }
        } else if (eventStr.equals(IFlowEvents.PUSH_FLOW_DENIED)||eventStr.equals(IFlowEvents.DELETE_FLOW_FAILED)) {
            DeployedFlow deployedFlow = new Gson().fromJson(controllerEvent.getObject(),
                    DeployedFlow.class);
            if (flows.containsKey(deployedFlow.getFlowId())) {
                DbUpdate update = new DbUpdate();
                updateList.add(update);
                update.setInventoryId(deployedFlow.getFlowId());
                update.setLegacyValue("false");
                update.setNewValue("false");
                update.setPropertyId("enabled");
                flows.remove(deployedFlow.getFlowId());
            }
        }
        return updateList;
    }

    @Override
    public void addFlow(DeployedFlow flow) {
        try {
            this.updateFlow(flow);
        } catch (FlowNotFoundException e) {
            flows.put(flow.getFlowId(), flow);
        }

    }

    @Override
    public void updateFlow(DeployedFlow updateFlow) throws FlowNotFoundException {
        if (!flows.containsKey(updateFlow.getFlowId()))
            throw new FlowNotFoundException("The flow with id '"
                    + updateFlow.getFlowId() + "' not found");
        DeployedFlow flow = flows.get(updateFlow.getFlowId());
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
    public DeployedFlowCollection getFlows() {
        List<DeployedFlow> flowList = new ArrayList<DeployedFlow>(flows.values());
        DeployedFlowCollection flowCollection = new DeployedFlowCollection();
        flowCollection.setFlows(flowList);
        return flowCollection;
    }

    @Override
    public DeployedFlow getFlow(String flowId) {
        return flows.get(flowId);
    }

}
