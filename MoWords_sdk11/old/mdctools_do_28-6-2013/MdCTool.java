package com.naens.tools.mdctools;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.Log;

import com.naens.tools.mdctools.mdcgraphics.MdCFontLetters;
import com.naens.tools.mdctools.mdcgraphics.MdCGraphicConverter;
import com.naens.tools.mdctools.mdcstruct.MdCCartouche;
import com.naens.tools.mdctools.mdcstruct.MdCElement;
import com.naens.tools.mdctools.mdcstruct.MdCLetter;
import com.naens.tools.mdctools.mdcstruct.MdCOperation;

public class MdCTool {

	private MdCTool () {
	}

	public static Bitmap mdcToBitmap(Typeface typeface, String mdc, int height, int color) throws InvalidMdCCodeException {
		MdCElement element = convertMdCToNodes(mdc);
		element.setRoot();
		Log.i("TAG", "root=" + element.toString() + " H=" + height);
		element.setGraphics(new MdCFontLetters((int) (height * 1.1), typeface, color));
		MdCGraphicConverter.fillGraphicSizes(element, height);
		MdCGraphicConverter.fillGraphicPlaces(element, 0, 0);
		Bitmap bitmap = Bitmap.createBitmap((int)element.getWidth() + 1,
				(int) element.getHeight() + 1, Bitmap.Config.ARGB_8888);
		Log.i("TAG", String.format("Bitmap: W=%d H=%d", bitmap.getWidth(), bitmap.getHeight()));

        Canvas canvas = new Canvas(bitmap);
		// canvas.drawColor(bkgColor);

		List <MdCLetter> letters = elementLetterList (element);

		for (MdCLetter letter : letters) {
			int x = (int) letter.getX();
			int y = (int) letter.getY();
			Bitmap letterBitmap = letter.getBitmap ();
//			Paint p = new Paint();
//			p.setColor(Color.TRANSPARENT);
			Log.i("TAG", String.format("l[%h] x=%d y=%d w=%d h=%d s=%.2f", letter.getCodePoint(), x, y, letterBitmap.getWidth(), letterBitmap.getHeight(), letter.getScale ()));
//			Rect r = new Rect(x, y, x + letterBitmap.getWidth(), y + letterBitmap.getHeight());
//			canvas.drawRect(r, p);
	        canvas.drawBitmap(letterBitmap, x, y, null);
		}

		List <MdCCartouche> cartouches = elementCartoucheList (element);

		for (MdCCartouche cartouche : cartouches) {
			int x = (int) cartouche.getX();
			int y = (int) cartouche.getY();
			Bitmap cartoucheBitmap = cartouche.getBitmap ();
//			Paint p = new Paint();
//			p.setColor(Color.TRANSPARENT);
			Log.i("TAG", String.format("c: x=%d y=%d w=%d h=%d s=%.2f", x, y, cartoucheBitmap.getWidth(), cartoucheBitmap.getHeight(), cartouche.getScale ()));
//			Rect r = new Rect(x, y, x + cartoucheBitmap.getWidth(), y + cartoucheBitmap.getHeight());
//			canvas.drawRect(r, p);
	        canvas.drawBitmap(cartoucheBitmap, x, y, null);
		}
		return bitmap;
	}

	private static List<MdCCartouche> elementCartoucheList(MdCElement element) {
		List <MdCCartouche> cartouches = new LinkedList<MdCCartouche> ();
		if (element instanceof MdCCartouche) {
			MdCCartouche cartouche = (MdCCartouche) element;
			cartouches.add(cartouche);
			cartouches.addAll(elementCartoucheList(cartouche.getElement()));
			return cartouches;
		}
		if (element instanceof MdCOperation) {
			for (MdCElement subNodeElement : ((MdCOperation) element).getSubNodes()) {
				cartouches.addAll(elementCartoucheList(subNodeElement));
			}
		}
		return cartouches;
	}

	private static List<MdCLetter> elementLetterList(MdCElement element) {
		List <MdCLetter> letters = new LinkedList<MdCLetter> ();
		if (element instanceof MdCLetter) {
			letters.add((MdCLetter) element);
			return letters;
		}
		if (element instanceof MdCOperation) {
			for (MdCElement subNodeElement : ((MdCOperation) element).getSubNodes()) {
				letters.addAll(elementLetterList(subNodeElement));
			}
		}
		if (element instanceof MdCCartouche) {
			letters.addAll(elementLetterList(((MdCCartouche)element).getElement()));
		}
		return letters;
	}

	private static MdCElement convertMdCToNodes(String mdc) throws InvalidMdCCodeException {
		if (mdc.trim ().equals ("")) {
			throw new InvalidMdCCodeException ();
		}
		mdc = "(" + mdc.replaceAll("[ ]+", ")*(") + ")";
		MdCElement result = removeMdCRepeatOperations(parseMdCString(mdc));
		if (result instanceof MdCOperation) {
			((MdCOperation)result).setRoot();
		}
		return result;
	}

