/**
 * Copyright 2005-2014 The Kuali Foundation
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

import com.google.common.annotations.Beta;
import org.kuali.rice.krad.data.provider.annotation.UifAutoCreateViewType;

import java.util.Collection;
import java.util.List;


/**
* Metadata for a given data object type.
*
* <p>
* References the data object class and contains lists of all the attributes, collections, and relationships within
* the class.
* </p>
*
* @author Kuali Rice Team (rice.collab@kuali.org)
*/
public interface DataObjectMetadata extends MetadataCommon {

    /**
    * Gets metadata object type.
    *
    * <p>
    * The type represented by this metadata object. Usually this will simply contain the class name created
    * by the persistence layer when it is loaded from the database.
    * </p>
    *
    * @return metadata object type. Will never return null.
    */
	Class<?> getType();

    /**
    * Gets attributes defined on the data object.
    *
    * <p>
    * Gets all the attributes defined on the data object in the order given by the MetadataProvider. This may or may not
    * be the same as the backing object's (table) and is most likely the order in which they appear in the source
    * persistence metadata (XML or annotations).
    * </p>
    *
    * @return Data object attributes. Will never return null. Will return an empty list if no attributes defined.
    */
	List<DataObjectAttribute> getAttributes();

    /**
    * Gets child collections.
    *
    * <p>
    * Gets all the child collections defined on the data object in the order given by the MetadataProvider.
    * </p>
    *
    * @return Child collections. Will never return null. Will return an empty list if no collections defined.
    */
	List<DataObjectCollection> getCollections();

    /**
    * Gets child relationships.
    *
    * <p>
    * Gets all the child relationships defined on the data object in the order given by the MetadataProvider.
    * </p>
    *
    * @return Child relationships. Will never return null. Will return an empty list if no relationships defined.
    */
	List<DataObjectRelationship> getRelationships();

    /**
    * Gets attribute metadata.
    *
    * <p>
    * Get the named attribute's metadata from the data object.
    * </p>
    *
    * @return <b>null</b> if the attributeName does not exist, the associated {@link DataObjectAttribute} otherwise.
    */
	DataObjectAttribute getAttribute(String attributeName);

    /**
    * Gets the named collection's metadata from the data object.
    *
    * <p>
    * The name is the property on the data object which holds
    * the {@link Collection}.
    * </p>
    *
    * @return <b>null</b> if the attributeName does not exist, the associated {@link DataObjectCollection} otherwise.
    */
	DataObjectCollection getCollection(String collectionName);

    /**
    * Gets the named relationship's metadata from the data object.
    *
    * <p>
    * The name is the property on the data object which holds the related business object's instance.
    * </p>
    *
    * @return <b>null</b> if the attributeName does not exist, the associated {@link DataObjectRelationship} otherwise.
    */
	DataObjectRelationship getRelationship(String relationshipName);

    /**
    * Gets attribute relationships.
    *
    * <p>
    * Returns all relationships of which the given attribute is part of the foreign key relationship.
    * </p>
    *
    * @return The list of relationship metadata objects or an empty list if none found.
    */
	List<DataObjectRelationship> getRelationshipsInvolvingAttribute(String attributeName);

	/**
	 * Gets relationship of last attribute.
     *
     * <p>
     * Returns a single relationship for which the given attribute is the last in the foreign key relationship.
	 * </p>
     *
	 * @return <b>null</b> if no relationship's foreign key set ends wit the given field. The
	 *         {@link DataObjectRelationship} otherwise.
	 */
	DataObjectRelationship getRelationshipByLastAttributeInRelationship(String attributeName);

    /**
    * Get the list of primary key attribute names for this data object.
    *
    * @return primary key attribute names.
    */
    List<String> getPrimaryKeyAttributeNames();

    /**
    * List of attribute names which form a "user friendly" key. (As opposed to a sequence number as used by some parts
    * of the system).
    *
    * <p>
    * An example here would be the KIM Role object where the Role ID is the primary key, but the Namespace and Name
    * properties form the user-visible and enterable key.
    * </p>
    *
    * @return a list containing the business key attributes names for the data object.
    */
	List<String> getBusinessKeyAttributeNames();

    /**
    * Returns true if the list of primary key names and business key attribute names are different.
    *
    * @return true if the list of primary key names and business key attributes are different, false if they are the
    *         same.
    */
	Boolean hasDistinctBusinessKey();

    /**
     * Gets primary display attribute name
     *
     * <p>
     * This is the field on the object which best represents it on displays.  It will be used to build
     * inquiry links and determine where to place quickfinder links.  Usually this will be the the primary key
     * or the last field of the primary key if there are multiple fields.
     * </p>
     * <p>
     * If not specified by the provider, the base implementation will default it to the last attribute in the
     * primaryKeyAttributeNames list.
     * </p>
     *
     * @return the name of the attribute to use for primary display purposes.
     */
    String getPrimaryDisplayAttributeName(); // KNS: titleAttribute

    /**
    * Determines whether optimistic locking is supported.
    *
    * <p>
    * Returns true if the underlying ORM tool performs optimistic locking checks on this object before saving. Under
    * the KNS, this was done via the versionNumber property and appropriate OJB configuration. In JPA, this is linked
    * to the @Version annotation.
    * </p>
    *
    * @return true if this data object is configured for optimistic locking.
    */
	boolean isSupportsOptimisticLocking();

    /**
    * BETA: Gets auto create uif view types.
    *
    * <p>
    * Returns collections of uif view types that should be auto created.
    * </p>
    *
    * @return collection of uif view types.
    */
    @Beta
	Collection<UifAutoCreateViewType> getAutoCreateUifViewTypes();

    /**
    * BETA: Determines where view type should be auto created.
    *
    * <p>
    * Determines whether the specified uif view type can be auto created.
    * </p>
    *
    * @return true if this uif view type should be auto created.
    */
    @Beta
	boolean shouldAutoCreateUifViewOfType(UifAutoCreateViewType viewType);
}
