package com.it.soul.lab.sql.query.builder;

import com.it.soul.lab.sql.query.SQLQuery.Logic;
import com.it.soul.lab.sql.query.models.Compare;
import com.it.soul.lab.sql.query.models.Property;

public interface ScalerClauseBuilder extends WhereClauseBuilder{
	public QueryBuilder countClause(Property prop, Compare comps);
	public QueryBuilder countClause(Logic logic, Compare... comps);
	public QueryBuilder distinctClause(Property prop, Compare comps);
	public QueryBuilder distinctClause(Logic logic, Compare... comps);
}