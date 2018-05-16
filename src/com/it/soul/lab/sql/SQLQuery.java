package com.it.soul.lab.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.it.soul.lab.util.EnumDefinitions;
import com.it.soul.lab.util.EnumDefinitions.ComparisonType;
import com.it.soul.lab.util.EnumDefinitions.DataType;
import com.it.soul.lab.util.EnumDefinitions.Logic;

public class SQLQuery {
	
	public enum QueryType{
		Select,
		Count,
		Distinct,
		Insert,
		Update,
		Delete
	}
	
	public static interface BuilderBase{
		public SQLQuery build();
	}
	
	public static interface ColumnsBuilder extends BuilderBase{
		public TableBuilder columns(String... name);
	}
	
	public static interface TableBuilder extends BuilderBase{
		public WhereClauseBuilder table(String name);
	}
	
	public static interface WhereClauseBuilder extends BuilderBase{
		public BuilderBase whereParams(Logic logic, String... name);
		public BuilderBase whereParams(Logic logic, Compare... comps);
		public BuilderBase countClause(Property prop, Compare comps);
		public BuilderBase countClause(Logic logic, Compare... comps);
		public BuilderBase distinctClause(Property prop, Compare comps);
		public BuilderBase distinctClause(Logic logic, Compare... comps);
	}
	
	public static class Builder implements ColumnsBuilder, TableBuilder, WhereClauseBuilder{
		
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
			case Count:
				temp = new SQLCountQuery();
				break;
			case Distinct:
				temp = new SQLDistinctQuery();
				break;
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
		
