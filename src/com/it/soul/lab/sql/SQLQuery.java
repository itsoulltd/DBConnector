package com.it.soul.lab.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.it.soul.lab.util.EnumDefinitions;
import com.it.soul.lab.util.EnumDefinitions.Logic;

public class SQLQuery {
	
	public enum QueryType{
		Select,
		Insert,
		Update,
		Delete
	}
	
	public static class Builder{
		
		private QueryType tempType = QueryType.Select;
		private SQLQuery tempQuery;
		
		public Builder(QueryType type){
			tempType = type;
			tempQuery = factory(tempType);
		}
		
		public SQLQuery build(){
			//SQLQuery query = factory(tempType);
			//TODO: Build up a new Object from existing one.
			return tempQuery;
		}
		
		private SQLQuery factory(QueryType type){
			SQLQuery temp = null;
			switch (type) {
			case Delete:
				temp = new SQLDeleteQuery();
				break;
			case Insert:
				temp = new SQLInsertQuery();
				break;
			case Update:
				temp = new SQLUpdateQuery();
				break;
			default:
				temp = new SQLSelectQuery();
				break;
			}
			return temp;
		}
		
		public Builder table(String name){
			tempQuery.setTableName(name);
			return this;
		}
		public Builder columns(String... name){
			tempQuery.setColumns(name);
			return this;
		}
		public Builder whereParams(Logic logic, String... name){
			tempQuery.setLogic(logic);
			tempQuery.setWhereParams(name);
			return this;
		}
		public Builder whereParams(Logic logic, Compare... comps){
			tempQuery.setLogic(logic);
			List<Compare> items = new ArrayList<Compare>(Arrays.asList(comps));
			tempQuery.setWhereCompareParams(items);
			return this;
		}
	}
	
	//////////////////////////////////SQLQuery///////////////////////////////////////////
	
	public List<Compare> getWhereCompareParams() {
		return whereCompareParams;
	}

	public void setWhereCompareParams(List<Compare> whereCompareParams) {
		this.whereCompareParams = whereCompareParams;
	}

	protected static boolean isAllParamEmpty(Object[]paramList){
		boolean result = false;
		if(paramList != null && paramList.length > 0){
			int count = 0;
			for(Object item : paramList){
				
				if(item.toString().trim().equals(""))
					continue;
				count++;
			}
			result = (count == 0) ? true : false;
		}
		return result;
	}
	
