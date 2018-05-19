package com.it.soul.lab.sql.query.models;

import java.util.ArrayList;
import java.util.List;

import com.it.soul.lab.sql.query.SQLQuery.ComparisonType;
import com.it.soul.lab.sql.query.SQLQuery.DataType;


public class Compare {
	public Compare(String property, ComparisonType type){
		this.property = property;
		this.type = type;
		this.valueProperty = new Property(property);
	}
	public String getProperty() {
		return property;
	}
	public ComparisonType getType() {
		return type;
	}
	public void setPropertyValue(Object value, DataType type){
		this.valueProperty.setValue(value);
		this.valueProperty.setType(type);
	}
	public Property getValueProperty() {
		return valueProperty;
	}

	private String property;
	private ComparisonType type;
	private Property valueProperty;
	
	public static List<Compare> createListFrom(String[] names, ComparisonType type){
		List<Compare> resutls = new ArrayList<Compare>();
		for (String name : names) {
			resutls.add(new Compare(name, type));
		}
		return resutls;
	}
	
	public static Properties convertToProperties(List<Compare> coms){
		Properties props = new Properties();
		for (Compare compare : coms) {
			props.add(compare.getValueProperty());
		}
		return props;
	}
}
