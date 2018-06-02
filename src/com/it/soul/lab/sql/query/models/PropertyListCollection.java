package com.it.soul.lab.sql.query.models;

import java.util.ArrayList;
import java.util.List;

public class PropertyListCollection {
	public PropertyListCollection() {
		super();
	}

	private List<PropertyList> propertyLists = new ArrayList<PropertyList>();

	public List<PropertyList> getPropertyLists() {
		return propertyLists;
	}

	public void setPropertyLists(List<PropertyList> items) {
		if (items == null) {return;}
		this.propertyLists = items;
	}
	public PropertyListCollection add(PropertyList list){
		if (propertyLists.contains(list)){
			return this;
		}
		propertyLists.add(list);
		return this;
	}
}
