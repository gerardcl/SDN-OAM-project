package dxat.appserver.manager.pojos;

public class OrgFlow {
	public String identifier;
	private String name;
	private String srcOTidentifier;
	private String dstOTidentifier;
	private short srcPort;
	private short dstPort;
	private int qos;
	private double bandwidth;
	private String protocol;
	private String assignedOrgId;
	private boolean active;
	
	public String getAssignedOrgId() {
		return assignedOrgId;
	}
	public void setAssignedOrgId(String assignedOrgId) {
		this.assignedOrgId = assignedOrgId;
	}
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
	public short getSrcPort() {
		return srcPort;
	}
	public void setSrcPort(short srcPort) {
		this.srcPort = srcPort;
	}
	public short getDstPort() {
		return dstPort;
	}
	public void setDstPort(short dstPort) {
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
