package org.kuali.rice.krad.data.metadata;

import java.util.List;

/**
 * Interface shared by all non-top-level metadata objects which link to other persistable objects. This is used as the
 * base interface for 1:1/M:1 Relationships and 1:M/N:M Collections.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface MetadataChild extends MetadataCommon {

	/**
	 * This is the type of the object referenced by this relationship or contained in this collection.
	 */
	Class<?> getRelatedType();

	/**
	 * Returns the related fields between the parent and child objects.
	 * 
	 * List must not be empty. There always must be at least one related field.
	 */
	List<DataObjectAttributeRelationship> getAttributeRelationships();

	/**
	 * For related objects, whether this object will be automatically saved when the containing object is persisted.
	 */
	boolean isSavedWithParent();

	/**
	 * For related objects, whether this object will be automatically deleted when the containing object is deleted.
	 * 
	 * This is a special case of the {@link #isSavedWithParent()} method. It probably would never be true if the
	 * {@link #isSavedWithParent()} returns false.
	 */
	boolean isDeletedWithParent();

	/**
	 * For related objects, whether this related object will be loaded from the persistence layer at the same time as
	 * the parent object.
	 * 
	 * If false, the object will be loaded upon demand, either via automatic lazy-loading provided by the infrastructure
	 * or by explicit request.
	 */
	boolean isLoadedAtParentLoadTime();

	/**
	 * For related objects, whether this related object will be loaded from the persistence layer automatically when it
	 * is accessed by client code.
	 * 
	 * If false, then the object must be refreshed manually by client code. (Though such a refresh may be possible by
	 * requesting the refresh from the persistence provider.)
	 */
	boolean isLoadedDynamicallyUponUse();

    /**
	 * For a given child key attribute, return the matching foreign key attribute on the parent object.
	 * 
	 * Will return null if the attribute name given is not part of the key relationship.
	 */
	String getParentAttributeNameRelatedToChildAttributeName(String childAttribute);
}
