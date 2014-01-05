package dxat.appserver.topology.interfaces;

import dxat.appserver.topology.pojos.Terminal;
import dxat.appserver.topology.pojos.TerminalCollection;

public interface ITopoTerminalManager {
	public TerminalCollection getTerminals();

	public Terminal getTerminal(String terminalId);
}
