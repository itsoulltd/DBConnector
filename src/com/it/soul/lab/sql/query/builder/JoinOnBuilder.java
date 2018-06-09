package com.it.soul.lab.sql.query.builder;

import com.it.soul.lab.sql.query.models.JoinExpression;

public interface JoinOnBuilder {
	public JoinBuilder on(JoinExpression expression);
}
