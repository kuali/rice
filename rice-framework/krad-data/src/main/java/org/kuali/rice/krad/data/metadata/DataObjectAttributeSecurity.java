package org.kuali.rice.krad.data.metadata;

import java.io.Serializable;

import org.kuali.rice.krad.data.metadata.impl.security.DataObjectAttributeMaskFormatter;

/**
 * Defines a set of restrictions that are possible on an attribute.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface DataObjectAttributeSecurity extends Serializable {

	boolean isReadOnly();
	boolean isHide();
	boolean isMask();
	boolean isPartialMask();

	DataObjectAttributeMaskFormatter getMaskFormatter();
	DataObjectAttributeMaskFormatter getPartialMaskFormatter();

	/**
	 * Returns whether any of the restrictions defined in this class are true.
	 */
	boolean hasAnyRestriction();

}