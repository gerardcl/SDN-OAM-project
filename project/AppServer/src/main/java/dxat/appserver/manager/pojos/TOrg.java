package dxat.appserver.manager.pojos;

public class TOrg {
	private String identifier;
	private String name;
	private String NIF;
	private String telephone;
	private String bankAccount;
	private boolean isOAM;

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
}
