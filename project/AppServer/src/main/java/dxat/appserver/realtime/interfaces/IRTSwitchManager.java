package dxat.appserver.realtime.interfaces;

import dxat.appserver.topology.exceptions.SwitchNotFoundException;
import dxat.appserver.topology.pojos.Switch;

public interface IRTSwitchManager {
	public void addSwitch(Switch sw);

	public void updateSwitch(Switch sw) throws SwitchNotFoundException;

	public void enableSwitch(String swId) throws SwitchNotFoundException;

	public void disableSwitch(String swId) throws SwitchNotFoundException;
}
