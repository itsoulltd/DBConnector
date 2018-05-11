package com.it.soul.lab.sql;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;

import com.it.soul.lab.sql.EnumDefinitions.ComparisonType;
import com.it.soul.lab.sql.EnumDefinitions.DataType;
import com.it.soul.lab.sql.EnumDefinitions.Logic;

public class SQLBuilder {
	
	private static final char QUIENTIFIER = 'e';
	private static final char STARIC = '*';
	private static final char marker = '?';
	private static final String COUNT_FUNC = "COUNT";
	private static final String DISTINCT_FUNC = "DISTINCT";
	
	/**
	 * 
	 * @param tableName
	 * @param param
	 * @param where
	 * @param type
	 * @param value
	 * @return
	 */
	public static String createCountFunctionQuery(String tableName, String param, String whereParam,ComparisonType type, Parameter paramValue){
		
		StringBuilder builder = new StringBuilder("Select ");
		
		param = (param != null && param.length()>=1) ? param : "*";
		builder.append(COUNT_FUNC+"(" + param + ")");
		builder.append(" From " + tableName + " ");
		
		if(whereParam != null && paramValue != null){
			builder.append(" Where " + whereParam +" "+ EnumDefinitions.convertOperator(type) +" ");
			if(paramValue.type == DataType.ParamDataTypeBoolean 
					|| paramValue.type == DataType.ParamDataTypeInt
					|| paramValue.type == DataType.ParamDataTypeDouble
					|| paramValue.type == DataType.ParamDataTypeFloat) {
				builder.append(paramValue.value);
			}else{
				builder.append("'"+paramValue.value+"'");
			}
		}
		
		return builder.toString();
	}
	/**
	 * 
	 * @param tableName
	 * @param param
	 * @param whereParam
	 * @param type
	 * @return
	 */
	public static String createCountFunctionQuery(String tableName, String param, String whereParam,ComparisonType type){

		StringBuilder builder = new StringBuilder("Select ");

		param = (param != null && param.length()>=1) ? param : "*";
		builder.append(COUNT_FUNC+"(" + param + ")");
		builder.append(" From " + tableName + " ");

		if(whereParam != null){
			builder.append(" Where " 
					+ whereParam +" "
					+ EnumDefinitions.convertOperator(type) +" ?");
		}

		return builder.toString();
	}
	/**
	 * 
	 * @param tableName
	 * @param param
	 * @param whereParams
	 * @return
	 */
	public static String createCountFunctionQuery(String tableName, String param, Logic logic,Map<String, ComparisonType> whereParams){

		StringBuilder builder = new StringBuilder("Select ");

		param = (param != null && param.length()>=1) ? param : "*";
		builder.append(COUNT_FUNC+"(" + param + ")");
		builder.append(" From " + tableName + " ");

		if(whereParams != null && whereParams.size() > 0){
			builder.append(" Where " );
			int count = 0;
			for (Entry<String,ComparisonType> ent : whereParams.entrySet()) {
				
				if(count++ != 0)
					builder.append(" "+ logic.name() +" ");
				builder.append(ent.getKey()+ " " 
						+ EnumDefinitions.convertOperator(ent.getValue()) + " ?");
			}    			
		}

		return builder.toString();
	}
	
	/**
	 * 
	 * @param tableName
	 * @param param
	 * @param where
	 * @param type
	 * @param value
	 * @return
	 */
	public static String createDistinctFunctionQuery(String tableName, String param, String whereParam,ComparisonType type, Parameter paramValue)
	throws IllegalArgumentException {
		
		StringBuilder builder = new StringBuilder("Select ");
		
		if(param != null && param.length()>=1 && !param.trim().startsWith("*")){
			builder.append(DISTINCT_FUNC+"(" + param + ")");
		}else{
			throw new IllegalArgumentException("Mallfunctioned Arguments!!!");
		}
		
		builder.append(" From " + tableName + " ");
		
		if(whereParam != null && paramValue != null){
			builder.append(" Where " + whereParam +" "+ EnumDefinitions.convertOperator(type) +" ");
			if(paramValue.type == DataType.ParamDataTypeBoolean 
					|| paramValue.type == DataType.ParamDataTypeInt
					|| paramValue.type == DataType.ParamDataTypeDouble
					|| paramValue.type == DataType.ParamDataTypeFloat) {
				builder.append(paramValue.value);
			}else{
				builder.append("'"+paramValue.value+"'");
			}
		}
		
		return builder.toString();
	}
	
