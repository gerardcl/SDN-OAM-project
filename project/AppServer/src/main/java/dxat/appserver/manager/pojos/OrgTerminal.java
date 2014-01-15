package dxat.appserver.manager.pojos;

public class OrgTerminal {
	public String identifier;
	private String hostName;
	private String ipAddress;
	private String mac;
	private double ifaceSpeed;
	private String description;
	private boolean active;
	private boolean assigned;
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getHostName() {
		return hostName;
	}
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public double getIfaceSpeed() {
		return ifaceSpeed;
	}
	public void setIfaceSpeed(double ifaceSpeed) {
		this.ifaceSpeed = ifaceSpeed;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public boolean isAssigned() {
		return assigned;
	}
	public void setAssigned(boolean assigned) {
		this.assigned = assigned;
	}
}
