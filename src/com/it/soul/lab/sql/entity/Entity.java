package com.it.soul.lab.sql.entity;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.it.soul.lab.sql.SQLExecutor;
import com.it.soul.lab.sql.query.SQLDeleteQuery;
import com.it.soul.lab.sql.query.SQLInsertQuery;
import com.it.soul.lab.sql.query.SQLQuery;
import com.it.soul.lab.sql.query.SQLQuery.QueryType;
import com.it.soul.lab.sql.query.SQLSelectQuery;
import com.it.soul.lab.sql.query.SQLUpdateQuery;
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
	private boolean hasPropertyAnnotation(Field field) {
		boolean hasPropertyAnno = field.isAnnotationPresent(com.it.soul.lab.sql.entity.Property.class);
		return hasPropertyAnno;
	}
	protected List<Property> getProperties(SQLExecutor exe) {
		List<Property> result = new ArrayList<>();
		boolean acceptAll = shouldAcceptAllProperty();
		for (Field field : this.getClass().getDeclaredFields()) {
			if(acceptAll == false && hasPropertyAnnotation(field) == false) {
				continue;
			}
			Property prop = getProperty(field.getName(), exe);
			if(prop == null) {continue;}
			result.add(prop);
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
		}else if(value instanceof Date || value instanceof java.util.Date) {
			return DataType.SQLDATE;
		}else if(value instanceof Timestamp || value instanceof Time) {
			return DataType.SQLTIMESTAMP;
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
	private java.util.Date parseDate(String val, DataType type, String format){
		try {
			SimpleDateFormat formatter = new SimpleDateFormat((format != null && format.trim().isEmpty() == false) 
																		? format 
																		: Property.SQL_DATETIME_FORMAT);
			java.util.Date date = formatter.parse(val);
			if(type == DataType.SQLTIMESTAMP) {
				return new Timestamp(date.getTime());
			}else {
				return new Date(date.getTime());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	private Object getFieldValue(Field field, SQLExecutor exe) throws IllegalArgumentException, IllegalAccessException, SQLException {
		Object value = field.get(this);
		//
		if(value == null && field.isAnnotationPresent(com.it.soul.lab.sql.entity.Property.class) == true) {
			com.it.soul.lab.sql.entity.Property annotation = field.getAnnotation(com.it.soul.lab.sql.entity.Property.class);
			String defaultVal = annotation.defaultValue();
			DataType type = annotation.type();
			switch (type) {
			case INT:
				value = Integer.valueOf(defaultVal);
				break;
			case FLOAT:
				value = Float.valueOf(defaultVal);
				break;
			case DOUBLE:
				value = Double.valueOf(defaultVal);
				break;
			case BOOL:
				value = Boolean.valueOf(defaultVal);
				break;
			case SQLDATE:
			case SQLTIMESTAMP:
				value = parseDate(defaultVal, type, annotation.parseFormat());
				break;
			case BLOB:
				value = (exe != null) ? exe.createBlob(defaultVal) : defaultVal;
				break;
			case BYTEARRAY:
				value = defaultVal.getBytes();
				break;
			default:
				value = defaultVal;
				break;
			}
		}
		//always.
		return value;
	}
	protected Property getProperty(String key, SQLExecutor exe) {
		Property result = null;
		try {
			Field field = this.getClass().getDeclaredField(key);
			if(field.isAnnotationPresent(PrimaryKey.class)) {
				if (((PrimaryKey)field.getAnnotation(PrimaryKey.class)).autoIncrement() == true) {return null;}
			}
			field.setAccessible(true);
			String name = field.getName();
			Object value = getFieldValue(field, exe);
			DataType type = getDataType(value);
			result = new Property(name, value, type);
			field.setAccessible(false);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	private boolean shouldAcceptAllProperty() {
		if(this.getClass().isAnnotationPresent(TableName.class) == false) {
			return true;
		}
		TableName tableName = (TableName) this.getClass().getAnnotation(TableName.class);
		return tableName.acceptAll();
	}
	private Boolean _isAutoIncremented = null;
	private boolean isAutoIncrement() {
		if(_isAutoIncremented == null) {
			PrimaryKey primAnno = getPrimaryKey(); 
			if(primAnno == null) {
				_isAutoIncremented = false;
			}
			_isAutoIncremented = primAnno.autoIncrement();
		}
		return _isAutoIncremented;
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
	private Property getPrimaryProperty(SQLExecutor exe) {
		Property result = null;
		try {
			String key = getPrimaryKey().name().trim();
			Field field = this.getClass().getDeclaredField(key);
			field.setAccessible(true);
			Object value = getFieldValue(field, exe);
			DataType type = getDataType(value);
			result = new Property(key, value, type);
			field.setAccessible(false);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	public Boolean update(SQLExecutor exe, String...keys) throws SQLException, Exception {
		List<Property> properties = new ArrayList<>();
		if(keys.length > 0) {
			for (String key : keys) {
				String skey = key.trim();
				Property prop = getProperty(skey, exe);
				if (prop == null) {continue;}
				properties.add(prop);
			}
		}else {
			properties = getProperties(exe);
		}
		String tableName = Entity.tableName(getClass());
		SQLUpdateQuery query = (SQLUpdateQuery) new SQLQuery.Builder(QueryType.UPDATE)
														.set(properties.toArray(new Property[0]))
														.from(tableName)
														.where(updateWhereExpression()).build();
		int isUpdate = exe.executeUpdate(query);
		return isUpdate == 1;
	}
	protected ExpressionInterpreter updateWhereExpression() {
		return new Expression(getPrimaryProperty(null), Operator.EQUAL);
	}
	@Override
	public Integer insert(SQLExecutor exe, String... keys) throws SQLException, Exception {
		List<Property> properties = new ArrayList<>();
		if(keys.length > 0) {
			for (String key : keys) {
				String skey = key.trim();
				Property prop = getProperty(skey, exe);
				if (prop == null) {continue;}
				properties.add(prop);
			}
		}else {
			properties = getProperties(exe);
		}
		SQLInsertQuery query = (SQLInsertQuery) new SQLQuery.Builder(QueryType.INSERT)
															.into(Entity.tableName(getClass()))
															.values(properties.toArray(new Property[0])).build();
		
		int insert = exe.executeInsert(isAutoIncrement(), query);
		return insert;
	}
	@Override
	public Boolean delete(SQLExecutor exe) throws SQLException, Exception {
		Expression exp = new Expression(getPrimaryProperty(exe), Operator.EQUAL);
		SQLDeleteQuery query = (SQLDeleteQuery) new SQLQuery.Builder(QueryType.DELETE)
														.rowsFrom(Entity.tableName(getClass()))
														.where(exp).build();
		int deletedId = exe.executeDelete(query);
		return deletedId == 1;
	}
	private static <T extends Entity> String tableName(Class<T> type) {
		if(type.isAnnotationPresent(TableName.class) == false) {
			return type.getSimpleName();
		}
		TableName tableName = (TableName) type.getAnnotation(TableName.class);
		String name = (tableName.value().trim().length() == 0) ? type.getSimpleName() : tableName.value().trim();
		return name;
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
		String name = Entity.tableName(type);
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
