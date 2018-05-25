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

import com.it.soul.lab.connect.DBConnection;
import com.it.soul.lab.connect.DBConnection.DriverClass;
import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.SQLDeleteQuery;
import com.it.soul.lab.sql.query.SQLInsertQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLScalerQuery;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.SQLUpdateQuery;
import com.it.soul.lab.sql.query.SQLQuery.DataType;
import com.it.soul.lab.sql.query.SQLQuery.Operator;
import com.it.soul.lab.sql.query.SQLQuery.QueryType;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.Property;

public class QueryExecutionTest {
	
	SQLExecutor exe;
	
	@Before
	public void before(){
		try {
			Connection conn = new DBConnection.Builder("jdbc:mysql://localhost:3306/testDB")
										.driver(DriverClass.MYSQL)
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
			Assert.assertEquals(exe.toString(x), str);
			
		} catch (IllegalArgumentException | SQLException e) {
			e.printStackTrace();
		} 
	}
	
	@Test public void testInsert(){
		//Insert into
		SQLInsertQuery iQuery2 = (SQLInsertQuery) new SQLQuery.Builder(QueryType.INSERT)
										.into("Passenger")
										.values(new Property("name","tanvir"), new Property("age", 28, DataType.INT), new Property("sex","male"))
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
}
