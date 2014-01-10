package dxat.appserver.topology.resources;

public interface MediaType {
	// Switches
	public final static String SWITCH = "application/vmd.dxat.appserver.topology.switch+json";
	public final static String SWITCHES_COLLECTION = "application/vmd.dxat.appserver.topology.switches.collection+json";
	// Links
	public final static String LINK = "application/vmd.dxat.appserver.topology.link+json";
	public final static String LINKS_COLLECTION = "application/vmd.dxat.appserver.topology.links.collection+json";
	// Terminals
	public final static String TERMINAL = "application/vmd.dxat.appserver.topology.terminal+json";
	public final static String TERMINALS_COLLECTION = "application/vmd.dxat.appserver.topology.terminals.collection+json";
	// Flows
	public final static String FLOW = "application/vmd.dxat.appserver.topology.flow+json";
	public final static String FLOWS_COLLECTION = "application/vmd.dxat.appserver.topology.flows.collection+json";
}
