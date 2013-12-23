package dxat.appserver.realtime.interfaces;

public interface ILinkEvents {
	public final static String LINK_UPDATED = "LINK_UPDATED";
	public final static String LINK_REMOVED = "LINK_REMOVED";
	public final static String SWITCH_UPDATED = "SWITCH_UPDATED";
	public final static String SWITCH_REMOVED = "SWITCH_REMOVED";
	public final static String PORT_UP = "PORT_UP";
	public final static String PORT_DOWN = "PORT_DOWN";
	public final static String TUNEL_PORT_ADDED = "TUNEL_PORT_ADDED";
	public final static String TUNEL_PORT_REMOVED = "TUNEL_PORT_REMOVED";
	
	public final static String LINKS_COLLECTION = "LINKS_COLLECTION";
}
