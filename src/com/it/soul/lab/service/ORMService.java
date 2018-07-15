package com.it.soul.lab.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.it.soul.lab.jpql.query.JPQLQuery;
import com.it.soul.lab.sql.query.SQLQuery.QueryType;
import com.it.soul.lab.sql.query.models.Expression;
import com.it.soul.lab.sql.query.models.ExpressionInterpreter;
import com.it.soul.lab.sql.query.models.Logic;
import com.it.soul.lab.sql.query.models.Operator;
import com.it.soul.lab.sql.query.models.Property;

public class ORMService<T> extends AbstractService<T> implements ORMServiceProtocol<T>,Serializable {

	private static final long serialVersionUID = -1656018780509389672L;
	private static final String _TAG = "GenericServiceImpl";
	@SuppressWarnings("unused")
	private static final String _MESSAGE = "GenericServiceImpl not available now!";
	
	public ORMService(EntityManager manager, String entity, Class<T> type){
		super(manager,entity,type);
	}
	
	public ORMService(EntityManager manager, Class<T> type){
		this(manager,type.getName(),type);
	}
	
	@Override
	public Collection<T> findAll() throws Exception {
		List<T> result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try{
			//String jpql = JPQLBuilders.createSelectQuery(getEntity(), null);
			JPQLQuery jpql = (JPQLQuery) new JPQLQuery.Builder(QueryType.SELECT).columns().from(getEntity()).build();
			TypedQuery<T> query = getEntityManager().createQuery(jpql.toString(), getEntityType());
			result = query.getResultList();
		}
		catch(PersistenceException e){result = null;}
		catch (Exception e) {throw e;}
		return result;
	}
	
	@Override
	public Collection<T> findAll(String...columns) throws Exception {
		List<T> result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try{
			//String jpql = JPQLBuilders.createSelectQuery(getEntity(), propertyNames);
			JPQLQuery jpql = (JPQLQuery) new JPQLQuery.Builder(QueryType.SELECT).columns(columns).from(getEntity()).build();
			TypedQuery<T> query = getEntityManager().createQuery(jpql.toString(), getEntityType());
			result = query.getResultList();
		}catch(PersistenceException e){result = null;}
		catch (Exception e) {throw e;}
		return result;
	}

	@Override
	@Deprecated public Collection<T> findAll(Property item, String...columns) throws Exception {
		List<T> result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try{
			//String jpql = JPQLBuilders.createSelectQuery(getEntity(), propertyNames, Logic.AND, new String[]{searchKey});
			JPQLQuery jpql = (JPQLQuery) new JPQLQuery.Builder(QueryType.SELECT).columns(columns).from(getEntity()).whereParams(Logic.AND, item.getKey()).build();
			TypedQuery<T> query = getEntityManager().createQuery(jpql.toString(), getEntityType());
			query.setParameter(item.getKey(), item.getValue());
			result = query.getResultList();
		}catch(PersistenceException e){result = null;}
		catch (Exception e) {throw e;}
		return result;
	}

	@Override
	@Deprecated public Collection<T> findAll(Map<String, Object> itemIds, Logic whereLogic, String...columns) throws Exception {
		List<T> result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try{
			//String jpql = JPQLBuilders.createSelectQuery(getEntity(), propertyNames, whereLogic, keyValuePair.keySet().toArray(new String[]{}));
			String[] whereParams = itemIds.keySet().toArray(new String[]{});
			JPQLQuery jpql = (JPQLQuery) new JPQLQuery.Builder(QueryType.SELECT).columns(columns).from(getEntity()).whereParams(whereLogic, whereParams).build();
			TypedQuery<T> query = getEntityManager().createQuery(jpql.toString(), getEntityType());
			for (Entry<String,Object> item : itemIds.entrySet()) {
				query.setParameter(item.getKey(), item.getValue());
			}
			result = query.getResultList();
		}catch(PersistenceException e){result = null;}
		catch (Exception e) {throw e;}
		return result;
	}
	
	public Collection<T> findMatches(ExpressionInterpreter expression , String...columns) throws Exception {
		List<T> result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try{
			//String jpql = JPQLBuilders.createSelectQuery(getEntity(), propertyNames, whereLogic, keyValuePair.keySet().toArray(new String[]{}));
			//String[] whereParams = itemIds.keySet().toArray(new String[]{});
			JPQLQuery jpql = (JPQLQuery) new JPQLQuery.Builder(QueryType.SELECT).columns(columns).from(getEntity()).where(expression).build();
			TypedQuery<T> query = getEntityManager().createQuery(jpql.toString(), getEntityType());
			for (Expression item : expression.resolveExpressions()) {
				query.setParameter(item.getProperty(), item.getValueProperty().getValue());
			}
			result = query.getResultList();
		}catch(PersistenceException e){result = null;}
		catch (Exception e) {throw e;}
		return result;
	}
	
