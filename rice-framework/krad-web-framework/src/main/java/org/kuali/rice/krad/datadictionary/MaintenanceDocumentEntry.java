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
package org.kuali.rice.krad.datadictionary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.maintenance.Maintainable;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizer;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentBase;
import org.kuali.rice.krad.maintenance.MaintenanceDocumentPresentationControllerBase;

/**
 * Data dictionary entry class for <code>MaintenanceDocument</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "maintenanceDocumentEntry", parent = "uifMaintenanceDocumentEntry")
public class MaintenanceDocumentEntry extends DocumentEntry {
    private static final long serialVersionUID = 4990040987835057251L;

    protected Class<?> dataObjectClass;
    protected Class<? extends Maintainable> maintainableClass;

    protected List<String> lockingKeys = new ArrayList<String>();
    protected List<String> clearValueOnCopyPropertyNames = new ArrayList<String>();

    protected boolean allowsNewOrCopy = true;
    protected boolean preserveLockingKeysOnCopy = false;
    protected boolean allowsRecordDeletion = false;

    public MaintenanceDocumentEntry() {
        super();

        setDocumentClass(getStandardDocumentBaseClass());
        documentAuthorizerClass = MaintenanceDocumentAuthorizerBase.class;
        documentPresentationControllerClass = MaintenanceDocumentPresentationControllerBase.class;
    }

    public Class<? extends Document> getStandardDocumentBaseClass() {
        return MaintenanceDocumentBase.class;
    }

    /*
            This attribute is used in many contexts, for example, in maintenance docs, it's used to specify the classname
            of the BO being maintained.
     */
    public void setDataObjectClass(Class<?> dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    @BeanTagAttribute(name = "dataObjectClass")
    public Class<?> getDataObjectClass() {
        return dataObjectClass;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.DocumentEntry#getEntryClass()
     */
    @SuppressWarnings("unchecked")
    @Override
    public Class getEntryClass() {
        return dataObjectClass;
    }

    /*
            The maintainableClass element specifies the name of the
            java class which is responsible for implementing the
            maintenance logic.
            The normal one is KualiMaintainableImpl.java.
     */
    public void setMaintainableClass(Class<? extends Maintainable> maintainableClass) {
        this.maintainableClass = maintainableClass;
    }

    @BeanTagAttribute(name = "maintainableClass")
    public Class<? extends Maintainable> getMaintainableClass() {
        return maintainableClass;
    }

    /**
     * @return List of all lockingKey fieldNames associated with this LookupDefinition, in the order in which they were
     *         added
     */
    public List<String> getLockingKeyFieldNames() {
        return lockingKeys;
    }

    /**
     * Gets the allowsNewOrCopy attribute.
     *
     * @return Returns the allowsNewOrCopy.
     */
    @BeanTagAttribute(name = "allowsNewOrCopy")
    public boolean getAllowsNewOrCopy() {
        return allowsNewOrCopy;
    }

    /**
     * The allowsNewOrCopy element contains a value of true or false.
     * If true, this indicates the maintainable should allow the
     * new and/or copy maintenance actions.
     */
    public void setAllowsNewOrCopy(boolean allowsNewOrCopy) {
        this.allowsNewOrCopy = allowsNewOrCopy;
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     *
     * @see org.kuali.rice.krad.datadictionary.DocumentEntry#completeValidation()
     */
    @Override
    public void completeValidation(ValidationTrace tracer) {
        super.completeValidation(tracer);

        if (dataObjectClass == null) {
            String currentValues[] = {};
            tracer.createError("invalid (null) dataObjectClass", currentValues);
        }

        if (maintainableClass == null) {
            String currentValues[] = {};
            tracer.createError("invalid (null) maintainableClass", currentValues);
        }

        for (String lockingKey : lockingKeys) {
            if ( StringUtils.isBlank(lockingKey) ) {
                String currentValues[] = {"lockingKeys = " + lockingKeys};
                tracer.createError("invalid (blank) lockingKey", currentValues);
            } else if (!DataDictionary.isPropertyOf(dataObjectClass, lockingKey)) {
                String currentValues[] = {"dataObjectClass = " + dataObjectClass, "lockingKey = " + lockingKey};
                tracer.createError("lockingKey not found in data object class", currentValues);
            }
        }

        for (String clearValueOnCopyPropertyName : clearValueOnCopyPropertyNames) {
            if (StringUtils.isBlank(clearValueOnCopyPropertyName)) {
                String currentValues[] = {"clearValueOnCopyPropertyNames = " + clearValueOnCopyPropertyNames};
                tracer.createError("invalid (blank) clearValueOnCopyPropertyNames", currentValues);
            } else if (!DataDictionary.isPropertyOf(dataObjectClass, clearValueOnCopyPropertyName)) {
                String currentValues[] = {"dataObjectClass = " + dataObjectClass,
                        "clearValueOnCopyPropertyName = " + clearValueOnCopyPropertyName};
                tracer.createError("clearValueOnCopyPropertyName not found in data object class", currentValues);
            }
        }

        if (documentAuthorizerClass != null
                && !MaintenanceDocumentAuthorizer.class.isAssignableFrom(documentAuthorizerClass)) {
            String currentValues[] = {"documentAuthorizerClass = " + documentAuthorizerClass.getName()};
            tracer.createError("Maintenance Documents must use an implementation of MaintenanceDocumentAuthorizer", currentValues);
        }
    }

    @Override
    protected void validateDefaultExistenceChecks( ValidationTrace tracer ) {
        for ( ReferenceDefinition refDef : defaultExistenceChecks ) {
            refDef.completeValidation(dataObjectClass, null, tracer.getCopy());
        }
    }


    @BeanTagAttribute(name = "lockingKeys", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getLockingKeys() {
        return lockingKeys;
    }

    /*
           The lockingKeys element specifies a list of fields
           that comprise a unique key.  This is used for record locking
           during the file maintenance process.
    */
    public void setLockingKeys(List<String> lockingKeys) {
        this.lockingKeys = lockingKeys;
    }

    /**
     * @return the preserveLockingKeysOnCopy
     */
    @BeanTagAttribute(name = "preserveLockingKeysOnCopy")
    public boolean getPreserveLockingKeysOnCopy() {
        return this.preserveLockingKeysOnCopy;
    }

    /**
     * @param preserveLockingKeysOnCopy the preserveLockingKeysOnCopy to set
     */
    public void setPreserveLockingKeysOnCopy(boolean preserveLockingKeysOnCopy) {
        this.preserveLockingKeysOnCopy = preserveLockingKeysOnCopy;
    }

    /**
     * @return the clearValueOnCopyPropertyNames
     */
    @BeanTagAttribute(name = "clearValueOnCopyPropertyNames", type = BeanTagAttribute.AttributeType.LISTVALUE)
    public List<String> getClearValueOnCopyPropertyNames() {
        return clearValueOnCopyPropertyNames;
    }

    /**
     * @param clearValueOnCopyPropertyNames the clearValueOnCopyPropertyNames to set
     */
    public void setClearValueOnCopyPropertyNames(List<String> clearValueOnCopyPropertyNames) {
        this.clearValueOnCopyPropertyNames = clearValueOnCopyPropertyNames;
    }

    /**
     * @return the allowRecordDeletion
     */
    @BeanTagAttribute(name = "allowsRecordDeletion")
    public boolean getAllowsRecordDeletion() {
        return this.allowsRecordDeletion;
    }

    /**
     * @param allowsRecordDeletion the allowRecordDeletion to set
     */
    public void setAllowsRecordDeletion(boolean allowsRecordDeletion) {
        this.allowsRecordDeletion = allowsRecordDeletion;
    }

}
