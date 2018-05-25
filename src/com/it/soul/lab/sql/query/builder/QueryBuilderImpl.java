package com.it.soul.lab.sql.query.builder;

import java.util.Arrays;
import java.util.List;

import com.it.soul.lab.sql.query.SQLCountQuery;
import com.it.soul.lab.sql.query.SQLDeleteQuery;
import com.it.soul.lab.sql.query.SQLDistinctQuery;
import com.it.soul.lab.sql.query.SQLInsertQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLQuery.Logic;
import com.it.soul.lab.sql.query.SQLQuery.QueryType;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.SQLUpdateQuery;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.LogicExpression;
import com.it.soul.lab.sql.query.models.Property;

public class QueryBuilderImpl implements ColumnsBuilder, TableBuilder, WhereClauseBuilder, InsertBuilder, ScalerClauseBuilder{

	protected QueryType tempType = QueryType.Select;
	protected SQLQuery tempQuery;

	public QueryBuilderImpl(){
		tempQuery = factory(tempType);
	}

	public SQLQuery build(){
		return tempQuery;
	}

	protected SQLQuery factory(QueryType type){
		SQLQuery temp = null;
		switch (type) {
		case Count:
			temp = new SQLCountQuery();
			break;
		case Distinct:
			temp = new SQLDistinctQuery();
			break;
		case Delete:
			temp = new SQLDeleteQuery();
			break;
		case Insert:
			temp = new SQLInsertQuery();
			break;
		case Update:
			temp = new SQLUpdateQuery();
			break;
		default:
			temp = new SQLSelectQuery();
			break;
		}
		return temp;
	}

	public WhereClauseBuilder from(String name){
		tempQuery.setTableName(name);
		return this;
	}
	public TableBuilder columns(String... name){
		tempQuery.setColumns(name);
		return this;
	}
	public QueryBuilder whereParams(Logic logic, String... name){
		if (logic != null){tempQuery.setLogic(logic);}
		tempQuery.setWhereParams(name);
		return this;
	}
	public QueryBuilder whereParams(Logic logic, Expression... comps){
		if (logic != null){tempQuery.setLogic(logic);}
		List<Expression> items = Arrays.asList(comps);
		tempQuery.setWhereCompareParams(items);
		return this;
	}
	@Override
	public ScalerClauseBuilder on(String name) {
		if(tempQuery instanceof SQLCountQuery){
			((SQLCountQuery)tempQuery).setTableName(name);
		}
		return this;
	}
	@Override
	public QueryBuilder countClause(Property prop, Expression comps) {
		if(tempQuery instanceof SQLCountQuery){
			((SQLCountQuery)tempQuery).setCountClouse(prop, comps);
		}
		return this;
	}
	@Override
	public QueryBuilder distinctClause(Property prop, Expression comps) {
		if(tempQuery instanceof SQLDistinctQuery){
			((SQLDistinctQuery)tempQuery).setCountClouse(prop, comps);
		}
		return this;
	}
	@Override
	public InsertBuilder into(String name) {
		if(tempQuery instanceof SQLInsertQuery){
			((SQLInsertQuery)tempQuery).setTableName(name);
		}
		return this;
	}
	@Override
	public QueryBuilder values(Property... properties) {
		if(tempQuery instanceof SQLInsertQuery){
			try{
				((SQLInsertQuery)tempQuery).setProperties(Arrays.asList(properties));
			}catch(IllegalArgumentException are){
				System.out.println(are.getMessage());
			}
		}
		return this;
	}
	@Override
	public WhereClauseBuilder rowsFrom(String name) {
		if(tempQuery instanceof SQLDeleteQuery){
			((SQLDeleteQuery)tempQuery).setTableName(name);
		}
		return this;
	}
	@Override
	public TableBuilder set(Property... properties) {
		if (tempQuery instanceof SQLUpdateQuery){
			try{
				((SQLUpdateQuery)tempQuery).setProperties(Arrays.asList(properties));
			}catch(IllegalArgumentException are){
				System.out.println(are.getMessage());
			}
		}
		return this;
	}
	@Override
	public QueryBuilder where(LogicExpression expression) {
		tempQuery.setWhereExpression(expression); 
		return this;
	}
}
