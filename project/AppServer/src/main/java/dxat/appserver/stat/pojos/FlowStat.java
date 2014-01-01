package dxat.appserver.stat.pojos;

public class FlowStat {
	private Integer key = null;
	private String srcMac;
	private String dstMac;
	private String srcIPv4;
	private Integer srcPort;
	private String dstIPv4;
	private Integer dstPort;
	private Integer typeOfService;
	private Integer protocol;
	private Double packetCount;
	private Double byteCount;

	public Integer getKey() {
		if (key == null) {
			String strKey = "";
			strKey += srcMac;
			strKey += srcIPv4;
			strKey += srcPort;
			strKey += dstMac;
			strKey += dstIPv4;
			strKey += dstPort;
			strKey += typeOfService;
			strKey += protocol;
			key = strKey.hashCode();
		}
		return key;
	}

	public String getSrcMac() {
		return srcMac;
	}

	public void setSrcMac(String srcMac) {
		this.srcMac = srcMac;
	}

	public String getDstMac() {
		return dstMac;
	}

	public void setDstMac(String dstMac) {
		this.dstMac = dstMac;
	}

	public Integer getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(Integer srcPort) {
		this.srcPort = srcPort;
	}

	public Integer getDstPort() {
		return dstPort;
	}

	public void setDstPort(Integer dstPort) {
		this.dstPort = dstPort;
	}

	public Integer getTypeOfService() {
		return typeOfService;
	}

	public void setTypeOfService(Integer typeOfService) {
		this.typeOfService = typeOfService;
	}

	public Integer getProtocol() {
		return protocol;
	}

	public void setProtocol(Integer protocol) {
		this.protocol = protocol;
	}

	public String getSrcIPv4() {
		return srcIPv4;
	}

	public void setSrcIPv4(String srcIPv4) {
		this.srcIPv4 = srcIPv4;
	}

	public String getDstIPv4() {
		return dstIPv4;
	}

	public void setDstIPv4(String dstIPv4) {
		this.dstIPv4 = dstIPv4;
	}

	public Double getPacketCount() {
		return packetCount;
	}

	public void setPacketCount(Double packetCount) {
		this.packetCount = packetCount;
	}

	public Double getByteCount() {
		return byteCount;
	}

	public void setByteCount(Double byteCount) {
		this.byteCount = byteCount;
	}

}
