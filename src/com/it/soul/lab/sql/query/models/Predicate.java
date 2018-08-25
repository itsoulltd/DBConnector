package com.it.soul.lab.sql.query.models;

public interface Predicate extends ExpressionInterpreter {
	public Predicate and(ExpressionInterpreter exp);
	public Predicate or(ExpressionInterpreter exp);
	public Predicate not();
	public Predicate and(String key, Object value, Operator opt);
	public Predicate or(String key, Object value, Operator opt);
}
