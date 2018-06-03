package com.it.soul.lab.sql.query.builder;

import com.it.soul.lab.sql.query.models.Logic;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.ExpressionInterpreter;

public interface WhereClauseBuilder extends OrderByBuilder{
	public OrderByBuilder whereParams(Logic logic, String... name);
	public OrderByBuilder whereParams(Logic logic, Expression... comps);
	public OrderByBuilder where(ExpressionInterpreter expression);
}