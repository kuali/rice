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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.core.util.Guid;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.util.OrmUtils;

/**
 * Business Object Base Business Object
 */
@MappedSuperclass
public abstract class PersistableBusinessObjectBase extends BusinessObjectBase implements PersistableBusinessObject {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PersistableBusinessObjectBase.class);
    @Version
    @Column(name="VER_NBR")
    protected Long versionNumber;
    @Column(name="OBJ_ID")
    private String objectId;
    @Transient
    private boolean newCollectionRecord;
    @Transient
    protected PersistableBusinessObjectExtension extension;

    // The following support notes on BusinessObjects (including DocumentHeader)
    @Transient
    private List boNotes = new ArrayList();
    @Transient
    private transient Boolean thisNotesSupport;
    @Transient
    private transient static Map notesSupport;
    
    // This is only a flag if a @Sequence is used and is set up explicitly on Maint Doc creation
    @Transient
    private boolean autoIncrementSet;
    
    public PersistableBusinessObjectBase() {
        autoIncrementSet = false;
    }
    
    public boolean isBoNotesSupport() {
        if (thisNotesSupport == null) {
            if (notesSupport == null) {
                notesSupport = new HashMap();
            }

            thisNotesSupport = (Boolean) notesSupport.get(getClass());
            if (thisNotesSupport == null) { // not cached
                if (LOG.isDebugEnabled()) {
                    LOG.debug("querying service for notesSupport state: " + getClass().getName());
                }
                thisNotesSupport = supportsBoNotes();
                if (thisNotesSupport == null)
                    thisNotesSupport = Boolean.FALSE;
                notesSupport.put(getClass(), thisNotesSupport);
            }
        }

        return thisNotesSupport.booleanValue();
    }

    protected Boolean supportsBoNotes() {
        return KNSServiceLocator.getBusinessObjectDictionaryService().areNotesSupported(getClass());
    }

    /**
     * @see org.kuali.core.bo.Persistable#getVersionNumber()
     */
    public Long getVersionNumber() {
        return versionNumber;
    }

    /**
     * @see org.kuali.core.bo.Persistable#setVersionNumber(java.lang.Long)
     */
    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }


    /**
     * getter for the guid based object id that is assignable to all objects, in order to support custom attributes a mapping must
     * also be added to the OJB file and a column must be added to the database for each business object that extension attributes
     * are supposed to work on.
     * 
     * @return
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * setter for the guid based object id that is assignable to all objects, in order to support custom attributes a mapping must
     * also be added to the OJB file and column must be added to the database for each business object that extension attributes are
     * supposed to work on.
     * 
     * @param objectId
     */
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }


    /**
     * Gets the newCollectionRecord attribute.
     * 
     * @return Returns the newCollectionRecord.
     */
    public boolean isNewCollectionRecord() {
        return newCollectionRecord;
    }

    /**
     * Sets the newCollectionRecord attribute value.
     * 
     * @param newCollectionRecord The newCollectionRecord to set.
     */
    public void setNewCollectionRecord(boolean isNewCollectionRecord) {
        this.newCollectionRecord = isNewCollectionRecord;
    }

    public void afterDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    }

    public void afterInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        if (isBoNotesSupport()) {
            if (!boNotes.isEmpty()) {
                saveNotes();

                // move attachments from pending directory
                if (this.hasNoteAttachments() && StringUtils.isNotEmpty(objectId)) {
                    KNSServiceLocator.getAttachmentService().moveAttachmentsWherePending(boNotes, objectId);
                }
            }
        }
    }

    public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        if (isBoNotesSupport()) {
            retrieveBoNotes();
        }
    }

    public void afterUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        if (isBoNotesSupport()) {
            if (!boNotes.isEmpty()) {
                saveNotes();

                // move attachments from pending directory
                if (this.hasNoteAttachments() && StringUtils.isNotEmpty(objectId)) {
                    KNSServiceLocator.getAttachmentService().moveAttachmentsWherePending(boNotes, objectId);
                }
            }
        }
    }

    public void beforeDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    }

    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        this.setObjectId(new Guid().toString());
    }

    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        if (StringUtils.isEmpty(this.getObjectId())) {
            this.setObjectId(new Guid().toString());
        }
    }
    
    @PrePersist
    public void beforeInsert() {
    	if (!isAutoIncrementSet()) {
    		OrmUtils.populateAutoIncValue(this, KNSServiceLocator.getEntityManagerFactory().createEntityManager());
    		setAutoIncrementSet(true);
    	}
    	setObjectId(new Guid().toString());
    }

    @PreUpdate
    public void beforeUpdate() {
        if (StringUtils.isEmpty(this.getObjectId())) {
            this.setObjectId(new Guid().toString());
        }
    }
    
    private void retrieveBoNotes() {
        this.boNotes = KNSServiceLocator.getNoteService().getByRemoteObjectId(this.objectId);
    }

    /**
     * This method is used to save any existing notes
     */
    private void saveNotes() {
        if (isBoNotesSupport()) {
            linkNoteRemoteObjectId();
            KNSServiceLocator.getNoteService().saveNoteList(this.getBoNotes());
        }
    }

    /**
     * This method links the note to the objectId
     */
    private void linkNoteRemoteObjectId() {
        List notes = getBoNotes();
        if (notes != null && !notes.isEmpty()) {
            for (Iterator iter = notes.iterator(); iter.hasNext();) {
                Note note = (Note) iter.next();
                note.setRemoteObjectIdentifier(getObjectId());
            }
        }
    }


    /**
     * getService Refreshes the reference objects from the primitive values.
     * 
     * @see org.kuali.core.bo.BusinessObject#refresh()
     */
    public void refresh() {
        KNSServiceLocator.getPersistenceService().retrieveNonKeyFields(this);
    }

    /**
     * @see org.kuali.core.bo.BusinessObject#refreshNonUpdateableReferences()
     */
    public void refreshNonUpdateableReferences() {
        KNSServiceLocator.getPersistenceService().refreshAllNonUpdatingReferences(this);
    }

    public void refreshReferenceObject(String referenceObjectName) {
        KNSServiceLocator.getPersistenceService().retrieveReferenceObject(this, referenceObjectName);
    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObject#buildListOfDeletionAwareLists()
     */
    public List buildListOfDeletionAwareLists() {
        return new ArrayList();
    }

    public void linkEditableUserFields() {
    }


    private boolean hasNoteAttachments() {
        for (Object obj : this.boNotes) {
            Note note = (Note) obj;
            if (note.getAttachment() != null) {
                return true;

            }
        }
        return false;
    }

    public List getBoNotes() {
        return boNotes;
    }

    public void setBoNotes(List boNotes) {
        this.boNotes = boNotes;
    }

    /**
     * return a note if in range, or an empty note if not
     * 
     * @param nbr
     * @return return a note if in range, or an empty note if not
     */
    public Note getBoNote(int nbr) {
        while (getBoNotes().size() <= nbr) {
            getBoNotes().add(new Note());
        }

        Note note = (Note) this.getBoNotes().get(nbr);

        // fix the primary key in case this is used for manual deleting
        if (note != null && StringUtils.isEmpty(note.getObjectId())) {
            note.setRemoteObjectIdentifier(getObjectId());
        }

        return note;
    }

    /**
     * This method adds a note
     * 
     * @param note
     * @return true if note added
     */
    public boolean addNote(Note note) {
        return this.getBoNotes().add(note);
    }

    public boolean deleteNote(Note note) {
        return this.getBoNotes().remove(note);
    }

	public PersistableBusinessObjectExtension getExtension() {
		if ( extension == null ) {
			try {
				Class extensionClass = KNSServiceLocator.getPersistenceStructureService().getBusinessObjectAttributeClass( this.getClass(), "extension" );
				if ( extensionClass != null ) {
					extension = (PersistableBusinessObjectExtension)extensionClass.newInstance();
				}
			} catch ( Exception ex ) {
				LOG.error( "unable to create extension object", ex );
			}
		}
		return extension;
	}

	public void setExtension(PersistableBusinessObjectExtension extension) {
		this.extension = extension;
	}

	public boolean isAutoIncrementSet() {
		return this.autoIncrementSet;
	}

	public void setAutoIncrementSet(boolean autoIncrementSet) {
		this.autoIncrementSet = autoIncrementSet;
	}

}
