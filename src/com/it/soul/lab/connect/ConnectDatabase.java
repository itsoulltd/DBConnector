package com.it.soul.lab.connect;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDatabase implements Serializable{

	private static final long serialVersionUID = -6801905544609003454L;
	private String driver = null;
	private String serverUrl = null;
	private String user = null;
	private String password = null;

	public ConnectDatabase(String driver, String URL, String userName, String password)
	{
		this.driver = driver;
		this.serverUrl = URL;
		this.user = userName;
		this.password = password;
	}

	private void printMetaInfos(DatabaseMetaData dma) throws Exception{

		//checkForWarning(conn.getWarnings());
		System.out.println("\nConnected To "+dma.getURL());
		System.out.println("Driver  "+dma.getDriverName());
		System.out.println("driver Version  "+dma.getDriverVersion());
	}

	/**
	 * 
	 * @throws SQLException
	 */
	public void closeConnections(Connection conn) 
	throws SQLException{

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

	public Connection getConnection() throws SQLException,Exception{
		
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

	public String getDriver() {
		return driver;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
