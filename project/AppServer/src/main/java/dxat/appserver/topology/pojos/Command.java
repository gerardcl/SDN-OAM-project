package dxat.appserver.topology.pojos;

public class Command {
	/*
	 * Controller Module -> Application server commands
	 */

	// Switches Commands
	public final static String ADD_SWITCH = "ADD_SWITCH";
	public final static String UPDATE_SWITCH = "UPDATE_SWITCH";
	public final static String DISABLE_SWITCH = "DISABLE_SWITCH";

	// Hosts Commands
	public final static String ADD_TERMINAL = "ADD_TERMINAL";
	public final static String UPDATE_TERMINAL = "UPDATE_TERMINAL";
	public final static String DISABLE_TERMINAL = "DISABLE_TERMINAL";

	// Links Commands
	public final static String ADD_LINK = "ADD_LINK";
	public final static String UPDATE_LINK = "UPDATE_LINK";
	public final static String DISABLE_LINK = "DISABLE_LINK";

	// Ports Statistics
	public final static String PUSH_STATS = "PUSH_STATS";

	/*
	 * Application server -> Controller Module commands
	 */

	// GET Network elements (Switches, Links and Hosts)
	public final static String GET_TERMINALS = "GET_TERMINALS";
	public final static String GET_LINKS = "GET_LINKS";
	public final static String GET_SWITCHES = "GET_SWITCHES";

	// PUSH and DELETE Flows at the controller
	public final static String PUSH_FLOW = "PUSH_FLOW";
	public final static String DELETE_FLOW = "DELETE_FLOW";
	
	/*
	 * Attributes
	 */
	private String event = ""; // Available commands
	private String object = ""; // Serialized object class

	/*
	 * Getters and Setters
	 */
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

}
