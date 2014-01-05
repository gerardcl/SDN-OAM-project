package dxat.appserver.realtime.interfaces;

public interface IRealTimeManager {
	public void suscribe(IRealTimeSuscriber suscriber);

	public void unsuscribe(IRealTimeSuscriber suscriber);

	public void broadcast(String message);
}
