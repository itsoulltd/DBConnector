package com.it.soul.lab.sql.query.models;

public class OrExpression extends AndExpression {

	public OrExpression(LogicExpression lhr, LogicExpression rhr) {
		super(lhr, rhr);
	}

	@Override
	public String express() {
		return "( " + lhr.express() + " OR " + rhr.express() + " )";
	}

}
