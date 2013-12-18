package dxat.appserver.stat.pojos;

public class StatResponse {
	
	private String idObject;
	private String parameter;
	private long[] timeAxxis;
	private double[] valueAxxis;
	public String getIdObject() {
		return idObject;
	}
	public void setIdObject(String idObject) {
		this.idObject = idObject;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public long[] getTimeAxxis() {
		return timeAxxis;
	}
	public void setTimeAxxis(long[] ls) {
		this.timeAxxis = ls;
	}
	public double[] getValueAxxis() {
		return valueAxxis;
	}
	public void setValueAxxis(double[] valueAxxis) {
		this.valueAxxis = valueAxxis;
	}
	
	
}
