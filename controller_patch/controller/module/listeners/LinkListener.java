package dxat.controller.module.listeners;

import com.google.gson.Gson;
import dxat.controller.module.DxatAppModule;
import dxat.controller.module.ModuleServerThread;
import dxat.controller.module.PojoTranslator;
import dxat.controller.module.events.ILinkEvents;
import dxat.controller.module.pojos.ControllerEvent;
import dxat.controller.module.pojos.LinkCollection;
import dxat.controller.module.pojos.TranferLink;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryListener;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.LinkInfo;
import net.floodlightcontroller.routing.Link;
import net.floodlightcontroller.topology.NodePortTuple;
import org.openflow.util.HexString;

import java.util.*;

public class LinkListener implements ILinkDiscoveryListener, ILinkEvents {
    private ModuleServerThread moduleServerThread = null;
    private ILinkDiscoveryService linkService = null;

    public LinkListener(ModuleServerThread moduleServerThread,
                        ILinkDiscoveryService linkService) {
        super();
        this.moduleServerThread = moduleServerThread;
        this.linkService = linkService;
    }

    @Override
    public void linkDiscoveryUpdate(LDUpdate update) {
        // If no switch detected return
        if (update.getDst() == 0 || update.getSrc() == 0) {
            return;
        }

        // Check if the link is available
        NodePortTuple srcPort = new NodePortTuple(update.getSrc(),
                update.getSrcPort());
        Set<Link> portLinks = linkService.getPortLinks().get(srcPort);
        boolean enabled = false;
        long dstSw = update.getDst();
        short dstPort = update.getDstPort();
        if (portLinks != null) {
            for (Link link : portLinks) {
                if (link.getDst() == dstSw && link.getDstPort() == dstPort) {
                    enabled = true;
                    break;
                }
            }
        }

        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        String updateStr = update.getOperation().toString();
        TranferLink link = PojoTranslator.linkUpdate2Pojo(update, enabled);
        if (updateStr.equals(UpdateOperation.LINK_REMOVED)) {
            controllerEvent.setEvent(LINK_REMOVED);
        } else if (updateStr.equals(UpdateOperation.LINK_UPDATED)) {
            controllerEvent.setEvent(LINK_UPDATED);
        } else if (updateStr.equals(UpdateOperation.PORT_DOWN)) {
            controllerEvent.setEvent(PORT_DOWN);
        } else if (updateStr.equals(UpdateOperation.PORT_UP)) {
            controllerEvent.setEvent(PORT_UP);
        } else if (updateStr.equals(UpdateOperation.SWITCH_REMOVED)) {
            controllerEvent.setEvent(SWITCH_REMOVED);
        } else if (updateStr.equals(UpdateOperation.SWITCH_UPDATED)) {
            controllerEvent.setEvent(LINK_UPDATED);
        } else if (updateStr.equals(UpdateOperation.TUNNEL_PORT_ADDED)) {
            controllerEvent.setEvent(TUNEL_PORT_ADDED);
        } else if (updateStr.equals(UpdateOperation.TUNNEL_PORT_REMOVED)) {
            controllerEvent.setEvent(TUNEL_PORT_REMOVED);
        } else {
            controllerEvent.setEvent(LINK_UPDATED);
            if (!enabled) {
                ReroutingThread reroutingThread = new ReroutingThread(update);
                Thread thread = new Thread(reroutingThread, "Reroute thread");
                thread.start();
            }
        }
        controllerEvent.setObject(new Gson().toJson(link));
        moduleServerThread.broadcastControllerEvent(controllerEvent);
    }

    public class ReroutingThread implements Runnable {
        private LDUpdate update;

        public ReroutingThread(LDUpdate update) {
            this.update = update;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String portId;
            portId = HexString.toHexString(update.getSrc()) + ":" + update.getSrcPort();
            DxatAppModule.getInstance().getFlowPusherManager().rerouteFlow(portId);
            portId = HexString.toHexString(update.getDst()) + ":" + update.getDstPort();
            DxatAppModule.getInstance().getFlowPusherManager().rerouteFlow(portId);
        }
    }

    @Override
    public void linkDiscoveryUpdate(List<LDUpdate> updateList) {
        for (LDUpdate update : updateList) {
            if (update != null)
                linkDiscoveryUpdate(update);
        }
    }

    public LinkCollection getAllLinkCollection() {
        LinkCollection linkCollection = new LinkCollection();
        linkCollection.setLinks(new ArrayList<TranferLink>());
        Map<net.floodlightcontroller.routing.Link, LinkInfo> linksMap = linkService
                .getLinks();
        for (net.floodlightcontroller.routing.Link ofLink : linksMap.keySet()) {
            TranferLink link = PojoTranslator.link2Pojo(ofLink);
            linkCollection.getLinks().add(link);
        }
        return linkCollection;
    }
}
