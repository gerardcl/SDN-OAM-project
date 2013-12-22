package dxat.appserver.topology.db;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;

import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.LinkExistsException;
import dxat.appserver.topology.exceptions.LinkNotFoundException;
import dxat.appserver.topology.exceptions.PortNotFoundException;
import dxat.appserver.topology.pojos.Link;
import dxat.appserver.topology.pojos.LinkCollection;

public class LinkTopologyDB extends TopologyDB {
	@Override
	public void opendb() throws CannotOpenDataBaseException {
		super.opendb();
		ResourceIterator<Node> linkManager = graphDb
				.findNodesByLabelAndProperty(
						DynamicLabel.label(Managers.LINK_MANAGER), ID_PROPERTY,
						Managers.LINK_MANAGER).iterator();
		if (!linkManager.hasNext()) {
			graphDb.createNode(DynamicLabel.label(Managers.LINK_MANAGER))
					.setProperty(ID_PROPERTY, Managers.LINK_MANAGER);
		}
	}

	@Override
	protected Node getManagerNode() {
		return graphDb
				.findNodesByLabelAndProperty(
						DynamicLabel.label(Managers.LINK_MANAGER), ID_PROPERTY,
						Managers.LINK_MANAGER).iterator().next();
	}

	private void setNodeLinkProperties(Node linkNode, Link link) {
		linkNode.setProperty(ID_PROPERTY, link.getLinkKey());
		linkNode.setProperty("dstPortId", link.getDstPortId());
		linkNode.setProperty("srcPortId", link.getSrcPortId());
		linkNode.setProperty("enabled", link.getEnabled());
	}

	private List<DbUpdate> updateNodeLinkProperties(Node linkNode, Link link) {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();
		String dstPortId = (String) linkNode.getProperty("dstPortId");
		String srcPortId = (String) linkNode.getProperty("srcPortId");
		Boolean enabled = (Boolean) linkNode.getProperty("enabled");

		if (!dstPortId.equals(link.getDstPortId())) {
			DbUpdate update = new DbUpdate();
			update.setInventoryId(link.getLinkKey());
			update.setLegacyValue(dstPortId);
			update.setNewValue(link.getDstPortId());
			updates.add(update);
			linkNode.setProperty("dstPortId", dstPortId);
		}

		if (!srcPortId.equals(link.getSrcPortId())) {
			DbUpdate update = new DbUpdate();
			update.setInventoryId(link.getLinkKey());
			update.setLegacyValue(srcPortId);
			update.setNewValue(link.getSrcPortId());
			updates.add(update);
			linkNode.setProperty("srcPortId", srcPortId);
		}

		if (!enabled.equals(link.getEnabled())) {
			DbUpdate update = new DbUpdate();
			update.setInventoryId(link.getLinkKey());
			update.setLegacyValue(enabled.toString());
			update.setNewValue(link.getEnabled().toString());
			updates.add(update);
			linkNode.setProperty("enabled", link.getEnabled());
		}

		return updates;
	}

	private void getNodeLinkProperties(Node linkNode, Link link) {
		link.setDstPortId((String) linkNode.getProperty("dstPortId"));
		link.setEnabled((Boolean) linkNode.getProperty("enabled"));
		link.setSrcPortId((String) linkNode.getProperty("srcPortId"));
	}

	public boolean existLink(String linkKey) {
		return graphDb
				.findNodesByLabelAndProperty(Labels.LINK_LABEL, ID_PROPERTY,
						linkKey).iterator().hasNext();
	}

	private Node getNodePort(String portId) throws PortNotFoundException {
		ResourceIterator<Node> portNodes = graphDb.findNodesByLabelAndProperty(
				Labels.PORT_LABEL, ID_PROPERTY, portId).iterator();
		if (!portNodes.hasNext())
			throw new PortNotFoundException(portId);

		return portNodes.next();
	}

