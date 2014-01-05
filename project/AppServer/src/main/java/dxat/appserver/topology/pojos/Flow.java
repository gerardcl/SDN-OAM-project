package dxat.appserver.topology.pojos;

public class Flow {
	private String flowId = "";
	private String srcTerminalId = "";
	private String dstTerminalId = "";
	private int srcPort = -1;
	private int dstPort = -1;
	private int qos = -1;
	private Double bandwidth = 0.0;
	private String protocol = "";
	private Boolean enabled = false;

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getSrcTerminalId() {
		return srcTerminalId;
	}

	public void setSrcTerminalId(String srcTerminalId) {
		this.srcTerminalId = srcTerminalId;
	}

	public String getDstTerminalId() {
		return dstTerminalId;
	}

	public void setDstTerminalId(String dstTerminalId) {
		this.dstTerminalId = dstTerminalId;
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

	public Double getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(Double bandwidth) {
		this.bandwidth = bandwidth;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

}
