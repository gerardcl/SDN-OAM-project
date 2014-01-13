package dxat.controller.module;

import com.google.gson.Gson;
import dxat.controller.module.events.IFlowEvents;
import dxat.controller.module.exceptions.*;
import dxat.controller.module.pojos.ControllerEvent;
import dxat.controller.module.pojos.DeployedFlow;
import dxat.controller.module.pojos.DeployedFlowCollection;
import dxat.controller.module.pojos.Flow;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.loadbalancer.LoadBalancer;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntries;
import net.floodlightcontroller.topology.NodePortTuple;
import org.openflow.protocol.*;
import org.openflow.util.HexString;
import org.openflow.util.U16;

import java.util.*;

public class FlowPusherManager {
    private HashMap<String, DeployedFlow> currentFlows;

    public FlowPusherManager() {
        super();
        currentFlows = new HashMap<String, DeployedFlow>();
    }

    public void deleteAllFlows() {
        DxatAppModule.getInstance().getFlowPusherService().deleteAllFlows();
    }

    /**
     * This function removes from the switches flow tables the desired flow.
     *
     * @param flow It is the flow to remove. It is only needed the flow id.
     * @return True if the flow is running or false if not.
     */
    public boolean deleteFlow(Flow flow) {
        Boolean found = false;
        IStaticFlowEntryPusherService flowPusherService = DxatAppModule.getInstance().getFlowPusherService();
        Map<String, Map<String, OFFlowMod>> switchFlowMap = flowPusherService
                .getFlows();

        if (!currentFlows.containsKey(flow.getFlowId()))
            return false;

        Set<String> dpidSet = switchFlowMap.keySet();
        for (String dpid : dpidSet) {
            String entryName = dpid + "." + flow.getFlowId() + ".";
            List<String> currentFlowList = new ArrayList<String>();
            // Get Switch flows
            Set<String> flowSet = switchFlowMap.get(dpid).keySet();
            for (String flowId : flowSet) {
                currentFlowList.add(new String(flowId));
                System.out.println(flowId);
            }

            // Delete Switch flows with the desired id
            for (String flowId : currentFlowList) {
                if (flowId.startsWith(entryName)) {
                    flowPusherService.deleteFlow(flowId);
                    found = true;
                }
            }
        }

        // Delete flow entry in the current flow list
        currentFlows.remove(flow.getFlowId());

        // Return
        return found;
    }

