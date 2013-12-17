package dxat.appserver.topology.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Label;

import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.SwitchNotFoundException;
import dxat.appserver.topology.pojos.Port;
import dxat.appserver.topology.pojos.Switch;
import dxat.appserver.topology.pojos.SwitchCollection;

public class SwitchTopologyDB extends TopologyDB {
	public SwitchTopologyDB() {
		super();
	}

	@Override
	public void opendb() throws CannotOpenDataBaseException {
		super.opendb();
		ResourceIterator<Node> switchManagerNodes = graphDb
				.findNodesByLabelAndProperty(
						DynamicLabel.label(Managers.SWITCH_MANAGER),
						ID_PROPERTY, Managers.SWITCH_MANAGER).iterator();
		if (!switchManagerNodes.hasNext()) {
			graphDb.createNode(DynamicLabel.label(Managers.SWITCH_MANAGER))
					.setProperty(ID_PROPERTY, Managers.SWITCH_MANAGER);
		}
	}

	@Override
	protected Node getManagerNode() {
		return graphDb
				.findNodesByLabelAndProperty(
						DynamicLabel.label(Managers.SWITCH_MANAGER),
						ID_PROPERTY, Managers.SWITCH_MANAGER).iterator().next();
	}

	private void setNodeSwitchProperties(Node switchNode, Switch sw) {
		switchNode.setProperty(ID_PROPERTY, sw.getSwId());
		switchNode.setProperty("enabled", sw.getEnabled());
		switchNode.setProperty("hardware", sw.getHardware());
		switchNode.setProperty("manufacturer", sw.getManufacturer());
		switchNode.setProperty("software", sw.getSoftware());

		HashMap<String, Node> portNodes = new HashMap<String, Node>();
		HashMap<String, Port> ports = new HashMap<String, Port>();

		// Get current node ports
		Iterable<Relationship> relations = switchNode.getRelationships();
		for (Relationship relation : relations) {
			Node portNode = relation.getEndNode();
			Boolean isPort = false;
			for (Label label : portNode.getLabels()) {
				if (label.name().equals("Port"))
					isPort = true;
			}

			if (isPort) {
				portNodes.put((String) portNode.getProperty(ID_PROPERTY),
						portNode);
			}
		}

		// Merge Switch -> Node Switch
		List<Port> portList = sw.getPorts();
		for (Port port : portList) {
			if (portNodes.containsKey(port.getPortId())) {
				setNodePortProperties(portNodes.get(port.getPortId()), port);
			} else {
				// if the port does not exit, create new
				Node portNode = graphDb.createNode(Labels.PORT_LABEL);
				setNodePortProperties(portNode, port);
				switchNode.createRelationshipTo(portNode, RelTypes.HAS);
				System.out.println("[NEO4J DEBUG] Port Added // node: '"
						+ portNode.getId() + "'");
			}
			ports.put(port.getPortId(), port);
		}

		// Merge Node Switch -> Switch
		for (Node portNode : portNodes.values()) {
			if (!ports.containsKey((String) portNode.getProperty(ID_PROPERTY)))
				portNode.setProperty("enabled", false);
		}
	}

	private void getNodeSwitchProperties(Node switchNode, Switch sw) {
		sw.setSwId((String) switchNode.getProperty(ID_PROPERTY));
		sw.setEnabled((Boolean) switchNode.getProperty("enabled"));
		sw.setHardware((String) switchNode.getProperty("hardware"));
		sw.setManufacturer((String) switchNode.getProperty("manufacturer"));
		sw.setSoftware((String) switchNode.getProperty("software"));

		Iterable<Relationship> relations = switchNode.getRelationships();
		List<Port> portList = new ArrayList<Port>();
		for (Relationship relation : relations) {
			Node portNode = relation.getEndNode();
			Boolean isPort = false;
			for (Label label : portNode.getLabels()) {
				if (label.name().equals("Port"))
					isPort = true;
			}
			if (isPort) {
				Port port = new Port();
				getNodePortProperties(portNode, port);
				portList.add(port);
			}
		}
		sw.setPorts(portList);
	}

	private void setNodePortProperties(Node portNode, Port port) {
		portNode.setProperty(ID_PROPERTY, port.getPortId());
		portNode.setProperty("enabled", port.getEnabled());
		portNode.setProperty("MAC", port.getMac());
		portNode.setProperty("name", port.getName());
	}

