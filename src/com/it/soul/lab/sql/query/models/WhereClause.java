package com.it.soul.lab.sql.query.models;

public interface WhereClause {
	public Predicate isEqualTo(Object value, DataType type);
	public Predicate notEqualTo(Object value, DataType type);
	public Predicate isGreaterThen(Object value, DataType type);
	public Predicate isGreaterThenOrEqual(Object value, DataType type);
	public Predicate isLessThen(Object value, DataType type);
	public Predicate isLessThenOrEqual(Object value, DataType type);
	public Predicate isIn(Object value, DataType type);
	public Predicate notIn(Object value, DataType type);
	public Predicate isLike(Object value, DataType type);
	public Predicate notLike(Object value, DataType type);
}
