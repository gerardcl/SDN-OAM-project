package dxat.appserver.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dxat.appserver.manager.pojos.OrgCollection;
import dxat.appserver.manager.pojos.OrgTerminal;
import dxat.appserver.manager.pojos.Org;
import dxat.appserver.manager.pojos.TOrg;

public class OrgManager {
	private static OrgManager instance;
	private HashMap<String, Org> orgs;
	private OrgManager(){
		orgs = new HashMap<String, Org>();
		/*some test init*/
		int i = 0;
		for(i = 1; i < 6; i++){
			Org org = new Org();
			String id = "orgId";
			String name = "orgName";
			id += Integer.toString(i);
			name += Integer.toString(i);
			org.setIdentifier(id);
			org.setName(name);
			org.setNIF("192.168.1.10");
			org.setTelephone("689400423");
			org.setBankAccount("2342343-23452345-23452345-23452345");
			org.setOAM(true);
			
			orgs.put(org.getIdentifier(), org);
		}
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
	public Org getOrg(String id){
		//TODO
		return null;
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
