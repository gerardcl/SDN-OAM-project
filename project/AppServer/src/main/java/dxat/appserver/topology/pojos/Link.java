package dxat.appserver.topology.pojos;

public class Link {
	private String srcPortId = "";
	private String dstPortId = "";
	private Boolean enabled = false;

	public String getSrcPortId() {
		return srcPortId;
	}

	public void setSrcPortId(String srcPortId) {
		this.srcPortId = srcPortId;
	}

	public String getDstPortId() {
		return dstPortId;
	}

	public void setDstPortId(String dstPortId) {
		this.dstPortId = dstPortId;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public String getLinkKey() {
		return this.getSrcPortId() + ":" + this.getDstPortId();
	}
}
