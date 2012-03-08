/**
 * 
 */
package ca.eandb.sortable;

/**
 * An object representing a product match along with some bookkeeping
 * information.  Two <code>ProductMatch</code> objects are considered equal to
 * one another if their underlying products are equal, regardless of the state
 * of any bookkeeping information.
 * 
 * @author Brad Kimmel
 */
public final class ProductMatch {
	
	/** The matching <code>Product</code>. */
	private final Product product;
	
	/**
	 * A value indicating whether this match represents a maximal match.  A
	 * match is considered maximal if there is no suffix which may be appended
	 * to the match to create a longer match for the same product.
	 */
	private boolean isMaximal = false;
	
	/**
	 * Creates a new <code>ProductMatch</code>.
	 * @param product The matching <code>Product</code>.
	 */
	public ProductMatch(Product product) {
		this.product = product;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ProductMatch && product.equals(((ProductMatch) obj).product);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return product.hashCode();
	}

	/**
	 * Gets a value indicating whether this match represents a maximal match. 
	 * A match is considered maximal if there is no suffix which may be
	 * appended to the match to create a longer match for the same product.
	 * @return A value indicating whether this match represents a maximal
	 * 		match.
	 */
	public boolean isMaximal() {
		return isMaximal;
	}

	/**
	 * Sets the value indicating whether this match represents a maximal match.
	 * A match is considered maximal if there is no suffix which may be
	 * appended to the match to create a longer match for the same product.
	 * @param isMaximal The value indicating whether this match represents a
	 * 		maximal match.
	 */
	public void setMaximal(boolean isMaximal) {
		this.isMaximal = isMaximal;
	}

	/**
	 * Gets the matching <code>Product</code>.
	 * @return The matching <code>Product</code>.
	 */
	public Product getProduct() {
		return product;
	}

}
