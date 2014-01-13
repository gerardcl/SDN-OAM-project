package dxat.controller.module.pojos;

public class ServerRequest {
	/*
	 * Attributes
	 */
	private String request = ""; // Available commands
	private String object = ""; // Serialized object class

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

}
