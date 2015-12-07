package com.example.toshiba.ternakku.http.exeption;

public class LisaException extends Exception {

	private static final long serialVersionUID = 1L;

	String error = "";
	
	public LisaException(String msg) {
		super(msg);
		
		error = msg;
	}
	
	public String getError() {
		return error;
	}
}
