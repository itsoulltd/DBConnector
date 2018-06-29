package com.it.soul.lab.sql.entity;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(TYPE)
public @interface TableName {
	public String value() default "";
	public boolean autoIncrement() default false;
}
