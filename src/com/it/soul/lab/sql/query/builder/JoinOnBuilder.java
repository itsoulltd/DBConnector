package com.it.soul.lab.sql.query.builder;

import com.it.soul.lab.sql.query.models.JoinExpression;

public interface JoinOnBuilder extends QueryBuilder{
	public JoinBuilder on(JoinExpression expression);
}
