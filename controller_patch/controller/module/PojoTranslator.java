package dxat.controller.module;

import dxat.controller.module.pojos.Port;
import dxat.controller.module.pojos.Switch;
import dxat.controller.module.pojos.Terminal;
import dxat.controller.module.pojos.TransferLink;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.ImmutablePort;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery.LDUpdate;
import net.floodlightcontroller.packet.IPv4;
import org.openflow.protocol.statistics.OFDescriptionStatistics;
import org.openflow.util.HexString;

import java.util.ArrayList;
import java.util.List;

public class PojoTranslator {
    public static Switch switch2Pojo(IOFSwitch inSw) {
        // Generate new switch and interfaces collection
        Switch sw = new Switch();
        List<Port> ports = new ArrayList<Port>();
        OFDescriptionStatistics desc = inSw.getDescriptionStatistics();

        // Get input switch ports and convert to interface collection
        for (ImmutablePort fPort : inSw.getPorts()) {

            // Get Hardware Address
            byte[] hwAddByte = fPort.getHardwareAddress();
            String hwAddStr = "";
            for (int i = 0; i < hwAddByte.length; i++) {
                if (i != 0)
                    hwAddStr += ":";
                hwAddStr += String.format("%02x", hwAddByte[i]);
            }

            // Create Interfaces
            Port port = new Port();
            port.setPortId(inSw.getStringId() + ":" + fPort.getPortNumber());
            port.setEnabled(fPort.isEnabled());
            port.setMac(hwAddStr);
            ports.add(port);
        }

        // Set Device attributes
        sw.setEnabled(true);
        sw.setHardware(desc.getHardwareDescription());
        sw.setManufacturer(desc.getManufacturerDescription());
        sw.setPorts(ports);
        sw.setSoftware(desc.getSoftwareDescription());
        sw.setSwId(inSw.getStringId());

        // Return Translated Switch
        return sw;
    }

    public static TransferLink linkUpdate2Pojo(LDUpdate fLink, boolean enabled) {
        TransferLink link = new TransferLink();
        link.setDstPortId(HexString.toHexString(fLink.getDst()) + ":"
                + fLink.getDstPort());
        link.setSrcPortId(HexString.toHexString(fLink.getSrc()) + ":"
                + fLink.getSrcPort());
        link.setEnabled(enabled);

        return link;
    }

    public static Terminal terminal2Pojo(IDevice dev) {
        Terminal terminal = new Terminal();
        terminal.setMac(dev.getMACAddressString());
        terminal.setTerminalId(dev.getMACAddressString());
        terminal.setEnabled(true);

        // Set ipv4
        Integer[] ipList = dev.getIPv4Addresses();
        if (ipList.length > 0) {
            terminal.setIpv4(IPv4.fromIPv4Address(ipList[0]));
        } else {
            terminal.setIpv4("0.0.0.0");
        }

        // Set Attachments
        SwitchPort[] attachments = dev.getAttachmentPoints();
        if (attachments.length > 0) {
            terminal.setPortAPId(HexString.toHexString(attachments[0]
                    .getSwitchDPID()) + ":" + attachments[0].getPort());
        } else {
            terminal.setPortAPId(HexString.toHexString(0) + ":" + 0);
        }
        return terminal;
    }

    public static TransferLink link2Pojo(
            net.floodlightcontroller.routing.Link ofLink) {
        TransferLink link = new TransferLink();
        link.setDstPortId(HexString.toHexString(ofLink.getDst()) + ":"
                + ofLink.getDstPort());
        link.setSrcPortId(HexString.toHexString(ofLink.getSrc()) + ":"
                + ofLink.getSrcPort());
        return link;
    }
}
