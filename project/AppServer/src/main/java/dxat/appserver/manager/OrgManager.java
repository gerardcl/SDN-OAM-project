package dxat.appserver.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import dxat.appserver.manager.database.Create;
import dxat.appserver.manager.database.Delete;
import dxat.appserver.manager.database.Read;
import dxat.appserver.manager.database.Update;
import dxat.appserver.manager.exceptions.FlowAlreadyExistsException;
import dxat.appserver.manager.exceptions.OrgAlreadyExistsException;
import dxat.appserver.manager.exceptions.OrgNotFoundException;
import dxat.appserver.manager.exceptions.TerminalAlreadyExistsException;
import dxat.appserver.manager.exceptions.UserAlreadyExistsException;
import dxat.appserver.manager.pojos.OrgCollection;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgSession;
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
	private HashMap<String, OrgSession> sessions;
	private HashMap<String, OrgTerminal> terminals;
	private Create dbcreate;
	private Read dbread;
	private Update dbupdate;
	private Delete dbdelete;
	private boolean dbExists;

	private OrgManager(){
		dbExists = true;
		initOrgManager();
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
		return orgs;
	}

	public TOrgCollection getAllTOrgs(){
		//AQUÍ IMPLEMENTEM LA GESTIÓ DE TORGS DINS ORGMANAGER, 
		//AIXÍ NO CAL FER
		//UN REST PER A TORG
		List<TOrg> torgList = new ArrayList<TOrg>(torgs.values());
		TOrgCollection orgs = new TOrgCollection();
		orgs.setTorgs(torgList);
		return orgs;
	}
	
	public Org getOrg(String id){
		return orgs.get(id);
	}
	
	public TOrg getTOrg(String id){
		//Quering Org from database
		return null;
	}
	public TOrg addTOrg(TOrg torg){
		dbcreate = new Create();
		TOrg newtorg = new TOrg();
		newtorg.setBankAccount(torg.getBankAccount());
		newtorg.setName(torg.getName());
		newtorg.setNIF(torg.getNIF());
		newtorg.setOAM(torg.isOAM());
		newtorg.setTelephone(torg.getTelephone());
		newtorg.setIdentifier(Integer.toString(Math.abs((int) (torg.getName().hashCode()*Math.random()*-10))));
		try {
			dbcreate.createOrg(newtorg);
			torgs.put(newtorg.getIdentifier(), newtorg);
			addOrg(newtorg);
			System.out.println("new org created with id "+newtorg.getIdentifier()+" and name "+newtorg.getName());
			return newtorg;
		} catch (OrgAlreadyExistsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public void addOrg(TOrg torg){
		Org neworg = new Org();
		neworg.setBankAccount(torg.getBankAccount());
		neworg.setName(torg.getName());
		neworg.setIdentifier(torg.getIdentifier());
		neworg.setNIF(torg.getNIF());
		neworg.setOAM(torg.isOAM());
		neworg.setTelephone(torg.getTelephone());
		neworg.setTorg(torg);
		HashMap<String, OrgFlow> oflows = new HashMap<String, OrgFlow>();
		HashMap<String, OrgUser> ousers = new HashMap<String, OrgUser>();
		HashMap<String, OrgTerminal> oterminals = new HashMap<String, OrgTerminal>();
		neworg.setFlows(oflows);
		neworg.setUsers(ousers);
		neworg.setTerminals(oterminals);
		orgs.put(neworg.getIdentifier(), neworg);
	}
	
	public TOrg updateTOrg(String orgId, TOrg torg){
		dbupdate = new Update();
		TOrg utorg = torgs.get(orgId);
		utorg.setIdentifier(orgId);
		utorg.setBankAccount(torg.getBankAccount());
		utorg.setName(torg.getName());
		utorg.setNIF(torg.getNIF());
		utorg.setOAM(torg.isOAM());
		utorg.setTelephone(torg.getTelephone());
		try {
			dbupdate.updateOrg(utorg);
			torgs.put(orgId, utorg);
			updateOrg(utorg);
			return utorg;
		} catch (OrgNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void updateOrg(TOrg torg){
		Org uOrg = orgs.get(torg.getIdentifier());
		uOrg.setBankAccount(torg.getBankAccount());
		uOrg.setName(torg.getName());
		uOrg.setNIF(torg.getNIF());
		uOrg.setOAM(torg.isOAM());
		uOrg.setTelephone(torg.getTelephone());
		uOrg.setTorg(torg);
		uOrg.setFlows(uOrg.getFlows());
		uOrg.setTerminals(uOrg.getTerminals());
		uOrg.setUsers(uOrg.getUsers());
		orgs.put(torg.getIdentifier(), uOrg);
	}
	
	public String deleteOrg(String orgId){
		//Deleting TOrg from database
		dbdelete = new Delete();
		try {
			dbdelete.deleteOrg(orgId);
			torgs.remove(orgId);
			deleteUsers(orgId);
			deleteTerminals(orgId);
			deleteFlows(orgId);
			orgs.remove(orgId);
			return orgId;
		} catch (OrgNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void deleteUsers(String orgId){
		Org selectedOrg = orgs.get(orgId);
		ArrayList<String> ids = new ArrayList<>();
		for (Entry<String, OrgUser> entry : users.entrySet()) {
			OrgUser user = entry.getValue();
			if(selectedOrg.getUsers().containsKey(user.getIdentifier())){
				ids.add(user.getIdentifier());
			}
		}
		for(String id: ids){
			users.remove(id);
		}
	}
	public void deleteTerminals(String orgId){
		Org selectedOrg = orgs.get(orgId);
		ArrayList<String> ids = new ArrayList<>();
		for (Entry<String, OrgTerminal> entry : terminals.entrySet()) {
			OrgTerminal terminal = entry.getValue();
			if(selectedOrg.getTerminals().containsKey(terminal.getIdentifier())){
				ids.add(terminal.getIdentifier());
			}
		}
		for(String id: ids){
			terminals.remove(id);
		}
	}
	public void deleteFlows(String orgId){
		Org selectedOrg = orgs.get(orgId);
		ArrayList<String> ids = new ArrayList<>();
		for (Entry<String, OrgFlow> entry : flows.entrySet()) {
			OrgFlow flow = entry.getValue();
			if(selectedOrg.getFlows().containsKey(flow.getIdentifier())){
				ids.add(flow.getIdentifier());
			}
		}
		for(String id: ids){
			flows.remove(id);
		}
	}
	
	public boolean existOrg(TOrg org){
		for (Entry<String, TOrg> entry1 : torgs.entrySet()) {
			TOrg torg = entry1.getValue();
			if(org.getName().equals(torg.getName())) return true;
		}
		return false;
	}

	public boolean existOrg(String orgId){
		if(orgs.containsKey(orgId)) return true;
		return false;
	}
	
	private String getOrgIdFromUserId(String userId){
		String orgId = null;
		for (Entry<String, Org> entry1 : orgs.entrySet()) {
			orgId = entry1.getKey();
			Org org = entry1.getValue();
			for (Entry<String, OrgUser> entry2 : org.getUsers().entrySet()) {
				if(entry2.getValue().getIdentifier().equals(userId)){
					return orgId;
				}
			}
		}
		return orgId;
	}
	
	private String getOrgNameFromUserId(String userId){
		for (Entry<String, Org> entry1 : orgs.entrySet()) {
			Org org = entry1.getValue();
			for (Entry<String, OrgUser> entry2 : org.getUsers().entrySet()) {
				if(entry2.getValue().getIdentifier().equals(userId)){
					return org.getName();
				}
			}
		}
		return null;
	}
	
	public OrgSession existUser(String username){
		//TO CHECK IT BY GOING THROUGH EACH ORG?¿ NEXT STEPS...
		for (Entry<String, OrgUser> entry : users.entrySet()) {
		    Object value = entry.getValue();
		    if(((OrgUser) value).getName().equals(username)) {
		    	OrgSession session = new OrgSession();
		    	session.setUserId(((OrgUser) value).getIdentifier());
		    	session.setSession(UUID.randomUUID().toString());
		    	session.setOrgName(getOrgNameFromUserId(session.getUserId()));
		    	session.setUserName(((OrgUser) value).getName());
		    	session.setToken("notoken");
		    	//CHECK IF ORGID == NULL THEN...
		    	session.setOrgId(getOrgIdFromUserId(session.getUserId()));
		    	sessions.put(((OrgUser) value).getName(), session);
		    	return ((OrgSession) session);
		    }
		}
		return null;
	}
	public List<Org> getOrgs(){
		//TODO
		return null;
	}
	
	//INIT MANAGER
	private void initOrgManager(){
		if(dbExists){	//CREATE ORGS FROM EXISTING DB
			dbread = new Read();
			orgs = dbread.getDBOrgs();
			torgs = dbread.getDBTOrgs();
			flows = dbread.getDBFlows();
			users = dbread.getDBUsers();
			terminals = dbread.getDBTerminals();
			sessions = new HashMap<String, OrgSession>();
			
		}else{ 			//CREATE DB FOR THE FIRST TIME 
			dbcreate = new Create();
			orgs = new HashMap<String, Org>();
			torgs = new HashMap<String, TOrg>();
			flows = new HashMap<String, OrgFlow>();
			users = new HashMap<String, OrgUser>();
			terminals = new HashMap<String, OrgTerminal>();
			sessions = new HashMap<String, OrgSession>();
			
			/*orgs test fake init*/
			int maxorgs = 10;
			int i = 0;
			int j = 0;
			for(j = 1; j < maxorgs; j++){
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
				org.setOAM(j%(maxorgs-1)==0?true:false);
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
				try {
					dbcreate.createOrg(torg);
				} catch (OrgAlreadyExistsException e) {
					e.printStackTrace();
				}
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
					flow.setSrcOTidentifier(orgTS);
					tempFlows.put(flow.getIdentifier(), flow);
					System.out.println("new flow with id = "+flow.getIdentifier());
					flows.put(flow.getIdentifier(), flow);
					try {
						dbcreate.createFlow(flow, org.getIdentifier());
					} catch (FlowAlreadyExistsException e) {
						e.printStackTrace();
					} catch (OrgNotFoundException e) {
						e.printStackTrace();
					}
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
					user.setPassword(Integer.toString(j)+"dxat"+Integer.toString(i));
					user.setAdmin(j%(maxorgs-1)==0?true:false);
					user.setActive(i%3==0?true:false);
					tempUsers.put(user.getIdentifier(), user);
					System.out.println("new user with id = "+user.getIdentifier());
					users.put(user.getIdentifier(), user);
					try {
						dbcreate.createUser(user, org.getIdentifier());
					} catch (OrgNotFoundException e) {
						e.printStackTrace();
					} catch (UserAlreadyExistsException e) {
						e.printStackTrace();
					}
				}
				org.setUsers(tempUsers);
				tempTerminals = new HashMap<String, OrgTerminal>();

//no create dummy terminals -> get/create it from topology 				
//				for(i=0; i<10; i++){
//					OrgTerminal terminal = new OrgTerminal();
//					String tid = "terminall";
//					String tname = "namee";
//					tid += Integer.toString(j);
//					tid += Integer.toString(i);
//					tname += Integer.toString(j);
//					tname += Integer.toString(i);
//					terminal.setIdentifier(tid);
//					terminal.setHostName(tname);
//					terminal.setDescription("terminal host for ");
//					terminal.setDescription(terminal.getDescription() + org.getName());
//					terminal.setIfaceSpeed(i*10000);
//					terminal.setIpAddress("192.168."+Integer.toString(i)+"."+Integer.toString(j)+"0");
//					terminal.setMac("DD:XX:AA:TT:"+Integer.toString(j)+"B:"+Integer.toString(i)+"C");
//					terminal.setActive(i%2==0?true:false);
//					terminal.setAssigned(i%2==0?true:false);
//					tempTerminals.put(terminal.getIdentifier(), terminal);
//					System.out.println("new terminal with id = "+terminal.getIdentifier());
//					terminals.put(terminal.getIdentifier(), terminal);
//					try {
//						dbcreate.createTerminal(terminal, org.getIdentifier());
//					} catch (TerminalAlreadyExistsException e) {
//						e.printStackTrace();
//					} catch (OrgNotFoundException e) {
//						e.printStackTrace();
//					}
//				}
				org.setTerminals(tempTerminals);
				orgs.put(org.getIdentifier(), org);
				torgs.put(torg.getIdentifier(), torg);
			}
		}
		
	}
}
