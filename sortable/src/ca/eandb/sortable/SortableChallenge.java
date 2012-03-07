/**
 * 
 */
package ca.eandb.sortable;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;

import ca.eandb.sortable.json.JSONListingReader;
import ca.eandb.sortable.json.JSONProductReader;

/**
 * An application that matches product listings to a collection of known
 * products.  This application implements a coding challenge described at
 * <a href="http://sortable.com/blog/coding-challenge/">http://sortable.com/blog/coding-challenge/</a>.
 * 
 * @author Brad Kimmel
 */
public final class SortableChallenge {

	/**
	 * Usage: ca.eandb.SortableChallenge  <products_file> [<listings_file> [<output_file>]]
     * Matches listings against a collection of products.
	 *
	 *   <products_file> - A file containing a list of products formatted as JSON
	 *                     objects, one per line.
	 *   <listings_file> - A file containing a collection of listings formatted as
	 *                     JSON objects, one per line.  If not specified, stdin is
	 *                     used.
	 *   <output_file>   - A file to which to write the results.  If not specified,
	 *                     stdout is used.
	 *                     
	 * @param args The program arguments as specified above.
	 */
	public static void main(String[] args) {

		// check that we have a valid number of arguments.
		if (args.length < 1 || args.length > 3) {
			usage();
			System.exit(1);
		}

		try {
			
			// Read the products from the products file and build the data
			// structures necessary to process the listings.
			JSONProductReader productReader = new JSONProductReader();
			Reader reader = new FileReader(args[0]);
			ProductTrieBuilder builder = new ProductTrieBuilder();
			
			productReader.read(reader, builder);
			
			// Read the listings, match them against the products, and print
			// the results.
			reader = args.length > 1 ? new FileReader(args[1]) : 
				new InputStreamReader(System.in);
			JSONListingReader listingReader = new JSONListingReader(
					builder.getManufacturerRoot(), builder.getModelRoot());
			
			Writer out = args.length > 2 ? new FileWriter(args[2]) : 
				new PrintWriter(System.out);
			
			listingReader.read(reader, out);
			
		} catch (Exception e) {

			// For now just print the error if one occurs.
			e.printStackTrace();
			
		}
		
	}
	
	/** Print the usage information for this application. */
	private static void usage() {
		System.out.printf("Usage: %s <products_file> [<listings_file> [<output_file>]]", SortableChallenge.class.getName());
		System.out.println();
		System.out.println("Matches listings against a collection of products.");
		System.out.println();
		System.out.println("  <products_file> - A file containing a list of products formatted as JSON");
		System.out.println("                    objects, one per line.");
		System.out.println("  <listings_file> - A file containing a collection of listings formatted as");
		System.out.println("                    JSON objects, one per line.  If not specified, stdin is");
		System.out.println("                    used.");
		System.out.println("  <output_file>   - A file to which to write the results.  If not specified,");
		System.out.println("                    stdout is used.");
	}

}
