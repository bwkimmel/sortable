package ca.eandb.sortable;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */

/**
 * Represents a node in a trie (see {@link http://en.wikipedia.org/wiki/Trie}),
 * a tree structure that stores strings.  Each node in the trie represents a
 * string prefix.  Strings with a common prefix will share a common path from
 * the root of the tree until the point at which the strings diverge.
 * 
 * @author Brad Kimmel
 */
public final class TrieNode {
	
	/**
	 * A <code>Map</code> used to lookup the children of a particular node.
	 * This map will be shared by all the <code>TrieNode</code>s for a given
	 * trie.  The key type for this map is the (node, character) pair that
	 * uniquely identifies the parent-child edge.
	 * 
	 * @see ChildRef
	 */
	private final Map<ChildRef, TrieNode> children;
	
	/** An optional data <code>Object</code> associated with this node. */
	private Object data;
	
	/**
	 * Represents a <code>(TrieNode, char)</code> pair that uniquely identifies
	 * a parent-child edge in the trie.
	 */
	private static class ChildRef {

		/** The parent <code>TrieNode</code>. */
		public final TrieNode parent;
		
		/**
		 * The <code>char</code> that identifies a particular child of the
		 * parent <code>TreeNode</code>.
		 */
		public final char character;

		/**
		 * Creates a new <code>ChildRef</code>.
		 * @param parent The parent <code>TrieNode</code>.
		 * @param character The <code>char</code> that identifies a particular
		 * 		child of the parent <code>TreeNode</code>.
		 */
		public ChildRef(TrieNode parent, char character) {
			this.parent = parent;
			this.character = character;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			return (obj instanceof ChildRef) && equals((ChildRef) obj);
		}
		
		/**
		 * Determines whether two <code>ChildRef</code>s represent the same
		 * edge in the trie.
		 * @param other The <code>ChildRef</code> to compare with this one.
		 * @return A value indicating if this <code>ChildRef</code> and
		 * 		<code>other</code> represent the same edge.
		 */
		public boolean equals(ChildRef other) {
			return parent == other.parent && character == other.character;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return parent.hashCode() ^ new Character(character).hashCode();
		}
	}
	
	/**
	 * Creates a new root <code>TrieNode</code>.  
	 */
	public TrieNode() {
		this(new HashMap<ChildRef, TrieNode>());
	}
	
	/**
	 * Creates a new <code>TrieNode</code>.
	 * @param children The parent-child <code>Map</code> (shared among all the
	 * 		nodes for a given trie).
	 */
	private TrieNode(Map<ChildRef, TrieNode> children) {
		this.children = children;
	}
	
	/**
	 * Creates a new <code>TrieNode</code> which is to be the child of this
	 * <code>TrieNode</code>.
	 * @return A new <code>TrieNode</code>.
	 */
	private TrieNode newChild() {
		return new TrieNode(children);
	}
	
	/**
	 * Finds a child of this <code>TrieNode</code>.
	 * @param c The <code>char</code> identifying which child to find.
	 * @return The specified child <code>TrieNode</code>, or <code>null</code>
	 * 		if no such child exists.
	 */
	public TrieNode findChild(char c) {
		ChildRef ref = new ChildRef(this, c);
		return children.get(ref);
	}
	
	/**
	 * Finds a descendant of this <code>TrieNode</code>.
	 * @param s The <code>String</code> identifying the path to follow.
	 * @return The specified descendant <code>TrieNode</code>, or
	 * 		<code>null</code> if no such descendant exists.
	 */
	public TrieNode findDescendant(String s) {
		TrieNode node = this;
		for (int i = 0, n = s.length(); i < n && node != null; i++) {
			node = node.findChild(s.charAt(i));
		}
		return node;
	}
	
	/**
	 * Inserts a child <code>TrieNode</code> into the trie.
	 * @param c The <code>char</code> identifying the new child.
	 * @return The new child <code>TrieNode</code>, or the existing child if
	 * 		one already exists corresponding to <code>c</code>.
	 */
	public TrieNode insert(char c) {
		TrieNode child = findChild(c);
		if (child == null) {
			ChildRef ref = new ChildRef(this, c);

			child = newChild();
			children.put(ref, child);
		}
		return child;	
	}
	
	/**
	 * Inserts a chain of descendant <code>TrieNode</code> into the trie.
	 * @param s The <code>String</code> identifying the path to insert.
	 * @return The new descendant <code>TrieNode</code> at the end of the path,
	 * 		or the existing descendant if one already exists corresponding to
	 * 		<code>s</code>.
	 */
	public TrieNode insert(String s) {
		TrieNode node = this;
		for (int i = 0, n = s.length(); i < n; i++) {
			node = node.insert(s.charAt(i));
		}
		return node;
	}
	
	/**
	 * Gets the <code>Object</code> associated with this node.
	 * @return The <code>Object</code> associated with this node.
	 */
	public Object getData() {
		return data;
	}
	
	/**
	 * Sets the <code>Object</code> associated with this node.
	 * @param data The <code>Object</code> to associate with this node.
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
}
