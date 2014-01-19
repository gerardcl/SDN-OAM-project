package dxat.controller.module.listeners;

import com.google.gson.Gson;
import dxat.controller.module.ModuleServerThread;
import dxat.controller.module.events.IPacketEvent;
import dxat.controller.module.pojos.ControllerEvent;
import dxat.controller.module.pojos.Packet;
import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.packet.IPv4;
import org.openflow.protocol.OFMatch;
import org.openflow.protocol.OFMessage;
import org.openflow.protocol.OFPacketIn;
import org.openflow.protocol.OFType;

import java.util.Date;

/**
 * Created by xavier on 1/10/14.
 */
public class PacketListener implements IOFMessageListener {

	private ModuleServerThread moduleServerThread;

	public PacketListener(ModuleServerThread moduleServerThread) {
		super();
		this.moduleServerThread = moduleServerThread;
	}

	@Override
	public Command receive(IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		/* Get data of the message header */
		OFPacketIn pi = (OFPacketIn) msg;
		OFMatch match = new OFMatch();
		match.loadFromPacket(pi.getPacketData(), (short) 0);

		/* Copy data into the Packet Instance */
		Packet packet = new Packet();
		packet.setNetworkDestination(IPv4.fromIPv4Address(match
				.getNetworkDestination()));
		packet.setNetworkProtocol(match.getNetworkProtocol());
		packet.setNetworkSource(IPv4.fromIPv4Address(match.getNetworkSource()));
		packet.setNetworkTypeOfService(match.getNetworkTypeOfService());
		packet.setPortId(sw.getStringId() + ":" + match.getInputPort());
		packet.setTransportDestination(match.getTransportDestination());
		packet.setTransportSource(match.getTransportSource());

		/* Create controller event */
		ControllerEvent controllerEvent = new ControllerEvent();
		controllerEvent.setEvent(IPacketEvent.PACKET_RECEIVED);
		controllerEvent.setObject(new Gson().toJson(packet));
		controllerEvent.setTimestamp(new Date().getTime());

		/* Send the event to the application server */
		moduleServerThread.broadcastControllerEvent(controllerEvent);

		return Command.CONTINUE;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		return false;
	}
}
