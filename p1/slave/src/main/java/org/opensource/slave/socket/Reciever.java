package org.opensource.slave.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.format.DataContainer;
import org.opensource.slave.common.Constant;
import org.opensource.slave.config.AppProperties;
import org.opensource.slave.repository.AbstractRepositoryManager;

public class Reciever {
	private final static Logger logger = LogManager.getLogger(Reciever.class);

	private final int DEFAULT_PORT_NUM = 20000;
	private final String DEFAULT_IP_ADDRESS = "127.0.0.1";
	private final String DEFAULT_ID = "TEST";
	private int PORT = 20000;
	private String IP = "127.0.0.1";
	private String ID = "TEST";
	
	AbstractRepositoryManager repositoryManager;
	
	private String back_data_table = "myData";
	
	public Reciever(AppProperties props, AbstractRepositoryManager repositoryManager) {
		this.repositoryManager = repositoryManager;
		if(props.getPropsMap().get(Constant.MASTERPORT) != null){
			PORT = Integer.valueOf(props.getPropsMap().get(Constant.MASTERPORT));
		}else{
			PORT = DEFAULT_PORT_NUM;
		}
		if(props.getPropsMap().get(Constant.MASTERIP) != null){
			IP = props.getPropsMap().get(Constant.MASTERIP);
		}else{
			IP = DEFAULT_IP_ADDRESS;
		}
		if(props.getPropsMap().get(Constant.SLAVEID) != null){
			ID = props.getPropsMap().get(Constant.SLAVEID);
		}else{
			ID = DEFAULT_IP_ADDRESS;
		}
		
		if(props.getPropsMap().get(Constant.DB_BACKUP_TABLE_NAME) != null){
			back_data_table = props.getPropsMap().get(Constant.DB_BACKUP_TABLE_NAME);
    	}
	}
	
	public void startReciving(){
		Socket socket = null;
		ObjectOutputStream oos =null;
		ObjectInputStream ois = null;
		try {
			socket = new Socket(IP,PORT);
		
			oos = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Sending request to Socket Server");
			oos.writeObject(ID);
			ois = new ObjectInputStream(socket.getInputStream());
			
			while(true){
				if(!socket.isConnected()) break;
		        DataContainer dataContainer = (DataContainer) ois.readObject();
	            System.out.println("Message: " + dataContainer.getRowCount());
	            
	            backUpData(dataContainer);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}finally{
			 try {
				ois.close();
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void backUpData(DataContainer dataContainer) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		Connection con = null;
		try {
			con = repositoryManager.getConnection();
			
			if (!repositoryManager.isExist(con, back_data_table)){
				repositoryManager.createTargetDataTable(con, back_data_table);
			}
			
			Statement stmt = con.createStatement();
            con.setAutoCommit(false);
			PreparedStatement pstmt = con.prepareStatement(
                    "INSERT INTO "+back_data_table+"(value, created) VALUES(?,?)");
			
			for (List<String> row : dataContainer.getRowList()) {
                // Add each parameter to the row.
                pstmt.setInt(1, Integer.parseInt(row.get(0)));
                Date d = format.parse(row.get(1));
                pstmt.setTimestamp(2,  new Timestamp(d.getTime()));
                pstmt.addBatch();
            }
	     
            try {
                pstmt.executeBatch();
            } catch (Exception e) {
                System.out.println("Error message: " + e.getMessage());
                return; 
            }
            
            con.commit();
		} catch(Exception e) {
			logger.error(e);
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
}
