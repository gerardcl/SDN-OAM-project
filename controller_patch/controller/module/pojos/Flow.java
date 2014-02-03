package dxat.controller.module.pojos;

public class Flow {
    private String flowId = "DefaultFlow";
    private String srcIpAddr = "10.0.0.1";
    private String dstIpAddr = "10.0.0.4";
    private short srcPort = 0;
    private short dstPort = 0;
    private int qos = -1;
    private Double bandwidth = 0.0;
    private byte protocol = 0;

    public Flow() {
        super();
    }

    public Flow(Flow flow) {
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

    public Double getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Double bandwidth) {
        this.bandwidth = bandwidth;
    }

    public byte getProtocol() {
        return protocol;
    }

    public void setProtocol(byte protocol) {
        this.protocol = protocol;
    }

}
