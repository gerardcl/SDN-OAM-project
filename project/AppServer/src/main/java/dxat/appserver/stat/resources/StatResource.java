package dxat.appserver.stat.resources;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import dxat.appserver.stat.StatManager;
import dxat.appserver.stat.pojos.StatResponse;

@Path("/")
public class StatResource {
	
	private StatManager statMngr = StatManager.getInstance();
	@GET
	@Path("/{itemId}/{statParameter}/{typeOfStat}/{granularity}")
	@Produces(MediaType.STATS)
	public StatResponse getStat(@PathParam("itemId") String itemId,
			@PathParam("statParameter") String statParameter,
			@PathParam("typeOfStat") String typeOfStat,
			@PathParam("granularity") String granularity) throws IOException {
			
			return statMngr.getStat(itemId, statParameter, typeOfStat, granularity);
	}

}
