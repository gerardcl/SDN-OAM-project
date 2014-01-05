package dxat.appserver.realtime.interfaces;

public interface IRealTimeSuscriber {
	public String getSuscriberKey();

	public void sendEventMsg(String eventStr);
}
