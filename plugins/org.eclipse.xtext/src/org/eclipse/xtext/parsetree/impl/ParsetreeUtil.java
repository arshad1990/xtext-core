/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.parsetree.impl;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.parsetree.AbstractNode;
import org.eclipse.xtext.parsetree.CompositeNode;
import org.eclipse.xtext.parsetree.LeafNode;
import org.eclipse.xtext.parsetree.ParsetreePackage;
import org.eclipse.xtext.parsetree.SyntaxError;
import org.eclipse.xtext.util.Strings;

/**
 * @author koehnlein
 * @author bkolb
 * @author Sebastian Zarnekow
 */
public class ParsetreeUtil {

	private static void checkArgument(AbstractNodeImpl abstractParserNode) {
		int classifierID = abstractParserNode.eClass().getClassifierID();
		if (classifierID != ParsetreePackage.COMPOSITE_NODE && classifierID != ParsetreePackage.LEAF_NODE) {
			throw new IllegalArgumentException("Illegal subtype of AbstarctParserNode "
					+ abstractParserNode.eClass().getName());
		}
	}

	public static int line(AbstractNodeImpl _this) {
		checkArgument(_this);
		AbstractNode rootContainer = (AbstractNode) EcoreUtil.getRootContainer(_this);
		EList<LeafNode> leafNodes = rootContainer.getLeafNodes(_this);
		int line = 1;
		for (LeafNode leafNode : leafNodes) {
			String text = leafNode.getText();
			line += Strings.countLines(text);
		}
		return line;
	}

	public static int totalEndLine(AbstractNodeImpl _this) {
		int line = _this.getTotalLine();
		String text = _this.serialize();
		line += Strings.countLines(text);
		return line;
	}

	public static String serialize(AbstractNodeImpl _this) {
		if (_this instanceof LeafNodeImpl)
			return serialize((LeafNodeImpl)_this);
		checkArgument(_this);
		StringBuilder buffer = new StringBuilder(Math.max(16, _this.getTotalLength()));
//		EList<LeafNode> leafNodes = _this.getLeafNodes();
//		for (LeafNode leafNode : leafNodes) {
//			buff.append(leafNode.getText());
//		}
		serialize(_this, buffer);
		return buffer.toString();
	}
	
	public static void serialize(AbstractNode node, StringBuilder buffer) {
		if (node instanceof LeafNode)
			buffer.append(((LeafNode) node).getText());
		else {
			CompositeNode parent = (CompositeNode) node;
			EList<AbstractNode> children = parent.getChildren();
			for(int i = 0; i < children.size(); i++) {
				serialize(children.get(i), buffer);
			}
		}
	}

	public static String serialize(LeafNodeImpl _this) {
		return _this.getText();
	}

	public static EList<LeafNode> getLeafNodes(AbstractNodeImpl _this) {
		return getLeafNodes(_this, null);
	}

	public static EList<LeafNode> getLeafNodes(AbstractNodeImpl _this, AbstractNode to) {
		checkArgument(_this);
		BasicEList<LeafNode> result = new BasicEList<LeafNode>();
		TreeIterator<EObject> allContents = _this.eAllContents();
		while (allContents.hasNext()) {
			EObject current = allContents.next();
			if (current.equals(to))
				return result;
			if (current instanceof LeafNode) {
				result.add((LeafNode) current);
			}
		}
		return result;
	}

	public static EList<SyntaxError> allSyntaxErrors(CompositeNodeImpl compositeNodeImpl) {
		BasicEList<SyntaxError> basicEList = new BasicEList<SyntaxError>();
		addAllSyntaxErrors(compositeNodeImpl, basicEList);
		return basicEList;
	}
	
