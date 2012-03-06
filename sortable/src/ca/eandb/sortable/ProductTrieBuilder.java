/**
 * 
 */
package ca.eandb.sortable;

import java.util.LinkedList;
import java.util.List;

import ca.eandb.sortable.Product.Field;

/**
 * @author brad
 *
 */
public final class ProductTrieBuilder implements ProductVisitor {
	
	private final TrieNode root = new TrieNode();

	/*(non-Javadoc)
	 * @see ca.eandb.sortable.ProductVisitor#visit(ca.eandb.sortable.Product)
	 */
	@Override
	public void visit(Product product) {
		addProduct(product);
	}
	
	public void addProduct(Product product) {
		processField(product, Field.MANUFACTURER, product.getManufacturer());
		processField(product, Field.MODEL, product.getModel());
		if (product.getFamily() != null) {
			processField(product, Field.MODEL, product.getFamily() + " " + product.getModel());
		}
	}
	
	private void processField(Product product, Field field, String value) {		
		value = StringUtil.normalize(value);
		String[] words = value.split(" ");
		ProductMatch match = new ProductMatch(product, field);
		
		for (int i = 0; i < words.length; i++) {
			String word = "";
			for (int j = i; j < words.length; j++) {
				word += words[j];

				if (j - i < words.length) {
					if (word.length() <= 1) {
						continue;
					}
					if (i == j && field == Field.MODEL && word.length() > 3 && !word.matches(".*[0-9].*")) {
						continue;
					}
					if (word.length() < 4 && word.matches("[0-9]*")) {
						continue;
					}
				}

				TrieNode node = root.insert(word);
				List<ProductMatch> products = (List<ProductMatch>) node.getData();
				
				if (products == null) {
					products = new LinkedList<ProductMatch>();
					node.setData(products);
				}

				products.add(match);
			}
		}

	}
	
	public TrieNode getRoot() {
		return root;
	}

}
