package dxat.appserver.manager.resources;

import javax.ws.rs.Path;

import dxat.appserver.manager.OrgTerminalManager;

@Path("/manager")
public class OrgTerminalResource {
//	- orgTerminalManager: OrgTerminalManager
//	+ getAllOrgTerminal(String):List<OrgTerminal>
//	+ addOrgTerminal(String, OrgTerminal):OrgTerminal
//	+ getOrgTerminal(String, String):AccessPoint
//	+ deleteOrgTerminal(String, String)
//	+ updateOrgTerminal(String, OrgTerminal)
	private OrgTerminalManager orgTerminalManager = OrgTerminalManager.getInstance();
	
}
