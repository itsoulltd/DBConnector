package com.it.soul.lab.util;


public class EnumDefinitions {

	/**
	 * 
	 * @author 
	 * Don't change The current order of the enumerations. If needed then add new type to the end. 
	 *
	 */
	
	public static enum DataType{
    	ParamDataTypeInt,
    	ParamDataTypeFloat,
    	ParamDataTypeDouble,
    	ParamDataTypeBoolean,
    	ParamDataTypeString,
    	ParamDataTypeSQLDate,
    	ParamDataTypeBlob,
    	ParamDataTypeByteArray,
    	ParamDataTypeObject
    }
    
    public static enum ComparisonType{
    	IsEqual,
    	IsNotEqual,
    	IsGreater,
    	IsGreaterOrEqual,
    	IsSmaller,
    	IsSmallerOrEqual,
    	IN,
    	NOT_IN,
    	LIKE,
    	NOT_LIKE
    }
    
    public static enum Logic{
    	AND,
    	OR
    }
    
    public static String convertOperator(ComparisonType type){
		
		String eq = "=";
		switch (type) {
		case IsNotEqual:
			eq = "!=";
			break;
		case IsGreater:
			eq = ">";
			break;
		case IsGreaterOrEqual:
			eq = ">=";
			break;
		case IsSmaller:
			eq = "<";
			break;
		case IsSmallerOrEqual:
			eq = "<=";
			break;
		case IN:
			eq = "IN";
			break;
		case NOT_IN:
			eq = "NOT IN";
			break;
		case LIKE:
			eq = "LIKE";
			break;
		case NOT_LIKE:
			eq = "NOT LIKE";
		default:
			break;
		}
		return eq;
	}
	
	public enum ContainerHolderType{
		HOLDER_TYPE_NONE,
		HOLDER_TYPE_VERTICAL,
		HOLDER_TYPE_HORIZONTAL,
		HOLDER_TYPE_ACCORDION,
		HOLDER_TYPE_TAB
	}
	
	public enum ContentType{
		CONTENT_TYPE_NONE,
		CONTENT_TYPE_STRING,
		CONTENT_TYPE_INEGAR,
		CONTENT_TYPE_BOOLEAN,
		CONTENT_TYPE_OBJECT,
		CONTENT_TYPE_LOB
	}
	
	public enum ContentAccessPermission{
		CONTENT_PERMISSION_NONE,
		CONTENT_PERMISSION_PUBLIC,
		CONTENT_PERMISSION_PROTECTED,
		CONTENT_PERMISSION_PRIVATE
	}
	
	public enum Priority{
		PRIORITY_NONE,
		PRIORITY_LOW,
		PRIORITY_MEDIUM,
		PRIORITY_HIGH
	}
	
	public enum UserType{
		USER_TYPE_ADMIN,
		USER_TYPE_USER,
		USER_TYPE_OTHER
	}
}
