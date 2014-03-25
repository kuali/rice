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

import java.util.List;

/**
* Non-top-level metadata object
*
* <p>
* Interface shared by all non-top-level metadata objects which link to other persistable objects. This is used as the
* base interface for 1:1/M:1 Relationships and 1:M/N:M Collections.
* </p>
*
* @author Kuali Rice Team (rice.collab@kuali.org)
*/
public interface MetadataChild extends MetadataCommon {

    /**
    * Gets the type of related object
    *
    * <p>
    * This is the type of the object referenced by this relationship or contained in this collection.
    * </p>
    *
    * @return type of related object
    */
	Class<?> getRelatedType();

    /**
    * Gets the parent-child related fields
    *
    * <p>
    * Returns the related fields between the parent and child objects.
    * </p>
    *
    * @return related fields. List must not be empty. There always must be at least one related field.
    */
	List<DataObjectAttributeRelationship> getAttributeRelationships();

    /**
    * Gets bi-directional relationship
    *
    * <p>
    * If this metadata element is part of a bi-directional relationship, this method returns the other side of the
    * bi-directional relationship.
    * </p>
    *
    * @return the inverse of this relationship if it is bi-directional, false otherwise
    */
    MetadataChild getInverseRelationship();

    /**
    * Determines whether object automatically saved
    *
    * <p>
    * For related objects, whether this object will be automatically saved when the containing object is persisted.
    * </p>
    *
    * @return whether object is automatically saved
    */
	boolean isSavedWithParent();

    /**
    * Determines whether this object will be automatically deleted when the containing object is deleted.
    *
    * <p>
    * This is a special case of the {@link #isSavedWithParent()} method. It probably would never be true if the
    * {@link #isSavedWithParent()} returns false.
    * </p>
    *
    * @return whether automatically deleted
    */
	boolean isDeletedWithParent();

    /**
    * Determines whether object will be loaded with parent
    *
    * <p>
    * For related objects, whether this related object will be loaded from the persistence layer at the same time as
    * the parent object.
    * </p>
    * <p>
    * If false, the object will be loaded upon demand, either via automatic lazy-loading provided by the infrastructure
    * or by explicit request.
    * </p>
    *
    * @return whether object
    */
	boolean isLoadedAtParentLoadTime();

    /**
    * Determines whether the object is reloaded automatically with parent
    *
    * <p>
    * For related objects, whether this related object will be loaded from the persistence layer automatically when it
    * is accessed by client code.
    * </p>
    * <p>
    * If false, then the object must be refreshed manually by client code. (Though such a refresh may be possible by
    * requesting the refresh from the persistence provider.)
    * </p>
    *
    * @return whether object loaded automatically with parent
    */
	boolean isLoadedDynamicallyUponUse();

    /**
    * Gets foreign key attribute from parent.
    *
    * <p>
    * For a given child key attribute, return the matching foreign key attribute on the parent object.
    * </p>
    *
    * @return null if the attribute name given is not part of the key relationship.
    */
	String getParentAttributeNameRelatedToChildAttributeName(String childAttribute);
}
