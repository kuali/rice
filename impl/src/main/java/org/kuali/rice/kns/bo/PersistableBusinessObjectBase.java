/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kns.bo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.struts.upload.FormFile;
import org.kuali.rice.core.util.OrmUtils;
import org.kuali.rice.kns.service.AttachmentService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.Guid;

/**
 * Business Object Base Business Object
 */
@MappedSuperclass
public abstract class PersistableBusinessObjectBase extends BusinessObjectBase implements PersistableBusinessObject {
    private static final long serialVersionUID = 1451642350593233282L;
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
    @Transient
    private transient FormFile attachmentFile;

    // The following support notes on BusinessObjects (including DocumentHeader)
    @Transient
    private List boNotes = null;
    @Transient
    private transient Boolean thisNotesSupport;
    @Transient
    private transient static Map<Class<? extends PersistableBusinessObjectBase>,Boolean> notesSupportCache = new HashMap<Class<? extends PersistableBusinessObjectBase>,Boolean>();
    
    @Transient
    private static transient AttachmentService attachmentService;
    @Transient
    private static transient PersistenceService persistenceService;
    @Transient
    private static transient PersistenceStructureService persistenceStructureService;
    @Transient
    private static transient NoteService noteService;
    
    // This is only a flag if a @Sequence is used and is set up explicitly on Maint Doc creation
    @Transient
    private boolean autoIncrementSet;
    
    public PersistableBusinessObjectBase() {
        autoIncrementSet = false;
    }
    
    public boolean isBoNotesSupport() {
        if (thisNotesSupport == null) {
            thisNotesSupport = notesSupportCache.get(getClass());
            if (thisNotesSupport == null) { // not cached
                if (LOG.isDebugEnabled()) {
                    LOG.debug("querying service for notesSupport state: " + getClass().getName());
                }
                // protect against concurrent modification to the cached map
                synchronized ( notesSupportCache ) {
                    thisNotesSupport = supportsBoNotes();
                    if (thisNotesSupport == null) {
                        thisNotesSupport = Boolean.FALSE;
                    }
                    notesSupportCache.put(getClass(), thisNotesSupport);
				}
            }
        }

        return thisNotesSupport;
    }

    protected Boolean supportsBoNotes() {
        return KNSServiceLocator.getBusinessObjectDictionaryService().areNotesSupported(getClass());
    }

    /**
     * @see org.kuali.rice.kns.bo.Persistable#getVersionNumber()
     */
    public Long getVersionNumber() {
        return versionNumber;
    }

