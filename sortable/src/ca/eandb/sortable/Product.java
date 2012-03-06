/**
 * 
 */
package ca.eandb.sortable;

import java.util.Date;

/**
 * @author Brad Kimmel
 *
 */
public final class Product {
	
	public static enum Field {
		NAME,
		MANUFACTURER,
		MODEL,
		FAMILY,
		ANNOUNCED_DATE
	};

	private final String name;
	
	private final String manufacturer;
	
	private final String model;
	
	private final String family;
	
	private final String announcedDate;

	/**
	 * @param name
	 * @param manufacturer
	 * @param model
	 * @param family
	 * @param announcedDate
	 */
	public Product(String name, String manufacturer, String model,
			String family, String announcedDate) {
		this.name = name;
		this.manufacturer = manufacturer;
		this.model = model;
		this.family = family;
		this.announcedDate = announcedDate;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the manufacturer
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * @return the model
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @return the family
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * @return the announcedDate
	 */
	public String getAnnouncedDate() {
		return announcedDate;
	}
	
}
