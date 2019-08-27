package org.opensource.slave.repository;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class AbstractRepositoryManager {
	public abstract Connection getConnection() throws SQLException;
	public abstract void createTargetDataTable(String name) throws SQLException;
	public abstract boolean isExist(Connection con, String name) throws SQLException;
	public abstract void bulkInsert();
}
