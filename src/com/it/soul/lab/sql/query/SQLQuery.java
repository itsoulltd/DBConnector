package com.it.soul.lab.sql.query;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.it.soul.lab.sql.query.builder.QueryBuilderImpl;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.ExpressionInterpreter;
import com.it.soul.lab.sql.query.models.PropertyList;

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
	
	public ExpressionInterpreter getWhereExpression() {
		return whereExpression;
	}

	public void setWhereExpression(ExpressionInterpreter whereExpression) {
		this.whereExpression = whereExpression;
		Expression[] comps = whereExpression.resolveExpressions();
		this.whereCompareParams = Arrays.asList(comps);
	}
	
	public List<Expression> getWhereCompareParams() {
		return whereCompareParams;
	}
	
	public PropertyList getWhereCompareProperties() {
		return Expression.convertToProperties(whereCompareParams);
	}

	public void setWhereCompareParams(List<Expression> whereCompareParams) {
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
			whereCompareParams = new ArrayList<Expression>();
			for (String params : whereParams) {
				whereCompareParams.add(new Expression(params, Operator.EQUAL));
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
	
	public String queryString() throws IllegalArgumentException{
		if(tableName == null || tableName.trim().equals("")){
			throw new IllegalArgumentException("Parameter 'tableName' must not be Null OR Empty.");
		}
		return "";
	}
	
	@Override
	public String toString() {
		return queryString().trim();
	}
	
	private String tableName;
	private String[] columns;
	private String[] whereParams;
	private Logic logic = Logic.AND;
	private List<Expression> whereCompareParams;
	private ExpressionInterpreter whereExpression;
	
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////SQLQuery-Enums//////////////////////////////////////////////
	
	public enum QueryType{
		SELECT,
		COUNT,
		DISTINCT,
		INSERT,
		UPDATE,
		DELETE,
		MAX
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
    
    public static enum Operator{
    	
    	EQUAL,
    	NOTEQUAL,
    	GREATER_THAN,
    	GREATER_THAN_OR_EQUAL,
    	LESS_THAN,
    	LESS_THAN_OR_EQUAL,
    	IN,
    	NOT_IN,
    	LIKE,
    	NOT_LIKE,
    	ASC,
    	DESC;
    	
    	public String toString(){

    		String eq = "=";
    		switch (this) {
    		case NOTEQUAL:
    			eq = "!=";
    			break;
    		case GREATER_THAN:
    			eq = ">";
    			break;
    		case GREATER_THAN_OR_EQUAL:
    			eq = ">=";
    			break;
    		case LESS_THAN:
    			eq = "<";
    			break;
    		case LESS_THAN_OR_EQUAL:
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
    			break;
    		case ASC:
    			eq = "ASC";
    			break;
    		case DESC:
    			eq = "DESC";
    			break;
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
