package com.it.soul.lab.sql.query;

import java.util.List;
import com.it.soul.lab.sql.query.models.Compare;

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
				pqlBuffer.append( QUIENTIFIER + "." + str);
			}
			//If all passed parameter is empty
			if(count == 0){pqlBuffer.append(QUIENTIFIER + "." + STARIC);}
		}else{
			pqlBuffer.append(QUIENTIFIER + "." + STARIC);
		}
	}
	
	@Override
	public void setTableName(String tableName) {
		super.setTableName(tableName);
		prepareTableName(tableName);
	}
	
	protected void prepareTableName(String name){
		pqlBuffer.append(" FROM "+ name + " " + QUIENTIFIER);
	}
	
	@Override
	public void setWhereParams(String[] whereParams) {
		super.setWhereParams(whereParams);
		prepareWhereParams(whereParams);
	}

	/**
	 * @param whereParams
	 */
	protected void prepareWhereParams(String[] whereParams) {
		/*if(whereParams != null 
				&& whereParams.length > 0
				&& !isAllParamEmpty(whereParams)){
			
			if(pqlBuffer.length() > 0){
				pqlBuffer.append(" WHERE ");
				int count = 0;
				for(String param : whereParams){
					if(param.trim().equals("")){continue;}
					if(count++ != 0){pqlBuffer.append( " " + getLogic().name() + " ");}
					pqlBuffer.append( QUIENTIFIER + "." + param + " = " + MARKER);
				}
			}
		}*/
		prepareWhereParams(Compare.createListFrom(whereParams, ComparisonType.IsEqual));
	}
	
	@Override
	public void setWhereCompareParams(List<Compare> whereParams) {
		super.setWhereCompareParams(whereParams);
		prepareWhereParams(whereParams);
	}

	/**
	 * @param whereParams
	 */
	protected void prepareWhereParams(List<Compare> whereParams) {
		if(whereParams != null 
				&& whereParams.size() > 0
				&& !isAllParamEmpty(whereParams.toArray())){
			
			if(pqlBuffer.length() > 0){
				pqlBuffer.append(" WHERE ");
				int count = 0;
				for(Compare param : whereParams){
					if(param.getProperty().trim().equals("")){continue;}
					if(count++ != 0){pqlBuffer.append( " " + getLogic().name() + " ");}
					pqlBuffer.append( QUIENTIFIER + "." + param.getProperty() + " " + param.getType().toString() + " " + MARKER);
				}
			}
		}
	}
	
	public static String create(String tableName, String[]projectionParams, Logic whereLogic, List<Compare> whereParams)
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
				for(Compare param : whereParams){
					if(param.getProperty().trim().equals("")){continue;}
					if(count++ != 0){pqlBuffer.append( " " + whereLogic.name() + " ");}
					pqlBuffer.append( QUIENTIFIER + "." + param.getProperty() + " " + param.getType().toString() + " " + MARKER);
				}
			}
		}
		//
		return pqlBuffer.toString();
	}
	
	public static String create(String tableName, String[]projectionParams, Logic whereLogic, String[] whereParams)
			throws IllegalArgumentException{
		//Query Builders
		/*StringBuffer pqlBuffer = null;
		try{ pqlBuffer = new StringBuffer(create(tableName, projectionParams)); }
		catch(IllegalArgumentException iex){throw iex;}

		if(whereParams != null 
				&& whereParams.length > 0
				&& !isAllParamEmpty(whereParams)){

			if(pqlBuffer.length() > 0){
				pqlBuffer.append(" WHERE ");
				int count = 0;
				for(String param : whereParams){
					if(param.trim().equals("")){continue;}
					if(count++ != 0){pqlBuffer.append( " " + whereLogic.name() + " ");}
					pqlBuffer.append( QUIENTIFIER + "." + param + " = " + MARKER);
				}
			}
		}
		return pqlBuffer.toString();*/
		
		return SQLSelectQuery.create(tableName, projectionParams, whereLogic, Compare.createListFrom(whereParams, ComparisonType.IsEqual));
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