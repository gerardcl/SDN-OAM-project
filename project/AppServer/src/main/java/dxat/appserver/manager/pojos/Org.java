package dxat.appserver.manager.pojos;

import java.util.HashMap;

public class Org {
	private String identifier;
	private String name;
	private String NIF;
	private String telephone;
	private String bankAccount;
	private boolean isOAM;
	private HashMap<String, OrgUser> users;
	private HashMap<String, OrgTerminal> terminals;
	private HashMap<String, OrgFlow> flows;
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNIF() {
		return NIF;
	}
	public void setNIF(String nIF) {
		NIF = nIF;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public boolean isOAM() {
		return isOAM;
	}
	public void setOAM(boolean isOAM) {
		this.isOAM = isOAM;
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
	public HashMap<String, OrgFlow> getFlows() {
		return flows;
	}
	public void setFlows(HashMap<String, OrgFlow> flows) {
		this.flows = flows;
	}
	
}
