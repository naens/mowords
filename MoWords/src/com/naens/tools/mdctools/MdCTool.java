package com.naens.tools.mdctools;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.naens.tools.mdctools.mdcgraphics.MdCFontLetters;
import com.naens.tools.mdctools.mdcgraphics.MdCGraphicConverter;
import com.naens.tools.mdctools.mdcstruct.MdCCartouche;
import com.naens.tools.mdctools.mdcstruct.MdCElement;
import com.naens.tools.mdctools.mdcstruct.MdCLetter;
import com.naens.tools.mdctools.mdcstruct.MdCOperation;

public class MdCTool {

	private MdCTool() {
	}

	public static Bitmap mdcToBitmap(Typeface typeface, String mdc, int height, int color)
			throws InvalidMdCCodeException {
		boolean vertical = false;
		boolean rightToLeft = false;
		Pattern pattern = Pattern.compile("^#([^\\s]*)\\s+(.*)$");
		Matcher matcher = pattern.matcher(mdc);
		if (matcher.matches()) {
			String options = matcher.group(1);
			vertical = options.contains("v"); // v = vertical, h = horizontal
			rightToLeft = options.contains("r"); // r = right to left, l = left
													// to right
			mdc = matcher.group(2);
		}
		MdCElement element = convertMdCToNodes(mdc);
		element.setRoot();
		Log.i("TAG", "root=" + element.toString() + " H=" + height);
		if (vertical) {
			if (element instanceof MdCOperation && ((MdCOperation) element).getDirection().equals(MdCOperation.SubNodesDirection.HORIZONTAL)) {
				MdCOperation operation = (MdCOperation) element;
				for (MdCElement subElement : operation.getSubNodes()) {
					removeMdCRepeatOperations(subElement);
					rotateAllLetters90LeftAndFlipOperations(subElement);
				}
			} else if (element instanceof MdCCartouche) {
				MdCCartouche c = (MdCCartouche) element;
				while (c.getElement() instanceof MdCOperation
						&& ((MdCOperation)c.getElement()).getSubNodes().size() == 1) {
					c.setElement(((MdCOperation)c.getElement()).getSubNodes().get(0));
				}
				MdCElement cElement = c.getElement();
				if (cElement instanceof MdCOperation) {
					MdCOperation operation = (MdCOperation) cElement;
					for (MdCElement subElement : operation.getSubNodes()) {
						removeMdCRepeatOperations(subElement);
						rotateAllLetters90LeftAndFlipOperations(subElement);
					}
				} else {
					removeMdCRepeatOperations(cElement);
					rotateAllLetters90LeftAndFlipOperations(cElement);
				}
			} else {
				removeMdCRepeatOperations(element);
				rotateAllLetters90LeftAndFlipOperations(element);
			}
			Log.i("TAG", "top-bottom: " + element);
		} else {
			removeMdCRepeatOperations(element);
		}
		Log.i("TAG", "vertical=" + vertical);
		Log.i("TAG", "rightToLeft=" + rightToLeft);
		element.setGraphics(new MdCFontLetters((int) (height * 1.1), typeface, color));
		MdCGraphicConverter.fillGraphicSizes(element, height);
		MdCGraphicConverter.fillGraphicPlaces(element, 0, 0);
		int d = (int) ((double) height / 2.0 - element.getHeight() / 2.0);
		Bitmap bitmap = Bitmap.createBitmap((int) element.getWidth() + 1, height /*(int)element.getHeight() + 1 */, Bitmap.Config.ARGB_8888);
		Log.i("TAG", String.format("Bitmap: W=%d H=%d", bitmap.getWidth(), bitmap.getHeight()));

		Canvas canvas = new Canvas(bitmap);
		 canvas.drawColor(0xFFCAEEFF);
//		canvas.drawColor(Color.DKGRAY);

		List<MdCLetter> letters = elementLetterList(element);

		for (MdCLetter letter : letters) {
			int x = (int) letter.getX();
			int y = (int) letter.getY() + d;
			Bitmap letterBitmap = letter.getBitmap();
			// Paint p = new Paint();
			// p.setColor(Color.TRANSPARENT);
			Log.i("TAG", String.format("l[%h] x=%d y=%d w=%d h=%d s=%.2f", letter.getCodePoint(), x, y,
							letterBitmap.getWidth(), letterBitmap.getHeight(), letter.getScale()));
			// Rect r = new Rect(x, y, x + letterBitmap.getWidth(), y +
			// letterBitmap.getHeight());
			// canvas.drawRect(r, p);
			canvas.drawBitmap(letterBitmap, x, y, null);
		}

		List<MdCCartouche> cartouches = elementCartoucheList(element);

		for (MdCCartouche cartouche : cartouches) {
			int x = (int) cartouche.getX();
			int y = (int) cartouche.getY() + d;
			Bitmap cartoucheBitmap = cartouche.getBitmap();
			// Paint p = new Paint();
			// p.setColor(Color.TRANSPARENT);
			Log.i("TAG", String.format("c: x=%d y=%d w=%d h=%d s=%.2f", x, y, cartoucheBitmap.getWidth(),
							cartoucheBitmap.getHeight(), cartouche.getScale()));
			// Rect r = new Rect(x, y, x + cartoucheBitmap.getWidth(), y +
			// cartoucheBitmap.getHeight());
			// canvas.drawRect(r, p);
			canvas.drawBitmap(cartoucheBitmap, x, y, null);
		}
		if (rightToLeft || vertical) {
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix matrix = new Matrix();
			if (vertical) {
				matrix.postScale(1, rightToLeft ? 1 : -1);
			} else {
				matrix.postScale(rightToLeft ? -1 : 1, 1);
			}
			matrix.postRotate(vertical ? 90 : 0);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
		}
		return bitmap;
	}

