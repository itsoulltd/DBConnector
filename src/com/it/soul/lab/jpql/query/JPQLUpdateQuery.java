package com.it.soul.lab.jpql.query;

import java.util.List;

import com.it.soul.lab.sql.query.SQLUpdateQuery;
import com.it.soul.lab.sql.query.models.Compare;
import com.it.soul.lab.sql.query.models.LogicExpression;

public class JPQLUpdateQuery extends SQLUpdateQuery {
	
	@Override
	protected void prepareTableName(String name) {
		pqlBuffer.append(getTableName() + " " + QUIENTIFIER + " SET");
	}
	
	@Override
	protected void prepareColumns(String[] columns) {
		if(getColumns() != null && getColumns().length > 0){
			int count = 0;
			for(String column : getColumns()){
				if(column.trim().equals("")){continue;}
				if(count++ != 0){paramBuffer.append(", ");}
				paramBuffer.append( QUIENTIFIER + "." + column + " = :" + column);
			}
		}
	}
	
	@Override
	protected void prepareWhereParams(String[] whereParams) {
		prepareWhereParams(Compare.createListFrom(whereParams, ComparisonType.IsEqual));
	}
	
	@Override
	protected void prepareWhereParams(List<Compare> whereParams) {
		if(whereParams != null 
				&& whereParams.size() > 0
				&& !isAllParamEmpty(whereParams.toArray())) {
			
			if(whereBuffer.length() > 0){
				whereBuffer.append("WHERE ");
				int count = 0;
				for(Compare param : whereParams){
					if(param.getProperty().trim().equals("")){continue;}
					if(count++ != 0){whereBuffer.append( " " + getLogic().name() + " ");}
					whereBuffer.append( QUIENTIFIER + "." + param.getProperty() + " " + param.getType().toString() + " " + ":" + param.getProperty());
				}
			}
		}
	}
	
	@Override
	protected void prepareWhereExpression(LogicExpression whereExpression) {
		Compare[] resolved = whereExpression.resolveCompares();
		for (Compare comp : resolved) {
			comp.setQuientifier(QUIENTIFIER).setMarker(":"+comp.getProperty());
		}
		whereBuffer.append(" WHERE " + whereExpression.express());
		for (Compare comp : resolved) {
			comp.setQuientifier(' ');
		}
	}
}
