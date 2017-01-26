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
package org.kuali.rice.krad.datadictionary;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;

/**
 * A single BusinessObject entry in the DataDictionary, which contains information relating to the display, validation,
 * and general maintenance of a BusinessObject and its attributes.
 *
 * Note: the setters do copious amounts of validation, to facilitate generating errors during the parsing process
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "businessObjectEntry")
public class BusinessObjectEntry extends DataObjectEntry {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BusinessObjectEntry.class);

    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        super.setDataObjectClass(businessObjectClass);

        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }

        if (getRelationships() != null) {
            for (RelationshipDefinition rd : getRelationships()) {
                rd.setSourceClass(businessObjectClass);
            }
        }
    }

    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return (Class<? extends BusinessObject>) super.getDataObjectClass();
    }

    /**
     * The baseBusinessObjectClass is an optional parameter for specifying a base class
     * for the dataObjectClass, allowing the data dictionary to index by the base class
     * in addition to the current class.
     */

    public void setBaseBusinessObjectClass(Class<? extends BusinessObject> baseBusinessObjectClass) {
        super.setBaseDataObjectClass(baseBusinessObjectClass);
    }

    @BeanTagAttribute(name = "baseBusinessObjectClass")
    public Class<? extends BusinessObject> getBaseBusinessObjectClass() {
        return (Class<? extends BusinessObject>) super.getBaseDataObjectClass();
    }

    /**
     * Directly validate simple fields, call completeValidation on Definition fields.
     */
    @Override
    public void completeValidation() {
        completeValidation(new ValidationTrace());
    }

    @Override
    public void completeValidation(ValidationTrace tracer) {
        super.completeValidation(tracer);
        try {
            if (inactivationBlockingDefinitions != null && !inactivationBlockingDefinitions.isEmpty()) {
                for (InactivationBlockingDefinition inactivationBlockingDefinition : inactivationBlockingDefinitions) {
                    inactivationBlockingDefinition.completeValidation(getDataObjectClass(), null, tracer.getCopy());
                }
            }
        } catch (Exception ex) {
            String currentValues[] =
                {"BO Class = " + getBusinessObjectClass(), "Exception = " + ex.getMessage()};
            tracer.createError("Unable to validate BO Entry", currentValues);
            LOG.error("Exception while validating BusinessObjectEntry: " + getBusinessObjectClass(), ex );
        }
    }

    @Override
    public void dataDictionaryPostProcessing() {
        super.dataDictionaryPostProcessing();
        if (inactivationBlockingDefinitions != null) {
            for (InactivationBlockingDefinition ibd : inactivationBlockingDefinitions) {
                ibd.setBusinessObjectClass(getBusinessObjectClass());
                if (StringUtils.isNotBlank(ibd.getBlockedReferencePropertyName())
                        && ibd.getBlockedBusinessObjectClass() == null) {
                    // if the user didn't specify a class name for the blocked reference, determine it here
                    ibd.setBlockedBusinessObjectClass(DataDictionary.getAttributeClass(getDataObjectClass(),
                            ibd.getBlockedReferencePropertyName()));
                }
                ibd.setBlockingReferenceBusinessObjectClass(getBusinessObjectClass());
            }
        }
    }
}