	private void getNodePortProperties(Node portNode, Port port) {
		port.setEnabled((Boolean) portNode.getProperty("enabled"));
		port.setMac((String) portNode.getProperty("MAC"));
		port.setName((String) portNode.getProperty("name"));
		port.setPortId((String) portNode.getProperty(ID_PROPERTY));
	}

	public boolean existSwitch(String swId) {
		return graphDb
				.findNodesByLabelAndProperty(Labels.SWITCH_LABEL, ID_PROPERTY,
						swId).iterator().hasNext();
	}

	public void addSwitch(Switch sw) {
		try {
			updateSwitch(sw);
		} catch (SwitchNotFoundException e) {
			// Create Switch
			Node switchNode = graphDb.createNode(Labels.SWITCH_LABEL);
			getManagerNode().createRelationshipTo(switchNode, RelTypes.ELEMENT);
			setNodeSwitchProperties(switchNode, sw);
			System.out.println("[NEO4J DEBUG] Switch Added // node: '"
					+ switchNode.getId() + "'");

			// Get and create ports
			List<Port> portList = sw.getPorts();
			for (Port port : portList) {
				Node portNode = graphDb.createNode(Labels.SWITCH_LABEL);
				setNodePortProperties(portNode, port);
				switchNode.createRelationshipTo(portNode, RelTypes.HAS);
				System.out.println("[NEO4J DEBUG] Port Added // node: '"
						+ portNode.getId() + "'");
			}
		}
	}

	public Switch getSwitch(String swId) throws SwitchNotFoundException {
		ResourceIterator<Node> switchNodes = graphDb
				.findNodesByLabelAndProperty(Labels.SWITCH_LABEL, ID_PROPERTY,
						swId).iterator();
		if (!switchNodes.hasNext())
			throw new SwitchNotFoundException(swId);

		Node switchNode = switchNodes.next();
		Switch sw = new Switch();
		getNodeSwitchProperties(switchNode, sw);
		return sw;
	}

	public void updateSwitch(Switch sw) throws SwitchNotFoundException {
		ResourceIterator<Node> switchNodes = graphDb
				.findNodesByLabelAndProperty(Labels.SWITCH_LABEL, ID_PROPERTY,
						sw.getSwId()).iterator();
		if (!switchNodes.hasNext())
			throw new SwitchNotFoundException(sw);

		Node switchNode = switchNodes.next();
		setNodeSwitchProperties(switchNode, sw);
		System.out.println("[NEO4J DEBUG] Switch updated // node: '"
				+ switchNode.getId() + "'");
	}

	public SwitchCollection getAllSwitches() {
		Iterable<Relationship> switchRelations = getManagerNode()
				.getRelationships();
		List<Switch> switchList = new ArrayList<Switch>();

		for (Relationship relation : switchRelations) {
			Node switchNode = relation.getEndNode();
			Boolean isSwitch = false;
			for (Label label : switchNode.getLabels()) {
				if (label.name().equals("Switch")) {
					isSwitch = true;
				}
			}

			if (isSwitch) {
				Switch sw = new Switch();
				getNodeSwitchProperties(switchNode, sw);
				switchList.add(sw);
			}
		}
		SwitchCollection switchCollection = new SwitchCollection();
		switchCollection.setSwitches(switchList);
		return switchCollection;
	}

	public void disableSwitch(String swId) throws SwitchNotFoundException {
		ResourceIterator<Node> switchNode = graphDb.findNodesByLabelAndProperty(
				Labels.SWITCH_LABEL, ID_PROPERTY, swId).iterator();

		if (!switchNode.hasNext())
			throw new SwitchNotFoundException(swId);
		switchNode.next().setProperty("enabled", false);
	}

	public void enableSwitch(String swId) throws SwitchNotFoundException {
		ResourceIterator<Node> switchNode = graphDb.findNodesByLabelAndProperty(
				Labels.SWITCH_LABEL, ID_PROPERTY, swId).iterator();

		if (!switchNode.hasNext())
			throw new SwitchNotFoundException(swId);
		switchNode.next().setProperty("enabled", true);
	}
}
