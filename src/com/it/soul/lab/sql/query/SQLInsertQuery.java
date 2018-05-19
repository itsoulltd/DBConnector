package com.it.soul.lab.sql.query;

import java.util.List;
import java.util.Map.Entry;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.Properties;

public class SQLInsertQuery extends SQLQuery{
	
	private StringBuffer pqlBuffer = new StringBuffer("INSERT INTO ");
	private StringBuffer paramBuffer = new StringBuffer(" ( ");
	private StringBuffer valueBuffer = new StringBuffer(" VALUES ( ");
	private Properties properties;
	
	@Override
	public String queryString() throws IllegalArgumentException {
		super.queryString();
		return pqlBuffer.toString() + paramBuffer.toString() + valueBuffer.toString();
	}
	
	@Override
	public void setTableName(String tableName) {
		super.setTableName(tableName);
		pqlBuffer.append(getTableName());
	}
	
	public void setProperties(List<Property> props) throws IllegalArgumentException{
		if(props == null || props.size() == 0){
			throw new IllegalArgumentException("In Properties can't be null or zero.");
		}
		this.properties = new Properties();
		int count = 0;
		for (Property prop : props) {
			if(prop.getKey().trim().equals("")){ continue; }
			this.properties.add(prop);
			if(count != 0){ paramBuffer.append(", "); valueBuffer.append(", "); }
			paramBuffer.append( prop.getKey() );
			Property val = prop;
			if(val.getValue() != null && val.getType() != null){
				if(val.getType() == DataType.BOOL 
    					|| val.getType() == DataType.INT
    					|| val.getType() == DataType.DOUBLE
    					|| val.getType() == DataType.FLOAT) {
					valueBuffer.append(val.getValue().toString());
				}else{
					valueBuffer.append("'"+val.getValue().toString()+"'");
				}
			}else{
				valueBuffer.append(MARKER);
			}
			if(count == (props.size() - 1)){ paramBuffer.append(")"); valueBuffer.append(")"); }
			count++;
		}
	}
	
	public static String createInsertQuery(String tableName, Properties properties){
		
		//Checking Illegal Arguments
		try{
			if(tableName == null || tableName.trim().equals("")){
				throw new IllegalArgumentException("Parameter 'tableName' must not be Null OR Empty.");
			}
			if(isAllParamEmpty(properties.getProperties().toArray())){
				throw new IllegalArgumentException("All Empty Parameters!!! You nuts (:D");
			}
		}catch(IllegalArgumentException iex){
			throw iex;
		}
		
		StringBuffer pqlBuffer = new StringBuffer("INSERT INTO " + tableName + " ( " );
		StringBuffer valueBuffer = new StringBuffer(" VALUES ( ");
		
		if(properties != null && properties.size() > 0){
			
			int count = 0;
			for( Entry<String,Property> ent : properties.keyValueMap().entrySet()){
				
				if(ent.getKey().trim().equals("")){
					continue;
				}
				
				if(count != 0){
					pqlBuffer.append(", ");
					valueBuffer.append(", ");
				}
				
				pqlBuffer.append( ent.getKey() );
				
				Property val = ent.getValue();
				if(val.getValue() != null && val.getType() != null){
					if(val.getType() == DataType.BOOL 
	    					|| val.getType() == DataType.INT
	    					|| val.getType() == DataType.DOUBLE
	    					|| val.getType() == DataType.FLOAT) {
						valueBuffer.append(val.getValue().toString());
					}else{
						valueBuffer.append("'"+val.getValue().toString()+"'");
					}
				}else{
					valueBuffer.append(MARKER);
				}
				
				if(count == (properties.size() - 1)){
					pqlBuffer.append(") ");
					valueBuffer.append(")");
				}
				count++;
			}
		}
		
		return pqlBuffer.toString() + valueBuffer.toString();
	}

	public Properties getProperties() {
		return properties;
	}
	
}