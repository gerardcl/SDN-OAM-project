package dxat.appserver.realtime.pojos;

public class ControllerEvent {
	/*
	 * Attributes
	 */
	private Long timestamp;
	private String event = ""; // Available commands
	private String object = ""; // Serialized object class

	/*
	 * Getters and Setters
	 */
	public String getEvent() {
		return event;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
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
