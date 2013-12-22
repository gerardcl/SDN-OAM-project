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
import dxat.appserver.topology.exceptions.SwitchExistsException;
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

	private List<DbUpdate> updateNodeSwitchProperties(Node switchNode, Switch sw) {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		Boolean enabled = (Boolean) switchNode.getProperty("enabled");
		String hardware = (String) switchNode.getProperty("hardware");
		String manufacturer = (String) switchNode.getProperty("manufacturer");
		String software = (String) switchNode.getProperty("software");

		if (!enabled.equals(sw.getEnabled())) {
			DbUpdate update = new DbUpdate();
			update.setInventoryId(sw.getSwId());
			update.setPropertyId("enabled");
			update.setLegacyValue(enabled.toString());
			update.setNewValue(sw.getEnabled().toString());
			updates.add(update);
			switchNode.setProperty("enabled", sw.getEnabled());
		}

		if (!hardware.equals(sw.getHardware())) {
			DbUpdate update = new DbUpdate();
			update.setInventoryId(sw.getSwId());
			update.setPropertyId("hardware");
			update.setLegacyValue(hardware);
			update.setNewValue(sw.getHardware());
			updates.add(update);
			switchNode.setProperty("hardware", sw.getHardware());
		}

		if (!manufacturer.equals(sw.getManufacturer())) {
			DbUpdate update = new DbUpdate();
			update.setInventoryId(sw.getSwId());
			update.setPropertyId("manufacturer");
			update.setLegacyValue(manufacturer);
			update.setNewValue(sw.getManufacturer());
			updates.add(update);
			switchNode.setProperty("manufacturer", sw.getManufacturer());
		}

		if (!software.equals(sw.getSoftware())) {
			DbUpdate update = new DbUpdate();
			update.setInventoryId(sw.getSwId());
			update.setPropertyId("software");
			update.setLegacyValue(software);
			update.setNewValue(sw.getSoftware());
			updates.add(update);
			switchNode.setProperty("software", sw.getSoftware());
		}

		HashMap<String, Node> portNodes = new HashMap<String, Node>();
		HashMap<String, Port> ports = new HashMap<String, Port>();

		// Get current node ports
		Iterable<Relationship> relations = switchNode.getRelationships();
		for (Relationship relation : relations) {
			Node portNode = relation.getEndNode();
			if (portNode.hasLabel(Labels.PORT_LABEL)) {
				portNodes.put((String) portNode.getProperty(ID_PROPERTY),
						portNode);
			}
		}

		// Merge Switch -> Node Switch
		List<Port> portList = sw.getPorts();
		for (Port port : portList) {
			if (portNodes.containsKey(port.getPortId())) {
				updates.addAll(updateNodePortProperties(
						portNodes.get(port.getPortId()), port));
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
			String portId = (String) portNode.getProperty(ID_PROPERTY);
			enabled = (Boolean) portNode.getProperty("enabled");
			if (!ports.containsKey(portId) && enabled) {
				DbUpdate update = new DbUpdate();
				update.setInventoryId(portId);
				update.setLegacyValue(enabled.toString());
				update.setNewValue("false");
				update.setPropertyId("enabled");
				updates.add(update);
				portNode.setProperty("enabled", false);
			}
		}
		return updates;
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

	private List<DbUpdate> updateNodePortProperties(Node portNode, Port port) {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();
		Boolean enabled = (Boolean) portNode.getProperty("enabled");
		String mac = (String) portNode.getProperty("MAC");
		String name = (String) portNode.getProperty("name");

		if (!enabled.equals(port.getEnabled())) {
			DbUpdate update = new DbUpdate();
			update.setInventoryId(port.getPortId());
			update.setLegacyValue(enabled.toString());
			update.setNewValue(port.getEnabled().toString());
			update.setPropertyId("enabled");
			updates.add(update);
			portNode.setProperty("enabled", port.getEnabled());
		}

		if (!mac.equals(port.getMac())) {
			DbUpdate update = new DbUpdate();
			update.setInventoryId(port.getPortId());
			update.setLegacyValue(mac);
			update.setNewValue(port.getMac());
			update.setPropertyId("MAC");
			updates.add(update);
			portNode.setProperty("MAC", port.getMac());
		}

		if (!name.equals(port.getName())) {
			DbUpdate update = new DbUpdate();
			update.setInventoryId(port.getPortId());
			update.setLegacyValue(name);
			update.setNewValue(port.getName());
			update.setPropertyId("name");
			updates.add(update);
			portNode.setProperty("name", port.getName());
		}

		return updates;
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

	public List<DbUpdate> addSwitch(Switch sw) throws SwitchExistsException {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();
		if (existSwitch(sw.getSwId()))
			throw new SwitchExistsException(
					"Trying to add a switch whose identifier already exists('"
							+ sw.getSwId() + "')");

		// Create Switch
		Node switchNode = graphDb.createNode(Labels.SWITCH_LABEL);
		getManagerNode().createRelationshipTo(switchNode, RelTypes.ELEMENT);
		setNodeSwitchProperties(switchNode, sw);
		DbUpdate update = new DbUpdate();
		update.setInventoryId(sw.getSwId());
		update.setLegacyValue("NONE");
		update.setNewValue("NEW");
		update.setPropertyId("SWITCH");
		updates.add(update);

		// Get and create ports
		List<Port> portList = sw.getPorts();
		for (Port port : portList) {
			Node portNode = graphDb.createNode(Labels.SWITCH_LABEL);
			setNodePortProperties(portNode, port);
			switchNode.createRelationshipTo(portNode, RelTypes.HAS);
			update = new DbUpdate();
			update.setInventoryId(port.getPortId());
			update.setLegacyValue("NONE");
			update.setNewValue("NEW");
			update.setPropertyId("PORT");
		}
		return updates;
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

	public List<DbUpdate> updateSwitch(Switch sw)
			throws SwitchNotFoundException {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();
		ResourceIterator<Node> switchNodes = graphDb
				.findNodesByLabelAndProperty(Labels.SWITCH_LABEL, ID_PROPERTY,
						sw.getSwId()).iterator();
		if (!switchNodes.hasNext())
			throw new SwitchNotFoundException(sw);

		Node switchNode = switchNodes.next();
		updates.addAll(updateNodeSwitchProperties(switchNode, sw));
		return updates;
	}

	public SwitchCollection getAllSwitches() {
		Iterable<Relationship> switchRelations = getManagerNode()
				.getRelationships();
		List<Switch> switchList = new ArrayList<Switch>();

		for (Relationship relation : switchRelations) {
			Node switchNode = relation.getEndNode();
			if (switchNode.hasLabel(Labels.SWITCH_LABEL)) {
				Switch sw = new Switch();
				getNodeSwitchProperties(switchNode, sw);
				switchList.add(sw);
			}
		}
		SwitchCollection switchCollection = new SwitchCollection();
		switchCollection.setSwitches(switchList);
		return switchCollection;
	}

	public List<DbUpdate> disableSwitch(String swId)
			throws SwitchNotFoundException {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		ResourceIterator<Node> switchNodes = graphDb
				.findNodesByLabelAndProperty(Labels.SWITCH_LABEL, ID_PROPERTY,
						swId).iterator();

		if (!switchNodes.hasNext())
			throw new SwitchNotFoundException(swId);

		Node switchNode = switchNodes.next();
		Boolean enabled = (Boolean) switchNode.getProperty("enabled");
		if (enabled.equals(true)) {
			switchNode.setProperty("enabled", false);
			DbUpdate update = new DbUpdate();
			update.setInventoryId(swId);
			update.setPropertyId("enabled");
			update.setLegacyValue("true");
			update.setNewValue("false");
			updates.add(update);
		}

		return updates;
	}

	public List<DbUpdate> enableSwitch(String swId)
			throws SwitchNotFoundException {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();

		ResourceIterator<Node> switchNodes = graphDb
				.findNodesByLabelAndProperty(Labels.SWITCH_LABEL, ID_PROPERTY,
						swId).iterator();

		if (!switchNodes.hasNext())
			throw new SwitchNotFoundException(swId);

		Node switchNode = switchNodes.next();
		Boolean enabled = (Boolean) switchNode.getProperty("enabled");
		if (enabled.equals(true)) {
			switchNode.setProperty("enabled", true);
			DbUpdate update = new DbUpdate();
			update.setInventoryId(swId);
			update.setPropertyId("enabled");
			update.setLegacyValue("false");
			update.setNewValue("true");
			updates.add(update);
		}

		return updates;
	}

	public List<DbUpdate> mergeCollection(SwitchCollection switchCollection) {
		List<DbUpdate> updates = new ArrayList<DbUpdate>();
		List<Switch> switchList = switchCollection.getSwitches();
		HashMap<String, Switch> switchMap = new HashMap<String, Switch>();

		for (Switch sw : switchList) {
			switchMap.put(sw.getSwId(), sw);
			try {
				updates.addAll(addSwitch(sw));
			} catch (SwitchExistsException e) {
				try {
					updates.addAll(updateSwitch(sw));
				} catch (SwitchNotFoundException e1) {
				}
			}
		}

		Iterable<Relationship> switchRelations = getManagerNode()
				.getRelationships();
		for (Relationship relation : switchRelations) {
			Node switchNode = relation.getEndNode();
			if (switchNode.hasLabel(Labels.SWITCH_LABEL)) {
				String swId = (String) switchNode.getProperty(ID_PROPERTY);
				if (!switchMap.containsKey(swId)) {
					try {
						updates.addAll(disableSwitch(swId));
					} catch (SwitchNotFoundException e) {
					}
				}
			}
		}

		return updates;
	}
}
