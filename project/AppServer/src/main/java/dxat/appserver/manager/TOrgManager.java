package dxat.appserver.manager;

import java.util.List;

import dxat.appserver.manager.pojos.TOrg;

public class TOrgManager {
	private static TOrgManager instance;
	private OrgManager orgManager;
	private TOrgManager(){
		//TODO initialize!
		orgManager = OrgManager.getInstance();

	}
	public static TOrgManager getInstance(){
		if(instance == null)
			instance = new TOrgManager();
		return instance;
	}
	public List<TOrg> getAllTOrgs(String orgId){
		List<TOrg> torgs = (List<TOrg>) orgManager.getAllTOrgs();
		return torgs;
	}
	public void addTOrg(String orgId, TOrg torg){
		//TODO
	}
	public TOrg getTOrg(String orgId, String torgId){
		//TODO
		return null;
	}
	public void deleteTOrg(String orgId, String torgId){
		//TODO
	}
	public void updateTOrg(String orgId, TOrg torg){
		//TODO
	}
}
