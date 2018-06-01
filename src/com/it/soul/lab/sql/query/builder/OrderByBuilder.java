package com.it.soul.lab.sql.query.builder;

public interface OrderByBuilder extends QueryBuilder {
	public LimitBuilder orderBy(String...columns);
}