	/**
	 * 
	 * @param tableName
	 * @param projectionParams
	 * @return
	 */
	public static String createSelectQuery(String tableName, String...projectionParams)
	throws IllegalArgumentException{
		
		//Checking Illegal Arguments
		try{
			if(tableName == null || tableName.trim().equals("")){
				throw new IllegalArgumentException("Parameter 'tableName' must not be Null OR Empty.");
			}
		}catch(IllegalArgumentException iex){
			throw iex;
		}
		
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
		//
		return pqlBuffer.toString();
	}
	
	/**
	 * 
	 * @param tableName
	 * @param projectionParams
	 * @param whereLogic
	 * @param whereParams
	 * @return
	 */
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
					pqlBuffer.append( QUIENTIFIER + "." + param + " = " + marker);
				}
			}
		}
		//
		return pqlBuffer.toString();
	}
	
	/**
	 * 
	 * @param tableName
	 * @param projectionParams
	 * @param whereLogic
	 * @param whereParams 
	 * @return
	 */
	public static String createSelectQuery(String tableName, String[]projectionParams, Logic whereLogic, Map<String, ComparisonType> whereParams)
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
				&& !isAllParamEmpty(whereParams.keySet().toArray())){
			
			if(pqlBuffer.length() > 0){
				pqlBuffer.append(" WHERE ");
				
				int count = 0;
				for(Entry<String, ComparisonType> param : whereParams.entrySet()){
					
					if(param.getKey().trim().equals("")){
						continue;
					}
					if(count++ != 0){
						pqlBuffer.append( " " + whereLogic.name() + " ");
					}
					pqlBuffer.append( QUIENTIFIER + "." + param.getKey() + " " + EnumDefinitions.convertOperator(param.getValue()) + " " + marker);
				}
			}
		}
		//
		return pqlBuffer.toString();
	}
	
	/**
	 * 
	 * @param tableName
	 * @param setParams
	 * @param whereLogic
	 * @param whereParams
	 * @return
	 */
	public static String createUpdateQuery(String tableName, String[]setParams, Logic whereLogic, String[] whereParams){
		
		//Checking Illegal Arguments
		try{
			if(tableName == null || tableName.trim().equals("")){
				throw new IllegalArgumentException("Parameter 'tableName' must not be Null OR Empty.");
			}
			if(isAllParamEmpty(setParams)){
				throw new IllegalArgumentException("All Empty Parameters!!! You nuts (:D");
			}
		}catch(IllegalArgumentException iex){
			throw iex;
		}
		
		StringBuffer pqlBuffer = new StringBuffer("UPDATE " + tableName + " SET ");
		
		if(setParams != null && setParams.length > 0){
			
			int count = 0;
			for(String str : setParams){
				
				if(str.trim().equals("")){
					continue;
				}
				if(count++ != 0){
					pqlBuffer.append(", ");
				}
				
				pqlBuffer.append( str + " = " + marker);
			}
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
					
					pqlBuffer.append( param + " = " + marker);
				}
			}
		}
		
		return pqlBuffer.toString();
	}
	
	/**
	 * 
	 * @param tableName
	 * @param setParams
	 * @param whereLogic
	 * @param whereParams
	 * @return
	 */
	public static String createUpdateQuery(String tableName, String[]setParams, Logic whereLogic, Map<String,ComparisonType> whereParams){
		
		//Checking Illegal Arguments
		try{
			if(tableName == null || tableName.trim().equals("")){
				throw new IllegalArgumentException("Parameter 'tableName' must not be Null OR Empty.");
			}
			if(isAllParamEmpty(setParams)){
				throw new IllegalArgumentException("All Empty Parameters!!! You nuts (:D");
			}
		}catch(IllegalArgumentException iex){
			throw iex;
		}
		
		StringBuffer pqlBuffer = new StringBuffer("UPDATE " + tableName + " SET ");
		
		if(setParams != null && setParams.length > 0){
			
			int count = 0;
			for(String str : setParams){
				
				if(str.trim().equals("")){
					continue;
				}
				if(count++ != 0){
					pqlBuffer.append(", ");
				}
				
				pqlBuffer.append( str + " = " + marker);
			}
		}
		
		if(whereParams != null 
				&& whereParams.size() > 0
				&& !isAllParamEmpty(whereParams.keySet().toArray())){
			
			if(pqlBuffer.length() > 0){
				pqlBuffer.append(" WHERE ");
				
				int count = 0;
				for(Entry<String,ComparisonType> param : whereParams.entrySet()){
					
					if(param.getKey().trim().equals("")){
						continue;
					}
					if(count++ != 0){
						pqlBuffer.append( " " + whereLogic.name() + " ");
					}
					
					pqlBuffer.append( param.getKey() + EnumDefinitions.convertOperator(param.getValue()) + marker);
				}
			}
		}
		
		return pqlBuffer.toString();
	}
	
	/**
	 * 
	 * @param tableName
	 * @param insertParams
	 * @return
	 */
	public static String createInsertQuery(String tableName, Object[]insertParams){
		
		//Checking Illegal Arguments
		try{
			if(tableName == null || tableName.trim().equals("")){
				throw new IllegalArgumentException("Parameter 'tableName' must not be Null OR Empty.");
			}
			if(isAllParamEmpty(insertParams)){
				throw new IllegalArgumentException("All Empty Parameters!!! You nuts (:D");
			}
		}catch(IllegalArgumentException iex){
			throw iex;
		}
		
		StringBuffer pqlBuffer = new StringBuffer("INSERT INTO " + tableName + " ( " );
		StringBuffer valueBuffer = new StringBuffer(" VALUES ( ");
		
		if(insertParams != null && insertParams.length > 0){
			
			int count = 0;
			for(Object str : insertParams){
				
				if(str.toString().trim().equals("")){
					continue;
				}
				
				if(count != 0){
					pqlBuffer.append(", ");
					valueBuffer.append(", ");
				}
				
				pqlBuffer.append( str.toString() );
				valueBuffer.append(marker);
				
				if(count == (insertParams.length - 1)){
					pqlBuffer.append(")");
					valueBuffer.append(")");
				}
				count++;
			}
		}
		
		return pqlBuffer.toString() + valueBuffer.toString();
	}
	/**
	 * 
	 * @param tableName
	 * @param insertParams
	 * @return
	 */
	public static String createInsertQuery(String tableName, Map<String, Parameter> insertParams){
		
		//Checking Illegal Arguments
		try{
			if(tableName == null || tableName.trim().equals("")){
				throw new IllegalArgumentException("Parameter 'tableName' must not be Null OR Empty.");
			}
			if(isAllParamEmpty(insertParams.keySet().toArray())){
				throw new IllegalArgumentException("All Empty Parameters!!! You nuts (:D");
			}
		}catch(IllegalArgumentException iex){
			throw iex;
		}
		
		StringBuffer pqlBuffer = new StringBuffer("INSERT INTO " + tableName + " ( " );
		StringBuffer valueBuffer = new StringBuffer(" VALUES ( ");
		
		if(insertParams != null && insertParams.size() > 0){
			
			int count = 0;
			for( Entry<String,Parameter> ent : insertParams.entrySet()){
				
				if(ent.getKey().trim().equals("")){
					continue;
				}
				
				if(count != 0){
					pqlBuffer.append(", ");
					valueBuffer.append(", ");
				}
				
				pqlBuffer.append( ent.getKey() );
				
				Parameter val = ent.getValue();
				if(val.getType() == DataType.ParamDataTypeBoolean 
    					|| val.getType() == DataType.ParamDataTypeInt
    					|| val.getType() == DataType.ParamDataTypeDouble
    					|| val.getType() == DataType.ParamDataTypeFloat) {
					valueBuffer.append(val.getValue().toString());
				}else{
					valueBuffer.append("'"+val.getValue().toString()+"'");
				}
				
				if(count == (insertParams.size() - 1)){
					pqlBuffer.append(") ");
					valueBuffer.append(")");
				}
				count++;
			}
		}
		
		return pqlBuffer.toString() + valueBuffer.toString();
	}
	
	/**
	 * 
	 * @param tableName
	 * @param whereLogic
	 * @param whereParams
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String createDeleteQuery(String tableName ,Logic whereLogic ,String...whereParams)
	throws IllegalArgumentException{
		
		//Checking Illegal Arguments
		try{
			if(tableName == null || tableName.trim().equals("")){
				throw new IllegalArgumentException("Parameter 'tableName' must not be Null OR Empty.");
			}
		}catch(IllegalArgumentException iex){
			throw iex;
		}
		
		//Query Builders
		//StringBuffer pqlBuffer = new StringBuffer("DELETE FROM "+ tableName + " " + QUIENTIFIER );
		StringBuffer pqlBuffer = new StringBuffer("DELETE FROM "+ tableName + " " );
		
		if(whereParams != null 
				&& whereParams.length > 0
				&& !isAllParamEmpty(whereParams)){
			
			if(pqlBuffer.length() > 0){
				
				pqlBuffer.append( " WHERE ");
				
				int count = 0;
				for(String param : whereParams){
					
					if(param.trim().equals("")){
						continue;
					}
					if(count++ != 0){
						pqlBuffer.append( " " + whereLogic.name() + " ");
					}
					//pqlBuffer.append( QUIENTIFIER + "." + param + " = " + marker);
					pqlBuffer.append( param + " = " + marker);
				}
			}
		}
		
		//
		return pqlBuffer.toString();
	}
	/**
	 * 
	 * @param tableName
	 * @param whereLogic
	 * @param whereParams
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static String createDeleteQuery(String tableName ,Logic whereLogic ,Map<String, ComparisonType> whereParams)
	throws IllegalArgumentException{
		
		//Checking Illegal Arguments
		try{
			if(tableName == null || tableName.trim().equals("")){
				throw new IllegalArgumentException("Parameter 'tableName' must not be Null OR Empty.");
			}
		}catch(IllegalArgumentException iex){
			throw iex;
		}
		
		//Query Builders
		StringBuffer pqlBuffer = new StringBuffer("DELETE FROM "+ tableName + " " );
		
		if(whereParams != null 
				&& whereParams.size() > 0
				&& !isAllParamEmpty(whereParams.keySet().toArray())){
			
			if(pqlBuffer.length() > 0){
				
				pqlBuffer.append( " WHERE ");
				
				int count = 0;
				for(Entry<String,ComparisonType> ent : whereParams.entrySet()){
					
					if(ent.getKey().trim().equals("")){
						continue;
					}
					if(count++ != 0){
						pqlBuffer.append( " " + whereLogic.name() + " ");
					}
					
					pqlBuffer.append( ent.getKey() + " " +EnumDefinitions.convertOperator(ent.getValue())+" " + marker);
				}
			}
		}
		
		//
		return pqlBuffer.toString();
	}
	
	private static boolean isAllParamEmpty(Object[]paramList){
		
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
	
	/**
     * 
     */
    public static class Parameter{
    	
    	private static final String SQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    	private String property;
    	private DataType type;
    	private Object value;
    	
    	public Parameter(Object value, DataType type){
    		
    		this.property = "";
    		this.type = type;
    		
    		//TODO Regx type value validation is required.
    		if(value instanceof java.util.Date || value instanceof java.sql.Date){
    			value = getFormattedDateString(value);
    			this.type = DataType.ParamDataTypeString;
    		}
    		
    		this.value = value;
    	}
    	
    	public Parameter(String property, Object value, DataType type){
    		this(value,type);
    		this.property = property;
    	}
    	
    	@Override
    	public boolean equals(Object obj) {
    		
    		if(obj instanceof Parameter){
    			boolean isSame = false;
    			Parameter compareble = (Parameter)obj;
    			if(this.getProperty() == compareble.getProperty()){
    				isSame = true;
    			}
    			return isSame;
    		}else{
    			return false;
    		}
    	}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String property) {
			this.property = property;
		}

		public DataType getType() {
			return type;
		}
		
		private String getFormattedDateString(Object date) {

			String result = null;

			SimpleDateFormat formatter = new SimpleDateFormat(SQL_DATE_FORMAT);
			try {
				if (date != null 
						&& ((date instanceof java.util.Date) 
								|| (date instanceof java.sql.Date))) {

					result = formatter.format(date);
				}
			} catch (Exception ex) {
				result = null;
				ex.printStackTrace();
			}

			return result;
		}

    }//End ParamProperties
}
