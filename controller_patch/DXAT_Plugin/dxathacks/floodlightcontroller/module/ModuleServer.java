package dxathacks.floodlightcontroller.module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.google.gson.Gson;

import dxathacks.floodlightcontroller.module.listeners.DeviceListener;
import dxathacks.floodlightcontroller.module.listeners.LinkListener;
import dxathacks.floodlightcontroller.module.listeners.SwitchListener;
import dxathacks.floodlightcontroller.pojos.Command;
import dxathacks.floodlightcontroller.pojos.ControllerInterface;
import dxathacks.floodlightcontroller.pojos.Host;
import dxathacks.floodlightcontroller.pojos.Link;
import dxathacks.floodlightcontroller.pojos.PortStatisticsCollection;
import dxathacks.floodlightcontroller.pojos.Switch;

/**
 * @author Xavier Arteaga
 * 
 */
public class ModuleServer implements Runnable, ControllerInterface {
	// Socket server
	private ServerSocket sSocket;

	// Listeners and interfaces
	private DeviceListener deviceListener = null;
	private LinkListener linkListener = null;
	private SwitchListener switchListener = null;

	// Socket reader and writer
	private BufferedReader reader = null;
	private PrintWriter writer = null;

	// Statistics module
	private StatisticsModule statisticsModule = null;
	private Thread statisticsThread = null;

	public ModuleServer(int port) {
		super();

		// Create Server Socket
		try {
			sSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public void setListeners(DeviceListener deviceListener,
			LinkListener linkListener, SwitchListener switchListener) {
		this.deviceListener = deviceListener;
		this.linkListener = linkListener;
		this.switchListener = switchListener;
	}

	private void sendCommand(Command cmd) {
		try {
			this.writer.write(new Gson().toJson(cmd) + "\n");
			this.writer.flush();
		} catch (Exception e) {
			this.printException(e);
		}
	}

	public void run() {
		while (!sSocket.isClosed()) {
			try {
				// Wait connection
				Socket socket = sSocket.accept();

				// Set reader and write: Used when we expect text
				this.reader = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				this.writer = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream()));

				// Start Statistics
				statisticsModule = new StatisticsModule(this,
						switchListener.getSwitchService());
				statisticsThread = new Thread(statisticsModule,
						"Statistics Module");
				statisticsThread.start();
				
				// Read line command
				Command cmd = new Gson().fromJson(reader.readLine(),
						Command.class);
				if (cmd.getEvent().equals(Command.GET_HOSTS)) {
					this.deviceListener.updateHosts();
				} else if (cmd.getEvent().equals(Command.GET_LINKS)) {
					this.linkListener.updateLinks();
				} else if (cmd.getEvent().equals(Command.GET_SWITCHES)) {
					this.switchListener.updateSwitches();
				}
			} catch (Exception e) {
				this.printException(e);
			}
		}
		try {
			sSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void printException(Exception e) {
		System.out.println("DXAT MODULE: Error: '" + e.getMessage() + "'");
	}

	public void addSwitch(Switch sw) {
		Command cmd = new Command();
		cmd.setEvent(Command.ADD_SWITCH);
		cmd.setSource(sw.getClass().toString());
		cmd.setObject(new Gson().toJson(sw));
		this.sendCommand(cmd);
	}

	public void updateSwitch(Switch sw) {
		Command cmd = new Command();
		cmd.setEvent(Command.UPDATE_SWITCH);
		cmd.setSource(sw.getClass().toString());
		cmd.setObject(new Gson().toJson(sw));
		this.sendCommand(cmd);
	}

	public void deleteSwitch(String swId) {
		Command cmd = new Command();
		cmd.setEvent(Command.DELETE_SWITCH);
		cmd.setSource(String.class.toString());
		cmd.setObject(swId);
		this.sendCommand(cmd);
	}

	public void addLink(Link lnk) {
		Command cmd = new Command();
		cmd.setEvent(Command.ADD_LINK);
		cmd.setSource(lnk.getClass().toString());
		cmd.setObject(new Gson().toJson(lnk));
		this.sendCommand(cmd);
	}

	public void updateLink(Link lnk) {
		Command cmd = new Command();
		cmd.setEvent(Command.UPDATE_LINK);
		cmd.setSource(lnk.getClass().toString());
		cmd.setObject(new Gson().toJson(lnk));
		this.sendCommand(cmd);
	}

	public void deleteLink(Link lnk) {
		Command cmd = new Command();
		cmd.setEvent(Command.DELETE_LINK);
		cmd.setSource(lnk.getClass().toString());
		cmd.setObject(new Gson().toJson(lnk));
		this.sendCommand(cmd);
	}

	@Override
	public void addHost(Host host) {
		Command cmd = new Command();
		cmd.setEvent(Command.ADD_HOST);
		cmd.setSource(host.getClass().toString());
		cmd.setObject(new Gson().toJson(host));
		this.sendCommand(cmd);
	}

	@Override
	public void updateHost(Host host) {
		Command cmd = new Command();
		cmd.setEvent(Command.UPDATE_HOST);
		cmd.setSource(host.getClass().toString());
		cmd.setObject(new Gson().toJson(host));
		this.sendCommand(cmd);
	}

	@Override
	public void deleteHost(Host host) {
		Command cmd = new Command();
		cmd.setEvent(Command.DELETE_HOST);
		cmd.setSource(host.getClass().toString());
		cmd.setObject(new Gson().toJson(host));
		this.sendCommand(cmd);
	}

	@Override
	public void pushStatistics(PortStatisticsCollection stats) {
		Command cmd = new Command();
		cmd.setEvent(Command.PUSH_STATS);
		cmd.setSource(stats.getClass().toString());
		cmd.setObject(new Gson().toJson(stats));
		this.sendCommand(cmd);
	}

}
