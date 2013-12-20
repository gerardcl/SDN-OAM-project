package dxat.appserver.manager.pojos;

public class OrgFlow {
	public String identifier;
	private String name;
	private String srcOTidentifier;
	private String dstOTidentifier;
	private int srcPort;
	private int dstPort;
	private int qos;
	private double bandwidth;
	private String protocol;
	private boolean active;
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSrcOTidentifier() {
		return srcOTidentifier;
	}
	public void setSrcOTidentifier(String srcOTidentifier) {
		this.srcOTidentifier = srcOTidentifier;
	}
	public String getDstOTidentifier() {
		return dstOTidentifier;
	}
	public void setDstOTidentifier(String dstOTidentifier) {
		this.dstOTidentifier = dstOTidentifier;
	}
	public int getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}
	public int getDstPort() {
		return dstPort;
	}
	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}
	public int getQos() {
		return qos;
	}
	public void setQos(int qos) {
		this.qos = qos;
	}
	public double getBandwidth() {
		return bandwidth;
	}
	public void setBandwidth(double bandwidth) {
		this.bandwidth = bandwidth;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
}
