/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.core.bo;

import java.util.List;

import org.apache.ojb.broker.PersistenceBrokerAware;

/**
 * Declares common methods for all persistable objects.
 */
public interface PersistableBusinessObject extends BusinessObject, PersistenceBrokerAware {

    /**
     * @return object versionm number, used by optimistic locking
     */
    public Long getVersionNumber();

    /**
     * Sets object's version number, used by optimistic locking
     * 
     * @param versionNumber
     */
    public void setVersionNumber(Long versionNumber);
    
    /**
     * @return objectID, the unique identifier for the object 
     */
    public String getObjectId();

    /**
     * Sets the unique identifer for the object
     * 
     * @param objectId
     */
    public void setObjectId(String objectId);

    
    public abstract boolean isBoNotesSupport();
    
    /**
     * @return Returns the boNotes List.
     */
    public abstract List getBoNotes();

    /**
     * @see org.kuali.core.bo.BusinessObject#refreshNonUpdateableReferences()
     */
    public abstract void refreshNonUpdateableReferences();

    /**
     * Refreshes the reference objects and non-key fields using the primitive values; only if the object is "empty" - i.e. objectId
     * is null or empty string.
     */
    public abstract void refreshIfEmpty();

    /**
     * This method is used to refresh a reference object that hangs off of a document. For example, if the attribute's keys were
     * updated for a reference object, but the reference object wasn't, this method would go out and retrieve the reference object.
     * 
     * @param referenceObjectName
     */
    public abstract void refreshReferenceObject(String referenceObjectName);

    /**
     * If this method is not implemented appropriately for PersistableBusinessObject with collections, then PersistableBusinessObject with collections will not persist deletions correctly.
     * Elements that have been deleted will reappear in the DB after retrieval.
     * 
     * @return List of collections which need to be monitored for changes by OJB
     */
    public List buildListOfDeletionAwareLists();

    /**
     * Returns the boolean indicating whether this record is a new record of a maintenance document collection.
     * Used to determine whether the record can be deleted on the document.
     */
    public boolean isNewCollectionRecord();
    
    /**
     * Sets the boolean indicating this record is a new record of a maintenance document collection.
     * Used to determine whether the record can be deleted on the document.
     */
    public void setNewCollectionRecord(boolean isNewCollectionRecord);

    /**
     * Hook to link in any editable user fields.
     */
    public void linkEditableUserFields();
    
    public Note getBoNote(int nbr);

    public boolean addNote(Note note);

    public boolean deleteNote(Note note);

    public PersistableBusinessObjectExtension getExtension();
}