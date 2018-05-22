package com.it.soul.lab.sql.query.builder;

import com.it.soul.lab.sql.query.models.Compare;
import com.it.soul.lab.sql.query.models.Property;

public interface ScalerClauseBuilder extends WhereClauseBuilder{
	public QueryBuilder countClause(Property prop, Compare comps);
	public QueryBuilder distinctClause(Property prop, Compare comps);
}