	private static void rotateAllLetters90LeftAndFlipOperations(MdCElement element) {
		if (element instanceof MdCLetter) {
			MdCLetter letter = (MdCLetter) element;
			letter.setRotate((letter.getRotate() + 270) % 360);
			letter.setFlip(!letter.isFlip());
		}
		if (element instanceof MdCCartouche) {
			MdCCartouche cartouche = (MdCCartouche) element;
//			cartouche.setRotate((cartouche.getRotate() + 270) % 360);
			rotateAllLetters90LeftAndFlipOperations(cartouche.getElement());
		}
		if (element instanceof MdCOperation) {
			MdCOperation operation = (MdCOperation) element;
			// if (!element.isRoot()){
			if (operation.getDirection() == MdCOperation.SubNodesDirection.VERTICAL) {
				operation.setDirection(MdCOperation.SubNodesDirection.HORIZONTAL);
			} else if (operation.getDirection() == MdCOperation.SubNodesDirection.HORIZONTAL) {
				operation.setDirection(MdCOperation.SubNodesDirection.VERTICAL);
				// }
			} else {
				// operation.flipElements ();
			}
			for (MdCElement subNodeElement : operation.getSubNodes()) {
				rotateAllLetters90LeftAndFlipOperations(subNodeElement);
			}
		}
	}

