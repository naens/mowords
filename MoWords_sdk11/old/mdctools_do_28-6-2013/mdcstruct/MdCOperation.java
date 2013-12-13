package com.naens.tools.mdctools.mdcstruct;

import java.util.ArrayList;
import java.util.List;

import com.naens.tools.mdctools.mdcgraphics.MdCFontLetters;
import com.naens.tools.mdctools.mdcgraphics.MdCGraphicConverter;


public class MdCOperation extends MdCElement {

	public enum SubNodesDirection {HORIZONTAL, VERTICAL}

	private SubNodesDirection direction;

	private List <MdCElement> subNodes = new ArrayList <MdCElement> (5);

	private Float maxWidth;

	private Float maxHeight;

	public MdCOperation(SubNodesDirection direction) {
		super();
		this.direction = direction;
	}

	public SubNodesDirection getDirection() {
		return direction;
	}

	public List<MdCElement> getSubNodes() {
		return subNodes;
	}

	public void addSubNode (MdCElement node) {
		subNodes.add(node);
	}

	@Override
	public String toString() {
		String op = direction == null ? "0" : direction == SubNodesDirection.HORIZONTAL ? "*" : ":";
		String result = "";
		for (MdCElement subNode : subNodes) {
			if (subNode instanceof MdCLetter) {
				result += subNode.toString() + op;
			}
			if (subNode instanceof MdCOperation) {
				result += "(" + subNode.toString() +")" + op;
			}
			if (subNode instanceof MdCCartouche) {
				result += subNode.toString() + op;
			}
		}
		return result.substring(0, result.length() - 1);
	}

	public float getWidth() {
		if (maxWidth != null) {
			return maxWidth;
		}
		if (direction == SubNodesDirection.VERTICAL) {
			return getMaxVElWidth();
		}
		if (direction == SubNodesDirection.HORIZONTAL) {
			return getSumHElWidth() + (subNodes.size() - 1) * MdCGraphicConverter.H_GAP;
		}
		return -1;
	}

	public float getHeight() {
		if (maxHeight != null) {
			return maxHeight;
		}
		if (direction == SubNodesDirection.HORIZONTAL) {
			return getMaxHElHeight();
		}
		if (direction == SubNodesDirection.VERTICAL) {
			return getSumVElHeight() + (subNodes.size() - 1) * MdCGraphicConverter.V_GAP;
		}
		return -1;
	}

	private float getSumVElHeight() {	//width based on elements - gaps
		if (direction == SubNodesDirection.HORIZONTAL) {
			throw new IllegalStateException("direction must be vertical to call this method");
		}
		float result = 0;
		if (direction == SubNodesDirection.VERTICAL) {
			for (MdCElement subNode : subNodes) {
				result += subNode.getHeight ();
			}
		}
		return result;
	}

	private float getMaxHElHeight() {
		if (direction == SubNodesDirection.VERTICAL) {
			throw new IllegalStateException("direction must be horizontal to call this method");
		}
		float result = 0;
		if (direction == SubNodesDirection.HORIZONTAL) {
			for (MdCElement subNode : subNodes) {
				if (subNode.getHeight () > result) {
					result = subNode.getHeight ();
				}
			}
		}
		return result;
	}

	public float getSumHElWidth () {	//width based on elements - gaps
		if (direction == SubNodesDirection.VERTICAL) {
			throw new IllegalStateException("direction must be horizontal to call this method");
		}
		float result = 0;
		if (direction == SubNodesDirection.HORIZONTAL) {
			for (MdCElement subNode : subNodes) {
				result += subNode.getWidth ();
			}
		}
		return result;
	}

	public float getMaxVElWidth () {	//max vertical width
		if (direction == SubNodesDirection.HORIZONTAL) {
			throw new IllegalStateException("direction must be vertical to call this method");
		}
		float result = 0;
		if (direction == SubNodesDirection.VERTICAL) {
			for (MdCElement subNode : subNodes) {
				if (subNode.getWidth () > result) {
					result = subNode.getWidth();
				}
			}
		}
		return result;
	}

	public void setMaxWidth(Float realWidth) {		//max width > el width
		this.maxWidth = realWidth;
	}

	public void setMaxHeight(float maxHeight) {
		this.maxHeight = maxHeight;
	}

	public float getElHeight() {
		float result = 0;
		if (direction == SubNodesDirection.VERTICAL) {		//sum width without gaps
			for (MdCElement subNode : subNodes) {
				result += subNode.getHeight ();
			}
		}

		if (direction == SubNodesDirection.HORIZONTAL) {	//find highest element
			for (MdCElement subNode : subNodes) {
				if (subNode.getHeight () > result) {
					result = subNode.getHeight();
				}
			}
		}

		return result;
	}

	@Override
	public void setGraphics(MdCFontLetters mdcFontLetters) {
		for (MdCElement subNode : subNodes) {
			subNode.setGraphics(mdcFontLetters);
		}
	}

	@Override
	public void scale(float scale) {
		super.scale (scale);
		for (MdCElement subNode : subNodes) {
			subNode.scale (scale);
		}
	}
}
