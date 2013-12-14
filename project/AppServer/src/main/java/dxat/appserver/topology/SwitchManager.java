package dxat.appserver.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dxat.appserver.realtime.RealTimeManager;
import dxat.appserver.realtime.interfaces.IRTSwitchManager;
import dxat.appserver.topology.exceptions.SwitchNotFoundException;
import dxat.appserver.topology.interfaces.ITopoSwitchManager;
import dxat.appserver.topology.pojos.Switch;
import dxat.appserver.topology.pojos.SwitchCollection;

public class SwitchManager implements IRTSwitchManager, ITopoSwitchManager {
	private static SwitchManager instance = null;
	private HashMap<String, Switch> switches = null;

	private SwitchManager() {
		super();
		this.switches = new HashMap<String, Switch>();
	}

	public static SwitchManager getInstance() {
		if (instance == null)
			instance = new SwitchManager();
		return instance;
	}

	@Override
	public SwitchCollection getSwitches() {
		List<Switch> swList = new ArrayList<Switch>(this.switches.values());
		SwitchCollection swCollection = new SwitchCollection();
		swCollection.setSwitches(swList);
		return swCollection;
	}

	@Override
	public Switch getSwitch(String swId) {
		return switches.get(swId);
	}

	@Override
	public void addSwitch(Switch sw) {
		try {
			this.updateSwitch(sw);
		} catch (SwitchNotFoundException e) {
			RealTimeManager.getInstance().broadcast(
					"[ADDING SWITCH] Switch Key: " + sw.getSwId());
			this.switches.put(sw.getSwId(), sw);
		}
	}

	@Override
	public void updateSwitch(Switch updateSwitch)
			throws SwitchNotFoundException {
		// Check existence of the current switch
		if (!this.switches.containsKey(updateSwitch.getSwId()))
			throw new SwitchNotFoundException("Switch with identifier '"
					+ updateSwitch.getSwId() + "' not found");

		// Broadcast event
		RealTimeManager.getInstance().broadcast(
				"[UPDATING SWITCH] Switch Key: " + updateSwitch.getSwId());

		// Update swit
		Switch sw = switches.get(updateSwitch.getSwId());
		sw.setEnabled(updateSwitch.getEnabled());
		sw.setHardware(updateSwitch.getHardware());
		sw.setManufacturer(updateSwitch.getManufacturer());
		sw.setPorts(updateSwitch.getPorts());
		sw.setSoftware(updateSwitch.getSoftware());
	}

	@Override
	public void enableSwitch(String swId) throws SwitchNotFoundException {
		// Check existence of the current switch
		if (!this.switches.containsKey(swId))
			throw new SwitchNotFoundException("Switch with identifier '" + swId
					+ "' not found");
		// Broadcast event
		RealTimeManager.getInstance().broadcast(
				"[ENABLING SWITCH] Switch Key: " + swId);
		// Enable Switch
		this.switches.get(swId).setEnabled(true);
	}

	@Override
	public void disableSwitch(String swId) throws SwitchNotFoundException {
		// Check existence of the current switch
		if (this.switches.containsKey(swId))
			throw new SwitchNotFoundException("Switch with identifier '" + swId
					+ "' not found");
		// Broadcast event
		RealTimeManager.getInstance().broadcast(
				"[DISABLING SWITCH] Switch Key: " + swId);
		// Disable switch
		this.switches.get(swId).setEnabled(false);
	}
}
