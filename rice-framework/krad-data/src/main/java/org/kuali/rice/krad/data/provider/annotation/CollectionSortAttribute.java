package org.kuali.rice.krad.data.provider.annotation;

import org.kuali.rice.krad.data.metadata.SortDirection;

public @interface CollectionSortAttribute {
	String value();

	SortDirection sortDirection() default SortDirection.ASCENDING;
}
