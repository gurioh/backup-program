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
import org.opensource.master.config.AppProperties;
import org.opensource.master.repository.AbstractRepositoryManager;
import org.opensource.master.repository.postgresql.PostgresqlRepositoryManager;
import org.opensource.master.socket.Sender;
import org.opensource.master.socket.WorkerPool;

public class SocketService {
	private final static Logger logger = LogManager.getLogger(SocketService.class);
	private AppProperties props;
	private WorkerPool workerPool;
	private int DEFAULT_PORT_NUM = 20000;
	private int port;
	private ServerSocket _serverSocket;
	private AbstractRepositoryManager repositoryManager = null;
	
	public SocketService() {}

	public void initialize(AppProperties props) {
		repositoryManager = new PostgresqlRepositoryManager(props);
		workerPool = new WorkerPool(props);
		this.props = props;
		
		if(props.getPropsMap().get("PORT") != null){
			port = Integer.valueOf(props.getPropsMap().get("PORT"));
		}else{
			port = DEFAULT_PORT_NUM;
		}

	}

	public void dataGenerateStart(){
		Thread th = new Thread(new Generator(props,repositoryManager));
		th.start();
	}
	
	private void CheckSocketStatus() {
		Runnable r2 = new Runnable() {
            public void run() {
            	System.out.println("Current Socket is "+workerPool.getActiveThreadCount());
            	workerPool.removeNotActiveWorker();
            }

        };
        
        ScheduledExecutorService heartBeat = Executors.newSingleThreadScheduledExecutor();
        heartBeat.scheduleAtFixedRate(r2, 0, 3, TimeUnit.SECONDS);
	}
	
	public void socketServerStart() {
		dataGenerateStart();
		//CheckSocketStatus();
		try {
			_serverSocket = new ServerSocket(port);
			Socket l_socket = null;
			while(true) {
				l_socket = _serverSocket.accept();
				Sender l_worker = (Sender) workerPool.getWorker();
				l_worker.setSocket(l_socket);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
