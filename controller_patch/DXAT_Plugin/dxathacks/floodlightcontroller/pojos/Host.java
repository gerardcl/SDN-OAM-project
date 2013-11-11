package dxathacks.floodlightcontroller.pojos;

import java.util.ArrayList;
import java.util.List;

public class Host {
	private String dhcpName = "";
	private String hostId = "";
	private String mac = null;
	private List<String> ipv4 = null; // List of IPv4 interfaces
	private List<Short> vlan = null; // VLAN tags
	private List<String> swId = null; // Switch where it is connected
	private List<Integer> portId = null; // Port where it is connected
	

	public Host() {
		ipv4 = new ArrayList<String>();
		vlan = new ArrayList<Short>();
		swId = new ArrayList<String>();
		portId = new ArrayList<Integer>();
	}

	public String getDhcpName() {
		return dhcpName;
	}

	public void setDhcpName(String dhcpName) {
		this.dhcpName = dhcpName;
	}

	public String getHostId() {
		return hostId;
	}

	public void setHostId(String hostId) {
		this.hostId = hostId;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public List<String> getIpv4() {
		return ipv4;
	}

	public void setIpv4(List<String> ipv4) {
		this.ipv4 = ipv4;
	}

	public List<Short> getVlan() {
		return vlan;
	}

	public void setVlan(List<Short> vlan) {
		this.vlan = vlan;
	}

	public List<String> getSwId() {
		return swId;
	}

	public void setSwId(List<String> swId) {
		this.swId = swId;
	}

	public List<Integer> getPortId() {
		return portId;
	}

	public void setPortId(List<Integer> portId) {
		this.portId = portId;
	}


}