	private static List<MdCCartouche> elementCartoucheList(MdCElement element) {
		List<MdCCartouche> cartouches = new LinkedList<MdCCartouche>();
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
		List<MdCLetter> letters = new LinkedList<MdCLetter>();
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
			letters.addAll(elementLetterList(((MdCCartouche) element).getElement()));
		}
		return letters;
	}

	private static MdCElement convertMdCToNodes(String mdc) throws InvalidMdCCodeException {
		if (mdc.trim().equals("")) {
			throw new InvalidMdCCodeException();
		}
//		mdc = "(" + mdc.replaceAll("[ ]+", ")*(") + ")";
//		mdc = mdc.replaceAll("<", "<(");
//		mdc = mdc.replaceAll(">", ")>");
		mdc = mdc.replaceAll("^([^ <>]+)[ ]+", "($1) ");
		mdc = mdc.replaceAll("[ ]+([^ <>]+)$", " ($1)");
		mdc = mdc.replaceAll("[ ]+([^ <>]+)[ ]+", " ($1) ");
		mdc = mdc.replaceAll("<([^ <>]+)[ ]+", "<($1) ");
		mdc = mdc.replaceAll("[ ]+([^ <>]+)>", " ($1)>");
		mdc = mdc.replaceAll("[ ]+", "*");
		MdCElement result = parseMdCString(mdc);
		return result;
	}

	private static MdCElement parseMdCString(String element) throws InvalidMdCCodeException {
		if (!element.matches(".*[\\*\\:\\<\\>].*")) { // does not contain * : < // >
			if (element.matches("^\\(.*\\)$")) { // ^(.*)$
				return parseMdCString(element.substring(1, element.length() - 1));
			}
			boolean flip = false;
			int rotate = 0;
			String code = element;
			if (element.matches(".*\\\\$")) { // flip //ends with '\'
				flip = true;
				code = element.replaceAll("\\\\$", "");
			}
			if (element.matches(".*\\\\r\\d+$")) { // rotate // '\d'= A digit:
													// '[0-9]'
				String[] sAr = element.split("\\\\r");
				rotate = (4 - (Integer.parseInt(sAr[1]) % 4)) * 90;
				code = sAr[0];
			}
			/*
			 * if (element.matches(".*\\\\R\\d+$")) { //rotate String [] sAr =
			 * element.split("\\\\r"); rotate = Integer.parseInt(sAr [1]); code
			 * = sAr [0]; }
			 */
			if (element.matches(".*\\\\t\\d+$")) { // rotate & flip
				flip = true;
				String[] sAr = element.split("\\\\t");
				rotate = (Integer.parseInt(sAr[1]) % 4) * 90;
				code = sAr[0];
			}
			if (code.matches("[1-5]+")) {
				boolean v = rotate % 180 == 90;
				int k = Integer.parseInt(code);
				if (k == 1) {
					MdCLetter l = new MdCLetter(convertMdCToCodePoint("Z1"), rotate);
					l.setFlip(flip);
					return l;
				}

				MdCOperation operation = new MdCOperation(v ? MdCOperation.SubNodesDirection.VERTICAL
						: MdCOperation.SubNodesDirection.HORIZONTAL);
				for (int i = 0; i < k; i++) {
					MdCLetter l = new MdCLetter(convertMdCToCodePoint("Z1"), rotate);
					l.setFlip(flip);
					operation.addSubNode(l);
				}
				return operation;
			} else {
				MdCLetter l = new MdCLetter(convertMdCToCodePoint(code), rotate);
				l.setFlip(flip);
				return l;
			}
		}

		List<MdCOperation.SubNodesDirection> operations = new LinkedList<MdCOperation.SubNodesDirection>();
		List<String> subElements = new LinkedList<String>();
		List<Integer> cartouches = new LinkedList<Integer>();
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
						// currentLetter.append(c);
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
						++level;
						// currentLetter.append(c);
						break;
					case ')':
						if (level == 0) {
							throw new IllegalStateException("')' with level = 0");
						}
						--level;
						break;
					case '<':
						++cartoucheLevel;
						// currentLetter.append(c);
						break;
					case '>':
						if (cartoucheLevel == 0) {
							throw new IllegalStateException("'>' with cartouche level = 0");
						}
						--cartoucheLevel;
						if (cartoucheLevel == 0 && level == 0) {
							cartouches.add(subElements.size());
						}
						break;
					default:
						// currentLetter.append(c);
						break;

				}

				if (cartoucheLevel > 0 || level > 0) {
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
		if (subElements.size() == 1) {
			if (cartouches.size() == 1) {
				MdCCartouche.CartoucheType cartoucheType = null;
				String eString = subElements.get(0);
				if (eString.startsWith("#")) {
					eString = eString.substring(1);
					cartoucheType = MdCCartouche.CartoucheType.HWT;
				} else if (eString.startsWith("$")) {
					eString = eString.substring(1);
					cartoucheType = MdCCartouche.CartoucheType.SEREKH;
				} else if (eString.startsWith("0")) {
					eString = eString.substring(1);
					cartoucheType = MdCCartouche.CartoucheType.ENCLOSURE;
				}
				MdCElement subEl = parseMdCString(eString);
				return new MdCCartouche(subEl, cartoucheType);
			} else {
				MdCOperation result = new MdCOperation(MdCOperation.SubNodesDirection.HORIZONTAL);

				MdCElement e = parseMdCString(subElements.get(0));
				result.addSubNode(e);
				return result;
			}
		} else {
			if (operations.contains(MdCOperation.SubNodesDirection.VERTICAL)) {
				MdCOperation result = new MdCOperation(MdCOperation.SubNodesDirection.VERTICAL);
				List<Integer> verticalIndexes = findIndexes(operations, MdCOperation.SubNodesDirection.VERTICAL);
				verticalIndexes.add(subElements.size() - 1);
				int oldIndex = -1;
				for (Integer verticalIndex : verticalIndexes) {
					List<String> subSubElements = subElements.subList(oldIndex + 1, verticalIndex + 1);
					MdCElement e = null;
					int i = oldIndex + 1;
					if (subSubElements.size() == 1) {	//CARTOUCHE < >	HWT <# >	SEREKH<$ >	ENCLOSURE<0 >
						String eString = subElements.get(i);
						if (cartouches.contains(i)) {
							MdCCartouche.CartoucheType cartoucheType = null;
							if (eString.startsWith("#")) {
								eString = eString.substring(1);
								cartoucheType = MdCCartouche.CartoucheType.HWT;
							} else if (eString.startsWith("$")) {
								eString = eString.substring(1);
								cartoucheType = MdCCartouche.CartoucheType.SEREKH;
							} else if (eString.startsWith("0")) {
								eString = eString.substring(1);
								cartoucheType = MdCCartouche.CartoucheType.ENCLOSURE;
							}
							MdCElement subEl = parseMdCString(eString);
							e = new MdCCartouche(subEl, cartoucheType);
						} else {
							e = parseMdCString(subSubElements.get(0));
						}
					} else {
						MdCOperation mop = new MdCOperation(MdCOperation.SubNodesDirection.HORIZONTAL);
						for (String sel : subSubElements) {
							MdCElement e2 = parseMdCString(sel);
							if (cartouches.contains(i)) {
								e2 = new MdCCartouche(e2);
							}
							mop.addSubNode(e2);
							++i;
						}
						e = mop;
					}
					result.addSubNode(e);
					oldIndex = verticalIndex;
				}
				return result;
			} else {
				MdCOperation result = new MdCOperation(MdCOperation.SubNodesDirection.HORIZONTAL);
				for (int i = 0; i < subElements.size(); ++i) {
					MdCElement e = parseMdCString(subElements.get(i));
					if (cartouches.contains(i)) {
						e = new MdCCartouche(e);
					}
					result.addSubNode(e);
				}
				return result;
			}
		}
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

		return MdCToUnicode.getCode(subElement)/* -0x13000 + 0xE000 */;
	}

	private static void removeMdCRepeatOperations(MdCElement element) {
		if (element instanceof MdCLetter) {
			// nothing to do
		}
		if (element instanceof MdCCartouche) {
			MdCCartouche cartouche = (MdCCartouche) element;
			while (cartouche.getElement() instanceof MdCOperation
					&& ((MdCOperation)cartouche.getElement()).getSubNodes().size() == 1) {
				cartouche.setElement(((MdCOperation)cartouche.getElement()).getSubNodes().get(0));
			}
			removeMdCRepeatOperations(cartouche.getElement());
		}
		if (element instanceof MdCOperation) {
			// TODO removeMdCRepeatOperations sub nodes?
			MdCOperation operation = (MdCOperation) element;
			List<MdCElement> subNodes = operation.getSubNodes();
			boolean hasMore;
			do {
				hasMore = false;
				List<MdCElement> subNodesCopy = new LinkedList<MdCElement>(subNodes);
				for (int i = subNodesCopy.size() - 1; i >= 0; --i) {
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
		}
	}

	public static Rect findInsideRect(Bitmap bmp, int startX, int startY, int color) {
		Integer topY = null;
		Integer bottomY = null;
		Integer leftX = null;
		Integer rightX = null;
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int i = 1;
		while (topY == null || bottomY == null || leftX == null || rightX == null) {
			int firstX = leftX == null ? startX - i: leftX;
			int lastX = rightX == null ? startX + i: rightX;
			int firstY = topY == null ? startY - i : topY;
			int lastY = bottomY == null ? startY + i : bottomY;
			if (firstX < 0) {
				leftX = 0;
			}
			if (lastX > width - 1) {
				lastX = rightX = width - 1;
			}
			if (firstY < 0) {
				topY = firstY = 0;
			}
			if (lastY > height - 1) {
				bottomY = lastY = height - 1;
			}
			if (topY == null) {
				int y = firstY;
				for (int x = firstX + 1; x <= lastX - 1; ++x) {
					if (bmp.getPixel(x, y) != color) {
						topY = y + 1;
						break;
					}
				}
			}
			if (bottomY == null) {
				int y = lastY;
				for (int x = firstX + 1; x <= lastX - 1; ++x) {
					if (bmp.getPixel(x, y) != color) {
						bottomY = y - 1;
						break;
					}
				}
			}
			if (leftX == null) {
				int x = firstX;
				for (int y = firstY; y <= lastY; ++y) {
					if (bmp.getPixel(x, y) != color) {
						leftX = x + 1;
						break;
					}
				}
			}
			if (rightX == null) {
				int x = lastX;
				for (int y = firstY; y <= lastY; ++y) {
					if (bmp.getPixel(x, y) != color) {
						rightX = x - 1;
						break;
					}
				}
			}
			++i;
		}
		return new Rect(leftX, topY, rightX, bottomY);
	}
}
