package dxat.appserver.stat.pojos;

import dxat.appserver.flows.pojos.DeployedFlow;

public class AgregatedFlow implements Comparable<AgregatedFlow> {
	private String src;
	private String dst;
	private double traffic;

	public AgregatedFlow() {
		traffic = 0;
	}

	public void setForward(DeployedFlow deployedFlow) {
		src = deployedFlow.getSrcIpAddr();
		dst = deployedFlow.getDstIpAddr();
	}

	public void setBackward(DeployedFlow deployedFlow) {
		dst = deployedFlow.getSrcIpAddr();
		src = deployedFlow.getDstIpAddr();
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public String getDst() {
		return dst;
	}

	public void setDst(String dst) {
		this.dst = dst;
	}

	public double getTraffic() {
		return traffic;
	}

	public void setTraffic(double traffic) {
		this.traffic = traffic;
	}

	public void aggregate(double value) {
		traffic += value;
	}

	@Override
	public int compareTo(AgregatedFlow agregatedFlow) {
		int val;

		val =  src.compareTo(agregatedFlow.getSrc());
		if (val==0){
			return dst.compareTo(agregatedFlow.getDst());
		}
		return val;
	}
}
