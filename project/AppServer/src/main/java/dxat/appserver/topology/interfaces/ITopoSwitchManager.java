package dxat.appserver.topology.interfaces;

import dxat.appserver.topology.pojos.Switch;
import dxat.appserver.topology.pojos.SwitchCollection;

public interface ITopoSwitchManager {
	public SwitchCollection getSwitches();

	public Switch getSwitch(String swId);
}
