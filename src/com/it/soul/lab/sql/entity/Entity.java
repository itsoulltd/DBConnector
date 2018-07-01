package com.it.soul.lab.sql.entity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.SQLDeleteQuery;
import com.it.soul.lab.sql.query.SQLInsertQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.SQLUpdateQuery;
import com.it.soul.lab.sql.query.SQLQuery.QueryType;
import com.it.soul.lab.sql.query.models.AndExpression;
import com.it.soul.lab.sql.query.models.DataType;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.ExpressionInterpreter;
import com.it.soul.lab.sql.query.models.Operator;
import com.it.soul.lab.sql.query.models.Property;
import com.it.soul.lab.sql.query.models.Table;

public abstract class Entity implements EntityInterface{
	public Entity() {
		super();
	}
	protected List<Property> getProperties() {
		List<Property> result = new ArrayList<>();
		for (Field field : this.getClass().getDeclaredFields()) {
			Property prop = getProperty(field.getName());
			if(prop == null) {continue;}
			result.add(prop);
		}
		return result;
	}
	protected Property getProperty(String key) {
		Property result = null;
		try {
			Field field = this.getClass().getDeclaredField(key);
			field.setAccessible(true);
			String name = field.getName();
			Object value = field.get(this);
			DataType type = getDataType(value);
			result = new Property(name, value, type);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return result;
	}
	private DataType getDataType(Object value) {
		if(value instanceof Integer) {
			return DataType.INT;
		}else if(value instanceof Double) {
			return DataType.DOUBLE;
		}else if(value instanceof Float) {
			return DataType.FLOAT;
		}else if(value instanceof Boolean) {
			return DataType.BOOL;
		}else if(value instanceof String) {
			return DataType.STRING;
		}else if(value instanceof Date 
				|| value instanceof Timestamp 
				|| value instanceof java.util.Date) {
			return DataType.SQLDATE;
		}else if(value instanceof Blob) {
			return DataType.BLOB;
		}else if(value instanceof Integer) {
			return DataType.INT;
		}else if(value instanceof Byte[]) {
			return DataType.BYTEARRAY;
		}else {
			return DataType.OBJECT;
		}
	}
	private String tableName() {
		if(this.getClass().isAnnotationPresent(TableName.class) == false) {
			return this.getClass().getName();
		}
		Annotation annotation = this.getClass().getAnnotation(TableName.class);
		TableName tableName = (TableName) annotation;
		String name = (tableName.value().trim().length() == 0) ? this.getClass().getName() : tableName.value().trim();
		return name;
	}
	private boolean isAutoIncrement() {
		if(this.getClass().isAnnotationPresent(TableName.class) == false) {
			return false;
		}
		Annotation annotation = this.getClass().getAnnotation(TableName.class);
		TableName tableName = (TableName) annotation;
		return tableName.autoIncrement();
	}
	private PrimaryKey getPrimaryKey() {
		PrimaryKey key = null;
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field field : fields) {
			if(field.isAnnotationPresent(PrimaryKey.class)) {
				key = field.getAnnotation(PrimaryKey.class);
				break;
			}
		}
		return key;
	}
	private Property getPrimaryProperty() {
		String key = getPrimaryKey().value().trim();
		Property prop = getProperty(key);
		return prop;
	}
	public Boolean update(SQLExecutor exe, String...keys) throws SQLException, Exception {
		List<Property> properties = new ArrayList<>();
		if(keys.length > 0) {
			for (String key : keys) {
				String skey = key.trim();
				Property prop = getProperty(skey);
				if (prop == null) {continue;}
				properties.add(prop);
			}
		}else {
			properties = getProperties();
		}
		String tableName = tableName();
		SQLUpdateQuery query = (SQLUpdateQuery) new SQLQuery.Builder(QueryType.UPDATE)
														.set(properties.toArray(new Property[0]))
														.from(tableName)
														.where(updateWhereExpression()).build();
		int isUpdate = exe.executeUpdate(query);
		return isUpdate == 1;
	}
	protected ExpressionInterpreter updateWhereExpression() {
		return new Expression(getPrimaryProperty(), Operator.EQUAL);
	}
	@Override
	public Boolean insert(SQLExecutor exe, String... keys) throws SQLException, Exception {
		List<Property> properties = new ArrayList<>();
		if(keys.length > 0) {
			for (String key : keys) {
				String skey = key.trim();
				Property prop = getProperty(skey);
				if (prop == null) {continue;}
				properties.add(prop);
			}
		}else {
			properties = getProperties();
		}
		SQLInsertQuery query = (SQLInsertQuery) new SQLQuery.Builder(QueryType.INSERT)
															.into(tableName())
															.values(properties.toArray(new Property[0])).build();
		
		int insert = exe.executeInsert(isAutoIncrement(), query);
		return insert == 1;
	}
	@Override
	public Boolean delete(SQLExecutor exe) throws SQLException, Exception {
		Expression exp = new Expression(getPrimaryProperty(), Operator.EQUAL);
		SQLDeleteQuery query = (SQLDeleteQuery) new SQLQuery.Builder(QueryType.DELETE)
														.rowsFrom(tableName())
														.where(exp).build();
		int deletedId = exe.executeDelete(query);
		return deletedId == 1;
	}
	public static <T extends Entity> List<T> read(Class<T>  type, SQLExecutor exe, Property...match) throws SQLException, Exception{
		ExpressionInterpreter and = null;
		ExpressionInterpreter lhr = null;
		for (int i = 0; i < match.length; i++) {
			if(lhr == null) {
				lhr = new Expression(match[i], Operator.EQUAL);
				and = lhr;
			}else {
				ExpressionInterpreter rhr = new Expression(match[i], Operator.EQUAL);
				and = new AndExpression(lhr, rhr);
				lhr = and;
			}
		}
		return T.read(type, exe, and);
	}
	public static <T extends Entity> List<T> read(Class<T>  type, SQLExecutor exe, ExpressionInterpreter expression) throws SQLException, Exception{
		TableName annotation = (TableName) type.getAnnotation(TableName.class);
		String name = (annotation.value().trim().length() == 0) ? type.getName() : annotation.value().trim();
		SQLSelectQuery query = null;
		if(expression != null) {
			query = (SQLSelectQuery) new SQLQuery.Builder(QueryType.SELECT)
					.columns()
					.from(name)
					.where(expression).build();
		}else {
			query = (SQLSelectQuery) new SQLQuery.Builder(QueryType.SELECT)
					.columns()
					.from(name).build();
		}
		ResultSet set = exe.executeSelect(query);
		Table table = exe.collection(set);
		return table.inflate(type);
	}
}
