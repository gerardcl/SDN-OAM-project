package dxat.appserver.manager.resources;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import dxat.appserver.manager.OrgTerminalManager;
import dxat.appserver.manager.exceptions.OrgNotFoundException;
import dxat.appserver.manager.exceptions.TerminalNotFoundException;
import dxat.appserver.manager.pojos.OrgTerminal;
import dxat.appserver.manager.pojos.OrgTerminalCollection;

@Path("/")
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
		List<OrgTerminal> orgTerminalList = new ArrayList<OrgTerminal>(orgTerminalManager.orgManager.getTerminals().values());
		OrgTerminalCollection orgTerminals = new OrgTerminalCollection();
		orgTerminals.setOrgTerminals(orgTerminalList);
		return orgTerminals;//(OrgFlowCollection) orgFlowManager.getAllFlows();
	}

	@GET
	@Path("/terminal/{orgId}/all")	
	@Produces(AppServerMediaType.ORG_TERMINAL_COLLECTION) 
	public OrgTerminalCollection getAllOrgTerminals(@PathParam("orgId") String orgId) {
		List<OrgTerminal> orgTerminalList = new ArrayList<OrgTerminal>(orgTerminalManager.orgManager.getOrg(orgId).getTerminals().values());
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

	//CREATE TERMINAL TO DB OR UPDATE IT-> ONLY POSSIBLE PARAMETERS FROM ORG TO TOPO...
	//ONLY ONE ORG PER TERMINAL AND THE OTHER WAY AROUND
	@POST
	@Path("/terminal/{orgId}/")
	@Consumes(AppServerMediaType.ORG_TERMINAL_COLLECTION)
	@Produces(AppServerMediaType.ORG_TERMINAL_COLLECTION)
	public OrgTerminal createTerminal(@PathParam("orgId") String orgId, OrgTerminal terminal){
		System.out.println("trying to assign terminal");
		OrgTerminal nterm = new OrgTerminal();
		if(!orgTerminalManager.orgManager.existOrg(orgId)) return null;
		if(!orgTerminalManager.orgManager.getOrg(orgId).getTerminals().containsKey(terminal.getIdentifier())){
			nterm = orgTerminalManager.tryCreateOrgTerminal(orgId, terminal);
			return nterm;
		}
		return null;	
	}

	@PUT
	@Path("/terminal/{orgId}/{terminalId}")
	@Consumes(AppServerMediaType.ORG_TERMINAL_COLLECTION)
	@Produces(AppServerMediaType.ORG_TERMINAL_COLLECTION)
	public OrgTerminal updateTerminal(@PathParam("orgId") String orgId, @PathParam("terminalId") String terminalId, OrgTerminal terminal){
		OrgTerminal nterm = new OrgTerminal();
		if(!orgTerminalManager.orgManager.existOrg(orgId)) return null;
		if(orgTerminalManager.orgManager.getOrg(orgId).getTerminals().containsKey(terminal.getIdentifier())){
			if(terminal.getIdentifier().equals(terminalId)){
				nterm.setDescription(terminal.getDescription());
				nterm.setHostName(terminal.getHostName());
				nterm.setPortApiID(terminal.getPortApiID());
				nterm.setActive(terminal.isActive());
				nterm.setAssigned(terminal.isAssigned()); //assigned IN FLOW

				nterm.setAssignedOrgId(orgId);
				nterm.setIdentifier(terminal.getIdentifier());

				nterm.setIfaceSpeed(terminal.getIfaceSpeed());
				nterm.setIpAddress(terminal.getIpAddress());
				nterm.setMac(terminal.getMac());

				try {
					orgTerminalManager.dbupdate.updateTerminal(nterm, orgId);
					orgTerminalManager.orgManager.getOrg(orgId).getTerminals().put(terminal.getIdentifier(), nterm);
					orgTerminalManager.putOrgTerminal(nterm);					
					return nterm;
				} catch (TerminalNotFoundException | OrgNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	//DELETE TERMINAL -> UNASSING ORG (drop from orgdb too)
	@DELETE
	@Path("/terminal/{orgId}/{terminalId}")
	public OrgTerminal deleteOrg(@PathParam("orgId") String orgId, @PathParam("terminalId") String terminalId){
		OrgTerminal ot = new OrgTerminal();
		if(!orgTerminalManager.orgManager.existOrg(orgId)) return null;
		if(!orgTerminalManager.orgManager.getOrg(orgId).getTerminals().containsKey(terminalId)) return null;
		else {
			ot = orgTerminalManager.orgManager.getOrg(orgId).getTerminals().get(terminalId);
			ot.setAssignedOrgId(null);
			try {
				orgTerminalManager.dbdelete.deleteTerminal(orgId, terminalId);
				orgTerminalManager.orgManager.getOrg(orgId).getTerminals().remove(terminalId);
				orgTerminalManager.orgManager.getTerminals().put(terminalId, ot);
				//TODO MAYBE CREATE WS TO BROADCAST UNASSIGNED TERMINAL
				return ot;
			} catch (TerminalNotFoundException | OrgNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ot;
	}
}