	public static void addAllSyntaxErrors(CompositeNode node, BasicEList<SyntaxError> result) {
		if (node.getSyntaxError() != null)
			result.add(node.getSyntaxError());
		List<AbstractNode> children = node.getChildren();
		for(int i = 0; i< children.size(); i++) {
			AbstractNode child = children.get(i);
			if (child instanceof LeafNode) {
				if (child.getSyntaxError() != null)
					result.addUnique(child.getSyntaxError());
			} else {
				addAllSyntaxErrors((CompositeNode) child, result);
			}
		}
	}

	public static EList<SyntaxError> allSyntaxErrors(LeafNodeImpl leafNodeImpl) {
		BasicEList<SyntaxError> basicEList = new BasicEList<SyntaxError>();
		if (leafNodeImpl.getSyntaxError() != null)
			basicEList.add(leafNodeImpl.getSyntaxError());
		return basicEList;
	}

	public static EList<SyntaxError> allSyntaxErrors(AbstractNodeImpl abstractNodeImpl) {
		return null;
	}

	/**
	 * @param abstractNode
	 * @return
	 */
	public static int getOffset(AbstractNode abstractNode) {
		if (abstractNode instanceof LeafNode)
			return abstractNode.getTotalOffset();
		final CompositeNode node = (CompositeNode) abstractNode;
		for (int i = 0; i < node.getChildren().size(); i++) {
			final AbstractNode child = node.getChildren().get(i);
			if (child instanceof CompositeNode) {
				if (hasLeafNodes((CompositeNode) child))
					return getOffset(child);
			} else {
				final LeafNode leaf = (LeafNode) child;
				if (!leaf.isHidden())
					return leaf.getTotalOffset();
			}
		}
		// every child node is a hidden node, return total offset
		return abstractNode.getTotalOffset();
	}

	private static boolean hasLeafNodes(CompositeNode child) {
		for (AbstractNode node : child.getChildren()) {
			if (node instanceof CompositeNode) {
				if (hasLeafNodes((CompositeNode) node))
					return true;
			} else {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param abstractNode
	 * @return
	 */
	public static int getLine(AbstractNode abstractNode) {
		if (abstractNode instanceof LeafNode)
			return abstractNode.getTotalLine();
		final CompositeNode node = (CompositeNode) abstractNode;
		for (int i = 0; i < node.getChildren().size(); i++) {
			final AbstractNode child = node.getChildren().get(i);
			if (child instanceof CompositeNode)
				return getLine(child);
			final LeafNode leaf = (LeafNode) child;
			if (!leaf.isHidden())
				return leaf.getTotalLine();
		}
		// every child node is a hidden node, return total line
		return abstractNode.getTotalLine();
	}

	/**
	 * @param abstractNode
	 * @return
	 */
	public static int getLength(AbstractNode abstractNode) {
		if (abstractNode instanceof LeafNode)
			return abstractNode.getTotalLength();
		final CompositeNode node = (CompositeNode) abstractNode;
		for (int i = node.getChildren().size() - 1; i >= 0; i--) {
			final AbstractNode child = node.getChildren().get(i);
			if (child instanceof CompositeNode)
				return child.getOffset() - abstractNode.getOffset() + child.getLength();
			if (!((LeafNode) child).isHidden())
				return child.getTotalOffset() - abstractNode.getOffset() + child.getTotalLength();
		}
		// every child node is a hidden node, return total length
		return abstractNode.getTotalLength();
	}

	/**
	 * @param abstractNode
	 * @return
	 */
	public static int endLine(AbstractNode abstractNode) {
		if (abstractNode instanceof LeafNode)
			return abstractNode.totalEndLine();
		final CompositeNode node = (CompositeNode) abstractNode;
		for (int i = node.getChildren().size() - 1; i >= 0; i--) {
			final AbstractNode child = node.getChildren().get(i);
			if (child instanceof CompositeNode)
				return endLine(child);
			if (!((LeafNode) child).isHidden())
				return child.totalEndLine();
		}
		// every child node is a hidden node, return total endLine
		return abstractNode.totalEndLine();
	}

}
