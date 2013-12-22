package dxathacks.floodlightcontroller.pojos;

import java.util.ArrayList;
import java.util.List;

public class InterfacesCollection {
	private List<Interface> interfaces;

	public InterfacesCollection() {
		interfaces = new ArrayList<Interface>();
	}

	public List<Interface> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<Interface> interfaces) {
		this.interfaces = interfaces;
	}

	public void addInterface (Interface ifc){
		interfaces.add(ifc);
	}
	
}
