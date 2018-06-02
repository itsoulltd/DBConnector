package com.it.soul.lab.sql.query.models;

import java.util.ArrayList;
import java.util.List;

public class PropertyCollection {
	public PropertyCollection() {
		super();
	}

	private List<PropertyList> items = new ArrayList<PropertyList>();

	public List<PropertyList> getItems() {
		return items;
	}

	public void setItems(List<PropertyList> items) {
		if (items == null) {return;}
		this.items = items;
	}
	public PropertyCollection add(PropertyList list){
		if (items.contains(list)){
			return this;
		}
		items.add(list);
		return this;
	}
}
