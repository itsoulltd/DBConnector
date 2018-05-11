package com.it.soul.lab.sql;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ResourcesFactory {

	private static ResourcesFactory _sharedInstance = null;
	private static Object _local = new Object();
	private static final String _EXTENTION = ".properties";
	
	private ResourcesFactory(){
		
	}
	
	public static ResourcesFactory sharedInstance(){
		
		synchronized (_local) {
			
			if(_sharedInstance == null){
				_sharedInstance = new ResourcesFactory();
			}
		}
		
		return _sharedInstance;
	}
	
	@Override
	protected Object clone() throws CloneNotSupportedException {
		
		return _sharedInstance;
	}
	
	@Override
	protected void finalize() throws Throwable {
		
		_sharedInstance = null;
		super.finalize();
	}
	
	/**
	 * 
	 * @param resourceName 
	 * e.g. pass method parameter = ResourcesBundel,
	 * when resource file name is ResourcesBundel.properties
	 * @return Properties
	 */
	public Properties getNamedResources(String resourceName)
	throws IOException,IllegalArgumentException{
		
		Properties result = null;
		
		try{
			if(resourceName != null && !resourceName.trim().equals("")){
				InputStream fileIO = getClass().getResourceAsStream("/" + resourceName + _EXTENTION);
				result = new Properties();
				result.load(fileIO);
				fileIO.close();
			}else{
				throw new IllegalArgumentException("Parameter should not be Null!!");
			}
		}
		catch(IOException ioe){
			throw ioe;
		}
		catch(IllegalArgumentException ille){
			throw ille;
		}
		
		return result;
	}
	
	public boolean saveNamedResources(String resourceName, Properties resource) 
	throws IOException,IllegalArgumentException{
		
		boolean result = false;
		
		try{
			if(resourceName != null && !resourceName.trim().equals("")
					&& resource != null){
				
				resource.store(new FileOutputStream("/"+resourceName+_EXTENTION), null);
				result = true;
			}else{
				throw new IllegalArgumentException("Parameter should not be Null!!");
			}
		}catch(IOException e){
			throw e;
		}catch(IllegalArgumentException e){
			throw e;
		}
		
		return result;
	}
}
