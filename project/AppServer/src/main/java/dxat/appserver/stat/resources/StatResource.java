package dxat.appserver.stat.resources;

import dxat.appserver.flows.FlowManager;
import dxat.appserver.flows.pojos.DeployedFlow;
import dxat.appserver.stat.StatManager;
import dxat.appserver.stat.pojos.*;
import dxat.appserver.topology.LinkManager;
import dxat.appserver.topology.TerminalManager;
import dxat.appserver.topology.pojos.Link;
import dxat.appserver.topology.pojos.Terminal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Path("/")
public class StatResource {

    private StatManager statMngr = StatManager.getInstance();

    /*
     * @param itemId port identifier with format "XX:XX:XX:XX:XX:XX:XX:XX:X"
     *
     * @param statParameter Statistic requested parameter, are allowed:
     * receivePackets, transmitPackets, receiveBytes, transmitBytes,
     * receiveDropped, transmitDropped, receiveErrors, transmitErrors,
     * receiveFrameErrors, receiveOverrunErrors, receiveCRCErrors, collisions
     *
     * @param typeOfStat Type of the value: MIN, MAX, AVERAGE
     *
     * @param granularity Defines the last item fraction that is requested, the
     * types allowed are: second, minute, hour, day, week, year.
     *
     * @return the specific requested statistics of the desired port
     */
    @GET
    @Path("/port/{itemId}/{statParameter}/{typeOfStat}/{granularity}")
    @Produces(MediaType.STATS)
    public StatResponse getPortStat(@PathParam("itemId") String itemId,
                                    @PathParam("statParameter") String statParameter,
                                    @PathParam("typeOfStat") String typeOfStat,
                                    @PathParam("granularity") String granularity) throws IOException {

        return statMngr.getPortStat(itemId, statParameter, typeOfStat,
                granularity);
    }

    /*
     * @param itemId switch identifier (dpId) with format
     * "XX:XX:XX:XX:XX:XX:XX:XX"
     *
     * @param statParameter Statistic requested parameter, are allowed:
     * packetCount, byteCount, flowCount
     *
     * @param typeOfStat Type of the value: MIN, MAX, AVERAGE
     *
     * @param granularity Defines the last item fraction that is requested, the
     * types allowed are: second, minute, hour, day, week, year.
     *
     * @return the specific requested statistics of the desired switch
     */
    @GET
    @Path("/switch/{itemId}/{statParameter}/{typeOfStat}/{granularity}")
    @Produces(MediaType.STATS)
    public StatResponse getSwitchStat(@PathParam("itemId") String itemId,
                                      @PathParam("statParameter") String statParameter,
                                      @PathParam("typeOfStat") String typeOfStat,
                                      @PathParam("granularity") String granularity) throws IOException {

        return statMngr.getSwitchStat(itemId, statParameter, typeOfStat,
                granularity);
    }

    /*
     * @param itemId flow name with format "flowName.direction" (the directions
     * can be forward and backward)
     *
     * @param statParameter Statistic requested parameter, are allowed:
     * packetCount, byteCount
     *
     * @param typeOfStat Type of the value: MIN, MAX, AVERAGE
     *
     * @param granularity Defines the last item fraction that is requested, the
     * types allowed are: second, minute, hour, day, week, year.
     *
     * @return the specific requested statistics of the desired flow
     */
    @GET
    @Path("/flow/{itemId}/{statParameter}/{typeOfStat}/{granularity}")
    @Produces(MediaType.STATS)
    public StatResponse getFlowStat(@PathParam("itemId") String itemId,
                                    @PathParam("statParameter") String statParameter,
                                    @PathParam("typeOfStat") String typeOfStat,
                                    @PathParam("granularity") String granularity) throws IOException {

        return statMngr.getFlowStat(itemId, statParameter, typeOfStat,
                granularity);
    }

    /*
     * @param statParameter Statistic requested parameter, are allowed: CpuAvg,
     * MemoryPCT
     *
     * @param typeOfStat Type of the value: MIN, MAX, AVERAGE
     *
     * @param granularity Defines the last item fraction that is requested, the
     * types allowed are: second, minute, hour, day, week, year.
     *
     * @return the specific requested statistics of controller
     */
    @GET
    @Path("/controller/{statParameter}/{typeOfStat}/{granularity}")
    @Produces(MediaType.STATS)
    public StatResponse getFlowStat(
            @PathParam("statParameter") String statParameter,
            @PathParam("typeOfStat") String typeOfStat,
            @PathParam("granularity") String granularity) throws IOException {

        return statMngr.getControllerStat(statParameter, typeOfStat,
                granularity);
    }

