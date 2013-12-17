package dxat.appserver.topology;

import dxat.appserver.realtime.interfaces.IRTSwitchManager;
import dxat.appserver.topology.db.SwitchTopologyDB;
import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.SwitchNotFoundException;
import dxat.appserver.topology.interfaces.ITopoSwitchManager;
import dxat.appserver.topology.pojos.Switch;
import dxat.appserver.topology.pojos.SwitchCollection;

public class SwitchManager implements IRTSwitchManager, ITopoSwitchManager {
	private static SwitchManager instance = null;

	private SwitchManager() {
		super();
	}

	public static SwitchManager getInstance() {
		if (instance == null)
			instance = new SwitchManager();
		return instance;
	}

	@Override
	public SwitchCollection getSwitches() {
		SwitchTopologyDB switchTopologyDB = new SwitchTopologyDB();
		SwitchCollection switchCollection = null;
		try {
			switchTopologyDB.opendb();
			switchCollection = switchTopologyDB.getAllSwitches();
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			switchTopologyDB.closedb();
		}
		return switchCollection;
	}

	@Override
	public Switch getSwitch(String swId) {
		SwitchTopologyDB switchTopologyDB = new SwitchTopologyDB();
		Switch sw = null;
		try {
			switchTopologyDB.opendb();
			sw = switchTopologyDB.getSwitch(swId);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} catch (SwitchNotFoundException e) {
			System.out.println(e.getMessage());
		} finally {
			switchTopologyDB.closedb();
		}
		return sw;
	}

	@Override
	public void addSwitch(Switch sw) {
		SwitchTopologyDB switchDB = new SwitchTopologyDB();
		try {
			switchDB.opendb();
			switchDB.addSwitch(sw);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			switchDB.closedb();
		}
	}

	@Override
	public void updateSwitch(Switch updateSwitch)
			throws SwitchNotFoundException {
		SwitchTopologyDB switchDB = new SwitchTopologyDB();
		try {
			switchDB.opendb();
			switchDB.updateSwitch(updateSwitch);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			switchDB.closedb();
		}
	}

	@Override
	public void enableSwitch(String swId) throws SwitchNotFoundException {
		SwitchTopologyDB switchDB = new SwitchTopologyDB();
		try {
			switchDB.opendb();
			switchDB.enableSwitch(swId);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			switchDB.closedb();
		}
	}

	@Override
	public void disableSwitch(String swId) throws SwitchNotFoundException {
		SwitchTopologyDB switchDB = new SwitchTopologyDB();
		try {
			switchDB.opendb();
			switchDB.disableSwitch(swId);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			switchDB.closedb();
		}
	}
}
