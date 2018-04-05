package com.naens.mdctools.mdcgraphics;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.util.SparseArray;

import com.naens.mdctools.mdcstruct.MdCCartouche;
import com.naens.mdctools.mdcstruct.MdCElement;
import com.naens.mdctools.mdcstruct.MdCLetter;
import com.naens.mdctools.mdcstruct.MdCOperation;

public class MdCGraphicConverter {

	public static final float V_GAP = 2;
	public static final float H_GAP = 2;

/*
 * 			+ Percents
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
			if (element instanceof MdCOperation) {
				((MdCOperation) element).setMaxWidth (cartouche.getInWidth());
			}
		}

		if (root instanceof MdCOperation) {
			MdCOperation rootOperation = (MdCOperation) root;
			List <MdCElement> elements = rootOperation.getSubNodes();
			int n = elements.size();
			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.VERTICAL) {
				if (rootOperation.isRoot() && parentHeight > rootOperation.getHeight()) {
					rootOperation.setMaxHeight(parentHeight);
				}

				float hAllGaps = (n - 1) * V_GAP;
				float totKnownHeight = 0;
				float maxWidth = 0;
				int numberUnknown = 0;
				boolean maxKnown = false;
				boolean [] knownArray = new boolean [elements.size()];
				SparseArray <MdCLetter> verticalAroundLetters = new SparseArray <MdCLetter> ();

				for (int i = 0; i < elements.size(); ++ i) {
					MdCElement subElement = elements.get(i);
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						if (l.aroundInfo().isAroundVertical()) {
							verticalAroundLetters.put(i, l);
							totKnownHeight += l.aroundInfo().getAroundLetterPartHeight();
						} else {
							totKnownHeight += l.getHeight();
						}
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
				if (scaleUnknown > 1) {
					scaleUnknown = 1;
				}
				float scaleKnown = (parentHeight - numberUnknown * unknownElementHeight - hAllGaps) / totKnownHeight;
				if (scaleKnown > 1) {
					scaleKnown = 1;
				}

				int key = 0;
				int nVerticalAround = verticalAroundLetters.size();
				//remove arounds if not needed!
				if (totKnownHeight + hAllGaps < parentHeight && numberUnknown == 0) {
					for(int i = 0; i < nVerticalAround; i++) {
						key = verticalAroundLetters.keyAt(i);
					   // 	get the object by the key.
						MdCLetter verticalAroundLetter = verticalAroundLetters.get(key);
						LetterAroundInformation letterAround = verticalAroundLetter.aroundInfo();
						if (totKnownHeight + hAllGaps + letterAround.getAroundInsideHeight() < parentHeight) {
							totKnownHeight += verticalAroundLetter.getHeight();
							letterAround.setAroundFull(true);
						} else {
							break;
						}
					}
				}

				//add in elements to around letters
				int additionalHeight = 0;
				int nChange = 0;
				float maxAroundShift = 0;
				Set <MdCElement> takenElements = new HashSet<MdCElement>();
				for(int i = 0; i < nVerticalAround; i++) {
				   key = verticalAroundLetters.keyAt(i);
				   // get the object by the key.
				   MdCLetter verticalAroundLetter = verticalAroundLetters.get(key);
				   LetterAroundInformation letterAround = verticalAroundLetter.aroundInfo();
				   if (letterAround.isAroundFull()) {
					   continue;		//letter already done, is inside around...
				   }
				   float sideShift = letterAround.getAroundLetterPartWidth() * scaleKnown;				//letter.getWidth() - inRect.width();
				   float aroundInsideWidth = letterAround.getAroundInsideWidth() * scaleKnown;			//inRect.width() + gaps;
				   int sumH = 0;
				   float sumHScaled = 0;
				   int nInElements = 0;
				   int nUnknown = 0;
				   int limit;
				   int maxi = -1;
				   int step;
				   if (letterAround.isAroundYBefore()) {
					   limit = i >= nVerticalAround - 1 ? elements.size() - 1 : verticalAroundLetters.keyAt(i + 1) - 1;
					   //ensure empty around letter
					   if (i < nVerticalAround - 1 && verticalAroundLetters.keyAt(i + 1) - key == 1) {
						   limit += 1;
					   }
					   step = 1;
				   } else {
					   limit = i <= 0 ? 0 : verticalAroundLetters.keyAt(i - 1) + 1;
					   //ensure empty around letter
					   if (i > 0 && verticalAroundLetters.keyAt(i - 1) - key == -1) {
						   limit -= 1;
					   }
					   step = -1;
				   }
				   if (key == limit) {
					   letterAround.setAroundFull (true);
				   }
				   for (int t = key + step; t != limit + step; t += step) {
					   MdCElement e = elements.get(t);
					   float scale = knownArray [t] ? scaleKnown : scaleUnknown;
					   float elementScaledWidth = e.getWidth() * scale;
					   float elementScaledHeight = knownArray [t] ? e.getHeight() * scaleKnown : unknownElementHeight;
					   float elementUnscaledHeight = knownArray [t] ? e.getHeight() : parentHeight;
					   if (letterAround.isAroundLeftRight() && elementScaledWidth > aroundInsideWidth - 2*H_GAP ||
							   (elementScaledWidth * 1.5 > aroundInsideWidth - 2*H_GAP) && 
							   (letterAround.isAroundYBefore() && letterAround.isRight() 
									   || letterAround.isAroundYAfter() && letterAround.isLeft()) 
							|| takenElements.contains(e)) {
						   if (t == key + step) {
							   letterAround.setAroundFull (true);
						   }
						   break;//DO NOT ADD ELEMENT!!!
						   //+ do not add other elements
					   }
					   sumH += elementUnscaledHeight;
					   sumHScaled += elementScaledHeight;
					   letterAround.addAroundElement(e);
					   takenElements.add(e);
					   if (e instanceof MdCLetter && ((MdCLetter) e).aroundInfo().isAroundVertical()) {
						   ((MdCLetter) e).aroundInfo().setAroundFull (true);
						   additionalHeight += ((MdCLetter) e).aroundInfo().getAroundInsideHeight();
					   }
					   ++ nInElements;
					   if (!knownArray [t]) {
						   ++ nUnknown;
					   }
					   if (elementScaledWidth > aroundInsideWidth) {
						   aroundInsideWidth = elementScaledWidth;
						   maxi = i;
//						   letterAround.setInsideElementsWidth (e.getWidth());
					   }
					   if (sumHScaled + nInElements * V_GAP >= letterAround.getAroundInsideHeight() * scaleKnown) {
						   letterAround.setAroundFull (true);
						   break;
					   }
				   }
				   if (maxi != -1 && aroundInsideWidth + sideShift > (maxKnown ? maxWidth * scaleKnown : maxWidth * scaleUnknown)) {
					   maxWidth = aroundInsideWidth / (knownArray [maxi] ? scaleKnown : scaleUnknown);
					   maxKnown = knownArray [i];
					   maxAroundShift = sideShift / scaleKnown;
				   }
				   if (!letterAround.isAroundFull () || letterAround.isAroundEmpty()) {
					   additionalHeight += letterAround.getAroundInsideHeight() - sumH;
					   nChange -= nInElements;
					   numberUnknown -= nUnknown;
				   }
				}

				//update
				n += nChange;
				hAllGaps = (n - 1) * V_GAP;
				totKnownHeight += additionalHeight;

				unknownElementHeight = (parentHeight - hAllGaps) / n;
				scaleUnknown = unknownElementHeight / parentHeight;
				if (scaleUnknown > 1) {
					scaleUnknown = 1;
				}
				scaleKnown = (parentHeight - numberUnknown * unknownElementHeight - hAllGaps) / totKnownHeight;
				if (scaleKnown > 1) {
					scaleKnown = 1;
				}

				float tmpw = (maxKnown ? maxWidth * scaleKnown : maxWidth * scaleUnknown) + maxAroundShift * scaleKnown + (maxAroundShift == 0 ? 0 : H_GAP);

				for (int i = 0; i < elements.size(); ++ i) {
					MdCElement subElement = elements.get(i);
					float scale = knownArray [i] ? scaleKnown : scaleUnknown;
					if (scale < 1) {
						subElement.scale(scale);
					}
					//scale operations
					if (subElement instanceof MdCOperation) {
						MdCOperation subOperation = (MdCOperation) subElement;
						fillGraphicSizes (subOperation, knownArray [i] ? subOperation.getHeight() : unknownElementHeight);
						if (subOperation.getAroundLetter() != null) {
							LetterAroundInformation aroundLetter = subOperation.getAroundLetter().aroundInfo();
							float tmpscale = aroundLetter.isAroundYAfter() ? scaleKnown : 1;
							if (aroundLetter.isAroundLeftRight()) {
								subOperation.setMaxWidth (aroundLetter.getAroundInsideWidth() * tmpscale - 2 * H_GAP);
							} else if (aroundLetter.getInsideElementsWidth() < aroundLetter.getAroundInsideWidth()) {
								subOperation.setMaxWidth (aroundLetter.getAroundInsideWidth() * tmpscale - H_GAP);
							} else {
								subOperation.setMaxWidth (tmpw - aroundLetter.getAroundLetterPartWidth() * tmpscale - H_GAP);
							}
						} else {
							//set max width
							subOperation.setMaxWidth (tmpw);
						}
					}
					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						MdCElement element = cartouche.getElement();
						float inScale = cartouche.getInHeight() / cartouche.getHeight();
						if (inScale < 1) {
							element.scale(inScale);
						}
						fillGraphicSizes (element, cartouche.getInHeight());
					}
				} 
				if (tmpw < rootOperation.getWidth()) {
					tmpw = rootOperation.getWidth();
				}
				rootOperation.setMaxWidth(tmpw);
			}

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.HORIZONTAL) {
				rootOperation.setMaxHeight(parentHeight);

				//find around letters
				List <Integer> arounds = new LinkedList <Integer>();
				for (int i = 0; i < elements.size(); ++i) {
					MdCElement subElement = elements.get(i);
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						LetterAroundInformation ai = l.aroundInfo();
						if (ai != null && ai.isAroundHorizontal()) {
							arounds.add(i);
						}
					}
				}

				//fill around letters
				Set <MdCElement> takenElements = new HashSet<MdCElement>();
				for (int i = 0; i < arounds.size(); ++i) {
					int ar = arounds.get(i);
					MdCLetter aroundLetter = (MdCLetter) elements.get(ar);
					LetterAroundInformation ai = aroundLetter.aroundInfo();
					int last;
					int step;
					if (ai.isAroundXBefore()) {	//go ->
						step = 1;
						last = i == arounds.size() - 1 ? elements.size() - 1 : arounds.get(i + 1) - 1;
					} else {	//go <-
						step = -1;
						last = i == 0 ? 0 : arounds.get(i - 1) + 1;
					}
					if (ar == last) {
						ai.setAroundFull(true);
						continue;
					}
					float aroundLetterScale = parentHeight < aroundLetter.getHeight() ? parentHeight / aroundLetter.getHeight() : 1;
					float inWidthScaled = ai.getAroundInsideWidth() * aroundLetterScale;
					float lphScaled = ai.getAroundLetterPartHeight() * aroundLetterScale;
					float sumElWidth = 0;
					for (int k = ar + step; k != last + step; k += step) {
						
						float scale = 1;
						MdCElement insideElement = elements.get (k);
						float inElementWidth = insideElement.getWidth();
						float availableH = parentHeight - lphScaled - (ai.isAroundTopBottom() ? 2*V_GAP : V_GAP);
						if (insideElement instanceof MdCLetter) {
							MdCLetter insideLetter = (MdCLetter) insideElement;
							scale = parentHeight < insideLetter.getHeight() ? parentHeight / insideLetter.getHeight() : 1;
						}
						if (takenElements.contains(insideElement) 
								|| availableH < 4 + 2*V_GAP 
								|| availableH * 1.75 < insideElement.getHeight() && insideElement instanceof MdCLetter
								|| inWidthScaled * 2.25 < sumElWidth + inElementWidth * scale) {
							ai.setAroundFull(sumElWidth == 0);
							break;
						}
						if (availableH < insideElement.getHeight()) {
							scale *= availableH / insideElement.getHeight();
						}
						sumElWidth += insideElement.getWidth() * scale;
						ai.addAroundElement(insideElement);
						takenElements.add(insideElement);
						if (sumElWidth > inWidthScaled){
							ai.setAroundFull(true);
							break;
						}
					}
				}

				//max height + sub elements
				for (MdCElement subElement : elements) {
					MdCLetter aroundLetter = subElement.getAroundLetter();
					float availableH = parentHeight;
					if (aroundLetter != null) {
						LetterAroundInformation ai = aroundLetter.aroundInfo();
						float aroundLetterScale = parentHeight < aroundLetter.getHeight() ? parentHeight / aroundLetter.getHeight() : 1;
						float lphScaled = ai.getAroundLetterPartHeight() * aroundLetterScale;
						availableH -= lphScaled + (ai.isAroundTopBottom() ? 2*V_GAP : V_GAP);
					}

					//get max letter height
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						float elH = l.getHeight();
						float scale = availableH / elH;
						if (elH > availableH) {
							l.scale(scale);
						}
					}

					//scale operation subelements
					if (subElement instanceof MdCOperation) {
						((MdCOperation) subElement).setMaxHeight(parentHeight);
						float hh = rootOperation.isRoot () ? availableH :  availableH - 2 * V_GAP; 
						fillGraphicSizes (subElement, hh);
					}

					//scale cartouche
					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						float elH = cartouche.getHeight();
						float scale = availableH / elH;
						if (elH > availableH) {
							cartouche.scale(scale);
						}
						MdCElement el = cartouche.getElement();
						fillGraphicSizes (el, cartouche.getInHeight());
						if (el instanceof MdCOperation) {
							((MdCOperation) el).setMaxWidth (cartouche.getInWidth());
						}
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

			float inX = cartouche.getInWidth () / 2 - element.getWidth() / 2 + cartouche.getLeftBorder ();
			float inY = cartouche.getInHeight () / 2 - element.getHeight() / 2 + cartouche.getTopBorder ();
			fillGraphicPlaces (element, cartouche.getX() + inX, cartouche.getY() + inY);
		}

		if (root instanceof MdCOperation) {
			MdCOperation rootOperation = (MdCOperation) root;
			List <MdCElement> elements = rootOperation.getSubNodes();
			int n = rootOperation.getEffectiveNumberOfElements();

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.VERTICAL) {
				float tmpY = y;
				float maxH = rootOperation.getHeight ();
				float elH = rootOperation.getElHeight ();
				float gap = maxH > elH ? (maxH - elH) / (n - 1) : V_GAP;
//				float aroundPlusH = maxH > elH ? maxH - elH : 0;
				for (MdCElement subElement : elements) {
					MdCLetter aroundLetter = subElement.getAroundLetter ();
					LetterAroundInformation aroundInfo = aroundLetter != null ? aroundLetter.aroundInfo() : null;
					float gap2 = (aroundInfo != null && !aroundInfo.isAroundFull() ? aroundInfo.getAroundVGap() : gap);
					if (aroundInfo != null && aroundInfo.isAroundYAfter() && subElement.getAroundOrder() == 0
							&& !aroundInfo.isAroundFull()) {
						tmpY += gap2;
					}
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						float letterWidth = l.getWidth();
						float rootWidth = rootOperation.getWidth();
						float shiftX = aroundInfo == null ? 0 : aroundInfo.getAroundVerticalShiftX ();		//shift if inside around letter
						if (!l.aroundInfo().isAroundEmpty() && (l.aroundInfo().isAroundYBefore() || l.aroundInfo().isAroundYAfter())) {
							//is vertical around letter!
							float letterShiftX = l.aroundInfo().getAroundVerticalLetterShiftX ();
							l.setX (x + rootWidth / 2 - letterWidth / 2 + letterShiftX + shiftX);
							float aroundGap = l.aroundInfo().getAroundVGap();
							float shiftY = l.aroundInfo().isAroundYAfter() ? l.aroundInfo().getAroundInsideHeight() : 0;
							l.setY (tmpY - shiftY);
							tmpY += l.aroundInfo().getAroundLetterPartHeight() + aroundGap - shiftY;
						} else {
							l.setX (x + rootWidth / 2 - letterWidth / 2 + shiftX);
							l.setY (tmpY);
							tmpY += l.getHeight() + gap2;
						}
					}
					if (subElement instanceof MdCOperation) {
						MdCOperation subOperation = (MdCOperation) subElement;
						fillGraphicPlaces (subElement, x + (aroundInfo == null ? 0 : H_GAP + aroundInfo.getAroundLeftInsideWidth()), tmpY);
						tmpY += subOperation.getHeight() + gap2;
					}
					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						MdCElement element = cartouche.getElement();
						cartouche.setX (x + rootOperation.getWidth() / 2 - cartouche.getWidth() / 2 + (aroundInfo == null ? 0 : H_GAP + aroundInfo.getAroundLeftInsideWidth()));
						cartouche.setY (tmpY);
						float inX = cartouche.getInWidth () / 2 - element.getWidth() / 2 + cartouche.getLeftBorder ();
						float inY = cartouche.getInHeight () / 2 - element.getHeight() / 2 + cartouche.getTopBorder ();
						fillGraphicPlaces (element, cartouche.getX() + inX, cartouche.getY() + inY);
//						fillGraphicPlaces (element, x + inX, tmpY + inY);
						tmpY += cartouche.getHeight() + gap2;
					}
				}
			}

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.HORIZONTAL) {
				float tmpX = x;
				float opWidth = rootOperation.getWidth();
				float opElWidth = rootOperation.getSumHElWidth();
				float gap = opWidth > opElWidth ? (opWidth - opElWidth) / (n - 1) : H_GAP;
				for (MdCElement subElement : elements) {
					MdCLetter aroundLetter = subElement.getAroundLetter();
					LetterAroundInformation alai = aroundLetter == null ? null : aroundLetter.aroundInfo();
					float aroundYShift = alai != null && alai.isAroundHorizontal() && alai.getTop () != 0 ? alai.getTop () + V_GAP : 0;
					float availableH = rootOperation.getHeight();
					if (alai != null && alai.isAroundHorizontal()) {
						availableH -= alai.getAroundLetterPartHeight() - (alai.isAroundTopBottom() ? 2*V_GAP : V_GAP);
					}
					if (aroundLetter != null && alai.isAroundHorizontal()) {
						gap = !alai.isAroundFull() ? alai.getAroundHGap() : gap;
					}

					if (alai != null && alai.isAroundXAfter() && subElement.getAroundOrder() == 0 && !alai.isAroundFull()) {
						tmpX += gap;
					}
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						LetterAroundInformation ai = l.aroundInfo();
						gap = ai != null && ai.isAroundHorizontal() && !ai.isAroundFull() ? ai.getAroundHGap() : gap;
						float shiftAroundLetterX = ai != null && !ai.isAroundEmpty() && ai.isAroundHorizontal() && ai.isAroundXAfter() ? ai.getAroundInsideWidth() : 0; 
						l.setX(tmpX - shiftAroundLetterX);
						l.setY(y + availableH / 2 - l.getHeight() / 2 + aroundYShift);
						tmpX += (ai != null && ai.isAroundHorizontal() && !ai.isAroundEmpty() ? ai.getAroundLetterPartWidth() : l.getWidth()) + gap;
					}
					if (subElement instanceof MdCOperation) {
						fillGraphicPlaces (subElement, tmpX, y + aroundYShift);
						tmpX += subElement.getWidth() + gap;
					}

					if (subElement instanceof MdCCartouche) {
						MdCCartouche cartouche = (MdCCartouche) subElement;
						MdCElement element = cartouche.getElement();
						cartouche.setX (tmpX);
						cartouche.setY (y + availableH / 2 - cartouche.getHeight() / 2 + aroundYShift);
						float inX = cartouche.getInWidth () / 2 - element.getWidth() / 2 + cartouche.getLeftBorder ();
						float inY = cartouche.getInHeight () / 2 - element.getHeight() / 2 + cartouche.getTopBorder ();
						fillGraphicPlaces (element, cartouche.getX() + inX, cartouche.getY() + inY);
						tmpX += subElement.getWidth() + gap;
					}
				}
			}

		}
	}

}
