package org.opensource.master.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.master.common.Constant;
import org.opensource.master.config.AppProperties;
import org.opensource.master.repository.AbstractRepositoryManager;

public class Generator implements Runnable{
	private final static Logger logger = LogManager.getLogger(Generator.class);
	private AbstractRepositoryManager repositoryManager = null;
	private String target_data_table = "myData";
	
	public Generator(AppProperties props, AbstractRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager; 
		if(props.getPropsMap().get(Constant.DB_TARGET_TABLE_NAME) != null){
    		target_data_table = props.getPropsMap().get(Constant.DB_TARGET_TABLE_NAME);
    	}
	}
	
	public void run() {
		try{
			final Connection con = repositoryManager.getConnection();
			logger.info("Connection set");
			
			if (!repositoryManager.isExist(con, target_data_table)){
				repositoryManager.createTargetDataTable(con, target_data_table);
			}
			
			Runnable runnable = new Runnable() {
				String query = "INSERT INTO "+target_data_table+"(value, created) VALUES(?, ?)";
				
	            public void run() {
	            	try {
	            		Random rf = new Random();
						PreparedStatement pst = con.prepareStatement(query);
						pst.setInt(1,rf.nextInt());
						pst.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
						pst.executeUpdate();
					} catch (Exception e) {
						e.printStackTrace();
					}
	            }
	        };
	        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	        service.scheduleAtFixedRate(runnable, 0, 100, TimeUnit.MILLISECONDS);
		
		}catch (Exception e) {
			logger.error(e.getMessage());
		}finally {
		}
	}
}
