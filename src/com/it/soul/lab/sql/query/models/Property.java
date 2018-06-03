package com.it.soul.lab.sql.query.models;

import java.text.SimpleDateFormat;
import com.it.soul.lab.sql.query.models.DataType;

public class Property {
	
	public Property() {
		super();
	}

	private static final String SQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private String key = null;
	private DataType type = null;
	private Object value = null;
	
	private Property(Object value, DataType type){
		this.value = value;
		this.type = type;
		if (value != null){
			//TODO: Regx type value validation is required.
			if(value instanceof java.util.Date || value instanceof java.sql.Date){
				value = getFormattedDateString(value);
				this.type = DataType.STRING;
			}
		}
	}
	
	public Property(String key, Object value, DataType type){
		this(value,type);
		this.key = key;
	}
	
	public Property(String key){
		this(key, null, DataType.OBJECT);
	}
	
	public Property(String key, String value){
		this(key, value, DataType.STRING);
	}
	
	public Property(Property prop) {
		this(prop.getKey(), prop.getValue(), prop.getType());
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof Property){
			boolean isSame = false;
			Property compareble = (Property)obj;
			if(this.getKey() == compareble.getKey()){
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
	public String getKey() {
		return key;
	}
	public void setKey(String property) {
		this.key = property;
	}
	public DataType getType() {
		return type;
	}
	public void setType(DataType type) {
		this.type = type;
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
}
