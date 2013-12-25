package dxat.appserver.manager.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import dxat.appserver.manager.OrgTerminalManager;
import dxat.appserver.manager.pojos.OrgTerminal;
import dxat.appserver.manager.pojos.OrgTerminalCollection;

@Path("/manager")
public class OrgTerminalResource {
//	- orgTerminalManager: OrgTerminalManager
//	+ getAllOrgTerminal(String):List<OrgTerminal>
//	+ addOrgTerminal(String, OrgTerminal):OrgTerminal
//	+ getOrgTerminal(String, String):AccessPoint
//	+ deleteOrgTerminal(String, String)
//	+ updateOrgTerminal(String, OrgTerminal)
	private OrgTerminalManager orgTerminalManager = OrgTerminalManager.getInstance();
	
	//AQUESTA ES PASSARÀ AL ORGMANAGER QUE TINDRÀ TOTES LES ORGS ALLÀ
	//EL QUE FAREM AQUÍ SERÀ TRACTARO JA COM FLOWS D'UNA ORG
	//LA INTENCIÓ ÉS PASSAR AQUÍ SEMPRE LES ORGID
	//EL MOTIU ÉS PER LA DEFINICIÓ COEHERENT DE URLS I SEGURETAT
	@GET
	@Path("/terminal/all")	
	@Produces(AppServerMediaType.ORG_TERMINAL_COLLECTION) 
	public OrgTerminalCollection getAllTerminals() {
		List<OrgTerminal> orgTerminalList = new ArrayList<OrgTerminal>(orgTerminalManager.orgManager.getInstance().getTerminals().values());
		OrgTerminalCollection orgTerminals = new OrgTerminalCollection();
		orgTerminals.setOrgTerminals(orgTerminalList);
		return orgTerminals;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}
	
	@GET
	@Path("/terminal/{orgId}/all")	
	@Produces(AppServerMediaType.ORG_TERMINAL_COLLECTION) 
	public OrgTerminalCollection getAllOrgTerminals(@PathParam("orgId") String orgId) {
		List<OrgTerminal> orgTerminalList = new ArrayList<OrgTerminal>(orgTerminalManager.orgManager.getInstance().getOrg(orgId).getTerminals().values());
		OrgTerminalCollection orgTerminals = new OrgTerminalCollection();
		orgTerminals.setOrgTerminals(orgTerminalList);
		return orgTerminals;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}	
	
	@GET
	@Path("/terminal/{orgId}/{terminalId}") 
	@Produces(AppServerMediaType.ORG_TERMINAL_COLLECTION)
	public OrgTerminal getOrgTerminal(@PathParam("orgId") String orgId, @PathParam("terminalId") String terminalId) {
		return orgTerminalManager.orgManager.getOrg(orgId).getTerminals().get(terminalId);
	}	
	
}
