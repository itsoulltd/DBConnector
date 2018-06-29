package com.it.soul.lab.unit.test;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.it.soul.lab.connect.JDBConnection;
import com.it.soul.lab.connect.JDBConnection.DriverClass;
import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.ExpressionInterpreter;
import com.it.soul.lab.sql.query.models.Operator;
import com.it.soul.lab.sql.query.models.Property;

public class PersonTest {
	
	SQLExecutor exe;
	String[] names = new String[]{"Sohana","Towhid","Tanvir","Sumaiya","Tusin"};
	Integer[] ages = new Integer[] {15, 18, 28, 26, 32, 34, 25, 67};
	
	@Before
	public void before(){
		
		try {
			Connection conn = new JDBConnection.Builder("jdbc:mysql://localhost:3306/testDB")
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
	
	private String getRandomName() {
		Random rand = new Random();
		int index = rand.nextInt(names.length);
		return names[index];
	}
	
	private Integer getRandomAge() {
		Random rand = new Random();
		int index = rand.nextInt(ages.length);
		return ages[index];
	}
	
	@After
	public void after(){
		exe.close();
	}

	@Test
	public void testUpdate() {
		Person person = new Person();
		person.setUuid(UUID.randomUUID().toString());
		person.setName(getRandomName());
		try {
			Boolean res = person.insert(exe);
			Assert.assertTrue("Inserted", res);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		person.setAge(getRandomAge());
		person.setIsActive(false);
		person.setSalary(200.00);
		person.setDob(new Date(0));
		try {
			Boolean res = person.update(exe, "age","isActive","salary","dob");
			Assert.assertTrue("Updated", res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInsert() {
		Person person = new Person();
		person.setUuid(UUID.randomUUID().toString());
		person.setName(getRandomName());
		person.setAge(getRandomAge());
		person.setIsActive(true);
		person.setSalary(89200.00);
		person.setDob(new Date(0));
		try {
			Boolean res = person.insert(exe);
			Assert.assertTrue("Inserted", res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDelete() {
		Person person = new Person();
		person.setUuid(UUID.randomUUID().toString());
		person.setName(getRandomName());
		try {
			Boolean res = person.insert(exe);
			Assert.assertTrue("Inserted", res);
			Boolean del = person.delete(exe);
			Assert.assertTrue("Deleted", del);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testReadClassOfTSQLExecutorPropertyArray() {
		try {
			List<Person> sons = Person.read(Person.class, exe, new Property("name", "Sohana"));
			Assert.assertTrue("Count is there", sons.size() > 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testReadClassOfTSQLExecutorExpressionInterpreter() {
		try {
			ExpressionInterpreter exp = new Expression(new Property("name", "Towhid"), Operator.EQUAL);
			List<Person> sons = Person.read(Person.class, exe, exp);
			Assert.assertTrue("Count is there", sons.size() > 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