	public List<DbUpdate> addLink(Link link) throws PortNotFoundException,
			LinkExistsException {
		if (existLink(link.getLinkKey()))
			throw new LinkExistsException(
					"Trying to add a link whose identifier already exists('"
							+ link.getLinkKey() + "')");

		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		// Create Link
		Node linkNode = graphDb.createNode(Labels.LINK_LABEL);
		getManagerNode().createRelationshipTo(linkNode, RelTypes.ELEMENT);
		setNodeLinkProperties(linkNode, link);

		// Create Neo4j Port Link
		Node srcPortNode = getNodePort(link.getSrcPortId());
		Node dstPortNode = getNodePort(link.getDstPortId());
		srcPortNode.createRelationshipTo(dstPortNode, RelTypes.LINK);

		DbUpdate update = new DbUpdate();
		update.setInventoryId(link.getLinkKey());
		update.setLegacyValue("NONE");
		update.setNewValue("NEW");
		update.setPropertyId("LINK");
		updates.add(update);
		return updates;
	}

	public Link getLink(String linkKey) throws LinkNotFoundException {
		ResourceIterator<Node> linkNodes = graphDb.findNodesByLabelAndProperty(
				Labels.LINK_LABEL, ID_PROPERTY, linkKey).iterator();
		if (!linkNodes.hasNext())
			throw new LinkNotFoundException(linkKey);

		Node linkNode = linkNodes.next();
		Link link = new Link();
		getNodeLinkProperties(linkNode, link);
		return link;
	}

	public List<DbUpdate> updateLink(Link link) throws LinkNotFoundException {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();
		ResourceIterator<Node> linkNodes = graphDb.findNodesByLabelAndProperty(
				Labels.LINK_LABEL, ID_PROPERTY, link.getLinkKey()).iterator();
		if (!linkNodes.hasNext())
			throw new LinkNotFoundException("Trying update link with id '"
					+ link.getLinkKey() + "'. It has not been found.");

		Node linkNode = linkNodes.next();
		updates.addAll(updateNodeLinkProperties(linkNode, link));
		return updates;
	}

	public LinkCollection getAllLinks() {
		Iterable<Relationship> linkRelations = getManagerNode()
				.getRelationships();
		List<Link> linkList = new ArrayList<Link>();

		for (Relationship relation : linkRelations) {
			Node linkNode = relation.getEndNode();
			if (linkNode.hasLabel(Labels.LINK_LABEL)) {
				Link link = new Link();
				getNodeLinkProperties(linkNode, link);
				linkList.add(link);
			}
		}
		LinkCollection linkCollection = new LinkCollection();
		linkCollection.setLinks(linkList);
		return linkCollection;
	}

	public List<DbUpdate> disableLink(String linkKey)
			throws LinkNotFoundException {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		ResourceIterator<Node> linkNodes = graphDb.findNodesByLabelAndProperty(
				Labels.SWITCH_LABEL, ID_PROPERTY, linkKey).iterator();

		if (!linkNodes.hasNext())
			throw new LinkNotFoundException("Trying disable link with id '"
					+ linkKey + "'. It has not been found.");

		Node linkNode = linkNodes.next();
		Boolean enabled = (Boolean) linkNode.getProperty("enabled");
		if (enabled.equals(true)) {
			linkNode.setProperty("enabled", false);
			DbUpdate update = new DbUpdate();
			update.setInventoryId(linkKey);
			update.setPropertyId("enabled");
			update.setLegacyValue("true");
			update.setNewValue("false");
			updates.add(update);
		}

		return updates;
	}

	public List<DbUpdate> enableLink(String linkKey)
			throws LinkNotFoundException {
		ResourceIterator<Node> linkNodes = graphDb.findNodesByLabelAndProperty(
				Labels.SWITCH_LABEL, ID_PROPERTY, linkKey).iterator();

		if (!linkNodes.hasNext())
			throw new LinkNotFoundException("Trying disable link with id '"
					+ linkKey + "'. It has not been found.");

		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		Node linkNode = linkNodes.next();
		Boolean enabled = (Boolean) linkNode.getProperty("enabled");
		if (enabled.equals(true)) {
			linkNode.setProperty("enabled", true);
			DbUpdate update = new DbUpdate();
			update.setInventoryId(linkKey);
			update.setPropertyId("enabled");
			update.setLegacyValue("false");
			update.setNewValue("true");
			updates.add(update);
		}

		return updates;
	}

}
