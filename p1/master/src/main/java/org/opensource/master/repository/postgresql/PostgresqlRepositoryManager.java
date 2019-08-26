package org.opensource.master.repository.postgresql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensource.master.common.Constant;
import org.opensource.master.config.AppProperties;
import org.opensource.master.repository.AbstractRepositoryManager;
import org.opensource.master.service.Generator;

public class PostgresqlRepositoryManager extends AbstractRepositoryManager {
	private final static Logger logger = LogManager.getLogger(PostgresqlRepositoryManager.class);
	
	String url = "jdbc:postgresql://localhost/repo";
    String user = "henry";
    String password = "1234";
    
    public PostgresqlRepositoryManager() {}
    
    public PostgresqlRepositoryManager(AppProperties props) {
    	if(props.getPropsMap().get(Constant.DB_URL) != null){
    		url = props.getPropsMap().get(Constant.DB_URL);
    	}
    	if(props.getPropsMap().get(Constant.DB_USER) != null){
    		user = props.getPropsMap().get(Constant.DB_USER);
    	}
    	if(props.getPropsMap().get(Constant.DB_PASSWORD) != null){
    		password = props.getPropsMap().get(Constant.DB_PASSWORD);
    	}
    }
    
	@Override
	public Connection getConnection() throws SQLException {
		Connection con = DriverManager.getConnection(url,user,password);
		return con;
	}

	@Override
	public void createTargetDataTable(Connection con, String tableName) throws SQLException {
		String sql = "CREATE TABLE IF NOT EXISTS "+tableName+"("+
						 "value INTEGER NOT NULL,"+
						 "created TIMESTAMP NOT NULL"+
					  ")";
		logger.info("[SQL]="+sql);
		Statement st = null;
		try{
			st = con.createStatement();
			int result = st.executeUpdate(sql);
		}catch(Exception e){
			e.printStackTrace();
			st.close();
		}finally{
			st.close();
		}
		logger.info("Created");
	}

	@Override
	public boolean isExist(Connection con, String tableName) throws SQLException {
		boolean tExists = false;
	    try (ResultSet rs = con.getMetaData().getTables(null, null, tableName, null)) {
	        while (rs.next()) { 
	            String tName = rs.getString("TABLE_NAME");
	            if (tName != null && tName.equals(tableName)) {
	                tExists = true;
	                break;
	            }
	        }
	    }
	    return tExists;
	}
	
	@Override
	public void bulkInsert() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createSyncDataTable(Connection con, String name) throws SQLException {

		String sql = "CREATE TABLE IF NOT EXISTS " + name + "(" + "name text  PRIMARY KEY NOT NULL,"
				+ "cur_offset Integer NOT NULL" + ")";
		logger.info("[SQL]=" + sql);
		Statement st = null;
		try {
			st = con.createStatement();
			int result = st.executeUpdate(sql);
		} catch (Exception e) {
			e.printStackTrace();
			st.close();
		} finally {
			st.close();
		}
		logger.info("Created");
	}

}
