package dxathacks.floodlightcontroller.module.listeners;

import java.util.Collection;

import dxathacks.floodlightcontroller.module.ModuleServer;
import dxathacks.floodlightcontroller.module.PojoTranslator;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.IDeviceListener;
import net.floodlightcontroller.devicemanager.IDeviceService;

public class DeviceListener implements IDeviceListener {
	private ModuleServer appInterface = null;
	private IDeviceService deviceService = null;

	public DeviceListener(ModuleServer appInterface, IDeviceService deviceService){
		this.appInterface = appInterface;
		this.deviceService = deviceService;
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
		appInterface.addHost(PojoTranslator.host2Pojo(device));
	}

	@Override
	public void deviceRemoved(IDevice device) {
		appInterface.deleteHost(PojoTranslator.host2Pojo(device));
	}

	@Override
	public void deviceMoved(IDevice device) {
		appInterface.updateHost(PojoTranslator.host2Pojo(device));
	}

	@Override
	public void deviceIPV4AddrChanged(IDevice device) {
		appInterface.updateHost(PojoTranslator.host2Pojo(device));
	}

	@Override
	public void deviceVlanChanged(IDevice device) {
		appInterface.updateHost(PojoTranslator.host2Pojo(device));
	}

	@SuppressWarnings("unchecked")
	public void updateHosts(){
		Collection<IDevice> devs = (Collection<IDevice>) deviceService.getAllDevices();
		for (IDevice dev : devs){
			appInterface.addHost(PojoTranslator.host2Pojo(dev));
		}
	}
}
