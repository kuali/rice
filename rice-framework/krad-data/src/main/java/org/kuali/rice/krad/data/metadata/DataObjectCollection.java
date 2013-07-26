package org.kuali.rice.krad.data.metadata;

import java.util.List;

/**
 * Represents the metadata for a collection within a data object.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataObjectCollection extends MetadataChild {

	/**
	 * The elementLabel defines the name to be used for a single object within the collection.
	 * 
	 * For example: "Address" may be the name of one object within the "Addresses" collection.
	 */
	String getElementLabel();

	/**
	 * The minimum number of items which may be in the collection.
	 * 
	 * This is an optional attribute and may not be set by the metadata provider.
	 * 
	 * @return The minimum number of items which must be in this collection or <b>null</b> if unset.
	 */
	Long getMinItems();

	/**
	 * The maximum number of items which may be in the collection.
	 * 
	 * This is an optional attribute and may not be set by the metadata provider.
	 * 
	 * @return The maximum number of items which may be in this collection or <b>null</b> if unset.
	 */
	Long getMaxItems();

	/**
	 * The default ordering of collection items as specified by the metadata provider.
	 * 
	 * @return The list of fields in order by which to sort, or an empty list if none specified.
	 */
	List<DataObjectCollectionSortAttribute> getDefaultOrdering();

	/**
	 * Whether the referenced collection uses a linking object in the underlying implementation. (In case that is
	 * somehow important to using code.)
	 */
	boolean isIndirectCollection();
}