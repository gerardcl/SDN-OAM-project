package dxat.controller.module;

import dxat.controller.module.exceptions.IllegalFlowEntryException;
import dxat.controller.module.pojos.Flow;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.loadbalancer.LoadBalancer;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntries;
import net.floodlightcontroller.topology.NodePortTuple;
import org.openflow.protocol.*;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionEnqueue;
import org.openflow.protocol.action.OFActionType;
import org.openflow.util.HexString;
import org.openflow.util.U16;

import java.util.*;

/**
 * Created by xavier on 1/18/14.
 */
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
            List<String> currentFlowList = new ArrayList<String>();

            // Get Switch flows
            Set<String> flowSet = switchFlowMap.get(dpid).keySet();
            for (String flowId : flowSet) {
                currentFlowList.add(new String(flowId));
            }

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

                    if (flowCostMap.containsKey(link) == false) {
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

                    if (flowCostMap.containsKey(link) == false) {
                        flowCostMap.put(link, new HashMap<String, Integer>());
                    }
                    flowCostMap.get(link).put(flow.getFlowId(), flow.getBandwidth().intValue());
                }
            } else {
                // Get second port
                nodePortTuple2 = routeNodes.get(i);

                String dpid = HexString.toHexString(nodePortTuple1.getNodeId());
                Short port1 = nodePortTuple1.getPortId();
                Short port2 = nodePortTuple2.getPortId();

                // Print entry
                //System.out.println("DPID: " + ap1Dpid + " Port1:  " + ap1Port + " Port2: " + ap2Port);

                // FORWARD Flow entry
                String entryName = dpid + "." + flow.getFlowId()
                        + ".forward";
                String matchString = "";
                matchString += "nw_dst=" + flow.getDstIpAddr() + ",";
                matchString += "nw_src=" + flow.getSrcIpAddr() + ",";
                matchString += "nw_proto=" + flow.getProtocol() + ",";
                matchString += "tp_dst=" + flow.getDstPort() + ",";
                matchString += "tp_src=" + flow.getSrcPort() + ",";
                matchString += "dl_type=" + 0x800 + ",";
                matchString += "in_port=" + port1;

                String actionString = "output=" + port2;
LinkedHashSet<Link> linkedHashSet;
                OFFlowMod fm = (OFFlowMod) switchService.getOFMessageFactory()
                        .getMessage(OFType.FLOW_MOD);

                fm.setIdleTimeout((short) 1); // infinite
                fm.setHardTimeout((short) 0); // infinite
                fm.setBufferId(OFPacketOut.BUFFER_ID_NONE);
                fm.setCommand((short) 0);
                fm.setFlags((short) 0);
                fm.setOutPort(OFPort.OFPP_NONE.getValue());
                fm.setCookie((long) 0);
                fm.setPriority(Short.MAX_VALUE);
                StaticFlowEntries.initDefaultFlowMod(fm, entryName);

                //fm.setActions()

                LoadBalancer.parseActionString(fm, actionString);

                fm.setPriority(U16.t(LoadBalancer.LB_PRIORITY));

                OFMatch ofMatch = new OFMatch();
                try {
                    ofMatch.fromString(matchString);
                } catch (IllegalArgumentException e) {
                    deleteFlowEntries(flow);
                    throw new IllegalFlowEntryException(flow, dpid, matchString);
                }
                fm.setMatch(ofMatch);
                flowPusherService.addFlow(entryName, fm, dpid);

                // BACKWARD
                entryName = dpid + "." + flow.getFlowId() + ".backward";
                matchString = "";
                matchString += "nw_dst=" + flow.getSrcIpAddr() + ",";
                matchString += "nw_src=" + flow.getDstIpAddr() + ",";
                matchString += "nw_proto=" + flow.getProtocol() + ",";
                matchString += "tp_dst=" + flow.getSrcPort() + ",";
                matchString += "tp_src=" + flow.getDstPort() + ",";
                matchString += "dl_type=" + 0x800 + ",";
                matchString += "in_port=" + port2;

                actionString = "output=" + port1;

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
                    deleteFlowEntries(flow);
                    throw new IllegalFlowEntryException(flow, dpid,
                            matchString);
                }
                fm.setMatch(ofMatch);
                flowPusherService.addFlow(entryName, fm, dpid);

            }
        }
    }
}
