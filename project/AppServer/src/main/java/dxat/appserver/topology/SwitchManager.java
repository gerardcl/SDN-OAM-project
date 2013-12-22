package dxat.appserver.topology;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import dxat.appserver.realtime.interfaces.ISwitchEvents;
import dxat.appserver.realtime.pojos.ControllerEvent;
import dxat.appserver.topology.db.DbUpdate;
import dxat.appserver.topology.db.SwitchTopologyDB;
import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.SwitchExistsException;
import dxat.appserver.topology.exceptions.SwitchNotFoundException;
import dxat.appserver.topology.interfaces.ITopoSwitchManager;
import dxat.appserver.topology.pojos.Switch;
import dxat.appserver.topology.pojos.SwitchCollection;

public class SwitchManager implements ITopoSwitchManager, ISwitchEvents {
	private static SwitchManager instance = null;

	private SwitchManager() {
		super();
	}

	public static SwitchManager getInstance() {
		if (instance == null)
			instance = new SwitchManager();
		return instance;
	}

	private Switch getSwitchFromJSON(String jsonStr) {
		return new Gson().fromJson(jsonStr, Switch.class);
	}

	public List<DbUpdate> processEvent(ControllerEvent controllerEvent)
			throws CannotOpenDataBaseException, SwitchNotFoundException {
		String eventStr = controllerEvent.getEvent();
		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		if (eventStr.equals(ISwitchEvents.SWITCH_ACTIVATED)
				|| eventStr.equals(ISwitchEvents.SWITCH_CHANGED)
				|| eventStr.equals(ISwitchEvents.SWITCH_PORT_CHANGED)) {
			Switch sw = getSwitchFromJSON(controllerEvent.getObject());
			SwitchTopologyDB switchDB = new SwitchTopologyDB();
			try {
				switchDB.opendb();
				updates.addAll(switchDB.updateSwitch(sw));
			} catch (SwitchNotFoundException e) {
				throw e;
			} finally {
				switchDB.closedb();
			}
		} else if (eventStr.equals(ISwitchEvents.SWITCH_ADDED)) {
			Switch sw = getSwitchFromJSON(controllerEvent.getObject());
			SwitchTopologyDB switchDB = new SwitchTopologyDB();
			try {
				switchDB.opendb();
				try {
					updates.addAll(switchDB.addSwitch(sw));
				} catch (SwitchExistsException e1) {
					updates.addAll(switchDB.updateSwitch(sw));
				}
			} catch (SwitchNotFoundException e) {
				throw e;
			} finally {
				switchDB.closedb();
			}
		} else if (eventStr.equals(ISwitchEvents.SWITCH_REMOVED)) {
			Switch sw = getSwitchFromJSON(controllerEvent.getObject());
			SwitchTopologyDB switchDB = new SwitchTopologyDB();
			try {
				switchDB.opendb();
				updates.addAll(switchDB.disableSwitch(sw.getSwId()));
			} catch (SwitchNotFoundException e) {
				throw e;
			} finally {
				switchDB.closedb();
			}
		} else if (eventStr.equals(ISwitchEvents.SWITCHES_COLLECTION)) {
			SwitchCollection switchCollection = new Gson().fromJson(
					controllerEvent.getObject(), SwitchCollection.class);
			SwitchTopologyDB switchDB = new SwitchTopologyDB();
			switchDB.opendb();
			updates.addAll(switchDB.mergeCollection(switchCollection));
			switchDB.closedb();
		}
		return updates;
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

}
