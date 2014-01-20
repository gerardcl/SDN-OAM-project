package dxat.controller.module.stats;

import com.google.gson.Gson;
import com.sun.management.OperatingSystemMXBean;
import dxat.controller.module.DxatAppModule;
import dxat.controller.module.events.IStatisticsEvent;
import dxat.controller.module.pojos.*;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import org.openflow.protocol.OFFlowMod;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.statistics.*;
import org.openflow.util.HexString;

import java.lang.management.ManagementFactory;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class StatisticsThread implements Runnable {
    public StatisticsThread() {
        super();
    }

    private FlowStat getFlowStat(String dpid, OFMatch inMatch) {
        // Get switch service
        IFloodlightProviderService switchService = DxatAppModule.getInstance().getSwitchService();

        // Get switch
        IOFSwitch sw = switchService.getSwitch(HexString.toLong(dpid));

        FlowStat flowStat = null;
        OFStatisticsRequest req = new OFStatisticsRequest();
        req.setStatisticType(OFStatisticsType.FLOW);
        int requestLength = req.getLengthU();
        OFFlowStatisticsRequest specificReq = new OFFlowStatisticsRequest();
        OFMatch match = new OFMatch();
        match.setWildcards(0xffffffff);
        specificReq.setMatch(match);
        specificReq.setOutPort(OFPort.OFPP_NONE.getValue());
        specificReq.setTableId((byte) 0xff);
        req.setStatistics(Collections.singletonList((OFStatistics) specificReq));
        requestLength += specificReq.getLength();
        req.setLengthU(requestLength);
        try {
            Future<List<OFStatistics>> future = sw.queryStatistics(req);
            List<OFStatistics> values = future.get(10, TimeUnit.SECONDS);
            for (int i = 0; i < values.size(); i++) {
                OFFlowStatisticsReply oFFlowStat = (OFFlowStatisticsReply) values
                        .get(i);
                OFMatch ofMatch = oFFlowStat.getMatch();
                if (inMatch.getDataLayerType() == ofMatch.getDataLayerType()
                        && inMatch.getNetworkDestination() == ofMatch
                        .getNetworkDestination()
                        && inMatch.getNetworkProtocol() == ofMatch
                        .getNetworkProtocol()
                        && inMatch.getNetworkSource() == ofMatch
                        .getNetworkSource()
                        && inMatch.getNetworkTypeOfService() == ofMatch
                        .getNetworkTypeOfService()
                        && inMatch.getTransportDestination() == ofMatch
                        .getTransportDestination()
                        && inMatch.getTransportSource() == ofMatch
                        .getTransportSource()) {
                    flowStat = new FlowStat();
                    flowStat.setByteCount(0.0 + oFFlowStat.getByteCount());
                    flowStat.setPacketCount(0.0 + oFFlowStat.getPacketCount());
                }
            }
        } catch (Exception e) {
            System.out
                    .println("[DXAT STATISTICS MODULE EXCEPTION] Switch with dpid "
                            + dpid + " unreachable.");
        }
        return flowStat;
    }

    private List<FlowStat> getFlowStats() {
        HashMap<String, FlowStat> flowStatMap = new HashMap<String, FlowStat>();
        Map<String, Map<String, OFFlowMod>> switchFlowMap = DxatAppModule.getInstance().getFlowPusherService().getFlows();
        Set<String> dpidList = switchFlowMap.keySet();

        for (String dpid : dpidList) {
            Set<String> flowEntries = switchFlowMap.get(dpid).keySet();
            for (String entryName : flowEntries) {
                String[] parts = entryName.split("\\.");
                if (parts.length > 1) {
                    String flowName = parts[1] + "." + parts[2];
                    if (!flowStatMap.containsKey(flowName)) {
                        FlowStat flowStat = getFlowStat(dpid, switchFlowMap.get(dpid).get(entryName).getMatch());
                        if (flowStat != null) {
                            flowStat.setName(flowName);
                            flowStatMap.put(flowName, flowStat);
                        }
                    }
                }
            }
        }

        return new ArrayList<FlowStat>(flowStatMap.values());
    }

    private void sendStats() {
        // Get switch service
        IFloodlightProviderService switchService = DxatAppModule.getInstance().getSwitchService();

        // Define StatCollection
        StatCollection statCollection = new StatCollection();
        List<PortStat> portStats = new ArrayList<PortStat>();
        List<SwitchStat> switchStats = new ArrayList<SwitchStat>();
        statCollection.setPortStatCollection(portStats);
        statCollection.setSwitchStatCollection(switchStats);

        // Controller Stat
        ControllerStat controllerStat = new ControllerStat();
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory
                .getOperatingSystemMXBean();
        Double freeMem = new Double(Runtime.getRuntime().freeMemory());
        Double totalMem = new Double(Runtime.getRuntime().totalMemory());

        controllerStat.setMemoryPct(100.0 * (totalMem - freeMem) / totalMem);
        controllerStat.setCpuAvg(100.0 * operatingSystemMXBean
                .getSystemCpuLoad());
        statCollection.setControllerStat(controllerStat);

        // Get switches IDs
        Set<Long> swList = switchService.getAllSwitchDpids();
        for (Long swId : swList) {
            // Get switch
            IOFSwitch sw = switchService.getSwitch(swId);

            // Get Switch Aggregate
            SwitchStat switchStat = new SwitchStat();
            switchStat.setByteCount(0);
            switchStat.setFlowCount(0);
            switchStat.setPacketCount(0);
            switchStat.setSwitchId(sw.getStringId());
            switchStats.add(switchStat);

            // Get Per Port Stats
            OFStatisticsRequest req = new OFStatisticsRequest();
            req.setStatisticType(OFStatisticsType.PORT);
            int requestLength = req.getLengthU();
            Future<List<OFStatistics>> future;
            List<OFStatistics> values = null;
            OFPortStatisticsRequest specificPortReq = new OFPortStatisticsRequest();
            specificPortReq.setPortNumber(OFPort.OFPP_NONE.getValue());
            req.setStatistics(Collections
                    .singletonList((OFStatistics) specificPortReq));
            requestLength += specificPortReq.getLength();

            req.setLengthU(requestLength);
            try {
                future = sw.queryStatistics(req);
                values = future.get(10, TimeUnit.SECONDS);
                for (int i = 0; i < values.size(); i++) {
                    OFPortStatisticsReply ofstat = (OFPortStatisticsReply) values
                            .get(i);
                    PortStat portStat = new PortStat();
                    portStat.setPortId(sw.getStringId() + ":"
                            + ofstat.getPortNumber());
                    portStat.setCollisions(ofstat.getCollisions());
                    portStat.setReceiveBytes(ofstat.getReceiveBytes());
                    portStat.setReceiveCRCErrors(ofstat.getReceiveCRCErrors());
                    portStat.setReceiveDropped(ofstat.getReceiveDropped());
                    portStat.setReceiveErrors(ofstat.getreceiveErrors());
                    portStat.setReceiveFrameErrors(ofstat
                            .getReceiveFrameErrors());
                    portStat.setReceiveOverrunErrors(ofstat
                            .getReceiveOverrunErrors());
                    portStat.setReceivePackets(ofstat.getreceivePackets());
                    portStat.setTransmitBytes(ofstat.getTransmitBytes());
                    portStat.setTransmitDropped(ofstat.getTransmitDropped());
                    portStat.setTransmitErrors(ofstat.getTransmitErrors());
                    portStat.setTransmitPackets(ofstat.getTransmitPackets());
                    portStats.add(portStat);
                }
            } catch (Exception e) {
                System.out.println("Failure retrieving statistics from switch "
                        + sw);
            }
        }

        // Get Flow stats
        statCollection.setFlowStatCollection(getFlowStats());

        // System.out.println("[Pushing stats...]");

        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setEvent(IStatisticsEvent.PUSH_STATS);
        controllerEvent.setObject(new Gson().toJson(statCollection));
        DxatAppModule.getInstance().getModuleServerThread().sendControllerEvent(controllerEvent);
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            this.sendStats();
        }
    }
}
