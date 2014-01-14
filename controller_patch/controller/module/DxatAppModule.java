package dxat.controller.module;

import dxat.controller.module.listeners.DeviceListener;
import dxat.controller.module.listeners.LinkListener;
import dxat.controller.module.listeners.PacketListener;
import dxat.controller.module.listeners.SwitchListener;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.staticflowentry.IStaticFlowEntryPusherService;

import java.util.Collection;
import java.util.Map;

/**
 * @author Xavier Arteaga <xavier.arteaga@estudiant.upc.edu>
 */
public class DxatAppModule implements IFloodlightModule {
	/**
	 * Instance of the singleton class
	 */
	private static DxatAppModule instance;

	/**
	 * Instance of the server thread
	 */
	private ModuleServerThread moduleServerThread = null;

	/**
	 * Service instance related with the discovery of new network devices
	 * (Terminals)
	 */
	private IDeviceService deviceService;

	/**
	 * Service instance related with the topology and link discovery.
	 */
	private ILinkDiscoveryService linkService;

	/**
	 * Service instance related with the management of the switches and OpenFlow
	 * messages.
	 */
	private IFloodlightProviderService switchService;

	/**
	 * Service instance related with the generation of routes and topology.
	 */
	private IRoutingService routingService;

	/**
	 * Service instance related with the flow push. It is needed for push,
	 * delete and monitor flows.
	 */
	private IStaticFlowEntryPusherService flowPusherService;

	/**
     *
     */
	private DeviceListener deviceListener;

	/**
     *
     */
	private LinkListener linkListener;

	/**
     *
     */
	private PacketListener packetListener;

	/**
     *
     */
	private SwitchListener switchListener;

	/**
	 * FlowPusherManager
	 */
	private FlowPusherManager flowPusherManager;

	/**
	 * Statistics Thread
	 */
	private StatisticsThread statisticsThread;

	/**
	 * This function return the instance of the DxatAppModule with all the
	 * required services.
	 * 
	 * @return The instance of the DxatAppModule
	 */
	public static DxatAppModule getInstance() {
		return instance;
	}

	public ModuleServerThread getModuleServerThread() {
		return moduleServerThread;
	}

	public IDeviceService getDeviceService() {
		return deviceService;
	}

	public ILinkDiscoveryService getLinkService() {
		return linkService;
	}

	public IFloodlightProviderService getSwitchService() {
		return switchService;
	}

	public IRoutingService getRoutingService() {
		return routingService;
	}

	public IStaticFlowEntryPusherService getFlowPusherService() {
		return flowPusherService;
	}

	public FlowPusherManager getFlowPusherManager() {
		return flowPusherManager;
	}

	public DeviceListener getDeviceListener() {
		return deviceListener;
	}

	public LinkListener getLinkListener() {
		return linkListener;
	}

	public PacketListener getPacketListener() {
		return packetListener;
	}

	public SwitchListener getSwitchListener() {
		return switchListener;
	}

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
		// Set instance
		instance = this;

		// Get Services
		deviceService = context.getServiceImpl(IDeviceService.class);
		linkService = context.getServiceImpl(ILinkDiscoveryService.class);
		switchService = context
				.getServiceImpl(IFloodlightProviderService.class);
		routingService = (IRoutingService) context
				.getServiceImpl(IRoutingService.class);
		flowPusherService = (IStaticFlowEntryPusherService) context
				.getServiceImpl(IStaticFlowEntryPusherService.class);

		// Get the instance and run the thread
		moduleServerThread = new ModuleServerThread(7666);

		// Create flow pusher manager
		flowPusherManager = new FlowPusherManager();

		// Device (Host) Listener Initialization
		deviceListener = new DeviceListener();
		deviceService.addListener(deviceListener);

		// Link Listener Initialization
		linkListener = new LinkListener(moduleServerThread, linkService);
		linkService.addListener(linkListener);

		// Switch Listener Initialization
		switchListener = new SwitchListener();
		switchService.addOFSwitchListener(switchListener);

		// Packet Listener Initialization
		// packetListener = new PacketListener(moduleServerThread);
		// switchService.addOFMessageListener(OFType.PACKET_IN ,
		// packetListener);

		// Create module of statistics
		statisticsThread = new StatisticsThread();

		Thread thread = new Thread(moduleServerThread, "DXAT Thread");
		thread.start();

		Thread threadStat = new Thread(statisticsThread,
				"Statistics Module Thread");
		threadStat.start();

	}

	@Override
	public void startUp(FloodlightModuleContext context) {

	}

}
