package dxat.appserver.topology.db;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;

import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.PortNotFoundException;
import dxat.appserver.topology.exceptions.TerminalNotFoundException;
import dxat.appserver.topology.pojos.Terminal;
import dxat.appserver.topology.pojos.TerminalCollection;

public class TerminalTopologyDB extends TopologyDB {
	@Override
	public void opendb() throws CannotOpenDataBaseException {
		super.opendb();
		ResourceIterator<Node> linkManager = graphDb
				.findNodesByLabelAndProperty(
						DynamicLabel.label(Managers.TERMINAL_MANAGER),
						ID_PROPERTY, Managers.TERMINAL_MANAGER).iterator();
		if (!linkManager.hasNext()) {
			graphDb.createNode(DynamicLabel.label(Managers.TERMINAL_MANAGER))
					.setProperty(ID_PROPERTY, Managers.TERMINAL_MANAGER);
		}
	}

	@Override
	protected Node getManagerNode() {
		return graphDb
				.findNodesByLabelAndProperty(
						DynamicLabel.label(Managers.TERMINAL_MANAGER),
						ID_PROPERTY, Managers.TERMINAL_MANAGER).iterator()
				.next();
	}

	private void setNodeTerminalProperties(Node terminalNode, Terminal terminal) {
		terminalNode.setProperty(ID_PROPERTY, terminal.getTerminalId());
		terminalNode.setProperty("ipv4", terminal.getIpv4());
		terminalNode.setProperty("mac", terminal.getMac());
		terminalNode.setProperty("portAPId", terminal.getPortAPId());
		terminalNode.setProperty("enabled", terminal.getEnabled());
	}

	private void getNodeTerminalProperties(Node terminalNode, Terminal terminal) {
		terminal.setEnabled((Boolean) terminalNode.getProperty("enabled"));
		terminal.setIpv4((String) terminalNode.getProperty("ipv4"));
		terminal.setMac((String) terminalNode.getProperty("mac"));
		terminal.setPortAPId((String) terminalNode.getProperty("portAPId"));
		terminal.setTerminalId((String) terminalNode.getProperty(ID_PROPERTY));
	}

	public boolean existTerminal(String terminalId) {
		return graphDb
				.findNodesByLabelAndProperty(Labels.TERMINAL_LABEL,
						ID_PROPERTY, terminalId).iterator().hasNext();
	}

	private Node getNodePort(String portId) throws PortNotFoundException {
		ResourceIterator<Node> portNodes = graphDb.findNodesByLabelAndProperty(
				Labels.PORT_LABEL, ID_PROPERTY, portId).iterator();
		if (!portNodes.hasNext())
			throw new PortNotFoundException(portId);

		return portNodes.next();
	}

	public void addTerminal(Terminal terminal) throws PortNotFoundException {
		try {
			updateTerminal(terminal);
		} catch (TerminalNotFoundException e) {
			// Create Link
			Node terminalNode = graphDb.createNode(Labels.TERMINAL_LABEL);
			getManagerNode().createRelationshipTo(terminalNode,
					RelTypes.ELEMENT);
			setNodeTerminalProperties(terminalNode, terminal);

			// Create Neo4j Port Link
			Node srcPortNode = terminalNode;
			Node dstPortNode = getNodePort(terminal.getPortAPId());
			srcPortNode.createRelationshipTo(dstPortNode, RelTypes.LINK);
			System.out.println("[NEO4J DEBUG] Terminal Added // node: '"
					+ terminalNode.getId() + "'");
		}
	}

	public Terminal getTerminal(String terminalId)
			throws TerminalNotFoundException {
		ResourceIterator<Node> terminalNodes = graphDb
				.findNodesByLabelAndProperty(Labels.TERMINAL_LABEL,
						ID_PROPERTY, terminalId).iterator();
		if (!terminalNodes.hasNext())
			throw new TerminalNotFoundException(terminalId);

		Node terminalNode = terminalNodes.next();
		Terminal terminal = new Terminal();
		getNodeTerminalProperties(terminalNode, terminal);
		return terminal;
	}

	public void updateTerminal(Terminal terminal)
			throws TerminalNotFoundException {
		ResourceIterator<Node> terminalNodes = graphDb
				.findNodesByLabelAndProperty(Labels.LINK_LABEL, ID_PROPERTY,
						terminal.getTerminalId()).iterator();
		if (!terminalNodes.hasNext())
			throw new TerminalNotFoundException(terminal);

		Node terminalNode = terminalNodes.next();
		setNodeTerminalProperties(terminalNode, terminal);
		System.out.println("[NEO4J DEBUG] Terminal updated // node: '"
				+ terminalNode.getId() + "'");

	}

	public TerminalCollection getAllTerminals() {
		Iterable<Relationship> terminalRelations = getManagerNode()
				.getRelationships();
		List<Terminal> terminalList = new ArrayList<Terminal>();

		for (Relationship relation : terminalRelations) {
			Node terminalNode = relation.getEndNode();
			if (terminalNode.hasLabel(Labels.TERMINAL_LABEL)) {
				Terminal terminal = new Terminal();
				getNodeTerminalProperties(terminalNode, terminal);
				terminalList.add(terminal);
			}
		}
		TerminalCollection terminalCollection = new TerminalCollection();
		terminalCollection.setTerminals(terminalList);
		return terminalCollection;
	}

	public void disableTerminal(String terminalId)
			throws TerminalNotFoundException {
		ResourceIterator<Node> terminalNodes = graphDb
				.findNodesByLabelAndProperty(Labels.TERMINAL_LABEL,
						ID_PROPERTY, terminalId).iterator();
		if (!terminalNodes.hasNext())
			throw new TerminalNotFoundException(terminalId);
		terminalNodes.next().setProperty("enabled", false);
	}

	public void enableTerminal(String terminalId)
			throws TerminalNotFoundException {
		ResourceIterator<Node> terminalNodes = graphDb
				.findNodesByLabelAndProperty(Labels.TERMINAL_LABEL,
						ID_PROPERTY, terminalId).iterator();
		if (!terminalNodes.hasNext())
			throw new TerminalNotFoundException(terminalId);
		terminalNodes.next().setProperty("enabled", true);
	}

}
