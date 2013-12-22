package dxat.appserver.realtime.interfaces;

import dxat.appserver.topology.exceptions.SwitchNotFoundException;
import dxat.appserver.topology.pojos.Switch;

public interface ISwitchEvents {
	public final static String SWITCH_ADDED = "SWITCH_ADDED";
	public final static String SWITCH_REMOVED = "SWITCH_REMOVED";
	public final static String SWITCH_ACTIVATED = "SWITCH_ACTIVATED";
	public final static String SWITCH_PORT_CHANGED = "SWITCH_PORT_CHANGED";
	public final static String SWITCH_CHANGED = "SWITCH_CHANGED";

	public void switchAdded(Switch sw);

	public void switchRemoved(Switch sw) throws SwitchNotFoundException;

	public void switchActivated(Switch sw) throws SwitchNotFoundException;

	public void switchPortChanged(Switch sw) throws SwitchNotFoundException;

	public void switchChanged(Switch sw) throws SwitchNotFoundException;
}
