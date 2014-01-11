package dxat.controller.module.pojos;

public class Flow {
	private String flowId = "DefaultFlow";
	private String srcIpAddr = "10.0.0.1";
	private String dstIpAddr = "10.0.0.4";
	private int srcPort = 0;
	private int dstPort = 0;
	private int qos = -1;
	private Double bandwidth = 0.0;
	private String protocol = "0";

    public Flow (){
        super();
    }

    public Flow (Flow flow){
        flowId = flow.getFlowId();
        srcIpAddr = flow.getSrcIpAddr();
        dstIpAddr = flow.getDstIpAddr();
        srcPort = flow.getDstPort();
        dstPort = flow.getDstPort();
        qos = flow.getQos();
        bandwidth = flow.getBandwidth();
        protocol = flow.getProtocol();
    }

	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getSrcIpAddr() {
		return srcIpAddr;
	}

	public void setSrcIpAddr(String srcIpAddr) {
		this.srcIpAddr = srcIpAddr;
	}

	public String getDstIpAddr() {
		return dstIpAddr;
	}

	public void setDstIpAddr(String dstIpAddr) {
		this.dstIpAddr = dstIpAddr;
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

}
