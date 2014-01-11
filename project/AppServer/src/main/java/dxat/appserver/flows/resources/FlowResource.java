package dxat.appserver.topology.resources;

import dxat.appserver.flows.FlowManager;
import dxat.appserver.flows.pojos.DeployedFlow;
import dxat.appserver.flows.pojos.DeployedFlowCollection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/")
public class FlowResource {
    @GET
    @Path("/flows/")
    @Produces(MediaType.FLOWS_COLLECTION)
    public DeployedFlowCollection getEnabledFlows() {
        return FlowManager.getInstance().getFlows();
    }

    @GET
    @Path("/flows/{flowId}")
    @Produces(MediaType.FLOW)
    public DeployedFlow getFlow(@PathParam("flowId") String flowId) {
        return FlowManager.getInstance().getFlow(flowId);
    }
}
