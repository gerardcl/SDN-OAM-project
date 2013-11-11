package dxathacks.floodlightcontroller.pojos;

public class Link {

	private String srcSwitch = "";
	private int srcPort = -1;
	private String dstSwitch = "";
	private int dstPort = -1;
	private String type = "";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSrcSwitch() {
		return srcSwitch;
	}

	public void setSrcSwitch(String srcSwitch) {
		this.srcSwitch = srcSwitch;
	}

	public int getSrcPort() {
		return srcPort;
	}

	public void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public String getDstSwitch() {
		return dstSwitch;
	}

	public void setDstSwitch(String dstSwitch) {
		this.dstSwitch = dstSwitch;
	}

	public int getDstPort() {
		return dstPort;
	}

	public void setDstPort(int dstPort) {
		this.dstPort = dstPort;
	}

}
