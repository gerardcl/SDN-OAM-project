package dxat.appserver.realtime.interfaces;

public interface ITerminalEvents {
	public final static String TERMINAL_ADDED = "TERMINAL_ADDED";
	public final static String TERMINAL_REMOVED = "TERMINAL_REMOVED";
	public final static String TERMINAL_MOVED = "TERMINAL_MOVED";
	public final static String TERMINAL_IPV4_CHANGED = "TERMINAL_IPV4_CHANGED";
	public final static String TERMINAL_VLAN_CHANGED = "TERMINAL_VLAN_CHANGED";
	
	public static  final String TERMINALS_COLLECTION = "TERMINALS_COLLECTION";
}
