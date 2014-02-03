package dxat.controller.module;

import com.google.gson.Gson;
import dxat.controller.module.events.IFlowEvents;
import dxat.controller.module.exceptions.*;
import dxat.controller.module.listeners.DeviceListener;
import dxat.controller.module.pojos.ControllerEvent;
import dxat.controller.module.pojos.DeployedFlow;
import dxat.controller.module.pojos.DeployedFlowCollection;
import dxat.controller.module.pojos.Flow;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.Cluster;
import net.floodlightcontroller.topology.NodePortTuple;
import net.floodlightcontroller.topology.TopologyInstance;
import org.openflow.util.HexString;

import java.util.*;

public class FlowPusherManager {
    /**
     *
     */
    private HashMap<String, DeployedFlow> currentFlows;

    /**
     *
     */
    private FlowTableManager flowTableManager;

    public FlowPusherManager() {
        super();
        currentFlows = new HashMap<String, DeployedFlow>();
        flowTableManager = new FlowTableManager();
    }

    private static Route getRoute(NodePortTuple srcNode, NodePortTuple dstNode, List<Link> links,
                                  HashMap<Link, Integer> cost) {
        // Get source and destination nodes identifiers
        long srcId = srcNode.getNodeId();
        long dstId = dstNode.getNodeId();

        // Create cluster
        Cluster cluster = new Cluster();
        for (Link link : links) {
            cluster.addLink(link);
        }

        // Find links using the dijkstra algorithm
        TopologyInstance topologyInstance = new TopologyInstance();
        Map<Long, Link> nexthoplinks = topologyInstance.dijkstra(cluster, dstId, cost, true).getLinks();

        LinkedList<NodePortTuple> route = new LinkedList<NodePortTuple>();

        NodePortTuple npt;
        while (srcId != dstId) {
            Link l = nexthoplinks.get(srcId);
            if (l == null) {
                return null;
            }
            npt = new NodePortTuple(l.getSrc(), l.getSrcPort());
            route.addLast(npt);
            npt = new NodePortTuple(l.getDst(), l.getDstPort());
            route.addLast(npt);
            srcId = nexthoplinks.get(srcId).getDst();
        }

        // Add Sourcer and destination nodes
        Route result = null;
        if (!route.isEmpty()) {
            route.addFirst(srcNode);
            route.addLast(dstNode);
            result = new Route(null, route);
        }

        return result;
    }

    /**
     *
     */
    public void deleteAllFlows() {
        System.out.println("--- DELETING ALL FLOWS ---");
        Chronometer chronometer = new Chronometer();
        chronometer.tic();
        // Delete all flow tables
        flowTableManager.deleteAllEntries();

        // Clear current flows
        currentFlows.clear();
        System.out.println("The controller spend " + chronometer.toc() + " ms deleting all flow entries");
    }

    /**
     * This function removes from the switches flow tables the desired flow.
     *
     * @param flow It is the flow to remove. It is only needed the flow id.
     * @return True if the flow is running or false if not.
     */
    public boolean deleteFlow(Flow flow) {
        System.out.println("--- DELETING FLOW '" + flow.getFlowId() + "' ---");
        Chronometer chronometer = new Chronometer();
        chronometer.tic();

        // Check if the flow exists
        if (!currentFlows.containsKey(flow.getFlowId()))
            return false;

        // Delete Flow tables entries
        flowTableManager.deleteFlowEntries(flow);

        // Delete flow entry in the current flow list
        currentFlows.remove(flow.getFlowId());

        System.out.println("The controller spend " + chronometer.toc() + " ms deleting the flow '" + flow.getFlowId() + "' entries");

        // Return
        return true;
    }

