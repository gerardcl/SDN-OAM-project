package dxathacks.floodlightcontroller.pojos;

import java.util.ArrayList;
import java.util.List;

public class PortStatisticsCollection {
	private List<PortStatistics> portStatistics = null;

	public PortStatisticsCollection (){
		portStatistics = new ArrayList<PortStatistics>();
	}

	public List<PortStatistics> getPortStatistics() {
		return portStatistics;
	}

	public void setPortStatistics(List<PortStatistics> portStatistics) {
		this.portStatistics = portStatistics;
	}
	
	
	
}
