/**
 * 
 */
package ca.eandb.sortable;

import java.util.LinkedList;

import ca.eandb.sortable.Product.Field;

/**
 * A builder that creates tries to use to match against the model name and the
 * manufacturer for each of the <code>Product</code>s provided.  After all
 * <code>Product</code>s have been added, each <code>TrieNode</code>
 * corresponding to a match for one or more products will have a
 * <code>LinkedList</code> of <code>Product</code>s in its data field.  The
 * strings to be associated with a given product include the concatenations of
 * all sequences of consecutive words within:
 * 
 * 	- For the manufacturer trie: the <code>manufacturer</code> field.
 *  - For the model trie:
 *    - the "model" field, or
 *    - the "product_name" field, or
 *    - the "family" field concatenated with the "model" field.
 *    
 * except for certain strings which are judged not likely to be proper matches
 * (see comments in {@link #processField(TrieNode, Product, Field, String)}
 * below).
 * 
 * @see TrieNode#getData()
 * @author Brad Kimmel
 */
public final class ProductTrieBuilder implements ProductVisitor {
	
	/** The <code>TrieNode</code> at the root of the model name trie. */
	private final TrieNode modelRoot = new TrieNode();
	
	/** The <code>TrieNode</code> at the root of the manufacturer trie. */
	private final TrieNode manufacturerRoot = new TrieNode();

	/*(non-Javadoc)
	 * @see ca.eandb.sortable.ProductVisitor#visit(ca.eandb.sortable.Product)
	 */
	@Override
	public void visit(Product product) {
		addProduct(product);
	}
	
	/**
	 * Adds the specified product to the tries.
	 * @param product The <code>Product</code> to add.
	 */
	public void addProduct(Product product) {
		
		/* Add the manufacturer string to a separate trie. */
		processField(manufacturerRoot, product, Field.MANUFACTURER, product.getManufacturer());
		
		/* Some product entries have the family, while others what have what
		 * looks to be the "family" as part of the model.  Still others may
		 * only include the "family" within the product_name field.  For
		 * example, in the provided product list, the Canon "EOS" family seems
		 * particularly inconsistent about where the word "EOS" is found.  To
		 * capture all possible cases, we include several possibilities that
		 * may represent the whole model name.  Note that these only indicate
		 * which strings *might* be matches for a given product -- so there's
		 * no harm in adding "too much" information here.
		 */
		processField(modelRoot, product, Field.MODEL, product.getName());
		processField(modelRoot, product, Field.MODEL, product.getModel());
		if (product.getFamily() != null) {
			processField(modelRoot, product, Field.MODEL, product.getFamily() + " " + product.getModel());
		}
		
	}
	
	/**
	 * Inserts substrings of the provided string that are to be considered as
	 * matches into the trie and associate the specified product with the nodes
	 * corresponding to the ends of those substrings. 
	 * @param root The root <code>TrieNode</code> of the trie to insert into.
	 * @param product The <code>Product</code> to associate with the substrings
	 * 		of <code>value</code>.
	 * @param field The <code>Product.Field</code> associated with this string
	 * 		(affects the rules used to judge whether a substring is considered
	 * 		to be a match).
	 * @param value The <code>String</code> whose substrings to insert into the
	 * 		trie.
	 */
	private void processField(TrieNode root, Product product, Field field, String value) {		
		
		/* Split the string into its component words and insert the concatenation
		 * of every consecutive subsequence of those words into the trie, subject
		 * to some additional rules described below.
		 */
		value = StringUtil.normalize(value);
		String[] words = value.split(" ");
		for (int i = 0; i < words.length; i++) {
			String word = "";
			
			for (int j = i; j < words.length; j++) {
				word += words[j];

				/* These are some tweaks to help eliminate false positives:
				 * 
				 * - Don't consider single-character matches.  For example, for
				 *   the "Pentax K-r", we don't want to consider "K" or "r" in
				 *   isolation to be a match.
				 * - Don't consider long words (> 3 characters) of only letters
				 *   to be a valid match for a model name.  Some model names
				 *   have common dictionary words in them, which may erroneously
				 *   match one of the words in the listing (e.g., "Canon EOS
				 *   Kiss Digital X3" -- the word "Digital" may trigger many
				 *   false positives).  Ideally, we want to allow model names
				 *   that include only letters (such as IXUS, ELPH).  As a
				 *   compromise, we are assuming 4+ letters in a row to be a
				 *   likely dictionary word.  A better solution might be to 
				 *   search against a word list (such as the unix word list).
				 * - Don't consider short strings of only numbers to be a match.
				 * 
				 * Notwithstanding the above, a match on the entire string is
				 * always accepted.
				 */
				if (j - i < words.length) { // always accept entire string
					if (word.length() <= 1) { // single character
						continue;
					}
					if (field == Field.MODEL && word.length() > 3
							&& !word.matches(".*[0-9].*")) { // likely dictionary word 
						continue;
					}
					if (word.length() < 4 && word.matches("[0-9]*")) { // short number
						continue;
					}
				}

				/* Insert the word into the trie and associate the product with
				 * it.
				 */
				TrieNode node = root.insert(word);
				LinkedList<Product> products = (LinkedList<Product>) node.getData();
				
				/* create a new list at this node if needed. */
				if (products == null) {
					products = new LinkedList<Product>();
					node.setData(products);
				}

				/* Associate the product with the current trie node if it not
				 * already.  We need only examine the last node in the list
				 * since we are processing one product fully before moving on
				 * to the next one.
				 */
				if (products.isEmpty() || product != products.getLast()) {
					products.addLast(product);
				}
			}
		}

	}
	
	/**
	 * Gets the <code>TrieNode</code> at the root of the manufacturer trie. 
	 * @return The <code>TrieNode</code> at the root of the manufacturer trie.
	 */
	public TrieNode getManufacturerRoot() {
		return manufacturerRoot;
	}
	
	/**
	 * Gets the <code>TrieNode</code> at the root of the model name trie. 
	 * @return The <code>TrieNode</code> at the root of the model name trie.
	 */
	public TrieNode getModelRoot() {
		return modelRoot;
	}

}
