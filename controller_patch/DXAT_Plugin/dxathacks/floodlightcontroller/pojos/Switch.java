package dxathacks.floodlightcontroller.pojos;

public class Switch {
	private InterfacesCollection interfaces;
	private String inventoryId = ""; // SW-XXX (XXX is the identifier in
										// Floodlight list)
	private String ofAddr = ""; // Ip address and port of the switch
								// (configuration interface)
	private int nports = 0; // Number of ports
	private String type = ""; // Role in floodlight

	private String software = "";
	private String hardware = "";
	private String manufacturer = "";
	private String serialNum = "";
	private String datapath = "";

	public String getSoftware() {
		return software;
	}

	public void setSoftware(String software) {
		this.software = software;
	}

	public String getHardware() {
		return hardware;
	}

	public void setHardware(String hardware) {
		this.hardware = hardware;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getSerialNum() {
		return serialNum;
	}

	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}

	public String getDatapath() {
		return datapath;
	}

	public void setDatapath(String datapath) {
		this.datapath = datapath;
	}

	public InterfacesCollection getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(InterfacesCollection interfaces) {
		this.interfaces = interfaces;
	}

	public String getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getOfAddr() {
		return ofAddr;
	}

	public void setOfAddr(String ofAddr) {
		this.ofAddr = ofAddr;
	}

	public int getNports() {
		return nports;
	}

	public void setNports(int nports) {
		this.nports = nports;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
