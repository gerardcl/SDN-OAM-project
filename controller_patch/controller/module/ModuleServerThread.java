package dxat.controller.module;

import com.google.gson.Gson;
import dxat.controller.module.events.*;
import dxat.controller.module.exceptions.*;
import dxat.controller.module.listeners.SwitchListener;
import dxat.controller.module.pojos.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
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
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        }

        public void sendControllerEvent(ControllerEvent controllerEvent) {
            if (writer == null) {
                return;
            }

            try {
                writer.write(new Gson().toJson(controllerEvent) + "\n");
                writer.flush();
                System.out.println("[Sending Event to AppServer addr. '" + socket.getInetAddress().getHostAddress() +
                        "'] " + controllerEvent.getEvent());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * Main thread of the AppServer. It only reads requests from the AppServer. When the socket
         * is closed the thread removes it self of the list.
         */
        @Override
        public void run() {
            while (!socket.isClosed()) {
                try {
                    while (!socket.isClosed()) {
                        // Read line command
                        String newLine = reader.readLine();

                        // Convert new line to a server request
                        ServerRequest serverRequest = new Gson().fromJson(newLine, ServerRequest.class);

                        if (serverRequest != null) {
                            // Print the new request
                            System.out.println("[NEW REQUEST] " + serverRequest.getRequest());

                            // Process server request
                            processRequest(serverRequest);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        socket.close();
                    } catch (Exception e) {
                    }
                }
            }
            // Remove AppServer from the server list
            serversList.remove(this);
        }

        /**
         * Process server requests and reply to the same AppServer with an event.
         *
         * @param serverRequest is the server request.
         */
        private void processRequest(ServerRequest serverRequest) {
            // Create controller event
            ControllerEvent controllerEvent = new ControllerEvent();
            controllerEvent.setTimestamp(new Date().getTime());

            // Process request from server
            try {
                if (serverRequest.getRequest().equals(IServerRequests.TERMINALS_REQUEST)) {
                    // Terminal collection request
                    TerminalCollection terminalCollection = DxatAppModule.getInstance().getDeviceListener().getAllTerminalCollection();
                    controllerEvent.setObject(new Gson().toJson(terminalCollection));
                    controllerEvent.setEvent(ITerminalEvents.TERMINALS_COLLECTION);
                } else if (serverRequest.getRequest().equals(IServerRequests.LINKS_REQUEST)) {
                    // Link collection requests
                    LinkCollection linkCollection = DxatAppModule.getInstance().getLinkListener().getAllLinkCollection();
                    controllerEvent.setObject(new Gson().toJson(linkCollection));
                    controllerEvent.setEvent(ILinkEvents.LINKS_COLLECTION);
                } else if (serverRequest.getRequest().equals(IServerRequests.SWITCHES_REQUEST)) {
                    // Switch collection request
                    SwitchListener switchListener = DxatAppModule.getInstance().getSwitchListener();
                    SwitchCollection switchCollection = switchListener.getAllSwitchCollection();
                    controllerEvent.setObject(new Gson().toJson(switchCollection));
                    controllerEvent.setEvent(ISwitchEvents.SWITCHES_COLLECTION);
                } else if (serverRequest.getRequest().equals(
                        IServerRequests.PUSH_FLOW_REQUEST)) {
                    // Flow push request
                    Flow flow = new Gson().fromJson(serverRequest.getObject(), Flow.class);
                    DeployedFlow deployedFlow = DxatAppModule.getInstance().getFlowPusherManager().pushFlow(flow);
                    controllerEvent.setObject(new Gson().toJson(deployedFlow));
                    controllerEvent.setEvent(IFlowEvents.PUSH_FLOW_SUCCESS); // In case of fail are call the exceptions
                } else if (serverRequest.getRequest().equals(
                        IServerRequests.DELETE_FLOW_REQUEST)) {
                    // Delete flow request
                    Flow flow = new Gson().fromJson(serverRequest.getObject(), Flow.class);
                    Boolean found = DxatAppModule.getInstance().getFlowPusherManager().deleteFlow(flow);
                    if (found) {
                        // If the entry has been found
                        controllerEvent.setEvent(IFlowEvents.DELETE_FLOW_SUCCESS);
                        controllerEvent.setObject(new Gson().toJson(flow));
                    } else {
                        // If no entry found
                        controllerEvent.setEvent(IFlowEvents.DELETE_FLOW_FAILED);
                        controllerEvent.setObject(new Gson().toJson(flow));
                    }
                } else if (serverRequest.getRequest().equals(IServerRequests.DELETE_ALL_FLOWS_REQUEST)) {
                    // Delete all flows request
                    DxatAppModule.getInstance().getFlowPusherManager().deleteAllFlows();
                    controllerEvent.setEvent(IFlowEvents.ALL_FLOWS_DELETED);
                } else if (serverRequest.getRequest().equals(IServerRequests.FLOWS_REQUEST)){
                    
                    controllerEvent.setEvent(IFlowEvents.FLOWS_COLLECTION);
                }
            } catch (DstTerminalNotFoundException e) {
                // If the destination IPv4 terminal is not found in the topology
                controllerEvent.setTimestamp(new Date().getTime());
                controllerEvent.setObject(e.getMessage());
                controllerEvent.setEvent(IFlowEvents.PUSH_FLOW_DST_TERMINAL_NOT_FOUND);
            } catch (SrcTerminalNotFoundException e) {
                // If the source IPv4 terminal is not found in the topology
                controllerEvent.setTimestamp(new Date().getTime());
                controllerEvent.setObject(e.getMessage());
                controllerEvent.setEvent(IFlowEvents.PUSH_FLOW_SRC_TERMINAL_NOT_FOUND);
            } catch (UnreachableTerminalsException e) {
                // If no path is possible between the desired terminals
                controllerEvent.setTimestamp(new Date().getTime());
                controllerEvent.setObject(e.getMessage());
                controllerEvent.setEvent(IFlowEvents.PUSH_FLOW_UNREACHABLE_TERMINALS);
            } catch (IllegalFlowEntryException e) {
                // If it is not possible write a flow entry inside a switch
                controllerEvent.setTimestamp(new Date().getTime());
                controllerEvent.setObject(e.getMessage());
                controllerEvent.setEvent(IFlowEvents.PUSH_FLOW_ILLEGAL_FLOW_ENTRY);
            } catch (FlowAlreadyExistsException e) {
                controllerEvent.setTimestamp(new Date().getTime());
                controllerEvent.setObject(e.getMessage());
                controllerEvent.setEvent(IFlowEvents.PUSH_FLOW_FLOW_ALREADY_EXIST);
            }

            // Send the event to the AppServer
            sendControllerEvent(controllerEvent);
        }
    }

    /**
     * Socket used for connect the AppServer
     */
    private ServerSocket sSocket;

    /**
     * List of connected AppServer to the controller
     */
    private List<AppServer> serversList;

    /**
     * Constructor of the Module Server Thread. Initialize socket
     *
     * @param port TCP port used for listen new connections to the controller (For AppServer)
     */
    public ModuleServerThread(int port) {
        super();

        // Initialize server list
        serversList = new ArrayList<AppServer>();

        // Create Server Socket
        try {
            sSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * @param controllerEvent Controller event to send to be broad casted to all the connected AppServer
     */
    public void sendControllerEvent(ControllerEvent controllerEvent) {
        for (AppServer appServer : serversList) {
            appServer.sendControllerEvent(controllerEvent);
        }
    }

    /**
     * Main thread of the server. It only listen connections and put them in the list.
     */
    public void run() {
        while (!sSocket.isClosed()) {
            try {
                // Wait connection
                Socket socket = sSocket.accept();

                // Create new AppServer instance
                AppServer appServer = new AppServer(socket);
                serversList.add(appServer);
                Thread appServerThread = new Thread(appServer, "App server at '" +
                        socket.getInetAddress().getHostAddress() + "'");
                appServerThread.run();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    sSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
