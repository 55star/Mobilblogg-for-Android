package com.fivestar.mobilblogg;

public class CommunicatorException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
