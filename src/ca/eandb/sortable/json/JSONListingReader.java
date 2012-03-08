/**
 * 
 */
package ca.eandb.sortable.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ca.eandb.sortable.Product;
import ca.eandb.sortable.StringUtil;
import ca.eandb.sortable.TrieNode;

/**
 * An object that processes a set of product listings, matches them with a
 * product, and prints the results to a specified <code>PrintStream</code>.
 * The listings are given as JSON objects, with one listing per line.  See
 * comments in {@link #match(TrieNode, String, Set)} for details on how the
 * matching is performed.
 * @author Brad Kimmel
 */
public final class JSONListingReader {
	
	/**
	 * The root <code>TrieNode</code> containing the set of strings that match
	 * the "manufacturer" field of a <code>Product</code>.
	 * 
	 * @see ca.eandb.sortable.ProductTrieBuilder
	 */
	private final TrieNode manufacturerTrie;
	
	/**
	 * The root <code>TrieNode</code> containing the set of strings that match
	 * the model name of a <code>Product</code>.  This could be the substrings
	 * of the "product_name" field, the "model" field, or the concatenation of
	 * the "family" and "model" fields (if the "family" field is present).  See
	 * {@link ca.eandb.sortable.ProductTrieBuilder} for details.
	 * 
	 * @see ca.eandb.sortable.ProductTrieBuilder
	 */
	private final TrieNode modelTrie;

	/**
	 * A value indicating whether the results should consist of a list of the
	 * unmatched listings, rather than the results specified in the
	 * <a href="http://sortable.com/blog/coding-challenge/">challenge
	 * specifications</a>.
	 */
	private final boolean printMisses = Boolean.parseBoolean(
			System.getProperty("ca.eandb.sortable.printMisses", "true"));
	
	/**
	 * Creates a new <code>JSONListingReader</code>. 
	 * @param manufacturerTrie The root <code>TrieNode</code> containing the
	 * 		set of strings that match the "manufacturer" field of a
	 * 		<code>Product</code>.
	 * @param modelTrie The root <code>TrieNode</code> containing the set of
	 * 		strings that match the model name of a <code>Product</code>.
	 */
	public JSONListingReader(TrieNode manufacturerTrie, TrieNode modelTrie) {
		this.manufacturerTrie = manufacturerTrie;
		this.modelTrie = modelTrie;
	}

	/**
	 * Reads the JSON-formatted listings from the file (one listing per line),
	 * matches the listings to at most one product, and prints the results to
	 * the specified <code>PrintStream</code>. 
	 * @param in The <code>Reader</code> to read the listings from.
	 * @param out The <code>PrintStream</code> to write the results to.
	 * @throws IOException If an exception is thrown while reading from
	 * 		<code>in</code>.
	 * @throws ParseException If a line in the file does not represent a valid
	 * 		JSON object.
	 */
	public void read(Reader in, Writer out_) throws IOException, ParseException {
		
		// Wrap the output writer in a PrintWriter if it is not already a
		// PrintWriter.
		PrintWriter out = out_ instanceof PrintWriter ? (PrintWriter) out_ : new PrintWriter(out_);
		
		// Map to store the matching listings corresponding to each product.
		Map<String, JSONArray> matches = new HashMap<String, JSONArray>();

		int numListings = 0;	// total number of listings
		int numMatches = 0;		// number of listings with a unique product match
		
		BufferedReader buf = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
		
		JSONParser parser = new JSONParser();
		
		while (true) {
			String line = buf.readLine();
			if (line == null) {
				break;
			}
			
			JSONObject json = (JSONObject) parser.parse(line);
		
			/* Find all the products with a matching manufacturer. */
			Set<Product> mp = match(manufacturerTrie, (String) json.get("manufacturer"), null);
			
			if (mp != null) { // don't continue if we didn't find any
				
				/* Eliminate everything after the word "for" (or its french
				 * translation "pour"), as everything that follows is most
				 * likely not the product itself.  For example:
				 * 
				 *   "Battery pack *for* Canon EOS 7D"
				 *   "Leather case *for* Nikon S6100"
				 * 
				 * We could make this more generic, for example by using an
				 * online translation service to translate detect the language
				 * and translate it to English, but this will do for
				 * demonstration purposes.
				 */
				String title = (String) json.get("title");
				title = title.replaceFirst(" for .*", "");
				title = title.replaceFirst(" pour .*", "");
				
				/* Match the listing title against the model name, only
				 * considering those products with the correct manufacturer.
				 */
				Product product = matchOne(modelTrie, title, mp);
			
				/* If we found a match, add some fields identifying the matched
				 * product to the listing JSON and reprint it.
				 */
				if (product != null) {
					numMatches++;
					
					if (!printMisses) {
						JSONArray array = matches.get(product.getName());
						if (array == null) {
							array = new JSONArray();
							matches.put(product.getName(), array);
						}
				
						array.add(json);
					}
				} else if (printMisses) {
					out.println(line);
				}
				
			}
			
			numListings++;
		}
				
		// print the list of matching listings.
		if (!printMisses) {
			for (Map.Entry<String, JSONArray> e : matches.entrySet()) {
				JSONObject obj = new JSONObject();
				obj.put("product_name", e.getKey());
				obj.put("listings", e.getValue());
				obj.writeJSONString(out);
				out.println();
			}
		}
		
		double pctMatch = 100.0 * (double) numMatches / (double) numListings;
		System.err.printf("Matched %d of %d listings (%4.1f%%).", numMatches, numListings, pctMatch);
		
	}

