/**
 * 
 */
package ca.eandb.sortable.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ca.eandb.sortable.Product;
import ca.eandb.sortable.StringUtil;
import ca.eandb.sortable.TrieNode;

/**
 * @author brad
 *
 */
public final class JSONListingReader {
	
	private final TrieNode manufacturerTrie;
	
	private final TrieNode modelTrie;
	
	public JSONListingReader(TrieNode manufacturerTrie, TrieNode modelTrie) {
		this.manufacturerTrie = manufacturerTrie;
		this.modelTrie = modelTrie;
	}

	public void read(Reader in, PrintStream out) throws IOException, ParseException {
		
		BufferedReader buf = in instanceof BufferedReader ? (BufferedReader) in : new BufferedReader(in);
		
		JSONParser parser = new JSONParser();
		
		while (true) {
			String line = buf.readLine();
			if (line == null) {
				break;
			}
			
			JSONObject json = (JSONObject) parser.parse(line);
		
			String title = (String) json.get("title");
			title = title.replaceFirst(" for .*", "");
			title = title.replaceFirst(" pour .*", "");
			Set<Product> mp = match(manufacturerTrie, (String) json.get("manufacturer"), null);
			if (mp != null) {
				Product product = matchOne(modelTrie, title, mp);
			
				if (product != null) {
					json.put("product_name", product.getName());
					json.put("model", product.getModel());
					out.println(json.toJSONString());
				} else {
	//				out.println(json.toJSONString());
				}
			}
		}
		
	}
	
	
	private Set<Product> match(TrieNode root, String title, Set<Product> filter) {
		
		title = StringUtil.normalize(title);
		
		String[] words = title.split(" ");
		
		Queue<TrieNode> cursors = new LinkedList<TrieNode>();
		Map<TrieNode, Set<Product>> matches = new HashMap<TrieNode, Set<Product>>();
		
		for (String word : words) {
			cursors.add(root);
			for (int i = 0, n = cursors.size(); i < n; i++) {
				TrieNode node = cursors.remove();
				node = node.findDescendant(word);
				if (node != null) {
					if (node.getData() != null) { /* we have some matches. */
						Set<Product> products = new HashSet<Product>((List<Product>) node.getData());
						if (filter != null) {
							products.retainAll(filter);
						}
						if (!products.isEmpty()) {
							matches.put(node, products);
							for (TrieNode anc = node.getParent(); anc != null; anc = anc.getParent()) {
								matches.remove(anc);
							}
						}
					}
					cursors.add(node);
				}
			}
		}
		
		
		Set<Product> results = null;
		boolean foundSingleton = false;
		for (Set<Product> products : matches.values()) {
			if (!foundSingleton && products.size() == 1) {
				foundSingleton = true;
				results = products;
			} else {
				if (foundSingleton) {
					if (products.size() == 1) { results.retainAll(products); }
				} else {
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
