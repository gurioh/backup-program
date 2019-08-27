package org.opensource.slave;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.slave.config.AppProperties;
import org.opensource.slave.service.SocketService;

public class ClientStarter {
private final static Logger logger = LogManager.getLogger(ClientStarter.class);
	
	private static void Start(AppProperties props) {
		SocketService service = new SocketService();
		service.initialize(props);
		service.socketClientStart();
	}
	
	public static void main(String[] args) {
		
		String propFile = "";
		for(String l_arg : args) {
			if(l_arg.indexOf("") > -1) {
				propFile = l_arg.split("=")[1];
				
				AppProperties props = new AppProperties();
				props.initialize();
				props.loadConfig(propFile);
				
				
				Start(props);
			} else {
				logger.info("Put properties location");
			}
		}
		logger.info("There are no properties info");
	}
}
