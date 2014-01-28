package dxat.appserver.realtime.resources;

import dxat.appserver.flows.pojos.Flow;
import dxat.appserver.realtime.RealTimeManager;

public class PushPresetFlow {

    private static void pushSSFFlows() {
        Flow flow = new Flow();
        flow.setFlowId("ssh_h1");
        flow.setSrcIpAddr("192.168.0.105");
        flow.setSrcPort((short) 0);
        flow.setDstIpAddr("192.168.0.101");
        flow.setDstPort((short) 0);
        flow.setBandwidth(1e3);
        RealTimeManager.getInstance().pushFlow(flow);

        flow = new Flow();
        flow.setFlowId("ssh_h2");
        flow.setSrcIpAddr("192.168.0.105");
        flow.setSrcPort((short) 0);
        flow.setDstIpAddr("192.168.0.102");
        flow.setDstPort((short) 0);
        flow.setBandwidth(1e3);
        RealTimeManager.getInstance().pushFlow(flow);

        flow = new Flow();
        flow.setFlowId("ssh_h3");
        flow.setSrcIpAddr("192.168.0.105");
        flow.setSrcPort((short) 0);
        flow.setDstIpAddr("192.168.0.103");
        flow.setDstPort((short) 0);
        flow.setBandwidth(1e3);
        RealTimeManager.getInstance().pushFlow(flow);

        flow = new Flow();
        flow.setFlowId("ssh_h4");
        flow.setSrcIpAddr("192.168.0.105");
        flow.setSrcPort((short) 0);
        flow.setDstIpAddr("192.168.0.104");
        flow.setDstPort((short) 0);
        flow.setBandwidth(1e3);
        RealTimeManager.getInstance().pushFlow(flow);
    }

    private static void deleteSSHFlows() {
        Flow flow = new Flow();
        flow.setFlowId("ssh_h1");
        RealTimeManager.getInstance().deleteFlow(flow);

        flow = new Flow();
        flow.setFlowId("ssh_h2");
        RealTimeManager.getInstance().deleteFlow(flow);

        flow = new Flow();
        flow.setFlowId("ssh_h3");
        RealTimeManager.getInstance().deleteFlow(flow);

        flow = new Flow();
        flow.setFlowId("ssh_h4");
        RealTimeManager.getInstance().deleteFlow(flow);
    }

    private static void pushDefaultFlow1() {
        Flow flow = new Flow();
        flow.setFlowId("DefaultFlow1");
        flow.setSrcIpAddr("10.0.0.1");
        flow.setSrcPort((short) 0);
        flow.setDstIpAddr("10.0.0.4");
        flow.setDstPort((short) 0);
        flow.setBandwidth(1e3);
        RealTimeManager.getInstance().pushFlow(flow);
    }

    private static void pushDefaultFlow2() {
        Flow flow = new Flow();
        flow.setFlowId("DefaultFlow2");
        flow.setSrcIpAddr("10.0.0.2");
        flow.setSrcPort((short) 0);
        flow.setDstIpAddr("10.0.0.3");
        flow.setDstPort((short) 0);
        flow.setBandwidth(2e3);
        RealTimeManager.getInstance().pushFlow(flow);
    }

    private static void pushFlowH1H2 (){
        Flow flow = new Flow();
        flow.setFlowId("FlowH1H2");
        flow.setSrcIpAddr("192.168.0.101");
        flow.setSrcPort((short) 0);
        flow.setDstIpAddr("192.168.0.102");
        flow.setDstPort((short) 0);
        flow.setBandwidth(2e3);
        RealTimeManager.getInstance().pushFlow(flow);
    }

    private static void pushFlowH1H4 (){
        Flow flow = new Flow();
        flow.setFlowId("FlowH1H4");
        flow.setSrcIpAddr("192.168.0.101");
        flow.setSrcPort((short) 0);
        flow.setDstIpAddr("192.168.0.104");
        flow.setDstPort((short) 0);
        flow.setBandwidth(2e3);
        RealTimeManager.getInstance().pushFlow(flow);
    }

    private static void pushFlowH3H2 (){
        Flow flow = new Flow();
        flow.setFlowId("FlowH3H2");
        flow.setSrcIpAddr("192.168.0.103");
        flow.setSrcPort((short) 0);
        flow.setDstIpAddr("192.168.0.102");
        flow.setDstPort((short) 0);
        flow.setBandwidth(2e3);
        RealTimeManager.getInstance().pushFlow(flow);
    }

    private static void pushFlowH3H4 (){
        Flow flow = new Flow();
        flow.setFlowId("FlowH3H4");
        flow.setSrcIpAddr("192.168.0.103");
        flow.setSrcPort((short) 0);
        flow.setDstIpAddr("192.168.0.104");
        flow.setDstPort((short) 0);
        flow.setBandwidth(2e3);
        RealTimeManager.getInstance().pushFlow(flow);
    }

    private static void pushPresetFlow(String flowName) {
        switch (flowName) {
            case "SSHFlows":
                pushSSFFlows();
                break;
            case "DefaultFlow1":
                pushDefaultFlow1();
                break;
            case "DefaultFlow2":
                pushDefaultFlow2();
                break;
            case "FlowH1H2":
                pushFlowH1H2();
                break;
            case "FlowH1H4":
                pushFlowH1H4();
                break;
            case "FlowH3H2":
                pushFlowH3H2();
                break;
            case "FlowH3H4":
                pushFlowH3H4();
                break;
        }
    }

    private static void deletePresetFlow(String flowName) {
        if (flowName.equals("SSHFlows")) {
            deleteSSHFlows();
        } else {
            Flow flow = new Flow();
            flow.setFlowId(flowName);
            RealTimeManager.getInstance().deleteFlow(flow);
        }

    }

    public static void presetFlowManager(String message) {
        if (message.equals("DeleteAllFlows")) {
            RealTimeManager.getInstance().deleteAllFlows();
        } else if (message.startsWith("Push")) {
            String flowName = message.replace("Push", "");
            pushPresetFlow(flowName);
        } else if (message.startsWith("Delete")) {
            String flowName = message.replace("Delete", "");
            deletePresetFlow(flowName);
        }
    }
}
