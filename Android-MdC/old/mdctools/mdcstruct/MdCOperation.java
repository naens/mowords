package com.naens.tools.mdctools.mdcstruct;

import java.util.ArrayList;
import java.util.List;


public class MdCOperation extends MdCElement {

	public enum SubNodesDirection {HORIZONTAL, VERTICAL}

	private SubNodesDirection direction;

	private List <MdCElement> subNodes = new ArrayList <MdCElement> (5);

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

}
