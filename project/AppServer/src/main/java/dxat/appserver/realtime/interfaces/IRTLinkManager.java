package dxat.appserver.realtime.interfaces;

import dxat.appserver.topology.exceptions.LinkNotFoundException;
import dxat.appserver.topology.pojos.Link;

public interface IRTLinkManager {
	public void addLink(Link link);

	public void updateLink(Link link) throws LinkNotFoundException;

	public void enableLink(String srcportId, String dstPortId) throws LinkNotFoundException;

	public void disableLink(String srcportId, String dstPortId) throws LinkNotFoundException;
}
