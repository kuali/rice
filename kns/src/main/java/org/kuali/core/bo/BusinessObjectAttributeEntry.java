/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.util.LinkedHashMap;

/**
 * Business Object Attribute Entry Business Object
 * 
 * 
 */
public class BusinessObjectAttributeEntry extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = 8136616401437024033L;
    private String dictionaryBusinessObjectName;
    private String attributeName;
    private String attributeLabel;
    private String attributeShortLabel;
    private String attributeMaxLength;
    private String attributeValidatingExpression;
    private String attributeControlType;
    private String attributeSummary;
    private String attributeDescription;
    private String attributeFormatterClassName;


    /**
     * @return Returns the attributeControlType.
     */
    public String getAttributeControlType() {
        return attributeControlType;
    }

    /**
     * @param attributeControlType The attributeControlType to set.
     */
    public void setAttributeControlType(String attributeControlType) {
        this.attributeControlType = attributeControlType;
    }

    /**
     * @return Returns the attributeDescription.
     */
    public String getAttributeDescription() {
        return attributeDescription;
    }

    /**
     * @param attributeDescription The attributeDescription to set.
     */
    public void setAttributeDescription(String attributeDescription) {
        this.attributeDescription = attributeDescription;
    }

    /**
     * @return Returns the attributeFormatterClassName.
     */
    public String getAttributeFormatterClassName() {
        return attributeFormatterClassName;
    }

    /**
     * @param attributeFormatterClassName The attributeFormatterClassName to set.
     */
    public void setAttributeFormatterClassName(String attributeFormatterClassName) {
        this.attributeFormatterClassName = attributeFormatterClassName;
    }

    /**
     * @return Returns the attributeLabel.
     */
    public String getAttributeLabel() {
        return attributeLabel;
    }

    /**
     * @param attributeLabel The attributeLabel to set.
     */
    public void setAttributeLabel(String attributeLabel) {
        this.attributeLabel = attributeLabel;
    }

    /**
     * @return Returns the attributeMaxLength.
     */
    public String getAttributeMaxLength() {
        return attributeMaxLength;
    }

    /**
     * @param attributeMaxLength The attributeMaxLength to set.
     */
    public void setAttributeMaxLength(String attributeMaxLength) {
        this.attributeMaxLength = attributeMaxLength;
    }

    /**
     * @return Returns the attributeName.
     */
    public String getAttributeName() {
        return attributeName;
    }

    /**
     * @param attributeName The attributeName to set.
     */
    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    /**
     * @return Returns the attributeShortLabel.
     */
    public String getAttributeShortLabel() {
        return attributeShortLabel;
    }

    /**
     * @param attributeShortLabel The attributeShortLabel to set.
     */
    public void setAttributeShortLabel(String attributeShortLabel) {
        this.attributeShortLabel = attributeShortLabel;
    }

    /**
     * @return Returns the attributeSummary.
     */
    public String getAttributeSummary() {
        return attributeSummary;
    }

    /**
     * @param attributeSummary The attributeSummary to set.
     */
    public void setAttributeSummary(String attributeSummary) {
        this.attributeSummary = attributeSummary;
    }

    /**
     * @return Returns the attributeValidatingExpression.
     */
    public String getAttributeValidatingExpression() {
        return attributeValidatingExpression;
    }

    /**
     * @param attributeValidatingExpression The attributeValidatingExpression to set.
     */
    public void setAttributeValidatingExpression(String attributeValidatingExpression) {
        this.attributeValidatingExpression = attributeValidatingExpression;
    }

    /**
     * @return Returns the businessObjectClass.
     */
    public String getDictionaryBusinessObjectName() {
        return dictionaryBusinessObjectName;
    }

    /**
     * @param businessObjectClass The businessObjectClass to set.
     */
    public void setDictionaryBusinessObjectName(String businessObjectClass) {
        this.dictionaryBusinessObjectName = businessObjectClass;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("dictionaryBusinessObjectName", getDictionaryBusinessObjectName());
        m.put("attributeName", getAttributeName());

        return m;
    }
}