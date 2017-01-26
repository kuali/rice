/**
 * Copyright 2005-2017 The Kuali Foundation
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

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerAware;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.LegacyAppFrameworkAdapterService;
import org.kuali.rice.krad.util.LegacyDataFramework;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @deprecated use new KRAD Data framework {@link org.kuali.rice.krad.data.DataObjectService}.
 *             In this framework, data objects are not required to have a superclass and can
 *             be POJO's but they can optionally use {@link org.kuali.rice.krad.bo.DataObjectBase}
 *             to emulate previous functionality.
 */
@Deprecated
@MappedSuperclass
public abstract class PersistableBusinessObjectBase extends BusinessObjectBase implements PersistableBusinessObject, PersistenceBrokerAware, Versioned, GloballyUnique {

    /**
     * EclipseLink static weaving does not weave MappedSuperclass unless an Entity or Embedded is
     * weaved which uses it, hence this class.
     */
    @Embeddable
    private static final class WeaveMe extends PersistableBusinessObjectBase {}

    private static final long serialVersionUID = 1451642350593233282L;
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PersistableBusinessObjectBase.class);

	@Version
    @Column(name = "VER_NBR", length = 8)
    protected Long versionNumber;

    @Column(name = "OBJ_ID", length = 36, unique = true)
    protected String objectId;

    @Transient protected boolean newCollectionRecord;
    @Transient protected PersistableBusinessObjectExtension extension;

    private static transient LegacyAppFrameworkAdapterService legacyDataAdapter;

    @Override
    public Long getVersionNumber() {
        return versionNumber;
    }

    @Override
    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    @Override
    public String getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    /**
     * Gets the newCollectionRecord attribute.
     *
     * @return Returns the newCollectionRecord.
     */
    @Override
    public boolean isNewCollectionRecord() {
        return newCollectionRecord;
    }

    /**
     * Sets the newCollectionRecord attribute value.
     *
     * @param isNewCollectionRecord The newCollectionRecord to set.
     */
    @Override
    public void setNewCollectionRecord(boolean isNewCollectionRecord) {
        this.newCollectionRecord = isNewCollectionRecord;
    }

    /**
     * Implementation of the OJB afterDelete hook which delegates to {@link #postRemove()}.  This method is final
     * because it is recommended that sub-classes override and implement postRemove if they need to take
     * advantage of this persistence hook.
     *
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterDelete(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    @LegacyDataFramework
    public final void afterDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	postRemove();
    }

    /**
     * Default implementation of the JPA {@link PostRemove} hook.  This implementation currently does nothing,
     * however sub-classes can override and implement this method if needed.
     *
     * <p>This method is currently invoked by the corresponding OJB {@link #afterDelete(PersistenceBroker)} hook.
     */
    @PostRemove
    protected void postRemove() {
    	// do nothing
    }

    /**
     * Implementation of the OJB afterInsert hook which delegates to {@link #postPersist()}.  This method is final
     * because it is recommended that sub-classes override and implement postPersist if they need to take
     * advantage of this persistence hook.
     *
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    @LegacyDataFramework
    public final void afterInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	postPersist();
    }

    /**
     * Default implementation of the JPA {@link PostPersist} hook.  This implementation currently does nothing,
     * however sub-classes can override and implement this method if needed.
     *
     * <p>This method is currently invoked by the corresponding OJB {@link #afterInsert(PersistenceBroker)} hook.
     */
    @PostPersist
    protected void postPersist() {
    	// do nothing
    }

    /**
     * Implementation of the OJB afterLookup hook which delegates to {@link #postLoad()}.  This method is final
     * because it is recommended that sub-classes override and implement postLoad if they need to take
     * advantage of this persistence hook.
     *
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterLookup(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    public final void afterLookup(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	postLoad();
    }

    /**
     * Default implementation of the JPA {@link PostLoad} hook.  This implementation currently does nothing,
     * however sub-classes can override and implement this method if needed.
     *
     * <p>This method is currently invoked by the corresponding OJB {@link #afterLookup(PersistenceBroker)} hook.
     */
    @PostLoad
    protected void postLoad() {
    	// do nothing
    }

    /**
     * Implementation of the OJB afterUpdate hook which delegates to {@link #postUpdate()}.  This method is final
     * because it is recommended that sub-classes override and implement postUpdate if they need to take
     * advantage of this persistence hook.
     *
     * @see org.apache.ojb.broker.PersistenceBrokerAware#afterUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    @LegacyDataFramework
    public final void afterUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	postUpdate();
    }

    /**
     * Default implementation of the JPA {@link PostUpdate} hook.  This implementation currently does nothing,
     * however sub-classes can override and implement this method if needed.
     *
     * <p>This method is currently invoked by the corresponding OJB {@link #afterUpdate(PersistenceBroker)} hook.
     */
    @PostUpdate
    protected void postUpdate() {
    	// do nothing
    }

    /**
     * Implementation of the OJB beforeDelete hook which delegates to {@link #preRemove()}.  This method is final
     * because it is recommended that sub-classes override and implement preRemove if they need to take
     * advantage of this persistence hook.
     *
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeDelete(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    @LegacyDataFramework
    public final void beforeDelete(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	preRemove();
    }

    /**
     * Default implementation of the JPA {@link PreRemove} hook.  This implementation currently does nothing,
     * however sub-classes can implement this method if needed.
     *
     * <p>This method is currently invoked by the corresponding OJB {@link #beforeDelete(PersistenceBroker)} hook.
     */
    @PreRemove
    protected void preRemove() {
    	// do nothing
    }

    /**
     * Implementation of the OJB beforeInsert hook which delegates to {@link #prePersist()}.  This method is final
     * because it is recommended that sub-classes override and implement prePersist if they need to take
     * advantage of this persistence hook.
     *
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeInsert(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    @LegacyDataFramework
    public final void beforeInsert(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
        //setObjectId(UUID.randomUUID().toString());
        setObjectId(null);
        prePersist();
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
     * Implementation of the OJB beforeUpdate hook which delegates to {@link #preUpdate()}.  This method is final
     * because it is recommended that sub-classes override and implement preUpdate if they need to take
     * advantage of this persistence hook.
     *
     * @see org.apache.ojb.broker.PersistenceBrokerAware#beforeUpdate(org.apache.ojb.broker.PersistenceBroker)
     */
    @Override
    @LegacyDataFramework
    public final void beforeUpdate(PersistenceBroker persistenceBroker) throws PersistenceBrokerException {
    	preUpdate();
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
    private void generateAndSetObjectIdIfNeeded() {
    	if (StringUtils.isEmpty(getObjectId())) {
            setObjectId(UUID.randomUUID().toString());
        }
    }

    /**
     * getService Refreshes the reference objects from the primitive values.
     *
     * @see org.kuali.rice.krad.bo.BusinessObject#refresh()
     */
    @Override
    public void refresh() {
        getLegacyDataAdapter().refresh(this);
    }

    @Override
    public void refreshNonUpdateableReferences() {
        getLegacyDataAdapter().refreshNonUpdateableReferences(this);
    }

	@Override
    public void refreshReferenceObject(String referenceObjectName) {
        getLegacyDataAdapter().refreshReferenceObject(this, referenceObjectName);
	}

    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        return new ArrayList<Collection<PersistableBusinessObject>>();
    }

    @Override
    public void linkEditableUserFields() {
    	// do nothing
    }

	@Override
    public PersistableBusinessObjectExtension getExtension() {
		if ( extension == null
                && getLegacyDataAdapter().isPersistable(this.getClass())) {
			try {
                extension = getLegacyDataAdapter().getExtension(this.getClass());
			} catch ( Exception ex ) {
				LOG.error( "unable to create extension object", ex );
			}
		}
		return extension;
	}

	@Override
    public void setExtension(PersistableBusinessObjectExtension extension) {
		this.extension = extension;
	}

    /**
     * Returns the legacy data adapter for handling legacy KNS and KRAD data and metadata.
     *
     * @return the legacy data adapter
     * @deprecated application code should never use this! Always use KRAD code directly.
     */
    @Deprecated
    protected static LegacyAppFrameworkAdapterService getLegacyDataAdapter() {
        return KNSServiceLocator.getLegacyAppFrameworkAdapterService();
    }

}
