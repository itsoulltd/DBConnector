package com.it.soul.lab.sql.query.models;

public interface WhereClause {
	public Predicate isEqualTo(Object value, DataType type);
	public Predicate notEqualTo(Object value, DataType type);
	public Predicate greaterThen(Object value, DataType type);
	public Predicate greaterThenOrEqual(Object value, DataType type);
	public Predicate lessThen(Object value, DataType type);
	public Predicate lessThenOrEqual(Object value, DataType type);
	public Predicate in(Object value, DataType type);
	public Predicate notIn(Object value, DataType type);
	public Predicate like(Object value, DataType type);
	public Predicate notLike(Object value, DataType type);
}
