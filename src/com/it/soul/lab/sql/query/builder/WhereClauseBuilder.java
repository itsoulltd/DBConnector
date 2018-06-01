package com.it.soul.lab.sql.query.builder;

import com.it.soul.lab.sql.query.SQLQuery.Logic;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.LogicExpression;

public interface WhereClauseBuilder extends LimitBuilder{
	public OrderByBuilder whereParams(Logic logic, String... name);
	public OrderByBuilder whereParams(Logic logic, Expression... comps);
	public OrderByBuilder where(LogicExpression expression);
}