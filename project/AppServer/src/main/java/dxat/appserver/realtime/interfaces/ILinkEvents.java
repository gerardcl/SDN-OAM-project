package dxat.appserver.realtime.interfaces;

import dxat.appserver.topology.exceptions.LinkNotFoundException;
import dxat.appserver.topology.exceptions.PortNotFoundException;
import dxat.appserver.topology.pojos.Link;

public interface ILinkEvents {
	public final static String LINK_UPDATED = "LINK_UPDATED";
	public final static String LINK_REMOVED = "LINK_REMOVED";
	public final static String SWITCH_UPDATED = "SWITCH_UPDATED";
	public final static String SWITCH_REMOVED = "SWITCH_REMOVED";
	public final static String PORT_UP = "PORT_UP";
	public final static String PORT_DOWN = "PORT_DOWN";
	public final static String TUNEL_PORT_ADDED = "TUNEL_PORT_ADDED";
	public final static String TUNEL_PORT_REMOVED = "TUNEL_PORT_REMOVED";

	public void linkUpdated(Link link) throws LinkNotFoundException;

	public void linkRemoved(Link link) throws LinkNotFoundException ;

	public void switchUpdated(Link link) throws LinkNotFoundException ;

	public void switchRemoved(Link link) throws LinkNotFoundException ;

	public void portUp(Link link) throws PortNotFoundException ;

	public void portDown(Link link) throws LinkNotFoundException ;

	public void tunelPortAdded(Link link) throws PortNotFoundException ;

	public void tunelPortRemoved(Link link) throws LinkNotFoundException ;
}
