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
	public Compare[] resolveCompares() {
		List<Compare> comps = new ArrayList<Compare>();
		comps.addAll(Arrays.asList(lhr.resolveCompares()));
		return comps.toArray(new Compare[0]);
	}
}
