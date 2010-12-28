/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.rice.kns.document;

import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.maintenance.Maintainable;


/**
 * Common interface for all maintenance documents.
 */
public interface MaintenanceDocument extends Document {

    /**
     * @return String containing the xml representation of the maintenance document
     */
    public String getXmlDocumentContents();

    /**
     * @return Maintainable which holds the new maintenance record
     */
    public Maintainable getNewMaintainableObject();

    /**
     * @return Maintainable which holds the old maintenance record
     */
    public Maintainable getOldMaintainableObject();

    /**
     * Sets the xml contents of the maintenance document
     * 
     * @param documentContents - String xml
     */
    public void setXmlDocumentContents(String documentContents);

    /**
     * @param newMaintainableObject - Initializes the new maintainable
     */
    public void setNewMaintainableObject(Maintainable newMaintainableObject);

    /**
     * @param newMaintainableObject - Initializes the old maintainable
     */
    public void setOldMaintainableObject(Maintainable oldMaintainableObject);

    /**
     * Returns a reference to the PersistableBusinessObject that this MaintenanceDocument is maintaining.
     */
    public PersistableBusinessObject getDocumentBusinessObject();
    
    /**
     * Builds the xml document string from the contents of the old and new maintainbles.
     */
    public void populateXmlDocumentContentsFromMaintainables();

    /**
     * Populates the old and new maintainables from the xml document contents string.
     */
    public void populateMaintainablesFromXmlDocumentContents();

    /**
     * @return boolean - indicates whether this is an edit or new maintenace document by the existence of an old maintainable
     */
    public boolean isOldBusinessObjectInDocument();

    /**
     * 
     * Returns true if this maintenance document is creating a new Business Object, false if its an edit.
     * 
     */
    public boolean isNew();

    /**
     * 
     * Returns true if this maintenance document is editing an existing Business Object, false if its creating a new one.
     * 
     */
    public boolean isEdit();

    /**
     * 
     * Returns true if this maintenance document is creating a new Business Object out of an existing Business Object,
     * for example, a new division vendor out of an existing parent vendor.
     * 
     */
    public boolean isNewWithExisting();
    
    /**
     * 
     * A flag which indicates whether the primary keys have been cleared on a Copy-type of document. This will be true if the 'clear
     * keys on a copy' has been done, and it will be false if not.
     * 
     * @return true if the primary keys have been cleared already, false if not.
     * 
     */
    public boolean isFieldsClearedOnCopy();

    /**
     * 
     * This method sets the value of the fieldsClearedOnCopy.
     * 
     * @param fieldsClearedOnCopy - true or false
     * 
     */
    public void setFieldsClearedOnCopy(boolean keysClearedOnCopy);

    /**
     * 
     * This method...
     * @return
     */
    public boolean getDisplayTopicFieldInNotes();

    /**
     * 
     * This method...
     */
    public void setDisplayTopicFieldInNotes(boolean displayTopicFieldInNotes);

}
