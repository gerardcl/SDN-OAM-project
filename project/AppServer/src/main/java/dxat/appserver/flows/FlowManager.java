package dxat.appserver.flows;

import com.google.gson.Gson;
import dxat.appserver.flows.pojos.DeployedFlow;
import dxat.appserver.flows.pojos.DeployedFlowCollection;
import dxat.appserver.realtime.events.IFlowEvents;
import dxat.appserver.realtime.pojos.ControllerEvent;
import dxat.appserver.realtime.pojos.DbUpdate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class FlowManager {
    /**
     *
     */
    private static FlowManager instance = null;

    /**
     *
     */
    private HashMap<String, DeployedFlow> flows = null;

    /**
     *
     */
    private FlowManager() {
        flows = new HashMap<String, DeployedFlow>();
    }

    /**
     * @return
     */
    public static FlowManager getInstance() {
        if (instance == null)
            instance = new FlowManager();
        return instance;
    }

    /**
     * @param controllerEvent
     * @return
     */
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
                update.setMessage("The flow with id '" + deployedFlow.getFlowId() +
                        "' has been deployed successfully.");
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
                update.setMessage("Flow with id '" + deployedFlow.getFlowId() + "' has been removed form the forwarding tables.");
                flows.remove(deployedFlow.getFlowId());
            }
        } else if (eventStr.equals(IFlowEvents.PUSH_FLOW_DST_TERMINAL_NOT_FOUND) ||
                eventStr.equals(IFlowEvents.PUSH_FLOW_FLOW_ALREADY_EXIST) ||
                eventStr.equals(IFlowEvents.PUSH_FLOW_ILLEGAL_FLOW_ENTRY) ||
                eventStr.equals(IFlowEvents.PUSH_FLOW_SRC_TERMINAL_NOT_FOUND) ||
                eventStr.equals(IFlowEvents.PUSH_FLOW_UNREACHABLE_TERMINALS) ||
                eventStr.equals(IFlowEvents.DELETE_FLOW_FAILED) ||
                eventStr.equals(IFlowEvents.REROUTE_FLOW_DST_TERMINAL_NOT_FOUND) ||
                eventStr.equals(IFlowEvents.REROUTE_FLOW_FLOW_ALREADY_EXIST) ||
                eventStr.equals(IFlowEvents.REROUTE_FLOW_ILLEGAL_FLOW_ENTRY) ||
                eventStr.equals(IFlowEvents.REROUTE_FLOW_SRC_TERMINAL_NOT_FOUND) ||
                eventStr.equals(IFlowEvents.REROUTE_FLOW_UNREACHABLE_TERMINALS)) {

            // Set only the message of the update
            DbUpdate update = new DbUpdate();
            updateList.add(update);
            update.setMessage(controllerEvent.getObject());
        } else if (eventStr.equals(IFlowEvents.REROUTE_FLOW_SUCCESS)) {
            // Get deployed flows
            DeployedFlow newDeployedFlow = new Gson().fromJson(controllerEvent.getObject(), DeployedFlow.class);
            DeployedFlow deployedFlow = flows.get(newDeployedFlow.getFlowId());

            // Set update with the new route
            DbUpdate update = new DbUpdate();
            updateList.add(update);
            update.setInventoryId(deployedFlow.getFlowId());
            update.setLegacyValue(new Gson().toJson(deployedFlow.getRoute()));
            update.setNewValue(new Gson().toJson(newDeployedFlow.getRoute()));
            update.setPropertyId("route");
            update.setMessage("The flow with id '" + deployedFlow.getFlowId() +
                    "' has been rerouted successfully after the fall of a port.");

            // Update route
            deployedFlow.setRoute(newDeployedFlow.getRoute());
        } else if (eventStr.equals(IFlowEvents.ALL_FLOWS_DELETED)) {
            Collection<DeployedFlow> flowCollection = flows.values();
            for (DeployedFlow deployedFlow : flowCollection) {
                DbUpdate update = new DbUpdate();
                updateList.add(update);
                update.setInventoryId(deployedFlow.getFlowId());
                update.setLegacyValue("true");
                update.setNewValue("false");
                update.setPropertyId("enabled");
                update.setMessage("The flow with id '" + deployedFlow.getFlowId() + "' has been deleted successfully.");
                flows.remove(deployedFlow.getFlowId());
            }
            flows.clear();
        } else if (eventStr.equals(IFlowEvents.FLOW_COLLECTION)) {
            DeployedFlowCollection deployedFlowCollection = new Gson().fromJson(controllerEvent.getObject(),
                    DeployedFlowCollection.class);
            List<DeployedFlow> deployedFlowList = deployedFlowCollection.getFlows();
            flows.clear();
            for (DeployedFlow deployedFlow : deployedFlowList) {
                // Put flow in the database
                flows.put(deployedFlow.getFlowId(), deployedFlow);

                // Set update
                DbUpdate update = new DbUpdate();
                update.setNewValue("true");
                update.setLegacyValue("false");
                update.setInventoryId(deployedFlow.getFlowId());
                update.setPropertyId("enabled");
                update.setMessage("The flow with ID '" + deployedFlow.getFlowId() + "' is deployed in the network.");
                updateList.add(update);
            }
        }
        return updateList;
    }

    /**
     * @return
     */
    public DeployedFlowCollection getFlows() {
        List<DeployedFlow> flowList = new ArrayList<DeployedFlow>(flows.values());
        DeployedFlowCollection flowCollection = new DeployedFlowCollection();
        flowCollection.setFlows(flowList);
        return flowCollection;
    }

    /**
     * @param flowId
     * @return
     */
    public DeployedFlow getFlow(String flowId) {
        return flows.get(flowId);
    }

}
