package com.naens.tools.mdctools.mdcstruct;

import java.io.UnsupportedEncodingException;

public class MdCLetter extends MdCElement {

	private byte [] utf8Char;

	public MdCLetter(byte [] utf8Char) {
		super();
		this.utf8Char = utf8Char;
	}

	public byte [] getCharacter () {
		return utf8Char;
	}

	@Override
	public String toString() {
		try {
			return new String(utf8Char, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "bad encoding";
		}
	}
}
