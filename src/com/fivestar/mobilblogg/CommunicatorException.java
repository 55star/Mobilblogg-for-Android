package com.fivestar.mobilblogg;

public class CommunicatorException extends Exception {
	String error;
	
	public CommunicatorException() {
		super();
		error = "unknown";
	}
	
	public CommunicatorException(String e) {
		error = e;
	}
	
	public String getError() {
		return error;
	}
}
