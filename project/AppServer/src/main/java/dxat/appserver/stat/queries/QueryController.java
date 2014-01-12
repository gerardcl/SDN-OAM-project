package dxat.appserver.stat.queries;


import java.io.IOException;

import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;

public class QueryController {
	
	
	public static long[] getTimevalues(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		FetchRequest request = rrdDb.createFetchRequest( type, end-start, end);
		FetchData fetchData = request.fetchData();
		
		return fetchData.getTimestamps();
	}
	
	public static double[] getCPUAvg(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
        
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
        request.setFilter("CpuAvg!");
        FetchData fetchData = request.fetchData();
		return fetchData.getValues("CpuAvg!");
	}
	
	public static double[] getMemoryPct(RrdDb rrdDb, long start, long end, String typeOfStat) throws IOException{
		
		ConsolFun type = ConsolFun.valueOf(typeOfStat);
		
		FetchRequest request = rrdDb.createFetchRequest(type, end-start, end);
		request.setFilter("MemoryPCT!");
		FetchData fetchData = request.fetchData();
		
		return fetchData.getValues("MemoryPCT!");
		
	}

}
