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
			org.setNIF("C-123456"+Integer.toString(i)+Integer.toString(j));
			org.setTelephone("689404"+Integer.toString(i)+Integer.toString(j));
			org.setBankAccount("2013-1234-5678-2345234"+Integer.toString(j)+Integer.toString(i));
			org.setOAM(i%5==0?true:false);
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
			for(i=0; i<10; i++){
				OrgFlow flow = new OrgFlow();
				String fid = "flouu";
				String fname = "namee";
				String orgTS = "src";
				String orgTD = "dst";
				fid += Integer.toString(j);
				fid += Integer.toString(i);
				fname += Integer.toString(j);
				fname += Integer.toString(i);
				orgTS += Integer.toString(j);
				orgTS += Integer.toString(i);
				orgTD += Integer.toString(j);
				orgTD += Integer.toString(i);
				flow.setIdentifier(fid);
				flow.setBandwidth(5000000);
				flow.setDstPort(5000);
				flow.setSrcPort(6000);
				flow.setProtocol("TCP");
				flow.setName(fname);
				flow.setQos(2000);
				flow.setActive(i%3==0?true:false);
				flow.setDstOTidentifier(orgTD);
				flow.setDstOTidentifier(orgTS);
				tempFlows.put(flow.identifier, flow);
				flows.put(flow.identifier, flow);
			}
			org.setFlows(tempFlows);
			
			tempUsers = new HashMap<String, OrgUser>();
			for(i=0; i<10; i++){
				OrgUser user = new OrgUser();
				String uid = "useer";
				String uname = "namee";
				uid += Integer.toString(j);
				uid += Integer.toString(i);
				uname += Integer.toString(j);
				uname += Integer.toString(i);
				user.setIdentifier(uid);
				user.setName(uname);
				user.setEmail(uid+"@"+org.getName()+".com");
				user.setTelephone(654321000+i*100+j*10+i+j);
				user.setPassword(org.getName()+"dxat"+Integer.toString(i));
				user.setAdmin(i%4==0?true:false);
				user.setActive(i%3==0?true:false);
				tempUsers.put(user.identifier, user);
				users.put(user.identifier, user);
			}
			org.setUsers(tempUsers);
			
			tempTerminals = new HashMap<String, OrgTerminal>();
			for(i=0; i<10; i++){
				OrgTerminal terminal = new OrgTerminal();
				String tid = "terminall";
				String tname = "namee";
				tid += Integer.toString(j);
				tid += Integer.toString(i);
				tname += Integer.toString(j);
				tname += Integer.toString(i);
				terminal.setIdentifier(tid);
				terminal.setHostName(tname);
				terminal.setDescription("terminal host for ");
				terminal.setDescription(terminal.getDescription() + org.getName());
				terminal.setIfaceSpeed(i*10000);
				terminal.setIpAddress("192.168."+Integer.toString(i)+"."+Integer.toString(j)+"0");
				terminal.setMac("DD:XX:AA:TT:"+Integer.toString(j)+"B:"+Integer.toString(i)+"C");
				terminal.setActive(i%2==0?true:false);
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
