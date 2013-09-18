package com.naens.tools.mdctools.mdcgraphics;

import java.util.List;

import android.util.Log;

import com.naens.tools.mdctools.mdcstruct.MdCElement;
import com.naens.tools.mdctools.mdcstruct.MdCLetter;
import com.naens.tools.mdctools.mdcstruct.MdCOperation;
import com.naens.tools.mdctools.mdcstruct.MdCCartouche;

public class MdCGraphicConverter {

	public static final float V_GAP = 2;
	public static final float H_GAP = 2;

/*
 * 			TODO:	- Around right/left letters
 * 					- Rotate
 * 					+ Percents
 * 					+ right -> left, top -> bottom
 */

	public static void fillGraphicSizes (MdCElement root, float scale, float maxHeight) {

		if (root instanceof MdCLetter) {
			MdCLetter rootLetter = (MdCLetter) root;

			Log.i("TAG", String.format ("info.getHeight() = %f, maxHeight = %f", rootLetter.getUnscaledHeight(), maxHeight));
			if (rootLetter.getUnscaledHeight() >= maxHeight) {
				rootLetter.scale(maxHeight / rootLetter.getUnscaledHeight());
			}
		}
	
		if (root instanceof MdCCartouche) {
			MdCCartouche cartouche = (MdCCartouche) root;
			cartouche.scale (maxHeight / cartouche.getUnscaledHeight());
			float cartoucheScale = cartouche.getCartoucheVScale();
			fillGraphicSizes (cartouche.getElement(), cartoucheScale, cartoucheScale * maxHeight);
		}

		if (root instanceof MdCOperation) {
			MdCOperation rootOperation = (MdCOperation) root;
			rootOperation.setMaxHeight (maxHeight);
			List <MdCElement> elements = rootOperation.getSubNodes();
			int n = elements.size();
			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.VERTICAL) {

				//Letters
				float sumHLetters = 0;
				float maxWidth = 0;
				int nOp = 0;
				MdCElement maxElement = null;

				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						sumHLetters += l.getUnscaledHeight();
						if (subElement.getUnscaledWidth() > maxWidth) {
							maxWidth = subElement.getUnscaledWidth();
							maxElement = subElement;
						}
					} else {
						++ nOp;
					}
				}

