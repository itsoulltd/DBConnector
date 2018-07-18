package com.it.soul.lab.service;

import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManager;
import com.it.soul.lab.sql.query.models.Logic;
import com.it.soul.lab.sql.query.models.ExpressionInterpreter;
import com.it.soul.lab.sql.query.models.Property;


public interface ORMServiceProtocol<T> {

	public Class<T> getEntityType();
	public String getEntity();
	public EntityManager getEntityManager();
	
	public Collection<T> findAll() throws Exception;
	public Collection<T> findAll(String...columns) throws Exception;
	@Deprecated public Collection<T> findAll(Property item, String...columns) throws Exception;
	@Deprecated public Collection<T> findAll(Map<String,Object> itemIds, Logic whereLogic, String...columns) throws Exception;
	public Collection<T> findMatches(ExpressionInterpreter expression , String...columns) throws Exception;
	
	public Object findBy(Property searchProperty) throws Exception;
	public Object findBy(Property searchProperty, String...columns) throws Exception;
	
	public boolean exist(Object itemId) throws Exception;
	public long rowCount() throws Exception;
	public Object insert(Object item) throws Exception;
	public Object update(Object item) throws Exception;
	public boolean delete(Object item) throws Exception;
	public Collection<?> batchInsert(Collection<? extends Object> items) throws Exception;
	public Collection<?> batchUpdate(Collection<? extends Object> items) throws Exception;
	public boolean batchDelete(Collection<? extends Object> items) throws Exception;
	public Collection<?> batchInsert(Collection<? extends Object> items, int batchSize) throws Exception;
	public Collection<?> batchUpdate(Collection<? extends Object> items, int batchSize) throws Exception;
	public Object refresh(Object item) throws Exception;
	public Collection<T> refresh(Collection<T> items) throws Exception;
	public void clearItem(Object item) throws Exception;
	public void clearItems(Collection<T> items) throws Exception;
}