	/**
	 * Matches the specified string against the <code>Product</code>s stored in
	 * the specified trie.
	 * @param root The <code>TrieNode</code> at the root of the trie to use to
	 * 		match against.
	 * @param s The <code>String</code> to match against.
	 * @param filter A <code>Set</code> of <code>Product</code>s used to filter
	 * 		the results.  If present, the specified trie will be treated as if
	 * 		it only contained products in this <code>Set</code>.
	 * @return A <code>Set</code> containing all of the <code>Product</code>s
	 * 		that match.
	 */
	private Set<Product> match(TrieNode root, String s, Set<Product> filter) {
		
		// preprocess string for matching
		s = StringUtil.normalize(s);
		String[] words = s.split(" ");
		
		/* Attempt to match all of the sequences of consecutive words against
		 * against the provided trie.  For example, if s is "The quick brown
		 * fox", we want to consider the following for possible matches:
		 * 
		 *   - "The", "quick", "brown", "fox"
		 *   - "Thequick", "quickbrown", "brownfox"
		 *   - "Thequickbrown", "quickbrownfox"
		 *   - "Thequickbrownfox"
		 * 
		 * The reason for this logic is so that we can match against listings
		 * where the model number is split into multiple words in the listing,
		 * but not in the product data, or vice versa -- or to allow for the
		 * listing to contain only a partial model number (for example,
		 * "Panasonic FP 7" instead of "Panasonic DMC-FP7").
		 * 
		 * To accomplish this, we keep track of a queue of positions (cursors)
		 * within the trie.  We iterate through the list of words, and for each
		 * word, we attempt to match that word using each cursor (as well as
		 * the root node, which is added as a cursor each time through the
		 * loop).  If we find a descendant matching the word, then we:
		 * 
		 *   1) Check to see if the descendant has products associated with it.
		 *      If there are, we keep track of the set of products associated
		 *      with this node in a map (matches).  If a filter was provided,
		 *      it is employed here.
		 *   2) Add the descendant to the queue as a new cursor.
		 *   
		 * We also only want to consider longest matches.  That is, if a
		 * substring of a match also matches, we want to ignore the matches for
		 * the substring.  The reason for this rule is so that for pairs of 
		 * products whose model name differs from another only by the addition
		 * of more characters, if those characters are present, we do not want
		 * to report a match against the other product.  For example, consider
		 * 
		 *   Pentax WG-1
		 *   Pentax WG-1 GPS
		 *   
		 * If a listing contained the words "WG-1 GPS", without this rule both
		 * products would match this listing, resulting in the algorithm
		 * reporting no certain match.
		 * 
		 * We accomplish this by removing the matches for all ancestors
		 * when inserting a new matching node into the map.  Because we are
		 * adding matches in breadth-first order, we can do this within the
		 * loop rather than as a separate tree-traversal at the end.
		 */
		Queue<TrieNode> cursors = new LinkedList<TrieNode>();
		Map<TrieNode, Set<Product>> matches = new HashMap<TrieNode, Set<Product>>();
		
		for (String word : words) {
			cursors.add(root);
			for (int i = 0, n = cursors.size(); i < n; i++) {
				TrieNode node = cursors.remove();
				node = node.findDescendant(word);
				if (node != null) {
					if (node.getData() != null) {	// we have some matches.
						Set<Product> products = new HashSet<Product>((List<Product>) node.getData());
						
						// apply the filter
						if (filter != null) {
							products.retainAll(filter);
						}
						
						// do we still have matches after applying filter?
						if (!products.isEmpty()) {
							matches.put(node, products);
							
							// remove matches for ancestor nodes.
							for (TrieNode anc = node.getParent(); anc != null; anc = anc.getParent()) {
								matches.remove(anc);
							}
						}
					}
					
					// add cursor for descendant node
					cursors.add(node);
				}
			}
		}
		
		/* Now that we have a collection of possible matches, we must resolve
		 * them to a minimal set of matches (ideally only one).  The following
		 * possibilities should be considered:
		 * 
		 *   1) There may be conflicting matches against multiple products.
		 *      That is, one trie node matches against exactly one product, and
		 *      another node matches against exactly one different product.
		 *      More generally, two trie nodes may match but the intersection
		 *      of the sets of matching products is empty.
		 *      
		 *      If this occurs, it is likely because the listing refers to an
		 *      accessory (such as a battery, case, etc) that may be used for
		 *      multiple products.  For example:
		 *      
		 *      "Battery pack to be used with Canon EOS 5D, 7D, or T2i cameras"
		 *      
		 *      Notice that a listing like this one would not be caught by the
		 *      "for/pour" rule in the preprocessing stage.
		 *      
		 *   2) Depending on the word separation for the model number in the
		 *      product database vs the listings, and whether a family name is
		 *      included or not may cause quirks in the matching.  For example,
		 *      consider the following products:
		 *      
		 *       (a) "Canon EOS Rebel T1i"
		 *       (b) "Canon EOS Rebel T2i"
		 *       (c) "Canon Rebel T3i"
		 *       
		 *      Here, "EOS" probably should be included in (c), but it is not.
		 *      If the listing says "Canon EOS Rebel T3i", then EOS will match
		 *      against (a), (b), (and probably others), but "Rebel T3i" will
		 *      result in a unique match (c).  Even though the resulting
		 *      intersection of matching sets will be empty, we want the latter
		 *      match to take precedence because it is a unique match.
		 *      
		 *  The following rules are therefore used to resolve the matches:
		 *  
		 *   1) If there are any nodes which match against only one product,
		 *      then we return that product as long as ALL such nodes match
		 *      against the same product.
		 *   2) If all matching nodes match against multiple products, then we
		 *      return a set containing only those products which are matched
		 *      by every node.  Note that, because we have eliminated matching
		 *      nodes that were ancestors of other matching nodes, conflicts
		 *      between such pairs of nodes do not affect the results.  We only
		 *      consider maximal matches.
		 */
		Set<Product> results = null;
		boolean foundSingleton = false;
		for (Set<Product> products : matches.values()) {
			if (!foundSingleton && products.size() == 1) {
				foundSingleton = true;
				results = products;
			} else {
				if (foundSingleton) {
					// if we've already found a singleton, only consider other
					// singletons from here on.
					if (products.size() == 1) { results.retainAll(products); }
				} else { // !foundSingleton
					if (results == null) {
						results = products;
					} else {
						results.retainAll(products);
					}
				}
			}
		}
		
		return results;

	}
	
	/**
	 * Matches the specified string with at most one <code>Product</code>.
	 * @param root The <code>TrieNode</code> at the root of the trie to use to
	 * 		match against.
	 * @param s The <code>String</code> to match against.
	 * @param filter A <code>Set</code> of <code>Product</code>s used to filter
	 * 		the results.  If present, the specified trie will be treated as if
	 * 		it only contained products in this <code>Set</code>.
	 * @return The matching <code>Product</code>, if there is exactly one, or
	 * 		<code>null</code> if zero or more than one <code>Product</code>
	 * 		matches.
	 */
	private Product matchOne(TrieNode root, String s, Set<Product> filter) {
		Set<Product> products = match(root, s, filter);
		if (products != null && products.size() == 1) {
			for (Product p : products) {
				return p;
			}
		}
		return null;
	}
	
}
