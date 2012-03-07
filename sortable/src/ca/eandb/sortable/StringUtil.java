/**
 * 
 */
package ca.eandb.sortable;

import java.text.Normalizer;

/**
 * String-related utility methods.
 * @author Brad Kimmel
 */
public final class StringUtil {
	
	/**
	 * Normalizes a string by removing features that should not be considered
	 * as differentiating between two strings (such as accents and case).
	 * @param s The <code>String</code> to normalize.
	 * @return A canonical representation of <code>String</code>.
	 */
	public static String normalize(String s) {
		
		s = Normalizer.normalize(s, Normalizer.Form.NFD);
		
		// remove accents
		s = s.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		
		// make everything lower-case to make all comparisons case-insensitive. 
		s = s.toLowerCase();
		
		// only consider alphanumeric characters -- remove all others
		s = s.replaceAll("[^a-z0-9]+", " ");
		
		/* add word break at letter-number boundaries.  Model numbers composed
		 * of letters and numbers should be treated as being composed of
		 * separate words, since in some listings they may be written with
		 * or without intervening characters.
		 */		  
		s = s.replaceAll("([a-z])([0-9])", "$1 $2");				
		s = s.replaceAll("([0-9])([a-z])", "$1 $2");
		
		return s;
		
	}
	
	/** Private constructor. */
	private StringUtil() {}

}