    /**
     * This function pushes a flow. It seeks the access points of the terminals.
     * After compute the route and write the flow tables of the switches.
     * Finally, if no error store and return the deployed flow.
     *
     * @param flow This is the flow which is pushed into the network.
     * @return The deployed flow, which contains the same information than the
     * original flow plus the route ports.
     * @throws DstTerminalNotFoundException  If the destination terminal cannot be found in the network.
     * @throws SrcTerminalNotFoundException  If the source terminal cannot be found in the network.
     * @throws UnreachableTerminalsException If it is not possible find a route between the pair of
     *                                       terminals.
     * @throws IllegalFlowEntryException     If there is an error generating a flow entry.
     * @throws FlowAlreadyExistsException    If the flow has been pushed previously.
     */
    public DeployedFlow pushFlow(Flow flow)
            throws DstTerminalNotFoundException, SrcTerminalNotFoundException,
            UnreachableTerminalsException, IllegalFlowEntryException,
            FlowAlreadyExistsException {

        System.out.println("--- PUSHING FLOW! ---");
        Chronometer chronometer = new Chronometer();
        chronometer.tic();

        // Check if this flow has been already pushed
        if (currentFlows.containsKey(flow.getFlowId())) {
            throw new FlowAlreadyExistsException(flow);
        }

        // Get services
        ILinkDiscoveryService topologyService = DxatAppModule.getInstance().getLinkService();

        // Initialize the attachment points
        DeviceListener deviceListener = DxatAppModule.getInstance().getDeviceListener();
        NodePortTuple srcNode = deviceListener.getAttachmentPoint(flow.getSrcIpAddr());
        NodePortTuple dstNode = deviceListener.getAttachmentPoint(flow.getDstIpAddr());

        // Check if terminals are reachable for find their access points
        if (dstNode == null) {
            throw new DstTerminalNotFoundException(flow);
        } else if (srcNode == null) {
            throw new SrcTerminalNotFoundException(flow);
        }

        // Get topology
        List<Link> links = new ArrayList<Link>(topologyService.getLinks().keySet());

        // Calculate the best route
        Route route = getRoute(srcNode, dstNode, links, flowTableManager.getCosts());

        // Check if possible a route between the switches
        if (route == null) {
            throw new UnreachableTerminalsException(flow);
        }

        flowTableManager.pushFlowEntries(route, flow);

        // Print route
        List<NodePortTuple> forwardPath = route.getPath();
        System.out.println("--- Forward path ---");
        for (NodePortTuple nodePortTuple : forwardPath)
            System.out.println("\t" + nodePortTuple.toString());
        System.out.println("--- Forward path ---");

        // Get the list of ports of the flow
        List<String> flowPortSet = new ArrayList<String>();
        for (NodePortTuple node : forwardPath) {
            flowPortSet.add(HexString.toHexString(node.getNodeId()) + ":"
                    + node.getPortId());
        }

        // Create Deployed flow
        DeployedFlow deployedFlow = new DeployedFlow(flow);
        deployedFlow.setBandwidth(flow.getBandwidth());
        deployedFlow.setDstPort(flow.getDstPort());
        deployedFlow.setFlowId(flow.getFlowId());
        deployedFlow.setProtocol(flow.getProtocol());
        deployedFlow.setQos(flow.getQos());
        deployedFlow.setRoute(new ArrayList<String>(flowPortSet));
        deployedFlow.setSrcPort(flow.getSrcPort());

        // Add flow to the registry of flows
        currentFlows.put(flow.getFlowId(), deployedFlow);

        System.out.println("The flow '" + flow.getFlowId() + "' has been pushed in " + chronometer.toc() + " ms from request arrival.");

        // Return the deployed flow
        return deployedFlow;
    }

    public void rerouteFlow(Link link) {
        System.out.println("--- LINK REMOVED: '" + link.toString() + "' ---");
        Chronometer chronometer = new Chronometer();
        chronometer.tic();

        // Get related flows
        List<String> relatedFlows = flowTableManager.getRelatedFlows(link);

        // If no related flows with the link return
        if (relatedFlows == null)
            return;
        else if (relatedFlows.size() == 0)
            return;

        // Delete and push the flows related with the port
        for (String flowId : relatedFlows) {
            System.out.println("--- REROUTING FLOW: '" + flowId + "' ---");

            // Get the flow id from the current deployed flows
            Flow flow = new Flow(currentFlows.get(flowId));

            // Delete the flow
            deleteFlow(flow);

            // Try to deploy the new flow
            ControllerEvent controllerEvent = new ControllerEvent();
            controllerEvent.setTimestamp(new Date().getTime());
            try {
                DeployedFlow deployedFlow = pushFlow(flow);
                controllerEvent.setEvent(IFlowEvents.REROUTE_FLOW_SUCCESS);
                controllerEvent.setObject(new Gson().toJson(deployedFlow));
            } catch (DstTerminalNotFoundException e) {
                controllerEvent
                        .setEvent(IFlowEvents.REROUTE_FLOW_DST_TERMINAL_NOT_FOUND);
                controllerEvent.setObject(new Gson().toJson(flow));
            } catch (SrcTerminalNotFoundException e) {
                controllerEvent
                        .setEvent(IFlowEvents.REROUTE_FLOW_SRC_TERMINAL_NOT_FOUND);
                controllerEvent.setObject(new Gson().toJson(flow));
            } catch (UnreachableTerminalsException e) {
                controllerEvent
                        .setEvent(IFlowEvents.REROUTE_FLOW_UNREACHABLE_TERMINALS);
                controllerEvent.setObject(new Gson().toJson(flow));
            } catch (IllegalFlowEntryException e) {
                controllerEvent
                        .setEvent(IFlowEvents.REROUTE_FLOW_ILLEGAL_FLOW_ENTRY);
                controllerEvent.setObject(new Gson().toJson(flow));
            } catch (FlowAlreadyExistsException e) {
                controllerEvent
                        .setEvent(IFlowEvents.PUSH_FLOW_FLOW_ALREADY_EXIST);
                controllerEvent.setObject(new Gson().toJson(flow));
            }

            // Broadcast event with the new deployed flow
            DxatAppModule.getInstance().getModuleServerThread()
                    .broadcastControllerEvent(controllerEvent);
        }
        System.out.println("All flows have been rerouted in " + chronometer.toc() + " ms.");
    }

    public DeployedFlowCollection getDeployedFlows() {
        DeployedFlowCollection deployedFlowCollection = new DeployedFlowCollection();
        List<DeployedFlow> deployedFlowList = new ArrayList<DeployedFlow>(
                currentFlows.values());
        deployedFlowCollection.setFlows(deployedFlowList);
        return deployedFlowCollection;
    }
}
