package dxat.controller.module.listeners;

import com.google.gson.Gson;
import dxat.controller.module.DxatAppModule;
import dxat.controller.module.PojoTranslator;
import dxat.controller.module.events.ITerminalEvents;
import dxat.controller.module.pojos.ControllerEvent;
import dxat.controller.module.pojos.Terminal;
import dxat.controller.module.pojos.TerminalCollection;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceListener;
import net.floodlightcontroller.devicemanager.SwitchPort;
import net.floodlightcontroller.packet.IPv4;
import net.floodlightcontroller.topology.NodePortTuple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class DeviceListener implements IDeviceListener, ITerminalEvents {

    public DeviceListener() {
        super();
    }

    @Override
    public String getName() {
        return "DXAT Project Listener";
    }

    @Override
    public boolean isCallbackOrderingPrereq(String type, String name) {
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(String type, String name) {
        return false;
    }

    @Override
    public void deviceAdded(IDevice device) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(TERMINAL_ADDED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .terminal2Pojo(device)));
        DxatAppModule.getInstance().getModuleServerThread()
                .broadcastControllerEvent(controllerEvent);
    }

    @Override
    public void deviceRemoved(IDevice device) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(TERMINAL_REMOVED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .terminal2Pojo(device)));
        DxatAppModule.getInstance().getModuleServerThread()
                .broadcastControllerEvent(controllerEvent);
    }

    @Override
    public void deviceMoved(IDevice device) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(TERMINAL_MOVED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .terminal2Pojo(device)));
        DxatAppModule.getInstance().getModuleServerThread()
                .broadcastControllerEvent(controllerEvent);
    }

    @Override
    public void deviceIPV4AddrChanged(IDevice device) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(TERMINAL_IPV4_CHANGED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .terminal2Pojo(device)));
        DxatAppModule.getInstance().getModuleServerThread()
                .broadcastControllerEvent(controllerEvent);
    }

    @Override
    public void deviceVlanChanged(IDevice device) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setEvent(TERMINAL_VLAN_CHANGED);
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .terminal2Pojo(device)));
        DxatAppModule.getInstance().getModuleServerThread()
                .broadcastControllerEvent(controllerEvent);
    }

    /**
     * This method is for get all the terminals (network devices) connected in the network. It is used when the Application
     * Server wants know all the available terminals in the network.
     *
     * @return Terminal Collection
     */
    public TerminalCollection getAllTerminalCollection() {
        TerminalCollection terminalCollection = new TerminalCollection();
        terminalCollection.setTerminals(new ArrayList<Terminal>());

        @SuppressWarnings("unchecked")
        Collection<IDevice> devs = (Collection<IDevice>) DxatAppModule
                .getInstance().getDeviceService().getAllDevices();

        for (IDevice dev : devs) {
            Terminal terminal = PojoTranslator.terminal2Pojo(dev);
            terminalCollection.getTerminals().add(terminal);
        }
        return terminalCollection;
    }

    /**
     * This method is for get the attachment point of a terminal. It has been programed for the flow push manager.
     *
     * @param ipAddr The IP v4 address of the desired terminal (or network device) in string format notation.
     * @return It return a NodePortTuple if the terminal has been found or a null pointer if has not been found.
     */
    public NodePortTuple getAttachmentPoint(String ipAddr) {
        return getAttachmentPoint(IPv4.toIPv4Address(ipAddr));
    }

    /**
     * This method is for get the attachment point of a terminal. It has been programed for the flow push manager.
     *
     * @param intAddr The IP v4 address of the desired terminal (or network device) in integer format notation.
     * @return It return a NodePortTuple if the terminal has been found or a null pointer if has not been found.
     */
    public NodePortTuple getAttachmentPoint(int intAddr) {
        NodePortTuple attachmentPoint = null;

        @SuppressWarnings("unchecked")
        Collection<IDevice> devices = (Collection<IDevice>) DxatAppModule
                .getInstance().getDeviceService().getAllDevices();
        for (IDevice device : devices) {
            Integer[] addresses = device.getIPv4Addresses();
            for (Integer addr : addresses) {
                if (addr.equals(intAddr)) {
                    SwitchPort[] attachments = device.getAttachmentPoints();
                    if (attachments.length > 0) {
                        SwitchPort attachment = attachments[0];
                        attachmentPoint = new NodePortTuple(attachment.getSwitchDPID(), attachment.getPort());
                        break;
                    }
                }
            }
        }
        return attachmentPoint;
    }
}
