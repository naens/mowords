package com.naens.tools.mdctools.mdcgraphics;

import java.util.List;

import android.graphics.Typeface;

import com.naens.tools.mdctools.mdcstruct.MdCElement;
import com.naens.tools.mdctools.mdcstruct.MdCLetter;
import com.naens.tools.mdctools.mdcstruct.MdCOperation;

public class MdCGraphicConverter {

	public static final float V_GAP = 2;
	public static final float H_GAP = 2;

	private MdCFontLetters mdcFontLetters;

	public void fillGraphicSizes (MdCElement root, float scale, float maxHeight) {
		if (root instanceof MdCLetter) {
			MdCLetter rootLetter = (MdCLetter) root;
			rootLetter.setGraphics(mdcFontLetters);
			MdCGraphicLetterInfo info = rootLetter.getInfo();

			if (info.getHeight() >= maxHeight) {
				info.setScale(info.getHeight() / maxHeight);
			}
		}

		if (root instanceof MdCOperation) {
			MdCOperation rootOperation = (MdCOperation) root;
			List <MdCElement> elements = rootOperation.getSubNodes();
			int n = elements.size();
			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.VERTICAL) {
				float d = (maxHeight - (n - 1) * V_GAP) / n;
				float sumHBigLetters = 0;
				float sumHSmallLetters = 0;
				float maxWidth = 0;
				int nSmall = 0;
				int nBig = 0;
				for (MdCElement subElement : elements) {
					if (subElement.getWidth() > maxWidth) {
						maxWidth = subElement.getWidth();
					}
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						l.setGraphics(mdcFontLetters);
						if (l.getInfo().getHeight() > d) { //big letters
							sumHBigLetters += l.getInfo().getHeight();
							++ nBig;
						} else {
							sumHSmallLetters += l.getInfo().getHeight();
							++ nSmall;
						}
					}
				}
				rootOperation.setMaxWidth (maxWidth);
				float operationsHeight = (maxHeight - sumHSmallLetters - (n - 1) * V_GAP) / (n - nSmall);
				float scaleOperations = operationsHeight / maxHeight;
				float scaleBig = (nBig * operationsHeight) / sumHBigLetters;

				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						MdCGraphicLetterInfo info = l.getInfo();
						if (info.getHeight() > d) { //big letters
							info.scale(scaleBig);
						}
					}
					if (subElement instanceof MdCOperation) {
						fillGraphicSizes (subElement, scaleOperations, operationsHeight);
					}
				}
			}

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.HORIZONTAL) {
				float maxLetterHeight = 0;
				for (MdCElement subElement : elements) {

					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						l.setGraphics(mdcFontLetters);
						MdCGraphicLetterInfo info = l.getInfo();
						if (info.getHeight() > maxLetterHeight) {
							maxLetterHeight = info.getHeight();
						}
					}
					if (subElement instanceof MdCOperation) {
						fillGraphicSizes (subElement, scale, maxHeight - 2 * V_GAP);
						MdCOperation o = (MdCOperation) subElement;
						o.setMaxHeight (maxHeight - 2 * V_GAP);
					}
				}

				float letterScale = maxLetterHeight > maxHeight ? maxHeight / maxLetterHeight : 1;

				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						MdCGraphicLetterInfo info = l.getInfo();
						info.scale(letterScale);
					}
				}
			}
		}
	}

	public void fillGraphicPlaces (MdCElement root, float x, float y) {

		if (root instanceof MdCLetter) {
			MdCLetter rootLetter = (MdCLetter) root;
			MdCGraphicLetterInfo info = rootLetter.getInfo();
			info.setX(x);
			info.setY(y);
		}


		if (root instanceof MdCOperation) {
			MdCOperation rootOperation = (MdCOperation) root;
			List <MdCElement> elements = rootOperation.getSubNodes();
			int n = elements.size();

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.VERTICAL) {
				float tmpY = y;
				float gap = rootOperation.getHeight () > rootOperation.getElHeight() ? rootOperation.getHeight () / n : V_GAP;
				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						MdCGraphicLetterInfo info = l.getInfo();
						info.setX(x + rootOperation.getWidth() / 2 - info.getWidth() / 2);
						info.setY(tmpY);
						tmpY += info.getHeight() + gap;
					}
					if (subElement instanceof MdCOperation) {
						fillGraphicPlaces (subElement, x, tmpY);
						tmpY += ((MdCOperation) subElement).getHeight() + gap;
					}
				}
			}

			if (rootOperation.getDirection() == MdCOperation.SubNodesDirection.HORIZONTAL) {
				float tmpX = x;
				float gap = rootOperation.getWidth() > rootOperation.getElWidth() ? rootOperation.getWidth() / n : H_GAP;
				for (MdCElement subElement : elements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter l = (MdCLetter) subElement;
						MdCGraphicLetterInfo info = l.getInfo();
						info.setX(tmpX);
						info.setY(y + rootOperation.getHeight() / 2 - info.getHeight() / 2);
						tmpX += info.getWidth() + gap;
					}
					if (subElement instanceof MdCOperation) {
						fillGraphicPlaces (subElement, tmpX, y);
						tmpX += subElement.getWidth() + gap;
					}
				}
			}
			
		}
	}

	public MdCGraphicConverter(Typeface typeface, int fontSize) {
		mdcFontLetters = new MdCFontLetters(fontSize, typeface);
	}

}
