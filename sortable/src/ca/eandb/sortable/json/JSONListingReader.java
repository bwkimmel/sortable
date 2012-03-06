/**
 * 
 */
package ca.eandb.sortable.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ca.eandb.sortable.Product;
import ca.eandb.sortable.Product.Field;
import ca.eandb.sortable.ProductMatch;
import ca.eandb.sortable.StringUtil;
import ca.eandb.sortable.TrieNode;

/**
 * @author brad
 *
 */
public final class JSONListingReader {
	
	private final TrieNode productTrie;
	
	public JSONListingReader(TrieNode productTrie) {
		this.productTrie = productTrie;
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
			Set<Product> mp = match((String) json.get("manufacturer"), Field.MANUFACTURER, null);
			Product product = matchOne(title, Field.MODEL, mp);
		
			if (mp != null && mp.contains(product)) {
				json.put("product_name", product.getName());
				json.put("model", product.getModel());
			} else {
				out.println(json.toJSONString());
			}
		}
		
	}
	
	private Set<Product> match(String title, Field field, Set<Product> filter) {
		
		title = StringUtil.normalize(title);
		
		String[] words = title.split(" ");
		
		Queue<TrieNode> cursors = new LinkedList<TrieNode>();
		Set<Product> products = null;
		Set<Product> nodeProducts = new HashSet<Product>();
		Set<Product> foundModelMatch = new HashSet<Product>();
		
		for (String word : words) {
			cursors.add(productTrie);
			for (int i = 0, n = cursors.size(); i < n; i++) {
				TrieNode node = cursors.remove();
				node = node.findDescendant(word);
				if (node != null) {
					List<ProductMatch> matches = (List<ProductMatch>) node.getData();
					if (matches != null) {
						nodeProducts.clear();
						for (ProductMatch match : matches) {
							Product product = match.getProduct();
							nodeProducts.add(product);
							if (match.getField() == field) {
								foundModelMatch.add(product);
							}
						}
						if (filter != null) { nodeProducts.retainAll(filter); }
						if (!nodeProducts.isEmpty()) {
							if (products == null) {
								products = new HashSet<Product>(nodeProducts);
							} else {
								products.retainAll(nodeProducts);
								if (products.isEmpty()) {
									break;
								}
							}
						}
					}
					cursors.add(node);
				}
			}
			
			if (products != null && products.isEmpty()) {
				break;
			}
		}
		
		if (products != null) {
			products.retainAll(foundModelMatch);
		}
		
		return products;

	}
	
	private Product matchOne(String s, Field field, Set<Product> filter) {
		Set<Product> products = match(s, field, filter);
		if (products != null && products.size() == 1) {
			Product[] p = products.toArray(new Product[1]);
			return p[0];
		}
		return null;
	}
	
}
