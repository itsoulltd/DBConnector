package com.it.soul.lab.service;

import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManager;

import com.it.soul.lab.util.EnumDefinitions.Logic;


public interface ORMServiceProtocol<T> {

	public Class<T> getEntityType();
	public String getEntity();
	public EntityManager getEntityManager();
	public Collection<T> findAll() throws Exception;
	public Collection<T> findAll(String[] propertyNames) throws Exception;
	public Collection<T> findAll(String itemId, Object itemValue, String[] propertyNames) throws Exception;
	public Collection<T> findAll(Map<String,Object> itemIds, Logic whereLogic, String[] propertyNames) throws Exception;
	public Object findBy(String searchProperty, Object value) throws Exception;
	public Object findBy(String searchProperty, Object value, String... propertyNames) throws Exception;
	public boolean isItemExist(Object itemId) throws Exception;
	public long getItemCount() throws Exception;
	public Object addNewItem(Object item) throws Exception;
	public Object modifyItem(Object item) throws Exception;
	public boolean deleteItem(Object item) throws Exception;
	public Collection<?> addNewItems(Collection<? extends Object> items) throws Exception;
	public Collection<?> modifyItems(Collection<? extends Object> items) throws Exception;
	public boolean deleteItems(Collection<? extends Object> items) throws Exception;
	public Collection<?> addNewItems(Collection<? extends Object> items, int batchSize) throws Exception;
	public Collection<?> modifyItems(Collection<? extends Object> items, int batchSize) throws Exception;
	public Object refresh(Object item) throws Exception;
	public Collection<T> refresh(Collection<T> items) throws Exception;
	public void clearItem(Object item) throws Exception;
	public void clearItems(Collection<T> items) throws Exception;
}
