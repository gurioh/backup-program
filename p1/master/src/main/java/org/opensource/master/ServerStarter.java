package org.opensource.master;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.master.config.AppProperties;
import org.opensource.master.service.Generator;
import org.opensource.master.service.SocketService;

public class ServerStarter {
	private final static Logger logger = LogManager.getLogger(ServerStarter.class);
	
	private static void Start(AppProperties props) {
		
		Generator generator = new Generator(props);
		generator.start();
		
		SocketService service = new SocketService();
		service.initialize(props);
		service.start();
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