	private static MdCElement parseMdCString(String element) throws InvalidMdCCodeException {
		if (element.matches("[1-5]+")) {
			int k = Integer.parseInt(element);
			if (k == 1) {
				return new MdCLetter(convertMdCToCodePoint("Z1"), 0);
			}

			MdCOperation operation = new MdCOperation(MdCOperation.SubNodesDirection.HORIZONTAL);
			for (int i = 0; i < k; i++) {
				operation.addSubNode(new MdCLetter(convertMdCToCodePoint("Z1"), 0));
			}
			return operation;
		}
//		if (element.matches("^<[^>]*>$")) {
//			return new MdCCartouche (parseMdCString(element.substring(1, element.length() - 1)));
//		}

		if (!element.matches(".*[\\*\\:\\<\\>].*")) {
			if (element.matches("^\\(.*\\)$")) {
				return parseMdCString (element.substring(1, element.length() - 1));
			}
			String [] ss = element.split("\\\\");
			String code = ss [0];
			int rotate = ss.length == 2 ? Integer.parseInt(ss [1]) : 0;
			return new MdCLetter(convertMdCToCodePoint(code), rotate);
		}

		MdCOperation result;
		List <MdCOperation.SubNodesDirection> operations = new LinkedList <MdCOperation.SubNodesDirection>();
		List <String> subElements = new LinkedList <String> ();
		List <Integer> cartouches = new LinkedList <Integer> ();
		int level = 0;
		int cartoucheLevel = 0;
		char[] chars = element.toCharArray();
		StringBuffer currentLetter = new StringBuffer();
		for (char c : chars) {
			if (level == 0 && cartoucheLevel == 0) { 
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
						level += 1;
						break;
					case ')':
						throw new IllegalStateException("')' with level = 0");
					case '<':
						cartoucheLevel += 1;
//						currentLetter.append(c);
						break;
					case '>':
						throw new IllegalStateException("'>' with cartouche level = 0");
					default:
						currentLetter.append(c);
						break;
				}
			} else {
				switch (c) {
					case '(':
						++ level;
//						currentLetter.append(c);
						break;
					case ')':
						if (level == 0) {
							throw new IllegalStateException("')' with level = 0");
						}
						-- level;
						break;
					case '<':
						++ cartoucheLevel;
//						currentLetter.append(c);
						break;
					case '>':
						if (cartoucheLevel == 0) {
							throw new IllegalStateException("'>' with cartouche level = 0");
						}
						-- cartoucheLevel;
						if (cartoucheLevel == 0 && level == 0) {
							cartouches.add(subElements.size());
						} 
						break;
					default:
//						currentLetter.append(c);
						break;
						
				}

				if (cartoucheLevel > 0 || level > 0){
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
		if (subElements.size () == 1) {
			MdCElement e = parseMdCString(subElements.get (0));
			return cartouches.size () == 1 ? new MdCCartouche (e) : e;
		} else {
			if (operations.contains(MdCOperation.SubNodesDirection.VERTICAL)) {
				result = new MdCOperation(MdCOperation.SubNodesDirection.VERTICAL);
				List<Integer> verticalIndexes = findIndexes(operations, MdCOperation.SubNodesDirection.VERTICAL);
				verticalIndexes.add(subElements.size() - 1);
				int oldIndex = -1;
				for (Integer verticalIndex : verticalIndexes) {
					List<String> subSubElements = subElements.subList(oldIndex + 1, verticalIndex + 1);
					MdCElement e = null;
					int i = oldIndex + 1;
					if (subSubElements.size() == 1) {
						e = parseMdCString(subElements.get (i));
						if (cartouches.contains (i)) {
							e = new MdCCartouche (e);
						}
//						e = parseMdCString(subSubElements.get(0));
					} else {
						MdCOperation mop = new MdCOperation(MdCOperation.SubNodesDirection.HORIZONTAL);
						for (String sel : subSubElements) {
							MdCElement e2 = parseMdCString(sel);
							if (cartouches.contains (i)) {
								e2 =  new MdCCartouche (e2);
							}
							mop.addSubNode(e2);
							++ i;
						}
						e = mop;
					}
					result.addSubNode(e);
					oldIndex = verticalIndex;
				}
			} else {
				result = new MdCOperation(MdCOperation.SubNodesDirection.HORIZONTAL);
				for (int i = 0; i < subElements.size(); ++ i) {
					MdCElement e = parseMdCString(subElements.get (i));
					if (cartouches.contains (i)) {
						e =  new MdCCartouche (e);
					}
					result.addSubNode(e);
				}
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

	private static int convertMdCToCodePoint(String subElement) throws InvalidMdCCodeException {
		if (subElement.length() == 0) {
			return -1;
		}

		return MdCToUnicode.getCode (subElement);
	}

	private static MdCElement removeMdCRepeatOperations(MdCElement element) {
		if (element instanceof MdCLetter) {
			return element;
		}
		if (element instanceof MdCCartouche) {
			MdCCartouche cartouche = (MdCCartouche) element;
			removeMdCRepeatOperations(cartouche.getElement());
			return cartouche;
		}
		if (element instanceof MdCOperation) {
			MdCOperation operation = (MdCOperation) element;
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
		return element;
	}
}
