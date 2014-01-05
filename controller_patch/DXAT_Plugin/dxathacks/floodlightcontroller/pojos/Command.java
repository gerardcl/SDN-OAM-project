package dxathacks.floodlightcontroller.pojos;

public class Command {
	/*
	 * Controller Module -> Application server commands
	 */
	
	// Switches Commands
	public final static String ADD_SWITCH = "ADD_SWITCH";
	public final static String DELETE_SWITCH = "DELETE_SWITCH";
	public final static String UPDATE_SWITCH = "UPDATE_SWITCH";

	// Hosts Commands
	public final static String ADD_HOST = "ADD_HOST";
	public final static String UPDATE_HOST = "UPDATE_HOST";
	public final static String DELETE_HOST = "DELETE_HOST";

	// Links Commands
	public final static String ADD_LINK = "ADD_LINK";
	public final static String DELETE_LINK = "DELETE_LINK";
	public final static String UPDATE_LINK = "UPDATE_LINK";

	// Ports Statistics
	public final static String PUSH_STATS = "PUSH_STATS";
	
	/*
	 * Application server -> Controller Module commands
	 */
	
	// GET Network elements (Switches, Links and Hosts)
	public final static String GET_HOSTS = "GET_HOSTS";
	public final static String GET_LINKS = "GET_LINKS";
	public final static String GET_SWITCHES = "GET_SWITCHES";
	
	/*
	 *  Attributes
	 */
	private String event = ""; // Available commands
	private String object = ""; // Serialized object 
	private String source = ""; // Serialized object class

	/*
	 * Getters and Setters
	 */
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

}
