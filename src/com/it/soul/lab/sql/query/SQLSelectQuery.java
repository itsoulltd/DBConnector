package com.it.soul.lab.sql.query;

import java.util.List;

import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.ExpressionInterpreter;
import com.it.soul.lab.sql.query.models.Logic;
import com.it.soul.lab.sql.query.models.Operator;

public class SQLSelectQuery extends SQLQuery{
	
	protected StringBuffer pqlBuffer;
	protected Integer limit;
	protected Integer offset;
	protected List<String> orderByList;
	
	public SQLSelectQuery() {
		this.pqlBuffer = new StringBuffer("SELECT ");
	}
	
	@Override
	public String queryString() throws IllegalArgumentException{
		super.queryString();
		return pqlBuffer.toString();
	}
	
	public void setLimit(Integer limit, Integer offset) {
		this.limit = (limit < 0) ? 0 : limit;
		this.offset = (offset < 0) ? 0 : offset;
		if (limit > 0) { 
			pqlBuffer.append(" LIMIT " + limit) ;
			if (offset > 0) { pqlBuffer.append(" OFFSET " + offset) ;}
		}
	}
	
	public void setOrderBy(List<String> columns, Operator opt) {
		this.orderByList = columns;
		if (columns != null && columns.size() > 0) {
			StringBuffer orderBuffer = new StringBuffer(" ORDER BY ");
			int count = 0;
			for(String col : columns) {
				if(col.trim().equals("")){continue;}
				if(count++ != 0){pqlBuffer.append(", ");}
				orderBuffer.append(col);
			}
			if (count > 0) {
				if (opt != null) {orderBuffer.append(" " + opt.toString());}
				pqlBuffer.append(orderBuffer.toString());
			}
		}
	}
	
	public void setGroupBy(List<String> columns) {
		this.orderByList = columns;
		if (columns != null && columns.size() > 0) {
			StringBuffer orderBuffer = new StringBuffer(" GROUP BY ");
			int count = 0;
			for(String col : columns) {
				if(col.trim().equals("")){continue;}
				if(count++ != 0){pqlBuffer.append(", ");}
				orderBuffer.append(col);
			}
			if (count > 0) {
				pqlBuffer.append(orderBuffer.toString());
			}
		}
	}
	
	@Override
	public void setColumns(String[] columns) {
		super.setColumns(columns);
		prepareColumns(getColumns());
	}
	
	protected void prepareColumns(String[] columns){
		if(getColumns() != null && getColumns().length > 0){
			int count = 0;
			for(String str : getColumns()){
				if(str.trim().equals("")){continue;}
				if(count++ != 0){pqlBuffer.append(", ");}
				pqlBuffer.append(str);
			}
			//If all passed parameter is empty
			if(count == 0){pqlBuffer.append(STARIC);}
		}else{
			pqlBuffer.append(STARIC);
		}
	}
	
	@Override
	public void setTableName(String tableName) {
		super.setTableName(tableName);
		prepareTableName(tableName);
	}
	
	protected void prepareTableName(String name){
		pqlBuffer.append(" FROM "+ name + " ");
	}
	
	@Override
	public void setWhereParams(String[] whereParams) {
		super.setWhereParams(whereParams);
		prepareWhereParams(whereParams);
	}
	
	protected void prepareWhereParams(String[] whereParams) {
		prepareWhereParams(Expression.createListFrom(whereParams, Operator.EQUAL));
	}
	
	@Override
	public void setWhereCompareParams(List<Expression> whereParams) {
		super.setWhereCompareParams(whereParams);
		prepareWhereParams(whereParams);
	}
	
	protected void prepareWhereParams(List<Expression> whereParams) {
		if(whereParams != null 
				&& whereParams.size() > 0
				&& !isAllParamEmpty(whereParams.toArray())){
			
			if(pqlBuffer.length() > 0){
				pqlBuffer.append("WHERE ");
				int count = 0;
				for(Expression param : whereParams){
					if(param.getProperty().trim().equals("")){continue;}
					if(count++ != 0){pqlBuffer.append( " " + getLogic().name() + " ");}
					pqlBuffer.append(param.getProperty() + " " + param.getType().toString() + " " + MARKER);
				}
			}
		}
	}
	
	@Override
	public void setWhereExpression(ExpressionInterpreter whereExpression) {
		super.setWhereExpression(whereExpression);
		prepareWhereExpression(whereExpression);
	}
	
	protected void prepareWhereExpression(ExpressionInterpreter whereExpression){
		pqlBuffer.append("WHERE " + whereExpression.interpret());
	}
	
	public static String create(String tableName, String[]projectionParams, Logic whereLogic, List<Expression> whereParams)
			throws IllegalArgumentException{

		//Query Builders
		StringBuffer pqlBuffer = null;
		try{pqlBuffer = new StringBuffer(create(tableName, projectionParams));}
		catch(IllegalArgumentException iex){throw iex;}

		if(whereParams != null 
				&& whereParams.size() > 0
				&& !isAllParamEmpty(whereParams.toArray())){

			if(pqlBuffer.length() > 0){
				pqlBuffer.append(" WHERE ");
				int count = 0;
				for(Expression param : whereParams){
					if(param.getProperty().trim().equals("")){continue;}
					if(count++ != 0){pqlBuffer.append( " " + whereLogic.name() + " ");}
					pqlBuffer.append(param.getProperty() + " " + param.getType().toString() + " " + MARKER);
				}
			}
		}
		//
		return pqlBuffer.toString();
	}
	
	public static String create(String tableName, String[]projectionParams, Logic whereLogic, String[] whereParams)
			throws IllegalArgumentException{
		return SQLSelectQuery.create(tableName, projectionParams, whereLogic, Expression.createListFrom(whereParams, Operator.EQUAL));
	}
	
	public static String create(String tableName, String...projectionParams)
			throws IllegalArgumentException{
		//Query Builders
		StringBuffer pqlBuffer = new StringBuffer("SELECT ");
		if(projectionParams != null && projectionParams.length > 0){
			int count = 0;
			for(String str : projectionParams){
				if(str.trim().equals("")){continue;}
				if(count++ != 0){pqlBuffer.append(", ");}
				pqlBuffer.append( QUIENTIFIER + "." + str);
			}
			//If all passed parameter is empty
			if(count == 0){pqlBuffer.append(QUIENTIFIER + "." + STARIC);}
		}else{
			pqlBuffer.append(QUIENTIFIER + "." + STARIC);
		}
		pqlBuffer.append(" FROM "+ tableName + " " + QUIENTIFIER);
		return pqlBuffer.toString();
	}
}