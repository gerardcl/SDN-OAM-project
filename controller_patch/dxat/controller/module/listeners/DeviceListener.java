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
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isCallbackOrderingPostreq(String type, String name) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void deviceAdded(IDevice device) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(TERMINAL_ADDED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .terminal2Pojo(device)));
        DxatAppModule.getInstance().getModuleServerThread().broadcastControllerEvent(controllerEvent);
    }

    @Override
    public void deviceRemoved(IDevice device) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(TERMINAL_REMOVED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .terminal2Pojo(device)));
        DxatAppModule.getInstance().getModuleServerThread().broadcastControllerEvent(controllerEvent);
    }

    @Override
    public void deviceMoved(IDevice device) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(TERMINAL_MOVED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .terminal2Pojo(device)));
        DxatAppModule.getInstance().getModuleServerThread().broadcastControllerEvent(controllerEvent);
    }

    @Override
    public void deviceIPV4AddrChanged(IDevice device) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(TERMINAL_IPV4_CHANGED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .terminal2Pojo(device)));
        DxatAppModule.getInstance().getModuleServerThread().broadcastControllerEvent(controllerEvent);
    }

    @Override
    public void deviceVlanChanged(IDevice device) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .terminal2Pojo(device)));
        DxatAppModule.getInstance().getModuleServerThread().broadcastControllerEvent(controllerEvent);
    }

    public TerminalCollection getAllTerminalCollection() {
        TerminalCollection terminalCollection = new TerminalCollection();
        terminalCollection.setTerminals(new ArrayList<Terminal>());

        @SuppressWarnings("unchecked")
        Collection<IDevice> devs = (Collection<IDevice>) DxatAppModule.getInstance().getDeviceService()
                .getAllDevices();

        for (IDevice dev : devs) {
            Terminal terminal = PojoTranslator.terminal2Pojo(dev);
            terminalCollection.getTerminals().add(terminal);
        }
        return terminalCollection;
    }
}
