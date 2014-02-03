/**
 *    Copyright 2011, Big Switch Networks, Inc.
 *    Originally created by David Erickson, Stanford University
 *
 *    Licensed under the Apache License, Version 2.0 (the "License"); you may
 *    not use this file except in compliance with the License. You may obtain
 *    a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *    License for the specific language governing permissions and limitations
 *    under the License.
 **/

package dxat.controller.module;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.counter.ICounterStoreService;
import net.floodlightcontroller.devicemanager.IDeviceService;
import net.floodlightcontroller.routing.ForwardingBase;
import net.floodlightcontroller.routing.IRoutingDecision;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.staticflowentry.StaticFlowEntries;
import net.floodlightcontroller.topology.ITopologyService;
import net.floodlightcontroller.topology.NodePortTuple;
import org.openflow.protocol.*;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;
import org.openflow.util.HexString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ArpForwarding extends ForwardingBase implements IFloodlightModule {

    @Override
    public Command processPacketInMessage(IOFSwitch sw, OFPacketIn pi, IRoutingDecision decision,
                                          FloodlightContext cntx) {
        OFMatch ofMatch = new OFMatch();
        ofMatch.loadFromPacket(pi.getPacketData(), pi.getInPort());

        // Filter (ARP)
        if (ofMatch.getDataLayerType() == 0x806) {
            NodePortTuple attachmentPoint = DxatAppModule.getInstance().getDeviceListener().getAttachmentPoint(ofMatch.getNetworkDestination());
            if (attachmentPoint != null) {
                //System.out.println("Doing ARP Smart forwarding to Attachment point: " + attachmentPoint.toString());
                doSmartForward(attachmentPoint, pi, cntx);
            } else {
                doFlood(sw, pi, cntx);
            }
            return Command.CONTINUE;
        } else {
            // Push Drop entry for this entry
            pushDropMatch(sw, ofMatch, cntx);
        }

        return Command.CONTINUE;
    }

    private void pushDropMatch(IOFSwitch sw, OFMatch match, FloodlightContext cntx) {

        //System.out.println("Pushing drop entry with match: '" + match.toString() + "'.");

        short inputPort =match.getInputPort();

        match = new OFMatch();
        match.setInputPort(inputPort);
        match.setDataLayerType((short) 0x800);

        // Create flow-mod based on packet-in and src-switch
        OFFlowMod fm = (OFFlowMod) floodlightProvider.getOFMessageFactory().getMessage(OFType.FLOW_MOD);
        String entryName = sw.getStringId()+":"+match.getInputPort();
        StaticFlowEntries.initDefaultFlowMod(fm, entryName);

        List<OFAction> actions = new ArrayList<OFAction>(); // no actions = drop
        fm.setIdleTimeout((short) 10); // infinite
        fm.setHardTimeout((short) 20); // infinite
        fm.setBufferId(OFPacketOut.BUFFER_ID_NONE);
        fm.setCommand((short) 0);
        fm.setFlags((short) 0);
        fm.setOutPort(OFPort.OFPP_NONE.getValue());
        fm.setCookie((long) 0);
        fm.setPriority((short)1);
        fm.setActions(actions);
        fm.setMatch(match);

        // Push static flow entry
        DxatAppModule.getInstance().getFlowPusherService().addFlow(entryName, fm, sw.getStringId());

        /*try {
            sw.write(fm, cntx);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Creates a OFPacketOut with the OFPacketIn data that is flooded on all ports unless
     * the port is blocked, in which case the packet will be dropped.
     *
     * @param sw   The switch that receives the OFPacketIn
     * @param pi   The OFPacketIn that came to the switch
     * @param cntx The FloodlightContext associated with this OFPacketIn
     */
    /**
     * Creates a OFPacketOut with the OFPacketIn data that is flooded on all ports unless
     * the port is blocked, in which case the packet will be dropped.
     *
     * @param attachmentPoint Attachment point of the ARP message destination
     * @param pi              Packet in
     * @param cntx            Floodlight context
     */
    protected void doSmartForward(NodePortTuple attachmentPoint, OFPacketIn pi, FloodlightContext cntx) {
        IOFSwitch sw = DxatAppModule.getInstance().getSwitchService().getSwitch(attachmentPoint.getNodeId());

        // Set Action to flood
        OFPacketOut po =
                (OFPacketOut) floodlightProvider.getOFMessageFactory().getMessage(OFType.PACKET_OUT);
        List<OFAction> actions = new ArrayList<OFAction>();
        actions.add(new OFActionOutput(attachmentPoint.getPortId(), (short) 0xFFFF));

        po.setActions(actions);
        po.setActionsLength((short) OFActionOutput.MINIMUM_LENGTH);

        // set buffer-id, in-port and packet-data based on packet-in
        short poLength = (short) (po.getActionsLength() + OFPacketOut.MINIMUM_LENGTH);
        po.setBufferId(OFPacketOut.BUFFER_ID_NONE);
        po.setInPort(pi.getInPort());
        byte[] packetData = pi.getPacketData();
        poLength += packetData.length;
        po.setPacketData(packetData);
        po.setLength(poLength);

        try {
            messageDamper.write(sw, po, cntx);
        } catch (IOException ignored) {
        }
    }

    /**
     * Creates a OFPacketOut with the OFPacketIn data that is flooded on all ports unless
     * the port is blocked, in which case the packet will be dropped.
     *
     * @param sw   The switch that receives the OFPacketIn
     * @param pi   The OFPacketIn that came to the switch
     * @param cntx The FloodlightContext associated with this OFPacketIn
     */
    protected void doFlood(IOFSwitch sw, OFPacketIn pi, FloodlightContext cntx) {
        if (!topology.isIncomingBroadcastAllowed(sw.getId(),
                pi.getInPort())) {
            return;
        }

        // Set Action to flood
        OFPacketOut po =
                (OFPacketOut) floodlightProvider.getOFMessageFactory().getMessage(OFType.PACKET_OUT);
        List<OFAction> actions = new ArrayList<OFAction>();
        if (sw.hasAttribute(IOFSwitch.PROP_SUPPORTS_OFPP_FLOOD)) {
            actions.add(new OFActionOutput(OFPort.OFPP_FLOOD.getValue(),
                    (short) 0xFFFF));
        } else {
            actions.add(new OFActionOutput(OFPort.OFPP_ALL.getValue(),
                    (short) 0xFFFF));
        }
        po.setActions(actions);
        po.setActionsLength((short) OFActionOutput.MINIMUM_LENGTH);

        // set buffer-id, in-port and packet-data based on packet-in
        short poLength = (short) (po.getActionsLength() + OFPacketOut.MINIMUM_LENGTH);
        po.setBufferId(OFPacketOut.BUFFER_ID_NONE);
        po.setInPort(pi.getInPort());
        byte[] packetData = pi.getPacketData();
        poLength += packetData.length;
        po.setPacketData(packetData);
        po.setLength(poLength);

        try {
            messageDamper.write(sw, po, cntx);
        } catch (IOException ignored) {
        }

    }

    // IFloodlightModule methods

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        // We don't export any services
        return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService>
    getServiceImpls() {
        // We don't have any services
        return null;
    }

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        Collection<Class<? extends IFloodlightService>> l =
                new ArrayList<Class<? extends IFloodlightService>>();
        l.add(IFloodlightProviderService.class);
        l.add(IDeviceService.class);
        l.add(IRoutingService.class);
        l.add(ITopologyService.class);
        l.add(ICounterStoreService.class);
        return l;
    }

    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        super.init();
        this.floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);
        this.deviceManager = context.getServiceImpl(IDeviceService.class);
        this.routingEngine = context.getServiceImpl(IRoutingService.class);
        this.topology = context.getServiceImpl(ITopologyService.class);
        this.counterStore = context.getServiceImpl(ICounterStoreService.class);
    }

    @Override
    public void startUp(FloodlightModuleContext context) {
        super.startUp();
    }
}
