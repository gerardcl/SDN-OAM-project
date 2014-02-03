package dxat.appserver.manager;

import java.util.List;
import java.util.Map.Entry;

import dxat.appserver.manager.database.Create;
import dxat.appserver.manager.database.Delete;
import dxat.appserver.manager.database.Update;
import dxat.appserver.manager.exceptions.OrgNotFoundException;
import dxat.appserver.manager.exceptions.TerminalAlreadyExistsException;
import dxat.appserver.manager.exceptions.TerminalNotFoundException;
import dxat.appserver.manager.pojos.OrgTerminal;
import dxat.appserver.topology.pojos.Terminal;
import dxat.appserver.topology.pojos.TerminalCollection;

public class OrgTerminalManager {
	private static OrgTerminalManager instance;
	public OrgManager orgManager;
	private Create dbcreate;
	public Update dbupdate;
	public Delete dbdelete;

	private OrgTerminalManager(){
		//TODO initialize!
		dbupdate = new Update();
		dbcreate = new Create();
		dbdelete = new Delete();
		orgManager = OrgManager.getInstance();

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

	public OrgTerminal getOrgTerminal(String orgId, String terminalId){
		//TODO
		return null;
	}
	public void deleteOrgTerminal(String orgId, String terminalId){
		//TODO
	}

	//ADD/PUT HM 
	public OrgTerminal putOrgTerminal(OrgTerminal terminal){
		orgManager.getTerminals().put(terminal.getIdentifier(), terminal);
		return terminal;
	}

	//ALSO CALLED FROM TOPO RESOURCE PUT
	//CREATE TERMINAL TO DB IF ASSIGNING ORG FROM MANAGER
	//UPDATE  HM AND CREATE(only if assigned orgId) DB
	public OrgTerminal tryCreateOrgTerminal(String orgId, OrgTerminal terminal){
		OrgTerminal oterminal = new OrgTerminal();
		oterminal.setActive(true);
		oterminal.setIdentifier(Integer.toString(terminal.getMac().hashCode()));
		oterminal.setIpAddress(terminal.getIpAddress());
		oterminal.setPortApiID(terminal.getPortApiID());
		oterminal.setAssigned(true);
		oterminal.setAssignedOrgId(orgId);
		oterminal.setDescription(terminal.getDescription());
		oterminal.setHostName(terminal.getHostName());
		oterminal.setIfaceSpeed(terminal.getIfaceSpeed());
		oterminal.setMac(terminal.getMac());
		
		if(orgId!=null){
			try {
				orgManager.getTerminals().put(oterminal.getIdentifier(), oterminal);
				orgManager.getOrg(orgId).getTerminals().put(oterminal.getIdentifier(), oterminal);
				dbcreate.createTerminal(oterminal, orgId);
				return oterminal;
			} catch (TerminalAlreadyExistsException | OrgNotFoundException e) {
				e.printStackTrace();
			}
		}
//		else{
//
//			//TODO CHECK IF NEED TO EXIST THIS ELSE OPTION (should never enter here)
//
//			try {
//				dbupdate.updateTerminal(terminal, terminal.getAssignedOrgId());
//				return terminal;
//			} catch (TerminalNotFoundException | OrgNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
		return null;
	}

	//UPDATE ORG TERMINALS FROM TOPO TERMINALS
	public void updateOrgTerminals(TerminalCollection allTTerminals) {
		boolean updated;
		boolean found;
		String idFound = null;
		for(Terminal tterminal : allTTerminals.getTerminals()){
			OrgTerminal newterminal = new OrgTerminal();
			OrgTerminal oterminal = new OrgTerminal();

			//TODO CHECK NUMBER OF TTERMINALS
			System.out.println("SIZE OF T.TERMINALS COLLECTION : "+allTTerminals.getTerminals().size());
			
			if(!tterminal.getPortAPId().equals("00:00:00:00:00:00:00:00:0") && !tterminal.getIpv4().equals("0.0.0.0")){
				updated = false;
				found = false;
				if(!orgManager.getTerminals().isEmpty()){
					for(Entry<String, OrgTerminal> allOTerminals : orgManager.getTerminals().entrySet()){
						oterminal = allOTerminals.getValue();

						if(oterminal.getMac().equals(tterminal.getMac())){
							found = true;
							idFound = Integer.toString(tterminal.getTerminalId().hashCode());
							break;
						}
					}				
					if(found){
						found = false;
						if(!orgManager.getTerminals().get(idFound).getIdentifier().equals(Integer.toString(tterminal.getTerminalId().hashCode()))){
							orgManager.getTerminals().get(idFound).setIdentifier(Integer.toString(tterminal.getTerminalId().hashCode()));
							System.out.println("[TERMINAL TOPOLOGY TO MANAGER] INCONGRUENCE! (but... could be the first check of this terminal! ;) )");
							System.out.println("------------> matching of mac but not of ids -> setting id O.Term. from topo");
						}
						//update HM and DB with updated oterminal
						//FALTA XEQUEJAR SI TÉ ASSIGNED ORG AQUÍ!!!
						if(orgManager.getTerminals().get(idFound).getAssignedOrgId()==null){
							System.out.println("UPDATING TERMINAL HM: "+Integer.toString(tterminal.getTerminalId().hashCode()));

							//ONLY ADD HM
							//update oterminal
							updated = true;
							oterminal.setActive(true);
							oterminal.setIdentifier(Integer.toString(tterminal.getMac().hashCode()));
							oterminal.setIpAddress(tterminal.getIpv4());
							oterminal.setPortApiID(tterminal.getPortAPId());
							oterminal.setAssigned(true);
							oterminal.setAssignedOrgId(null);
							oterminal.setDescription(orgManager.getTerminals().get(idFound).getDescription());
							oterminal.setHostName(orgManager.getTerminals().get(idFound).getHostName());
							oterminal.setIfaceSpeed(orgManager.getTerminals().get(idFound).getIfaceSpeed());
							oterminal.setMac(tterminal.getMac());
//							orgManager.getTerminals().get(idFound).setActive(true);
//							orgManager.getTerminals().get(idFound).setIdentifier(tterminal.getTerminalId());
//							orgManager.getTerminals().get(idFound).setIpAddress(tterminal.getIpv4());
//							orgManager.getTerminals().get(idFound).setPortApiID(tterminal.getPortAPId());
							putOrgTerminal(oterminal);
						}else{
							System.out.println("UPDATING ORG TERMINAL HM AND DB: "+Integer.toString(tterminal.getTerminalId().hashCode()));
							//update oterminal
							//CHECK IF TERMINAL IN ORG 
							updated = true;
							oterminal.setActive(true);
							oterminal.setIdentifier(Integer.toString(tterminal.getMac().hashCode()));
							oterminal.setIpAddress(tterminal.getIpv4());
							oterminal.setPortApiID(tterminal.getPortAPId());
							oterminal.setAssigned(true);
							oterminal.setAssignedOrgId(orgManager.getTerminals().get(idFound).getAssignedOrgId());
							oterminal.setDescription(orgManager.getTerminals().get(idFound).getDescription());
							oterminal.setHostName(orgManager.getTerminals().get(idFound).getHostName());
							oterminal.setIfaceSpeed(orgManager.getTerminals().get(idFound).getIfaceSpeed());
							oterminal.setMac(tterminal.getMac());
//							orgManager.getTerminals().get(idFound).setActive(true);
//							orgManager.getTerminals().get(idFound).setIdentifier(tterminal.getTerminalId());
//							orgManager.getTerminals().get(idFound).setIpAddress(tterminal.getIpv4());
//							orgManager.getTerminals().get(idFound).setPortApiID(tterminal.getPortAPId());
							try {
								dbupdate.updateTerminal(oterminal, oterminal.getAssignedOrgId());
								orgManager.getOrg(oterminal.getAssignedOrgId()).getTerminals().put(oterminal.getIdentifier(), oterminal);
								orgManager.getTerminals().put(oterminal.getIdentifier(), oterminal);
							} catch (TerminalNotFoundException
									| OrgNotFoundException e) {
								e.printStackTrace();
							}
						}
					}
					if(!updated){
						System.out.println("NEW TERMINAL: "+tterminal.getTerminalId());
						updated = false;
						//PUSH OTERMINAL - orgId = null - setActive -
						newterminal.setActive(true);
						newterminal.setAssigned(false);
						newterminal.setDescription("new terminal from topology");
						newterminal.setIdentifier(Integer.toString(tterminal.getMac().hashCode()));
						newterminal.setPortApiID(tterminal.getPortAPId());
						newterminal.setHostName("not set");
						newterminal.setIfaceSpeed(0);
						newterminal.setIpAddress(tterminal.getIpv4());
						newterminal.setMac(tterminal.getMac());
						newterminal.setAssignedOrgId(null);
						putOrgTerminal(newterminal);
					}
				}else{
					//PUSH FIRST OTERMINAL - orgId = null - setActive -
					System.out.println("FIRST TERMINAL: "+tterminal.getTerminalId());
					newterminal.setActive(true);
					newterminal.setAssigned(false);
					newterminal.setDescription("new terminal from topology");
					newterminal.setIdentifier(Integer.toString(tterminal.getMac().hashCode()));
					newterminal.setPortApiID(tterminal.getPortAPId());
					newterminal.setHostName("not set");
					newterminal.setIfaceSpeed(0);
					newterminal.setIpAddress(tterminal.getIpv4());
					newterminal.setMac(tterminal.getMac());
					newterminal.setAssignedOrgId(null);
					putOrgTerminal(newterminal);
				}
			}
		}
	}
}
