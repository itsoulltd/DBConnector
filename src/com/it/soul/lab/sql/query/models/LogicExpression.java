package com.it.soul.lab.sql.query.models;

public interface LogicExpression {
	public String express();
	public Compare[] resolveCompares();
}
