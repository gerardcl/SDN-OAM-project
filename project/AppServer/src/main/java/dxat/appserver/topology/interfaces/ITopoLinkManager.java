package dxat.appserver.topology.interfaces;

import dxat.appserver.topology.pojos.Link;
import dxat.appserver.topology.pojos.LinkCollection;

public interface ITopoLinkManager {
	public LinkCollection getLinks();

	public Link getLink(String srcPortId, String dstPortId);
}
