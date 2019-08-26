package org.opensource.master.config;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AppProperties {
	private final static Logger logger = LogManager.getLogger(AppProperties.class);
	private static Map<String, String> _propsMap;
	
	
	public AppProperties() {
	}
	
	public void initialize() {
		_propsMap = new HashMap<>();
	}
	
	public void loadConfig(String configPath) {
		FileInputStream fis = null;
		File file = new File(configPath);
		Properties props = new Properties();
		try {
			if(file != null && file.exists()) {
				fis = new FileInputStream(configPath);
				props.load(new BufferedInputStream(fis));
				for(Entry<Object, Object> entry : props.entrySet()){
					_propsMap.put(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		} finally {
			if(fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public Map<String, String> getPropsMap() {
		return _propsMap;
	}


	public void setPropsMap(Map<String, String> _propsMap) {
		this._propsMap = _propsMap;
	}
	
	
	
	
}
