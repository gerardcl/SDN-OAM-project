package dxat.controller.module;

import dxat.controller.module.exceptions.IllegalFlowEntryException;
import dxat.controller.module.pojos.Flow;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntries;
import net.floodlightcontroller.topology.NodePortTuple;
import org.openflow.protocol.*;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.util.HexString;

import java.util.*;

public class FlowTableManager {

    private HashMap<Link, HashMap<String, Integer>> flowCostMap;

    public FlowTableManager() {
        super();
        flowCostMap = new HashMap<Link, HashMap<String, Integer>>();
    }

    public void deleteAllEntries() {
        // Delete all flow tables
        DxatAppModule.getInstance().getFlowPusherService().deleteAllFlows();

        // Clear flow cost map
        flowCostMap.clear();
    }

    public HashMap<Link, Integer> getCosts() {
        HashMap<Link, Integer> linkCosts = new HashMap<Link, Integer>();
        Set<Link> linkSet = flowCostMap.keySet();
        System.out.println("--- GETTING LINK COSTS ---");
        for (Link link : linkSet) {
            Integer cost = 0;
            Collection<Integer> flowCosts = flowCostMap.get(link).values();
            for (Integer flowCost : flowCosts) {
                cost = cost + flowCost;
            }
            linkCosts.put(link, cost);
            System.out.println("Link: " + link.toString() + " Cost: " + cost);
        }
        System.out.println("--- GETTING LINK COSTS ---");
        return linkCosts;
    }

    public void deleteFlowEntries(Flow flow) {
        IStaticFlowEntryPusherService flowPusherService = DxatAppModule
                .getInstance().getFlowPusherService();
        Map<String, Map<String, OFFlowMod>> switchFlowMap = flowPusherService
                .getFlows();

        // Look for the entry tables for the desired flow to delete
        Set<String> dpidSet = switchFlowMap.keySet();
        for (String dpid : dpidSet) {
            // Compute entry name start
            String entryName = dpid + "." + flow.getFlowId() + ".";
            List<String> currentFlowList = new ArrayList<String>(switchFlowMap.get(dpid).keySet());

            // Delete Switch flows with the desired id
            for (String flowId : currentFlowList) {
                if (flowId.startsWith(entryName)) {
                    flowPusherService.deleteFlow(flowId);
                }
            }
        }

        // Delete flow mappings
        Collection<HashMap<String, Integer>> flowMapCollection = flowCostMap.values();
        for (HashMap<String, Integer> flowHashMap : flowMapCollection) {
            flowHashMap.remove(flow.getFlowId());
        }
    }

    public List<String> getRelatedFlows(Link link) {
        if (!flowCostMap.containsKey(link))
            return null;

        return new ArrayList<String>(flowCostMap.get(link).keySet());
    }

    public void pushFlowEntries(Route route, Flow flow) throws IllegalFlowEntryException {
        // Get switch Service
        IFloodlightProviderService switchService = DxatAppModule.getInstance().getSwitchService();
        IStaticFlowEntryPusherService flowPusherService = DxatAppModule.getInstance().getFlowPusherService();

        int networkDestination = IPv4.toIPv4Address(flow.getDstIpAddr());
        int networkSource = IPv4.toIPv4Address(flow.getSrcIpAddr());

        // Put entries in the forwarding tables
        List<NodePortTuple> routeNodes = route.getPath();
        NodePortTuple nodePortTuple1 = null;
        NodePortTuple nodePortTuple2 = null;
        for (int i = 0; i < routeNodes.size(); i++) {
            if (i % 2 == 0) {
                // get first port
                nodePortTuple1 = routeNodes.get(i);

                if (nodePortTuple2 != null) {
                    // Map forward flow in the links
                    Link link = new Link();
                    link.setDst(nodePortTuple2.getNodeId());
                    link.setDstPort(nodePortTuple2.getPortId());
                    link.setSrc(nodePortTuple1.getNodeId());
                    link.setSrcPort(nodePortTuple1.getPortId());

                    if (!flowCostMap.containsKey(link)) {
                        flowCostMap.put(link, new HashMap<String, Integer>());
                        System.out.println("Mapping a flow to the link " + link.toString());
                    }
                    flowCostMap.get(link).put(flow.getFlowId(), flow.getBandwidth().intValue());

                    // Map backward flow in the links
                    link = new Link();
                    link.setDst(nodePortTuple1.getNodeId());
                    link.setDstPort(nodePortTuple1.getPortId());
                    link.setSrc(nodePortTuple2.getNodeId());
                    link.setSrcPort(nodePortTuple2.getPortId());

                    if (!flowCostMap.containsKey(link)) {
                        flowCostMap.put(link, new HashMap<String, Integer>());
                    }
                    flowCostMap.get(link).put(flow.getFlowId(), flow.getBandwidth().intValue());
                }
            } else {
                // Get second port
                nodePortTuple2 = routeNodes.get(i);

                if (nodePortTuple1 == null) {
                    deleteFlowEntries(flow);
                    throw new IllegalFlowEntryException(flow, "Null pointer", "");
                }

                String dpid = HexString.toHexString(nodePortTuple1.getNodeId());
                Short port1 = nodePortTuple1.getPortId();
                Short port2 = nodePortTuple2.getPortId();

                // FORWARD Flow entry
                String entryName = dpid + "." + flow.getFlowId()
                        + ".forward";

                // Set OpenFlow Match
                OFMatch ofMatch = new OFMatch();
                ofMatch.setNetworkDestination(networkDestination);
                ofMatch.setNetworkSource(networkSource);
                ofMatch.setNetworkProtocol((byte) flow.getProtocol());
                ofMatch.setTransportDestination((short) flow.getDstPort());
                ofMatch.setTransportSource((short) flow.getSrcPort());
                ofMatch.setDataLayerType((short) 0x800);
                ofMatch.setInputPort(port1);

                // Set Action
                List<OFAction> ofActionList = new ArrayList<OFAction>();
                ofActionList.add(new OFActionOutput(port2));

                // Set Flow Modification
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
                fm.setActions(ofActionList);
                fm.setMatch(ofMatch);
                StaticFlowEntries.initDefaultFlowMod(fm, entryName);

                // Push static flow entry
                flowPusherService.addFlow(entryName, fm, dpid);

                // BACKWARD
                entryName = dpid + "." + flow.getFlowId() + ".backward";

                // Set OpenFlow Match
                ofMatch = new OFMatch();
                ofMatch.setNetworkDestination(networkSource);
                ofMatch.setNetworkSource(networkDestination);
                ofMatch.setNetworkProtocol((byte) flow.getProtocol());
                ofMatch.setTransportDestination((short) flow.getSrcPort());
                ofMatch.setTransportSource((short) flow.getDstPort());
                ofMatch.setDataLayerType((short) 0x800);
                ofMatch.setInputPort(port2);

                // Set Action
                ofActionList = new ArrayList<OFAction>();
                ofActionList.add(new OFActionOutput(port1));

                // Set Flow Modification
                fm.setActions(ofActionList);
                fm.setMatch(ofMatch);
                StaticFlowEntries.initDefaultFlowMod(fm, entryName);

                // Push static flow entry
                flowPusherService.addFlow(entryName, fm, dpid);
            }
        }
    }
}