	public String[] getWhereParams() {
		return whereParams;
	}
	public void setWhereParams(String[] whereParams) {
		this.whereParams = whereParams;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String[] getColumns() {
		return columns;
	}
	public void setColumns(String[] columns) {
		this.columns = columns;
	}
	public Logic getLogic() {
		return logic;
	}
	public void setLogic(Logic logic) {
		this.logic = logic;
	}
	protected static final char QUIENTIFIER = 'e';
	protected static final char STARIC = '*';
	protected static final char MARKER = '?';
	protected static final String COUNT_FUNC = "COUNT";
	protected static final String DISTINCT_FUNC = "DISTINCT";
	
	public String queryString() throws IllegalArgumentException{
		if(tableName == null || tableName.trim().equals("")){
			throw new IllegalArgumentException("Parameter 'tableName' must not be Null OR Empty.");
		}
		return "";
	}
	
	private String tableName;
	private String[] columns;
	private String[] whereParams;
	private Logic logic;
	private List<Compare> whereCompareParams;
	
	/////////////////////////////////////////SQLSelectQuery/////////////////////////////////////////////////
	
	public static class SQLSelectQuery extends SQLQuery{
		
		private StringBuffer pqlBuffer = new StringBuffer("SELECT ");
		
		@Override
		public String queryString() throws IllegalArgumentException{
			super.queryString();
			return createSelectQuery(getTableName(), getColumns());
		}
		
		@Override
		public void setColumns(String[] columns) {
			super.setColumns(columns);
			if(getColumns() != null && getColumns().length > 0){
				int count = 0;
				for(String str : getColumns()){
					if(str.trim().equals("")){
						continue;
					}
					if(count++ != 0){
						pqlBuffer.append(", ");
					}
					pqlBuffer.append( QUIENTIFIER + "." + str);
				}
				//If all passed parameter is empty
				if(count == 0){
					pqlBuffer.append(QUIENTIFIER + "." + STARIC);
				}
			}else{
				pqlBuffer.append(QUIENTIFIER + "." + STARIC);
			}
		}
		
		@Override
		public void setTableName(String tableName) {
			super.setTableName(tableName);
			pqlBuffer.append(" FROM "+ tableName + " " + QUIENTIFIER);
		}
		
		@Override
		public void setWhereParams(String[] whereParams) {
			super.setWhereParams(whereParams);
			if(whereParams != null 
					&& whereParams.length > 0
					&& !isAllParamEmpty(whereParams)){
				if(pqlBuffer.length() > 0){
					pqlBuffer.append(" WHERE ");
					
					int count = 0;
					for(String param : whereParams){
						
						if(param.trim().equals("")){
							continue;
						}
						if(count++ != 0){
							pqlBuffer.append( " " + getLogic().name() + " ");
						}
						pqlBuffer.append( QUIENTIFIER + "." + param + " = " + MARKER);
					}
				}
			}
		}
		
		@Override
		public void setWhereCompareParams(List<Compare> whereParams) {
			super.setWhereCompareParams(whereParams);
			if(whereParams != null 
					&& whereParams.size() > 0
					&& !isAllParamEmpty(whereParams.toArray())){
				
				if(pqlBuffer.length() > 0){
					pqlBuffer.append(" WHERE ");
					
					int count = 0;
					for(Compare param : whereParams){
						
						if(param.getProperty().trim().equals("")){
							continue;
						}
						if(count++ != 0){
							pqlBuffer.append( " " + getLogic().name() + " ");
						}
						pqlBuffer.append( QUIENTIFIER + "." + param.getProperty() + " " + EnumDefinitions.convertOperator(param.getType()) + " " + MARKER);
					}
				}
			}
		}
		
		public static String createSelectQuery(String tableName, String[]projectionParams, Logic whereLogic, List<Compare> whereParams)
				throws IllegalArgumentException{
					
					//Query Builders
					StringBuffer pqlBuffer = null;
					try{
						pqlBuffer = new StringBuffer(createSelectQuery(tableName, projectionParams));
					}catch(IllegalArgumentException iex){
						throw iex;
					}
					
					if(whereParams != null 
							&& whereParams.size() > 0
							&& !isAllParamEmpty(whereParams.toArray())){
						
						if(pqlBuffer.length() > 0){
							pqlBuffer.append(" WHERE ");
							
							int count = 0;
							for(Compare param : whereParams){
								
								if(param.getProperty().trim().equals("")){
									continue;
								}
								if(count++ != 0){
									pqlBuffer.append( " " + whereLogic.name() + " ");
								}
								pqlBuffer.append( QUIENTIFIER + "." + param.getProperty() + " " + EnumDefinitions.convertOperator(param.getType()) + " " + MARKER);
							}
						}
					}
					//
					return pqlBuffer.toString();
				}
		
		public static String createSelectQuery(String tableName, String[]projectionParams, Logic whereLogic, String[] whereParams)
				throws IllegalArgumentException{
					//Query Builders
					StringBuffer pqlBuffer = null;
					try{
						pqlBuffer = new StringBuffer(createSelectQuery(tableName, projectionParams));
					}catch(IllegalArgumentException iex){
						throw iex;
					}
					
					if(whereParams != null 
							&& whereParams.length > 0
							&& !isAllParamEmpty(whereParams)){
						
						if(pqlBuffer.length() > 0){
							pqlBuffer.append(" WHERE ");
							
							int count = 0;
							for(String param : whereParams){
								
								if(param.trim().equals("")){
									continue;
								}
								if(count++ != 0){
									pqlBuffer.append( " " + whereLogic.name() + " ");
								}
								pqlBuffer.append( QUIENTIFIER + "." + param + " = " + MARKER);
							}
						}
					}
					//
					return pqlBuffer.toString();
				}
		
		public static String createSelectQuery(String tableName, String...projectionParams)
				throws IllegalArgumentException{
			//Query Builders
			StringBuffer pqlBuffer = new StringBuffer("SELECT ");
			if(projectionParams != null && projectionParams.length > 0){
				int count = 0;
				for(String str : projectionParams){
					if(str.trim().equals("")){
						continue;
					}
					if(count++ != 0){
						pqlBuffer.append(", ");
					}
					pqlBuffer.append( QUIENTIFIER + "." + str);
				}
				//If all passed parameter is empty
				if(count == 0){
					pqlBuffer.append(QUIENTIFIER + "." + STARIC);
				}
			}else{
				pqlBuffer.append(QUIENTIFIER + "." + STARIC);
			}
			pqlBuffer.append(" FROM "+ tableName + " " + QUIENTIFIER);
			return pqlBuffer.toString();
		}
	}
	
/////////////////////////////////////SQLUpdateQuery/////////////////////////////////////////////
	
	public static class SQLUpdateQuery extends SQLQuery{
		
	}
	
//////////////////////////////////SQLInsertQuery////////////////////////////////////////////////
	
	public static class SQLInsertQuery extends SQLQuery{
		
	}
	
//////////////////////////////////SQLDeleteQuery//////////////////////////////////////////////////
	
	public static class SQLDeleteQuery extends SQLQuery{
		
	}

}