    /**
     * @see org.kuali.rice.kns.bo.Persistable#setVersionNumber(java.lang.Long)
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
    	// no need to attempt to load any notes since this is a new object
        if (boNotes != null && !boNotes.isEmpty()) {
        	if (isBoNotesSupport()) {
                saveNotes();

                // move attachments from pending directory
                if (hasNoteAttachments() && StringUtils.isNotEmpty(objectId)) {
                    getAttachmentService().moveAttachmentsWherePending(getBoNotes(), objectId);
                }
            }
        }
    }

    public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    }

    public void afterUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	// if the bo notes have not been loaded yet, then there is no need to attempt to save them
    	// also, the saveNotes call never attempts to remove notes removed from the note list
    	// so, an empty list will have no affect during the save process
        if (boNotes != null && !boNotes.isEmpty()) {
        	if (isBoNotesSupport()) {
                saveNotes();

                // move attachments from pending directory
                if (hasNoteAttachments() && StringUtils.isNotEmpty(objectId)) {
                    getAttachmentService().moveAttachmentsWherePending(getBoNotes(), objectId);
                }
            }
        }
    }

    public void beforeDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    }

    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        setObjectId(new Guid().toString());
    }

    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	beforeUpdate();
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
        if (StringUtils.isEmpty(getObjectId())) {
            setObjectId(new Guid().toString());
        }
    }
    
    private void retrieveBoNotes() {
        boNotes = getNoteService().getByRemoteObjectId(objectId);
    }

    /**
     * This method is used to save any existing notes
     */
    private void saveNotes() {
        if (isBoNotesSupport()) {
            linkNoteRemoteObjectId();
            getNoteService().saveNoteList(getBoNotes());
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
     * @see org.kuali.rice.kns.bo.BusinessObject#refresh()
     */
    public void refresh() {
        getPersistenceService().retrieveNonKeyFields(this);
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObject#refreshNonUpdateableReferences()
     */
    public void refreshNonUpdateableReferences() {
        getPersistenceService().refreshAllNonUpdatingReferences(this);
    }

	public void refreshReferenceObject(String referenceObjectName) {
		if ( StringUtils.isNotBlank(referenceObjectName) && !StringUtils.equals(referenceObjectName, "extension")) {
			final PersistenceStructureService pss = getPersistenceStructureService();
			if ( pss.hasReference(this.getClass(), referenceObjectName) || pss.hasCollection(this.getClass(), referenceObjectName)) {
            	getPersistenceService().retrieveReferenceObject( this, referenceObjectName);
			} else {
                LOG.warn( "refreshReferenceObject() called with non-reference property: " + referenceObjectName );
			}
		}
	}

    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObject#buildListOfDeletionAwareLists()
     */
    public List buildListOfDeletionAwareLists() {
        return new ArrayList();
    }

    public void linkEditableUserFields() {
    }


    private boolean hasNoteAttachments() {
        for (Object obj : getBoNotes()) {
            Note note = (Note) obj;
            if (note.getAttachment() != null) {
                return true;

            }
        }
        return false;
    }

    public List getBoNotes() {
    	if ( boNotes == null ) {
			if (isBoNotesSupport()) {
				retrieveBoNotes();
			}
			// ensure that the list is not null after this point
			if ( boNotes == null ) {
				boNotes = new ArrayList(0);
			}
    	}
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

        Note note = (Note) getBoNotes().get(nbr);

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
        return getBoNotes().add(note);
    }

    public boolean deleteNote(Note note) {
        return getBoNotes().remove(note);
    }

	public PersistableBusinessObjectExtension getExtension() {
		if ( extension == null ) {
			try {
				Class extensionClass = getPersistenceStructureService().getBusinessObjectAttributeClass( getClass(), "extension" );
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

	public FormFile getAttachmentFile() {
        return this.attachmentFile;
    }

    public void setAttachmentFile(FormFile attachmentFile) {
        this.attachmentFile = attachmentFile;
    }
    
    public void populateAttachmentForBO() {
        if((this.getAttachmentFile() != null) && (this instanceof PersistableAttachment)) {
            PersistableAttachment boAttachment = (PersistableAttachment) this;
            if (this.getAttachmentFile().getFileSize() > 0) {
                try {
                    boAttachment.setAttachmentContent(this.getAttachmentFile().getFileData());
                }catch (FileNotFoundException e) {
                    LOG.error("Error while populating the Document Attachment", e);
                    throw new RuntimeException("Could not populate DocumentAttachment object", e);
                }catch (IOException e) {
                    LOG.error("Error while populating the Document Attachment", e);
                    throw new RuntimeException("Could not populate DocumentAttachment object", e);
                } 
                boAttachment.setFileName(this.getAttachmentFile().getFileName());
                boAttachment.setContentType(this.getAttachmentFile().getContentType());
            }
        }

        Map properties = null; 
        try {
            properties = PropertyUtils.describe(this);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
      
        Iterator propIter = properties.entrySet().iterator();
    
        while (propIter.hasNext()) {
            Map.Entry entry = (Map.Entry) propIter.next();
            Object value = entry.getValue();
            if(value instanceof List) {
                List valueList = (List) value;
                for (Object element : valueList) {
                    if(element instanceof PersistableBusinessObjectBase && element instanceof PersistableAttachment) {
                        ((PersistableBusinessObjectBase) element).populateAttachmentForBO();
                    }
                }
            }
        }
    }


    public boolean isAutoIncrementSet() {
		return autoIncrementSet;
	}

	public void setAutoIncrementSet(boolean autoIncrementSet) {
		this.autoIncrementSet = autoIncrementSet;
	}

	/**
	 * @return the attachmentService
	 */
	protected static AttachmentService getAttachmentService() {
		if ( attachmentService == null ) {
			attachmentService = KNSServiceLocator.getAttachmentService();
		}
		return attachmentService;
	}

	/**
	 * @return the persistenceService
	 */
	protected static PersistenceService getPersistenceService() {
		if ( persistenceService == null ) {
			persistenceService = KNSServiceLocator.getPersistenceService();
		}
		return persistenceService;
	}

	protected static PersistenceStructureService getPersistenceStructureService() {
		if ( persistenceStructureService == null ) {
			persistenceStructureService = KNSServiceLocator.getPersistenceStructureService();
		}
		return persistenceStructureService;
	}
	
	/**
	 * @return the noteService
	 */
	protected static NoteService getNoteService() {
		if ( noteService == null ) {
			noteService = KNSServiceLocator.getNoteService();
		}
		return noteService;
	}

}
