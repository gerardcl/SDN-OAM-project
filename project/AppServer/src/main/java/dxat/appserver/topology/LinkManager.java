package dxat.appserver.topology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dxat.appserver.topology.exceptions.LinkNotFoundException;
import dxat.appserver.topology.interfaces.ITopoLinkManager;
import dxat.appserver.topology.pojos.Link;
import dxat.appserver.topology.pojos.LinkCollection;
import dxat.appserver.topology.realtime.IRTLinkManager;

public class LinkManager implements ITopoLinkManager, IRTLinkManager {
	private static LinkManager instance = null;
	private HashMap<String, Link> links = null;

	private LinkManager() {
		links = new HashMap<String, Link>();
	}

	public static LinkManager getInstance() {
		if (instance == null)
			instance = new LinkManager();

		return instance;
	}

	@Override
	public void addLink(Link link) {

	}

	@Override
	public void updateLink(Link linkUpdate) throws LinkNotFoundException {
		if (!links.containsKey(linkUpdate.getLinkKey()))
			throw new LinkNotFoundException("Link with source port id '"
					+ linkUpdate.getSrcPortId()
					+ "' to the destination port id '"
					+ linkUpdate.getDstPortId() + "'.");
		Link link = links.get(linkUpdate.getLinkKey());
		link.setDstPortId(linkUpdate.getDstPortId());
		link.setEnabled(linkUpdate.getEnabled());
		link.setSrcPortId(linkUpdate.getSrcPortId());
	}

	@Override
	public void enableLink(String srcPortId, String dstPortId)
			throws LinkNotFoundException {
		Link link = new Link();
		link.setSrcPortId(srcPortId);
		link.setDstPortId(dstPortId);

		if (!links.containsKey(link.getLinkKey()))
			throw new LinkNotFoundException("Link with source port id '"
					+ srcPortId + "' to the destination port id '" + dstPortId
					+ "'.");
		links.get(link.getLinkKey()).setEnabled(true);
	}

	@Override
	public void disableLink(String srcPortId, String dstPortId)
			throws LinkNotFoundException {
		Link link = new Link();
		link.setSrcPortId(srcPortId);
		link.setDstPortId(dstPortId);

		if (!links.containsKey(link.getLinkKey()))
			throw new LinkNotFoundException("Link with source port id '"
					+ srcPortId + "' to the destination port id '" + dstPortId
					+ "'.");
		links.get(link.getLinkKey()).setEnabled(false);
	}

	@Override
	public LinkCollection getLinks() {
		List <Link> linkList = new ArrayList<Link>(links.values());
		LinkCollection linkCollection= new LinkCollection();
		linkCollection.setLinks(linkList);
		return linkCollection;
	}

	@Override
	public Link getLink(String srcPortId, String dstPortId) {
		Link link = new Link();
		link.setSrcPortId(srcPortId);
		link.setDstPortId(dstPortId);
		return links.get(link.getLinkKey());
	}

}
