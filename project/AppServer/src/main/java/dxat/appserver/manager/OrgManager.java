package dxat.appserver.manager;

import java.util.HashMap;
import java.util.List;

import dxat.appserver.manager.pojos.OrgTerminal;
import dxat.appserver.manager.pojos.Organization;
import dxat.appserver.manager.pojos.TOrganization;

public class OrgManager {
	private static OrgManager instance;
	private HashMap<String, Organization> orgs;
	private OrgManager(){
		orgs = new HashMap<String, Organization>();
		/*some test init*/
	}
	public static OrgManager getInstance(){
		if(instance == null)
			instance = new OrgManager();
		return instance;
	}
	public Organization getOrg(String id){
		//TODO
		return null;
	}
	public void addOrg(TOrganization id){
		//TODO
	}
	public TOrganization delete(String id){
		//TODO
		return null;
	}
	public Organization updateOrg(Organization org){
		//TODO
		return org;
	}
	public boolean existOrg(OrgTerminal orgT){
		//TODO
		return false;
	}
	public List<Organization> getOrgs(){
		//TODO
		return null;
	}
}
