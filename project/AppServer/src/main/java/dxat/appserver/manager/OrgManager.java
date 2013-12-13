package dxat.appserver.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dxat.appserver.manager.pojos.OrgCollection;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgTerminal;
import dxat.appserver.manager.pojos.Org;
import dxat.appserver.manager.pojos.OrgUser;
import dxat.appserver.manager.pojos.TOrg;
import dxat.appserver.manager.pojos.TOrgCollection;

public class OrgManager {
	private static OrgManager instance;
	private HashMap<String, Org> orgs;
	private HashMap<String, TOrg> torgs; //same orgs ids
	private HashMap<String, OrgFlow> flows;
	private HashMap<String, OrgUser> users;
	private HashMap<String, OrgTerminal> terminals;

	private OrgManager(){
		orgs = new HashMap<String, Org>();
		torgs = new HashMap<String, TOrg>();
		flows = new HashMap<String, OrgFlow>();
		users = new HashMap<String, OrgUser>();
		terminals = new HashMap<String, OrgTerminal>();
		
		/*some test init*/
		int i = 0;
		int j = 0;
		for(j = 1; j < 6; j++){
			Org org = new Org();
			String id = "orgId";
			String name = "orgName";
			id += Integer.toString(j);
			name += Integer.toString(j);
			org.setIdentifier(id);
			org.setName(name);
			org.setNIF("192.168.1.10");
			org.setTelephone("689400423");
			org.setBankAccount("2342343-23452345-23452345-23452345");
			org.setOAM(true);
			HashMap<String, OrgFlow> tempFlows;
			HashMap<String, OrgUser> tempUsers;
			HashMap<String, OrgTerminal> tempTerminals;			
			TOrg torg = new TOrg();
			
			torg.setBankAccount(org.getBankAccount());
			torg.setIdentifier(org.getIdentifier());
			torg.setName(org.getName());
			torg.setNIF(org.getNIF());
			torg.setOAM(org.isOAM());
			torg.setTelephone(org.getTelephone());
			org.setTorg(torg);

			tempFlows = new HashMap<String, OrgFlow>();
			for(i=1; i<11; i++){
				OrgFlow flow = new OrgFlow();
				String fid = "flouu";
				String fname = "namee";
				String orgS = "src";
				String orgD = "dst";
				fid += Integer.toString(j);
				fid += Integer.toString(i);
				fname += Integer.toString(j);
				fname += Integer.toString(i);
				orgS += Integer.toString(j);
				orgS += Integer.toString(i);
				orgD += Integer.toString(j);
				orgD += Integer.toString(i);
				flow.setIdentifier(fid);
				flow.setBandwidth(5000000);
				flow.setDstPort(5000);
				flow.setSrcPort(6000);
				flow.setProtocol("TCP");
				flow.setName(fname);
				flow.setQos(2000);
				flow.setActive(true);
				flow.setDstOTidentifier(orgD);
				flow.setDstOTidentifier(orgS);
				tempFlows.put(flow.identifier, flow);
				flows.put(flow.identifier, flow);
			}
			org.setFlows(tempFlows);
			
			tempUsers = new HashMap<String, OrgUser>();
			for(i=1; i<11; i++){
				OrgUser user = new OrgUser();
				String uid = "useer";
				String uname = "namee";

				uid += Integer.toString(j);
				uid += Integer.toString(i);
				uname += Integer.toString(j);
				uname += Integer.toString(i);
				user.setIdentifier(uid);
//				user.setBandwidth(5000000);
//				user.setDstPort(5000);
//				user.setSrcPort(6000);
//				user.setProtocol("TCP");
				user.setName(uname);
//				user.setQos(2000);
				user.setActive(true);
//				user.setDstOTidentifier(orgD);
//				user.setDstOTidentifier(orgS);
				tempUsers.put(user.identifier, user);
				users.put(user.identifier, user);
			}
			org.setUsers(tempUsers);
			
			tempTerminals = new HashMap<String, OrgTerminal>();
			for(i=1; i<11; i++){
				OrgTerminal terminal = new OrgTerminal();
				String tid = "terminall";
				String tname = "namee";
				String orgS = "src";
				String orgD = "dst";
				tid += Integer.toString(j);
				tid += Integer.toString(i);
				tname += Integer.toString(j);
				tname += Integer.toString(i);
//				orgS += Integer.toString(i);
//				terminal.setBandwidth(5000000);
//				terminal.setDstPort(5000);
//				terminal.setSrcPort(6000);
//				terminal.setProtocol("TCP");
				terminal.setHostName(tname);
//				terminal.setQos(2000);
				terminal.setActive(true);
//				terminal.setDstOTidentifier(orgD);
//				terminal.setDstOTidentifier(orgS);
				tempTerminals.put(terminal.identifier, terminal);
				terminals.put(terminal.identifier, terminal);
			}
			org.setTerminals(tempTerminals);
			
			orgs.put(org.getIdentifier(), org);
			torgs.put(torg.getIdentifier(), torg);
		}
	}

	public HashMap<String, OrgFlow> getFlows() {
		return flows;
	}

	public void setFlows(HashMap<String, OrgFlow> flows) {
		this.flows = flows;
	}

	public HashMap<String, OrgUser> getUsers() {
		return users;
	}

	public void setUsers(HashMap<String, OrgUser> users) {
		this.users = users;
	}

	public HashMap<String, OrgTerminal> getTerminals() {
		return terminals;
	}

	public void setTerminals(HashMap<String, OrgTerminal> terminals) {
		this.terminals = terminals;
	}

	public HashMap<String, TOrg> getTorgs() {
		return torgs;
	}

	public void setTorgs(HashMap<String, TOrg> torgs) {
		this.torgs = torgs;
	}

	public static OrgManager getInstance(){
		if(instance == null)
			instance = new OrgManager();
		return instance;
	}
	public OrgCollection getAllOrgs(){
		List<Org> orgList = new ArrayList<Org>(orgs.values());
		OrgCollection orgs = new OrgCollection();
		orgs.setOrgCollection(orgList);
		
		//ES POT PASSAR MODELS ENTRE HTMLS?¿
		
		
		return orgs;
	}

	public TOrgCollection getAllTOrgs(){
		List<TOrg> torgList = new ArrayList<TOrg>(torgs.values());
		TOrgCollection orgs = new TOrgCollection();
		orgs.setTorgs(torgList);
		
		//AQUÍ IMPLEMENTEM LA GESTIÓ DE TORGS DINS ORGMANAGER, 
		//AIXÍ NO CAL FER
		//UN REST PER A TORG
		
		
		return orgs;
	}
	
	public Org getOrg(String id){
		return orgs.get(id);
	}
	public void addOrg(TOrg id){
		//TODO
	}
	public TOrg delete(String id){
		//TODO
		return null;
	}
	public Org updateOrg(Org org){
		//TODO
		return org;
	}
	public boolean existOrg(OrgTerminal orgT){
		//TODO
		return false;
	}
	public List<Org> getOrgs(){
		//TODO
		return null;
	}
}
