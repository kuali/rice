package org.kuali.rice.krad.data.metadata;

import org.kuali.rice.krad.data.provider.CompositeMetadataProvider;

/**
 * A component of {@link MetadataCommon} which specifies what to do when a duplicate data object, attribute, collection
 * or reference is encountered during the merging performed by the {@link CompositeMetadataProvider}.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)

 * 
 */
public enum MetadataMergeAction {
	/**
	 * The default behavior. Unset attributes will be left alone.
	 */
	MERGE,
	/**
	 * If a match is found (same data object or attribute name), the existing object will be replaced completely by this
	 * one.
	 */
	REPLACE,
	/**
	 * If a match is found (same data object or attribute name), the existing object will be removed. (Any attribute
	 * except that forming the key (type or attribute name) can be left unset.)
	 */
	REMOVE,
	/**
	 * If a match is found (same data object or attribute name), the existing object will be left alone. The metadata
	 * object will only be included if there is not already an existing object.
	 */
	NO_OVERRIDE
}
