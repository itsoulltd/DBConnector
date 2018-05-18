package com.it.soul.lab.sql.query.builder;

import com.it.soul.lab.sql.query.SQLQuery.Logic;
import com.it.soul.lab.sql.query.models.Compare;

public interface WhereClauseBuilder extends QueryBuilder{
	public QueryBuilder whereParams(Logic logic, String... name);
	public QueryBuilder whereParams(Logic logic, Compare... comps);
}