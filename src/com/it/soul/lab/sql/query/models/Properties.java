package com.it.soul.lab.sql.query.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.it.soul.lab.sql.query.SQLQuery.DataType;

public class Properties {
	private List<Property> items = new ArrayList<Property>();
	public Properties add(Property prop){
		if (items.contains(prop)){
			return this;
		}
		items.add(prop);
		return this;
	}
	public Properties add(String name){
		return add(new Property(name));
	}
	public Properties add(String name, Object value, DataType type){
		return add(new Property(name, value, type));
	}
	public List<Property> getProperties(){
		return new ArrayList<Property>(this.items);
	}
	public String[] getKeys(){
    	//Before Java 8
        List<String> result = new ArrayList<String>();
        for (Property x : this.items) {
            result.add(x.getKey());
        }
    	return result.toArray(new String[]{});
    }
    public Map<String, Property> keyValueMap(){
    	Map<String, Property> result = new HashMap<String, Property>();
    	for (Property parameter : this.items) {
			result.put(parameter.getKey(), parameter);
		}
    	return result;
    }
    public Map<String,Object> mapObjectToColumns(List<String> newColumns, Map<String,Object> dataMap){
        Map<String,Object> nXRow = new HashMap<String, Object>(dataMap.size() <= 0 ? 1 : dataMap.size());
        if(dataMap.size() > 0 && newColumns.size() == getKeys().length){
            for(int index = 0; index < newColumns.size(); index++){
                String key = newColumns.get(index);
                String crosKey = getKeys()[index];
                if(dataMap.containsKey(crosKey)){
                    Object n = dataMap.get(crosKey);
                    if (n instanceof Property) {
                    	Property m = (Property)n;
                        n = new Property(key, m.getValue(), m.getType());
                    }
                    nXRow.put(key, n);
                }
            }
        }
        return nXRow;
    }
    public Map<String,Property> mapPropertyToColumns(List<String> newColumns, Map<String, Property> dataMap){
        Map<String,Property> nXRow = new HashMap<String, Property>(dataMap.size() <= 0 ? 1 : dataMap.size());
        if (dataMap.size() > 0) {
            if (newColumns.size() == getKeys().length) {
                for (int index = 0; index < newColumns.size(); index++) {
                    String key = newColumns.get(index);
                    String crosKey = getKeys()[index];
                    if (dataMap.containsKey(crosKey)) {
                    	Property m = dataMap.get(crosKey);
                    	Property n = new Property(key, m.getValue(),
                                m.getType());
                        nXRow.put(key, n);
                    }
                }
            }
        }
        return nXRow;
    }
    public int size(){
    	return items.size();
    }
}
