package dxat.appserver.topology.resources;

import dxat.appserver.topology.TerminalManager;
import dxat.appserver.topology.pojos.Terminal;
import dxat.appserver.topology.pojos.TerminalCollection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/terminals")
public class TerminalResource {
    private TerminalManager terminalManager = TerminalManager.getInstance();

    @GET
    @Path("/")
    @Produces(MediaType.TERMINALS_COLLECTION)
    public TerminalCollection getAllTerminals() {
        TerminalCollection terminalCollection = terminalManager.getTerminals();
        if (terminalCollection == null) {
            return new TerminalCollection();
        }
        return terminalCollection;
    }

    @GET
    @Path("/terminals/{terminalId}")
    @Produces(MediaType.TERMINAL)
    public Terminal getLink(@PathParam("terminalId") String terminalId) {
        return terminalManager.getTerminal(terminalId);
    }
}
