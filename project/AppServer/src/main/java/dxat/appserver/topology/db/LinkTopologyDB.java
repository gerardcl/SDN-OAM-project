package dxat.appserver.topology.db;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;

import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
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

	public void addLink(Link link) throws PortNotFoundException {
		try {
			updateLink(link);
		} catch (LinkNotFoundException e) {
			// Create Link
			Node linkNode = graphDb.createNode(Labels.LINK_LABEL);
			getManagerNode().createRelationshipTo(linkNode, RelTypes.ELEMENT);
			setNodeLinkProperties(linkNode, link);

			// Create Neo4j Port Link
			Node srcPortNode = getNodePort(link.getSrcPortId());
			Node dstPortNode = getNodePort(link.getDstPortId());
			srcPortNode.createRelationshipTo(dstPortNode, RelTypes.LINK);

			System.out.println("[NEO4J DEBUG] Link Added // node: '"
					+ linkNode.getId() + "'");
		}
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

	public void updateLink(Link link) throws LinkNotFoundException {
		ResourceIterator<Node> linkNodes = graphDb.findNodesByLabelAndProperty(
				Labels.LINK_LABEL, ID_PROPERTY, link.getLinkKey()).iterator();
		if (!linkNodes.hasNext())
			throw new LinkNotFoundException(link);

		Node linkNode = linkNodes.next();
		setNodeLinkProperties(linkNode, link);
		System.out.println("[NEO4J DEBUG] Link updated // node: '"
				+ linkNode.getId() + "'");

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

	public void disableLink(String linkKey) throws LinkNotFoundException {
		ResourceIterator<Node> linkNodes = graphDb.findNodesByLabelAndProperty(
				Labels.LINK_LABEL, ID_PROPERTY, linkKey).iterator();
		if (!linkNodes.hasNext())
			throw new LinkNotFoundException(linkKey);
		linkNodes.next().setProperty("enabled", false);
	}

	public void enableLink(String linkKey) throws LinkNotFoundException {
		ResourceIterator<Node> linkNodes = graphDb.findNodesByLabelAndProperty(
				Labels.LINK_LABEL, ID_PROPERTY, linkKey).iterator();
		if (!linkNodes.hasNext())
			throw new LinkNotFoundException(linkKey);
		linkNodes.next().setProperty("enabled", true);
	}

}
