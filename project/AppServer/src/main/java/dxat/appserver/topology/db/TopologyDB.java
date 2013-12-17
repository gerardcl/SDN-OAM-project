package dxat.appserver.topology.db;

import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.Label;

import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;

public abstract class TopologyDB {
	protected static enum RelTypes implements RelationshipType {
		ELEMENT, HAS, LINK,
	}
	
	protected static interface Managers {
		public static final String SWITCH_MANAGER = "SwitchManager";
		public static final String LINK_MANAGER = "LinkManager";
		public static final String TERMINAL_MANAGER = "TerminalManager";
	}
	
	protected static interface Labels {
		public static final Label SWITCH_LABEL = DynamicLabel.label("Switch");
		public static final Label PORT_LABEL = DynamicLabel.label("Port");
		public static final Label LINK_LABEL = DynamicLabel.label("Link");
		public static final Label TERMINAL_LABEL = DynamicLabel.label("Terminal");
	}
	
	protected static final String DB_PATH = "/home/xavier/neo4jDB";
	protected static final String ID_PROPERTY = "inventoryId";

	protected GraphDatabaseService graphDb = null;
	protected Transaction tx = null;
	
	protected abstract Node getManagerNode ();
	
	public void opendb() throws CannotOpenDataBaseException {
		try {
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
		} catch (Exception e) {
			throw new CannotOpenDataBaseException(
					"Database files could be in use (path: '" + DB_PATH
							+ "'). Exception detail: '" + e.getMessage() + "'");
		}

		tx = graphDb.beginTx();
	}
	
	public void closedb() {
		tx.success();
		tx.close();
		graphDb.shutdown();
	}
	
}
