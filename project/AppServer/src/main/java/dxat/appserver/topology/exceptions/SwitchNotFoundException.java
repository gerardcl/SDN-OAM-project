package dxat.appserver.topology.exceptions;

import dxat.appserver.topology.pojos.Switch;

public class SwitchNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SwitchNotFoundException(Switch sw) {
		super("Switch with id '" + sw.getSwId() + "' not found");
	}

	public SwitchNotFoundException(String swId) {
		super("Switch with id '" + swId + "' not found");
	}
}
