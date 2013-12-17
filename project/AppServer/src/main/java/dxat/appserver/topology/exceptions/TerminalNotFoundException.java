package dxat.appserver.topology.exceptions;

import dxat.appserver.topology.pojos.Terminal;

public class TerminalNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TerminalNotFoundException(Terminal terminal) {
		super("Terminal with id '" + terminal.getTerminalId() + "' not found");
	}

	public TerminalNotFoundException(String terminalId) {
		super("Terminal with id '" + terminalId + "' not found");
	}
}
