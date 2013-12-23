package dxat.appserver.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class LoadConfig {
	private static LoadConfig instance = null;
	private Properties props = null;
	private HashMap<String, String> propertiesMap = null;

	private LoadConfig() {
		super();
		InputStream ins = null;
		propertiesMap = new HashMap<String, String>();
		try {
			ins = getClass().getResourceAsStream(
					"config.properties");
			if (ins==null)
				System.out.println("MCET");
			props = new Properties();
			props.load(ins);
			for (String key : props.stringPropertyNames()) {
				propertiesMap.put(key, props.getProperty(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (ins != null)
				try {
					ins.close();
				} catch (IOException e) {
				}
		}
	}

	private String getPrivProperty(String key) {
		return propertiesMap.get(key);
	}

	public static String getProperty(String key) {
		if (instance == null) {
			instance = new LoadConfig();
		}

		return instance.getPrivProperty(key);
	}

}
