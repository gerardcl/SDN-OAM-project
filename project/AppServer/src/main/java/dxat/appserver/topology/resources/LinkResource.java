package dxat.appserver.topology.resources;

import dxat.appserver.flows.FlowManager;
import dxat.appserver.topology.LinkManager;
import dxat.appserver.topology.SwitchManager;
import dxat.appserver.topology.TerminalManager;
import dxat.appserver.topology.pojos.Link;
import dxat.appserver.topology.pojos.LinkCollection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/links")
public class LinkResource {
    private SwitchManager switchManager = SwitchManager.getInstance();
    private LinkManager linkManager = LinkManager.getInstance();
    private TerminalManager terminalManager = TerminalManager.getInstance();
    private FlowManager flowManager = FlowManager.getInstance();

    @GET
    @Path("/")
    @Produces(MediaType.LINKS_COLLECTION)
    public LinkCollection getEnabledLinks() {
        return linkManager.getLinks();
    }

    @GET
    @Path("/links/{srcPortId}/{dstPortId}")
    @Produces(MediaType.LINK)
    public Link getLink(@PathParam("srcPortId") String srcPortId,
                        @PathParam("dstPortId") String dstPortId) {
        return linkManager.getLink(srcPortId, dstPortId);
    }
}
