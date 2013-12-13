package com.naens.tools.mdctools;

public class InvalidMdCCodeException extends RuntimeException {

	private static final long serialVersionUID = -9086943767160436161L;
	private String wrongChar;

	public InvalidMdCCodeException(String mdcChar) {
		wrongChar = mdcChar;
	}

	public InvalidMdCCodeException () {
	}

	public String getWrongChar() {
		return wrongChar;
	}

}
