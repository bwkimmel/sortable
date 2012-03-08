/**
 * 
 */
package ca.eandb.sortable;

import java.io.Reader;

/**
 * Represents an object capable of producing <code>Product</code>s from a
 * provided input source.
 * @author Brad Kimmel
 */
public interface ProductReader {
	
	/**
	 * Reads the <code>Product</code>s from the provided source and enumerates
	 * them to the provided <code>ProductVisitor</code>.
	 * @param in The <code>Reader</code> to read the <code>Product</code>s from.
	 * @param visitor The <code>ProductVisitor</code> to use to enumerate the
	 * 		<code>Product</code>s.
	 * @throws Exception If an error occurs while reading.
	 */
	void read(Reader in, ProductVisitor visitor) throws Exception;

}