	@Deprecated public Collection<T> findAll(Map<String, Object> itemIds, Logic whereLogic, Map<String, Operator> operators, String...columns) throws Exception {
		List<T> result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try{
			//new way
			String[] whereParams = itemIds.keySet().toArray(new String[0]);
			List<Expression> compares = new ArrayList<Expression>();
			for (String string : whereParams) {
				compares.add(new Expression(string, operators.get(string)));
			}
			Expression[] whereCompares = compares.toArray(new Expression[0]);
			JPQLQuery jpql = (JPQLQuery) new JPQLQuery.Builder(QueryType.SELECT).columns(columns).from(getEntity()).whereParams(whereLogic, whereCompares).build();
			//
			//String jpql = JPQLBuilders.createSelectQuery(getEntity(), propertyNames, whereLogic, operators);
			TypedQuery<T> query = getEntityManager().createQuery(jpql.toString(), getEntityType());
			for (Entry<String,Object> item : itemIds.entrySet()) {
				query.setParameter(item.getKey(), item.getValue());
			}
			result = query.getResultList();
		}
		catch(PersistenceException e){result = null;}
		catch (Exception e) {throw e;}
		return result;
	}

	@Override
	public Object findBy(Property searchProperty) throws Exception {
		Object result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		
		try{
			//String jpql = JPQLBuilders.createSelectQuery(getEntity(), null, Logic.AND, new String[]{searchKey});
			JPQLQuery jpql = (JPQLQuery) new JPQLQuery.Builder(QueryType.SELECT).columns().from(getEntity()).whereParams(Logic.AND, searchProperty.getKey()).build();
			TypedQuery<T> query = getEntityManager().createQuery(jpql.toString(), getEntityType());
			query.setParameter(searchProperty.getKey(), searchProperty.getValue());
			result = query.getSingleResult();
		}
		catch(PersistenceException e){result = null;}
		catch (Exception e) {throw e;}
		return result;
	}
	
	@Override
	public Object findBy(Property searchProperty, String...columns) throws Exception{
		Object result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		
		try{
			//String jpql = JPQLBuilders.createSelectQuery(getEntity(), propertyNames, Logic.AND, new String[]{searchKey});
			JPQLQuery jpql = (JPQLQuery) new JPQLQuery.Builder(QueryType.SELECT).columns(columns).from(getEntity()).whereParams(Logic.AND, searchProperty.getKey()).build();
			TypedQuery<T> query = getEntityManager().createQuery(jpql.toString(), getEntityType());
			query.setParameter(searchProperty.getKey(), searchProperty.getValue());
			result = query.getSingleResult();
		}
		catch(PersistenceException e){result = null;}
		catch (Exception e) {throw e;}
		return result;
	}

	@Override
	public boolean isItemExist(Object itemId) throws Exception {
		boolean result = false;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try{
			if(getEntityManager().find(getEntityType(), itemId) != null){result = true;}
		}catch (Exception e) {throw e;}
		return result;
	}

	@Override
	public long getItemCount() throws Exception {
		long result = 0;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try{
			String pql = "SELECT COUNT(u) FROM "+getEntity()+" u";
			Query query = getEntityManager().createQuery(pql);
			Long val = (Long)query.getSingleResult();
			result = val;
		}catch(PersistenceException e){result = 0;}
		catch (Exception e) {throw e;}
		return result;
	}

