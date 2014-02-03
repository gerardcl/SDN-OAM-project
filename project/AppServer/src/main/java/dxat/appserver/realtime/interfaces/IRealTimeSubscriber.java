package dxat.appserver.realtime.interfaces;

public interface IRealTimeSubscriber {
	public String getSubscriberKey();

	public void sendEventMsg(String eventStr);
}
