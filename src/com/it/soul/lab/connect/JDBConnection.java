package com.it.soul.lab.connect;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBConnection implements Serializable{
	
	public static enum DriverClass{
		MYSQL,
		PostgraySQLv7,
		DB2,
		OracleOCI9i,
		SQLServer;
		
		public String toString(){
			String result = "";
			switch (this) {
			case MYSQL:
				result = "com.mysql.jdbc.Driver";
				break;
			case PostgraySQLv7:
				result = "org.postgresql.Driver";
				break;
			case DB2:
				result = "COM.ibm.db2.jdbc.app.DB2Driver";
				break;
			case OracleOCI9i:
				result = "oracle.jdbc.driver.OracleDriver";
				break;
			case SQLServer:
				result = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
				break;
			default:
				result = "sun.jdbc.odbc.JdbcOdbcDriver";
				break;
			}
			return result;
		}
		
	}
	
	public static class Builder {
		private JDBConnection dbConnection;
		public Builder(String connectionURL){
			dbConnection = new JDBConnection();
			dbConnection.serverUrl = connectionURL;
		}
		public Builder driver(DriverClass driver){
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
	
	private JDBConnection(){}

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
