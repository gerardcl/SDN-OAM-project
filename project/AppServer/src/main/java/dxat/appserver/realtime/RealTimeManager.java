package dxat.appserver.realtime;

import java.util.Collection;
import java.util.HashMap;

import dxat.appserver.config.LoadConfig;
import dxat.appserver.realtime.interfaces.IRealTimeManager;
import dxat.appserver.realtime.interfaces.IRealTimeSuscriber;

public class RealTimeManager implements IRealTimeManager {
	private static RealTimeManager instance = null;
	private HashMap<String, IRealTimeSuscriber> suscribers = null;

	private RealTimeManager() {
		super();
		suscribers = new HashMap<String, IRealTimeSuscriber>();
		Thread rtThread = new Thread(new RealTimeThread(
				LoadConfig.getProperty("controller.ip"),
				Integer.valueOf(LoadConfig.getProperty("controller.port"))),
				"DXAT AppServer Real Time Module");
		rtThread.start();
	}

	public static RealTimeManager getInstance() {
		if (instance == null)
			instance = new RealTimeManager();
		return instance;
	}

	@Override
	public void suscribe(IRealTimeSuscriber suscriber) {
		suscribers.put(suscriber.getSuscriberKey(), suscriber);
	}

	@Override
	public void unsuscribe(IRealTimeSuscriber suscriber) {
		suscribers.remove(suscriber.getSuscriberKey());
	}

	@Override
	public void broadcast(String message) {
		Collection<IRealTimeSuscriber> suscriberCollection = suscribers
				.values();
		for (IRealTimeSuscriber suscriber : suscriberCollection) {
			suscriber.sendEventMsg(message);
		}
	}
}
