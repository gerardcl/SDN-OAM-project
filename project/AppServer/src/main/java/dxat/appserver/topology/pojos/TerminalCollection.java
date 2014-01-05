package dxat.appserver.topology.pojos;

import java.util.ArrayList;
import java.util.List;

public class TerminalCollection {
	private List<Terminal> terminals = null;

	public TerminalCollection() {
		terminals = new ArrayList<Terminal>();
	}

	public List<Terminal> getTerminals() {
		return terminals;
	}

	public void setTerminals(List<Terminal> terminals) {
		this.terminals = terminals;
	}

}