	/*
     * @return the traffic matrix as a lists of ip src, ip dst and instant value
	 * 
	 */

    @GET
    @Path("/trafficmatrix/")
    @Produces(MediaType.MATRIXSTATS)
    public MatrixStat getTrafficMatrix() throws IOException {
        HashMap<AggregatedFlow, AggregatedFlow> aggregatedFlows = new HashMap<AggregatedFlow, AggregatedFlow>();

        List<DeployedFlow> deployedFlowList = FlowManager.getInstance().getFlows().getFlows();
        for (DeployedFlow deployedFlow : deployedFlowList) {
            AggregatedFlow agregatedFlow = new AggregatedFlow();
            agregatedFlow.setForward(deployedFlow);
            if (!aggregatedFlows.containsKey(agregatedFlow)) {
                aggregatedFlows.put(agregatedFlow, agregatedFlow);
            }
            aggregatedFlows.get(agregatedFlow).aggregate(statMngr.getInstance().getFlowStat(deployedFlow.getFlowId() + ".forward", "byteCount", "AVERAGE", "second").getValueAxxis()[0]);

            agregatedFlow = new AggregatedFlow();
            agregatedFlow.setBackward(deployedFlow);
            if (!aggregatedFlows.containsKey(agregatedFlow)) {
                aggregatedFlows.put(agregatedFlow, agregatedFlow);
            }
            aggregatedFlows.get(agregatedFlow).aggregate(statMngr.getInstance().getFlowStat(deployedFlow.getFlowId() + ".backward", "byteCount", "AVERAGE", "second").getValueAxxis()[0]);

        }
        List<AggregatedFlow> aggregatedFlowList = new ArrayList<AggregatedFlow>(aggregatedFlows.values());
        Collections.sort(aggregatedFlowList);
        MatrixStat matrix = new MatrixStat();
        matrix.setMatrix(aggregatedFlowList);

        return matrix;
    }

    @GET
    @Path("/weathermap/")
    @Produces(MediaType.MATRIXSTATS)
    public WeatherMap getWeatherMap() throws IOException {
        WeatherMap weatherMap = new WeatherMap();

        List<Link> links = LinkManager.getInstance().getLinks().getLinks();
        List<LinkStat> linkStats = new ArrayList<LinkStat>();

        for (Link link : links) {
            // Forward link
            LinkStat linkStat = new LinkStat();
            linkStat.setDstPortId(link.getDstPortId());
            linkStat.setSrcPortId(link.getSrcPortId());
            linkStat.setEnabled(link.getEnabled());
            linkStat.setRate(getPortStat(link.getSrcPortId(), "transmitBytes", "AVERAGE", "second").getValueAxxis()[0]);
            linkStats.add(linkStat);

            // Backward link
            /*linkStat = new LinkStat();
            linkStat.setSrcPortId(link.getDstPortId());
            linkStat.setDstPortId(link.getSrcPortId());
            linkStat.setEnabled(link.getEnabled());
            linkStat.setRate(getPortStat(link.getDstPortId(), "transmitBytes", "AVERAGE", "second").getValueAxxis()[0]);
            linkStats.add(linkStat);*/
        }
        weatherMap.setLinkStats(linkStats);

        List<AggregatedFlow> matrixStat = getTrafficMatrix().getMatrix();
        List<Terminal> terminals = TerminalManager.getInstance().getTerminals().getTerminals();
        List<TerminalStat> terminalStats = new ArrayList<TerminalStat>();

        for (Terminal terminal : terminals) {
            if (!terminal.getIpv4().equals("0.0.0.0")) {
                double in = 0.0;
                double out = 0.0;
                TerminalStat terminalStat = new TerminalStat();
                terminalStat.setEnabled(terminal.getEnabled());
                terminalStat.setIpv4(terminal.getIpv4());
                terminalStat.setMac(terminal.getMac());
                terminalStat.setPortAPId(terminal.getPortAPId());
                terminalStat.setTerminalId(terminal.getTerminalId());
                for (AggregatedFlow aggregatedFlow : matrixStat) {
                    if (aggregatedFlow.getDst().equals(terminal.getIpv4())) {
                        in += aggregatedFlow.getTraffic();
                    }

                    if (aggregatedFlow.getSrc().equals(terminal.getIpv4())) {
                        out += aggregatedFlow.getTraffic();
                    }
                }
                terminalStat.setIn(in);
                terminalStat.setOut(out);
                terminalStats.add(terminalStat);
            }
        }
        weatherMap.setTerminalStats(terminalStats);


        return weatherMap;
    }
}
