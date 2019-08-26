package org.opensource.slave.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.slave.config.AppProperties;
import org.opensource.slave.repository.AbstractRepositoryManager;
import org.opensource.slave.repository.postgresql.PostgresqlRepositoryManager;
import org.opensource.slave.socket.Reciever;

public class SocketService {
	private final static Logger logger = LogManager.getLogger(SocketService.class);
	
	private AbstractRepositoryManager repositoryManager = null;
	private AppProperties props;
	
	public SocketService() {
	}
	
	public void initialize(AppProperties props) {
		repositoryManager = new PostgresqlRepositoryManager(props);
		this.props = props;
	}
	
	public void socketClientStart() {
		Reciever reciever = new Reciever(props, repositoryManager);
		reciever.startReciving();
	}
	
	
}
