package com.naens.model;

import java.io.Serializable;


public class WordFont implements Serializable {

	private static final long serialVersionUID = 2722449800132269919L;

	private String name;

	private int size;

	public WordFont (String name, int size) {
		this.name = name;
		this.size = size;
	}

	public String getName () {
		return name;
	}

	public int getSize () {
		return size;
	}

}
