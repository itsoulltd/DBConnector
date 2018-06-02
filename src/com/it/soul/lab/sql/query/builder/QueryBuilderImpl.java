package com.it.soul.lab.sql.query.builder;

import java.util.Arrays;
import java.util.List;

import com.it.soul.lab.sql.query.SQLDeleteQuery;
import com.it.soul.lab.sql.query.SQLInsertQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLQuery.Logic;
import com.it.soul.lab.sql.query.SQLQuery.Operator;
import com.it.soul.lab.sql.query.SQLQuery.QueryType;
import com.it.soul.lab.sql.query.SQLScalerQuery;
import com.it.soul.lab.sql.query.SQLScalerQuery.ScalerType;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.SQLUpdateQuery;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.ExpressionInterpreter;
import com.it.soul.lab.sql.query.models.Property;

public class QueryBuilderImpl implements ColumnsBuilder, TableBuilder
, WhereClauseBuilder, InsertBuilder, ScalerClauseBuilder, OrderByBuilder{

	protected QueryType tempType = QueryType.SELECT;
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
		case COUNT:
			temp = new SQLScalerQuery(ScalerType.COUNT);
			break;
		case DISTINCT:
			temp = new SQLScalerQuery(ScalerType.DISTINCT);
			break;
		case DELETE:
			temp = new SQLDeleteQuery();
			break;
		case INSERT:
			temp = new SQLInsertQuery();
			break;
		case UPDATE:
			temp = new SQLUpdateQuery();
			break;
		case MAX:
			temp = new SQLScalerQuery(ScalerType.MAX);
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
	public OrderByBuilder whereParams(Logic logic, String... name){
		if (logic != null){tempQuery.setLogic(logic);}
		tempQuery.setWhereParams(name);
		return this;
	}
	public OrderByBuilder whereParams(Logic logic, Expression... comps){
		if (logic != null){tempQuery.setLogic(logic);}
		List<Expression> items = Arrays.asList(comps);
		tempQuery.setWhereCompareParams(items);
		return this;
	}
	@Override
	public ScalerClauseBuilder on(String name) {
		if(tempQuery instanceof SQLScalerQuery){
			((SQLScalerQuery)tempQuery).setTableName(name);
		}
		return this;
	}
	@Override
	public QueryBuilder scalerClause(Property prop, Expression comps) {
		if(tempQuery instanceof SQLScalerQuery){
			((SQLScalerQuery)tempQuery).setScalerClouse(prop, comps);
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
	public OrderByBuilder where(ExpressionInterpreter expression) {
		tempQuery.setWhereExpression(expression); 
		return this;
	}
	@Override
	public QueryBuilder addLimit(Integer limit, Integer offset) {
		if(tempQuery instanceof SQLSelectQuery) {
			((SQLSelectQuery)tempQuery).setLimit(limit, offset);
		}
		return this;
	}
	@Override
	public LimitBuilder orderBy(String... columns) {
		if(tempQuery instanceof SQLSelectQuery) {
			((SQLSelectQuery)tempQuery).setOrderBy(Arrays.asList(columns), Operator.ASC);
		}
		return this;
	}
}
