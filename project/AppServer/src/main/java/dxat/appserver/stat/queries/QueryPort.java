package dxat.appserver.stat.queries;

import java.io.IOException;

import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;

public class QueryPort {

	public static long[] getTimevalues(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		FetchData fetchData = request.fetchData();
		return fetchData.getTimestamps();
	}
	
	public static double[] getReceivePackets(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("receivePackets!");
		FetchData fetchData = request.fetchData();
		return fetchData.getValues("receivePackets!");
	}
	
	public static double[] getTransmitPackets(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("transmitPackets!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("transmitPackets!");
	}
	
	public static double[] getReceiveBytes(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("receiveBytes!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("receiveBytes!");
	}
	
	public static double[] getTransmitBytes(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("transmitBytes!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("transmitBytes!");
	}
	
	public static double[] getReceiveDropped(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("receiveDropped!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("receiveDropped!");
	}
	
	public static double[] getTransmitDropped(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("transmitDropped!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("transmitDropped!");
	}
	
	public static double[] getReceiveErrors(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("receiveErrors!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("receiveErrors!");
	}

	public static double[] getTransmitErrors(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("transmitErrors!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("transmitErrors!");
	}
	
	public static double[] getReceiveFrameErrors(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("receiveFrameErrors!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("receiveFrameErrors!");
	}	

	public static double[] getReceiveOverrunErrors(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("receiveOverrunError!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("receiveOverrunError!");
	}	

	public static double[] getReceiveCRCErrors(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("receiveCRCErrors!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("receiveCRCErrors!");
	}

	public static double[] getCollisions(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("collisions!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("collisions!");
	}	

}
