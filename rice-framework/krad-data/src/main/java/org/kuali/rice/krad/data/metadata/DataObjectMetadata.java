/*
 * Copyright 2006-2013 The Kuali Foundation
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

import java.util.Collection;
import java.util.List;


public interface DataObjectMetadata extends MetadataCommon {

	/**
	 * The type represented by this metadata object. Usually this will simply contain the class name created
	 * by the persistence layer when it is loaded from the database.
	 * 
	 * Will never return null.
	 */
	Class<?> getType();

	/**
	 * Get all the attributes defined on the data object in the order given by the MetadataProvider. This may or may not
	 * be the same as the backing object's (table) and is most likely the order in which they appear in the source
	 * persistence metadata (XML or annotations)
	 * 
	 * Will never return null. Will return an empty list if no attributes defined.
	 */
	List<DataObjectAttribute> getAttributes();

	/**
	 * Get all the child collections defined on the data object in the order given by the MetadataProvider.
	 * 
	 * Will never return null. Will return an empty list if no collections defined.
	 */
	List<DataObjectCollection> getCollections();

	/**
	 * Get all the child relationships defined on the data object in the order given by the MetadataProvider.
	 * 
	 * Will never return null. Will return an empty list if no relationships defined.
	 */
	List<DataObjectRelationship> getRelationships();

	/**
	 * Get the named attribute's metadata from the data object.
	 * 
	 * @return <b>null</b> if the attributeName does not exist, the associated {@link DataObjectAttribute} otherwise.
	 */
	DataObjectAttribute getAttribute(String attributeName);

	/**
	 * Get the named collection's metadata from the data object. The name is the property on the data object which holds
	 * the {@link Collection}.
	 * 
	 * @return <b>null</b> if the attributeName does not exist, the associated {@link DataObjectCollection} otherwise.
	 */
	DataObjectCollection getCollection(String collectionName);

	/**
	 * Get the named relationship's metadata from the data object. The name is the property on the data object which
	 * holds the related business object's instance.
	 * 
	 * @return <b>null</b> if the attributeName does not exist, the associated {@link DataObjectRelationship} otherwise.
	 */
	DataObjectRelationship getRelationship(String relationshipName);

	/**
	 * Returns all relationships of which the given attribute is part of the foreign key relationship.
	 * 
	 * @return The list of relationship metadata objects or an empty list if none found.
	 */
	List<DataObjectRelationship> getRelationshipsInvolvingAttribute(String attributeName);

	/**
	 * Returns a single relationship for which the given attribute is the last in the foreign key relationship.
	 * 
	 * @return <b>null</b> if no relationship's foreign key set ends wit the given field. The
	 *         {@link DataObjectRelationship} otherwise.
	 */
	DataObjectRelationship getRelationshipByLastAttributeInRelationship(String attributeName);

	/**
	 * Get the list of primary key attribute names for this data object.
	 */
    List<String> getPrimaryKeyAttributeNames();

	/**
	 * List of attribute names which form a "user friendly" key. (As opposed to a sequence number as used by some parts
	 * of the system.)
	 * 
	 * An example here would be the KIM Role object where the Role ID is the primary key, but the Namespace and Name
	 * properties form the user-visible and enterable key.
	 * 
	 * @return a list containing the business key attributes names for the data object
	 */
	List<String> getBusinessKeyAttributeNames();

	/**
	 * Returns true if the list of primary key names and business key attribute names are different.
	 * 
	 * @return true if the list of primary key names and business key attributes are different, false if they are the
     *         same
	 */
	Boolean hasDistinctBusinessKey();

    /**
     * This is the field on the object which best represents it on displays.  It will be used to build
     * inquiry links and determine where to place quickfinder links.  Usually this will be the the primary key
     * or the last field of the primary key if there are multiple fields.
     *
     * If not specified by the provider, the base implementation will default it to the last attribute in the
     * primaryKeyAttributeNames list.
     *
     * @return the name of the attribute to use for primary display purposes
     */
    String getPrimaryDisplayAttributeName(); // KNS: titleAttribute

	/**
	 * Returns true if the underlying ORM tool performs optimistic locking checks on this object before saving. Under
	 * the KNS, this was done via the versionNumber property and appropriate OJB configuration. In JPA, this is linked
	 * to the @Version annotation.
	 * 
	 * @return true if this data object is configured for optimistic locking
	 */
	boolean isSupportsOptimisticLocking();

}
