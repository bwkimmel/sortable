package ca.eandb.sortable;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */

/**
 * @author Brad Kimmel
 *
 */
public final class TrieNode {
	
	private final Map<ChildRef, TrieNode> children;
	
	private Object data;
	
	private static class ChildRef {
		
		public final TrieNode parent;
		public final char character;
		
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
	
	public TrieNode() {
		this(new HashMap<ChildRef, TrieNode>());
	}
	
	private TrieNode(Map<ChildRef, TrieNode> children) {
		this.children = children;
	}
	
	private TrieNode newChild() {
		return new TrieNode(children);
	}
	
	public TrieNode findChild(char c) {
		ChildRef ref = new ChildRef(this, c);
		return children.get(ref);
	}
	
	public TrieNode findDescendant(String s) {
		TrieNode node = this;
		for (int i = 0, n = s.length(); i < n && node != null; i++) {
			node = node.findChild(s.charAt(i));
		}
		return node;
	}
	
	public TrieNode insert(char c) {
		TrieNode child = findChild(c);
		if (child == null) {
			ChildRef ref = new ChildRef(this, c);

			child = newChild();
			children.put(ref, child);
		}
		return child;	
	}
	
	public TrieNode insert(String s) {
		TrieNode node = this;
		for (int i = 0, n = s.length(); i < n; i++) {
			node = node.insert(s.charAt(i));
		}
		return node;
	}
	
	public Object getData() {
		return data;
	}
	
	public void setData(Object data) {
		this.data = data;
	}
	
}
