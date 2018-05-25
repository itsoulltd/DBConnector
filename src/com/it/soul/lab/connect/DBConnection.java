package com.it.soul.lab.connect;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection implements Serializable{
	
	public static enum JDBCDriver{
		MYSQL,
		PostgraySQL,
		DB2,
		Oracle,
		SQLServer;
		
		public String toString(){
			String result = "";
			switch (this) {
			case MYSQL:
				result = "com.mysql.jdbc.Driver";
				break;
			default:
				break;
			}
			return result;
		}
		
	}
	
	public static class Builder {
		private DBConnection dbConnection;
		public Builder(String connectionURL){
			dbConnection = new DBConnection();
			dbConnection.serverUrl = connectionURL;
		}
		public Builder driver(JDBCDriver driver){
			dbConnection.driver = driver.toString();
			return this;
		}
		public Builder credential(String name, String password){
			dbConnection.user = name;
			dbConnection.password = password;
			return this;
		}
		public Connection build() throws SQLException, Exception{
			return dbConnection.getConnection();
		}
	}

	private static final long serialVersionUID = -6801905544609003454L;
	private String driver = null;
	private String serverUrl = null;
	private String user = null;
	private String password = null;
	
	private DBConnection(){}

	private void printMetaInfos(DatabaseMetaData dma) throws Exception{
		//checkForWarning(conn.getWarnings());
		System.out.println("\nConnected To "+dma.getURL());
		System.out.println("Driver  "+dma.getDriverName());
		System.out.println("driver Version  "+dma.getDriverVersion());
	}

	private Connection getConnection() throws SQLException, Exception{
		Connection conn = null;
		try{
			if(getDriver() != null 
					&& getServerUrl() != null
					&& getUser() != null ){
				Class.forName(getDriver());
				setPassword((getPassword() != null) ? getPassword() : "");
				conn = DriverManager.getConnection(getServerUrl(),getUser(),getPassword());
				printMetaInfos(conn.getMetaData());
			}else{
				throw new IllegalArgumentException("Database Engine driver OR Server URL OR UserName should not be empty.");
			}
		}catch(SQLException exp){
			throw exp;
		}catch(Exception e){
			throw e;
		}
		return conn;
	}

	private String getDriver() {
		return driver;
	}
	private String getServerUrl() {
		return serverUrl;
	}
	private String getUser() {
		return user;
	}
	private String getPassword() {
		return password;
	}
	private void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * @param Connection conn
	 * @throws SQLException
	 */
	public static void close(Connection conn) throws SQLException{
		if(conn != null && !conn.isClosed()){
			try{
				if(!conn.getAutoCommit())
					conn.commit();
			}catch(SQLException exp){
				if(!conn.getAutoCommit())
					conn.rollback();
				throw exp;
			}
			finally{
	        	try {
					if(conn != null && !conn.isClosed())
						conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        }
		}
	}
	
}
