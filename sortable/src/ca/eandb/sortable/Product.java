/**
 * 
 */
package ca.eandb.sortable;


/**
 * An entity object representing a product.
 * @author Brad Kimmel
 */
public final class Product {
	
	/** An enumeration of the fields within this entity. */
	public static enum Field {
		NAME,
		MANUFACTURER,
		MODEL,
		FAMILY,
		ANNOUNCED_DATE
	};

	/** The product name (key field). */
	private final String name;
	
	/** The name of the manufacturer. */
	private final String manufacturer;
	
	/** The model name/number. */
	private final String model;
	
	/** The family of models. */
	private final String family;
	
	/**
	 * The date the product was announced.  Ideally, this would be a
	 * <code>Date</code> field, but to keep things simple we've left it as a
	 * <code>String</code>, since we don't use this field anyway.
	 */
	private final String announcedDate;

	/**
	 * Creates a new <code>Product</code>.
	 * @param name The product name.
	 * @param manufacturer The name of the manufacturer.
	 * @param model The model name/number.
	 * @param family The family of models.
	 * @param announcedDate The date the product was announced.
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
	 * Gets the product name.
	 * @return The product name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the name of the manufacturer.
	 * @return The name of the manufacturer.
	 */
	public String getManufacturer() {
		return manufacturer;
	}

	/**
	 * Gets the model name/number.
	 * @return The model name/number.
	 */
	public String getModel() {
		return model;
	}

	/**
	 * Gets the name of the family of models.
	 * @return The name of the family of models.
	 */
	public String getFamily() {
		return family;
	}

	/**
	 * Gets the date the product was announced.
	 * @return The date the product was announced.
	 */
	public String getAnnouncedDate() {
		return announcedDate;
	}
	
}
