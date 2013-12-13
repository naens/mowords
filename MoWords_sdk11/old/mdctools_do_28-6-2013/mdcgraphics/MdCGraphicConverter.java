package com.naens.tools.mdctools.mdcgraphics;

import java.util.List;

import com.naens.tools.mdctools.mdcstruct.MdCCartouche;
import com.naens.tools.mdctools.mdcstruct.MdCElement;
import com.naens.tools.mdctools.mdcstruct.MdCLetter;
import com.naens.tools.mdctools.mdcstruct.MdCOperation;

public class MdCGraphicConverter {

	public static final float V_GAP = 2;
	public static final float H_GAP = 2;

/*
 * 			TODO:	- Around right/left letters
 * 					- Rotate
 * 					+ Percents
 * 					+ right -> left, top -> bottom
 */

	public static void fillGraphicSizes (MdCElement root, float parentHeight) {

		if (root instanceof MdCLetter) {
			MdCLetter rootLetter = (MdCLetter) root;
			if (rootLetter.getHeight() >= parentHeight) {
				rootLetter.scale(parentHeight / rootLetter.getHeight());	//0.37|13000
			}
		}
	
		if (root instanceof MdCCartouche) {
			MdCCartouche cartouche = (MdCCartouche) root;
			MdCElement element = cartouche.getElement();
			cartouche.scale(parentHeight / cartouche.getHeight ());
			float inScale = cartouche.getInHeight() / element.getHeight();
			if (inScale < 1) {
				element.scale(inScale);
			}
			fillGraphicSizes (element, cartouche.getInHeight());
		}

		if (root instanceof MdCOperation) {
			MdCOperation rootOperation = (MdCOperation) root;
			rootOperation.setMaxHeight (parentHeight);
			List <MdCElement> elements = rootOperation.getSubNodes();
			int n = elements.size();
			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.VERTICAL) {

				float hAllGaps = (n - 1) * V_GAP;
				float totKnownHeight = 0;
				float maxWidth = 0;
				int numberUnknown = 0;
				boolean maxKnown = false;
				boolean [] knownArray = new boolean [elements.size()];

				for (int i = 0; i < elements.size(); ++ i) {
					MdCElement subElement = elements.get(i);
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						totKnownHeight += l.getHeight();
						knownArray [i] = true;
					} else if (subElement instanceof MdCOperation) {
						MdCOperation operation = (MdCOperation) subElement;
						float opHeight = operation.getHeight();
						if (opHeight > 0) {
							totKnownHeight += operation.getHeight();
							knownArray [i] = true;
						} else {
							++ numberUnknown;
							knownArray [i] = false;
						}
					} else if (subElement instanceof MdCCartouche) {						
						MdCCartouche cartouche = (MdCCartouche) subElement;
						totKnownHeight += cartouche.getHeight();
						knownArray [i] = true;
					}
					if (subElement.getWidth() > maxWidth) {
						maxWidth = subElement.getWidth();
						maxKnown = knownArray [i];
					}
				}

				float unknownElementHeight = (parentHeight - hAllGaps) / n;
				float scaleUnknown = unknownElementHeight / parentHeight;
				float scaleKnown = (parentHeight - numberUnknown * unknownElementHeight - hAllGaps) / totKnownHeight;

				for (int i = 0; i < elements.size(); ++ i) {
					MdCElement subElement = elements.get(i);
					float scale = knownArray [i] ? scaleKnown : scaleUnknown;
					if (scale <1) {
						subElement.scale(scale);
					}
					//scale operations
					if (subElement instanceof MdCOperation) {
						MdCOperation operation = (MdCOperation) subElement;
						fillGraphicSizes (operation, knownArray [i] ? operation.getHeight() : unknownElementHeight);
						//set max width
						operation.setMaxWidth (maxKnown ? maxWidth * scaleKnown : maxWidth * scaleUnknown);
					}
					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						MdCElement element = cartouche.getElement();
						cartouche.scale(cartouche.getHeight() / cartouche.getHeight ());
						float inScale = cartouche.getInHeight() / element.getHeight();
						if (inScale < 1) {
							element.scale(inScale);
						}
						fillGraphicSizes (element, cartouche.getInHeight());
					}
				}
			}

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.HORIZONTAL) {
				float maxLetterHeight = 0;
				//max height + sub elements
				for (MdCElement subElement : elements) {

					//get max letter height
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						if (l.getHeight() > maxLetterHeight) {
							maxLetterHeight = l.getHeight();
						}
					}

					//scale operation subelements
					if (subElement instanceof MdCOperation) {
						float hh = rootOperation.isRoot () ? parentHeight :  parentHeight - 2 * V_GAP; 
						fillGraphicSizes (subElement, hh);
					}

					//scale cartouche
					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						MdCElement el = cartouche.getElement();
//						cartouche.scale (parentHeight / cartouche.getHeight());
//						float newHeight = cartouche.getCartoucheVScale() * parentHeight;
//						float newHeight = cartouche.getCartoucheVScale() * cartouche.getHeight();
						fillGraphicSizes (el, cartouche.getInHeight());
//						fillGraphicSizes (el, newHeight);
						if (el instanceof MdCOperation) {
							((MdCOperation) el).setMaxWidth (cartouche.getInWidth());
						}
					}
				}
