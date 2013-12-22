package dxat.appserver.topology;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import dxat.appserver.realtime.interfaces.ILinkEvents;
import dxat.appserver.realtime.interfaces.ISwitchEvents;
import dxat.appserver.realtime.pojos.ControllerEvent;
import dxat.appserver.topology.db.DbUpdate;
import dxat.appserver.topology.db.LinkTopologyDB;
import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.LinkNotFoundException;
import dxat.appserver.topology.exceptions.PortNotFoundException;
import dxat.appserver.topology.interfaces.ITopoLinkManager;
import dxat.appserver.topology.pojos.Link;
import dxat.appserver.topology.pojos.LinkCollection;

public class LinkManager implements ITopoLinkManager, ILinkEvents {
	private static LinkManager instance = null;

	public static LinkManager getInstance() {
		if (instance == null)
			instance = new LinkManager();
		return instance;
	}

	public List<DbUpdate> processEvent(ControllerEvent controllerEvent)
			throws JsonSyntaxException, PortNotFoundException, LinkNotFoundException {
		String eventStr = controllerEvent.getEvent();
		if (eventStr.equals(ILinkEvents.LINK_UPDATED)) {
			LinkManager.getInstance().linkUpdated(
					new Gson().fromJson(controllerEvent.getObject(),
							Link.class));
		} else if (eventStr.equals(ILinkEvents.LINK_REMOVED)) {
			LinkManager.getInstance().linkRemoved(
					new Gson().fromJson(controllerEvent.getObject(),
							Link.class));
		} else if (eventStr.equals(ILinkEvents.PORT_DOWN)) {
			LinkManager.getInstance().portDown(
					new Gson().fromJson(controllerEvent.getObject(),
							Link.class));
		} else if (eventStr.equals(ILinkEvents.PORT_UP)) {
			LinkManager.getInstance().portUp(
					new Gson().fromJson(controllerEvent.getObject(),
							Link.class));
		} else if (eventStr.equals(ILinkEvents.SWITCH_REMOVED)) {
			LinkManager.getInstance().switchRemoved(
					new Gson().fromJson(controllerEvent.getObject(),
							Link.class));
		} else if (eventStr.equals(ILinkEvents.SWITCH_UPDATED)) {
			LinkManager.getInstance().switchUpdated(
					new Gson().fromJson(controllerEvent.getObject(),
							Link.class));
		} else if (eventStr.equals(ILinkEvents.TUNEL_PORT_ADDED)) {
			LinkManager.getInstance().tunelPortAdded(
					new Gson().fromJson(controllerEvent.getObject(),
							Link.class));
		} else if (eventStr.equals(ILinkEvents.TUNEL_PORT_REMOVED)) {
			LinkManager.getInstance().tunelPortRemoved(
					new Gson().fromJson(controllerEvent.getObject(),
							Link.class));
		} else if (eventStr.equals(ISwitchEvents.SWITCH_ACTIVATED)) {
			LinkManager.getInstance().portUp(
					new Gson().fromJson(controllerEvent.getObject(),
							Link.class));
		}
		return null;
	}
	
	private void addLink(Link link) throws PortNotFoundException {
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

	private void updateLink(Link linkUpdate) throws LinkNotFoundException {
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

	/*private void enableLink(String srcPortId, String dstPortId)
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
	}*/

	private void disableLink(Link link)
			throws LinkNotFoundException {
		LinkTopologyDB linkTopologyDB = new LinkTopologyDB();
		try {
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

	@Override
	public void linkUpdated(Link link) throws LinkNotFoundException {
		updateLink(link);
	}

	@Override
	public void linkRemoved(Link link) throws LinkNotFoundException {
		disableLink(link);
	}

	@Override
	public void switchUpdated(Link link) throws LinkNotFoundException  {
		updateLink(link);
	}

	@Override
	public void switchRemoved(Link link)  throws LinkNotFoundException {
		disableLink(link);
	}

	@Override
	public void portUp(Link link)  throws PortNotFoundException {
		addLink(link);
	}

	@Override
	public void portDown(Link link)  throws LinkNotFoundException {
		disableLink(link);
	}

	@Override
	public void tunelPortAdded(Link link)  throws PortNotFoundException {
		addLink(link);
	}

	@Override
	public void tunelPortRemoved(Link link) throws LinkNotFoundException  {
		disableLink(link);
	}

}
