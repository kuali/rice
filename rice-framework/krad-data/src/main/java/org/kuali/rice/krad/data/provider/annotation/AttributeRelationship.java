package org.kuali.rice.krad.data.provider.annotation;

import org.kuali.rice.krad.data.metadata.DataObjectAttributeRelationship;

/**
 * Defines a relationship between attributes on two data objects. Analog to the {@link DataObjectAttributeRelationship}
 * metadata.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public @interface AttributeRelationship {
	String parentAttributeName();

	String childAttributeName();
}
