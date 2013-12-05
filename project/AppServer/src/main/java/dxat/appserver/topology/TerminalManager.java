package dxat.appserver.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dxat.appserver.topology.exceptions.TerminalNotFoundException;
import dxat.appserver.topology.interfaces.ITopoTerminalManager;
import dxat.appserver.topology.pojos.Terminal;
import dxat.appserver.topology.pojos.TerminalCollection;
import dxat.appserver.topology.realtime.IRTTerminalManager;

public class TerminalManager implements IRTTerminalManager,
		ITopoTerminalManager {
	private static TerminalManager instance = null;
	private HashMap<String, Terminal> terminals = null;

	private TerminalManager() {
		this.terminals = new HashMap<String, Terminal>();
	}

	public static TerminalManager getInstance() {
		if (instance == null)
			instance = new TerminalManager();

		return instance;
	}

	@Override
	public TerminalCollection getTerminals() {
		List<Terminal> terminalList = new ArrayList<Terminal>(
				this.terminals.values());
		TerminalCollection terminalCollection = new TerminalCollection();
		terminalCollection.setTerminals(terminalList);
		return terminalCollection;
	}

	@Override
	public Terminal getTerminal(String terminalId) {
		return terminals.get(terminalId);
	}

	@Override
	public void addTerminal(Terminal terminal) {
		try {
			this.updateTerminal(terminal);
		} catch (TerminalNotFoundException e) {
			this.terminals.put(terminal.getTerminalId(), terminal);
		}
	}

	@Override
	public void updateTerminal(Terminal updateTerminal)
			throws TerminalNotFoundException {
		if (!terminals.containsKey(updateTerminal.getTerminalId()))
			throw new TerminalNotFoundException("Terminal with identifier '"
					+ updateTerminal.getTerminalId() + "' not found");
		Terminal terminal = terminals.get(updateTerminal.getTerminalId());
		terminal.setEnabled(updateTerminal.getEnabled());
		terminal.setIpv4(updateTerminal.getIpv4());
		terminal.setMac(updateTerminal.getMac());
		terminal.setPortAPId(updateTerminal.getPortAPId());
	}

	@Override
	public void enableTerminal(String terminalId)
			throws TerminalNotFoundException {
		if (!terminals.containsKey(terminalId))
			throw new TerminalNotFoundException("Terminal with identifier '"
					+ terminalId + "' not found");
		terminals.get(terminalId).setEnabled(true);
	}

	@Override
	public void disableTerminal(String terminalId)
			throws TerminalNotFoundException {
		if (terminals.containsKey(terminalId))
			throw new TerminalNotFoundException("Host with identifier '"
					+ terminalId + "' not found");
		terminals.get(terminalId).setEnabled(false);
	}
}
