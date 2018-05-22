package com.it.soul.lab.sql.query.builder;

import com.it.soul.lab.sql.query.SQLQuery.Logic;
import com.it.soul.lab.sql.query.models.Compare;
import com.it.soul.lab.sql.query.models.LogicExpression;

public interface WhereClauseBuilder extends QueryBuilder{
	public QueryBuilder whereParams(Logic logic, String... name);
	public QueryBuilder whereParams(Logic logic, Compare... comps);
	public QueryBuilder whereExpression(LogicExpression expression);
}