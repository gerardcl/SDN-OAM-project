/**
 *    Copyright (c) 2008 The Board of Trustees of The Leland Stanford Junior
 *    University
 *
 *    Licensed under the Apache License, Version 2.0 (the "License"); you may
 *    not use this file except in compliance with the License. You may obtain
 *    a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 **/

package dxathacks.floodlightcontroller.pojos;

/**
 * Represents an ofp_port_stats structure
 * 
 * @author David Erickson (daviderickson@cs.stanford.edu)
 */
public class PortStatistics {
	private String switchId = "";
	private int portNumber = 0;
	private long receivePackets = 0;
	private long transmitPackets = 0;
	private long receiveBytes = 0;
	private long transmitBytes = 0;
	private long receiveDropped = 0;
	private long transmitDropped = 0;
	private long receiveErrors = 0;
	private long transmitErrors = 0;
	private long receiveFrameErrors = 0;
	private long receiveOverrunErrors = 0;
	private long receiveCRCErrors = 0;
	private long collisions = 0;

	public String getSwitchId() {
		return switchId;
	}

	public void setSwitchId(String switchId) {
		this.switchId = switchId;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

	public long getReceivePackets() {
		return receivePackets;
	}

	public void setReceivePackets(long receivePackets) {
		this.receivePackets = receivePackets;
	}

	public long getTransmitPackets() {
		return transmitPackets;
	}

	public void setTransmitPackets(long transmitPackets) {
		this.transmitPackets = transmitPackets;
	}

	public long getReceiveBytes() {
		return receiveBytes;
	}

	public void setReceiveBytes(long receiveBytes) {
		this.receiveBytes = receiveBytes;
	}

	public long getTransmitBytes() {
		return transmitBytes;
	}

	public void setTransmitBytes(long transmitBytes) {
		this.transmitBytes = transmitBytes;
	}

	public long getReceiveDropped() {
		return receiveDropped;
	}

	public void setReceiveDropped(long receiveDropped) {
		this.receiveDropped = receiveDropped;
	}

	public long getTransmitDropped() {
		return transmitDropped;
	}

	public void setTransmitDropped(long transmitDropped) {
		this.transmitDropped = transmitDropped;
	}

	public long getReceiveErrors() {
		return receiveErrors;
	}

	public void setReceiveErrors(long receiveErrors) {
		this.receiveErrors = receiveErrors;
	}

	public long getTransmitErrors() {
		return transmitErrors;
	}

	public void setTransmitErrors(long transmitErrors) {
		this.transmitErrors = transmitErrors;
	}

	public long getReceiveFrameErrors() {
		return receiveFrameErrors;
	}

	public void setReceiveFrameErrors(long receiveFrameErrors) {
		this.receiveFrameErrors = receiveFrameErrors;
	}

	public long getReceiveOverrunErrors() {
		return receiveOverrunErrors;
	}

	public void setReceiveOverrunErrors(long receiveOverrunErrors) {
		this.receiveOverrunErrors = receiveOverrunErrors;
	}

	public long getReceiveCRCErrors() {
		return receiveCRCErrors;
	}

	public void setReceiveCRCErrors(long receiveCRCErrors) {
		this.receiveCRCErrors = receiveCRCErrors;
	}

	public long getCollisions() {
		return collisions;
	}

	public void setCollisions(long collisions) {
		this.collisions = collisions;
	}

}
