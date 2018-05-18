package com.it.soul.lab.sql.query.models;

import com.it.soul.lab.sql.query.SQLQuery.ComparisonType;


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
