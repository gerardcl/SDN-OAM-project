package dxat.appserver.stat;

import java.io.IOException;

import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;

import com.google.gson.Gson;

import dxat.appserver.config.LoadConfig;
import dxat.appserver.realtime.RealTimeManager;
import dxat.appserver.stat.pojos.FlowStat;
import dxat.appserver.stat.pojos.PortStat;
import dxat.appserver.stat.pojos.StatCollection;
import dxat.appserver.stat.pojos.StatResponse;
import dxat.appserver.stat.pojos.SwitchStat;
import dxat.appserver.stat.queries.QueryController;
import dxat.appserver.stat.queries.QueryFlow;
import dxat.appserver.stat.queries.QueryPort;
import dxat.appserver.stat.queries.QuerySwitch;

//LLamada a la base de datos.
public class StatManager {
	private DBAccess db;
	private static StatManager instance;

	private StatManager() {
		this.db = new DBAccess();
	}

	public static StatManager getInstance() {
		if (instance == null)
			instance = new StatManager();
		return instance;
	}

	public void pushStat(StatCollection statcollection) throws IOException {

		// Pushing controllers statistics
		// Creating round robin database for Controller

		// System.out.print(new Gson().toJson(statcollection));

		if (!db.rrdFileExists(LoadConfig.getProperty("rrd4j.dir")
				+ "controller.rrd")) {
			RrdDef rrdDef = db.createRrdDefController(Util.getTime());
			RrdDb rrdInit = new RrdDb(rrdDef);
			rrdInit.close();
		}

		// Updating round robin database for controller
		else {

			RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(
					LoadConfig.getProperty("rrd4j.dir") + "controller.rrd");
			if (rrdDb.getLastUpdateTime() == Util.getTime())
				return;
			Sample sample = rrdDb.createSample();
			sample.setValue("CpuAvg!", statcollection.getControllerStat()
					.getCpuAvg());
			sample.setValue("MemoryPCT!", statcollection.getControllerStat()
					.getMemoryPct());
			sample.update();
			db.getRrdDbPool().release(rrdDb);
		}

		// Pushing Switch statitistcs
		for (SwitchStat sw : statcollection.getSwitchStatCollection()) {
			String resourcepath = LoadConfig.getProperty("rrd4j.dir")
					+ db.convertId(sw.getSwitchId()) + ".switch.rrd";
			if (!db.rrdFileExists(resourcepath)) {

				RrdDef rrdDef = db.createRrdDefSwitch(
						db.convertId(sw.getSwitchId()), Util.getTime());
				RrdDb rrdInit = new RrdDb(rrdDef);
				rrdInit.close();
			} else {
				RrdDb rrdDb = db.getRrdDbPool()
						.requestRrdDb(resourcepath);
				if (rrdDb.getLastUpdateTime() == Util.getTime())
					return;
				Sample sample = rrdDb.createSample();
				sample.setValue("packetCount!", sw.getPacketCount());
				sample.setValue("byteCount!", sw.getByteCount());
				sample.setValue("flowCount!", sw.getFlowCount());
				sample.update();
				db.getRrdDbPool().release(rrdDb);
			}
		}

		// Pushing port statistics
		for (PortStat port : statcollection.getPortStatCollection()) {
			if (!db.rrdFileExists(LoadConfig.getProperty("rrd4j.dir")
					+ db.convertId(port.getPortId()) + ".port.rrd")) {
				RrdDef rrdDef = db.createRrdDefPort(
						db.convertId(port.getPortId()), Util.getTime());
				RrdDb rrdInit = new RrdDb(rrdDef);
				rrdInit.close();
			} else {
				RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(
						LoadConfig.getProperty("rrd4j.dir")
								+ db.convertId(port.getPortId() + ".port.rrd"));
				if (rrdDb.getLastUpdateTime() == Util.getTime())
					return;
				Sample sample = rrdDb.createSample();
				sample.setValue("receivePackets!", port.getReceivePackets());
				sample.setValue("transmitPackets!", port.getTransmitPackets());
				sample.setValue("receiveBytes!", port.getReceiveBytes());
				sample.setValue("transmitBytes!", port.getTransmitBytes());
				sample.setValue("receiveDropped!", port.getReceiveDropped());
				sample.setValue("transmitDropped!", port.getTransmitDropped());
				sample.setValue("receiveErrors!", port.getReceiveErrors());
				sample.setValue("transmitErrors!", port.getTransmitErrors());
				sample.setValue("receiveFrameErrors!",
						port.getReceiveFrameErrors());
				sample.setValue("receiveOverrunError!",
						port.getReceiveOverrunErrors());
				sample.setValue("receiveCRCErrors!", port.getReceiveCRCErrors());
				sample.setValue("collisions!", port.getCollisions());
				sample.update();
				db.getRrdDbPool().release(rrdDb);
			}

		}

		// Pushing Flow statitistcs
		for (FlowStat flowStat : statcollection.getFlowStatCollection()) {
			//RealTimeManager.getInstance().broadcast("[STAT] " + (new Gson().toJson(flowStat)));
			if (!db.rrdFileExists(LoadConfig.getProperty("rrd4j.dir")
					+ db.convertId(flowStat.getName()) + ".flow.rrd")) {
				RrdDef rrdDef = db.createRrdDefFlow(
						db.convertId(flowStat.getName()), Util.getTime());
				RrdDb rrdInit = new RrdDb(rrdDef);
				rrdInit.close();
			} else {
				RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(
						LoadConfig.getProperty("rrd4j.dir")
								+ db.convertId(flowStat.getName() + ".flow.rrd"));
				if (rrdDb.getLastUpdateTime() == Util.getTime())
					return;
				Sample sample = rrdDb.createSample();
				sample.setValue("packetCount!", flowStat.getPacketCount());
				sample.setValue("byteCount!", flowStat.getByteCount());
				sample.update();
				db.getRrdDbPool().release(rrdDb);
			}
		}

	}

