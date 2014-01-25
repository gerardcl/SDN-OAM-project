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
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.routing.ForwardingBase;
import net.floodlightcontroller.routing.IRoutingDecision;
import net.floodlightcontroller.routing.IRoutingService;
import net.floodlightcontroller.topology.ITopologyService;
import org.openflow.protocol.*;
import org.openflow.protocol.action.OFAction;
import org.openflow.protocol.action.OFActionOutput;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ArpForwarding extends ForwardingBase implements IFloodlightModule {

    @Override
    public Command processPacketInMessage(IOFSwitch sw, OFPacketIn pi, IRoutingDecision decision,
                                          FloodlightContext cntx) {
        Ethernet eth = IFloodlightProviderService.bcStore.get(cntx,
                IFloodlightProviderService.CONTEXT_PI_PAYLOAD);

        OFMatch match = new OFMatch();
        match.loadFromPacket(pi.getPacketData(), (short) 0);

        // Filter (ARP)
        if (match.getDataLayerType() == 0x806) {
            doFlood(sw, pi, cntx);
            return Command.CONTINUE;
        }


        OFMatch ofMatch = new OFMatch();
        ofMatch.loadFromPacket(pi.getPacketData(), pi.getInPort());
        pushDropMatch(sw, ofMatch, cntx);

        return Command.CONTINUE;
    }

    private void pushDropMatch(IOFSwitch sw, OFMatch match, FloodlightContext cntx) {
        System.out.println("Pushing drop entry with identifier '" + sw.getStringId() + "." + match.getInputPort() + "'.");

        match = new OFMatch();

        // Create flow-mod based on packet-in and src-switch
        //match.setInputPort(port);
        OFFlowMod fm = (OFFlowMod) floodlightProvider.getOFMessageFactory().getMessage(OFType.FLOW_MOD);
        List<OFAction> actions = new ArrayList<OFAction>(); // no actions = drop
        fm.setCookie(0)
                .setPriority((short) 1)
                .setIdleTimeout((short) 5)
                .setHardTimeout((short) 60)
                .setBufferId(OFPacketOut.BUFFER_ID_NONE)
                .setMatch(match)
                .setActions(actions)
                .setLengthU(OFFlowMod.MINIMUM_LENGTH);

        try {
            sw.write(fm, cntx);
        } catch (IOException e) {
            e.printStackTrace();
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
        if (topology.isIncomingBroadcastAllowed(sw.getId(),
                pi.getInPort()) == false) {
            if (log.isTraceEnabled()) {
                log.trace("doFlood, drop broadcast packet, pi={}, " +
                        "from a blocked port, srcSwitch=[{},{}], linkInfo={}",
                        new Object[]{pi, sw.getId(), pi.getInPort()});
            }
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
        } catch (IOException e) {
        }

        return;
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