		public WhereClauseBuilder table(String name){
			tempQuery.setTableName(name);
			return this;
		}
		public TableBuilder columns(String... name){
			tempQuery.setColumns(name);
			return this;
		}
		public BuilderBase whereParams(Logic logic, String... name){
			tempQuery.setLogic(logic);
			tempQuery.setWhereParams(name);
			return this;
		}
		public BuilderBase whereParams(Logic logic, Compare... comps){
			tempQuery.setLogic(logic);
			List<Compare> items = new ArrayList<Compare>(Arrays.asList(comps));
			tempQuery.setWhereCompareParams(items);
			return this;
		}
		@Override
		public BuilderBase countClause(Property prop, Compare comps) {
			if(tempQuery instanceof SQLCountQuery){
				((SQLCountQuery)tempQuery).setCountClouse(prop, comps);
			}
			return this;
		}
		@Override
		public BuilderBase countClause(Logic logic, Compare... comps) {
			if(tempQuery instanceof SQLCountQuery){
				((SQLCountQuery)tempQuery).setCountClouse(logic, Arrays.asList(comps));
			}
			return this;
		}
		@Override
		public BuilderBase distinctClause(Property prop, Compare comps) {
			if(tempQuery instanceof SQLDistinctQuery){
				((SQLDistinctQuery)tempQuery).setCountClouse(prop, comps);
			}
			return this;
		}
		@Override
		public BuilderBase distinctClause(Logic logic, Compare... comps) {
			if(tempQuery instanceof SQLDistinctQuery){
				((SQLDistinctQuery)tempQuery).setCountClouse(logic, Arrays.asList(comps));
			}
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
	
	/////////////////////////////////////////SQLCountQuery/////////////////////////////////////////////////
	
	public static class SQLDistinctQuery extends SQLCountQuery{
		@Override
		protected void prepareColumnsBuffers() {
			if(getColumns() != null && getColumns().length > 0){
				String firstParam = getColumns()[0];
				pqlBuffer.append(DISTINCT_FUNC+"(" + firstParam + ")");
			}else{
				pqlBuffer.append(DISTINCT_FUNC+"(" + "*" + ")");
			}
		}
	}
	
	public static class SQLCountQuery extends SQLQuery{

		protected StringBuffer pqlBuffer = new StringBuffer("SELECT ");

		@Override
		public String queryString() throws IllegalArgumentException{
			super.queryString();
			return pqlBuffer.toString();
		}
		
		@Override
		public void setColumns(String[] columns) {
			super.setColumns(columns);
			prepareColumnsBuffers();
		}
		
		protected void prepareColumnsBuffers(){
			if(getColumns() != null && getColumns().length > 0){
				String firstParam = getColumns()[0];
				pqlBuffer.append(COUNT_FUNC+"(" + firstParam + ")");
			}else{
				pqlBuffer.append(COUNT_FUNC+"(" + "*" + ")");
			}
		}
		
		@Override
		public void setTableName(String tableName) {
			super.setTableName(tableName);
			pqlBuffer.append(" From " + tableName + " ");
		}
		
		public void setCountClouse(Property prop, Compare comps){
			if(prop != null){
				pqlBuffer.append("Where " + prop.getKey() +" "+ EnumDefinitions.convertOperator(comps.getType()) +" ");
				if(prop.getType() == DataType.ParamDataTypeBoolean 
						|| prop.getType() == DataType.ParamDataTypeInt
						|| prop.getType() == DataType.ParamDataTypeDouble
						|| prop.getType() == DataType.ParamDataTypeFloat) {
					pqlBuffer.append(prop.getValue());
				}else{
					pqlBuffer.append("'" + prop.getValue() + "'");
				}
			}
		}
		
		public void setCountClouse(Logic logic, List<Compare> whereParams){
			if(whereParams != null && whereParams.size() > 0){
				pqlBuffer.append("Where " );
				int count = 0;
				for (Compare ent : whereParams) {
					if(count++ != 0)
						pqlBuffer.append(" "+ logic.name() +" ");
					pqlBuffer.append(ent.getProperty()+ " " 
							+ EnumDefinitions.convertOperator(ent.getType()) + " ?");
				}    			
			}
		}
		
		public static String createCountFunctionQuery(String tableName, String param, Logic logic, List<Compare> whereParams){

			param = (param != null && param.length()>=1) ? param : "*";
			
			StringBuilder builder = new StringBuilder("SELECT ");
			builder.append(COUNT_FUNC+"(" + param + ")");
			builder.append(" From " + tableName + " ");

			if(whereParams != null && whereParams.size() > 0){
				builder.append("Where " );
				int count = 0;
				for (Compare ent : whereParams) {
					if(count++ != 0)
						builder.append(" "+ logic.name() +" ");
					builder.append(ent.getProperty() + " " 
							+ EnumDefinitions.convertOperator(ent.getType()) + " ?");
				}    			
			}
			return builder.toString();
		}

		public static String createCountFunctionQuery(String tableName, String param, String whereParam,ComparisonType type, Property paramValue){

			param = (param != null && param.length()>=1) ? param : "*";

			StringBuilder builder = new StringBuilder("SELECT ");
			builder.append(COUNT_FUNC+"(" + param + ")");
			builder.append(" From " + tableName + " ");

			if(whereParam != null && paramValue != null){
				builder.append("Where " + whereParam +" "+ EnumDefinitions.convertOperator(type) +" ");
				if(paramValue.getType() == DataType.ParamDataTypeBoolean 
						|| paramValue.getType() == DataType.ParamDataTypeInt
						|| paramValue.getType() == DataType.ParamDataTypeDouble
						|| paramValue.getType() == DataType.ParamDataTypeFloat) {
					builder.append(paramValue.getValue());
				}else{
					builder.append("'"+paramValue.getValue()+"'");
				}
			}

			return builder.toString();
		}

	}
	
	/////////////////////////////////////////SQLSelectQuery/////////////////////////////////////////////////
	//TODO: Following classes must be re-located in packages as subclass of SQLQuery.
	
	public static class SQLSelectQuery extends SQLQuery{
		
		private StringBuffer pqlBuffer = new StringBuffer("SELECT ");
		
		@Override
		public String queryString() throws IllegalArgumentException{
			super.queryString();
			return pqlBuffer.toString();
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
