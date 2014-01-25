package dxat.appserver.realtime.resources;

import dxat.appserver.flows.pojos.Flow;
import dxat.appserver.realtime.RealTimeManager;

/**
 * Created by xavier on 1/21/14.
 */
public class PushPresetFlow {

    private static void pushSSFFlows() {
        Flow flow = new Flow();
        flow.setFlowId("ssh_h1");
        flow.setSrcIpAddr("192.168.0.7");
        flow.setSrcPort(0);
        flow.setDstIpAddr("192.168.0.101");
        flow.setDstPort(0);
        flow.setBandwidth(1e3);
        RealTimeManager.getInstance().pushFlow(flow);

        flow = new Flow();
        flow.setFlowId("ssh_h2");
        flow.setSrcIpAddr("192.168.0.7");
        flow.setSrcPort(0);
        flow.setDstIpAddr("192.168.0.102");
        flow.setDstPort(0);
        flow.setBandwidth(1e3);
        RealTimeManager.getInstance().pushFlow(flow);

        flow = new Flow();
        flow.setFlowId("ssh_h3");
        flow.setSrcIpAddr("192.168.0.7");
        flow.setSrcPort(0);
        flow.setDstIpAddr("192.168.0.103");
        flow.setDstPort(0);
        flow.setBandwidth(1e3);
        RealTimeManager.getInstance().pushFlow(flow);

        flow = new Flow();
        flow.setFlowId("ssh_h4");
        flow.setSrcIpAddr("192.168.0.7");
        flow.setSrcPort(0);
        flow.setDstIpAddr("192.168.0.104");
        flow.setDstPort(0);
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
        flow.setSrcPort(0);
        flow.setDstIpAddr("10.0.0.4");
        flow.setDstPort(0);
        flow.setBandwidth(1e3);
        RealTimeManager.getInstance().pushFlow(flow);
    }

    private static void pushDefaultFlow2() {
        Flow flow = new Flow();
        flow.setFlowId("DefaultFlow2");
        flow.setSrcIpAddr("10.0.0.2");
        flow.setSrcPort(0);
        flow.setDstIpAddr("10.0.0.3");
        flow.setDstPort(0);
        flow.setBandwidth(2e3);
        RealTimeManager.getInstance().pushFlow(flow);
    }

    private static void pushPresetFlow(String flowName) {
        if (flowName.equals("SSHFlows")) {
            pushSSFFlows();
        } else if (flowName.equals("DefaultFlow1")) {
            pushDefaultFlow1();
        } else if (flowName.equals("DefaultFlow2")) {
            pushDefaultFlow2();
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
        if (message.startsWith("Push")) {
            String flowName = message.replace("Push", "");
            pushPresetFlow(flowName);
        } else if (message.startsWith("Delete")) {
            String flowName = message.replace("Delete", "");
            deletePresetFlow(flowName);
        } else if (message.equals("DeleteAllFlows")) {
            RealTimeManager.getInstance().deleteAllFlows();
        }
    }
}
