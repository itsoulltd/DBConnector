package com.it.soul.lab.sql.entity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.it.soul.lab.sql.query.models.DataType;

@Retention(RUNTIME)
@Target(FIELD)
public @interface Property {
	public String defaultValue() default "";
	public DataType type() default DataType.STRING;
	public String parseFormat() default "";
}
