package org.kuali.rice.krad.data.metadata;

import java.io.Serializable;

/**
 * Represents a relationship between a parent and child attribute. Used in the context of a {@link MetadataChild}
 * object.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataObjectAttributeRelationship extends Serializable {

	/**
	 * The property on the "parent" data object.
	 */
	String getParentAttributeName();

	/**
	 * The matching property on the "child", usually part of the child data object's primary key.
	 */
	String getChildAttributeName();

}