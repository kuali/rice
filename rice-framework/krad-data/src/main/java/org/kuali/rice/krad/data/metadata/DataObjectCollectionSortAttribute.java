package org.kuali.rice.krad.data.metadata;

import java.io.Serializable;

/**
 * Represents a single field on which to sort a {@link DataObjectCollection} within a {@link DataObjectMetadata}.
 * 
 * The collection may hold multiple of these objects to support sorting by multiple fields.
 */
public interface DataObjectCollectionSortAttribute extends Serializable {

	/**
	 * The attribute name on which to sort the collection.
	 */
	String getAttributeName();

	/**
	 * For this attribute, which way should we sort?
	 */
	SortDirection getSortDirection();

}