package dxathacks.floodlightcontroller.module;

import java.util.Collection;
import java.util.Map;

import dxathacks.floodlightcontroller.module.listeners.DeviceListener;
import dxathacks.floodlightcontroller.module.listeners.LinkListener;
import dxathacks.floodlightcontroller.module.listeners.SwitchListener;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;

public class DxatAppModule implements IFloodlightModule {

	// Module server and threat
	private Thread serverThreat = null;
	private ModuleServer appInterface = null;

	// Listeners
	private DeviceListener deviceListener = null;
	private SwitchListener switchListener = null;
	private LinkListener linkListener = null;

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {

		// Get Services
		IDeviceService deviceService = context
				.getServiceImpl(IDeviceService.class);
		ILinkDiscoveryService linkService = context
				.getServiceImpl(ILinkDiscoveryService.class);
		IFloodlightProviderService switchService = context
				.getServiceImpl(IFloodlightProviderService.class);
		
		// Set Application interface
		appInterface = new ModuleServer(7666);
		
		// Device (Host) Listener Initialization
		deviceListener = new DeviceListener(appInterface, deviceService);
		deviceService.addListener(deviceListener);

		// Link Listener Initialization

		linkListener = new LinkListener(appInterface, linkService);
		linkService.addListener(linkListener);

		// Switch Listener Initialization
		switchListener = new SwitchListener(appInterface, switchService);
		switchService.addOFSwitchListener(switchListener);

		// Set Application interface Listeners
		appInterface.setListeners(deviceListener, linkListener, switchListener);
	}

	@Override
	public void startUp(FloodlightModuleContext context) {
		// Create Threat and start it
		serverThreat = new Thread(appInterface, "DXAT Floodlight module server");
		serverThreat.start();
	}

}
