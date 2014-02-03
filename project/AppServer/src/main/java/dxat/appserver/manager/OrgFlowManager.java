package dxat.appserver.manager;

import dxat.appserver.flows.pojos.Flow;
import dxat.appserver.manager.database.Create;
import dxat.appserver.manager.database.Delete;
import dxat.appserver.manager.database.Update;
import dxat.appserver.manager.exceptions.FlowAlreadyExistsException;
import dxat.appserver.manager.exceptions.FlowNotFoundException;
import dxat.appserver.manager.exceptions.OrgNotFoundException;
import dxat.appserver.manager.pojos.OrgFlow;
import dxat.appserver.manager.pojos.OrgFlowCollection;
import dxat.appserver.realtime.RealTimeManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class OrgFlowManager {
    private static OrgFlowManager instance;
    public OrgManager orgManager;
    private HashMap<String, OrgFlow> flows;
    private HashMap<String, Flow> presetFlows;
    private Create dbcreate;
    private Update dbupdate;
    private Delete dbdelete;

    private OrgFlowManager() {
        //TODO initialize!
        dbcreate = new Create();
        dbupdate = new Update();
        dbdelete = new Delete();
        orgManager = OrgManager.getInstance();

        presetFlows = new HashMap<String, Flow>();
        Flow flow;

        // Load H1 to H2 preset flow
        flow = new Flow();
        flow.setFlowId("H1H2");
        flow.setSrcIpAddr("192.168.0.101");
        flow.setDstIpAddr("192.168.0.102");
        flow.setBandwidth(2.5e6);
        flow.setDstPort((short) 0);
        flow.setSrcPort((short) 0);
        presetFlows.put(flow.getFlowId(), flow);

        flow = new Flow();
        flow.setFlowId("H1H4");
        flow.setSrcIpAddr("192.168.0.101");
        flow.setDstIpAddr("192.168.0.104");
        flow.setBandwidth(2.5e6);
        flow.setDstPort((short) 0);
        flow.setSrcPort((short) 0);
        presetFlows.put(flow.getFlowId(), flow);

        flow = new Flow();
        flow.setFlowId("H3H2");
        flow.setSrcIpAddr("192.168.0.103");
        flow.setDstIpAddr("192.168.0.102");
        flow.setBandwidth(2.5e6);
        flow.setDstPort((short) 0);
        flow.setSrcPort((short) 0);
        presetFlows.put(flow.getFlowId(), flow);

        flow = new Flow();
        flow.setFlowId("H3H4");
        flow.setSrcIpAddr("192.168.0.103");
        flow.setDstIpAddr("192.168.0.104");
        flow.setBandwidth(2.5e6);
        flow.setDstPort((short) 0);
        flow.setSrcPort((short) 0);
        presetFlows.put(flow.getFlowId(), flow);

        flow = new Flow();
        flow.setFlowId("H5H2");
        flow.setSrcIpAddr("192.168.0.105");
        flow.setDstIpAddr("192.168.0.102");
        flow.setBandwidth(2.5e6);
        flow.setDstPort((short) 0);
        flow.setSrcPort((short) 0);
        presetFlows.put(flow.getFlowId(), flow);

        flow = new Flow();
        flow.setFlowId("H5H4");
        flow.setSrcIpAddr("192.168.0.105");
        flow.setDstIpAddr("192.168.0.104");
        flow.setBandwidth(2.5e6);
        flow.setDstPort((short) 0);
        flow.setSrcPort((short) 0);
        presetFlows.put(flow.getFlowId(), flow);
    }

    public void pushPresetFlow(String flowId) {
        Flow flow = presetFlows.get(flowId);
        if (flow != null) {
            RealTimeManager.getInstance().pushFlow(flow);
        }
    }

    public void deletePresetFlow(String flowId) {
        if (flowId.equals("all)")) {
            RealTimeManager.getInstance().deleteAllFlows();
        }
        Flow flow = presetFlows.get(flowId);
        if (flow != null) {
            RealTimeManager.getInstance().deleteFlow(flow);
        }
    }

    public static OrgFlowManager getInstance() {
        if (instance == null)
            instance = new OrgFlowManager();
        return instance;
    }

    public OrgFlowCollection getAllFlows() {
        List<OrgFlow> flowList = new ArrayList<OrgFlow>(flows.values());
        OrgFlowCollection orgFlows = new OrgFlowCollection();
        orgFlows.setOrgFlows(flowList);
        return orgFlows;
    }

    public OrgFlowCollection getAllOrgFlows(String orgId) {
        List<OrgFlow> flowList = new ArrayList<OrgFlow>(flows.values());
        OrgFlowCollection orgFlows = new OrgFlowCollection();
        orgFlows.setOrgFlows(flowList);
        return orgFlows;
    }

    public OrgFlow addOrgFlow(String orgId, OrgFlow flow) {


        if (!orgManager.getTerminals().containsKey(flow.getDstOTidentifier())) return null;
        if (!orgManager.getTerminals().containsKey(flow.getSrcOTidentifier())) return null;


        OrgFlow newflow = new OrgFlow();
        newflow.setDstOTidentifier(flow.getDstOTidentifier());
        newflow.setSrcOTidentifier(flow.getSrcOTidentifier());
        newflow.setSrcPort(flow.getSrcPort());
        newflow.setDstPort(flow.getDstPort());
        newflow.setName(flow.getName());
        newflow.setProtocol(flow.getProtocol());
        newflow.setQos(flow.getQos());
        String id = Integer.toString((flow.getName().hashCode()));
        id.concat(Integer.toString((Integer.toString(flow.getSrcPort()).hashCode())));
        id.concat(Integer.toString((flow.getDstOTidentifier().hashCode())));
        newflow.setIdentifier(Integer.toString((int) Math.abs(id.hashCode() * Math.random() * -10)));
        newflow.setBandwidth(flow.getBandwidth());

//TO CHANGE IT DINAMICALLY WHEN GETTING FLOW EVENTS!!!!!!!!!!!!!!!!!!!!
        newflow.setActive(true);

        try {
            dbcreate.createFlow(newflow, orgId);
            orgManager.getOrg(orgId).getFlows().put(newflow.getIdentifier(), newflow);
            orgManager.getFlows().put(newflow.getIdentifier(), newflow);


            OrgFlow orgFlow = orgManager.getFlows().get(newflow.getIdentifier());
            Flow flowing = new Flow();
            flowing.setBandwidth(orgFlow.getBandwidth());
            flowing.setDstIpAddr(orgManager.getTerminals().get(orgFlow.getDstOTidentifier()).getIpAddress());
            flowing.setDstPort(orgFlow.getDstPort());
            flowing.setFlowId(orgFlow.getIdentifier());
            flowing.setProtocol((byte) 0); //TODO
            flowing.setQos(orgFlow.getQos());
            flowing.setSrcIpAddr(orgManager.getTerminals().get(orgFlow.getSrcOTidentifier()).getIpAddress());
            flowing.setSrcPort(orgFlow.getSrcPort());

            RealTimeManager.getInstance().pushFlow(flowing);

            return newflow;
        } catch (FlowAlreadyExistsException | OrgNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public OrgFlow updateOrgFlow(String orgId, OrgFlow flow) {
        try {
            dbupdate.updateFlow(flow, orgId);
            orgManager.getOrg(orgId).getFlows().put(flow.getIdentifier(), flow);
            orgManager.getFlows().put(flow.getIdentifier(), flow);
            return flow;
        } catch (FlowNotFoundException | OrgNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public String deleteOrgFlow(String orgId, String flowId) {
        try {
            dbdelete.deleteFlow(orgId, flowId);
            orgManager.getOrg(orgId).getFlows().remove(flowId);

//TODO REMOVE ONCE RECEIVED SOCKET EVENT CONFIRMATION FROM CONTROLLER
            orgManager.getFlows().remove(flowId);

            OrgFlow orgFlow = orgManager.getFlows().get(flowId);
            Flow flow = new Flow();
            flow.setFlowId(orgFlow.getIdentifier());
            RealTimeManager.getInstance().deleteFlow(flow);

            return flowId;
        } catch (FlowNotFoundException | OrgNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public OrgFlow getOrgFlow(String orgId, String flowId) {
        //TODO
        return null;
    }

    public boolean existFlowInOrg(OrgFlow flow, String flowId, String orgId) {
        for (Entry<String, OrgFlow> entry1 : orgManager.getOrg(orgId).getFlows().entrySet()) {
            OrgFlow cflow = entry1.getValue();
            if (cflow.getIdentifier().equals(flow.getIdentifier()) && cflow.getIdentifier().equals(flowId)) return true;
        }
        return false;
    }
}
