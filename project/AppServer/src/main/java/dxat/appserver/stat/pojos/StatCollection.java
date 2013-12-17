package dxat.appserver.stat.pojos;

import java.util.List;

public class StatCollection {

	private long timeStamp;
	private ControllerStat controllerStat;
	private List<PortStat> portStatCollection;
	private List<SwitchStat> switchStatCollection;

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public ControllerStat getControllerStat() {
		return controllerStat;
	}

	public void setControllerStat(ControllerStat controllerStat) {
		this.controllerStat = controllerStat;
	}

	public List<PortStat> getPortStatCollection() {
		return portStatCollection;
	}

	public void setPortStatCollection(List<PortStat> portStatCollection) {
		this.portStatCollection = portStatCollection;
	}

	public List<SwitchStat> getSwitchStatCollection() {
		return switchStatCollection;
	}

	public void setSwitchStatCollection(List<SwitchStat> switchStatCollection) {
		this.switchStatCollection = switchStatCollection;
	}

}