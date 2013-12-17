package dxat.appserver.topology;

import dxat.appserver.realtime.interfaces.IRTLinkManager;
import dxat.appserver.topology.db.LinkTopologyDB;
import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.LinkNotFoundException;
import dxat.appserver.topology.exceptions.PortNotFoundException;
import dxat.appserver.topology.interfaces.ITopoLinkManager;
import dxat.appserver.topology.pojos.Link;
import dxat.appserver.topology.pojos.LinkCollection;

public class LinkManager implements ITopoLinkManager, IRTLinkManager {
	private static LinkManager instance = null;

	public static LinkManager getInstance() {
		if (instance == null)
			instance = new LinkManager();
		return instance;
	}

	@Override
	public void addLink(Link link) throws PortNotFoundException {
		LinkTopologyDB linkTopologyDB = new LinkTopologyDB();
		try {
			linkTopologyDB.opendb();
			linkTopologyDB.addLink(link);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			linkTopologyDB.closedb();
		}
	}

	@Override
	public void updateLink(Link linkUpdate) throws LinkNotFoundException {
		LinkTopologyDB linkTopologyDB = new LinkTopologyDB();
		try {
			linkTopologyDB.opendb();
			linkTopologyDB.updateLink(linkUpdate);
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			linkTopologyDB.closedb();
		}
	}

	@Override
	public void enableLink(String srcPortId, String dstPortId)
			throws LinkNotFoundException {
		LinkTopologyDB linkTopologyDB = new LinkTopologyDB();
		try {
			Link link = new Link();
			link.setDstPortId(dstPortId);
			link.setSrcPortId(srcPortId);
			linkTopologyDB.opendb();
			linkTopologyDB.enableLink(link.getLinkKey());
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			linkTopologyDB.closedb();
		}
	}

	@Override
	public void disableLink(String srcPortId, String dstPortId)
			throws LinkNotFoundException {
		LinkTopologyDB linkTopologyDB = new LinkTopologyDB();
		try {
			Link link = new Link();
			link.setDstPortId(dstPortId);
			link.setSrcPortId(srcPortId);
			linkTopologyDB.opendb();
			linkTopologyDB.enableLink(link.getLinkKey());
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			linkTopologyDB.closedb();
		}
	}

	@Override
	public LinkCollection getLinks() {
		LinkTopologyDB linkTopologyDB = new LinkTopologyDB();
		LinkCollection linkCollection = null;
		try {
			linkTopologyDB.opendb();
			linkCollection = linkTopologyDB.getAllLinks();
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} finally {
			linkTopologyDB.closedb();
		}
		return linkCollection;
	}

	@Override
	public Link getLink(String srcPortId, String dstPortId) {
		LinkTopologyDB linkTopologyDB = new LinkTopologyDB();
		Link link = null;
		try {
			Link linkT = new Link();
			linkT.setDstPortId(dstPortId);
			linkT.setSrcPortId(srcPortId);
			linkTopologyDB.opendb();
			link = linkTopologyDB.getLink(linkT.getLinkKey());
		} catch (CannotOpenDataBaseException e) {
			e.printStackTrace();
		} catch (LinkNotFoundException e) {
			System.out.println(e.getMessage());
		} finally {
			linkTopologyDB.closedb();
		}
		return link;
	}

}
