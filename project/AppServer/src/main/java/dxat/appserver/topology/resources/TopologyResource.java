package dxat.appserver.topology.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import dxat.appserver.flows.FlowManager;
import dxat.appserver.topology.LinkManager;
import dxat.appserver.topology.SwitchManager;
import dxat.appserver.topology.TerminalManager;
import dxat.appserver.flows.pojos.DeployedFlow;
import dxat.appserver.flows.pojos.DeployedFlowCollection;
import dxat.appserver.topology.pojos.Link;
import dxat.appserver.topology.pojos.LinkCollection;
import dxat.appserver.topology.pojos.Switch;
import dxat.appserver.topology.pojos.SwitchCollection;
import dxat.appserver.topology.pojos.Terminal;
import dxat.appserver.topology.pojos.TerminalCollection;

@Path("/")
public class TopologyResource {
	private SwitchManager switchManager = SwitchManager.getInstance();
	private LinkManager linkManager = LinkManager.getInstance();
	private TerminalManager terminalManager = TerminalManager.getInstance();
	private FlowManager flowManager = FlowManager.getInstance();

	/**
     * Returns the collection of all the switches (enabled and disabled)
     *
     * @return The collection of all switches (enabled and disabled)
	 */
	@GET
	@Path("/all/switches/")
	@Produces(MediaType.SWITCHES_COLLECTION)
	public SwitchCollection getAllSwitches() {
		return switchManager.getSwitches();
	}

	@GET
	@Path("/switches/")
	@Produces(MediaType.SWITCHES_COLLECTION)
	public SwitchCollection getEnabledSwitches() {
		List<Switch> allSwitches = switchManager.getSwitches().getSwitches();
		List<Switch> enabledSwitches = new ArrayList<Switch>();
		SwitchCollection switchCollection = new SwitchCollection();

		for (Switch sw : allSwitches) {
			if (sw.getEnabled())
				enabledSwitches.add(sw);
		}
		switchCollection.setSwitches(enabledSwitches);
		return switchCollection;
	}

	@GET
	@Path("/switches/{swId}")
	@Produces(MediaType.SWITCH)
	public Switch getSwitch(@PathParam("swId") String swId) {
		return switchManager.getSwitch(swId);
	}

	@GET
	@Path("/all/links/")
	@Produces(MediaType.LINKS_COLLECTION)
	public LinkCollection getAllLinks() {
		return linkManager.getLinks();
	}

	@GET
	@Path("/links/")
	@Produces(MediaType.LINKS_COLLECTION)
	public LinkCollection getEnabledLinks() {
		// ITopoSwitchManager switchManager = SwitchManager.getInstance();
		List<Link> allLinks = linkManager.getLinks().getLinks();
		List<Link> enabledLinks = new ArrayList<Link>();
		LinkCollection linkCollection = new LinkCollection();

		for (Link lk : allLinks) {
			if (lk.getEnabled())
				enabledLinks.add(lk);
		}
		linkCollection.setLinks(enabledLinks);
		return linkCollection;
	}

	@GET
	@Path("/links/{srcPortId}/{dstPortId}")
	@Produces(MediaType.LINK)
	public Link getLink(@PathParam("srcPortId") String srcPortId,
			@PathParam("dstPortId") String dstPortId) {
		return linkManager.getLink(srcPortId, dstPortId);
	}

	@GET
	@Path("/all/terminals/")
	@Produces(MediaType.TERMINALS_COLLECTION)
	public TerminalCollection getAllTerminals() {
		TerminalCollection terminalCollection = terminalManager.getTerminals();
		if (terminalCollection == null){
			return new TerminalCollection();
		}
		return terminalCollection;
	}

	@GET
	@Path("/terminals/")
	@Produces(MediaType.TERMINALS_COLLECTION)
	public TerminalCollection getEnabledTerminals() {
		List<Terminal> allTerminals = terminalManager.getTerminals()
				.getTerminals();
		List<Terminal> enabledTerminals = new ArrayList<Terminal>();
		TerminalCollection terminalCollection = new TerminalCollection();

		for (Terminal terminal : allTerminals) {
			if (terminal.getEnabled())
				enabledTerminals.add(terminal);
		}
		terminalCollection.setTerminals(enabledTerminals);
		return terminalCollection;
	}

	@GET
	@Path("/terminals/{terminalId}")
	@Produces(MediaType.TERMINAL)
	public Terminal getLink(@PathParam("terminalId") String terminalId) {
		return terminalManager.getTerminal(terminalId);
	}

	@GET
	@Path("/all/flows/")
	@Produces(MediaType.FLOW)
	public DeployedFlowCollection getAllFlows() {
		return flowManager.getFlows();
	}

	@GET
	@Path("/flows/")
	@Produces(MediaType.FLOWS_COLLECTION)
	public DeployedFlowCollection getEnabledFlows() {
		List<DeployedFlow> allFlows = flowManager.getFlows().getFlows();
		List<DeployedFlow> enabledFlows = new ArrayList<DeployedFlow>();
		DeployedFlowCollection flowCollection = new DeployedFlowCollection();

		for (DeployedFlow flow : allFlows) {
			if (flow.getEnabled())
				enabledFlows.add(flow);
		}
		flowCollection.setFlows(enabledFlows);
		return flowCollection;
	}

	@GET
	@Path("/flows/{flowId}")
	@Produces(MediaType.FLOW)
	public DeployedFlow getFlow(@PathParam("flowId") String flowId) {
		return flowManager.getFlow(flowId);
	}
}
