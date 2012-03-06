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
public final class ProductTrieBuilder {
	
	private final TrieNode root = new TrieNode();
	
	public void addProduct(Product product) {
		processField(product, Field.MANUFACTURER, product.getManufacturer());
		processField(product, Field.FAMILY, product.getFamily());
		processField(product, Field.MODEL, product.getModel());
	}
	
	private void processField(Product product, Field field, String value) {		
		value = StringUtil.normalize(value);
		String[] words = value.split(" ");
		ProductMatch match = new ProductMatch(product, field);
		
		for (String word : words) {
			TrieNode node = root.insert(word);
			List<ProductMatch> products = (List<ProductMatch>) node.getData();
			
			if (products == null) {
				products = new LinkedList<ProductMatch>();
				node.setData(products);
			}

			products.add(match);
		}
	}
	
	public TrieNode getRoot() {
		return root;
	}

}
