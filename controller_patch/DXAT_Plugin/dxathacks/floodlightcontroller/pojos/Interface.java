package dxathacks.floodlightcontroller.pojos;

public class Interface {
	private float currentSpeed = 0; // Bits Per Second
	private Boolean enabled = false; // Enabled
	private Boolean status = false; // True: Online; False: Offline
	private String mac = ""; // Ethernet MAC
	private int portId = -1; // Needed for the relational database

	public float getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(float currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getPortId() {
		return portId;
	}

	public void setPortId(int portId) {
		this.portId = portId;
	}

}