    /**
     * This function pushes a flow. It seeks the access points of the terminals. After compute the route
     * and write the flow tables of the switches. Finally, if no error store and return the deployed flow.
     *
     * @param flow This is the flow which is pushed into the network.
     * @return The deployed flow, which contains the same information than the original flow plus the route ports.
     * @throws DstTerminalNotFoundException  If the destination terminal cannot be found in the network.
     * @throws SrcTerminalNotFoundException  If the source terminal cannot be found in the network.
     * @throws UnreachableTerminalsException If it is not possible find a route between the pair of terminals.
     * @throws IllegalFlowEntryException     If there is an error generating a flow entry.
     * @throws FlowAlreadyExistsException    If the flow has been pushed previously.
     */
    public DeployedFlow pushFlow(Flow flow) throws DstTerminalNotFoundException, SrcTerminalNotFoundException,
            UnreachableTerminalsException, IllegalFlowEntryException, FlowAlreadyExistsException {

        if (currentFlows.containsKey(flow.getFlowId())) {
            throw new FlowAlreadyExistsException(flow);
        }

        // Get services
        IStaticFlowEntryPusherService flowPusherService = DxatAppModule.getInstance().getFlowPusherService();
        IFloodlightProviderService switchService = DxatAppModule.getInstance().getSwitchService();
        IDeviceService deviceService = DxatAppModule.getInstance().getDeviceService();
        IRoutingService routingService = DxatAppModule.getInstance().getRoutingService();

        // Get flow SRC and destination
        int flowSrcIpAddr = IPv4.toIPv4Address(flow.getSrcIpAddr());
        int flowDstIpAddr = IPv4.toIPv4Address(flow.getDstIpAddr());

        // Initialize the attachment points
        Long longSrcDpid = null;
        Short shortSrcPort = null;
        Long longDstDpid = null;
        Short shortDstPort = null;

        // Look for these attachment points
        @SuppressWarnings("unchecked")
        Collection<IDevice> deviceCollection = (Collection<IDevice>) deviceService
                .getAllDevices();
        // For each network device
        for (IDevice device : deviceCollection) {
            // Extract ip addreses
            Integer[] addresses = device.getIPv4Addresses();
            // For each IP address
            for (Integer intAddr : addresses) {
                // If the IP address is matches with the source address
                if (intAddr == flowSrcIpAddr) {
                    SwitchPort[] attachments = device.getAttachmentPoints();
                    if (attachments.length > 0) {
                        SwitchPort attachment = attachments[0];
                        longSrcDpid = attachment.getSwitchDPID();
                        shortSrcPort = (short) attachment.getPort();
                    }
                    // If the IP address is matches with the destination address
                } else if (intAddr == flowDstIpAddr) {
                    SwitchPort[] attachments = device.getAttachmentPoints();
                    if (attachments.length > 0) {
                        SwitchPort attachment = attachments[0];
                        longDstDpid = attachment.getSwitchDPID();
                        shortDstPort = (short) attachment.getPort();
                    }
                }
            }
        }

        // Check if terminals are reachable for find their access points
        if (longDstDpid == null) {
            throw new DstTerminalNotFoundException(flow);
        } else if (longSrcDpid == null) {
            throw new SrcTerminalNotFoundException(flow);
        }

        // Calculate The route
        Route route = routingService.getRoute(longSrcDpid, shortSrcPort,
                longDstDpid, shortDstPort, 0);

        // Check if the route has been done
        if (route == null) {
            throw new UnreachableTerminalsException(flow);
        }

        // Put entries in the forwarding tables
        List<NodePortTuple> routeNodes = route.getPath();
        String ap1Dpid = "";
        String ap1Port = "";
        String ap2Port = "";
        for (int i = 0; i < routeNodes.size(); i++) {
            if (i % 2 == 0) {
                ap1Dpid = HexString.toHexString(routeNodes.get(i).getNodeId());
                ap1Port = String.valueOf(routeNodes.get(i).getPortId());
            } else {
                ap2Port = String.valueOf(routeNodes.get(i).getPortId());

                // FORWARD Flow entry
                String entryName = ap1Dpid + "." + flow.getFlowId()
                        + ".forward";
                String matchString = "";
                matchString += "nw_dst=" + flow.getDstIpAddr() + ",";
                matchString += "nw_src=" + flow.getSrcIpAddr() + ",";
                matchString += "nw_proto=" + flow.getProtocol() + ",";
                matchString += "tp_dst=" + flow.getDstPort() + ",";
                matchString += "tp_src=" + flow.getSrcPort() + ",";
                matchString += "dl_type=" + 0x800 + ",";
                matchString += "in_port=" + ap1Port;

                String actionString = "output=" + ap2Port;

                OFFlowMod fm = (OFFlowMod) switchService.getOFMessageFactory()
                        .getMessage(OFType.FLOW_MOD);

                fm.setIdleTimeout((short) 0); // infinite
                fm.setHardTimeout((short) 0); // infinite
                fm.setBufferId(OFPacketOut.BUFFER_ID_NONE);
                fm.setCommand((short) 0);
                fm.setFlags((short) 0);
                fm.setOutPort(OFPort.OFPP_NONE.getValue());
                fm.setCookie((long) 0);
                fm.setPriority(Short.MAX_VALUE);
                StaticFlowEntries.initDefaultFlowMod(fm, entryName);

                LoadBalancer.parseActionString(fm, actionString);

                fm.setPriority(U16.t(LoadBalancer.LB_PRIORITY));

                OFMatch ofMatch = new OFMatch();
                try {
                    ofMatch.fromString(matchString);
                } catch (IllegalArgumentException e) {
                    deleteFlow(flow);
                    throw new IllegalFlowEntryException(flow, ap1Dpid, matchString);
                }
                fm.setMatch(ofMatch);
                flowPusherService.addFlow(entryName, fm, ap1Dpid);

                // BACKWARD
                entryName = ap1Dpid + "." + flow.getFlowId() + ".backward";
                matchString = "";
                matchString += "nw_dst=" + flow.getSrcIpAddr() + ",";
                matchString += "nw_src=" + flow.getDstIpAddr() + ",";
                matchString += "nw_proto=" + flow.getProtocol() + ",";
                matchString += "tp_dst=" + flow.getSrcPort() + ",";
                matchString += "tp_src=" + flow.getDstPort() + ",";
                matchString += "dl_type=" + 0x800 + ",";
                matchString += "in_port=" + ap2Port;

                actionString = "output=" + ap1Port;

                fm = (OFFlowMod) switchService.getOFMessageFactory()
                        .getMessage(OFType.FLOW_MOD);

                fm.setIdleTimeout((short) 0); // infinite
                fm.setHardTimeout((short) 0); // infinite
                fm.setBufferId(OFPacketOut.BUFFER_ID_NONE);
                fm.setCommand((short) 0);
                fm.setFlags((short) 0);
                fm.setOutPort(OFPort.OFPP_NONE.getValue());
                fm.setCookie((long) 0);
                fm.setPriority(Short.MAX_VALUE);
                StaticFlowEntries.initDefaultFlowMod(fm, entryName);

                LoadBalancer.parseActionString(fm, actionString);

                fm.setPriority(U16.t(LoadBalancer.LB_PRIORITY));

                ofMatch = new OFMatch();
                try {
                    ofMatch.fromString(matchString);
                } catch (IllegalArgumentException e) {
                    deleteFlow(flow);
                    throw new IllegalFlowEntryException(flow, ap1Dpid, matchString);
                }
                fm.setMatch(ofMatch);
                flowPusherService.addFlow(entryName, fm, ap1Dpid);
            }
        }

        // Get the list of ports of the flow
        Set<String> flowPortSet = new HashSet<String>();
        for (NodePortTuple node : routeNodes) {
            flowPortSet.add(HexString.toHexString(node.getNodeId()) + ":" + node.getPortId());
        }

        // Create Deployed flow
        DeployedFlow deployedFlow = new DeployedFlow();
        deployedFlow.setBandwidth(flow.getBandwidth());
        deployedFlow.setDstPort(flow.getDstPort());
        deployedFlow.setFlowId(flow.getFlowId());
        deployedFlow.setProtocol(flow.getProtocol());
        deployedFlow.setQos(flow.getQos());
        deployedFlow.setRoute(new ArrayList<String>(flowPortSet));
        deployedFlow.setSrcPort(flow.getSrcPort());

        // Add flow to the registry of flows
        currentFlows.put(flow.getFlowId(), deployedFlow);

        // Return the deployed flow
        return deployedFlow;
    }

