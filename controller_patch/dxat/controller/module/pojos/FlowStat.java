package dxat.controller.module.pojos;

public class FlowStat {
	private String name;
	private Double packetCount;
	private Double byteCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
