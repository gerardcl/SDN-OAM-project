package dxat.appserver.topology;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import dxat.appserver.realtime.interfaces.ITerminalEvents;
import dxat.appserver.realtime.pojos.ControllerEvent;
import dxat.appserver.topology.db.DbUpdate;
import dxat.appserver.topology.db.TerminalTopologyDB;
import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.PortNotFoundException;
import dxat.appserver.topology.exceptions.TerminalExistsException;
import dxat.appserver.topology.exceptions.TerminalNotFoundException;
import dxat.appserver.topology.interfaces.ITopoTerminalManager;
import dxat.appserver.topology.pojos.Terminal;
import dxat.appserver.topology.pojos.TerminalCollection;

public class TerminalManager implements ITerminalEvents, ITopoTerminalManager {
	private static TerminalManager instance = null;

	private TerminalManager() {
		super();
	}

	public static TerminalManager getInstance() {
		if (instance == null)
			instance = new TerminalManager();
		return instance;
	}

	public Terminal fromJSON(String jsonStr) {
		return new Gson().fromJson(jsonStr, Terminal.class);
	}

	public List<DbUpdate> processEvent(ControllerEvent controllerEvent)
			throws CannotOpenDataBaseException, PortNotFoundException,
			TerminalNotFoundException {

		String eventStr = controllerEvent.getEvent();
		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		if (eventStr.equals(ITerminalEvents.TERMINAL_ADDED)) {
			TerminalTopologyDB terminalTopologyDB = new TerminalTopologyDB();
			terminalTopologyDB.opendb();
			Terminal terminal = fromJSON(controllerEvent.getObject());
			try {
				try {
					updates.addAll(terminalTopologyDB.addTerminal(terminal));
				} catch (TerminalExistsException e) {
					updates.addAll(terminalTopologyDB.updateTerminal(terminal));
				}
			} catch (PortNotFoundException e) {
				throw e;
			} catch (TerminalNotFoundException e) {
				throw e;
			} finally {
				terminalTopologyDB.closedb();
			}

		} else if (eventStr.equals(ITerminalEvents.TERMINAL_IPV4_CHANGED)
				|| eventStr.equals(ITerminalEvents.TERMINAL_MOVED)
				|| eventStr.equals(ITerminalEvents.TERMINAL_VLAN_CHANGED)) {
			TerminalTopologyDB terminalTopologyDB = new TerminalTopologyDB();
			terminalTopologyDB.opendb();
			Terminal terminal = fromJSON(controllerEvent.getObject());
			try {
				updates.addAll(terminalTopologyDB.updateTerminal(terminal));
			} catch (TerminalNotFoundException e) {
				throw e;
			} finally {
				terminalTopologyDB.closedb();
			}
		} else if (eventStr.equals(ITerminalEvents.TERMINAL_REMOVED)) {
			TerminalTopologyDB terminalTopologyDB = new TerminalTopologyDB();
			terminalTopologyDB.opendb();
			Terminal terminal = fromJSON(controllerEvent.getObject());
			try {
				updates.addAll(terminalTopologyDB.disableTerminal(terminal
						.getTerminalId()));
			} catch (TerminalNotFoundException e) {
				throw e;
			} finally {
				terminalTopologyDB.closedb();
			}
		} else if (eventStr.equals(ITerminalEvents.TERMINALS_COLLECTION)) {
			TerminalCollection terminalCollection = new Gson().fromJson(
					controllerEvent.getObject(), TerminalCollection.class);
			TerminalTopologyDB terminalDB = new TerminalTopologyDB();
			terminalDB.opendb();
			updates.addAll(terminalDB.mergeCollection(terminalCollection));
			terminalDB.closedb();
		}

		return updates;
	}

	@Override
	public Terminal getTerminal(String terminalId) {
		TerminalTopologyDB terminalTopologyDB = new TerminalTopologyDB();
		Terminal terminal = null;
		try {
			terminalTopologyDB.opendb();
			terminal = terminalTopologyDB.getTerminal(terminalId);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} catch (TerminalNotFoundException e) {
			System.out.println(e.getMessage());
		} finally {
			terminalTopologyDB.closedb();
		}
		return terminal;
	}

	@Override
	public TerminalCollection getTerminals() {
		TerminalTopologyDB terminalTopologyDB = new TerminalTopologyDB();
		TerminalCollection terminalCollection = null;
		try {
			terminalTopologyDB.opendb();
			terminalCollection = terminalTopologyDB.getAllTerminals();
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			terminalTopologyDB.closedb();
		}
		return terminalCollection;
	}

}
