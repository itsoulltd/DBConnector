package com.it.soul.lab.sql.query.models;

import java.util.ArrayList;
import java.util.List;

public class Table {
	public Table() {
		super();
	}

	private List<Row> rows = new ArrayList<Row>();

	public List<Row> getRows() {
		return rows;
	}

	public void setRows(List<Row> items) {
		if (items == null) {return;}
		this.rows = items;
	}
	public Table add(Row list){
		if (rows.contains(list)){
			return this;
		}
		rows.add(list);
		return this;
	}
}
