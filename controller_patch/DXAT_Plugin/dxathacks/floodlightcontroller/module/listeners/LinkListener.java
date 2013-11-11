package dxathacks.floodlightcontroller.module.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dxathacks.floodlightcontroller.module.ModuleServer;
import dxathacks.floodlightcontroller.module.PojoTranslator;
import dxathacks.floodlightcontroller.pojos.Link;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryListener;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.linkdiscovery.LinkInfo;

public class LinkListener implements ILinkDiscoveryListener {
	private ModuleServer appInterface = null;
	private ILinkDiscoveryService linkService = null;

	public LinkListener(ModuleServer appInterface,
			ILinkDiscoveryService linkService) {
		this.appInterface = appInterface;
		this.linkService = linkService;
	}

	@Override
	public void linkDiscoveryUpdate(LDUpdate update) {
		if (update.getOperation().toString()
				.equals(UpdateOperation.LINK_UPDATED.toString())
				|| update.getOperation().toString()
						.equals(UpdateOperation.SWITCH_UPDATED.toString())) {
			appInterface.updateLink(PojoTranslator.linkUpdate2Pojo(update));
		} else if (update.getOperation().toString()
				.equals(UpdateOperation.LINK_REMOVED.toString())
				|| update.getOperation().toString()
						.equals(UpdateOperation.SWITCH_REMOVED.toString())
				|| update.getOperation().toString()
						.equals(UpdateOperation.TUNNEL_PORT_REMOVED.toString())
				|| update.getOperation().toString()
						.equals(UpdateOperation.PORT_DOWN.toString())) {
			appInterface.deleteLink(PojoTranslator.linkUpdate2Pojo(update));
		} else if (update.getOperation().toString()
				.equals(UpdateOperation.PORT_UP.toString())
				|| update.getOperation().toString()
						.equals(UpdateOperation.TUNNEL_PORT_ADDED.toString())) {
			appInterface.addLink(PojoTranslator.linkUpdate2Pojo(update));
		} else {
			appInterface.updateLink(PojoTranslator.linkUpdate2Pojo(update));
			System.out.println("%%%%%%%%%%%%%%% "
					+ "Warning at 'linkDiscoveryUpdate' Update Reason: '"
					+ update.getOperation().toString() + "' %%%%%%%%%%%%%%%");
		}
	}

	@Override
	public void linkDiscoveryUpdate(List<LDUpdate> updateList) {
		for (LDUpdate update : updateList) {
			if (update != null)
				this.linkDiscoveryUpdate(update);
		}
	}

	public void updateLinks() {
		Map<net.floodlightcontroller.routing.Link, LinkInfo> links = new HashMap<net.floodlightcontroller.routing.Link, LinkInfo>();

		if (linkService != null) {
			links.putAll(linkService.getLinks());
			for (net.floodlightcontroller.routing.Link link : links.keySet()) {
				LinkInfo info = links.get(link);
				LinkType type = linkService.getLinkType(link, info);

				Link lnk = new Link();
				lnk.setDstPort(link.getDstPort());
				lnk.setDstSwitch("SW-" + link.getDst());
				lnk.setSrcPort(link.getSrcPort());
				lnk.setDstSwitch("SW-" + link.getDst());
				lnk.setType(type.toString());

				// Update link
				appInterface.addLink(lnk);
			}
		}
	}

}
