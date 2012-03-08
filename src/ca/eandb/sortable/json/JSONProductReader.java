/**
 * 
 */
package ca.eandb.sortable.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ca.eandb.sortable.Product;
import ca.eandb.sortable.ProductReader;
import ca.eandb.sortable.ProductVisitor;

/**
 * An object that reads a collection of product entities from a file.  The
 * entities are stored as JSON objects, with one product per line.
 * 
 * @author Brad Kimmel
 */
public final class JSONProductReader implements ProductReader {

	/* (non-Javadoc)
	 * @see ca.eandb.sortable.ProductReader#read(java.io.Reader, ca.eandb.sortable.ProductVisitor)
	 */
	@Override
	public void read(Reader in, ProductVisitor visitor) throws IOException, ParseException {
		
		BufferedReader buf = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
		
		JSONParser parser = new JSONParser();

		while (true) {
			String line = buf.readLine();
			if (line == null) {
				break;
			}
			
			JSONObject json = (JSONObject) parser.parse(line);
			
			Product product = new Product(
					(String) json.get("product_name"),
					(String) json.get("manufacturer"),
					(String) json.get("model"),
					(String) json.get("family"),
					(String) json.get("announced-date"));

			visitor.visit(product);
		}
		
	}

}
