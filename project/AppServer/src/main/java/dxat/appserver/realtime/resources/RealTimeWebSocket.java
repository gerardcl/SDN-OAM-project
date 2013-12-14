package dxat.appserver.realtime.resources;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound; 

import dxat.appserver.realtime.RealTimeManager;
import dxat.appserver.realtime.interfaces.IRealTimeSuscriber;

public class RealTimeWebSocket extends WebSocketServlet {
	private static final long serialVersionUID = 1L;

	private List<WsOutbound> connections = new ArrayList<WsOutbound>();

	@Override
	protected StreamInbound createWebSocketInbound(String protocol,
			HttpServletRequest arg1) {
		return new DefaultWebSocket();
	}

	@Override
	protected String selectSubProtocol(List<String> subProtocols) {
		System.out.println("Sub-protocol: " + subProtocols);
		return null;
	}

	@Override
	protected boolean verifyOrigin(String origin) {
		System.out.println("Origin: " + origin);
		return true; // Check if the origin is your own server, otherwise there
						// is an outsider using your websocket
	}

	private class DefaultWebSocket extends MessageInbound implements IRealTimeSuscriber{
		private WsOutbound connection;

		public DefaultWebSocket() {
			super();
		}

		@Override
		public void onOpen(WsOutbound connection) {
			this.connection = connection;
			connections.add(connection);
			RealTimeManager.getInstance().suscribe(this);
			System.out.println("Open connection; total connections: "
					+ connections.size());
		}

		@Override
		protected void onTextMessage(CharBuffer buffer) throws IOException {
			String data = buffer.toString();
			System.out.println("Message recieved: " + data);
			send(connection, data);
			broadcast("broadcasted: " + data); // This is used for all client
												// connecting to
			// server
		}

		@Override
		protected void onBinaryMessage(ByteBuffer buffer) throws IOException {

		}

		@Override
		protected void onClose(int status) {
			connections.remove(connection);
			RealTimeManager.getInstance().unsuscribe(this);
			System.out.println("Close connection; total connections: "
					+ connections.size());
		}

		@Override
		public String getSuscriberKey() {
			return "HASHCODE_"+connection.hashCode();
		}

		@Override
		public void sendEventMsg(String eventStr) {
			send(connection, eventStr);
		}
	}

	/**
	 * Send a message to all connections.
	 * 
	 * @param message
	 */
	private void broadcast(String message) {
		for (WsOutbound connection : connections) {
			try {
				CharBuffer buffer = CharBuffer.wrap(message);
				connection.writeTextMessage(buffer);
			} catch (IOException e) {
				// Ignore
			}
		}
	}

	/**
	 * Sends a message to the receiver
	 * 
	 * @param reciever
	 * @param message
	 */
	private void send(WsOutbound reciever, String message) {
		try {
			CharBuffer buffer = CharBuffer.wrap(message);
			reciever.writeTextMessage(buffer);
		} catch (IOException e) {
			// Ignore
		}
	}
}