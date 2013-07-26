package org.kuali.rice.krad.data.metadata;

/**
 * Sorting orders used for collection definitions and lookup generation.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public enum SortDirection {
	ASCENDING,
	DESCENDING,
	// These latter are Oracle options on table sorting. However, recent ANSI
	// standards have extended them as requiring to be understood by all databases.
	// (Though not all support the style of sorting.)
	ASCENDING_NULLS_FIRST,
	ASCENDING_NULLS_LAST,
	DESCENDING_NULLS_FIRST,
	DESCENDING_NULLS_LAST
}
