package com.it.soul.lab.sql.query.models;

public enum ScalerType{
	COUNT,
	DISTINCT,
	MAX,
	MIN,
	SUM,
	AVG;
	
	public String toString(){
		String result;
		switch (this) {
		case DISTINCT:
			result = "DISTINCT";
			break;
		case MAX:
			result = "MAX";
			break;
		case MIN:
			result = "MIN";
			break;
		case SUM:
			result = "SUM";
			break;
		case AVG:
			result = "AVG";
			break;
		default:
			result = "COUNT";
			break;
		}
		return result;
	}
	
	public String function(String column){
		String result;
		switch (this) {
		case DISTINCT:
			result = this.toString()+"("+column+")";
			break;
		case MAX:
			result = this.toString()+"("+column+")";
			break;
		case MIN:
			result = this.toString()+"("+column+")";
			break;
		default:
			result = this.toString()+"("+column+")";
			break;
		}
		return result;
	}
	
	public String functionAlias(String column){
		String result;
		switch (this) {
		case DISTINCT:
			result = this.toString()+"("+column+") AS distinct_"+column;
			break;
		case MAX:
			result = this.toString()+"("+column+") AS max_"+column;
			break;
		case MIN:
			result = this.toString()+"("+column+") AS min_"+column;
			break;
		default:
			result = this.toString()+"("+column+") AS count_"+column;
			break;
		}
		return result;
	}
	
}