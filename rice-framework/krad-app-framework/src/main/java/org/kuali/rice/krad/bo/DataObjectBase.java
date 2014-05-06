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
package org.kuali.rice.krad.bo;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.kuali.rice.core.api.mo.common.GloballyUnique;
import org.kuali.rice.core.api.mo.common.Versioned;

/**
 * Declares an optional superclass for classes which can have their
 * state persisted.  A data object which is persistable defines some additional methods
 * which allow for various operations to be executed that relate to the persistent nature of
 * the data object.  A persistable data object also has some additional data
 * attributes which include the version number, the object id, and the extension.
 *
 * <p>The version number indicates the version of the data object when it was retrieved
 * from persistent storage.  This allows for services to check this version number
 * during persistence operations to prevent silent overwrites of data object state.
 * These kinds of scenarios might arise as a result of concurrent modification to the data
 * object in the persistent store (i.e. two web application users updating the same record
 * in a database).  The kind of check which would be performed using the version number is commonly
 * referred to as "optimistic locking".
 *
 * <p>The object id represents a globally unique identifier for the business object.  In practice,
 * this can be used by other portions of the system to link to data objects which
 * might be stored in different locations or even different persistent data stores.  In general, it
 * is not the responsibility of the client who implements a persistable data object to handle
 * generating this value.  The framework will handle this automatically at the point in time when
 * the data object is persisted.  If the client does need to do this themselves, however, care
 * should be taken that an appropriate globally unique value generator algorithm is used
 * (such as the one provided by {@link UUID}).
 *
 * <p>The extension object is primarily provided for the purposes of allowing implementer
 * customization of the data object without requiring the original data object to be
 * modified.  The additional extension object which is linked with the
 * parent data object via use of {@link org.kuali.rice.krad.data.provider.annotation.ExtensionFor}
 * annotation on the actual extension class} can contain additional data attributes and methods.
 * The framework will automatically request that this extension object be persisted when the parent
 * data object is persisted.  This is generally the most useful in cases where an application is defining
 * data objects that will be used in redistributable software packages (such as the
 * actual Kuali Foundation projects themselves).  If using the framework for the purposes
 * of implementing an internal application, the use of a data object extensions
 * is likely unnecessary.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@MappedSuperclass
public abstract class DataObjectBase implements Versioned, GloballyUnique, Serializable {

    /**
     * EclipseLink static weaving does not weave MappedSuperclass unless an Entity or Embedded is
     * weaved which uses it, hence this class.
     */
    @Embeddable
    private static final class WeaveMe extends DataObjectBase {}

	@Version
    @Column(name="VER_NBR", length=8)
    protected Long versionNumber;

    @Column(name="OBJ_ID", length=36, unique=true, nullable = false)
    protected String objectId;

    @Transient
    Object extensionObject;

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

    public Object getExtensionObject() {
        return extensionObject;
    }

    public void setExtensionObject(Object extensionObject) {
        this.extensionObject = extensionObject;
    }

    @Override
    public String toString() {
        class DataObjectToStringBuilder extends ReflectionToStringBuilder {
            private DataObjectToStringBuilder(Object object) {
                super(object);
            }

            @Override
            public boolean accept(Field field) {
                if (field.getType().isPrimitive()
                        || field.getType().isEnum()
                        || java.lang.String.class.isAssignableFrom(field.getType())
                        || java.lang.Number.class.isAssignableFrom(field.getType())
                        || java.util.Collection.class.isAssignableFrom(field.getType())) {
                    return super.accept(field);
                }
                return false;
            }
        };
        return new DataObjectToStringBuilder(this).toString();
    }

}
