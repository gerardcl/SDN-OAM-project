package dxat.appserver.flows.resources;

import dxat.appserver.flows.FlowManager;
import dxat.appserver.flows.pojos.DeployedFlow;
import dxat.appserver.flows.pojos.DeployedFlowCollection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/flows")
public class FlowResource {
    @GET
    @Path("/")
    @Produces(MediaType.FLOWS_COLLECTION)
    public DeployedFlowCollection getEnabledFlows() {
        return FlowManager.getInstance().getFlows();
    }

    @GET
    @Path("/{flowId}")
    @Produces(MediaType.FLOW)
    public DeployedFlow getFlow(@PathParam("flowId") String flowId) {
        return FlowManager.getInstance().getFlow(flowId);
    }
}
