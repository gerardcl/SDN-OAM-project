package dxat.appserver.stat.pojos;

public class StatRequest {
	
	private String idIbject;
	private String parameter;
	private String type;
	private String granularity;
	public String getIdIbject() {
		return idIbject;
	}
	public void setIdIbject(String idIbject) {
		this.idIbject = idIbject;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getGranularity() {
		return granularity;
	}
	public void setGranularity(String granularity) {
		this.granularity = granularity;
	}
	
	

}
