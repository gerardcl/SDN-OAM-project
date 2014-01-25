package dxat.controller.module.pojos;

public class Terminal {
    private String terminalId = "";
    private String mac = "";
    private String ipv4 = "";
    private String portAPId = "";
    private Boolean enabled = false;

    public String getTerminalId() {
        return terminalId;
    }

    public void setTerminalId(String terminalId) {
        this.terminalId = terminalId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIpv4() {
        return ipv4;
    }

    public void setIpv4(String ipv4) {
        this.ipv4 = ipv4;
    }

    public String getPortAPId() {
        return portAPId;
    }

    public void setPortAPId(String portAPId) {
        this.portAPId = portAPId;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
