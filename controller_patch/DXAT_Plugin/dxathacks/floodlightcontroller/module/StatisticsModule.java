package dxathacks.floodlightcontroller.module;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.openflow.protocol.OFPort;
import org.openflow.protocol.OFStatisticsRequest;
import org.openflow.protocol.statistics.OFPortStatisticsReply;
import org.openflow.protocol.statistics.OFPortStatisticsRequest;
import org.openflow.protocol.statistics.OFStatistics;
import org.openflow.protocol.statistics.OFStatisticsType;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import dxathacks.floodlightcontroller.pojos.ControllerInterface;
import dxathacks.floodlightcontroller.pojos.PortStatistics;
import dxathacks.floodlightcontroller.pojos.PortStatisticsCollection;

public class StatisticsModule implements Runnable {
	private ControllerInterface controllerItf = null;
	private IFloodlightProviderService switchService;

	public StatisticsModule(ControllerInterface controllerItf,
			IFloodlightProviderService switchService) {
		this.controllerItf = controllerItf;
		this.switchService = switchService;
	}

	private void sendStats() {
		PortStatisticsCollection stats = new PortStatisticsCollection();
		Set<Long> swList = switchService.getAllSwitchDpids();
		for (Long swId : swList) {
			IOFSwitch sw = switchService.getSwitch(swId);
			OFStatisticsRequest req = new OFStatisticsRequest();
			req.setStatisticType(OFStatisticsType.PORT);
			int requestLength = req.getLengthU();

			Future<List<OFStatistics>> future;
			List<OFStatistics> values = null;
			OFPortStatisticsRequest specificReq = new OFPortStatisticsRequest();
			specificReq.setPortNumber(OFPort.OFPP_NONE.getValue());
			req.setStatistics(Collections
					.singletonList((OFStatistics) specificReq));
			requestLength += specificReq.getLength();

			req.setLengthU(requestLength);
			try {
				future = sw.queryStatistics(req);
				values = future.get(10, TimeUnit.SECONDS);
				for (int i = 0; i < values.size(); i++) {
					OFPortStatisticsReply ofstat = (OFPortStatisticsReply) values
							.get(i);
					PortStatistics stat = new PortStatistics();
					stat.setCollisions(ofstat.getCollisions());
					stat.setPortNumber(ofstat.getPortNumber());
					stat.setReceiveBytes(ofstat.getReceiveBytes());
					stat.setReceiveCRCErrors(ofstat.getReceiveCRCErrors());
					stat.setReceiveDropped(ofstat.getReceiveDropped());
					stat.setReceiveErrors(ofstat.getreceiveErrors());
					stat.setReceiveFrameErrors(ofstat.getReceiveFrameErrors());
					stat.setReceiveOverrunErrors(ofstat
							.getReceiveOverrunErrors());
					stat.setReceivePackets(ofstat.getreceivePackets());
					stat.setSwitchId("SW-" + swId);
					stat.setTransmitBytes(ofstat.getTransmitBytes());
					stat.setTransmitDropped(ofstat.getTransmitDropped());
					stat.setTransmitErrors(ofstat.getTransmitErrors());
					stat.setTransmitPackets(ofstat.getTransmitPackets());
					stats.getPortStatistics().add(stat);
				}
			} catch (Exception e) {
				System.out.println("Failure retrieving statistics from switch "
						+ sw);
			}
		}
		controllerItf.pushStatistics(stats);
	}

	public void run() {
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			this.sendStats();
		}
	}
}
