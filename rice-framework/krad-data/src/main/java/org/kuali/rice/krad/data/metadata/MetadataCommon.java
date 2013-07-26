/**
 * Copyright 2005-2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.data.metadata;

import java.io.Serializable;

/**
 * An interface for attributes common to all primary metadata objects. (name, backing object, labels, etc...)
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)

 * 
 */
public interface MetadataCommon extends Serializable {

	/**
	 * {@link MetadataMergeAction} which determines how to handle the embedding of this object when multiple metadata
	 * providers are in use.
	 * 
	 * @see MetadataMergeAction
	 * @return the merge action to use when embedding this metadata when multiple metadata providers are in use
	 */
	MetadataMergeAction getMergeAction();

	/**
	 * An object representing the object for purposes of merging. This should return an attribute name or a unique data
	 * type object.
	 * 
	 * Whatever the class, it must have proper hashCode() and equals() semantics and not rely on object identity.
	 * 
	 * This method must not return null;
	 */
	Object getUniqueKeyForMerging();

	/**
	 * Provider specific name of the persistent storage behind this object type. For a data object, this would likely be
	 * the table name. For an attribute, this would be the table column name. It is to be used for reference purposes
	 * only.
	 * 
	 * @return String representing the backing object. Must not return null.
	 */
	String getBackingObjectName();

	/**
	 * The name of the object as known to the system. This would be the class name, attribute name, etc...
	 */
	String getName();

	/**
	 * The user displayed name of the object.
	 */
	String getLabel();

	/**
	 * A shorter version of the user displayed name of the object.
	 */
	String getShortLabel();

	/**
	 * A longer description of the object.
	 * 
	 * TODO: what is this used for?
	 */
	String getDescription();

	/**
	 * An even longer description of the object?
	 */
	String getSummary();

	/**
	 * Whether this metadata object should be considered read-only by calling code.
	 * 
	 * That is, the persistence layer is not likely to accept/persist an update to this object, attribute, collection,
	 * reference.
	 * 
	 */
	boolean isReadOnly();
}
