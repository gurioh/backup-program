package org.opensource.master.socket;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public abstract class AbstractWorker implements Runnable {
	public String id;
	
	public enum WorkerState{
		NON_ACTIVE, WORKING
	}
	
	private WorkerState _workerState = WorkerState.NON_ACTIVE;

	public WorkerState getWorkerState() {
		return _workerState;
	}

	public void setWorkerState(WorkerState _workerState) {
		this._workerState = _workerState;
	}
	
	public void isAvailable(){
		this._workerState = WorkerState.WORKING;
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
