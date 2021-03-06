package dxat.controller.module;

import com.google.gson.Gson;
import dxat.controller.module.events.IFlowEvents;
import dxat.controller.module.events.ILinkEvents;
import dxat.controller.module.events.ISwitchEvents;
import dxat.controller.module.events.ITerminalEvents;
import dxat.controller.module.exceptions.*;
import dxat.controller.module.listeners.SwitchListener;
import dxat.controller.module.pojos.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Xavier Arteaga <xavier.arteaga @ estudiant.upc.edu>
 */
public class ModuleServerThread implements Runnable {
    /**
     * Embedded class for manage each connected AppServer
     */
    private class AppServer implements Runnable {
        /**
         * Socket used for connect the AppServer
         */
        private Socket socket;

        /**
         * Buffered reader for incoming commands
         */
        private BufferedReader reader;

        /**
         * Print writer for outgoing events
         */
        private PrintWriter writer;

        public AppServer(Socket socket) throws IOException {
            // Set the socket
            this.socket = socket;

            // Set reader and write: Used when we expect text
            reader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(
                    socket.getOutputStream()));
        }

        public String getKey() {
            return socket.getInetAddress().getHostAddress() + ":"
                    + socket.getPort();
        }

        public void sendControllerEvent(ControllerEvent controllerEvent) {
            if (writer == null) {
                return;
            }

            try {
                writer.println(new Gson().toJson(controllerEvent) + "\n");
                writer.flush();

                System.out.println("[Sending Event to key '" + getKey() + "'] "
                        + controllerEvent.getEvent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Main thread of the AppServer. It only reads requests from the
         * AppServer. When the socket is closed the thread removes it self of
         * the list.
         */
        @Override
        public void run() {
            while (!socket.isClosed() && socket.isConnected()
                    && !socket.isInputShutdown() && !socket.isOutputShutdown()) {
                try {
                    // Read line command
                    String newLine = reader.readLine();

                    if (newLine != null) {
                        // Convert new line to a server request
                        ServerRequest serverRequest = new Gson().fromJson(
                                newLine, ServerRequest.class);

                        if (serverRequest != null) {
                            // Print the new request
                            System.out.println("[NEW REQUEST] "
                                    + serverRequest.getRequest());

                            // Process server request
                            processRequest(serverRequest);
                        }
                    }
                } catch (SocketException e) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            // Remove AppServer from the server list
            serversMap.remove(getKey());
            System.out.println("[REMOVING CONNECTION] AppServer with key '"
                    + getKey() + "'");
        }

        /**
         * Process server requests and reply to the same AppServer with an
         * event.
         *
         * @param serverRequest is the server request.
         */
        private void processRequest(ServerRequest serverRequest) {
            // Create controller event
            ControllerEvent controllerEvent = new ControllerEvent();
            controllerEvent.setTimestamp(new Date().getTime());

            // Process request from server
            try {
                if (serverRequest.getRequest().equals(
                        IServerRequests.TERMINALS_REQUEST)) {
                    // Terminal collection request
                    TerminalCollection terminalCollection = DxatAppModule
                            .getInstance().getDeviceListener()
                            .getAllTerminalCollection();
                    controllerEvent.setObject(new Gson()
                            .toJson(terminalCollection));
                    controllerEvent
                            .setEvent(ITerminalEvents.TERMINALS_COLLECTION);
                } else if (serverRequest.getRequest().equals(
                        IServerRequests.LINKS_REQUEST)) {
                    // Link collection requests
                    LinkCollection linkCollection = DxatAppModule.getInstance()
                            .getLinkListener().getAllLinkCollection();
                    controllerEvent
                            .setObject(new Gson().toJson(linkCollection));
                    controllerEvent.setEvent(ILinkEvents.LINKS_COLLECTION);
                } else if (serverRequest.getRequest().equals(
                        IServerRequests.SWITCHES_REQUEST)) {
                    // Switch collection request
                    SwitchListener switchListener = DxatAppModule.getInstance()
                            .getSwitchListener();
                    SwitchCollection switchCollection = switchListener
                            .getAllSwitchCollection();
                    controllerEvent.setObject(new Gson()
                            .toJson(switchCollection));
                    controllerEvent.setEvent(ISwitchEvents.SWITCHES_COLLECTION);
                } else if (serverRequest.getRequest().equals(
                        IServerRequests.PUSH_FLOW_REQUEST)) {
                    // Flow push request
                    Flow flow = new Gson().fromJson(serverRequest.getObject(),
                            Flow.class);
                    DeployedFlow deployedFlow = DxatAppModule.getInstance()
                            .getFlowPusherManager().pushFlow(flow);
                    controllerEvent.setObject(new Gson().toJson(deployedFlow));
                    controllerEvent.setEvent(IFlowEvents.PUSH_FLOW_SUCCESS); // In
                } else if (serverRequest.getRequest().equals(
                        IServerRequests.DELETE_FLOW_REQUEST)) {
                    // Delete flow request
                    Flow flow = new Gson().fromJson(serverRequest.getObject(),
                            Flow.class);
                    Boolean found = DxatAppModule.getInstance()
                            .getFlowPusherManager().deleteFlow(flow);
                    if (found) {
                        // If the entry has been found
                        controllerEvent
                                .setEvent(IFlowEvents.DELETE_FLOW_SUCCESS);
                        controllerEvent.setObject(new Gson().toJson(flow));
                    } else {
                        // If no entry found
                        controllerEvent
                                .setEvent(IFlowEvents.DELETE_FLOW_FAILED);
                        controllerEvent.setObject(new Gson().toJson(flow));
                    }
                } else if (serverRequest.getRequest().equals(
                        IServerRequests.DELETE_ALL_FLOWS_REQUEST)) {
                    // Delete all flows request
                    DxatAppModule.getInstance().getFlowPusherManager()
                            .deleteAllFlows();
                    controllerEvent.setEvent(IFlowEvents.ALL_FLOWS_DELETED);
                } else if (serverRequest.getRequest().equals(
                        IServerRequests.FLOWS_REQUEST)) {
                    DeployedFlowCollection deployedFlowCollection = DxatAppModule
                            .getInstance().getFlowPusherManager()
                            .getDeployedFlows();
                    controllerEvent.setObject(new Gson()
                            .toJson(deployedFlowCollection));
                    controllerEvent.setEvent(IFlowEvents.FLOWS_COLLECTION);
                }
            } catch (DstTerminalNotFoundException e) {
                // If the destination IPv4 terminal is not found in the topology
                controllerEvent.setTimestamp(new Date().getTime());
                controllerEvent.setObject(e.getMessage());
                controllerEvent
                        .setEvent(IFlowEvents.PUSH_FLOW_DST_TERMINAL_NOT_FOUND);
            } catch (SrcTerminalNotFoundException e) {
                // If the source IPv4 terminal is not found in the topology
                controllerEvent.setTimestamp(new Date().getTime());
                controllerEvent.setObject(e.getMessage());
                controllerEvent
                        .setEvent(IFlowEvents.PUSH_FLOW_SRC_TERMINAL_NOT_FOUND);
            } catch (UnreachableTerminalsException e) {
                // If no path is possible between the desired terminals
                controllerEvent.setTimestamp(new Date().getTime());
                controllerEvent.setObject(e.getMessage());
                controllerEvent
                        .setEvent(IFlowEvents.PUSH_FLOW_UNREACHABLE_TERMINALS);
            } catch (IllegalFlowEntryException e) {
                // If it is not possible write a flow entry inside a switch
                controllerEvent.setTimestamp(new Date().getTime());
                controllerEvent.setObject(e.getMessage());
                controllerEvent
                        .setEvent(IFlowEvents.PUSH_FLOW_ILLEGAL_FLOW_ENTRY);
            } catch (FlowAlreadyExistsException e) {
                controllerEvent.setTimestamp(new Date().getTime());
                controllerEvent.setObject(e.getMessage());
                controllerEvent
                        .setEvent(IFlowEvents.PUSH_FLOW_FLOW_ALREADY_EXIST);
            }
            // Send the event to the AppServer
            broadcastControllerEvent(controllerEvent);
            //sendControllerEvent(controllerEvent);
        }
    }

    /**
     * Server Socket port
     */
    private int port;

    /**
     * List of connected AppServer to the controller
     */
    private HashMap<String, AppServer> serversMap;

    /**
     * Constructor of the Module Server Thread. Initialize socket
     *
     * @param port TCP port used for listen new connections to the controller
     *             (For AppServer)
     */
    public ModuleServerThread(int port) {
        super();

        // Set the port
        this.port = port;

        // Initialize server list
        serversMap = new HashMap<String, AppServer>();

    }

    /**
     * @param controllerEvent Controller event to send to be broad casted to all the
     *                        connected AppServer
     */
    public void broadcastControllerEvent(ControllerEvent controllerEvent) {
        List<AppServer> appServerList = new ArrayList<AppServer>(
                serversMap.values());
        for (AppServer appServer : appServerList) {
            appServer.sendControllerEvent(controllerEvent);
        }
    }

    /**
     * Main thread of the server. It only listen connections and put them in the
     * list.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        ServerSocket sSocket;
        // Create Server Socket
        try {
            sSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Forever do...
        while (true) {
            try {
                // Wait connection
                Socket socket = sSocket.accept();

                // Create new AppServer instance
                AppServer appServer = new AppServer(socket);

                if (!serversMap.containsKey(appServer.getKey())) {
                    // Add the app server to the hashmap
                    serversMap.put(appServer.getKey(), appServer);

                    // Create threat and start
                    String threadId = "App server at '" + appServer.getKey()
                            + "'";
                    Thread appServerThread = new Thread(appServer, threadId);
                    appServerThread.start();

                    // Inform of the new connection
                    System.out.println("[NEW CONNECTION] AppServer with key '"
                            + appServer.getKey() + "'");
                } else {
                    socket.close();
                    System.out
                            .println("[NEW CONNECTION REFUSED] Already exist key: '"
                                    + appServer.getKey() + "'");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
