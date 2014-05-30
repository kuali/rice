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

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;
import org.kuali.rice.krad.datadictionary.validator.ValidationTrace;

/**
 * This is a description of what this class does - wliang don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "inactivationBlockingDefinition")
public class InactivationBlockingDefinition extends DataDictionaryDefinitionBase implements InactivationBlockingMetadata {
    private static final long serialVersionUID = -8765429636173190984L;

    protected Class<?> blockingReferenceBusinessObjectClass;
    protected String blockedReferencePropertyName;
    protected Class<?> blockedBusinessObjectClass;
    protected String inactivationBlockingDetectionServiceBeanName;
    protected String relationshipLabel;
    protected Class<?> businessObjectClass;

    @Override
    public void completeValidation(Class<?> rootDataObjectClass, Class<?> otherDataObjectClass, ValidationTrace tracer) {
        if (StringUtils.isBlank(inactivationBlockingDetectionServiceBeanName)) {
            if (StringUtils.isBlank(blockedReferencePropertyName)) {
                // the default inactivation blocking detection service (used when inactivationBlockingDetectionServiceBeanName is blank) requires that the property name be set
                String currentValues[] = {"rootDataObjectClass = " + rootDataObjectClass};
                tracer.createError("inactivationBlockingDetectionServiceBeanName and  blockedReferencePropertyName can't both be blank in InactivationBlockingDefinition", currentValues);
            }
        }
        if (getBlockedDataObjectClass() == null) {
            String currentValues[] = {"rootDataObjectClass = " + rootDataObjectClass};
            tracer.createError("Unable to determine blockedReferenceBusinessObjectClass in InactivationBlockingDefinition", currentValues);
        }
    }

    @Override
    @Deprecated
    @BeanTagAttribute(name = "blockedReferencePropertyName")
    public String getBlockedReferencePropertyName() {
        return this.blockedReferencePropertyName;
    }

    /**
     * @deprecated use {@link #setBlockedAttributeName(String)} instead
     */
    @Deprecated
    public void setBlockedReferencePropertyName(String blockedReferencePropertyName) {
        this.blockedReferencePropertyName = blockedReferencePropertyName;
    }

    @Override
    @BeanTagAttribute(name = "blockedAttributeName")
    public String getBlockedAttributeName() {
        return this.blockedReferencePropertyName;
    }

    public void setBlockedAttributeName(String blockedAttributeName) {
        this.blockedReferencePropertyName = blockedAttributeName;
    }

    @Override
    @Deprecated
    @SuppressWarnings("unchecked")
    @BeanTagAttribute(name = "blockedBusinessObjectClass")
    public Class<? extends BusinessObject> getBlockedBusinessObjectClass() {
        return (Class<? extends BusinessObject>)blockedBusinessObjectClass;
    }

    /**
     * @deprecated use {@link #setBlockedDataObjectClass(Class)} instead
     */
    @Deprecated
    public void setBlockedBusinessObjectClass(Class<? extends BusinessObject> blockedBusinessObjectClass) {
        this.blockedBusinessObjectClass = blockedBusinessObjectClass;
    }

    @Override
    @BeanTagAttribute(name = "blockedDataObjectClass")
    public Class<?> getBlockedDataObjectClass() {
        return blockedBusinessObjectClass;
    }

    public void setBlockedDataObjectClass(Class<?> blockedDataObjectClass) {
        this.blockedBusinessObjectClass = blockedDataObjectClass;
    }

    @Override
    @BeanTagAttribute(name = "inactivationBlockingDetectionServiceBeanName")
    public String getInactivationBlockingDetectionServiceBeanName() {
        return this.inactivationBlockingDetectionServiceBeanName;
    }

    public void setInactivationBlockingDetectionServiceBeanName(String inactivationBlockingDetectionServiceImpl) {
        this.inactivationBlockingDetectionServiceBeanName = inactivationBlockingDetectionServiceImpl;
    }

    @Override
    @Deprecated
    @SuppressWarnings("unchecked")
    @BeanTagAttribute(name = "blockingReferenceBusinessObjectClass")
    public Class<? extends BusinessObject> getBlockingReferenceBusinessObjectClass() {
        return (Class<? extends BusinessObject>)this.blockingReferenceBusinessObjectClass;
    }

    /**
     * @deprecated use {@link #setBlockingReferenceDataObjectClass(Class)}
     */
    @Deprecated
    public void setBlockingReferenceBusinessObjectClass(
            Class<? extends BusinessObject> blockingReferenceBusinessObjectClass) {
        this.blockingReferenceBusinessObjectClass = blockingReferenceBusinessObjectClass;
    }

    @Override
    @BeanTagAttribute(name = "blockingDataObjectClass")
    public Class<?> getBlockingDataObjectClass() {
        return blockingReferenceBusinessObjectClass;
    }

    public void setBlockingReferenceDataObjectClass(Class<?> blockingDataObjectClass) {
        this.blockingReferenceBusinessObjectClass = blockingDataObjectClass;
    }

    @Override
    @BeanTagAttribute(name = "relationshipLabel")
    public String getRelationshipLabel() {
        return this.relationshipLabel;
    }

    public void setRelationshipLabel(String relationshipLabel) {
        this.relationshipLabel = relationshipLabel;
    }

    /**
     * @deprecated the businessObjectClass does not appear to be used anywhere significantly internally or externally,
     *             no replacement
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    @BeanTagAttribute(name = "businessObjectClass")
    public Class<? extends BusinessObject> getBusinessObjectClass() {
        return (Class<? extends BusinessObject>)this.businessObjectClass;
    }

    /**
     * @deprecated the businessObjectClass does not appear to be used anywhere significantly internally or externally,
     *             no replacement
     */
    @Deprecated
    public void setBusinessObjectClass(Class<? extends BusinessObject> businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }

    @Override
    public String toString() {
        return "InactivationBlockingDefinition: blockedClass="
                + blockedBusinessObjectClass.getName()
                + " /blockingReferenceProperty="
                + blockedReferencePropertyName
                + " /blockingClass="
                + blockingReferenceBusinessObjectClass.getName();
    }
}