/*
				//scale letters with max height
				float letterScale = maxLetterHeight > parentHeight ? parentHeight / maxLetterHeight : 1;
				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						l.scale(letterScale);
					}
				}
*/
			}
		}
	}

	public static void fillGraphicPlaces (MdCElement root, float x, float y) {

		if (root instanceof MdCLetter) {
			MdCLetter rootLetter = (MdCLetter) root;
			rootLetter.setX (x);
			rootLetter.setY(y);
		}

		if (root instanceof MdCCartouche) {
			MdCCartouche cartouche = (MdCCartouche) root;
			MdCElement element = cartouche.getElement();

			cartouche.setX (x);
			cartouche.setY (y);

			float inX = cartouche.getInWidth () / 2 - element.getWidth() / 2 + cartouche.getLeftBorder ();
			float inY = cartouche.getInHeight () / 2 - element.getHeight() / 2 + cartouche.getTopBorder ();
			fillGraphicPlaces (element, cartouche.getX() + inX, cartouche.getY() + inY);
		}

		if (root instanceof MdCOperation) {
			MdCOperation rootOperation = (MdCOperation) root;
			List <MdCElement> elements = rootOperation.getSubNodes();
			int n = elements.size();

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.VERTICAL) {
				float tmpY = y;
				float maxH = rootOperation.getHeight ();
				float elH = rootOperation.getElHeight ();
				float gap = maxH > elH ? (maxH - elH) / (n - 1) : V_GAP;
				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						l.setX (x + rootOperation.getWidth() / 2 - l.getWidth() / 2);
						l.setY (tmpY);
						tmpY += l.getHeight() + gap;
					}
					if (subElement instanceof MdCOperation) {
						fillGraphicPlaces (subElement, x, tmpY);
						tmpY += ((MdCOperation) subElement).getHeight() + gap;
					}
					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						MdCElement element = cartouche.getElement();
						cartouche.setX (x + rootOperation.getWidth() / 2 - cartouche.getWidth() / 2);
						cartouche.setY (tmpY);
						float inX = cartouche.getInWidth () / 2 - element.getWidth() / 2 + cartouche.getLeftBorder ();
						float inY = cartouche.getInHeight () / 2 - element.getHeight() / 2 + cartouche.getTopBorder ();
						fillGraphicPlaces (element, cartouche.getX() + inX, cartouche.getY() + inY);
//						fillGraphicPlaces (element, x + inX, tmpY + inY);
						tmpY += cartouche.getHeight() + gap;
					}
				}
			}

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.HORIZONTAL) {
				float tmpX = x;
				float opWidth = rootOperation.getWidth();
				float opElWidth = rootOperation.getSumHElWidth();
				float gap = opWidth > opElWidth ? (opWidth - opElWidth) / (n - 1) : H_GAP;
				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						l.setX(tmpX);
						l.setY(y + rootOperation.getHeight() / 2 - l.getHeight() / 2);
						tmpX += l.getWidth() + gap;
					}
					if (subElement instanceof MdCOperation) {
						fillGraphicPlaces (subElement, tmpX, y);
						tmpX += subElement.getWidth() + gap;
					}

					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						MdCElement element = cartouche.getElement();
						cartouche.setX (tmpX);
						cartouche.setY (y + rootOperation.getHeight() / 2 - cartouche.getHeight() / 2);
						float inX = cartouche.getInWidth () / 2 - element.getWidth() / 2 + cartouche.getLeftBorder ();
						float inY = cartouche.getInHeight () / 2 - element.getHeight() / 2 + cartouche.getTopBorder ();
//						Log.i("TAG", String.format("width: in=%f el=%f", cartouche.getInWidth (), element.getWidth()));
//						Log.i("TAG", String.format("height: in=%f el=%f", cartouche.getInHeight (), element.getHeight()));
						fillGraphicPlaces (element, cartouche.getX() + inX, cartouche.getY() + inY);
						tmpX += subElement.getWidth() + gap;
					}
				}
			}
			
		}
	}

}
