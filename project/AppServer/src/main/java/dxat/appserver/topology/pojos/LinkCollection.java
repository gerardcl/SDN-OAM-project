package dxat.appserver.topology.pojos;

import java.util.ArrayList;
import java.util.List;

public class LinkCollection {
	private List<Link> links = null;

	public LinkCollection() {
		links = new ArrayList<Link>();
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

}
