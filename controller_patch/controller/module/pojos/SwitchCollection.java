package dxat.controller.module.pojos;

import java.util.ArrayList;
import java.util.List;

public class SwitchCollection {
	private List<Switch> switches = null;

	public SwitchCollection() {
		this.switches = new ArrayList<Switch>();
	}

	public List<Switch> getSwitches() {
		return switches;
	}

	public void setSwitches(List<Switch> switches) {
		this.switches = switches;
	}
}
