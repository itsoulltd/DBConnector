package com.it.soul.lab.sql;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.it.soul.lab.sql.query.SQLBuilder;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.SQLQuery.ComparisonType;
import com.it.soul.lab.sql.query.SQLQuery.DataType;
import com.it.soul.lab.sql.query.SQLQuery.Logic;

public class SQLExecutor implements Serializable{

	private static final long serialVersionUID = 6052074650432885583L;
	private Connection conn = null;

	public SQLExecutor(Connection conn){ this.conn = conn; }
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		close();//so that unreleased statement object goes to garbage.
	}
	
	/**
     * Following object container holds all Statement object belongs to any connections
     */
    private List<Statement> statementHolder = null;
    
    public List<Statement> getStatementHolder() {
    	if(null == statementHolder){
    		statementHolder = new ArrayList<Statement>();
    	}
		return statementHolder;
	}

	public void setStatementHolder(List<Statement> statementHolder) {
		this.statementHolder = statementHolder;
	}
	
	public void close(){
		try {
			int count = getStatementHolder().size();
			Boolean isAllCloed = true;
			if(count > 0){
				for (Statement iterable_element : getStatementHolder()) {
					try{
						iterable_element.close();
					}catch (SQLException e){
						isAllCloed = false;
					}
				}
			}
			getStatementHolder().clear();
			System.out.println("Retain Statement count was "+ count + ". All has been Closed : "+ (isAllCloed ? "YES":"NO"));
			closeConnections(conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void closeConnections(Connection conn) 
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
				System.out.println("Executor Has Closed.");
			}
		}
	}
	
	/**
	 * Display rows in a Result Set
	 * @param rst
	 */
	public void displayResultSet(ResultSet rst){
		
		try{
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			ResultSetMetaData rsmd=rst.getMetaData();
			int numCol = rsmd.getColumnCount();
			int totalHeaderLenght = 0;
			for(int x = 1; x <= numCol; x++){
				if(x > 1)System.out.print(",  ");
				System.out.print(rsmd.getColumnLabel(x));
				totalHeaderLenght += rsmd.getColumnLabel(x).length() + 5;
			}
			
			System.out.println("");
			for(int x = 0; x <= totalHeaderLenght; x++){
				System.out.print("-");
			}
			System.out.println("");
			
			boolean more = rst.next();
			while(more){
				for(int x = 1; x <= numCol; x++){
					if(x > 1)System.out.print(",    ");
					System.out.print(rst.getString(x));
				}
				System.out.println("");
				more=rst.next();
			}
		}catch(SQLException exp){
			exp.getStackTrace();
		}
	}
	
	/**
	 * 
	 * @param o
	 */
	public void displayCollection(Object o){
		if(o instanceof List){
			List<?> ox = (List<?>)o;
			for(Object x : ox){
				System.out.println(x.toString());
			}
			
		}else if(o instanceof Map){
			Map<?,?> ox = (Map<?,?>)o;
			System.out.println(ox.toString());
		}else if(o instanceof Set){
			Set<?> ox = (Set<?>)o;
			System.out.println(ox.toString());
		}
	}
	
