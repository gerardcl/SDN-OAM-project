package dxat.appserver.realtime;

import com.google.gson.Gson;
import dxat.appserver.flows.pojos.Flow;
import dxat.appserver.realtime.interfaces.IRealTimeManager;
import dxat.appserver.realtime.interfaces.IRealTimeSubscriber;
import dxat.appserver.realtime.interfaces.IServerRequests;
import dxat.appserver.realtime.pojos.ServerRequest;

import java.util.Collection;
import java.util.HashMap;

public class RealTimeManager implements IRealTimeManager {
    private static RealTimeManager instance;
    private HashMap<String, IRealTimeSubscriber> subscribers;
    private RealTimeThread realTimeThread;

    private RealTimeManager() {
        super();
        subscribers = new HashMap<String, IRealTimeSubscriber>();
        realTimeThread = new RealTimeThread("147.83.118.254", 7666);
        Thread rtThread = new Thread(realTimeThread,
                "DXAT AppServer Real Time Module");
        rtThread.start();
    }

    public void pushFlow(Flow flow) {
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setRequest(IServerRequests.PUSH_FLOW_REQUEST);
        serverRequest.setObject(new Gson().toJson(flow));
        realTimeThread.sendRequest(serverRequest);
    }

    public void deleteAllFlows() {
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setRequest(IServerRequests.DELETE_ALL_FLOWS_REQUEST);
        serverRequest.setObject("NONE");
        realTimeThread.sendRequest(serverRequest);
    }

    public void deleteFlow(Flow flow) {
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setRequest(IServerRequests.DELETE_FLOW_REQUEST);
        serverRequest.setObject(new Gson().toJson(flow));
        realTimeThread.sendRequest(serverRequest);
    }

    public static RealTimeManager getInstance() {
        if (instance == null)
            instance = new RealTimeManager();
        return instance;
    }

    @Override
    public void subscribe(IRealTimeSubscriber subscriber) {
        subscribers.put(subscriber.getSubscriberKey(), subscriber);
    }

    @Override
    public void unsubscribe(IRealTimeSubscriber subscriber) {
        subscribers.remove(subscriber.getSubscriberKey());
    }

    @Override
    public void broadcast(String message) {
        Collection<IRealTimeSubscriber> subscriberCollection = subscribers.values();
        for (IRealTimeSubscriber subscriber : subscriberCollection) {
            subscriber.sendEventMsg(message);
        }
    }
}