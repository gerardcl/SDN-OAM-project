package dxat.appserver.realtime.pojos;

public class ControllerEvent {
	/*
	 * Attributes
	 */
	private String event = ""; // Available commands
	private String object = ""; // Serialized object class

	/*
	 * Getters and Setters
	 */
	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

}
