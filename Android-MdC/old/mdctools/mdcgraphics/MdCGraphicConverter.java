package com.naens.tools.mdctools.mdcgraphics;

import java.util.LinkedList;
import java.util.List;

import android.graphics.Typeface;

import com.naens.tools.mdctools.mdcstruct.MdCElement;
import com.naens.tools.mdctools.mdcstruct.MdCLetter;
import com.naens.tools.mdctools.mdcstruct.MdCOperation;

public class MdCGraphicConverter {

	private static float vGap = 2;
	private static float hGap = 2;
	public static List <MdCGraphicLetter> mdcToGraphic (Typeface f, MdCElement e, int x, int y, float scale, int height,
			int textSize) {
		List <MdCGraphicLetter> result = new LinkedList <MdCGraphicLetter> ();

		if (e instanceof MdCLetter) {
			MdCLetter letter = (MdCLetter) e;
			MdCGraphicLetter graphicLetter = new MdCGraphicLetter(letter.getCharacter(), x, y, scale, textSize, f);
			if (graphicLetter.getHeight() > height) {
				float newScale = height / graphicLetter.getHeight();
				graphicLetter.setScale (newScale);
				//TODO x,y
			}
			result.add(graphicLetter);
		}

		if (e instanceof MdCOperation) {
			MdCOperation operation = (MdCOperation) e;
			List <MdCElement> subElements = operation.getSubNodes();
			if (operation.getDirection () == MdCOperation.SubNodesDirection.HORIZONTAL) {
				float elementX = x;
				float elementY = y;
				for (MdCElement subElement : subElements) {
					if (subElement instanceof MdCLetter) {
						MdCLetter subletter = (MdCLetter) subElement;
						MdCGraphicLetter graphicLetter = new MdCGraphicLetter(subletter.getCharacter(),
								elementX, elementY, scale, textSize, f);
						if (graphicLetter.getHeight() > height) {
							float newScale = height / graphicLetter.getHeight();
							graphicLetter.setScale (newScale);
							//TODO x,y
						}
					}
					if (subElement instanceof MdCOperation) {
						MdCOperation subOperation = (MdCOperation) subElement;
						if (subOperation.getDirection () == MdCOperation.SubNodesDirection.HORIZONTAL) {
							throw new IllegalStateException("wrong direction");
						}
						if (subOperation.getDirection () == MdCOperation.SubNodesDirection.VERTICAL) {
							//TODO x, y
							List <MdCGraphicLetter> subLetters = mdcToGraphic(f, subOperation, x, y, scale, height, textSize);
							//TODO => width ???
							result.addAll(subLetters);
						}
					}
				}
			}

			if (operation.getDirection () == MdCOperation.SubNodesDirection.VERTICAL) {
				
			}
		}
		
		return result;
	}

}