	public StatResponse getSwitchStat(String itemId, String statParameter,
			String typeOfStat, String granularity) throws IOException {
		String resourcePath = LoadConfig.getProperty("rrd4j.dir")
				+ db.convertId(itemId) + ".switch.rrd";
		StatResponse response = new StatResponse();
		long actualTimeStamp = Util.getTime();
		long start = getTimeStampGran(granularity)-1;
		long end = getEndTimeNormalized(granularity, actualTimeStamp);

		RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(resourcePath);

		response.setIdObject(itemId);
		response.setParameter(statParameter);
		response.setTimeAxxis(QueryController.getTimevalues(rrdDb, start, end,
				typeOfStat));

		switch (statParameter) {
		case "packetCount":
			response.setValueAxxis(QuerySwitch.getPacketCount(rrdDb, start,
					end, typeOfStat));
			break;
		case "byteCount":
			response.setValueAxxis(QuerySwitch.getByteCount(rrdDb, start, end,
					typeOfStat));
			break;
		case "flowCount":
			response.setValueAxxis(QuerySwitch.getFlowCount(rrdDb, start, end,
					typeOfStat));
			break;
		}
		db.getRrdDbPool().release(rrdDb);
		return response;
	}

	public StatResponse getFlowStat(String itemId, String statParameter,
			String typeOfStat, String granularity) throws IOException {
		String resourcePath = LoadConfig.getProperty("rrd4j.dir")
				+ db.convertId(itemId) + ".flow.rrd";
		StatResponse response = new StatResponse();
		long actualTimeStamp = Util.getTime();
		long start = getTimeStampGran(granularity)-1;
		long end = getEndTimeNormalized(granularity, actualTimeStamp);

		RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(resourcePath);

		response.setIdObject(itemId);
		response.setParameter(statParameter);
		response.setTimeAxxis(QueryFlow.getTimevalues(rrdDb, start, end,
				typeOfStat));

		switch (statParameter) {
		case "packetCount":
			response.setValueAxxis(QueryFlow.getPacketCount(rrdDb, start, end,
					typeOfStat));
			break;
		case "byteCount":
			response.setValueAxxis(QueryFlow.getByteCount(rrdDb, start, end,
					typeOfStat));
			break;
		}
		db.getRrdDbPool().release(rrdDb);
		return response;
	}

