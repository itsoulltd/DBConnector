package com.it.soul.lab.sql.query;


public class SQLDistinctQuery extends SQLCountQuery{
	@Override
	protected void prepareColumns(String[] columns) {
		if(columns != null && columns.length > 0){
			String firstParam = columns[0];
			pqlBuffer.append(DISTINCT_FUNC+"(" + firstParam + ")");
		}else{
			pqlBuffer.append(DISTINCT_FUNC+"(" + "*" + ")");
		}
	}
}