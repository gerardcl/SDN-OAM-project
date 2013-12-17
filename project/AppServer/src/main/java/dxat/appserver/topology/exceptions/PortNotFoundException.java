package dxat.appserver.topology.exceptions;


import dxat.appserver.topology.pojos.Port;

public class PortNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PortNotFoundException(Port port) {
		super("Port with id '" + port.getPortId() + "' not Found");
	}
	
	public PortNotFoundException(String portId) {
		super("Link with port id '" + portId + "' not Found");
	}
}
