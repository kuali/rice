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
package org.kuali.rice.krad.bo;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@MappedSuperclass
public abstract class VersionedAndGloballyUniqueBase implements Versioned, GloballyUnique, Serializable {

	@Version
    @Column(name="VER_NBR",length=8)
    protected Long versionNumber;
    @Column(name="OBJ_ID",length=36,unique=true)
    protected String objectId;

    @Override
    public Long getVersionNumber() {
        return versionNumber;
    }

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
    @Override
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
     * Default implementation of the JPA {@link PrePersist} hook which generates the unique objectId for this
     * persistable business object if it does not already have one.  Any sub-class which overrides this method
     * should take care to invoke super.prePersist to ensure that the objectId for this persistable
     * business object is generated properly.
     *
     * <p>This method is currently invoked by the corresponding OJB {@link #beforeInsert(PersistenceBroker)} hook.
     */
    @PrePersist
    protected void prePersist() {
    	generateAndSetObjectIdIfNeeded();
    }

    /**
     * Default implementation of the JPA {@link PreUpdate} hook which generates the unique objectId for this
     * persistable business object if it does not already have one.  Any sub-class which overrides this method
     * should take care to invoke super.preUpdate to ensure that the objectId for this persistable
     * business object is generated properly.
     *
     * <p>This method is currently invoked by the corresponding OJB {@link #beforeUpdate(PersistenceBroker)} hook.
     */
    @PreUpdate
    protected void preUpdate() {
    	generateAndSetObjectIdIfNeeded();
    }

    /**
     * If this PersistableBusinessObject does not already have a unique objectId, this method will generate
     * one and set it's value on this object.
     */
    protected void generateAndSetObjectIdIfNeeded() {
    	if (StringUtils.isEmpty(getObjectId())) {
            setObjectId(UUID.randomUUID().toString());
        }
    }

}
