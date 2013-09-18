package com.naens.tools.mdctools.mdcstruct;

import java.util.ArrayList;
import java.util.List;


public class MdCOperation extends MdCElement {

	public enum SubNodesDirection {HORIZONTAL, VERTICAL}

	private SubNodesDirection direction;

	private List <MdCElement> subNodes = new ArrayList <MdCElement> (5);

	private Float maxWidth;

	private float maxHeight;

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
			} else {
				result += "(" + subNode.toString() + ")" + op;
			}
		}
		return result.substring(0, result.length() - 1);
	}

	public float getWidth() {
		if (maxWidth != null) {
			return maxWidth;
		}
		return getElWidth();
	}

	public float getElWidth () {	//width based on elements - gaps
		float result = 0;
		if (direction == SubNodesDirection.HORIZONTAL) {
			for (MdCElement subNode : subNodes) {
				result += subNode.getWidth ();
			}
		}
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

	public float getHeight() {
		return maxHeight;
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

}
