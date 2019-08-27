package org.opensource.slave.service;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.slave.common.Constant;
import org.opensource.slave.config.AppProperties;
import org.opensource.slave.repository.AbstractRepositoryManager;
import org.opensource.slave.repository.postgresql.PostgresqlRepositoryManager;
import org.opensource.slave.socket.Reciever;

public class SocketService {
	private final static Logger logger = LogManager.getLogger(SocketService.class);
	
	private AbstractRepositoryManager repositoryManager = null;
	private AppProperties props;
	private String back_data_table = "myData";
	
	public SocketService() {
	}
	
	public void initialize(AppProperties props) {
		repositoryManager = new PostgresqlRepositoryManager(props);
		this.props = props;
		
		if(props.getPropsMap().get(Constant.DB_BACKUP_TABLE_NAME) != null){
			back_data_table = props.getPropsMap().get(Constant.DB_BACKUP_TABLE_NAME);
    	}
		
		try {
			repositoryManager.createTargetDataTable(back_data_table);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void socketClientStart() {
		Reciever reciever = new Reciever(props, repositoryManager);
		reciever.startReciving();
		logger.info("client start");
	}
	
}
