package dxat.controller.module.pojos;

public class SwitchStat {

    private String SwitchId;
    private double packetCount;
    private double byteCount;
    private double flowCount;

    public String getSwitchId() {
        return SwitchId;
    }

    public void setSwitchId(String switchId) {
        SwitchId = switchId;
    }

    public double getPacketCount() {
        return packetCount;
    }

    public void setPacketCount(double packetCount) {
        this.packetCount = packetCount;
    }

    public double getByteCount() {
        return byteCount;
    }

    public void setByteCount(double byteCount) {
        this.byteCount = byteCount;
    }

    public double getFlowCount() {
        return flowCount;
    }

    public void setFlowCount(double flowCount) {
        this.flowCount = flowCount;
    }

}