/**
 * 
 */
package ca.eandb.sortable;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;

import ca.eandb.sortable.json.JSONListingReader;
import ca.eandb.sortable.json.JSONProductReader;

/**
 * @author Brad Kimmel
 *
 */
public final class SortableChallenge {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			JSONProductReader productReader = new JSONProductReader();
			FileReader reader = new FileReader("/home/brad/work/sortable/products.txt");
			ProductTrieBuilder builder = new ProductTrieBuilder();
			
			productReader.read(reader, builder);
			
			reader = new FileReader("/home/brad/work/sortable/listings.txt");
			JSONListingReader listingReader = new JSONListingReader(builder.getManufacturerRoot(), builder.getModelRoot());
			
			Writer out = new FileWriter("/home/brad/work/sortable/listings.out.txt");
			
			listingReader.read(reader, out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
