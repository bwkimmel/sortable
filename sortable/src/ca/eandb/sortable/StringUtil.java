/**
 * 
 */
package ca.eandb.sortable;

import java.text.Normalizer;

/**
 * @author brad
 *
 */
public final class StringUtil {
	
	public static String normalize(String s) {
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		s = s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		s = s.toLowerCase();
		s = s.replaceAll("[^a-z0-9]+", " ");
		return s;
	}
	
	/** Private constructor. */
	private StringUtil() {}

}
