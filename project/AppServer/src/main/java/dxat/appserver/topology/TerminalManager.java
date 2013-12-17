package dxat.appserver.topology;

import dxat.appserver.realtime.interfaces.IRTTerminalManager;
import dxat.appserver.topology.db.TerminalTopologyDB;
import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.PortNotFoundException;
import dxat.appserver.topology.exceptions.TerminalNotFoundException;
import dxat.appserver.topology.interfaces.ITopoTerminalManager;
import dxat.appserver.topology.pojos.Terminal;
import dxat.appserver.topology.pojos.TerminalCollection;

public class TerminalManager implements IRTTerminalManager,
		ITopoTerminalManager {
	private static TerminalManager instance = null;

	private TerminalManager() {
		super();
	}

	public static TerminalManager getInstance() {
		if (instance == null)
			instance = new TerminalManager();
		return instance;
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
	public void addTerminal(Terminal terminal) {
		TerminalTopologyDB terminalTopologyDB = new TerminalTopologyDB();
		try {
			terminalTopologyDB.opendb();
			terminalTopologyDB.addTerminal(terminal);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} catch (PortNotFoundException e) {
			System.out.println(e.getMessage());
		} finally {
			terminalTopologyDB.closedb();
		}
	}

	@Override
	public void updateTerminal(Terminal updateTerminal)
			throws TerminalNotFoundException {
		TerminalTopologyDB terminalTopologyDB = new TerminalTopologyDB();
		try {
			terminalTopologyDB.opendb();
			terminalTopologyDB.updateTerminal(updateTerminal);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			terminalTopologyDB.closedb();
		}
	}

	@Override
	public void enableTerminal(String terminalId)
			throws TerminalNotFoundException {
		TerminalTopologyDB terminalTopologyDB = new TerminalTopologyDB();
		try {
			terminalTopologyDB.opendb();
			terminalTopologyDB.enableTerminal(terminalId);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			terminalTopologyDB.closedb();
		}
	}

	@Override
	public void disableTerminal(String terminalId)
			throws TerminalNotFoundException {
		TerminalTopologyDB terminalTopologyDB = new TerminalTopologyDB();
		try {
			terminalTopologyDB.opendb();
			terminalTopologyDB.disableTerminal(terminalId);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			terminalTopologyDB.closedb();
		}
	}
}
