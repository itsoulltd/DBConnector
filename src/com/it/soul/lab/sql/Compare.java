package com.it.soul.lab.sql;

import com.it.soul.lab.util.EnumDefinitions.ComparisonType;

public class Compare {
	public Compare(String property, ComparisonType type){
		this.property = property;
		this.type = type;
	}
	public String getProperty() {
		return property;
	}
	public ComparisonType getType() {
		return type;
	}
	private String property;
	private ComparisonType type;
}
