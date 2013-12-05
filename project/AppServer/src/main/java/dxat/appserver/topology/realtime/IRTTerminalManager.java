package dxat.appserver.topology.realtime;

import dxat.appserver.topology.exceptions.TerminalNotFoundException;
import dxat.appserver.topology.pojos.Terminal;

public interface IRTTerminalManager {
	public void addTerminal(Terminal terminal);

	public void updateTerminal(Terminal terminal) throws TerminalNotFoundException;

	public void enableTerminal(String terminalId) throws TerminalNotFoundException;

	public void disableTerminal(String terminalId) throws TerminalNotFoundException;
}
