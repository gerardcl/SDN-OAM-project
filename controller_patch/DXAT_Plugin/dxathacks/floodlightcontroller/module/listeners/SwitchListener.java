package dxathacks.floodlightcontroller.module.listeners;

import java.util.Set;

import dxathacks.floodlightcontroller.module.ModuleServer;
import dxathacks.floodlightcontroller.module.PojoTranslator;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch.PortChangeType;
import net.floodlightcontroller.core.IOFSwitchListener;
import net.floodlightcontroller.core.ImmutablePort;

public class SwitchListener implements IOFSwitchListener {

	private ModuleServer appInterface = null;
	private IFloodlightProviderService switchService = null;

	public SwitchListener(ModuleServer appInterface,
			IFloodlightProviderService switchService) {
		this.appInterface = appInterface;
		this.switchService = switchService;
	}

	public ModuleServer getAppInterface() {
		return appInterface;
	}

	public void setAppInterface(ModuleServer appInterface) {
		this.appInterface = appInterface;
	}

	public IFloodlightProviderService getSwitchService() {
		return switchService;
	}

	public void setSwitchService(IFloodlightProviderService switchService) {
		this.switchService = switchService;
	}

	@Override
	public void switchAdded(long switchId) {
		appInterface.addSwitch(PojoTranslator.switch2Pojo(switchService
				.getSwitch(switchId)));
	}

	@Override
	public void switchRemoved(long switchId) {
		appInterface.deleteSwitch("SW-" + switchId);
	}

	@Override
	public void switchActivated(long switchId) {
		appInterface.updateSwitch(PojoTranslator.switch2Pojo(switchService
				.getSwitch(switchId)));
	}

	@Override
	public void switchPortChanged(long switchId, ImmutablePort port,
			PortChangeType type) {
		appInterface.updateSwitch(PojoTranslator.switch2Pojo(switchService
				.getSwitch(switchId)));
	}

	@Override
	public void switchChanged(long switchId) {
		appInterface.updateSwitch(PojoTranslator.switch2Pojo(switchService
				.getSwitch(switchId)));
	}

	public void updateSwitches() {
		Set<Long> swList = switchService.getAllSwitchDpids();
		for (Long swId : swList) {
			this.appInterface.addSwitch(PojoTranslator
					.switch2Pojo(switchService.getSwitch(swId)));
		}
	}

}