	@Override
	public synchronized Object addNewItem(Object item) throws Exception {
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return null;}
		if(item != null){
			try{
				getEntityManager().getTransaction().begin(); 
				getEntityManager().persist(item);
				getEntityManager().getTransaction().commit();
			}catch (Exception e) {
				getEntityManager().getTransaction().rollback();
				throw e;
			}
		}
		return item;
	}

	@Override
	public synchronized Object modifyItem(Object item) throws Exception {
		Object result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		if(item != null){
			try{
				getEntityManager().getTransaction().begin();
				result = getEntityManager().merge(item);
				getEntityManager().getTransaction().commit();
			}catch(Exception e){
				getEntityManager().getTransaction().rollback();
				throw e;
			}
		}
		return result;
	}

	@Override
	public synchronized boolean deleteItem(Object item) throws Exception {
		boolean result = false;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		if(item != null){
			try{
				if(getEntityManager().contains(item)){
					getEntityManager().getTransaction().begin();
					getEntityManager().remove(item);
					getEntityManager().getTransaction().commit();
					result = true;
				}
			}catch(Exception e){
				getEntityManager().getTransaction().rollback();
				throw e;
			}
		}
		return result;
	}

	@Override
	public synchronized Collection<?> addNewItems(Collection<? extends Object> items)
			throws Exception {
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return null;}
		try {
			if (items != null && items.size() > 0) {
				//TODO Optimize implementation for large number of items
				getEntityManager().getTransaction().begin();
				for (Object _item : items) {
					getEntityManager().persist(_item);
				}
				getEntityManager().getTransaction().commit();
			}
		} catch (Exception e) {
			getEntityManager().getTransaction().rollback();
			throw e;
		}
		return items;
	}

	@Override
	public synchronized Collection<?> modifyItems(Collection<? extends Object> items)
			throws Exception {
		ArrayList<Object> result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try {
			if (items != null && items.size() > 0) {
				result = new ArrayList<Object>();
				//TODO Optimize implementation for large number of items
				getEntityManager().getTransaction().begin();
				for (Object _item : items) {
					result.add(getEntityManager().merge(_item));
				}
				getEntityManager().getTransaction().commit();
			}
		} catch (Exception e) {
			getEntityManager().getTransaction().rollback();
			result = null;
			throw e;
		}
		return result;
	}

	@Override
	public synchronized boolean deleteItems(Collection<? extends Object> items)
			throws Exception {
		boolean result = false;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try {
			if (items != null && items.size() > 0) {
				//TODO Optimize implementation for large number of items
				getEntityManager().getTransaction().begin();
				for (Object _item : items) {
					if(getEntityManager().contains(_item))
						getEntityManager().remove(_item);
				}
				getEntityManager().getTransaction().commit();
				result = true;
			}
		} catch (Exception e) {
			getEntityManager().getTransaction().rollback();
			throw e;
		}
		return result;
	}
	
	@Override
	public Collection<?> addNewItems(Collection<? extends Object> items,
			int batchSize) throws Exception {
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return null;}
		try {
			if (items != null && items.size() > 0) {
				getEntityManager().getTransaction().begin();
				int counter = 1;
				for (Object _item : items) {
					getEntityManager().persist(_item);
					counter ++;
					if((counter % batchSize) == 0){
						getEntityManager().flush();
						getEntityManager().clear();
					}
				}
				getEntityManager().getTransaction().commit();
			}
		} catch (Exception e) {
			getEntityManager().getTransaction().rollback();
			throw e;
		}
		return items;
	}

	@Override
	public Collection<?> modifyItems(Collection<? extends Object> items,
			int batchSize) throws Exception {
		ArrayList<Object> result = null;
		//Checking entityManager
		if(getEntityManager() == null || !getEntityManager().isOpen()){return result;}
		try {
			if (items != null && items.size() > 0) {
				result = new ArrayList<Object>();
				getEntityManager().getTransaction().begin();
				int counter = 1;
				for (Object _item : items) {
					result.add(getEntityManager().merge(_item));
					counter ++;
					if((counter % batchSize) == 0){
						getEntityManager().flush();
						getEntityManager().clear();
					}
				}
				getEntityManager().getTransaction().commit();
			}
		} catch (Exception e) {
			getEntityManager().getTransaction().rollback();
			result = null;
			throw e;
		}
		return result;
	}
	
	@Override
	public Object refresh(Object item) throws Exception{
		Object result = null;
		if(item != null){
			result = item;
			try{
				if(getEntityManager().contains(item)){
					getEntityManager().refresh(item);
				}else{
					result = getEntityManager().merge(item);
					getEntityManager().refresh(result);
				}
			}catch(Exception e){throw e;}
		}
		return result;
	}

	@Override
	public Collection<T> refresh(Collection<T> items) throws Exception {
		try{
			if(items != null && items.size() > 0){
				for(Object item : items){
					getEntityManager().refresh(item);
				}
			}
		}catch(Exception e){throw e;}
		return items;
	}

	@Override
	public void clearItem(Object item) throws Exception {
		if(item != null){
			try{
				if(getEntityManager().contains(item))
					getEntityManager().detach(item);
			}catch (Exception e) {throw e;}
		}
	}

	@Override
	public void clearItems(Collection<T> items) throws Exception {
		if(items != null && items.size() > 0){
			try{
				for(Object item : items){
					if(getEntityManager().contains(item))
						getEntityManager().detach(item);
				}
			}catch (Exception e) {throw e;}
		}		
	}

	@Override
	public String toString() {return  _TAG + " extends " + super.toString();}

}
