package dxat.appserver.topology.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dxat.appserver.realtime.pojos.DbUpdate;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;

import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.PortNotFoundException;
import dxat.appserver.topology.exceptions.TerminalExistsException;
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

	private List<DbUpdate> updateNodeTerminalProperties(Node terminalNode,
			Terminal terminal) {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		String ipv4 = (String) terminalNode.getProperty("ipv4");
		String mac = (String) terminalNode.getProperty("mac");
		String portAPId = (String) terminalNode.getProperty("portAPId");
		Boolean enabled = (Boolean) terminalNode.getProperty("enabled");

		if (!ipv4.equals(terminal.getIpv4())) {
			DbUpdate update = new DbUpdate();
			terminalNode.setProperty("ipv4", terminal.getIpv4());
			update.setInventoryId(terminal.getTerminalId());
			update.setPropertyId("ipv4");
			update.setLegacyValue(ipv4);
			update.setNewValue(terminal.getIpv4());
			updates.add(update);
		}

		if (!mac.equals(terminal.getMac())) {
			DbUpdate update = new DbUpdate();
			terminalNode.setProperty("mac", terminal.getMac());
			update.setInventoryId(terminal.getTerminalId());
			update.setPropertyId("mac");
			update.setLegacyValue(mac);
			update.setNewValue(terminal.getMac());
			updates.add(update);
		}

		if (!portAPId.equals(terminal.getPortAPId())) {
			DbUpdate update = new DbUpdate();
			terminalNode.setProperty("portAPId", terminal.getPortAPId());
			update.setInventoryId(terminal.getTerminalId());
			update.setPropertyId("portAPId");
			update.setLegacyValue(portAPId);
			update.setNewValue(terminal.getPortAPId());
			updates.add(update);
		}

		if (!enabled.equals(terminal.getEnabled())) {
			DbUpdate update = new DbUpdate();
			terminalNode.setProperty("enabled", terminal.getEnabled());
			update.setInventoryId(terminal.getTerminalId());
			update.setPropertyId("enabled");
			update.setLegacyValue(enabled.toString());
			update.setNewValue(terminal.getEnabled().toString());
			updates.add(update);
		}

		return updates;
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

	public List<DbUpdate> addTerminal(Terminal terminal)
			throws PortNotFoundException, TerminalExistsException {
		// Check existence of the Terminal
		if (existTerminal(terminal.getTerminalId()))
			throw new TerminalExistsException(
					"Trying to add a terminal whose identifier already exists('"
							+ terminal.getTerminalId() + "')");

		// Check existence of the Access point
		Node dstPortNode = null;
		if (!terminal.getPortAPId().equals("00:00:00:00:00:00:00:00:0")) {
			dstPortNode = getNodePort(terminal.getPortAPId());
		}

		// Generate Update
		List<DbUpdate> updates = null;
		DbUpdate update = new DbUpdate();
		update.setInventoryId(terminal.getTerminalId());
		update.setPropertyId("TERMINAL");
		update.setLegacyValue("NONE");
		update.setNewValue("NEW");
		updates = new ArrayList<DbUpdate>();
		updates.add(update);

		// Create Terminal
		Node terminalNode = graphDb.createNode(Labels.TERMINAL_LABEL);
		getManagerNode().createRelationshipTo(terminalNode, RelTypes.ELEMENT);
		setNodeTerminalProperties(terminalNode, terminal);

		// Create Neo4j Port Link
		if (dstPortNode != null) {
			Node srcPortNode = terminalNode;
			srcPortNode.createRelationshipTo(dstPortNode, RelTypes.LINK);
		}

		// Rerturn list of DB updates
		return updates;
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

	public List<DbUpdate> updateTerminal(Terminal terminal)
			throws TerminalNotFoundException {
		ResourceIterator<Node> terminalNodes = graphDb
				.findNodesByLabelAndProperty(Labels.TERMINAL_LABEL, ID_PROPERTY,
						terminal.getTerminalId()).iterator();

		// If terminal does not exist
		if (!terminalNodes.hasNext())
			throw new TerminalNotFoundException(terminal);

		Node terminalNode = terminalNodes.next();
		return updateNodeTerminalProperties(terminalNode, terminal);
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

	public List<DbUpdate> disableTerminal(String terminalId)
			throws TerminalNotFoundException {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		ResourceIterator<Node> terminalNodes = graphDb
				.findNodesByLabelAndProperty(Labels.TERMINAL_LABEL,
						ID_PROPERTY, terminalId).iterator();

		if (!terminalNodes.hasNext())
			throw new TerminalNotFoundException(terminalId);

		Node terminalNode = terminalNodes.next();
		Boolean enabled = (Boolean) terminalNode.getProperty("enabled");
		if (enabled.equals(true)) {
			terminalNode.setProperty("enabled", false);
			DbUpdate update = new DbUpdate();
			update.setInventoryId(terminalId);
			update.setPropertyId("enabled");
			update.setLegacyValue("true");
			update.setNewValue("false");
			updates.add(update);
		}

		return updates;
	}

	public List<DbUpdate> enableTerminal(String terminalId)
			throws TerminalNotFoundException {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		ResourceIterator<Node> terminalNodes = graphDb
				.findNodesByLabelAndProperty(Labels.TERMINAL_LABEL,
						ID_PROPERTY, terminalId).iterator();

		if (!terminalNodes.hasNext())
			throw new TerminalNotFoundException(terminalId);

		Node terminalNode = terminalNodes.next();
		Boolean enabled = (Boolean) terminalNode.getProperty("enabled");
		if (enabled.equals(false)) {
			terminalNode.setProperty("enabled", true);
			DbUpdate update = new DbUpdate();
			update.setInventoryId(terminalId);
			update.setPropertyId("enabled");
			update.setLegacyValue("false");
			update.setNewValue("true");
			updates.add(update);
		}

		return updates;
	}
	
	public List<DbUpdate> mergeCollection(TerminalCollection terminalCollection) throws PortNotFoundException {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();
		List<Terminal> terminalList = terminalCollection.getTerminals();
		HashMap<String, Terminal> terminalMap = new HashMap<String, Terminal>();

		for (Terminal terminal : terminalList) {
			terminalMap.put(terminal.getTerminalId(), terminal);
			try {
				updates.addAll(addTerminal(terminal));
			} catch (TerminalExistsException e) {
				try {
					updates.addAll(updateTerminal(terminal));
				} catch (TerminalNotFoundException e1) {
				}
			}
		}

		Iterable<Relationship> terminalRelations = getManagerNode()
				.getRelationships();
		for (Relationship relation : terminalRelations) {
			Node terminalNode = relation.getEndNode();
			if (terminalNode.hasLabel(Labels.TERMINAL_LABEL)) {
				String terminalId = (String) terminalNode.getProperty(ID_PROPERTY);
				if (!terminalMap.containsKey(terminalId)) {
					try {
						updates.addAll(disableTerminal(terminalId));
					} catch (TerminalNotFoundException e) {
					}
				}
			}
		}

		return updates;
	}
}
