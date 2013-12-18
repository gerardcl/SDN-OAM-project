package dxat.appserver.stat.database;

import static org.rrd4j.ConsolFun.AVERAGE;
import static org.rrd4j.ConsolFun.MAX;
import static org.rrd4j.ConsolFun.MIN;
import static org.rrd4j.DsType.GAUGE;
import static org.rrd4j.DsType.COUNTER;

import java.io.File;

import org.rrd4j.DsType;
import org.rrd4j.core.RrdDbPool;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Util;

import roundrobin.exceptions.PahtNotFoundException;

public class DBAccess {
	
	//static final long START 		= Util.getTimestamp(2010, 6, 6, 6, 0);
    //static final long END   		= Util.getTimestamp(2010, 6, 6, 7, 0);
    static final int MAX_STEP 		= 1;
    static String rrdPath			= "/home/jesus/rrd4j-testing/";
    static String xmlPath 			= "/home/jesus/rrd4j-testing/";
    static String rrdRestoredPath 	= "/home/jesus/rrd4j-testing/";
    
    //private PoolingRrDb rrdDbPool; 
	private RrdDbPool rrdDbPool = RrdDbPool.getInstance();
    
	
	public RrdDbPool getRrdDbPool() {
		return rrdDbPool;
	}

	public RrdDef createRrdDefController(String name, long start){

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

	public RrdDef createRrdDefPuerto(String name, long start) {
		String path = rrdPath + name + ".rrd";
		
		RrdDef rrdDef = new RrdDef(path, start - 1, 1);
		rrdDef.setVersion(2);
		rrdDef.addDatasource("receivePackets", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("transmitPackets", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveBytes", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("transmitBytes", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveDropped", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("transmitDropped", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveErrors", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("transmitErrors", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveFrameErrors", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveOverrunErrors", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("receiveCRCErrors", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("collisions", GAUGE, 600, 0, Double.NaN);
		
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
	
	public RrdDef createRrdDefSwitch(String name, long start){
		
		String path = rrdPath + name + ".rrd";
		
		RrdDef rrdDef = new RrdDef(path, start - 1, 1);
		rrdDef.setVersion(2);
		rrdDef.addDatasource("packetCount", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("byteCount", GAUGE, 600, 0, Double.NaN);
		rrdDef.addDatasource("flowCount", GAUGE, 600, 0, Double.NaN);
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
	
	public boolean rrdFileExists(String name){
		
		  if (!(name == null)){
			  File rrdFile = new File(name);
			  
			  if (!rrdFile.exists())
			  {
				  return false;
			  }
			  else return true;
		  }
		  else return false;
		
	}
	
	public String convertId(String name){
		if(name.equals("floodLight"))
			return name;
		else{
			String nom = name.replace(":", "_");
			return nom;
		}
		
	}
	
	public String typeObject(String name){
		if (name.contentEquals("floodLight")){
			return "controller";
		}
		else{
			if (name.length()== 23)
				return "sw";
			else
				return "port";
		}
	}
	
}