    public void rerouteFlow(String portId) {
        // List of the flows which use this port
        List<Flow> relatedFlows = new ArrayList<Flow>();

        // Fill the flow list which use the port
        Set<String> flowIdSet = currentFlows.keySet();
        for (String flowId : flowIdSet) {
            // Get the deployed flow
            DeployedFlow deployedFlow = currentFlows.get(flowId);

            // Get the set of ports
            Set<String> portSet = new HashSet<String>(deployedFlow.getRoute());

            // Check if the  flow uses the port
            if (portSet.contains(portId)) {
                // Add the flow to a list of flows
                relatedFlows.add(new Flow(deployedFlow));
            }
        }

        // Delete and push the flows related with the port
        for (Flow flow : relatedFlows) {
            deleteFlow(flow);
            ControllerEvent controllerEvent = new ControllerEvent();
            controllerEvent.setTimestamp(new Date().getTime());
            try {
                DeployedFlow deployedFlow = pushFlow(flow);
                controllerEvent.setEvent(IFlowEvents.REROUTE_FLOW_SUCCESS);
                controllerEvent.setObject(new Gson().toJson(deployedFlow));
            } catch (DstTerminalNotFoundException e) {
                controllerEvent.setEvent(IFlowEvents.REROUTE_FLOW_DST_TERMINAL_NOT_FOUND);
                controllerEvent.setObject(new Gson().toJson(flow));
            } catch (SrcTerminalNotFoundException e) {
                controllerEvent.setEvent(IFlowEvents.REROUTE_FLOW_SRC_TERMINAL_NOT_FOUND);
                controllerEvent.setObject(new Gson().toJson(flow));
            } catch (UnreachableTerminalsException e) {
                controllerEvent.setEvent(IFlowEvents.REROUTE_FLOW_UNREACHABLE_TERMINALS);
                controllerEvent.setObject(new Gson().toJson(flow));
            } catch (IllegalFlowEntryException e) {
                controllerEvent.setEvent(IFlowEvents.REROUTE_FLOW_ILLEGAL_FLOW_ENTRY);
                controllerEvent.setObject(new Gson().toJson(flow));
            } catch (FlowAlreadyExistsException e) {
                controllerEvent.setEvent(IFlowEvents.PUSH_FLOW_FLOW_ALREADY_EXIST);
                controllerEvent.setObject(new Gson().toJson(flow));
            }

            // Broadcast event with the new deployed flow
            DxatAppModule.getInstance().getModuleServerThread().broadcastControllerEvent(controllerEvent);
        }
    }

    public DeployedFlowCollection getDeployedFlows (){
        DeployedFlowCollection deployedFlowCollection = new DeployedFlowCollection();
        List<DeployedFlow> deployedFlowList = new ArrayList<DeployedFlow>(currentFlows.values());
        deployedFlowCollection.setFlows(deployedFlowList);
        return deployedFlowCollection;
    }
}
