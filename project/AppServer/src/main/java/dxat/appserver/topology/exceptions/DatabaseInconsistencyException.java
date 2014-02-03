package dxat.appserver.topology.exceptions;

public class DatabaseInconsistencyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public DatabaseInconsistencyException (String msg){
		super(msg);
	}
}
