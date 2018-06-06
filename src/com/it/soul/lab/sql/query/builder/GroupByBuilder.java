package com.it.soul.lab.sql.query.builder;

public interface GroupByBuilder extends OrderByBuilder{
	public OrderByBuilder groupBy(String...columns);
}
