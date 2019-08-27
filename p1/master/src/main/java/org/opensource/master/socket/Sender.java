package org.opensource.master.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.master.common.Constant;
import org.opensource.master.config.AppProperties;
import org.opensource.master.format.DataContainer;
import org.opensource.master.repository.AbstractRepositoryManager;

public class Sender extends AbstractWorker{

	private final static Logger logger = LogManager.getLogger(Sender.class);
	private AbstractRepositoryManager repositoryManager = null;
	private int LIMIT = 100;
	private Socket _socket;
	private String sync_table = "sync_info";
	private String target_data_table = "myData";
	
	String slaveID = "";
	ObjectInputStream ois = null;
	ObjectOutputStream oos = null;
	Connection con = null;
	public Sender(AppProperties props, AbstractRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager; 
		if(props.getPropsMap().get(Constant.DB_TARGET_TABLE_NAME) != null){
    		target_data_table = props.getPropsMap().get(Constant.DB_TARGET_TABLE_NAME);
    	}
		if(props.getPropsMap().get(Constant.DB_DATA_LIMIT) != null){
			LIMIT = Integer.parseInt(props.getPropsMap().get(Constant.DB_DATA_LIMIT));
    	}
		if(props.getPropsMap().get(Constant.DB_SYNC_TABLE_NAME) != null){
			sync_table = props.getPropsMap().get(Constant.DB_SYNC_TABLE_NAME);
    	}
	}
	
	public void setSocket(Socket p_socket) {
		_socket = p_socket;
		try {
			ois = new ObjectInputStream(_socket.getInputStream());
            slaveID = (String) ois.readObject();
            logger.info("Connected client ID : " + slaveID);
            oos = new ObjectOutputStream(_socket.getOutputStream());
			
            con = repositoryManager.getConnection();
			logger.info("Client connection set");
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e);
		}finally {
			
		}
	}
	
	@Override
	public void run() {
		DataContainer dataContainer = new DataContainer();
		dataContainer.setSlaveID(slaveID);
    	String sql = buildSyncInfoSQL(slaveID);
    	int offset = 0;
    	
    	try {
    		Statement st = con.createStatement();
    		ResultSet rs = st.executeQuery(sql);
    		
    		if (rs.next()) {
    			offset = rs.getInt(2);
            }
    		sql = buildSelectDataSQL(String.valueOf(offset));
    		
    		rs = st.executeQuery(sql);
    		int count = 0;
    		while(rs.next()){
    			dataContainer.add(
    				new ArrayList<>(
    					Arrays.asList(
    							String.valueOf(rs.getInt(1)), 
    							rs.getTimestamp(2).toString()
    					)
    				)
    			);
    			count++;
    		}
    		
    		oos.writeObject(dataContainer);
    		
    		sql = buildUpdateOffsetSQL(slaveID, offset+count);
    		int var = st.executeUpdate(sql);
    		
		} catch (Exception e) {
			setWorkerState(WorkerState.NON_ACTIVE);
			try {
				ois.close();
				oos.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
    		try {
				this.wait();
				
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public String buildSelectDataSQL(String offset){
		StringBuilder sql = new StringBuilder("SELECT");
					  sql.append(" * ");
					  sql.append("FROM ");
					  sql.append(target_data_table);
					  sql.append(" OFFSET ").append(offset);
					  sql.append(" LIMIT ").append(LIMIT);
	    return sql.toString();
	}
	
	public String buildSyncInfoSQL(String workerId){
		StringBuilder sql = new StringBuilder("SELECT");
					  sql.append(" * ");
					  sql.append("FROM ");
					  sql.append(sync_table);
					  sql.append(" WHERE ").append("name = '").append(workerId).append("'");
	    return sql.toString();
	}
	
	private String buildUpdateOffsetSQL(String workerId, int offset) {
		StringBuilder sql = new StringBuilder("INSERT INTO "+sync_table+" (name, cur_offset)");
		  sql.append(" VALUES (");
		  sql.append("'").append(workerId).append("',");
		  sql.append(String.valueOf(offset)).append(")");
		  sql.append(" ON CONFLICT (name) DO ");
		  sql.append("UPDATE SET");
		  sql.append(" name = '").append(workerId).append("',");
		  sql.append(" cur_offset = ").append(offset).append("");

		return sql.toString();
	}

}
