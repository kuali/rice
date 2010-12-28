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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.service.PersistenceStructureService;

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

    private static transient PersistenceService persistenceService;
    private static transient PersistenceStructureService persistenceStructureService;
    
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
    }

    public void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    }

    public void afterUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    }

    public void beforeDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    }

    public void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        setObjectId(UUID.randomUUID().toString());
    }

    public void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	beforeUpdate();
    }
    
    @PrePersist
    public void beforeInsert() {
    	setObjectId(UUID.randomUUID().toString());
    }

    @PreUpdate
    public void beforeUpdate() {
        if (StringUtils.isEmpty(getObjectId())) {
            setObjectId(UUID.randomUUID().toString());
        }
    }
    
    @PostPersist
    public void afterInsert() {
    }
    
    @PostUpdate
    public void afterUpdate() {
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

}
