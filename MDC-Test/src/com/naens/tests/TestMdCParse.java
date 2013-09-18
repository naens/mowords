package com.naens.tests;

import com.naens.tools.mdctools.MdCTools;
import com.naens.tools.mdctools.mdcstruct.MdCElement;

public class TestMdCParse {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MdCElement mdc = MdCTools.convertMdCToNodes("a b*7 k");
		System.out.println(mdc);

	}

}
