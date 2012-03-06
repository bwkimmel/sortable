/**
 * 
 */
package ca.eandb.sortable;

import ca.eandb.sortable.Product.Field;

/**
 * @author Brad Kimmel
 */
public final class ProductMatch {

	private final Product product;
	
	private final Field field;

	/**
	 * @param product
	 * @param field
	 */
	public ProductMatch(Product product, Field field) {
		this.product = product;
		this.field = field;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @return the field
	 */
	public Field getField() {
		return field;
	}
	
}
