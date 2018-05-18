package com.it.soul.lab.sql.query;

import java.util.List;
import com.it.soul.lab.sql.query.models.Compare;
import com.it.soul.lab.sql.query.models.Property;

public class SQLCountQuery extends SQLQuery{

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
			pqlBuffer.append("Where " + prop.getKey() +" "+ comps.getType().toString() +" ");
			if(prop.getType() == DataType.BOOL 
					|| prop.getType() == DataType.INT
					|| prop.getType() == DataType.DOUBLE
					|| prop.getType() == DataType.FLOAT) {
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
						+ ent.getType().toString() + " ?");
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
						+ ent.getType().toString() + " ?");
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
			builder.append("Where " + whereParam +" "+ type.toString() +" ");
			if(paramValue.getType() == DataType.BOOL 
					|| paramValue.getType() == DataType.INT
					|| paramValue.getType() == DataType.DOUBLE
					|| paramValue.getType() == DataType.FLOAT) {
				builder.append(paramValue.getValue());
			}else{
				builder.append("'"+paramValue.getValue()+"'");
			}
		}

		return builder.toString();
	}

}