	public StatResponse getControllerStat(String statParameter,
			String typeOfStat, String granularity) throws IOException {
		String resourcePath = LoadConfig.getProperty("rrd4j.dir")
				+"controller.rrd";
		StatResponse response = new StatResponse();
		long actualTimeStamp = Util.getTime();
		long start = getTimeStampGran(granularity)-1;
		long end = getEndTimeNormalized(granularity, actualTimeStamp);

		RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(resourcePath);

		response.setIdObject("Controller");
		response.setParameter(statParameter);
		response.setTimeAxxis(QueryController.getTimevalues(rrdDb, start, end,
				typeOfStat));

		switch (statParameter) {
		case "CpuAvg":
			response.setValueAxxis(QueryController.getCPUAvg(rrdDb, start, end,
					typeOfStat));
			break;
		case "MemoryPCT":
			response.setValueAxxis(QueryController.getMemoryPct(rrdDb, start,
					end, typeOfStat));
			break;
		}
		db.getRrdDbPool().release(rrdDb);
		return response;
	}

	public StatResponse getPortStat(String itemId, String statParameter,
			String typeOfStat, String granularity) throws IOException {
		String resourcePath = LoadConfig.getProperty("rrd4j.dir")
				+ db.convertId(itemId) + ".port.rrd";
		StatResponse response = new StatResponse();
		long actualTimeStamp = Util.getTime();
		long start = getTimeStampGran(granularity)-1;
		long end = getEndTimeNormalized(granularity, actualTimeStamp);

		RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(resourcePath);

		response.setIdObject(itemId);
		response.setParameter(statParameter);
		response.setTimeAxxis(QueryController.getTimevalues(rrdDb, start, end,
				typeOfStat));

		switch (statParameter) {
		case "receivePackets":
			response.setValueAxxis(QueryPort.getReceivePackets(rrdDb, start,
					end, typeOfStat));
			break;
		case "transmitPackets":
			response.setValueAxxis(QueryPort.getTransmitPackets(rrdDb, start,
					end, typeOfStat));
			break;
		case "receiveBytes":
			response.setValueAxxis(QueryPort.getReceiveBytes(rrdDb, start, end,
					typeOfStat));
			break;
		case "transmitBytes":
			response.setValueAxxis(QueryPort.getTransmitBytes(rrdDb, start,
					end, typeOfStat));
			break;
		case "receiveDropped":
			response.setValueAxxis(QueryPort.getReceiveDropped(rrdDb, start,
					end, typeOfStat));
			break;
		case "transmitDropped":
			response.setValueAxxis(QueryPort.getTransmitDropped(rrdDb, start,
					end, typeOfStat));
			break;
		case "receiveErrors":
			response.setValueAxxis(QueryPort.getReceiveErrors(rrdDb, start,
					end, typeOfStat));
			break;
		case "transmitErrors":
			response.setValueAxxis(QueryPort.getTransmitErrors(rrdDb, start,
					end, typeOfStat));
			break;
		case "receiveFrameErrors":
			response.setValueAxxis(QueryPort.getReceiveFrameErrors(rrdDb,
					start, end, typeOfStat));
			break;
		case "receiveOverrunErrors":
			response.setValueAxxis(QueryPort.getReceiveOverrunErrors(rrdDb,
					start, end, typeOfStat));
			break;
		case "receiveCRCErrors":
			response.setValueAxxis(QueryPort.getReceiveCRCErrors(rrdDb, start,
					end, typeOfStat));
			break;
		case "collisions":
			response.setValueAxxis(QueryPort.getCollisions(rrdDb, start, end,
					typeOfStat));
			break;
		}
		db.getRrdDbPool().release(rrdDb);
		return response;
	}

	private long getTimeStampGran(String granularity) {
		long value = 0;
		switch (granularity) {
		case "second":
			value = (long) 1;
			break;
		case "minute":
			value = (long) 60;
			break;
		case "hour":
			value = (long) 3600;
			break;
		case "day":
			value = (long) 3600 * 24;
			break;
		case "week":
			value = (long) 3600 * 24 * 7;
			break;
		case "month":
			value = (long) 3600 * 24 * 30;
			break;
		case "year":
			value = (long) 3600 * 24 * 30 * 365;
		}
		return value;
	}
	private long getEndTimeNormalized(String granularity, long time){
		long value = 0;
		switch (granularity){
		case "second":
			value = (long) (time-1);
			break;
		case "minute":
			value = (long) (time-1);
			break;
		case "hour":
			value = (long) (time-60);
			break;
		case "day":
			value = (long) (time-3600);
			break;
		case "week":
			value = (long) (time-(3600*24));
			break;
		case "month":
			value = (long) (time-(3600*24*7));
			break;
		case "year":
			value = (long) (time-(3600*24*7*30));
			break;
		}
		return value;
	}
}
