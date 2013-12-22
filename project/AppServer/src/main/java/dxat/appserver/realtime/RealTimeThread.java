package dxat.appserver.realtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import com.google.gson.Gson;

import dxat.appserver.realtime.interfaces.IServerRequests;
import dxat.appserver.realtime.pojos.ControllerEvent;
import dxat.appserver.realtime.pojos.RealTimeEvent;
import dxat.appserver.realtime.pojos.ServerRequest;
import dxat.appserver.topology.TerminalManager;
import dxat.appserver.topology.db.DbUpdate;
import dxat.appserver.topology.exceptions.CannotOpenDataBaseException;
import dxat.appserver.topology.exceptions.PortNotFoundException;
import dxat.appserver.topology.exceptions.TerminalNotFoundException;
import dxat.appserver.topology.pojos.Command;
import dxat.appserver.topology.pojos.Flow;

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
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setRequest(IServerRequests.SWITCHES_REQUEST);
		serverRequest.setObject(IServerRequests.SWITCHES_REQUEST);
		sendrequest(serverRequest);
	}

	public void getTerminals() {
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setRequest(IServerRequests.TERMINALS_REQUEST);
		serverRequest.setObject(IServerRequests.TERMINALS_REQUEST);
		sendrequest(serverRequest);
	}

	public void getLinks() {
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setRequest(IServerRequests.LINKS_REQUEST);
		serverRequest.setObject(IServerRequests.LINKS_REQUEST);
		sendrequest(serverRequest);
	}

	public void pushFlow(Flow flow) {
		Command cmd = new Command();
		cmd.setEvent(Command.PUSH_FLOW);
		cmd.setObject(new Gson().toJson(flow));
	}

	private void sendrequest(ServerRequest serverRequest) {
		try {
			writer.write(new Gson().toJson(serverRequest) + "\n");
			writer.flush();
		} catch (Exception e) {
			printException(new Exception("Exception sending command (" + e
					+ ")"));
		}
	}

	private void processEvent(ControllerEvent controllerEvent) {
		RealTimeEvent realTimeEvent = new RealTimeEvent();
		realTimeEvent.setEvent(controllerEvent.getEvent());
		realTimeEvent.setUpdates(new ArrayList<DbUpdate>());
		try {
			realTimeEvent.getUpdates()
					.addAll(TerminalManager.getInstance().processEvent(
							controllerEvent));
		} catch (CannotOpenDataBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PortNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TerminalNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RealTimeManager.getInstance().broadcast(
				new Gson().toJson(realTimeEvent));
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
			getSwitches();
			getLinks();
			getTerminals();

			// Reader
			while (!socket.isClosed()) {
				try {
					/*
					 * processCommand(new Gson().fromJson(reader.readLine(),
					 * Command.class));
					 */
					processEvent(new Gson().fromJson(reader.readLine(),
							ControllerEvent.class));
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