package com.it.soul.lab.jpql.query;

import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.builder.QueryBuilderImpl;

public class JPQLQuery extends SQLQuery{

public static class Builder extends QueryBuilderImpl{
		
		public Builder(QueryType type){
			tempType = type;
			tempQuery = factory(tempType);
		}
		
		public SQLQuery build(){
			//SQLQuery query = factory(tempType);
			//TODO: Build up a new Object from existing one.
			return tempQuery;
		}
		
		protected SQLQuery factory(QueryType type){
			SQLQuery temp = null;
			switch (type) {
			case Select:
				temp = new JPQLSelectQuery();
				break;
			case Update:
				temp = new JPQLUpdateQuery();
				break;
			default:
				temp = super.factory(type);
				break;
			}
			return temp;
		}
		
	}
	
}
