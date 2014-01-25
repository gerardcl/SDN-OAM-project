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

    private Link getLinkFromUpdate(LDUpdate update) {
        NodePortTuple srcPort = new NodePortTuple(update.getSrc(),
                update.getSrcPort());
        Link link = new Link();
        link.setSrcPort(update.getSrcPort());
        link.setSrc(update.getSrc());
        link.setDstPort(update.getDstPort());
        link.setDst(update.getDst());

        Set<Link> portLinks = linkService.getPortLinks().get(srcPort);

        if (portLinks==null)
            return null;

        if (portLinks.contains(link))
            return link;

        return null;
    }

    @Override
    public void linkDiscoveryUpdate(LDUpdate update) {
        // If no switch detected return
        if (update.getDst() == 0 || update.getSrc() == 0) {
            return;
        }

        // Check if the link is available
        boolean enabled = true;
        Link link = getLinkFromUpdate(update);
        if (link == null){
            enabled = false;
            link = new Link();
            link.setSrcPort(update.getSrcPort());
            link.setSrc(update.getSrc());
            link.setDstPort(update.getDstPort());
            link.setDst(update.getDst());
        }

        ControllerEvent controllerEvent = new ControllerEvent();
        controllerEvent.setTimestamp(new Date().getTime());
        String updateStr = update.getOperation().toString();
        TranferLink tLink = PojoTranslator.linkUpdate2Pojo(update, enabled);
        if (updateStr.equals(UpdateOperation.LINK_REMOVED)) {
            controllerEvent.setEvent(LINK_REMOVED);
            DxatAppModule.getInstance().getFlowPusherManager().rerouteFlow(link);
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
            controllerEvent.setEvent(TUNNEL_PORT_ADDED);
        } else if (updateStr.equals(UpdateOperation.TUNNEL_PORT_REMOVED)) {
            controllerEvent.setEvent(TUNNEL_PORT_ADDED);
        } else {
            controllerEvent.setEvent(LINK_UPDATED);
            if (!enabled)
                DxatAppModule.getInstance().getFlowPusherManager().rerouteFlow(link);
        }
        controllerEvent.setObject(new Gson().toJson(tLink));
        moduleServerThread.broadcastControllerEvent(controllerEvent);
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
