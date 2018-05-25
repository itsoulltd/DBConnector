package com.it.soul.lab.sql.query.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotExpression implements LogicExpression{

	protected LogicExpression lhr;
	
	public NotExpression(LogicExpression expression) {
		lhr = expression;
	}
	
	@Override
	public String express() {
		return " NOT " + lhr.express();
	}

	@Override
	public Expression[] resolveCompares() {
		List<Expression> comps = new ArrayList<Expression>();
		comps.addAll(Arrays.asList(lhr.resolveCompares()));
		return comps.toArray(new Expression[0]);
	}
}
