package dxat.appserver.topology.exceptions;

import dxat.appserver.topology.pojos.Link;

public class LinkNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LinkNotFoundException(Link link){
		super("Link with id '" + link.getLinkKey() + "' not found");
	}

	public LinkNotFoundException(String linkKey) {
		super("Link with id '" + linkKey + "' not found");
	}

}
