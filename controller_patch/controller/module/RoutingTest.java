package dxat.controller.module;

import net.floodlightcontroller.routing.BroadcastTree;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.routing.Route;
import net.floodlightcontroller.topology.Cluster;
import net.floodlightcontroller.topology.NodePortTuple;
import net.floodlightcontroller.topology.TopologyInstance;

import java.util.*;

/**
 * Created by xavier on 1/18/14.
 */
public class RoutingTest {

    private static Link createLink(long srcNode, short srcPort, long dstNode, short dstPort) {
        Link link = new Link();
        link.setDst(dstNode);
        link.setDstPort(dstPort);
        link.setSrc(srcNode);
        link.setSrcPort(srcPort);
        return link;
    }

    private static Link createLink(NodePortTuple src, NodePortTuple dst) {
        Link link = new Link();
        link.setDst(dst.getNodeId());
        link.setDstPort(dst.getPortId());
        link.setSrc(src.getNodeId());
        link.setSrcPort(src.getPortId());
        return link;
    }

    private static LinkedList<NodePortTuple> getRoute (NodePortTuple srcNode, NodePortTuple dstNode, List<Link> links, LinkCost cost){
        // Get source and destination nodes identifiers
        long srcId = srcNode.getNodeId();
        long dstId = dstNode.getNodeId();

        // Create cluster
        Cluster cluster = new Cluster();
        for (Link link:links){
            cluster.addLink(link);
        }

        // Find links using the dijkstra algorithm
        TopologyInstance topologyInstance = new TopologyInstance();
        Map<Long, Link> nexthoplinks = topologyInstance.dijkstra(cluster, dstId, cost.getCosts(), true).getLinks();

        LinkedList<NodePortTuple> route = new LinkedList<NodePortTuple>();

        NodePortTuple npt;
        while (srcId != dstId) {
            Link l = nexthoplinks.get(srcId);

            npt = new NodePortTuple(l.getSrc(), l.getSrcPort());
            route.addLast(npt);
            npt = new NodePortTuple(l.getDst(), l.getDstPort());
            route.addLast(npt);
            srcId = nexthoplinks.get(srcId).getDst();
        }

        // Add Sor
        route.addFirst(srcNode);
        route.addLast(dstNode);

        return route;
    }

    public static void main(String[] args) {
        System.out.println("Running Flow Pusher Manager Routing TEST");
        System.out.println("-------------------------------------------");

        /* TOPOLOGY

        h1    s2   h3
          \  /  \ /
           s1   s4
          /  \ /  \
        h2   s3    h4

        */

        // Create Switches dpids
        long dpid1 = 1;
        long dpid2 = 2;
        long dpid3 = 3;
        long dpid4 = 4;

        // Create ports
        NodePortTuple sw1p1 = new NodePortTuple(dpid1, 1);
        NodePortTuple sw1p2 = new NodePortTuple(dpid1, 2);
        NodePortTuple sw1p3 = new NodePortTuple(dpid1, 3);  // Host 1
        NodePortTuple sw1p4 = new NodePortTuple(dpid1, 4);  // Host 2

        NodePortTuple sw2p1 = new NodePortTuple(dpid2, 1);
        NodePortTuple sw2p2 = new NodePortTuple(dpid2, 2);

        NodePortTuple sw3p1 = new NodePortTuple(dpid3, 1);
        NodePortTuple sw3p2 = new NodePortTuple(dpid3, 2);

        NodePortTuple sw4p1 = new NodePortTuple(dpid4, 1);
        NodePortTuple sw4p2 = new NodePortTuple(dpid4, 2);
        NodePortTuple sw4p3 = new NodePortTuple(dpid4, 3);  // Host 3
        NodePortTuple sw4p4 = new NodePortTuple(dpid4, 4);  // Host 4

        // Create Cluster
        List<Link> linkList = new ArrayList<Link>();
        // Switch 1 outs
        Link links1s2 = createLink(sw1p1, sw2p1);
        Link links1s3 = createLink(sw1p2, sw3p1);
        linkList.add(links1s2);
        linkList.add(links1s3);
        // Switch 2 outs
        Link links2s1 = createLink(sw2p1, sw1p1);
        Link links2s4 = createLink(sw2p2, sw4p1);
        linkList.add(links2s1);
        linkList.add(links2s4);
        // Switch 3 outs
        Link links3s1 = createLink(sw3p1, sw1p2);
        Link links3s4 = createLink(sw3p2, sw4p2);
        linkList.add(links3s1);
        linkList.add(links3s4);
        // Switch 4 outs
        Link links4s2 = createLink(sw4p1, sw2p2);
        Link links4s3 = createLink(sw4p2, sw3p2);
        linkList.add(links4s2);
        linkList.add(links4s3);

        // Set costs
        LinkCost linkCost = new LinkCost();
        linkCost.putCost(links1s2, 10);
        linkCost.putCost(links2s1, 10);

        LinkedList<NodePortTuple> forwardRoute = getRoute(sw1p3, sw4p4, linkList, linkCost);
        System.out.println("Route: ");
        for (NodePortTuple nodePortTuple:forwardRoute){
            System.out.println("\t " + nodePortTuple.toString());
        }
    }
}
