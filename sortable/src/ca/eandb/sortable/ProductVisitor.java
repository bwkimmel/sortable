/**
 * 
 */
package ca.eandb.sortable;

/**
 * A visitor object for iterating through a collection of <code>Product</code>s.
 * @author Brad Kimmel
 */
public interface ProductVisitor {

	/**
	 * Visites a <code>Product</code>.
	 * @param product The <code>Product</code> to visit.
	 */
	void visit(Product product);
	
}
