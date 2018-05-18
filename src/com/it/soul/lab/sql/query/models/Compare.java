package com.it.soul.lab.sql.query.models;

import java.util.ArrayList;
import java.util.List;

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
	
	public static List<Compare> createListFrom(String[] names, ComparisonType type){
		List<Compare> resutls = new ArrayList<Compare>();
		for (String name : names) {
			resutls.add(new Compare(name, type));
		}
		return resutls;
	}
}
