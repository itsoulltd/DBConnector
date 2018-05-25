package com.it.soul.lab.sql.query;

import java.util.List;

import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.LogicExpression;

public class SQLSelectQuery extends SQLQuery{
	
	protected StringBuffer pqlBuffer;
	
	public SQLSelectQuery() {
		this.pqlBuffer = new StringBuffer("SELECT ");
	}
	
	@Override
	public String queryString() throws IllegalArgumentException{
		super.queryString();
		return pqlBuffer.toString();
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
		prepareWhereParams(Expression.createListFrom(whereParams, Operator.IsEqual));
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
	public void setWhereExpression(LogicExpression whereExpression) {
		super.setWhereExpression(whereExpression);
		prepareWhereExpression(whereExpression);
	}
	
	protected void prepareWhereExpression(LogicExpression whereExpression){
		pqlBuffer.append("WHERE " + whereExpression.express());
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
		return SQLSelectQuery.create(tableName, projectionParams, whereLogic, Expression.createListFrom(whereParams, Operator.IsEqual));
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