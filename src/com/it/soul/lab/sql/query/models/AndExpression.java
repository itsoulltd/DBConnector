package com.it.soul.lab.sql.query.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AndExpression implements LogicExpression {

	protected LogicExpression lhr;
	protected LogicExpression rhr;
	
	public AndExpression(LogicExpression lhr, LogicExpression rhr) {
		this.lhr = lhr;
		this.rhr = rhr;
	}

	@Override
	public String express() {
		return "( " + lhr.express() + " AND " + rhr.express() + " )";
	}
	
	@Override
	public String toString() {
		return express();
	}
	
	@Override
	public Expression[] resolveCompares() {
		List<Expression> comps = new ArrayList<Expression>();
		comps.addAll(Arrays.asList(lhr.resolveCompares()));
		comps.addAll(Arrays.asList(rhr.resolveCompares()));
		return comps.toArray(new Expression[0]);
	}

}
