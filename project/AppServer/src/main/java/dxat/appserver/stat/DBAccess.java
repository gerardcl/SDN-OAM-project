package dxat.appserver.stat;

import static org.rrd4j.ConsolFun.AVERAGE;
import static org.rrd4j.ConsolFun.MAX;
import static org.rrd4j.ConsolFun.MIN;
import static org.rrd4j.DsType.GAUGE;
import static org.rrd4j.DsType.DERIVE;

import java.io.File;

import org.rrd4j.core.RrdDbPool;
import org.rrd4j.core.RrdDef;

import dxat.appserver.config.LoadConfig;

public class DBAccess {

	public static final int MAX_STEP = 1;
	public static final String rrdPath = LoadConfig.getProperty("rrd4j.dir");

	private RrdDbPool rrdDbPool = RrdDbPool.getInstance();

	public RrdDbPool getRrdDbPool() {
		return rrdDbPool;
	}

	public RrdDef createRrdDefController(String name, long start) {

		String path = rrdPath + name + ".rrd";

		RrdDef rrdDef = new RrdDef(path, start - 1, 1);
		rrdDef.setVersion(2);
		rrdDef.addDatasource("CpuAvg", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("MemoryPCT", GAUGE, 600, 0, Double.NaN);
		rrdDef.addArchive(AVERAGE, 0.5, 1, 60);
		rrdDef.addArchive(AVERAGE, 0.5, 60, 1140);
		rrdDef.addArchive(AVERAGE, 0.5, 3600, 24);
		rrdDef.addArchive(MAX, 0.5, 1, 60);
		rrdDef.addArchive(MAX, 0.5, 60, 1140);
		rrdDef.addArchive(MAX, 0.5, 3600, 24);
		rrdDef.addArchive(MIN, 0.5, 1, 60);
		rrdDef.addArchive(MIN, 0.5, 60, 1140);
		rrdDef.addArchive(MIN, 0.5, 3600, 24);
		return rrdDef;

	}

	public RrdDef createRrdDefPort(String name, long start) {
		String path = rrdPath + name + ".rrd";

		RrdDef rrdDef = new RrdDef(path, start - 1, 1);
		rrdDef.setVersion(2);
		rrdDef.addDatasource("receivePackets", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("transmitPackets", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveBytes", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("transmitBytes", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveDropped", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("transmitDropped", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveErrors", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("transmitErrors", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveFrameErrors", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveOverrunErrors", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveCRCErrors", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("collisions", DERIVE, 600, 0, Double.NaN);

		rrdDef.addArchive(AVERAGE, 0.5, 1, 60);
		rrdDef.addArchive(AVERAGE, 0.5, 60, 1140);
		rrdDef.addArchive(AVERAGE, 0.5, 3600, 24);
		rrdDef.addArchive(MAX, 0.5, 1, 60);
		rrdDef.addArchive(MAX, 0.5, 60, 1140);
		rrdDef.addArchive(MAX, 0.5, 3600, 24);
		rrdDef.addArchive(MIN, 0.5, 1, 60);
		rrdDef.addArchive(MIN, 0.5, 60, 1140);
		rrdDef.addArchive(MIN, 0.5, 3600, 24);
		return rrdDef;

	}

	public RrdDef createRrdDefSwitch(String name, long start) {

		String path = rrdPath + name + ".rrd";

		RrdDef rrdDef = new RrdDef(path, start - 1, 1);
		rrdDef.setVersion(2);
		rrdDef.addDatasource("packetCount", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("byteCount", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("flowCount", DERIVE, 600, 0, Double.NaN);
		rrdDef.addArchive(AVERAGE, 0.5, 1, 60);
		rrdDef.addArchive(AVERAGE, 0.5, 60, 1140);
		rrdDef.addArchive(AVERAGE, 0.5, 3600, 24);
		rrdDef.addArchive(MAX, 0.5, 1, 60);
		rrdDef.addArchive(MAX, 0.5, 60, 1140);
		rrdDef.addArchive(MAX, 0.5, 3600, 24);
		rrdDef.addArchive(MIN, 0.5, 1, 60);
		rrdDef.addArchive(MIN, 0.5, 60, 1140);
		rrdDef.addArchive(MIN, 0.5, 3600, 24);
		return rrdDef;
	}

	public RrdDef createRrdDefFlow(String name, long start) {

		String path = rrdPath + name + ".rrd";

		RrdDef rrdDef = new RrdDef(path, start - 1, 1);
		rrdDef.setVersion(2);
		rrdDef.addDatasource("packetCount", DERIVE, 600, 0, Double.NaN);
		rrdDef.addDatasource("byteCount", DERIVE, 600, 0, Double.NaN);
		rrdDef.addArchive(AVERAGE, 0.5, 1, 60);
		rrdDef.addArchive(AVERAGE, 0.5, 60, 1140);
		rrdDef.addArchive(AVERAGE, 0.5, 3600, 24);
		rrdDef.addArchive(MAX, 0.5, 1, 60);
		rrdDef.addArchive(MAX, 0.5, 60, 1140);
		rrdDef.addArchive(MAX, 0.5, 3600, 24);
		rrdDef.addArchive(MIN, 0.5, 1, 60);
		rrdDef.addArchive(MIN, 0.5, 60, 1140);
		rrdDef.addArchive(MIN, 0.5, 3600, 24);
		return rrdDef;
	}
	
	public boolean rrdFileExists(String name) {

		if (!(name == null)) {
			File rrdFile = new File(name);

			if (!rrdFile.exists()) {
				return false;
			} else
				return true;
		} else
			return false;

	}

	public String convertId(String name) {
		if (name.equals("floodLight"))
			return name;
		else {
			String nom = name.replace(":", "_");
			return nom;
		}

	}

	public String typeObject(String name) {
		if (name.contentEquals("floodLight")) {
			return "controller";
		} else {
			if (name.length() == 23)
				return "sw";
			else
				return "port";
		}
	}

}
