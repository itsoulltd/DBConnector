package com.it.soul.lab.unit.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.it.soul.lab.connect.JDBConnection;
import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.SQLDeleteQuery;
import com.it.soul.lab.sql.query.SQLInsertQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLQuery.QueryType;
import com.it.soul.lab.sql.query.SQLScalerQuery;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.SQLUpdateQuery;
import com.it.soul.lab.sql.query.models.DataType;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.Operator;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.ScalerType;
import com.it.soul.lab.sql.query.models.Table;

public class QueryExecutionTest {
	
	SQLExecutor exe;
	
	@Before
	public void before(){
		try {
			Connection conn = new JDBConnection.Builder("jdbc:mysql://localhost:3306/testDB")
										.credential("root","towhid@123")
										.build();
			exe = new SQLExecutor(conn);
		} catch (SQLException e) {
			exe.close();
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void after(){
		exe.close();
	}
	
	@Test public void testSelectAll(){
		
		try{
			
			SQLSelectQuery query = (SQLSelectQuery) new SQLQuery.Builder(QueryType.SELECT).columns().from("Passenger").build();
			ResultSet set = exe.executeSelect(query);
			List<Map<String,Object>> x = exe.convertToKeyValuePaire(set);
			exe.displayCollection(x);
			Assert.assertTrue("Select All Successfull", x.size() > 0);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Test
	public void testSelect() {
		
		String str = "{name=sohana, id=2, age=25, sex=female};" + '\n';
		
		Expression compWith = new Expression("name", Operator.EQUAL).setPropertyValue("sohana", DataType.STRING);
		
		SQLSelectQuery qc = (SQLSelectQuery) new SQLQuery.Builder(QueryType.SELECT)
															.columns()
															.from("Passenger")
															.where(compWith)
															.build();
		try {
			ResultSet set = exe.executeSelect(qc);
			List<Map<String,Object>> x = exe.convertToKeyValuePaire(set);
			//Table x = exe.collection(set);
			Assert.assertEquals(exe.toString(x), str);
			
		} catch (IllegalArgumentException | SQLException e) {
			e.printStackTrace();
		} 
	}
	
	@Test public void testInsert(){
		//Insert into
		SQLInsertQuery iQuery2 = (SQLInsertQuery) new SQLQuery.Builder(QueryType.INSERT)
										.into("Passenger")
										.values(new Property("name","tanvir"), new Property("age", 28, DataType.INT), new Property("sex"))
										.build();
		try {
			int autoId = exe.executeInsert(true, iQuery2);
			Assert.assertTrue("New Item Created", autoId > 0);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	@Test public void updateTest(){
		
		try {
			SQLScalerQuery max = (SQLScalerQuery) new SQLQuery.Builder(QueryType.MAX).columns("id").on("Passenger").build();
			int autoId = exe.getScalerValue(max);
			Assert.assertTrue("Get Max value", autoId > 0);
			
			Expression compareWith = new Expression("id", Operator.EQUAL).setPropertyValue(autoId, DataType.INT);

			SQLUpdateQuery upQuery = (SQLUpdateQuery) new SQLQuery.Builder(QueryType.UPDATE)
											.set(new Property("name","tanvir Islam"), new Property("age", 29, DataType.INT))
											.from("Passenger")
											.where(compareWith).build();

			int updateId = exe.executeUpdate(upQuery);
			Assert.assertTrue("Updated Successfull", updateId == 1);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test public void deleteTest(){

		try {
			SQLScalerQuery max = (SQLScalerQuery) new SQLQuery.Builder(QueryType.MAX).columns("id").on("Passenger").build();
			int autoId = exe.getScalerValue(max);
			Assert.assertTrue("Get Max value", autoId > 0);

			Expression compareWith = new Expression("id", Operator.EQUAL).setPropertyValue(autoId, DataType.INT);

			SQLDeleteQuery dquery = (SQLDeleteQuery) new SQLQuery.Builder(QueryType.DELETE)
														.rowsFrom("Passenger")
														.where(compareWith)
														.build();
			
			int deletedId = exe.executeDelete(dquery);
			Assert.assertTrue("Delete Successfull", deletedId == 1);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test public void GroupByHaving() {
		
		try {
			SQLSelectQuery qu12 = (SQLSelectQuery) new SQLQuery.Builder(QueryType.SELECT)
					.columns("name",ScalerType.COUNT.toAlias("age"))
					.from("Passenger")
					.groupBy("name")
					.having(new Expression(ScalerType.COUNT.toString("age"), Operator.GREATER_THAN).setPropertyValue(1, DataType.INT))
					.orderBy(ScalerType.COUNT.toString("age"))
					.build();
			
			ResultSet set = exe.executeSelect(qu12);
			Table x = exe.collection(set);
			exe.displayCollection(x);
			Assert.assertTrue("Only 2 Rows Should returns.", x.getRows().size() > 0);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
