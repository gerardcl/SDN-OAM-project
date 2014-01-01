package dxat.appserver.realtime;

import java.util.Collection;
import java.util.HashMap;

import com.google.gson.Gson;

import dxat.appserver.config.LoadConfig;
import dxat.appserver.realtime.interfaces.IRealTimeManager;
import dxat.appserver.realtime.interfaces.IRealTimeSuscriber;
import dxat.appserver.realtime.interfaces.IServerRequests;
import dxat.appserver.realtime.pojos.Flow;
import dxat.appserver.realtime.pojos.ServerRequest;

public class RealTimeManager implements IRealTimeManager {
	private static RealTimeManager instance = null;
	private HashMap<String, IRealTimeSuscriber> suscribers = null;
	private RealTimeThread realTimeThread = null;

	private RealTimeManager() {
		super();
		suscribers = new HashMap<String, IRealTimeSuscriber>();
		realTimeThread = new RealTimeThread(
				LoadConfig.getProperty("controller.ip"),
				Integer.valueOf(LoadConfig.getProperty("controller.port")));
		Thread rtThread = new Thread(realTimeThread,
				"DXAT AppServer Real Time Module");
		rtThread.start();
	}

	public void pushFlow(Flow flow){
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setRequest(IServerRequests.PUSH_FLOW_REQUEST);
		serverRequest.setObject(new Gson().toJson(flow));
		realTimeThread.sendrequest(serverRequest);
	}
	
	public void deleteAllFlows(){
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setRequest(IServerRequests.DELETE_ALL_FLOWS_REQUEST);
		serverRequest.setObject("NONE");
		realTimeThread.sendrequest(serverRequest);
	}
	
	public void deleteFlow(Flow flow){
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setRequest(IServerRequests.DELETE_FLOW_REQUEST);
		serverRequest.setObject(new Gson().toJson(flow));
		realTimeThread.sendrequest(serverRequest);
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
