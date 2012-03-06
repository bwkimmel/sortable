/**
 * 
 */
package ca.eandb.sortable;

import java.io.Reader;

/**
 * @author Brad Kimmel
 *
 */
public interface ProductReader {
	
	void read(Reader in, ProductVisitor visitor) throws Exception;

}
