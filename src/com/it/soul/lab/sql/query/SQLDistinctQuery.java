package com.it.soul.lab.sql.query;


public class SQLDistinctQuery extends SQLCountQuery{
	@Override
	protected void prepareColumnsBuffers() {
		if(getColumns() != null && getColumns().length > 0){
			String firstParam = getColumns()[0];
			pqlBuffer.append(DISTINCT_FUNC+"(" + firstParam + ")");
		}else{
			pqlBuffer.append(DISTINCT_FUNC+"(" + "*" + ")");
		}
	}
}