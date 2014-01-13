package dxat.controller.module.pojos;

/**
 * Created by xavier on 1/10/14.
 */
public class Packet {
    private String portId;
    private byte networkTypeOfService;
    private byte networkProtocol;
    private String networkSource;
    private String networkDestination;
    private short transportSource;
    private short transportDestination;

    public byte getNetworkTypeOfService() {
        return networkTypeOfService;
    }

    public void setNetworkTypeOfService(byte networkTypeOfService) {
        this.networkTypeOfService = networkTypeOfService;
    }

    public byte getNetworkProtocol() {
        return networkProtocol;
    }

    public void setNetworkProtocol(byte networkProtocol) {
        this.networkProtocol = networkProtocol;
    }

    public String getNetworkSource() {
        return networkSource;
    }

    public void setNetworkSource(String networkSource) {
        this.networkSource = networkSource;
    }

    public String getNetworkDestination() {
        return networkDestination;
    }

    public void setNetworkDestination(String networkDestination) {
        this.networkDestination = networkDestination;
    }

    public short getTransportSource() {
        return transportSource;
    }

    public void setTransportSource(short transportSource) {
        this.transportSource = transportSource;
    }

    public short getTransportDestination() {
        return transportDestination;
    }

    public void setTransportDestination(short transportDestination) {
        this.transportDestination = transportDestination;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }
}
