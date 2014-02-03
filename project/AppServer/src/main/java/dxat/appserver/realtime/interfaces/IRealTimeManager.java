package dxat.appserver.realtime.interfaces;

public interface IRealTimeManager {
	public void subscribe(IRealTimeSubscriber subscriber);

	public void unsubscribe(IRealTimeSubscriber subscriber);

	public void broadcast(String message);
}
