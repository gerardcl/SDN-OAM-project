package dxat.controller.module.pojos;

import java.util.ArrayList;
import java.util.List;

public class DeployedFlowCollection {
    private List<DeployedFlow> flows = null;

    public DeployedFlowCollection() {
        flows = new ArrayList<DeployedFlow>();
    }

    public List<DeployedFlow> getFlows() {
        return flows;
    }

    public void setFlows(List<DeployedFlow> flows) {
        this.flows = flows;
    }

}
