package dxat.appserver.realtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.gson.Gson;

import dxat.appserver.topology.LinkManager;
import dxat.appserver.topology.SwitchManager;
import dxat.appserver.topology.TerminalManager;
import dxat.appserver.topology.pojos.Command;
import dxat.appserver.topology.pojos.Flow;
import dxat.appserver.topology.pojos.Link;
import dxat.appserver.topology.pojos.Switch;
import dxat.appserver.topology.pojos.Terminal;

public class RealTimeThread implements Runnable {
	private String serverAddr = "";
	private int serverPort = -1;
	private Socket socket = null;
	private BufferedReader reader = null;
	private PrintWriter writer = null;

	// Topology Managers
	public RealTimeThread(String serverAddr, int serverPort) {
		this.serverAddr = serverAddr;
		this.serverPort = serverPort;
	}

	public void getSwitches() {
		Command cmd = new Command();
		cmd.setEvent(Command.GET_SWITCHES);
		cmd.setObject("ALL_SWITCHES");
		this.sendCommand(cmd);
	}

	public void getHosts() {
		Command cmd = new Command();
		cmd.setEvent(Command.GET_TERMINALS);
		cmd.setObject("ALL_HOSTS");
		this.sendCommand(cmd);
	}

	public void getLinks() {
		Command cmd = new Command();
		cmd.setEvent(Command.GET_LINKS);
		cmd.setObject("ALL_LINKS");
		this.sendCommand(cmd);
	}

	public void pushFlow(Flow flow) {
		Command cmd = new Command();
		cmd.setEvent(Command.PUSH_FLOW);
		cmd.setObject(new Gson().toJson(flow));
		this.sendCommand(cmd);
	}

	private void sendCommand(Command cmd) {
		try {
			this.writer.write(new Gson().toJson(cmd) + "\n");
			this.writer.flush();
		} catch (Exception e) {
			this.printException(new Exception("Exception sending command (" + e
					+ ")"));
		}
	}

	private void processCommand(Command cmd) {
		// Switches Events
		try {
			if (cmd.getEvent().equals(Command.ADD_SWITCH)) {
				SwitchManager.getInstance().addSwitch(
						new Gson().fromJson(cmd.getObject(), Switch.class));
			} else if (cmd.getEvent().equals(Command.DISABLE_SWITCH)) {
				SwitchManager.getInstance().disableSwitch(cmd.getObject());
			} else if (cmd.getEvent().equals(Command.UPDATE_SWITCH)) {
				SwitchManager.getInstance().updateSwitch(
						new Gson().fromJson(cmd.getObject(), Switch.class));
				// Links Events
			} else if (cmd.getEvent().equals(Command.ADD_LINK)) {
				LinkManager.getInstance().addLink(
						new Gson().fromJson(cmd.getObject(), Link.class));
			} else if (cmd.getEvent().equals(Command.DISABLE_LINK)) {
				Link lnk = new Gson().fromJson(cmd.getObject(), Link.class);
				LinkManager.getInstance().disableLink(lnk.getSrcPortId(),
						lnk.getDstPortId());
			} else if (cmd.getEvent().equals(Command.UPDATE_LINK)) {
				LinkManager.getInstance().updateLink(
						new Gson().fromJson(cmd.getObject(), Link.class));
				// Terminal Events
			} else if (cmd.getEvent().equals(Command.ADD_TERMINAL)) {
				TerminalManager.getInstance().addTerminal(
						new Gson().fromJson(cmd.getObject(), Terminal.class));
			} else if (cmd.getEvent().equals(Command.DISABLE_TERMINAL)) {
				Terminal terminal = new Gson().fromJson(cmd.getObject(),
						Terminal.class);
				TerminalManager.getInstance().disableTerminal(
						terminal.getTerminalId());
			} else if (cmd.getEvent().equals(Command.UPDATE_TERMINAL)) {
				TerminalManager.getInstance().updateTerminal(
						new Gson().fromJson(cmd.getObject(), Terminal.class));
			}/*
			 * else if (cmd.getEvent().equals(Command.PUSH_STATS)) {
			 * controller.pushStatistics(new Gson().fromJson(cmd.getObject(),
			 * PortStatisticsCollection.class)); }
			 */else {
				System.out.println("WARNING!! Command not implemented: '"
						+ cmd.getEvent() + "");
			}
		} catch (Exception e) {
			System.out.println("[EXCEPTION] " + e.getMessage());
		}
	}

	private void connect() {
		// Try connect while is not connected
		while (socket == null) {
			try {
				System.out
						.println("Trying to connect to the OF Controller ...");

				// Try create the soccket
				this.socket = new Socket(this.serverAddr, this.serverPort);

				// If socket created, set writer and reader
				this.reader = new BufferedReader(new InputStreamReader(
						this.socket.getInputStream()));
				this.writer = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream()));
			} catch (Exception e) {
				try {
					// If Exception, wait 2 seconds ...
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// Do nothing ...
				}
			}

		}
	}

	public void run() {
		// Forever while
		while (true) {
			// Connect and get writer and reader
			this.connect();
			System.out.println("Connected to the controller ...");

			// Get current Hosts, Links and Switches
			this.getSwitches();
			this.getLinks();
			this.getHosts();

			// Reader
			while (!socket.isClosed()) {
				try {
					processCommand(new Gson().fromJson(reader.readLine(),
							Command.class));
				} catch (IOException e) {
					try {
						System.out.println("[Exception Reading line]");
						socket.close();
					} catch (IOException e1) {
					}
				}
			}
			socket = null;
		}
	}

	private void printException(Exception e) {
		System.out.println("DXAT CLIENT: Error: '" + e.getMessage() + "'");
	}
}