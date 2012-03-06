/**
 * 
 */
package ca.eandb.sortable;

import java.io.FileReader;

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
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
