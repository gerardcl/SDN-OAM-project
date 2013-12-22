package dxat.appserver.topology;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dxat.appserver.realtime.interfaces.ISwitchEvents;
import dxat.appserver.realtime.pojos.ControllerEvent;
import dxat.appserver.topology.db.DbUpdate;
import dxat.appserver.topology.db.SwitchTopologyDB;
import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
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

	public List<DbUpdate> processEvent(ControllerEvent controllerEvent)
			throws JsonSyntaxException, SwitchNotFoundException {
		String eventStr = controllerEvent.getEvent();
		if (eventStr.equals(ISwitchEvents.SWITCH_ACTIVATED)) {
			SwitchManager.getInstance().switchActivated(
					new Gson().fromJson(controllerEvent.getObject(),
							Switch.class));
		} else if (eventStr.equals(ISwitchEvents.SWITCH_ADDED)) {
			SwitchManager.getInstance().switchAdded(
					new Gson().fromJson(controllerEvent.getObject(),
							Switch.class));
		} else if (eventStr.equals(ISwitchEvents.SWITCH_CHANGED)) {
			SwitchManager.getInstance().switchChanged(
					new Gson().fromJson(controllerEvent.getObject(),
							Switch.class));
		} else if (eventStr.equals(ISwitchEvents.SWITCH_PORT_CHANGED)) {
			SwitchManager.getInstance().switchPortChanged(
					new Gson().fromJson(controllerEvent.getObject(),
							Switch.class));
		} else if (eventStr.equals(ISwitchEvents.SWITCH_REMOVED)) {
			SwitchManager.getInstance().switchRemoved(
					new Gson().fromJson(controllerEvent.getObject(),
							Switch.class));
		}
		return null;
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

	private void addSwitch(Switch sw) {
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

	private void updateSwitch(Switch updateSwitch)
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

	private void enableSwitch(String swId) throws SwitchNotFoundException {
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

	private void disableSwitch(String swId) throws SwitchNotFoundException {
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

	@Override
	public void switchAdded(Switch sw) {
		addSwitch(sw);
	}

	@Override
	public void switchRemoved(Switch sw) throws SwitchNotFoundException {
		disableSwitch(sw.getSwId());
	}

	@Override
	public void switchActivated(Switch sw) throws SwitchNotFoundException {
		enableSwitch(sw.getSwId());
	}

	@Override
	public void switchPortChanged(Switch sw) throws SwitchNotFoundException {
		updateSwitch(sw);
	}

	@Override
	public void switchChanged(Switch sw) throws SwitchNotFoundException {
		updateSwitch(sw);
	}
}
