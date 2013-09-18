package com.naens.old.tools.mdctools;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;

import com.naens.tools.mdctools.mdcstruct.MdCElement;
import com.naens.tools.mdctools.mdcstruct.MdCLetter;
import com.naens.tools.mdctools.mdcstruct.MdCOperation;

public class MdCTools {

	public static Bitmap mdcToBitmap(String mdc, int height) {
		return Bitmap.createBitmap(5, 5, Bitmap.Config.ARGB_8888);
	}

	public static MdCOperation convertMdCToNodes(String mdc) {
		String[] elements = mdc.split(" ");
		if (elements.length == 1) {
			MdCElement el = parseMdCString(mdc);
			if (el instanceof MdCOperation) {
				return removeMdCRepeatOperations((MdCOperation) el);
			} else {
				MdCOperation result = new MdCOperation(
						MdCOperation.SubNodesDirection.HORIZONTAL);
				result.addSubNode(el);
				return removeMdCRepeatOperations(result);
			}
		}
		MdCOperation result = new MdCOperation(
				MdCOperation.SubNodesDirection.HORIZONTAL);
		for (String subElement : elements) {
			result.addSubNode(parseMdCString(subElement));
		}
		return removeMdCRepeatOperations(result);
	}

	private static MdCElement parseMdCString(String element) {
		if (!element.matches(".*[\\*\\:].*")) {
			return new MdCLetter(convertMdCToUtf8(element));
		}

		MdCOperation result;
		List<MdCOperation.SubNodesDirection> operations = new LinkedList<MdCOperation.SubNodesDirection>();
		List<String> subElements = new LinkedList<String>();
		int level = 0;
		char[] chars = element.toCharArray();
		StringBuffer currentLetter = new StringBuffer();
		for (char c : chars) {
			if (level == 0) {
				switch (c) {
				case '*':
					operations.add(MdCOperation.SubNodesDirection.HORIZONTAL);
					subElements.add(currentLetter.toString());
					currentLetter = new StringBuffer();
					break;
				case ':':
					operations.add(MdCOperation.SubNodesDirection.VERTICAL);
					subElements.add(currentLetter.toString());
					currentLetter = new StringBuffer();
					break;
				case '(':
					level = 1;
					break;
				case ')':
					throw new IllegalStateException("')' with level 0");
				default:
					currentLetter.append(c);
					break;
				}
			} else {
				switch (c) {
				case '(':
					++level;
					break;
				case ')':
					--level;
					break;
				}
				if (level > 0) {
					currentLetter.append(c);
				}
			}
		}
		subElements.add(currentLetter.toString());

		/*
		 * 0 1 2 3 4 5 6 * * : * * : | b h j k u i o
		 * 
		 * * * : * * : | b h j k u i o
		 */
		if (operations.contains(MdCOperation.SubNodesDirection.VERTICAL)) {
			result = new MdCOperation(MdCOperation.SubNodesDirection.VERTICAL);
			List<Integer> verticalIndexes = findIndexes(operations,
					MdCOperation.SubNodesDirection.VERTICAL);
			verticalIndexes.add(subElements.size() - 1);
			int oldIndex = -1;
			for (Integer verticalIndex : verticalIndexes) {
				List<String> subSubElements = subElements.subList(oldIndex + 1,
						verticalIndex + 1);
				MdCElement e = null;
				if (subSubElements.size() == 1) {
					e = parseMdCString(subSubElements.get(0));
				} else {
					MdCOperation mop = new MdCOperation(
							MdCOperation.SubNodesDirection.HORIZONTAL);
					for (String sel : subSubElements) {
						mop.addSubNode(parseMdCString(sel));
					}
					e = mop;
				}
				result.addSubNode(e);
				oldIndex = verticalIndex;
			}
		} else {
			result = new MdCOperation(MdCOperation.SubNodesDirection.HORIZONTAL);
			for (String subElement : subElements) {
				result.addSubNode(parseMdCString(subElement));
			}
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	private static List<Integer> findIndexes(List l, Object o) {
		List<Integer> result = new LinkedList<Integer>();
		for (int i = 0; i < l.size(); i++) {
			Object to = l.get(i);
			if (o.equals(to)) {
				result.add(i);
			}
		}
		return result;
	}

	private static byte[] convertMdCToUtf8(String subElement) {
		if (subElement.length() == 0) {
			return new byte[0];
		}
		try {
			return subElement.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return new byte[0];
		}
	}

	private static MdCOperation removeMdCRepeatOperations(MdCOperation operation) {
		List<MdCElement> subNodes = operation.getSubNodes();
		boolean hasMore;
		do {
			hasMore = false;
			List<MdCElement> subNodesCopy = new LinkedList <MdCElement>(subNodes);
			for (int i = 0; i < subNodesCopy.size(); ++i) {
				MdCElement subElement = subNodesCopy.get(i);
				if (subElement instanceof MdCOperation) {
					MdCOperation subOperation = (MdCOperation) subElement;
					if (subOperation.getDirection() == operation.getDirection()) {
						subNodes.remove(i);
						subNodes.addAll(i, subOperation.getSubNodes());
						hasMore = true;
					}
				}
			}
		} while (hasMore);
		return operation;
	}
}
