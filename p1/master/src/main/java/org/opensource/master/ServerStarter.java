package org.opensource.master;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.master.config.AppProperties;
import org.opensource.master.service.SocketService;

public class ServerStarter {
	private final static Logger logger = LogManager.getLogger(ServerStarter.class);
	
	private static void Start(AppProperties props) {
		SocketService service = new SocketService();
		service.initialize(props);
		service.socketServerStart();
	}
	
	public static void main(String[] args) {
		String propFile = "C:\\Users\\Henry\\Documents\\카카오톡 받은 파일\\Project1\\Project1\\master\\src\\main\\resources\\config\\app.properties";
		AppProperties props = new AppProperties();
		props.initialize();
		props.loadConfig(propFile);
		
		
		Start(props);
	}

}
