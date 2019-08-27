package org.opensource.master.service;

import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.master.common.Constant;
import org.opensource.master.config.AppProperties;
import org.opensource.master.repository.AbstractRepositoryManager;
import org.opensource.master.repository.postgresql.PostgresqlRepositoryManager;
import org.opensource.master.socket.Sender;
import org.opensource.master.socket.WorkerPool;

public class SocketService {
	private final static Logger logger = LogManager.getLogger(SocketService.class);
	private WorkerPool workerPool;
	private AbstractRepositoryManager repositoryManager = null;
	private int DEFAULT_PORT_NUM = 20000;
	private int port;
	private String sync_table = "sync_info";
	private ServerSocket _serverSocket;
	
	public SocketService() {}

	public void initialize(AppProperties props) {
		workerPool = new WorkerPool(props);
		this.repositoryManager = new PostgresqlRepositoryManager(props);
		if(props.getPropsMap().get("PORT") != null){
			port = Integer.valueOf(props.getPropsMap().get("PORT"));
		}else{
			port = DEFAULT_PORT_NUM;
		}
		
		if(props.getPropsMap().get(Constant.DB_SYNC_TABLE_NAME) != null){
			sync_table = props.getPropsMap().get(Constant.DB_SYNC_TABLE_NAME);
    	}
		
		try {
			repositoryManager.createSyncDataTable(sync_table);
		} catch (SQLException e) {
			e.printStackTrace();
		};

	}

	public void start() {
		try {
			_serverSocket = new ServerSocket(port);
			Socket l_socket = null;
			while(true) {
				l_socket = _serverSocket.accept();
				Sender l_worker = (Sender) workerPool.getWorker();
				l_worker.setSocket(l_socket);
				
				ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
				service.scheduleAtFixedRate(l_worker, 0, 1000, TimeUnit.MILLISECONDS);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
