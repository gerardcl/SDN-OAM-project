package dxathacks.floodlightcontroller.module;

import java.util.Collection;

import org.openflow.protocol.statistics.OFDescriptionStatistics;

import dxathacks.floodlightcontroller.pojos.Host;
import dxathacks.floodlightcontroller.pojos.Interface;
import dxathacks.floodlightcontroller.pojos.InterfacesCollection;
import dxathacks.floodlightcontroller.pojos.Link;
import dxathacks.floodlightcontroller.pojos.Switch;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.ImmutablePort;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.devicemanager.internal.Device;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.floodlightcontroller.packet.IPv4;

public class PojoTranslator {
	public static Switch switch2Pojo (IOFSwitch inSw){
		// Generate new switch and interfaces collection
		Switch sw = new Switch();
		InterfacesCollection itfs = new InterfacesCollection();
		OFDescriptionStatistics desc = inSw.getDescriptionStatistics();
		
		// Get input switch ports and convert to interface collection
		Collection<ImmutablePort> ports = inSw.getPorts();
		for (ImmutablePort port : ports) {
			
			// Get Hardware Address
			byte[] hwAddByte = port.getHardwareAddress();
			String hwAddStr = "";
			for (int i = 0; i < hwAddByte.length; i++) {
				if (i != 0)
					hwAddStr += ":";
				hwAddStr += String.format("%02x", hwAddByte[i]);
			}
			
			// Create Interfaces
			Interface itf = new Interface();
			itf.setStatus(!port.isLinkDown());
			itf.setCurrentSpeed(Float.valueOf(port.getCurrentPortSpeed().getSpeedBps()));
			itf.setEnabled(port.isEnabled());
			itf.setPortId(port.getPortNumber());
			itf.setMac(hwAddStr);
			itfs.addInterface(itf);
		}

		// Set Device attributes
		sw.setInventoryId("SW-"+inSw.getId());
		sw.setNports(ports.size());
		sw.setInterfaces(itfs);
		sw.setOfAddr(inSw.getInetAddress().toString());
		sw.setType(inSw.getHARole().toString());
		sw.setDatapath(desc.getDatapathDescription());
		sw.setHardware(desc.getHardwareDescription());
		sw.setManufacturer(desc.getManufacturerDescription());
		sw.setSerialNum(desc.getSerialNumber());
		sw.setSoftware(desc.getSoftwareDescription());
		
		// Return Translated Switch
		return sw;
	}
	
	public static Link linkUpdate2Pojo (LDUpdate update){
		Link lnk = new Link();
		lnk.setDstPort(update.getDstPort());
		lnk.setDstSwitch("SW-" + update.getDst());
		lnk.setSrcPort(update.getSrcPort());
		lnk.setSrcSwitch("SW-" + update.getSrc());
		//lnk.setType(update.getType().name());
		lnk.setType("SWITCH2SWITCH");
		
		return lnk;
	}
	
	public static Host host2Pojo (IDevice dev){
		Host host = new Host();
		host.setDhcpName(((Device) dev).getDHCPClientName());
		host.setHostId("HOST-"+dev.getDeviceKey());
		host.setMac(dev.getMACAddressString());
		
		// Set ipv4
		Integer[] ipList = dev.getIPv4Addresses();
		for(Integer ipAddr : ipList){
			host.getIpv4().add(IPv4.fromIPv4Address(ipAddr));
		}
		
		// Set VLANs
		Short[] vlanList = dev.getVlanId();
		for(Short vlan : vlanList){
			host.getVlan().add(vlan);
		}
		
		// Set Attachments
		SwitchPort[] attachments = dev.getAttachmentPoints();
		for (SwitchPort port : attachments){
			host.getSwId().add("SW-" + port.getSwitchDPID());
			host.getPortId().add(port.getPort());
		}
		return host;
	}
}
