package dxat.appserver.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dxat.appserver.topology.exceptions.SwitchNotFoundException;
import dxat.appserver.topology.interfaces.ITopoSwitchManager;
import dxat.appserver.topology.pojos.Port;
import dxat.appserver.topology.pojos.Switch;
import dxat.appserver.topology.pojos.SwitchCollection;
import dxat.appserver.topology.realtime.IRTSwitchManager;

public class SwitchManager implements IRTSwitchManager, ITopoSwitchManager {
	private static SwitchManager instance = null;
	private HashMap<String, Switch> switches = null;

	private SwitchManager() {
		this.switches = new HashMap<String, Switch>();

		Switch sw = null;
		sw = new Switch();
		sw.setEnabled(true);
		sw.setHardware("Super Dotted");
		sw.setManufacturer("Made in home");
		sw.setSoftware("Made by an little Indian hungry programmer");
		sw.setSwId("TestSwitch");

		Port port = null;
		port = new Port();
		port.setEnabled(true);
		port.setMac("This is a port MAC");
		port.setName("This is a port name");
		port.setPortId(sw.getSwId() + ":" + 1);
		sw.getPorts().add(port);
		this.switches.put(sw.getSwId(), sw);
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
			this.switches.put(sw.getSwId(), sw);
		}
	}

	@Override
	public void updateSwitch(Switch updateSwitch)
			throws SwitchNotFoundException {
		if (!this.switches.containsKey(updateSwitch.getSwId()))
			throw new SwitchNotFoundException("Switch with identifier '"
					+ updateSwitch.getSwId() + "' not found");
		Switch sw = switches.get(updateSwitch.getSwId());
		sw.setEnabled(updateSwitch.getEnabled());
		sw.setHardware(updateSwitch.getHardware());
		sw.setManufacturer(updateSwitch.getManufacturer());
		sw.setPorts(updateSwitch.getPorts());
		sw.setSoftware(updateSwitch.getSoftware());
	}

	@Override
	public void enableSwitch(String swId) throws SwitchNotFoundException {
		if (!this.switches.containsKey(swId))
			throw new SwitchNotFoundException("Switch with identifier '" + swId
					+ "' not found");
		this.switches.get(swId).setEnabled(true);
	}

	@Override
	public void disableSwitch(String swId) throws SwitchNotFoundException {
		if (this.switches.containsKey(swId))
			throw new SwitchNotFoundException("Switch with identifier '" + swId
					+ "' not found");
		this.switches.get(swId).setEnabled(false);
	}
}
