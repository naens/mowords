package com.naens.tools;

import java.util.Comparator;

public class CaseUnsensitiveComparator implements Comparator<String> {
	@Override
	public int compare(String lhs, String rhs) {
		return lhs.toLowerCase().compareTo(rhs.toLowerCase());
	}
}