				//Scale Operations and Cartouches
				float hAllGaps = (n - 1) * V_GAP;
				float operationsHeight = (maxHeight - hAllGaps) / n;
				float scaleOperations = operationsHeight / maxHeight;
				for (MdCElement subElement : elements) {
					//scale operations
					if (subElement instanceof MdCOperation) {
						fillGraphicSizes (subElement, scaleOperations, operationsHeight);
						if (subElement.getUnscaledWidth() > maxWidth) {	//max element width
							maxWidth = subElement.getUnscaledWidth();
							maxElement = subElement;
						}
					}
					//scale cartouches
					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						cartouche.scale (scaleOperations);
						float cartoucheScale = cartouche.getCartoucheVScale();
						fillGraphicSizes (cartouche.getElement(), 
								scaleOperations * cartoucheScale, cartoucheScale * operationsHeight);
						if (cartouche.getUnscaledWidth() > maxWidth) {
							maxWidth = subElement.getUnscaledWidth();
							maxElement = subElement;
						}
					}
				}

				//scale Letters
				float scaleLetters = (maxHeight - nOp * operationsHeight - hAllGaps) / sumHLetters; 
				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCOperation) {
						//set max width
						if (maxElement instanceof MdCLetter) {
							((MdCOperation) subElement).setMaxWidth (maxWidth * scaleLetters);
						}
						if (maxElement instanceof MdCOperation) {
							((MdCOperation) subElement).setMaxWidth (maxWidth * scaleOperations);
						}
						if (maxElement instanceof MdCCartouche) {
							((MdCOperation) subElement).setMaxWidth (maxWidth * scaleOperations);
						}
					}
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						l.scale(scaleLetters);
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
						if (l.getUnscaledHeight() > maxLetterHeight) {
							maxLetterHeight = l.getUnscaledHeight();
						}
					}

					//scale operation subelements
					if (subElement instanceof MdCOperation) {
						float hh = rootOperation.isRoot () ? maxHeight :  maxHeight - 2 * V_GAP; 
						fillGraphicSizes (subElement, scale, hh);
					}

					//scale cartouche
					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						cartouche.scale (cartouche.getUnscaledHeight() / maxHeight);
						float newScale = scale * cartouche.getCartoucheVScale();
						float newHeight = newScale * maxHeight;
						fillGraphicSizes (cartouche.getElement(), newScale, newHeight);
					}
				}

				//scale letters with max height
				float letterScale = maxLetterHeight > maxHeight ? maxHeight / maxLetterHeight : 1;
				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						l.scale(letterScale);
					}
				}
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

			float inX = cartouche.getInWidth () / 2 - element.getScaledWidth() / 2 + cartouche.getLeftBorder ();
			float inY = cartouche.getInHeight () / 2 - element.getScaledHeight() / 2 + cartouche.getTopBorder ();
			fillGraphicPlaces (element, cartouche.getX() + inX, cartouche.getY() + inY);
		}

		if (root instanceof MdCOperation) {
			MdCOperation rootOperation = (MdCOperation) root;
			List <MdCElement> elements = rootOperation.getSubNodes();
			int n = elements.size();

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.VERTICAL) {
				float tmpY = y;
				float maxH = rootOperation.getScaledHeight ();
				float elH = rootOperation.getElHeight ();
				float gap = maxH > elH ? (maxH - elH) / (n - 1) : V_GAP;
				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						l.setX (x + rootOperation.getScaledWidth() / 2 - l.getScaledWidth() / 2);
						l.setY (tmpY);
						tmpY += l.getScaledHeight() + gap;
					}
					if (subElement instanceof MdCOperation) {
						fillGraphicPlaces (subElement, x, tmpY);
						tmpY += ((MdCOperation) subElement).getScaledHeight() + gap;
					}
					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						MdCElement element = cartouche.getElement();
						cartouche.setX (x + rootOperation.getScaledWidth() / 2 - cartouche.getScaledWidth() / 2);
						cartouche.setY (tmpY);
						float inX = cartouche.getInWidth () / 2 - element.getScaledWidth() / 2 + cartouche.getLeftBorder ();
						float inY = cartouche.getInHeight () / 2 - element.getScaledHeight() / 2 + cartouche.getTopBorder ();
						fillGraphicPlaces (element, cartouche.getX() + inX, cartouche.getY() + inY);
//						fillGraphicPlaces (element, x + inX, tmpY + inY);
						tmpY += cartouche.getScaledHeight() + gap;
					}
				}
			}

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.HORIZONTAL) {
				float tmpX = x;
				float opWidth = rootOperation.getScaledWidth();
				float opElWidth = rootOperation.getSumHElWidth();
				float gap = opWidth > opElWidth ? (opWidth - opElWidth) / (n - 1) : H_GAP;
				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						l.setX(tmpX);
						l.setY(y + rootOperation.getScaledHeight() / 2 - l.getScaledHeight() / 2);
						tmpX += l.getScaledWidth() + gap;
					}
					if (subElement instanceof MdCOperation) {
						fillGraphicPlaces (subElement, tmpX, y);
						tmpX += subElement.getScaledWidth() + gap;
					}

					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						MdCElement element = cartouche.getElement();
						cartouche.setX (tmpX);
						cartouche.setY (y + rootOperation.getScaledHeight() / 2 - cartouche.getScaledHeight() / 2);
						float inX = cartouche.getInWidth () / 2 - element.getScaledWidth() / 2 + cartouche.getLeftBorder ();
						float inY = cartouche.getInHeight () / 2 - element.getScaledHeight() / 2 + cartouche.getTopBorder ();
						Log.i("TAG", String.format("width: in=%f el=%f", cartouche.getInWidth (), element.getScaledWidth()));
						Log.i("TAG", String.format("height: in=%f el=%f", cartouche.getInHeight (), element.getScaledHeight()));
						fillGraphicPlaces (element, cartouche.getX() + inX, cartouche.getY() + inY);
						tmpX += subElement.getScaledWidth() + gap;
					}
				}
			}
			
		}
	}

}