////////////////////////////////////Block Of Queries///////////////////////
	
	 
	public boolean executeTableManipulation(String query)
    throws SQLException,Exception{
		
		if(query == null 
				|| query.length() <=0 
				|| !query.trim().toLowerCase().startsWith("create")
				|| !query.trim().toLowerCase().startsWith("delete")
				|| !query.trim().toLowerCase().startsWith("alter")){
			throw new Exception("Bad Formated Query : " + query);
		}
    	
        boolean isCreated = false;
        PreparedStatement stmt = null;
        try{ 
            if(conn != null){
            	conn.setAutoCommit(false);
                stmt = conn.prepareStatement(query);
                stmt.executeUpdate();
                isCreated = true;
                if(!conn.getAutoCommit())
                	conn.commit(); 
            }            
        }catch(SQLException exp){
        	
        	if(!conn.getAutoCommit())
        		conn.rollback();
        	isCreated = false;
        	throw exp;
        }finally{
        	if(stmt != null)
        		stmt.close();
        	conn.setAutoCommit(true);
        }
        return isCreated;		
    }
	
	
    /**
     * Query for Update,Insert,Delete
     * @param conn
     * @param query
     * @return Number Of affected rows
     */
    public int executeCUDQuery(String query)
    throws SQLException,Exception{
    	
    	if(query == null 
				|| query.length() <=0 
				|| !query.trim().toLowerCase().startsWith("insert")
				|| !query.trim().toLowerCase().startsWith("update")
				|| !query.trim().toLowerCase().startsWith("delete")){
			throw new Exception("Bad Formated Query : " + query);
		}
    	
        int rowUpdate = 0;
        PreparedStatement stmt = null;
        try{ 
            if(conn != null){
                stmt = conn.prepareStatement(query);
                rowUpdate = stmt.executeUpdate();
                if(!conn.getAutoCommit())
                	conn.commit(); 
            }            
        }catch(SQLException exp){
        	
        	if(!conn.getAutoCommit())
        		conn.rollback(); 
            throw exp;
        }finally{
        	if(stmt != null)
        		stmt.close();
        }
        return rowUpdate;		
    }
    
    /**
     * 
     * @param conn
     * @param tableName
     * @param setParams
     * @param whereLogic
     * @param operators
     * @param whereClause
     * @return int as affected row count 
     * @throws SQLException
     */
    public int executeUpdate(String tableName
    		, List<Property> setParameter
    		, Logic whereLogic
    		, List<Property> whereClause)
    throws SQLException,Exception{
    	
    	if(setParameter == null 
    			|| setParameter.size() <= 0){
    		throw new Exception("Set Parameter Should not be bull or empty!!!");
		}
    	
        int rowUpdated = 0;
        PreparedStatement stmt=null;
        String query = null;
        String [] whereKeySet = null;
        if(whereClause != null && whereClause.size() > 0){
        	whereKeySet = getProperties(whereClause);
        	query = SQLBuilder.createUpdateQuery(tableName, getProperties(setParameter), whereLogic, whereKeySet);
        }else{
        	query = SQLBuilder.createUpdateQuery(tableName, getProperties(setParameter), whereLogic, new String[]{});
        }
        try{ 
            if(conn != null){
                stmt = conn.prepareStatement(query);
                
                int length = setParameter.size();
                stmt = bindValueToStatement(stmt, 1, getProperties(setParameter), convertToHashMap(setParameter));
                if(whereKeySet != null)
                	stmt = bindValueToStatement(stmt, length+1, whereKeySet, convertToHashMap(whereClause));
                
                rowUpdated = stmt.executeUpdate();
                if(!conn.getAutoCommit())
                	conn.commit(); 
            }            
        }catch(SQLException exp){
        	if(!conn.getAutoCommit())
        		conn.rollback();
            throw exp;
        }catch (IllegalArgumentException e) {
        	 
            throw e;
		}finally{
        	if(stmt != null)
        		stmt.close();
        }
        return rowUpdated;		
    }
    
    @Deprecated
    public Integer[] executeUpdate(int batchSize
    		, String tableName
    		, List<Map<String, Property>> setParameter
    		, Logic whereLogic
    		, Map<String, Property> whereClause)
    throws SQLException,IllegalArgumentException,Exception{
    	
    	if(setParameter == null 
    			|| setParameter.size() <= 0
    			|| whereClause == null
    			|| whereClause.size() <= 0){
    		throw new Exception("Set Parameter Should not be bull or empty!!!");
		}
    	
    	List<Integer> affectedRows = new ArrayList<Integer>();
        PreparedStatement stmt=null;
        String[] keySet = setParameter.get(0).keySet().toArray(new String[]{});
        String query = null;
        String[] whereKeySet = null;
        whereKeySet = whereClause.keySet().toArray(new String[]{});
        query = SQLBuilder.createUpdateQuery(tableName
        		, keySet
        		, whereLogic
        		, whereKeySet);
        
        
        try{ 
        	batchSize = (batchSize < 100) ? 100 : batchSize;//Least should be 100
            if(conn != null){
                conn.setAutoCommit(false);
                List<int[]> batchUpdatedRowsCount = new ArrayList<int[]>();
                int length = keySet.length;
                stmt = conn.prepareStatement(query);
        		int batchCount = 1;
        		for (Map<String, Property> row : setParameter) {
            		stmt = bindValueToStatement(stmt, 1, keySet, row);
            		stmt = bindValueToStatement(stmt, length+1, whereKeySet, whereClause);
            		stmt.addBatch();
            		if((++batchCount % batchSize) == 0){
            			batchUpdatedRowsCount.add(stmt.executeBatch());
            		}
				}
        		if(setParameter.size() % batchSize != 0)
        			batchUpdatedRowsCount.add(stmt.executeBatch());
        		
        		for (int[] rr  : batchUpdatedRowsCount) {
            		for(int i = 0; i < rr.length ; i++){
                		affectedRows.add(rr[i]);
                	}
				}
            	
            	if(!conn.getAutoCommit())
            		conn.commit(); 
            }            
        }catch(SQLException exp){
        	
        	if(!conn.getAutoCommit())
        		conn.rollback();
            throw exp;
        }catch(IllegalArgumentException iel){
        	throw iel;
        }finally{
        	if(stmt != null)
        		stmt.close();
        	conn.setAutoCommit(true);
        }
        return affectedRows.toArray(new Integer[]{});		
    }
    
    public Integer[] executeUpdate(int batchSize
    		, String tableName
    		, List<List<Property>> setParameter
    		, Logic whereLogic
    		, List<List<Property>> whereClause)
    throws SQLException,IllegalArgumentException,Exception{
    	
    	if(setParameter == null 
    			|| setParameter.size() <= 0
    			|| whereClause == null
    			|| whereClause.size() <= 0){
    		throw new Exception("Set Parameter Should not be bull or empty!!!");
		}
    	
    	List<Integer> affectedRows = new ArrayList<Integer>();
        PreparedStatement stmt=null;
        String[] keySet = getProperties(setParameter.get(0));
        String query = null;
        String[] whereKeySet = null;
        whereKeySet = getProperties(whereClause.get(0));
        query = SQLBuilder.createUpdateQuery(tableName
        		, keySet
        		, whereLogic
        		, whereKeySet);
        
        
        try{ 
        	batchSize = (batchSize < 100) ? 100 : batchSize;//Least should be 100
            if(conn != null){
                conn.setAutoCommit(false);
                List<int[]> batchUpdatedRowsCount = new ArrayList<int[]>();
                int length = keySet.length;
                stmt = conn.prepareStatement(query);
        		int batchCount = 1;
        		for (int index = 0; index < setParameter.size(); index++) {
					Map<String, Property> row = convertToHashMap(setParameter
							.get(index));
					Map<String, Property> rowWhere = convertToHashMap(whereClause
							.get(index));
					stmt = bindValueToStatement(stmt, 1, keySet, row);
					stmt = bindValueToStatement(stmt, length + 1,
							whereKeySet, rowWhere);
					stmt.addBatch();
					if ((++batchCount % batchSize) == 0) {
						batchUpdatedRowsCount.add(stmt.executeBatch());
					}
				}
				if(setParameter.size() % batchSize != 0)
        			batchUpdatedRowsCount.add(stmt.executeBatch());
        		
        		for (int[] rr  : batchUpdatedRowsCount) {
            		for(int i = 0; i < rr.length ; i++){
                		affectedRows.add(rr[i]);
                	}
				}
            	
            	if(!conn.getAutoCommit())
            		conn.commit(); 
            }            
        }catch(SQLException exp){
        	
        	if(!conn.getAutoCommit())
        		conn.rollback();
            throw exp;
        }catch(IllegalArgumentException iel){
        	throw iel;
        }finally{
        	if(stmt != null){
        		try {
					stmt.clearBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
        		stmt.close();
        	}
        	conn.setAutoCommit(true);
        }
        return affectedRows.toArray(new Integer[]{});		
    }
    
    /**
     * 
     * @param conn
     * @param tableName
     * @param whereLogic
     * @param operators
     * @param paramValues
     * @return
     * @throws SQLException
     * @throws Exception
     */
    public int executeDelete(String tableName
    		, Logic whereLogic
    		, Map<String, ComparisonType> operators
    		, List<Property> whereClause)
    throws SQLException,Exception{
    	
    	if(whereClause == null || whereClause.size() <= 0){
    		throw new Exception("Where parameter should not be null or empty!!!");
    	}
    	
        int rowUpdated = 0;
        PreparedStatement stmt=null;
        String query = SQLBuilder.createDeleteQuery(tableName, whereLogic, operators);
        try{ 
            if(conn != null){
                stmt = conn.prepareStatement(query);
                stmt = bindValueToStatement(stmt, 1, getProperties(whereClause), convertToHashMap(whereClause));
                rowUpdated = stmt.executeUpdate();
                if(!conn.getAutoCommit())
                	conn.commit(); 
            }            
        }catch(SQLException exp){
        	if(!conn.getAutoCommit())
        		conn.rollback();
            throw exp;
        }catch (IllegalArgumentException e) {
        	 
            throw e;
		}finally{
        	if(stmt != null)
        		stmt.close();
        }
        return rowUpdated;		
    }
    
    public int executeDelete(int batchSize
    		, String tableName
    		, Logic whereLogic
    		, Map<String, ComparisonType> operators
    		, List<List<Property>> whereClause)
    throws SQLException,Exception{
    	
    	if(whereClause == null || whereClause.size() <= 0){
    		throw new Exception("Where parameter should not be null or empty!!!");
    	}
    	
        int rowUpdated = 0;
        PreparedStatement stmt=null;
        String query = SQLBuilder.createDeleteQuery(tableName, whereLogic, operators);
        String[] whereKeySet = getProperties(whereClause.get(0));
        try{
        	batchSize = (batchSize < 100) ? 100 : batchSize;//Least should be 100
            if(conn != null){
            	conn.setAutoCommit(false);
                int batchCount = 1;
                stmt = conn.prepareStatement(query);
            	for (List<Property> paramValue: whereClause) {
            		
                    stmt = bindValueToStatement(stmt, 1, whereKeySet, convertToHashMap(paramValue));
                    stmt.addBatch();
					if ((++batchCount % batchSize) == 0) {
						stmt.executeBatch();
					}
				}
            	if(whereClause.size() % batchSize != 0)
            		stmt.executeBatch();
            	
                if(!conn.getAutoCommit())
                	conn.commit(); 
            }            
        }catch(SQLException exp){
        	if(!conn.getAutoCommit())
        		conn.rollback();
        	 
            throw exp;
        }catch (IllegalArgumentException e) {
 
            throw e;
		}finally{
			if(stmt != null){
        		try {
					stmt.clearBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
        		stmt.close();
        	}
        	conn.setAutoCommit(true);
        }
        return rowUpdated;		
    }
    
    /**
     * Query for Insert with Auto Generated Id
     * @param conn
     * @param query
     * @return Last Inserted ID
     */
    public int executeInsert(boolean isAutoGenaretedId
    		, String query)
    throws SQLException,IllegalArgumentException{
    	
    	int lastIncrementedID = 0;
    	PreparedStatement stmt=null;
        try{ 
        	
        	if(query != null 
	    			&& query.length() > 0 
	    			&& !query.toUpperCase().startsWith("INSERT")){
	    		throw new IllegalArgumentException("Query string must be a Insert query!");
	    	}
            if(conn != null){
            	
                if (isAutoGenaretedId) {
					stmt = conn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
					stmt.executeUpdate();
					ResultSet rs = stmt.getGeneratedKeys();
					if (rs != null && rs.next())
						lastIncrementedID = rs.getInt(1);
				}else{
					stmt = conn.prepareStatement(query);                	
					lastIncrementedID = stmt.executeUpdate();
				}
				if(!conn.getAutoCommit())
                	conn.commit(); 
            }            
        }catch(SQLException exp){
        	
        	if(!conn.getAutoCommit())
        		conn.rollback();
            throw exp;
        }catch(IllegalArgumentException iel){
        	throw iel;
        }finally{
        	if(stmt != null)
        		stmt.close();
        }
        return lastIncrementedID;		
    }
    
    /**
     * 
     * @param conn
     * @param tableName
     * @param params
     * @return int as affected row count
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public int executeInsert(boolean isAutoGenaretedId
    		, String tableName
    		, List<Property> params)
    throws SQLException,IllegalArgumentException,Exception{
    	
    	if(params == null || params.size() <= 0){
    		throw new Exception("Parameter should not be null or empty!!!");
    	}
    	
    	int affectedRows = 0;
        PreparedStatement stmt=null;        
        String query = SQLBuilder.createInsertQuery(tableName, getProperties(params));
        try{ 
        	
            if(conn != null){
                
            	if(isAutoGenaretedId){
            		stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);                	
                	stmt.executeUpdate();
                	ResultSet set = stmt.getGeneratedKeys();
                	if(set != null && set.next()){
                		affectedRows = set.getInt(1);
                	}                	
            	}else{
            		stmt = conn.prepareStatement(query);                	
                	affectedRows = stmt.executeUpdate();
            	}
            	if(!conn.getAutoCommit())
            		conn.commit(); 
            }            
        }catch(SQLException exp){
        	
        	if(!conn.getAutoCommit())
        		conn.rollback();
            throw exp;
        }catch(IllegalArgumentException iel){
        	
        	throw iel;
        }finally{
        	if(stmt != null)
        		stmt.close();
        }
        return affectedRows;		
    }
    /**
     * 
     * @param conn
     * @param isAutoGenaretedId
     * @param tableName
     * @param params
     * @return
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public int executeParameterizedInsert(boolean isAutoGenaretedId
    		, String tableName
    		, List<Property> params)
    throws SQLException,IllegalArgumentException,Exception{
    	
    	if(params == null || params.size() <= 0){
    		throw new Exception("Parameter should not be null or empty!!!");
    	}
    	
    	int affectedRows = 0;
        PreparedStatement stmt=null;
        String query = SQLBuilder.createInsertQuery(tableName, getProperties(params));
        
        try{ 
        	
            if(conn != null){
                
            	if(isAutoGenaretedId){
            		stmt = conn.prepareStatement(query,	Statement.RETURN_GENERATED_KEYS);
                	stmt = bindValueToStatement(stmt, 1,getProperties(params), convertToHashMap(params));
                	stmt.executeUpdate();
                	ResultSet set = stmt.getGeneratedKeys();
                	if(set != null && set.next()){
                		affectedRows = set.getInt(1);
                	}                	
            	}else{
            		stmt = conn.prepareStatement(query);
                	stmt = bindValueToStatement(stmt, 1,getProperties(params), convertToHashMap(params));
                	affectedRows = stmt.executeUpdate();
            	}
            	if(!conn.getAutoCommit())
            		conn.commit(); 
            }            
        }catch(SQLException exp){
        	
        	if(!conn.getAutoCommit())
        		conn.rollback();
            throw exp;
        }catch(IllegalArgumentException iel){
        	
        	throw iel;
        }finally{
        	if(stmt != null)
        		stmt.close();
        }
        return affectedRows;		
    }
    
  
    public Integer[] executeParameterizedInsert(boolean isAutoGenaretedId
    		, int batchSize
    		, String tableName
    		, List<List<Property>> params)
    throws SQLException,IllegalArgumentException,Exception{
    	
    	if(params == null || params.size() <= 0){
    		throw new Exception("Parameter should not be null or empty!!!");
    	}
    	
    	List<Integer> affectedRows = new ArrayList<Integer>();
        PreparedStatement stmt=null;
        Object[] keySet = getProperties(params.get(0));
        String query = SQLBuilder.createInsertQuery(tableName, keySet);
        
        try{ 
        	batchSize = (batchSize < 100) ? 100 : batchSize;//Least should be 100
            if(conn != null){
                conn.setAutoCommit(false);
            	if(isAutoGenaretedId){
            		stmt = conn.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
                	int batchCount = 1;
            		for (List<Property> row : params) {
                		stmt = bindValueToStatement(stmt, 1, keySet, convertToHashMap(row));
                		stmt.addBatch();
                		if((++batchCount % batchSize) == 0){
                			stmt.executeBatch();
                		}
					}
            		if(params.size() % batchSize != 0)
            			stmt.executeBatch();
        			
                	ResultSet set = stmt.getGeneratedKeys();
                	
                	while(set.next()){
                		affectedRows.add(set.getInt(1));
                	}                	
            	}else{
            		stmt = conn.prepareStatement(query);
            		int batchCount = 1;
            		List<int[]> batchUpdatedRowsCount = new ArrayList<int[]>();
            		for (List<Property> row : params) {
                		stmt = bindValueToStatement(stmt, 1, keySet, convertToHashMap(row));
                		stmt.addBatch();
                		if((++batchCount % batchSize) == 0){
                			batchUpdatedRowsCount.add(stmt.executeBatch());
                		}
					}
            		if(params.size() % batchSize != 0)
            			batchUpdatedRowsCount.add(stmt.executeBatch());
        			
            		for (int[] rr  : batchUpdatedRowsCount) {
                		for(int i = 0; i < rr.length ; i++){
                    		affectedRows.add(rr[i]);
                    	}
    				}
            	}
            	if(!conn.getAutoCommit())
            		conn.commit();
            }            
        }catch(SQLException exp){
        	
        	if(!conn.getAutoCommit())
        		conn.rollback();
            throw exp;
        }catch(IllegalArgumentException iel){
        	throw iel;
        }finally{
        	if(stmt != null){
        		try {
					stmt.clearBatch();
				} catch (Exception e) {
					e.printStackTrace();
				}
        		stmt.close();
        	}
        	conn.setAutoCommit(true);
        }
        return affectedRows.toArray(new Integer[]{});		
    }
    
    /**
     * 
     * @param conn
     * @param query
     * @return
     * @throws SQLException
     */
    public int getRowCount(String query)
    throws SQLException{
    	
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        int rowCount = 0;
        try{
            if(conn != null){
                pstmt = conn.prepareStatement(query);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    rowCount = rs.getInt(1);                   
                } else {
                     rowCount=0;
                }
            }            
        }catch(SQLException e){
            throw e;
        }finally{
        	if(pstmt != null)
        		pstmt.close();
        }
        return rowCount;
     }
    
    /**
     * 
     * @param conn
     * @param tableName
     * @param param
     * @param whereParam
     * @param type
     * @param property
     * @return
     * @throws SQLException
     */
    public int getRowCount(String tableName
    		,String param
    		,String whereParam
    		,ComparisonType type
    		,Property property)
    throws SQLException{
    	
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        int rowCount = 0;
        String query = SQLBuilder.createCountFunctionQuery(tableName, param, whereParam, type);
        try{
            if(conn != null){
                pstmt = conn.prepareStatement(query);
                Map<String, Property> val = new HashMap<String, Property>();
                val.put(property.getKey(), property);
                pstmt = bindValueToStatement(pstmt
                		, 1
                		,new Object[]{whereParam}
                		, val);
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    rowCount = rs.getInt(1);                   
                } else {
                     rowCount=0;
                }
            }            
        }catch(SQLException e){
            throw e;
        }finally{
        	if(pstmt != null)
        		pstmt.close();
        }
        return rowCount;
     }
    
    public int getRowCount(String tableName
    		,String param
    		,Logic logic
    		,Map<String, ComparisonType> operators
    		,List<Property> whereClause)
    throws SQLException{
    	
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        int rowCount = 0;
        String query = SQLBuilder.createCountFunctionQuery(tableName, param, logic,operators);
        try{
            if(conn != null){
                pstmt = conn.prepareStatement(query);
                
                pstmt = bindValueToStatement(pstmt
                		, 1
                		, getProperties(whereClause)
                		, convertToHashMap(whereClause));
                rs = pstmt.executeQuery();
                if (rs.next()) {
                    rowCount = rs.getInt(1);                   
                } else {
                     rowCount=0;
                }
            }            
        }catch(SQLException e){
            throw e;
        }finally{
        	if(pstmt != null)
        		pstmt.close();
        }
        return rowCount;
     }
    
    /**
     * Query for select
     * @param conn
     * @param query
     * @return ResultSet
     */
    public ResultSet executeSelect(String query)
    throws SQLException,IllegalArgumentException{
    	
        PreparedStatement stmt = null;
        ResultSet rst=null;
        try{
        	
        	if(query != null 
	    			&& query.length() > 0 
	    			&& !query.toUpperCase().startsWith("SELECT")){
	    		throw new IllegalArgumentException("Query string must be a Select query!");
	    	}
            if(conn != null){
            	stmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                rst = stmt.executeQuery();                 
            }            
        }catch(SQLException exp){            
            throw exp;
        }catch(IllegalArgumentException iel){
        	throw iel;
        }finally{
        	getStatementHolder().add(stmt);
        }
        return rst;           
    }
    
    /**
     * 
     * @param conn
     * @param table
     * @param projectionParams
     * @param whereLogic
     * @param operators
     * @param whereClause. Entry example like, key=>"ParamName1", Value=>ParamProperties("ParamName1","value",ParamDataTypeString) 
     * OR like, key=>"ParamName2", Value=>ParamProperties("ParamName2","value",ParamDataTypeInt)  
     * @return ResultSet
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public ResultSet executeSelect(String table
    		, String[]projectionParams
    		, Logic whereLogic    		
    		, List<Property> whereClause)
    throws SQLException,IllegalArgumentException{
    	
        PreparedStatement stmt = null;
        ResultSet rst=null;
        String query = SQLBuilder.createSelectQuery(table, projectionParams, whereLogic, getProperties(whereClause));
        try{
        	
            if(conn != null && !conn.isClosed()){
            	stmt = conn.prepareStatement(query,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
            	stmt = bindValueToStatement(stmt, 1, getProperties(whereClause), convertToHashMap(whereClause));
        		rst = stmt.executeQuery();
            }            
        }catch(SQLException exp){            
            throw exp;
        }catch(IllegalArgumentException iel){
        	throw iel;
        }finally{
        	getStatementHolder().add(stmt);
        }
        return rst;           
    }
    
    /**
     * 
     * @param conn
     * @param table
     * @param projectionParams
     * @param whereLogic
     * @param operators 
     * @param whereClause, Parameter should be Entry example like, key=>"ParamName1", Value=>ParamProperties("ParamName1","value",ParamDataTypeString) 
     * OR like, key=>"ParamName2", Value=>ParamProperties("ParamName2","value",ParamDataTypeInt)
     * @return ResultSet
     * @throws SQLException
     * @throws IllegalArgumentException
     */
    public ResultSet executeSelect(String table
    		, String[]projectionParams
    		, Logic whereLogic
    		, Map<String, ComparisonType> operators
    		, List<Property> whereClause)
    throws SQLException,IllegalArgumentException{
    	
        PreparedStatement stmt = null;
        ResultSet rst=null;
        String query = SQLBuilder.createSelectQuery(table, projectionParams, whereLogic, operators);
        try{
        	
            if(conn != null && !conn.isClosed()){
                stmt = conn.prepareStatement(query,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_READ_ONLY);
                stmt = bindValueToStatement(stmt, 1,getProperties(whereClause), convertToHashMap(whereClause));
                rst = stmt.executeQuery();                 
            }            
        }catch(SQLException exp){            
            throw exp;
        }catch(IllegalArgumentException iel){
        	throw iel;
        }finally{
        	getStatementHolder().add(stmt);
        }
        return rst;           
    }
	
	/**
	 * 
	 * @param rst
	 * @return
	 */
	public List<Map<String,Property>> convertToMaps(ResultSet rst){
		
		List<Map<String,Property>> result = new ArrayList<Map<String, Property>>();
		
		try{
			//IF cursor is moved till last row. Then set to the above first row. 
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			
			ResultSetMetaData rsmd = rst.getMetaData();
			int numCol = rsmd.getColumnCount();
			
			while(rst.next()){ //For each Row
				
				Map<String, Property> row = new HashMap<String, Property>(numCol);
				for(int x = 1; x <= numCol; x++){ //For each column in a Row
					
					String key = rsmd.getColumnName(x);
					DataType type = convertDataType(rsmd.getColumnTypeName(x));
					Object value = getValueFromResultSet(type, rst, x);
					
					Property property = new Property(key, value, type);
					row.put(key, property);
				}
				result.add(row);
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	public List<Map<String,Property>> convertToMaps(ResultSet rst, List<String> paramProperties){
		
		if(paramProperties == null || paramProperties.size() <= 0){
            return convertToMaps(rst);
        }
		
		List<Map<String,Property>> result = new ArrayList<Map<String, Property>>();
		
		try{
			
			//IF cursor is moved till last row. Then set to the above first row. 
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			
			ResultSetMetaData rsmd = rst.getMetaData();
			//Optimization
            List<Integer> columnIndecies = new ArrayList<Integer>();
            for(String columnName : paramProperties){
                columnIndecies.add(rst.findColumn(columnName));
            }
			
			while(rst.next()){ //For each Row
				
				Map<String, Property> row = new HashMap<String, Property>(columnIndecies.size());
				for(int x : columnIndecies){ //For each column in the paramProperties

					String key = rsmd.getColumnName(x);

					DataType type = convertDataType(rsmd
							.getColumnTypeName(x));
					Object value = getValueFromResultSet(type, rst, x);
					Property property = new Property(key, value, type);
					row.put(key, property);

				}
				if(row.size() > 0)
					result.add(row);
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	public List<List<Property>> convertToLists(ResultSet rst){
		
		List<List<Property>> result = new ArrayList<List<Property>>();
		
		try{
			
			//IF cursor is moved till last row. Then set to the above first row. 
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			
			ResultSetMetaData rsmd = rst.getMetaData();
			int numCol = rsmd.getColumnCount();
			
			while(rst.next()){ //For each Row
				List<Property> row = new ArrayList<Property>(numCol);
				for(int x = 1; x <= numCol; x++){ //For each column in a Row
					
					String key = rsmd.getColumnName(x);
					DataType type = convertDataType(rsmd.getColumnTypeName(x));
					Object value = getValueFromResultSet(type, rst, x);
					
					Property property = new Property(key, value, type);
					row.add((x - 1), property);
				}
				result.add(row);
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	public List<List<Property>> convertToLists(ResultSet rst, List<String> paramProperties){
		
		if(paramProperties == null || paramProperties.size() <= 0){
            return convertToLists(rst);
        }
		
		List<List<Property>> result = new ArrayList<List<Property>>();
		
		try{
			
			//IF cursor is moved till last row. Then set to the above first row. 
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			
			ResultSetMetaData rsmd = rst.getMetaData();
			//Optimization
            List<Integer> columnIndecies = new ArrayList<Integer>();
            for(String columnName : paramProperties){
                columnIndecies.add(rst.findColumn(columnName));
            }
			
			while(rst.next()){ //For each Row
				List<Property> row = new ArrayList<Property>(columnIndecies.size());
				for(int x : columnIndecies){ //For each column in the paramProperties

					String key = rsmd.getColumnName(x);

					DataType type = convertDataType(rsmd
							.getColumnTypeName(x));
					Object value = getValueFromResultSet(type, rst, x);
					Property property = new Property(key, value, type);
					row.add(property);

				}
				if(row.size() > 0)
					result.add(row);
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	public List<Map<String, Object>> convertToKeyValuePaire(ResultSet rst){
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		try{
			
			//IF cursor is moved till last row. Then set to the above first row. 
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			
			ResultSetMetaData rsmd = rst.getMetaData();
			int numCol = rsmd.getColumnCount();
			
			while(rst.next()){ //For each Row
				
				Map<String, Object> row = new HashMap<String, Object>(numCol);
				for(int x = 1; x <= numCol; x++){ //For each column in a Row
					
					String key = rsmd.getColumnName(x);
					DataType type = convertDataType(rsmd.getColumnTypeName(x));
					Object value = getValueFromResultSet(type, rst, x);
					
					row.put(key, value);
				}
				result.add(row);
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	public List<Map<String, Object>> convertToKeyValuePaire(ResultSet rst, List<String> paramProperties){
		
		if(paramProperties == null || paramProperties.size() <= 0){
            return convertToKeyValuePaire(rst);
        }
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		try{
			
			//IF cursor is moved till last row. Then set to the above first row. 
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			
			ResultSetMetaData rsmd = rst.getMetaData();
			//Optimization
            List<Integer> columnIndecies = new ArrayList<Integer>();
            for(String columnName : paramProperties){
                columnIndecies.add(rst.findColumn(columnName));
            }
			
			while(rst.next()){ //For each Row
				
				Map<String, Object> row = new HashMap<String, Object>(columnIndecies.size());
				for(int x : columnIndecies){ //For each column in paramProperties

					String key = rsmd.getColumnName(x);

					DataType type = convertDataType(rsmd
							.getColumnTypeName(x));
					Object value = getValueFromResultSet(type, rst, x);
					row.put(key, value);
				}
				if(row.size() > 0)
					result.add(row);
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	public List<Map<String, Object>> convertToKeyValuePaire(ResultSet rst, List<String> paramProperties, List<String> paramPropertyNames){
		
		if(paramProperties == null 
				|| paramProperties.size() <= 0){
			return convertToKeyValuePaire(rst);
		}
		if(paramPropertyNames == null
				|| paramPropertyNames.size() <= 0
				|| paramProperties.size() != paramPropertyNames.size()){
			return convertToKeyValuePaire(rst, paramProperties);
		}
		
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		
		try{
			
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
				rst.beforeFirst();
			}
			
			ResultSetMetaData rsmd = rst.getMetaData();
			List<Integer> columnIndices = new ArrayList<Integer>(paramProperties.size());
			for(String columnName : paramProperties){
				columnIndices.add(rst.findColumn(columnName));
			}
			
			while(rst.next()){ //For each Row
				
				HashMap<String, Object> row = new HashMap<String, Object>(columnIndices.size());
				int newNameCount = 0;
				for(int x : columnIndices){ //For each column in paramProperties
					
					String key = paramPropertyNames.get(newNameCount++);
					DataType type = convertDataType(rsmd.getColumnTypeName(x));
					Object value = getValueFromResultSet(type, rst, x);
					
					row.put(key, value);
				}
				result.add(row);
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	public Map<Object, Map<String, Object>> convertToIndexedKeyValuePaire(ResultSet rst, String indexColumn){
		
		Map<Object,Map<String, Object>> result = new HashMap<Object, Map<String,Object>>();
		
		try{
			
			//IF cursor is moved till last row. Then set to the above first row. 
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			
			ResultSetMetaData rsmd = rst.getMetaData();
			int numCol = rsmd.getColumnCount();
			
			while(rst.next()){ //For each Row
				
				Object indexColValue = null;
				
				Map<String, Object> row = new HashMap<String, Object>(numCol);
				for(int x = 1; x <= numCol; x++){ //For each column in a Row
					
					String key = rsmd.getColumnName(x);
					DataType type = convertDataType(rsmd.getColumnTypeName(x));
					Object value = getValueFromResultSet(type, rst, x);
					
					if(key.equals(indexColumn))
						indexColValue = value;
					
					row.put(key, value);
				}
				if(indexColValue != null
						&& !result.containsKey(indexColValue))
					result.put(indexColValue,row);
				
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	public Map<Object, Map<String, Object>> convertToIndexedKeyValuePaire(ResultSet rst, String indexColumn, List<String> paramProperties){
		
		if(paramProperties == null || paramProperties.size() <= 0){
            return convertToIndexedKeyValuePaire(rst, indexColumn);
        }
		
		Map<Object,Map<String, Object>> result = new HashMap<Object, Map<String,Object>>();
		
		try{
			
			//IF cursor is moved till last row. Then set to the above first row. 
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			
			ResultSetMetaData rsmd = rst.getMetaData();
			
			//Optimization
            List<Integer> columnIndecies = new ArrayList<Integer>();
            for(String columnName : paramProperties){
                columnIndecies.add(rst.findColumn(columnName));
            }
			
			while(rst.next()){ //For each Row
				
				Object indexColValue = null;
				
				Map<String, Object> row = new HashMap<String, Object>(columnIndecies.size());
				for(int x : columnIndecies){ //For each column in the paramProperties
					
					String key = rsmd.getColumnName(x);

					DataType type = convertDataType(rsmd
							.getColumnTypeName(x));
					Object value = getValueFromResultSet(type, rst, x);
					if (key.equals(indexColumn))
						indexColValue = value;
					row.put(key, value);

				}
				if(indexColValue != null
						&& !result.containsKey(indexColValue)
						&& row.size() > 0)
					result.put(indexColValue,row);
				
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	public Map<Object, Map<String, Object>> convertToIndexedKeyValuePaire(ResultSet rst, String indexColumn, List<String> paramProperties, List<String> paramPropertyNames){
		
		if(paramProperties == null 
				|| paramProperties.size() <= 0){
			return convertToIndexedKeyValuePaire(rst, indexColumn);
		}
		if(paramPropertyNames == null
				|| paramPropertyNames.size() <= 0
				|| paramProperties.size() != paramPropertyNames.size()){
			return convertToIndexedKeyValuePaire(rst, indexColumn, paramProperties);
		}
		
		Map<Object,Map<String, Object>> result = new HashMap<Object, Map<String, Object>>();
		
		try{
			
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
				rst.beforeFirst();
			}
			
			ResultSetMetaData rsmd = rst.getMetaData();
			List<Integer> columnIndices = new ArrayList<Integer>();
			for(String columnName : paramProperties){
				columnIndices.add(rst.findColumn(columnName));
			}
			
			while(rst.next()){ //For each Row
				
				Object indexColValue = null;
				
				HashMap<String, Object> row = new HashMap<String, Object>(columnIndices.size());
				int newNameCount = 0;
				for(int x : columnIndices){ //For each column in paramProperties
					
					String key = rsmd.getColumnName(x);
					String keyConverted = paramPropertyNames.get(newNameCount++);
					DataType type = convertDataType(rsmd.getColumnTypeName(x));
					Object value = getValueFromResultSet(type, rst, x);
					
					if(key.equals(indexColumn))
						indexColValue = value;
					
					row.put(keyConverted, value);
				}
				if(indexColValue != null
						&& !result.containsKey(indexColValue))
					result.put(indexColValue,row);
				
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param rst
	 * @param rowIndex > 0
	 * @param isObjectType
	 * @return
	 */
	
	public Map<String, Object> retrieveRow(ResultSet rst, int rowIndex, boolean isObjectType){

        Map<String, Object> result = null;

        try{
            ResultSetMetaData rsmd = rst.getMetaData();
            int numCol = rsmd.getColumnCount();
            if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE){
                int offset = (rowIndex <= 0) ? 1 : rowIndex;
                rst.absolute(offset);
                HashMap<String, Object> row = new HashMap<String, Object>(numCol);
                
                for(int x = 1; x <= numCol; x++){ //For each column in a Row
                    
                    String key = rsmd.getColumnName(x);
                    DataType type = convertDataType(rsmd.getColumnTypeName(x));
                    Object value = getValueFromResultSet(type, rst, x);
                    if(!isObjectType){
                        Property property = new Property(key, value, type);
                        row.put(key, property);
                    }else{
                        row.put(key, value);
                    }
                }
                result = row;
            }else{
                if(!rst.isAfterLast()){
                    
                    while(rst.next()){
                        
                        if(rowIndex == rst.getRow()){
                            
                            HashMap<String, Object> row = new HashMap<String, Object>(numCol);
                            
                            for(int x = 1; x <= numCol; x++){ //For each column in a Row
                                
                                String key = rsmd.getColumnName(x);
                                DataType type = convertDataType(rsmd.getColumnTypeName(x));
                                Object value = getValueFromResultSet(type, rst, x);
                                if(!isObjectType){
                                    Property property = new Property(key, value, type);
                                    row.put(key, property);
                                }else{
                                    row.put(key, value);
                                }
                            }
                            result = row;
                            break;
                        }
                    }//end while
                }
            }//
        }catch(SQLException exp){

            result = null;
            exp.getStackTrace();
        }

        return result;
    }
	
	public List<Object> retrieveColumn(ResultSet rst, String indexColumn){
		
		List<Object> result = new ArrayList<Object>();
		
		try{
			
			//IF cursor is moved till last row. Then set to the above first row. 
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			
			ResultSetMetaData rsmd = rst.getMetaData();
			int x = (rst.findColumn(indexColumn) <= 0) ? 1 : rst.findColumn(indexColumn);
			
			DataType type = convertDataType(rsmd.getColumnTypeName(x));
			
			while(rst.next()){ //For each Row
				
				Object value = getValueFromResultSet(type, rst, x);
				result.add(value);
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	public List<Object> retrieveColumn(ResultSet rst, int indexColumn){
		
		List<Object> result = new ArrayList<Object>();
		
		try{
			
			//IF cursor is moved till last row. Then set to the above first row. 
			if(rst.getType() == ResultSet.TYPE_SCROLL_SENSITIVE && rst.isAfterLast()){
                rst.beforeFirst();
            }
			
			ResultSetMetaData rsmd = rst.getMetaData();
			int numCol = rsmd.getColumnCount();
			
			int x = 1;
			if( 1 <= indexColumn && indexColumn <= numCol){
				x = indexColumn;
			}
			
			DataType type = convertDataType(rsmd.getColumnTypeName(x));
			
			while(rst.next()){ //For each Row
				
				Object value = getValueFromResultSet(type, rst, x);
				result.add(value);
			}
		}catch(SQLException exp){
			
			result = null;
			exp.getStackTrace();
		}
		
		return result;
	}
	
	/*>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Private Methods>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>*/
    
    private String[] getProperties(List<Property> listOfParam){
    	//Before Java 8
        List<String> result = new ArrayList<String>();
        for (Property x : listOfParam) {
            result.add(x.getKey());
        }
    	return result.toArray(new String[]{});
    }
    
    private Map<String, Property> convertToHashMap(List<Property> listOfParam){
    	Map<String, Property> result = new HashMap<String, Property>();
    	for (Property parameter : listOfParam) {
			result.put(parameter.getKey(), parameter);
		}
    	return result;
    }
	
	private PreparedStatement bindValueToStatement(PreparedStatement stmt
			, int startIndex
			, Object[] params
			, Map<String, Property> paramValues)
    throws SQLException,IllegalArgumentException{
    	
    	try{
    		if(params.length == paramValues.size()){
            	
            	if(stmt != null){
            		int index = 1;
            		if(startIndex > 0){
            			index = startIndex;
            		}else{
            			throw new IllegalArgumentException("Index Out Of Bound!!!");
            		}
            		for (Object param : params) {
            			
            			Property property = paramValues.get(param.toString());

            			switch (property.getType()) {
	            			case STRING:
	            				stmt.setString(index++, (property.getValue() != null) ? property.getValue().toString().trim() : null);
	            				break;
	            			case INT:
	            				if(property.getValue() != null){
	            					stmt.setInt(index++, (Integer)property.getValue());
	            				}else{
	            					stmt.setNull(index++, java.sql.Types.INTEGER);
	            				}
	            				break;
	            			case BOOL:
	            				if(property.getValue() != null){
                                    stmt.setBoolean(index++, (Boolean)property.getValue());
                                }else{
                                    stmt.setNull(index++, java.sql.Types.BOOLEAN);
                                }
	            				break;
	            			case FLOAT:
	            				if(property.getValue() != null){
	            					stmt.setFloat(index++, (Float)property.getValue());
	            				}else{
	            					stmt.setNull(index++, java.sql.Types.FLOAT);
	            				}
	            				break;
	            			case DOUBLE:
	            				if(property.getValue() != null){
	            					stmt.setDouble(index++, (Double)property.getValue());
	            				}else{
	            					stmt.setNull(index++, java.sql.Types.DOUBLE);
	            				}
	            				break;
	            			case SQLDATETIME:
	            				stmt.setDate(index++, (property.getValue() != null) ?(Date)property.getValue() : null);
	            				break;
	            			case BLOB:
	            				if(property.getValue() != null){
	            					stmt.setBlob(index++, (Blob)property.getValue());
	            				}else{
	            					stmt.setNull(index++, java.sql.Types.BLOB);
	            				}
	            				break;
	            			case BYTEARRAY:
	            				if(property.getValue() != null){
                                    stmt.setBytes(index++, (byte[])property.getValue());
                                }else{
                                    stmt.setNull(index++, java.sql.Types.ARRAY);
                                }
	            				break;
	            			default:
	            				stmt.setObject(index++, property.getValue());
	            				break;
            			}
            		}
            	}
            }else{
            	throw new IllegalArgumentException("Parameter length mismatch");
            }
    	}catch(SQLException exp){
    		throw exp;
    	}
    	return stmt;
    }
	
	private Object getValueFromResultSet(DataType type, ResultSet rst, int index)
	throws SQLException{
		
		Object value = null;
		switch (type) {

		case INT:
			value = new Integer(rst.getInt(index));
			break;
		case DOUBLE:
			value = new Double(rst.getDouble(index));
			break;
		case FLOAT:
			value = new Float(rst.getFloat(index));
			break;
		case STRING:
			value = rst.getString(index);
			break;
		case BOOL:
			value = new Boolean(rst.getBoolean(index));
			break;
		case SQLDATETIME:
			value = rst.getDate(index);
			break;
		case BYTEARRAY:
			byte[] arr = rst.getBytes(index); 
			value = arr;
			break;
		default:
			value = rst.getObject(index);
			break;
		}
		
		return value;
	}
	
	private DataType convertDataType(String type){
		
		String trimedType = type.trim().toUpperCase();
		
		if(trimedType.equals("CHAR") 
				|| trimedType.equals("VARCHAR")
				|| trimedType.equals("LONGVARCHAR")){
			
			return DataType.STRING;
			
		}
		else if(trimedType.equals("INTEGER") 
				|| trimedType.equals("BIGINT")
				|| trimedType.equals("SMALLINT")){
			return DataType.INT;
			
		}
		else if(trimedType.equals("DATE") 
				|| trimedType.equals("TIME")
				|| trimedType.equals("TIMESTAMP")){
			return DataType.SQLDATETIME;
			
		}else if(trimedType.equals("FLOAT")){
			return DataType.FLOAT;
		}
		else if(trimedType.equals("DOUBLE")){
			return DataType.DOUBLE;
		}
		else if(trimedType.equals("BIT") 
				|| trimedType.equals("TINYINT")){
			return DataType.BOOL;
		}
		else if(trimedType.equals("BINARY") || trimedType.equals("VARBINARY") || trimedType.equals("LONGVARBINARY")){
			return DataType.BYTEARRAY;
		}
		else{
			return DataType.OBJECT;
		}
		
	}
	
	//////////////////////////////////////END//////////////////////////////////////
}
