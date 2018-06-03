package com.it.soul.lab.sql.query;

import java.util.List;

import com.it.soul.lab.sql.query.models.DataType;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.Logic;
import com.it.soul.lab.sql.query.models.Operator;
import com.it.soul.lab.sql.query.models.Property;

public class SQLScalerQuery extends SQLSelectQuery{
	
	public static enum ScalerType{
		COUNT,
		DISTINCT,
		MAX,
		MIN;
		
		public String toString(){
			String result;
			switch (this) {
			case DISTINCT:
				result = "DISTINCT";
				break;
			case MAX:
				result = "MAX";
				break;
			case MIN:
				result = "MIN";
				break;
			default:
				result = "COUNT";
				break;
			}
			return result;
		}
		
	}
	
	public SQLScalerQuery() {
		this(ScalerType.COUNT);
	}

	public SQLScalerQuery(ScalerType type) {
		this.pqlBuffer = new StringBuffer("SELECT ");
		this.type = type;
	}
	
	private ScalerType type = ScalerType.COUNT;

	public ScalerType getType() {
		return type;
	}

	@Override
	public String queryString() throws IllegalArgumentException{
		if(getTableName() == null || getTableName().trim().equals("")){
			throw new IllegalArgumentException("Parameter Table must not be Null OR Empty.");
		}
		if(isAllParamEmpty(getColumns())){
			throw new IllegalArgumentException("All Empty Parameters!!! You nuts (:D");
		}
		return pqlBuffer.toString();
	}
	
	protected void prepareColumns(String[] columns){
		if(columns != null && columns.length > 0){
			String firstParam = columns[0];
			pqlBuffer.append(type.toString()+"(" + firstParam + ")");
		}else{
			pqlBuffer.append(type.toString()+"(" + "*" + ")");
		}
	}
	
	@Override
	protected void prepareTableName(String name) {
		pqlBuffer.append(" From " + name + " ");
	}
	
	public void setScalerClouse(Property prop, Expression comps){
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
	
	@Override
	protected void prepareWhereParams(List<Expression> whereParams) {
		if(whereParams != null && whereParams.size() > 0){
			pqlBuffer.append("Where " );
			int count = 0;
			for (Expression ent : whereParams) {
				if(count++ != 0)
					pqlBuffer.append(" "+ getLogic().name() +" ");
				pqlBuffer.append(ent.getProperty()+ " " 
						+ ent.getType().toString() + " ?");
			}    			
		}
	}
	
	public static String createCountFunctionQuery(String tableName, String param, Logic logic, List<Expression> whereParams){

		param = (param != null && param.length()>=1) ? param : "*";
		
		StringBuilder builder = new StringBuilder("SELECT ");
		builder.append(ScalerType.COUNT.toString()+"(" + param + ")");
		builder.append(" From " + tableName + " ");

		if(whereParams != null && whereParams.size() > 0){
			builder.append("Where " );
			int count = 0;
			for (Expression ent : whereParams) {
				if(count++ != 0)
					builder.append(" "+ logic.name() +" ");
				builder.append(ent.getProperty() + " " 
						+ ent.getType().toString() + " ?");
			}    			
		}
		return builder.toString();
	}

	public static String createCountFunctionQuery(String tableName, String param, String whereParam,Operator type, Property paramValue){

		param = (param != null && param.length()>=1) ? param : "*";

		StringBuilder builder = new StringBuilder("SELECT ");
		builder.append(ScalerType.COUNT.toString()+"(" + param + ")");
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