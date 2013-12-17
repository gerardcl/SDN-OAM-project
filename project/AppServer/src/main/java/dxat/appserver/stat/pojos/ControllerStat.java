package dxat.appserver.stat.pojos;

public class ControllerStat {

	private double cpuAvg;
	private double memoryPct;

	public double getCpuAvg() {
		return cpuAvg;
	}

	public void setCpuAvg(double cpuAvg) {
		this.cpuAvg = cpuAvg;
	}

	public double getMemoryPct() {
		return memoryPct;
	}

	public void setMemoryPct(double memoryPct) {
		this.memoryPct = memoryPct;
	}

}