package dxat.appserver.realtime;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dxat.appserver.flows.FlowManager;
import dxat.appserver.realtime.events.IStatisticsEvent;
import dxat.appserver.realtime.interfaces.IServerRequests;
import dxat.appserver.realtime.pojos.ControllerEvent;
import dxat.appserver.realtime.pojos.DbUpdate;
import dxat.appserver.realtime.pojos.RealTimeEvent;
import dxat.appserver.realtime.pojos.ServerRequest;
import dxat.appserver.stat.StatManager;
import dxat.appserver.stat.pojos.StatCollection;
import dxat.appserver.topology.LinkManager;
import dxat.appserver.topology.SwitchManager;
import dxat.appserver.topology.TerminalManager;
import dxat.appserver.topology.exceptions.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class RealTimeThread implements Runnable {
    private String serverAddr = "";
    private int serverPort = -1;
    private Socket socket = null;
    private BufferedReader reader = null;
    private PrintWriter writer = null;

    // Topology Managers
    public RealTimeThread(String serverAddr, int serverPort) {
        //serverAddr = "147.83.118.254";
        this.serverAddr = serverAddr;
        this.serverPort = serverPort;
        System.out.println("************* CREATING REAL TIME THREAD *************");
        System.out.println("Controller IPv4: " + serverAddr);
        System.out.println("Controller port: " + serverPort);
        System.out.println("*****************************************************");
    }

    public void getSwitches() {
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setRequest(IServerRequests.SWITCHES_REQUEST);
        serverRequest.setObject(IServerRequests.SWITCHES_REQUEST);
        sendRequest(serverRequest);
    }

    public void getTerminals() {
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setRequest(IServerRequests.TERMINALS_REQUEST);
        serverRequest.setObject(IServerRequests.TERMINALS_REQUEST);
        sendRequest(serverRequest);
    }

    public void getFlows() {
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setRequest(IServerRequests.FLOWS_REQUEST);
        serverRequest.setObject(IServerRequests.FLOWS_REQUEST);
        sendRequest(serverRequest);
    }

    public void getLinks() {
        ServerRequest serverRequest = new ServerRequest();
        serverRequest.setRequest(IServerRequests.LINKS_REQUEST);
        serverRequest.setObject(IServerRequests.LINKS_REQUEST);
        sendRequest(serverRequest);
    }

    public void sendRequest(ServerRequest serverRequest) {
        try {
            writer.println(new Gson().toJson(serverRequest));
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
        realTimeEvent.setTimestamp(controllerEvent.getTimestamp());
        try {
            realTimeEvent.getUpdates()
                    .addAll(TerminalManager.getInstance().processEvent(
                            controllerEvent));
            realTimeEvent.getUpdates().addAll(SwitchManager.getInstance().processEvent(controllerEvent));
            realTimeEvent.getUpdates().addAll(LinkManager.getInstance().processEvent(controllerEvent));
            realTimeEvent.getUpdates().addAll(FlowManager.getInstance().processEvent(controllerEvent));
        } catch (CannotOpenDataBaseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (PortNotFoundException e) {
            printException(e);
        } catch (TerminalNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SwitchNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (LinkNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (LinkKeyBadFormatException e) {
            printException(e);
        }

        if (realTimeEvent.getUpdates().size() > 0) {
            RealTimeManager.getInstance().broadcast(
                    new Gson().toJson(realTimeEvent));
        }

        if (controllerEvent.getEvent().equals(IStatisticsEvent.PUSH_STATS)) {
            StatManager statManager = StatManager.getInstance();

            StatCollection statCollection = (StatCollection) new Gson().fromJson(controllerEvent.getObject(),
                    StatCollection.class);
            try {
                statManager.pushStat(statCollection);
            } catch (IOException e) {
                System.out
                        .println("[EXCEPTION PUSHING STAT] " + e.getMessage());
            }
        }
    }

    public void run() {
        // Forever while
        while (true) {
            // Connect and get writer and reader
            int trialNumber = 0;

            // Try connect while is not connected
            while (socket == null) {
                try {
                    System.out.println("Trying to connect to the OF Controller (trial " + trialNumber + ") ...");
                    trialNumber++;

                    // Try create the socket
                    socket = new Socket(serverAddr, serverPort);

                    // If socket created, set writer and reader
                    reader = new BufferedReader(new InputStreamReader(
                            this.socket.getInputStream()));
                    writer = new PrintWriter(new OutputStreamWriter(
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
            System.out.println(Thread.currentThread().getId() + ": Connected to the controller ... Free memory: " + Runtime.getRuntime().freeMemory() / 1000000);

            // Get current Hosts, Links and Switches
            getSwitches();
            getLinks();
            getTerminals();
            getFlows();

            while (!socket.isClosed() && socket.isConnected() && !socket.isInputShutdown() && !socket.isOutputShutdown()
                    && socket.isBound() ) {
                try {
                    String line = new String(reader.readLine());
                    if (line != null) {
                        //System.out.println(line);
                        ControllerEvent controllerEvent = new Gson().fromJson(line, ControllerEvent.class);
                        if (controllerEvent != null)
                            processEvent(controllerEvent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e){
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            System.out.println(Thread.currentThread().getId() + ": Disconnected from the controller ... Free memory: " + Runtime.getRuntime().freeMemory() / 1000000);
            socket = null;
        }
    }

    private void printException(Exception e) {
        System.out.println("DXAT CLIENT: Error: '" + e.getMessage() + "'");
    }
}
