package dxat.appserver.stat.pojos;

public class PortStat {
	
	private String portId;
	private double receivePackets;
	private double transmitPackets;
	private double receiveBytes;
	private double transmitBytes;
	private double receiveDropped;
	private double transmitDropped;
	private double receiveErrors;
	private double transmitErrors;
	private double receiveFrameErros;
	private double receiveOverrunErrors;
	private double receiveCRCErrors;
	private double collisions;
	public String getPortId() {
		return portId;
	}
	public void setPortId(String portId) {
		this.portId = portId;
	}
	public double getReceiveBytes() {
		return receiveBytes;
	}
	public void setReceiveBytes(double receiveBytes) {
		this.receiveBytes = receiveBytes;
	}
	public double getReceiveDropped() {
		return receiveDropped;
	}
	public void setReceiveDropped(double receiveDropped) {
		this.receiveDropped = receiveDropped;
	}
	public double getReceiveErrors() {
		return receiveErrors;
	}
	public void setReceiveErrors(double receiveErrors) {
		this.receiveErrors = receiveErrors;
	}
	public double getReceiveFrameErros() {
		return receiveFrameErros;
	}
	public void setReceiveFrameErros(double receiveFrameErros) {
		this.receiveFrameErros = receiveFrameErros;
	}
	public double getReceiveOverrunErrors() {
		return receiveOverrunErrors;
	}
	public void setReceiveOverrunErrors(double receiveOverrunErrors) {
		this.receiveOverrunErrors = receiveOverrunErrors;
	}
	public double getReceiveCRCErrors() {
		return receiveCRCErrors;
	}
	public void setReceiveCRCErrors(double receiveCRCErrors) {
		this.receiveCRCErrors = receiveCRCErrors;
	}
	public double getCollisions() {
		return collisions;
	}
	public void setCollisions(double collisions) {
		this.collisions = collisions;
	}
	
	
}
