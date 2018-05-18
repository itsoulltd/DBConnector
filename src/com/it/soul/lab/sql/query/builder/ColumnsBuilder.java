package com.it.soul.lab.sql.query.builder;



public interface ColumnsBuilder extends QueryBuilder{
	public TableBuilder columns(String... name);
	public InsertBuilder into(String name);
	public WhereClauseBuilder rowsFrom(String name);
}