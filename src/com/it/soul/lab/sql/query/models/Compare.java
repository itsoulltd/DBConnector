package com.it.soul.lab.sql.query.models;

import java.util.ArrayList;
import java.util.List;

import com.it.soul.lab.sql.query.SQLQuery.ComparisonType;
import com.it.soul.lab.sql.query.SQLQuery.DataType;


public class Compare implements LogicExpression{
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
	public Compare setPropertyValue(Object value, DataType type){
		this.valueProperty.setValue(value);
		this.valueProperty.setType(type);
		return this;
	}
	public Property getValueProperty() {
		return valueProperty;
	}
	public Compare setQuientifier(char quientifier){
		this.quientifier = quientifier;
		return this;
	}
	public Compare setMarker(String marker){
		this.expressMarker = marker;
		return this;
	}

	protected static final char MARKER = '?';
	private String property;
	private ComparisonType type;
	private Property valueProperty;
	private char quientifier = ' '; //Default is empty space
	private String expressMarker = String.valueOf(MARKER);
	
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
	public String toString(){
		if (Character.isWhitespace(quientifier) == false) {return  quientifier+ "." + getProperty() + " " + type.toString() + " " + getPropertyValue(valueProperty);}
		else {return getProperty() + " " + type.toString() + " " + getPropertyValue(valueProperty);}
	}
	private String getPropertyValue(Property val){
		if(val.getValue() != null && val.getType() != null){
			if(val.getType() == DataType.BOOL 
					|| val.getType() == DataType.INT
					|| val.getType() == DataType.DOUBLE
					|| val.getType() == DataType.FLOAT) {
				return val.getValue().toString();
			}else{
				return "'"+val.getValue().toString()+"'";
			}
		}else{
			return  String.valueOf(MARKER);
		}
	}
	@Override
	public String express() {
		if (Character.isWhitespace(quientifier) == false) {return quientifier+ "." + getProperty() + " " + type.toString() + " " + expressMarker;}
		else {return getProperty() + " " + type.toString() + " " + MARKER;}
	}
	@Override
	public Compare[] resolveCompares() {
		return new Compare[] {this};
	}
}
