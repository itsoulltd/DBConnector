package com.it.soul.lab.jpql.query;

import java.util.List;

import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.models.Compare;
import com.it.soul.lab.sql.query.models.LogicExpression;

public class JPQLSelectQuery extends SQLSelectQuery {

	@Override
	public String queryString() throws IllegalArgumentException {
		if(getTableName() == null || getTableName().trim().equals("")){
			throw new IllegalArgumentException("Parameter Table must not be Null OR Empty.");
		}
		return pqlBuffer.toString();
	}
	
	@Override
	protected void prepareTableName(String name) {
		super.prepareTableName(name);
	}
	
	@Override
	protected void prepareColumns(String[] columns) {
		if(getColumns() != null && getColumns().length > 0){

			int count = 0;
			for(String str : getColumns()){
				if(str.trim().equals("")){continue;}
				if(count++ != 0){pqlBuffer.append(", ");}
				pqlBuffer.append( QUIENTIFIER + "." + str);
			}
			//If all passed parameter is empty
			if(count == 0){pqlBuffer.append(QUIENTIFIER);}
		}else{
			pqlBuffer.append(QUIENTIFIER);
		}
	}
	
	@Override
	protected void prepareWhereParams(String[] whereParams) {
		prepareWhereParams(Compare.createListFrom(whereParams, ComparisonType.IsEqual));
	}
	
	@Override
	protected void prepareWhereParams(List<Compare> whereParams) {
		if (whereParams != null 
				&& whereParams.size() > 0
				&& !isAllParamEmpty(whereParams.toArray())){
			
			if(pqlBuffer.length() > 0){
				pqlBuffer.append(" WHERE ");
				int count = 0;
				for( Compare param : whereParams ){
					if(param.getProperty().trim().equals("")){continue;}
					if(count++ != 0){pqlBuffer.append( " " + getLogic().name() + " ");}
					pqlBuffer.append(QUIENTIFIER + "." + param.getProperty()+ " " + param.getType().toString() + " :" + param.getProperty());
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
		pqlBuffer.append(" WHERE " + whereExpression.express());
		for (Compare comp : resolved) {
			comp.setQuientifier(' ');
		}
	}
	
}
