package dxat.appserver.manager;

import java.util.List;

import dxat.appserver.manager.pojos.OrgTerminal;

public class OrgTerminalManager {
	private static OrgTerminalManager instance;
	private OrgManager orgManager;
	private OrgTerminalManager(){
		//TODO initialize!
		
	}
	public static OrgTerminalManager getInstance(){
		if(instance == null)
			instance = new OrgTerminalManager();
		return instance;
	}
	public List<OrgTerminal> getAllOrgTerminals(String orgId){
		//TODO
		return null;
	}
	public void addOrgTerminal(String orgId, OrgTerminal terminal){
		//TODO
	}
	public OrgTerminal getOrgTerminal(String orgId, String terminalId){
		//TODO
		return null;
	}
	public void deleteOrgTerminal(String orgId, String terminalId){
		//TODO
	}
	public void updateOrgTerminal(String orgId, OrgTerminal terminal){
		//TODO
	}
}
