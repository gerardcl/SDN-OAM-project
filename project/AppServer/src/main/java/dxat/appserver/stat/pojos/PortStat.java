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
	private double receiveFrameErrors;
	private double receiveOverrunErrors;
	private double receiveCRCErrors;
	private double collisions;

	public String getPortId() {
		return portId;
	}

	public void setPortId(String portId) {
		this.portId = portId;
	}

	public double getReceivePackets() {
		return receivePackets;
	}

	public void setReceivePackets(double receivePackets) {
		this.receivePackets = receivePackets;
	}

	public double getTransmitPackets() {
		return transmitPackets;
	}

	public void setTransmitPackets(double transmitPackets) {
		this.transmitPackets = transmitPackets;
	}

	public double getReceiveBytes() {
		return receiveBytes;
	}

	public void setReceiveBytes(double receiveBytes) {
		this.receiveBytes = receiveBytes;
	}

	public double getTransmitBytes() {
		return transmitBytes;
	}

	public void setTransmitBytes(double transmitBytes) {
		this.transmitBytes = transmitBytes;
	}

	public double getReceiveDropped() {
		return receiveDropped;
	}

	public void setReceiveDropped(double receiveDropped) {
		this.receiveDropped = receiveDropped;
	}

	public double getTransmitDropped() {
		return transmitDropped;
	}

	public void setTransmitDropped(double transmitDropped) {
		this.transmitDropped = transmitDropped;
	}

	public double getReceiveErrors() {
		return receiveErrors;
	}

	public void setReceiveErrors(double receiveErrors) {
		this.receiveErrors = receiveErrors;
	}

	public double getTransmitErrors() {
		return transmitErrors;
	}

	public void setTransmitErrors(double transmitErrors) {
		this.transmitErrors = transmitErrors;
	}

	public double getReceiveFrameErrors() {
		return receiveFrameErrors;
	}

	public void setReceiveFrameErrors(double receiveFrameErrors) {
		this.receiveFrameErrors = receiveFrameErrors;
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