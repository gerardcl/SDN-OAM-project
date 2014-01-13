package dxat.controller.module.pojos;

import java.util.List;

public class DeployedFlow extends Flow {
	private List<String> route;

	public List<String> getRoute() {
		return route;
	}

	public void setRoute(List<String> route) {
		this.route = route;
	}

}
