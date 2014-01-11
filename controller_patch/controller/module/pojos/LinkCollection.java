package dxat.controller.module.pojos;

import java.util.ArrayList;
import java.util.List;

public class LinkCollection {
	private List<TranferLink> links;

	public LinkCollection() {
		links = new ArrayList<TranferLink>();
	}

	public List<TranferLink> getLinks() {
		return links;
	}

	public void setLinks(List<TranferLink> links) {
		this.links = links;
	}

}
