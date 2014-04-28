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
package org.kuali.rice.krad.service.impl;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.AttributeSecurity;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.CollectionDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.krad.datadictionary.DataObjectEntry;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.krad.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.datadictionary.exception.UnknownBusinessClassAttributeException;
import org.kuali.rice.krad.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.uif.UifConstants.ViewType;
import org.kuali.rice.krad.uif.view.View;

/**
 * Service implementation for a DataDictionary. It is a thin wrapper around creating, initializing, and
 * returning a DataDictionary. This is the default, Kuali delivered implementation
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DataDictionaryServiceImpl implements DataDictionaryService {

    private DataDictionary dataDictionary;
    private Map<Class<?>, String> propertyEditorMap;

    private ConfigurationService kualiConfigurationService;
    private KualiModuleService kualiModuleService;
    private volatile DocumentTypeService documentTypeService;

    public DataDictionaryServiceImpl() {
        this.dataDictionary = new DataDictionary();
    }

    public DataDictionaryServiceImpl(DataDictionary dataDictionary) {
        this.dataDictionary = dataDictionary;
    }

    @Override
	public void setAdditionalDictionaryFiles(Map<String, List<String>> additionalDictionaryFiles) throws IOException {
        for (Map.Entry<String, List<String>> entry : additionalDictionaryFiles.entrySet()) {
            addDataDictionaryLocations(entry.getKey(), entry.getValue());
        }
    }

    @Override
	public void addDataDictionaryLocations(String namespaceCode, List<String> locations) throws IOException {
        for (String location : locations) {
            dataDictionary.addConfigFileLocation(namespaceCode, location);
        }
    }

    @Override
	public DataDictionary getDataDictionary() {
        return dataDictionary;
    }

    @Override
	public ControlDefinition getAttributeControlDefinition(String entryName, String attributeName) {
        ControlDefinition controlDefinition = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            controlDefinition = attributeDefinition.getControl();
        }

        return controlDefinition;
    }

    @Override
	public Integer getAttributeSize(String entryName, String attributeName) {
        Integer size = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            ControlDefinition controlDefinition = attributeDefinition.getControl();
            if (controlDefinition.isText() || controlDefinition.isCurrency()) {
                size = controlDefinition.getSize();
            }
        }

        return size;
    }

    @Override
	public Integer getAttributeMinLength(String entryName, String attributeName) {
        Integer minLength = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            minLength = attributeDefinition.getMinLength();
        }

        return minLength;
    }

    @Override
	public Integer getAttributeMaxLength(String entryName, String attributeName) {
        Integer maxLength = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            maxLength = attributeDefinition.getMaxLength();
        }

        return maxLength;
    }

    @Override
	public String getAttributeExclusiveMin(String entryName, String attributeName) {
        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        return attributeDefinition == null ? null : attributeDefinition.getExclusiveMin();
    }

    @Override
	public String getAttributeInclusiveMax(String entryName, String attributeName) {
        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        return attributeDefinition == null ? null : attributeDefinition.getInclusiveMax();
    }

    @Override
	public Pattern getAttributeValidatingExpression(String entryName, String attributeName) {
        Pattern regex = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null && (attributeDefinition.getValidationPattern() != null)) {
            regex = attributeDefinition.getValidationPattern().getRegexPattern();
        } else {
            // workaround for existing calls which don't bother checking for null return values
            regex = Pattern.compile(".*");
        }

        return regex;
    }

    @Override
	public String getAttributeLabel(String entryName, String attributeName) {
        String label = "";

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            // KULRICE-4445 prevent NullPointerException by ensuring a label is set
            label = attributeDefinition.getLabel();
            if (!StringUtils.isEmpty(attributeDefinition.getDisplayLabelAttribute())) {
                attributeDefinition = getAttributeDefinition(entryName, attributeDefinition.getDisplayLabelAttribute());
                if (attributeDefinition != null) {
                    label = attributeDefinition.getLabel();
                }
            }
        }

        return label;
    }

    @Override
	public String getAttributeShortLabel(String entryName, String attributeName) {
        String shortLabel = "";

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (!StringUtils.isEmpty(attributeDefinition.getDisplayLabelAttribute())) {
                attributeDefinition = getAttributeDefinition(entryName, attributeDefinition.getDisplayLabelAttribute());
                if (attributeDefinition != null) {
                    shortLabel = attributeDefinition.getShortLabel();
                }
            } else {
                shortLabel = attributeDefinition.getShortLabel();
            }
        }

        return shortLabel;
    }

    @Override
	public String getAttributeErrorLabel(String entryName, String attributeName) {
        String longAttributeLabel = this.getAttributeLabel(entryName, attributeName);
        String shortAttributeLabel = this.getAttributeShortLabel(entryName, attributeName);
        return longAttributeLabel + " (" + shortAttributeLabel + ")";
    }

    @Override
	public Class<? extends Formatter> getAttributeFormatter(String entryName, String attributeName) {
        Class formatterClass = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (attributeDefinition.hasFormatterClass()) {
                formatterClass = ClassLoaderUtils.getClass(attributeDefinition.getFormatterClass());
            }
        }

        return formatterClass;
    }

    @Override
	public Boolean getAttributeForceUppercase(String entryName,
            String attributeName) throws UnknownBusinessClassAttributeException {
        Boolean forceUppercase = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition == null) {
            throw new UnknownBusinessClassAttributeException(
                    "Could not find a matching data dictionary business class attribute entry for " + entryName + "." +
                            attributeName);
        }
        forceUppercase = attributeDefinition.getForceUppercase();

        return forceUppercase;
    }

    @Override
	public AttributeSecurity getAttributeSecurity(String entryName, String attributeName) {
        AttributeSecurity attributeSecurity = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            attributeSecurity = attributeDefinition.getAttributeSecurity();
        }

        return attributeSecurity;
    }

    @Override
	public String getAttributeSummary(String entryName, String attributeName) {
        String summary = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            summary = attributeDefinition.getSummary();
        }

        return summary;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeDescription(java.lang.String)
     */
    @Override
	public String getAttributeDescription(String entryName, String attributeName) {
        String description = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            description = attributeDefinition.getDescription();
        }

        return description;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#isAttributeRequired(java.lang.Class, java.lang.String)
     */
    @Override
	public Boolean isAttributeRequired(String entryName, String attributeName) {
        Boolean required = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            required = attributeDefinition.isRequired();
        }

        return required;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#isAttributeDefined(java.lang.Class, java.lang.String)
     */
    @Override
	public Boolean isAttributeDefined(String entryName, String attributeName) {
        boolean isDefined = false;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            isDefined = true;
        }

        return isDefined;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeValuesScopeId(java.lang.Class,
     *      java.lang.String)
     */
    @Override
	public Class<? extends KeyValuesFinder> getAttributeValuesFinderClass(String entryName, String attributeName) {
        Class valuesFinderClass = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            String valuesFinderClassName = attributeDefinition.getControl().getValuesFinderClass();
            valuesFinderClass = ClassLoaderUtils.getClass(valuesFinderClassName);
        }

        return valuesFinderClass;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getCollectionLabel(java.lang.Class, java.lang.String)
     */
    @Override
	public String getCollectionLabel(String entryName, String collectionName) {
        String label = "";

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            label = collectionDefinition.getLabel();
        }

        return label;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getCollectionShortLabel(java.lang.Class, java.lang.String)
     */
    @Override
	public String getCollectionShortLabel(String entryName, String collectionName) {
        String shortLabel = "";

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            shortLabel = collectionDefinition.getShortLabel();
        }

        return shortLabel;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getCollectionElementLabel(java.lang.Class,
     *      java.lang.String)
     */
    @Override
	public String getCollectionElementLabel(String entryName, String collectionName, Class dataObjectClass) {
        String elementLabel = "";

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            elementLabel = collectionDefinition.getElementLabel();
            if (StringUtils.isEmpty(elementLabel)) {
                BusinessObjectEntry boe = getDataDictionary().getBusinessObjectEntry(dataObjectClass.getName());
                if (boe != null) {
                    elementLabel = boe.getObjectLabel();
                }
            }
        }

        return elementLabel;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getCollectionSummary(java.lang.Class, java.lang.String)
     */
    @Override
	public String getCollectionSummary(String entryName, String collectionName) {
        String summary = null;

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            summary = collectionDefinition.getSummary();
        }

        return summary;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getCollectionDescription(java.lang.Class,
     *      java.lang.String)
     */
    @Override
	public String getCollectionDescription(String entryName, String collectionName) {
        String description = null;

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            description = collectionDefinition.getDescription();
        }

        return description;
    }

    @Override
	public Class<? extends BusinessObject> getRelationshipSourceClass(String entryName, String relationshipName) {
        Class sourceClass = null;

        RelationshipDefinition rd = getRelationshipDefinition(entryName, relationshipName);
        if (rd != null) {
            sourceClass = rd.getSourceClass();
        }

        return sourceClass;
    }

    @Override
	public Class<? extends BusinessObject> getRelationshipTargetClass(String entryName, String relationshipName) {
        Class targetClass = null;

        RelationshipDefinition rd = getRelationshipDefinition(entryName, relationshipName);
        if (rd != null) {
            targetClass = rd.getTargetClass();
        }

        return targetClass;
    }

    @Override
	public List<String> getRelationshipSourceAttributes(String entryName, String relationshipName) {
        List<String> sourceAttributes = null;

        RelationshipDefinition rd = getRelationshipDefinition(entryName, relationshipName);
        if (rd != null) {
            sourceAttributes = new ArrayList<String>();

            for (PrimitiveAttributeDefinition pad : rd.getPrimitiveAttributes()) {
                sourceAttributes.add(pad.getSourceName());
            }
        }

        return sourceAttributes;
    }

    @Override
	public List<String> getRelationshipTargetAttributes(String entryName, String relationshipName) {
        List<String> targetAttributes = null;

        RelationshipDefinition rd = getRelationshipDefinition(entryName, relationshipName);
        if (rd != null) {
            targetAttributes = new ArrayList<String>();

            for (PrimitiveAttributeDefinition pad : rd.getPrimitiveAttributes()) {
                targetAttributes.add(pad.getTargetName());
            }
        }

        return targetAttributes;
    }

    @Override
	public List<String> getRelationshipEntriesForSourceAttribute(String entryName, String sourceAttributeName) {
        List<String> relationships = new ArrayList<String>();

        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);

        for (RelationshipDefinition def : entry.getRelationships()) {
            for (PrimitiveAttributeDefinition pddef : def.getPrimitiveAttributes()) {
                if (StringUtils.equals(sourceAttributeName, pddef.getSourceName())) {
                    relationships.add(def.getObjectAttributeName());
                    break;
                }
            }
        }
        return relationships;
    }

    @Override
	public List<String> getRelationshipEntriesForTargetAttribute(String entryName, String targetAttributeName) {
        List<String> relationships = new ArrayList<String>();

        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);

        for (RelationshipDefinition def : entry.getRelationships()) {
            for (PrimitiveAttributeDefinition pddef : def.getPrimitiveAttributes()) {
                if (StringUtils.equals(targetAttributeName, pddef.getTargetName())) {
                    relationships.add(def.getObjectAttributeName());
                    break;
                }
            }
        }
        return relationships;
    }

    /**
     * @param entryName - the qualified object name e.g. edu.sampleu.demo.kitchensink.TimeInfo
     * @param attributeName - an attribute name e.g. startTimeAmPm
     * @return AttributeDefinition for the given dataObjectClass and attribute name, or null if there is none
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    @Override
	public AttributeDefinition getAttributeDefinition(String entryName, String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        AttributeDefinition attributeDefinition = null;

        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            attributeDefinition = entry.getAttributeDefinition(attributeName);
        }

        return attributeDefinition;
    }

    /**
     * @param entryName
     * @param collectionName
     * @return CollectionDefinition for the given entryName and collectionName, or null if there is none
     */
    private CollectionDefinition getCollectionDefinition(String entryName, String collectionName) {
        if (StringUtils.isBlank(collectionName)) {
            throw new IllegalArgumentException("invalid (blank) collectionName");
        }
        CollectionDefinition collectionDefinition = null;

        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            collectionDefinition = entry.getCollectionDefinition(collectionName);
        }

        return collectionDefinition;
    }

    /**
     * @param entryName
     * @param relationshipName
     * @return RelationshipDefinition for the given entryName and relationshipName, or null if there is none
     */
    private RelationshipDefinition getRelationshipDefinition(String entryName, String relationshipName) {
        if (StringUtils.isBlank(relationshipName)) {
            throw new IllegalArgumentException("invalid (blank) relationshipName");
        }

        RelationshipDefinition relationshipDefinition = null;

        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            relationshipDefinition = entry.getRelationshipDefinition(relationshipName);
        }

        return relationshipDefinition;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getRelationshipAttributeMap(java.lang.String, java.lang.String)
     */
    @Override
	public Map<String, String> getRelationshipAttributeMap(String entryName, String relationshipName) {
        Map<String, String> attributeMap = new HashMap<String, String>();
        RelationshipDefinition relationshipDefinition = getRelationshipDefinition(entryName, relationshipName);
        for (Iterator iter = relationshipDefinition.getPrimitiveAttributes().iterator(); iter.hasNext(); ) {
            PrimitiveAttributeDefinition attribute = (PrimitiveAttributeDefinition) iter.next();
            attributeMap.put(attribute.getTargetName(), attribute.getSourceName());
        }
        return attributeMap;
    }

    @Override
	public boolean hasRelationship(String entryName, String relationshipName) {
        return getRelationshipDefinition(entryName, relationshipName) != null;
    }

    @Override
	public List<String> getRelationshipNames(String entryName) {
        DataDictionaryEntryBase entry =
                (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);

        List<String> relationshipNames = new ArrayList<String>();
        for (RelationshipDefinition def : entry.getRelationships()) {
            relationshipNames.add(def.getObjectAttributeName());
        }
        return relationshipNames;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeControlDefinition(java.lang.String, java.lang.String)
     */
    @Override
	public ControlDefinition getAttributeControlDefinition(Class dataObjectClass, String attributeName) {
        return getAttributeControlDefinition(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeDescription(java.lang.String, java.lang.String)
     */
    @Override
	public String getAttributeDescription(Class dataObjectClass, String attributeName) {
        return getAttributeDescription(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeForceUppercase(java.lang.String, java.lang.String)
     */
    @Override
	public Boolean getAttributeForceUppercase(Class dataObjectClass, String attributeName) {
        return getAttributeForceUppercase(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeFormatter(java.lang.String, java.lang.String)
     */
    @Override
	public Class<? extends Formatter> getAttributeFormatter(Class dataObjectClass, String attributeName) {
        return getAttributeFormatter(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeLabel(java.lang.String, java.lang.String)
     */
    @Override
	public String getAttributeLabel(Class dataObjectClass, String attributeName) {
        return getAttributeLabel(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeMaxLength(java.lang.String, java.lang.String)
     */
    @Override
	public Integer getAttributeMaxLength(Class dataObjectClass, String attributeName) {
        return getAttributeMaxLength(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeShortLabel(java.lang.String, java.lang.String)
     */
    @Override
	public String getAttributeShortLabel(Class dataObjectClass, String attributeName) {
        return getAttributeShortLabel(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeErrorLabel(java.lang.String, java.lang.String)
     */
    @Override
	public String getAttributeErrorLabel(Class dataObjectClass, String attributeName) {
        return getAttributeErrorLabel(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeSize(java.lang.String, java.lang.String)
     */
    @Override
	public Integer getAttributeSize(Class dataObjectClass, String attributeName) {
        return getAttributeSize(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeSummary(java.lang.String, java.lang.String)
     */
    @Override
	public String getAttributeSummary(Class dataObjectClass, String attributeName) {
        return getAttributeSummary(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeValidatingExpression(java.lang.String, java.lang.String)
     */
    @Override
	public Pattern getAttributeValidatingExpression(Class dataObjectClass, String attributeName) {
        return getAttributeValidatingExpression(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeValuesFinderClass(java.lang.String, java.lang.String)
     */
    @Override
	public Class getAttributeValuesFinderClass(Class dataObjectClass, String attributeName) {
        return getAttributeValuesFinderClass(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeValidatingErrorMessageKey(java.lang.String, java.lang.String)
     */
    @Override
	public String getAttributeValidatingErrorMessageKey(String entryName, String attributeName) {
        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (attributeDefinition.hasValidationPattern()) {
                ValidationPattern validationPattern = attributeDefinition.getValidationPattern();
                return validationPattern.getValidationErrorMessageKey();
            }
        }
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAttributeValidatingErrorMessageParameters(java.lang.String, java.lang.String)
     */
    @Override
	public String[] getAttributeValidatingErrorMessageParameters(String entryName, String attributeName) {
        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (attributeDefinition.hasValidationPattern()) {
                ValidationPattern validationPattern = attributeDefinition.getValidationPattern();
                String attributeLabel = getAttributeErrorLabel(entryName, attributeName);
                return validationPattern.getValidationErrorMessageParameters(attributeLabel);
            }
        }
        return null;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getCollectionDescription(java.lang.String, java.lang.String)
     */
    @Override
	public String getCollectionDescription(Class dataObjectClass, String collectionName) {
        return getCollectionDescription(dataObjectClass.getName(), collectionName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getCollectionLabel(java.lang.String, java.lang.String)
     */
    @Override
	public String getCollectionLabel(Class dataObjectClass, String collectionName) {
        return getCollectionLabel(dataObjectClass.getName(), collectionName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getCollectionShortLabel(java.lang.String, java.lang.String)
     */
    @Override
	public String getCollectionShortLabel(Class dataObjectClass, String collectionName) {
        return getCollectionShortLabel(dataObjectClass.getName(), collectionName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getCollectionSummary(java.lang.String, java.lang.String)
     */
    @Override
	public String getCollectionSummary(Class dataObjectClass, String collectionName) {
        return getCollectionSummary(dataObjectClass.getName(), collectionName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#isAttributeDefined(java.lang.String, java.lang.String)
     */
    @Override
	public Boolean isAttributeDefined(Class dataObjectClass, String attributeName) {
        return isAttributeDefined(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#isAttributeRequired(java.lang.String, java.lang.String)
     */
    @Override
	public Boolean isAttributeRequired(Class dataObjectClass, String attributeName) {
        return isAttributeRequired(dataObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getDocumentLabelByClass(java.lang.Class)
     */
    @Override
	public String getDocumentLabelByClass(Class documentOrBusinessObjectClass) {
        return getDocumentLabelByTypeName(getDocumentTypeNameByClass(documentOrBusinessObjectClass));
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getDocumentLabelByTypeName(java.lang.String)
     */
    @Override
	public String getDocumentLabelByTypeName(String documentTypeName) {
        String label = null;
        if (StringUtils.isNotBlank(documentTypeName)) {
            DocumentType documentType = getDocumentTypeService().getDocumentTypeByName(documentTypeName);
            if (documentType != null) {
                label = documentType.getLabel();
            }
        }
        return label;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getDocumentTypeNameByClass(java.lang.Class)
     */
    @Override
	public String getDocumentTypeNameByClass(Class documentClass) {
        if (documentClass == null) {
            throw new IllegalArgumentException("invalid (null) documentClass");
        }
        if (!Document.class.isAssignableFrom(documentClass)) {
            throw new IllegalArgumentException("invalid (non-Document) documentClass");
        }

        String documentTypeName = null;

        DocumentEntry documentEntry = getDataDictionary().getDocumentEntry(documentClass.getName());
        if (documentEntry != null) {
            documentTypeName = documentEntry.getDocumentTypeName();
        }

        return documentTypeName;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getValidDocumentTypeNameByClass(java.lang.Class)
     */
    @Override
	public String getValidDocumentTypeNameByClass(Class documentClass) {
        String documentTypeName = getDocumentTypeNameByClass(documentClass);
        if (StringUtils.isBlank(documentTypeName)) {
            throw new UnknownDocumentTypeException(
                    "unable to get documentTypeName for unknown documentClass '" + documentClass.getName() + "'");
        }
        return documentTypeName;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getDocumentClassByTypeName(java.lang.String)
     */
    @Override
	public Class<? extends Document> getDocumentClassByTypeName(String documentTypeName) {
        Class clazz = null;

        DocumentEntry documentEntry = getDataDictionary().getDocumentEntry(documentTypeName);
        if (documentEntry != null) {
            clazz = documentEntry.getDocumentClass();
        }

        return clazz;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getValidDocumentClassByTypeName(java.lang.String)
     */
    @Override
	public Class<? extends Document> getValidDocumentClassByTypeName(String documentTypeName) {
        Class clazz = getDocumentClassByTypeName(documentTypeName);
        if (clazz == null) {
            throw new UnknownDocumentTypeException(
                    "unable to get class for unknown documentTypeName '" + documentTypeName + "'");
        }
        return clazz;
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getViewById(java.lang.String)
     */
    @Override
	public View getViewById(String viewId) {
        return dataDictionary.getViewById(viewId);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getDictionaryBean(java.lang.String)
     */
    @Override
	public Object getDictionaryBean(String id) {
        return dataDictionary.getDictionaryBean(id);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#containsDictionaryBean(java.lang.String)
     */
    @Override
	public boolean containsDictionaryBean(String id) {
        return dataDictionary.containsDictionaryBean(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getDictionaryBeanProperty(String beanName, String propertyName) {
        return dataDictionary.getDictionaryBeanProperty(beanName, propertyName);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getViewByTypeIndex(java.lang.String,
     *      java.util.Map)
     */
    @Override
	public View getViewByTypeIndex(ViewType viewTypeName, Map<String, String> indexKey) {
        return dataDictionary.getViewByTypeIndex(viewTypeName, indexKey);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getViewIdByTypeIndex(org.kuali.rice.krad.uif.UifConstants.ViewType,
     * java.util.Map<java.lang.String,java.lang.String>)
     */
    @Override
	public String getViewIdByTypeIndex(ViewType viewTypeName, Map<String, String> indexKey) {
        return dataDictionary.getViewIdByTypeIndex(viewTypeName, indexKey);
    }

    /**
     * @see org.kuali.rice.krad.service.DataDictionaryService#getGroupByAttributesForEffectiveDating(java.lang.Class)
     */
    @Override
	public List<String> getGroupByAttributesForEffectiveDating(Class dataObjectClass) {
        List<String> groupByList = null;

        DataObjectEntry objectEntry = getDataDictionary().getDataObjectEntry(dataObjectClass.getName());
        if (objectEntry != null) {
            groupByList = objectEntry.getGroupByAttributesForEffectiveDating();
        }

        return groupByList;
    }

    /**
     * Returns all of the inactivation blocks registered for a particular business object
     *
     * @see org.kuali.rice.krad.service.DataDictionaryService#getAllInactivationBlockingDefinitions(java.lang.Class)
     */
    @Override
	public Set<InactivationBlockingMetadata> getAllInactivationBlockingDefinitions(
            Class inactivationBlockedBusinessObjectClass) {
        Set<InactivationBlockingMetadata> blockingClasses =
                dataDictionary.getAllInactivationBlockingMetadatas(inactivationBlockedBusinessObjectClass);
        if (blockingClasses == null) {
            return Collections.emptySet();
        }
        return blockingClasses;
    }

    @Override
    public Map<Class<?>, String> getPropertyEditorMap() {
        return propertyEditorMap;
    }

    /**
     * @see #getPropertyEditorMap()
     *
     * @param propertyEditorMap mapping from property type to property editor
     */
    public void setPropertyEditorMap(Map<Class<?>, String> propertyEditorMap) {
        this.propertyEditorMap = propertyEditorMap;
    }

    public DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = KewApiServiceLocator.getDocumentTypeService();
        }
        return documentTypeService;
    }

    public void setKualiConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public ConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
