/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.it.soul.lab.connect;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DBConnectionPool implements Serializable{
    
	private static final long serialVersionUID = 8229833245259862179L;
	private static Object _lock = new Object();
	private static DBConnectionPool _sharedInstance = null;
	private static int activeConnectionCount = 0;
	
	private static InitialContext initCtx=null;
    private static SortedMap<String, DataSource> dataSourcePool = null;
    private static String _DEFAULT_KEY = null;
    
    /**
     * Example JNDILookUp String
     * "java:comp/env/jdbc/MySQLDB"
     * 
     * @param JNDILookUp
     * @throws NamingException
     * @throws IllegalArgumentException
     */
	private DBConnectionPool(String JNDILookUp) throws NamingException,IllegalArgumentException{
		if(JNDILookUp != null && !JNDILookUp.trim().equals("")){
			try{
	            initCtx = new InitialContext();
	            createNewSource(JNDILookUp);
	        }catch(NamingException ne){
	            throw ne;
	        }
		}else{
			throw new IllegalArgumentException("Jndi Look Up string must not null!!!");
		}
	}
	
	private static void createNewSource(String JNDILookUp){
		String[] arr = JNDILookUp.split("/");
		String lookUpName = arr[arr.length-1];
		if(_DEFAULT_KEY == null){
			_DEFAULT_KEY = lookUpName;
		}
		if(initCtx != null){
			try {
				if(!getDataSourcePool().containsKey(lookUpName)){
					DataSource source = (DataSource) initCtx.lookup(JNDILookUp);
					getDataSourcePool().put(lookUpName, source);
				}
			} catch (NamingException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * User must have to call this method first.
	 * @param JNDILookUp
	 * @return
	 * @throws Exception
	 */
	public static void configureConnectionPool(String JNDILookUp) 
	{
		synchronized (_lock) {
			if(_sharedInstance == null){
				try{
					_sharedInstance = new DBConnectionPool(JNDILookUp);
				}catch(Exception e){
					e.printStackTrace();
				}
			}else{
				if(JNDILookUp != null && !JNDILookUp.trim().equals("")){
					createNewSource(JNDILookUp);
				}
			}
		}
	}
	
	/**
	 * Use this method to get JDBC connection from connections pool.
	 * @return
	 * @throws Exception
	 */
	public static DBConnectionPool shared() 
	{
		synchronized (_lock) {
			if(_sharedInstance != null){
				return _sharedInstance;
			}
		}
		System.out.println("Please Call configureConnectionPool at least once.");
		return null;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return _sharedInstance;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		_sharedInstance = null;
	}
	
	@Override
	public String toString() {
		return "ConnectDatabaseJDBCSingleton : " + serialVersionUID;
	}
	
	public static int getActiveConnectionCount(){
		return activeConnectionCount;
	}
	
	private static void increasePoolCount(){
		synchronized (_lock) {
			activeConnectionCount++;
		}
	}
	
	//increase decrease 
	private static void decreasePoolCount(){
		synchronized (_lock) {
			activeConnectionCount--;
		}
	}
	
	private static SortedMap<String, DataSource> getDataSourcePool() {
		if(dataSourcePool == null){
			dataSourcePool = new TreeMap<String, DataSource>();
		}
		return dataSourcePool;
	}
	
	///////////////////////////////JDBL Connection Pooling/////////////////////////
	
	private DataSource findSourceByName(String key){
		if(!getDataSourcePool().containsKey(key))
			return getDataSourcePool().get(_DEFAULT_KEY);
		else
			return getDataSourcePool().get(key);
	}
	
	/**
	 * 
	 */
	synchronized public Connection getConnectionFromPool() throws SQLException{
    	Connection con = null;
        try{
        	con = findSourceByName(_DEFAULT_KEY).getConnection();
        	DBConnectionPool.increasePoolCount();
        }catch(SQLException sqe){
            throw sqe;
        }       
        return con;
    } 
	
	synchronized public Connection getConnectionFromPool(String key) throws SQLException{
    	Connection con = null;
        try{
        	con = findSourceByName(key).getConnection();
        	DBConnectionPool.increasePoolCount();
        }catch(SQLException sqe){
            throw sqe;
        }       
        return con;
    } 
    
	/**
	 * 
	 * @param userName
	 * @param pass
	 * @return
	 * @throws SQLException
	 */
    synchronized public Connection getConnectionFromPool(String key, String userName , String password) 
    throws SQLException{
    	Connection con = null;
    	try{
            con = findSourceByName(key).getConnection(userName,password);
            DBConnectionPool.increasePoolCount();
        }catch(SQLException sqe){
        	throw sqe;
        }
        return con;       
        
    }   
    
    /**
     * 
     * @param conn
     * @throws SQLException
     */
    synchronized public void releaseConnection(Connection conn)
    {
        try{
            if(conn != null && ! conn.getAutoCommit()){
            	conn.commit();
            }
        }catch(SQLException sqe){
        	try {
				if(!conn.getAutoCommit())
					conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
            sqe.printStackTrace();
        }
        finally{
        	try {
				if(conn != null && !conn.isClosed())
					conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
        	DBConnectionPool.decreasePoolCount();
        }
    }

    ////////////////////////////////////End Pooling////////////////////////////
}