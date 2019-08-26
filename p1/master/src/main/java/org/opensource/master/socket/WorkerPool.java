package org.opensource.master.socket;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.master.config.AppProperties;
import org.opensource.master.repository.AbstractRepositoryManager;
import org.opensource.master.repository.postgresql.PostgresqlRepositoryManager;
import org.opensource.master.socket.AbstractWorker.WorkerState;

public class WorkerPool {
	private final static Logger logger = LogManager.getLogger(WorkerPool.class);
	private List<AbstractWorker> senderList = null;
	
	private final Object lockObject = new Object();
	private int activeThreadCount = 0;
	
	private AppProperties props;
	private AbstractRepositoryManager repositoryManager = null;
	
	
	public WorkerPool(AppProperties props) {
		this.senderList = new ArrayList<AbstractWorker>();
		this.props = props;
		this.repositoryManager = new PostgresqlRepositoryManager(props);
		logger.debug("WorkerPool is created.");
	}

	public List<AbstractWorker> getSenderList() {
		return senderList;
	}

	public void setSenderList(List<AbstractWorker> senderList) {
		this.senderList = senderList;
	}

	public int getActiveThreadCount() {
		return activeThreadCount;
	}

	public void setActiveThreadCount(int activeThreadCount) {
		this.activeThreadCount = activeThreadCount;
	}
	
	public void removeNotActiveWorker(){
		for(AbstractWorker worker : senderList){
			if(worker.getWorkerState().equals(WorkerState.NON_ACTIVE)){
				worker.killService();
				senderList.remove(worker);
			}
		}
	}

	public AbstractWorker getWorker() {
		AbstractWorker l_worker = null;
		
		synchronized (lockObject) {
			try {
				l_worker = (AbstractWorker)Class.forName("org.opensource.master.socket.Sender")
						.getConstructor(AppProperties.class, AbstractRepositoryManager.class)
						.newInstance(props, repositoryManager);
				l_worker.setId("Test");
				this.senderList.add(l_worker);
				this.activeThreadCount = this.senderList.size();
				Thread l_thread = new Thread(l_worker);
				l_thread.start();
			} catch(Exception e) {
				e.printStackTrace();
			} 
		}
		
		return l_worker;
	}
	
}
