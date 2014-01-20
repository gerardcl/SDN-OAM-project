package dxat.controller.module.listeners;

import com.google.gson.Gson;
import dxat.controller.module.ModuleServerThread;
import dxat.controller.module.PojoTranslator;
import dxat.controller.module.events.ISwitchEvents;
import dxat.controller.module.pojos.ControllerEvent;
import dxat.controller.module.pojos.Switch;
import dxat.controller.module.pojos.SwitchCollection;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch.PortChangeType;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.ImmutablePort;
import org.openflow.util.HexString;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class SwitchListener implements IOFSwitchListener, ISwitchEvents {
    private ModuleServerThread moduleServerThread;
    private IFloodlightProviderService switchService;

    public SwitchListener(ModuleServerThread moduleServerThread,
                          IFloodlightProviderService switchService) {
        super();
        this.moduleServerThread = moduleServerThread;
        this.switchService = switchService;
    }

    public IFloodlightProviderService getSwitchService() {
        return switchService;
    }

    @Override
    public void switchAdded(long switchId) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(SWITCH_ADDED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .switch2Pojo(switchService.getSwitch(switchId))));
        moduleServerThread.sendControllerEvent(controllerEvent);
    }

    @Override
    public void switchRemoved(long switchId) {
        Switch sw = new Switch();
        sw.setSwId(HexString.toHexString(switchId));
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(SWITCH_REMOVED);
        controllerEvent.setObject(new Gson().toJson(sw));
        moduleServerThread.sendControllerEvent(controllerEvent);
    }

    @Override
    public void switchActivated(long switchId) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(SWITCH_ACTIVATED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .switch2Pojo(switchService.getSwitch(switchId))));
        moduleServerThread.sendControllerEvent(controllerEvent);
    }

    @Override
    public void switchPortChanged(long switchId, ImmutablePort port,
                                  PortChangeType type) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(SWITCH_PORT_CHANGED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .switch2Pojo(switchService.getSwitch(switchId))));
        moduleServerThread.sendControllerEvent(controllerEvent);
    }

    @Override
    public void switchChanged(long switchId) {
        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        controllerEvent.setEvent(SWITCH_CHANGED);
        controllerEvent.setObject(new Gson().toJson(PojoTranslator
                .switch2Pojo(switchService.getSwitch(switchId))));
        moduleServerThread.sendControllerEvent(controllerEvent);
    }

    public SwitchCollection getAllSwitchCollection() {
        Set<Long> switchIds = switchService.getAllSwitchDpids();
        SwitchCollection switchCollection = new SwitchCollection();
        switchCollection.setSwitches(new ArrayList<Switch>());

        for (Long switchId : switchIds) {
            switchCollection.getSwitches().add(
                    (PojoTranslator.switch2Pojo(switchService
                            .getSwitch(switchId))));
        }
        return switchCollection;
    }
}
