package com.it.soul.lab.sql.query;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.it.soul.lab.sql.query.builder.QueryBuilderImpl;
import com.it.soul.lab.sql.query.models.Compare;
import com.it.soul.lab.sql.query.models.LogicExpression;
import com.it.soul.lab.sql.query.models.Properties;

public class SQLQuery {
	
	public static class Builder extends QueryBuilderImpl{
		
		public Builder(QueryType type){
			tempType = type;
			tempQuery = factory(tempType);
		}
		
		public SQLQuery build(){
			//SQLQuery query = factory(tempType);
			//TODO: Build up a new Object from existing one.
			return tempQuery;
		}
		
	}
	
	//////////////////////////////////SQLQuery///////////////////////////////////////////
	
	public LogicExpression getWhereExpression() {
		return whereExpression;
	}

	public void setWhereExpression(LogicExpression whereExpression) {
		this.whereExpression = whereExpression;
		Compare[] comps = whereExpression.resolveCompares();
		this.whereCompareParams = Arrays.asList(comps);
	}
	
	public List<Compare> getWhereCompareParams() {
		return whereCompareParams;
	}
	
	public Properties getWhereCompareProperties() {
		return Compare.convertToProperties(whereCompareParams);
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
		if (whereParams == null && whereCompareParams != null) {
			return getWhereCompareProperties().getKeys();
		}
		return whereParams;
	}
	public void setWhereParams(String[] whereParams) {
		this.whereParams = whereParams;
		if(whereCompareParams == null){
			whereCompareParams = new ArrayList<Compare>();
			for (String params : whereParams) {
				whereCompareParams.add(new Compare(params, ComparisonType.IsEqual));
			}
		}
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
	
	@Override
	public String toString() {
		return queryString();
	}
	
	private String tableName;
	private String[] columns;
	private String[] whereParams;
	private Logic logic;
	private List<Compare> whereCompareParams;
	private LogicExpression whereExpression;
	
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////SQLQuery-Enums//////////////////////////////////////////////
	
	public enum QueryType{
		Select,
		Count,
		Distinct,
		Insert,
		Update,
		Delete
	}
	
	public static enum DataType{
    	INT,
    	FLOAT,
    	DOUBLE,
    	BOOL,
    	STRING,
    	SQLDATETIME,
    	BLOB,
    	BYTEARRAY,
    	OBJECT
    }
    
    public static enum ComparisonType{
    	
    	IsEqual,
    	IsNotEqual,
    	IsGreater,
    	IsGreaterOrEqual,
    	IsSmaller,
    	IsSmallerOrEqual,
    	IN,
    	NOT_IN,
    	LIKE,
    	NOT_LIKE;
    	
    	public String toString(){

    		String eq = "=";
    		switch (this) {
    		case IsNotEqual:
    			eq = "!=";
    			break;
    		case IsGreater:
    			eq = ">";
    			break;
    		case IsGreaterOrEqual:
    			eq = ">=";
    			break;
    		case IsSmaller:
    			eq = "<";
    			break;
    		case IsSmallerOrEqual:
    			eq = "<=";
    			break;
    		case IN:
    			eq = "IN";
    			break;
    		case NOT_IN:
    			eq = "NOT IN";
    			break;
    		case LIKE:
    			eq = "LIKE";
    			break;
    		case NOT_LIKE:
    			eq = "NOT LIKE";
    		default:
    			break;
    		}
    		return eq;
    	}
    }
    
    public static enum Logic{
    	AND,
    	OR
    }

}
