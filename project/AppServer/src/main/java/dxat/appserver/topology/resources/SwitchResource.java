package dxat.appserver.topology.resources;

import dxat.appserver.topology.SwitchManager;
import dxat.appserver.topology.pojos.Switch;
import dxat.appserver.topology.pojos.SwitchCollection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/switches")
public class SwitchResource {
    private SwitchManager switchManager = SwitchManager.getInstance();

    @GET
    @Path("/")
    @Produces(MediaType.SWITCHES_COLLECTION)
    public SwitchCollection getEnabledSwitches() {
        return switchManager.getSwitches();
    }

    @GET
    @Path("/{swId}")
    @Produces(MediaType.SWITCH)
    public Switch getSwitch(@PathParam("swId") String swId) {
        return switchManager.getSwitch(swId);
    }

}
