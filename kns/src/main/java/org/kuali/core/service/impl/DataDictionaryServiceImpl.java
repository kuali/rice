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
package org.kuali.core.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.datadictionary.AttributeDefinition;
import org.kuali.core.datadictionary.BusinessObjectEntry;
import org.kuali.core.datadictionary.CollectionDefinition;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.DataDictionaryBuilder;
import org.kuali.core.datadictionary.DataDictionaryEntryBase;
import org.kuali.core.datadictionary.DocumentEntry;
import org.kuali.core.datadictionary.PrimitiveAttributeDefinition;
import org.kuali.core.datadictionary.RelationshipDefinition;
import org.kuali.core.datadictionary.ValidationCompletionUtils;
import org.kuali.core.datadictionary.control.ControlDefinition;
import org.kuali.core.datadictionary.exporter.DataDictionaryMap;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UnknownBusinessClassAttributeException;
import org.kuali.core.service.AuthorizationService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.service.KualiModuleService;

/**
 * This class is the service implementation for a DataDictionary. It is a thin wrapper around creating, initializing, and returning
 * a DataDictionary. This is the default, Kuali delivered implementation.
 */
public class DataDictionaryServiceImpl implements DataDictionaryService {

    private static Log LOG = LogFactory.getLog(DataDictionaryServiceImpl.class);

    private DataDictionaryBuilder dataDictionaryBuilder;
//    private List<String> baselineDirectories;
    private DataDictionaryMap dataDictionaryMap = new DataDictionaryMap( this );

    private KualiConfigurationService kualiConfigurationService;
    private KualiGroupService kualiGroupService;
    private KualiModuleService kualiModuleService;
    private AuthorizationService authorizationService;
    private String institutionId;

    /**
     * Default constructor.
     */
    public DataDictionaryServiceImpl(ValidationCompletionUtils validationCompletionUtils) {
        dataDictionaryBuilder = new DataDictionaryBuilder(validationCompletionUtils);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#setBaselinePackages(java.lang.String)
     */
    public void setBaselinePackages(List baselinePackages) {
    	this.addDataDictionaryLocations(baselinePackages);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getDataDictionary()
     */
    public DataDictionary getDataDictionary() {
        waitForDDInitCompletion();
        return dataDictionaryBuilder.getDataDictionary();
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeControlDefinition(java.lang.String)
     */
    public ControlDefinition getAttributeControlDefinition(String entryName, String attributeName) {
        ControlDefinition controlDefinition = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            controlDefinition = attributeDefinition.getControl();
        }

        return controlDefinition;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeSize(java.lang.String)
     */
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

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeMaxLength(java.lang.String)
     */
    public Integer getAttributeMaxLength(String entryName, String attributeName) {
        Integer maxLength = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            maxLength = attributeDefinition.getMaxLength();
        }

        return maxLength;
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeExclusiveMin
     */
    public BigDecimal getAttributeExclusiveMin(String entryName, String attributeName) {
        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        return attributeDefinition == null ? null : attributeDefinition.getExclusiveMin();
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeInclusiveMax
     */
    public BigDecimal getAttributeInclusiveMax(String entryName, String attributeName) {
        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        return attributeDefinition == null ? null : attributeDefinition.getInclusiveMax();
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeValidatingExpression(java.lang.String)
     */
    public Pattern getAttributeValidatingExpression(String entryName, String attributeName) {
        Pattern regex = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (attributeDefinition.hasValidationPattern()) {
                regex = attributeDefinition.getValidationPattern().getRegexPattern();
            }
            else {
                // workaround for existing calls which don't bother checking for null return values
                regex = Pattern.compile(".*");
            }
        }

        return regex;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeLabel(java.lang.String)
     */
    public String getAttributeLabel(String entryName, String attributeName) {
        String label = "";

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if ( !StringUtils.isEmpty( attributeDefinition.getDisplayLabelAttribute() ) ) {
                attributeDefinition = getAttributeDefinition(entryName, attributeDefinition.getDisplayLabelAttribute());
                if (attributeDefinition != null) {
                    label = attributeDefinition.getLabel();
                }
            }
            label = attributeDefinition.getLabel();
        }

        return label;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeShortLabel(java.lang.String)
     */
    public String getAttributeShortLabel(String entryName, String attributeName) {
        String shortLabel = "";

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if ( !StringUtils.isEmpty( attributeDefinition.getDisplayLabelAttribute() ) ) {
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


    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeErrorLabel(java.lang.String)
     */
    public String getAttributeErrorLabel(String entryName, String attributeName) {
        String longAttributeLabel = this.getAttributeLabel(entryName, attributeName);
        String shortAttributeLabel = this.getAttributeShortLabel(entryName, attributeName);
        return longAttributeLabel + " (" + shortAttributeLabel + ")";
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeFormatter(java.lang.String)
     */
    public Class getAttributeFormatter(String entryName, String attributeName) {
        Class formatterClass = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            if (attributeDefinition.hasFormatterClass()) {
                formatterClass = attributeDefinition.getFormatterClass();
            }
        }

        return formatterClass;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeForceUppercase(java.lang.String)
     */
    public Boolean getAttributeForceUppercase(String entryName, String attributeName) throws UnknownBusinessClassAttributeException {
        Boolean forceUppercase = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition == null) {
            throw new UnknownBusinessClassAttributeException("Could not find a matching data dictionary business class attribute entry for " + entryName + "." + attributeName);
        }
        forceUppercase = attributeDefinition.getForceUppercase();

        return forceUppercase;
    }


    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeDisplayMask(java.lang.String, java.lang.String)
     */
    public Mask getAttributeDisplayMask(String entryName, String attributeName) {
        Mask displayMask = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            displayMask = attributeDefinition.getDisplayMask();
        }

        return displayMask;
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeDisplayWorkgroup(java.lang.String, java.lang.String)
     */
    public String getAttributeDisplayWorkgroup(String entryName, String attributeName) {
        String displayWorkgroup = "";

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            displayWorkgroup = attributeDefinition.getDisplayWorkgroup();
        }

        return displayWorkgroup;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeSummary(java.lang.String)
     */
    public String getAttributeSummary(String entryName, String attributeName) {
        String summary = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            summary = attributeDefinition.getSummary();
        }

        return summary;
    }


    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeDescription(java.lang.String)
     */
    public String getAttributeDescription(String entryName, String attributeName) {
        String description = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            description = attributeDefinition.getDescription();
        }

        return description;
    }


    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#isAttributeRequired(java.lang.Class, java.lang.String)
     */
    public Boolean isAttributeRequired(String entryName, String attributeName) {
        Boolean required = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            required = attributeDefinition.isRequired();
        }

        return required;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#isAttributeDefined(java.lang.Class, java.lang.String)
     */
    public Boolean isAttributeDefined(String entryName, String attributeName) {
        boolean isDefined = false;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            isDefined = true;
        }

        return Boolean.valueOf(isDefined);
    }


    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getAttributeValuesScopeId(java.lang.Class, java.lang.String)
     */
    public Class getAttributeValuesFinderClass(String entryName, String attributeName) {
        Class valuesFinderClass = null;

        AttributeDefinition attributeDefinition = getAttributeDefinition(entryName, attributeName);
        if (attributeDefinition != null) {
            valuesFinderClass = attributeDefinition.getControl().getValuesFinderClass();
        }

        return valuesFinderClass;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getCollectionLabel(java.lang.Class, java.lang.String)
     */
    public String getCollectionLabel(String entryName, String collectionName) {
        String label = "";

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            label = collectionDefinition.getLabel();
        }

        return label;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getCollectionShortLabel(java.lang.Class, java.lang.String)
     */
    public String getCollectionShortLabel(String entryName, String collectionName) {
        String shortLabel = "";

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            shortLabel = collectionDefinition.getShortLabel();
        }

        return shortLabel;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getCollectionElementLabel(java.lang.Class, java.lang.String)
     */
    public String getCollectionElementLabel(String entryName, String collectionName, Class businessObjectClass) {
        String elementLabel = "";

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            elementLabel = collectionDefinition.getElementLabel();
            if(StringUtils.isEmpty(elementLabel)) {
                BusinessObjectEntry boe = getDataDictionary().getBusinessObjectEntry(businessObjectClass.getName());
                if(boe!=null){
                    elementLabel = boe.getObjectLabel();
                }
            }
        }

        return elementLabel;
    }

    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getCollectionSummary(java.lang.Class, java.lang.String)
     */
    public String getCollectionSummary(String entryName, String collectionName) {
        String summary = null;

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            summary = collectionDefinition.getSummary();
        }

        return summary;
    }


    /**
     * @see org.kuali.core.service.BusinessObjectDictionaryService#getCollectionDescription(java.lang.Class, java.lang.String)
     */
    public String getCollectionDescription(String entryName, String collectionName) {
        String description = null;

        CollectionDefinition collectionDefinition = getCollectionDefinition(entryName, collectionName);
        if (collectionDefinition != null) {
            description = collectionDefinition.getDescription();
        }

        return description;
    }

    public Class getRelationshipSourceClass(String entryName, String relationshipName) {
        Class sourceClass = null;

        RelationshipDefinition rd = getRelationshipDefinition(entryName, relationshipName);
        if (rd != null) {
            sourceClass = rd.getSourceClass();
        }


        return sourceClass;
    }

    public Class getRelationshipTargetClass(String entryName, String relationshipName) {
        Class targetClass = null;

        RelationshipDefinition rd = getRelationshipDefinition(entryName, relationshipName);
        if (rd != null) {
            targetClass = rd.getTargetClass();
        }

        return targetClass;
    }

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


    public List<String> getRelationshipEntriesForSourceAttribute(String entryName, String sourceAttributeName) {
        List<String> relationships = new ArrayList<String>();
        
        DataDictionaryEntryBase entry = (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
        
        for (RelationshipDefinition def : entry.getRelationships().values()) {
            for (PrimitiveAttributeDefinition pddef : def.getPrimitiveAttributes()) {
                if (StringUtils.equals(sourceAttributeName, pddef.getSourceName())) {
                    relationships.add(def.getObjectAttributeName());
                    break;
                }
            }
        }
        return relationships;
    }

    public List<String> getRelationshipEntriesForTargetAttribute(String entryName, String targetAttributeName) {
        List<String> relationships = new ArrayList<String>();
        
        DataDictionaryEntryBase entry = (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
        
        for (RelationshipDefinition def : entry.getRelationships().values()) {
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
     * @param objectClass
     * @param attributeName
     * @return AttributeDefinition for the given objectClass and attribute name, or null if there is none
     * @throws IllegalArgumentException if the given Class is null or is not a BusinessObject class
     */
    private AttributeDefinition getAttributeDefinition(String entryName, String attributeName) {
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }
        AttributeDefinition attributeDefinition = null;

        DataDictionaryEntryBase entry = (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
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

        DataDictionaryEntryBase entry = (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
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

        DataDictionaryEntryBase entry = (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            relationshipDefinition = entry.getRelationshipDefinition(relationshipName);
        }

        return relationshipDefinition;
    }


    /**
     * @see org.kuali.core.service.DataDictionaryService#getRelationshipAttributeMap(java.lang.String, java.lang.String)
     */
    public Map<String, String> getRelationshipAttributeMap(String entryName, String relationshipName) {
        Map<String, String> attributeMap = new HashMap<String, String>();
        RelationshipDefinition relationshipDefinition = getRelationshipDefinition(entryName, relationshipName);
        for (Iterator iter = relationshipDefinition.getPrimitiveAttributes().iterator(); iter.hasNext();) {
            PrimitiveAttributeDefinition attribute = (PrimitiveAttributeDefinition) iter.next();
            attributeMap.put(attribute.getTargetName(), attribute.getSourceName());
        }
        return attributeMap;
    }

    public boolean hasRelationship(String entryName, String relationshipName) {
        return getRelationshipDefinition(entryName, relationshipName) != null;
    }

    public List<String> getRelationshipNames(String entryName) {
        DataDictionaryEntryBase entry = (DataDictionaryEntryBase) getDataDictionary().getDictionaryObjectEntry(entryName);
        
        List<String> relationshipNames = new ArrayList<String>();
        for (String relationshipName : entry.getRelationships().keySet()) {
            relationshipNames.add(relationshipName);
        }
        return relationshipNames;
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeControlDefinition(java.lang.String, java.lang.String)
     */
    public ControlDefinition getAttributeControlDefinition(Class businessObjectClass, String attributeName) {
        return getAttributeControlDefinition(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeDescription(java.lang.String, java.lang.String)
     */
    public String getAttributeDescription(Class businessObjectClass, String attributeName) {
        return getAttributeDescription(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeForceUppercase(java.lang.String, java.lang.String)
     */
    public Boolean getAttributeForceUppercase(Class businessObjectClass, String attributeName) {
        return getAttributeForceUppercase(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeFormatter(java.lang.String, java.lang.String)
     */
    public Class getAttributeFormatter(Class businessObjectClass, String attributeName) {
        return getAttributeFormatter(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeLabel(java.lang.String, java.lang.String)
     */
    public String getAttributeLabel(Class businessObjectClass, String attributeName) {
        return getAttributeLabel(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeMaxLength(java.lang.String, java.lang.String)
     */
    public Integer getAttributeMaxLength(Class businessObjectClass, String attributeName) {
        return getAttributeMaxLength(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeShortLabel(java.lang.String, java.lang.String)
     */
    public String getAttributeShortLabel(Class businessObjectClass, String attributeName) {
        return getAttributeShortLabel(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeErrorLabel(java.lang.String, java.lang.String)
     */
    public String getAttributeErrorLabel(Class businessObjectClass, String attributeName) {
        return getAttributeErrorLabel(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeSize(java.lang.String, java.lang.String)
     */
    public Integer getAttributeSize(Class businessObjectClass, String attributeName) {
        return getAttributeSize(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeSummary(java.lang.String, java.lang.String)
     */
    public String getAttributeSummary(Class businessObjectClass, String attributeName) {
        return getAttributeSummary(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeValidatingExpression(java.lang.String, java.lang.String)
     */
    public Pattern getAttributeValidatingExpression(Class businessObjectClass, String attributeName) {
        return getAttributeValidatingExpression(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeValuesFinderClass(java.lang.String, java.lang.String)
     */
    public Class getAttributeValuesFinderClass(Class businessObjectClass, String attributeName) {
        return getAttributeValuesFinderClass(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getCollectionDescription(java.lang.String, java.lang.String)
     */
    public String getCollectionDescription(Class businessObjectClass, String collectionName) {
        return getCollectionDescription(businessObjectClass.getName(), collectionName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getCollectionLabel(java.lang.String, java.lang.String)
     */
    public String getCollectionLabel(Class businessObjectClass, String collectionName) {
        return getCollectionLabel(businessObjectClass.getName(), collectionName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getCollectionShortLabel(java.lang.String, java.lang.String)
     */
    public String getCollectionShortLabel(Class businessObjectClass, String collectionName) {
        return getCollectionShortLabel(businessObjectClass.getName(), collectionName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeDisplayMask(java.lang.Class, java.lang.String)
     */
    public Mask getAttributeDisplayMask(Class businessObjectClass, String attributeName) {
        return getAttributeDisplayMask(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getAttributeDisplayWorkgroup(java.lang.Class, java.lang.String)
     */
    public String getAttributeDisplayWorkgroup(Class businessObjectClass, String attributeName) {
        return getAttributeDisplayWorkgroup(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getCollectionSummary(java.lang.String, java.lang.String)
     */
    public String getCollectionSummary(Class businessObjectClass, String collectionName) {
        return getCollectionSummary(businessObjectClass.getName(), collectionName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#isAttributeDefined(java.lang.String, java.lang.String)
     */
    public Boolean isAttributeDefined(Class businessObjectClass, String attributeName) {
        return isAttributeDefined(businessObjectClass.getName(), attributeName);
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#isAttributeRequired(java.lang.String, java.lang.String)
     */
    public Boolean isAttributeRequired(Class businessObjectClass, String attributeName) {
        return isAttributeRequired(businessObjectClass.getName(), attributeName);

    }

//    /**
//     * @see org.kuali.core.service.DataDictionaryService#getDocumentObjectClassnames()
//     */
//    public List getDocumentObjectClassnames() {
//        return getDataDictionary().getDocumentObjectClassNames();
//    }
    
    /**
     * @see org.kuali.core.service.DataDictionaryService#getDocumentLabelByClass(java.lang.Class)
     */
    public String getDocumentLabelByClass(Class documentOrBusinessObjectClass) {
        String label = null;
        DocumentEntry documentEntry = getDataDictionary().getDocumentEntry(documentOrBusinessObjectClass.getName());
        if (documentEntry != null) {
            label = documentEntry.getLabel();
        }
        return label;
    }
    
    /**
     * @see org.kuali.core.service.DataDictionaryService#getDocumentLabelByTypeName(java.lang.String)
     */
    public String getDocumentLabelByTypeName(String documentTypeName) {
        String label = null;
        DocumentEntry documentEntry = getDataDictionary().getDocumentEntry(documentTypeName);
        if (documentEntry != null) {
            label = documentEntry.getLabel();
        }
        return label;
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getDocumentTypeNameByClass(java.lang.Class)
     */
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
     * @see org.kuali.core.service.DataDictionaryService#getDocumentClassByTypeName(java.lang.String)
     */
    public Class getDocumentClassByTypeName(String documentTypeName) {
        Class clazz = null;

        DocumentEntry documentEntry = getDataDictionary().getDocumentEntry(documentTypeName);
        if (documentEntry != null) {
            clazz = documentEntry.getDocumentClass();
        }

        return clazz;
    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getDocumentTypeCodeByTypeName(java.lang.String)
     */
    public String getDocumentTypeCodeByTypeName(String documentTypeName) {
        DocumentEntry documentEntry = getDataDictionary().getDocumentEntry(documentTypeName);
        if (documentEntry != null) {
            return documentEntry.getDocumentTypeCode();
        }
        return null;
    }


//    /**
//     * @see org.kuali.core.service.DataDictionaryService#getDocumentTypeNameByTypeCode(java.lang.String)
//     */
//    public String getDocumentTypeNameByTypeCode(String documentTypeCode) {
//        DocumentEntry documentEntry = getDataDictionary().getDocumentEntryByCode(documentTypeCode);
//        if (documentEntry != null) {
//            return documentEntry.getDocumentTypeName();
//        }
//        return null;
//    }

    /**
     * @see org.kuali.core.service.DataDictionaryService#getPreRulesCheckClass(java.lang.String)
     */
    public Class getPreRulesCheckClass(String docTypeName) {
        Class preRulesCheckClass = null;

        DocumentEntry documentEntry = getDataDictionary().getDocumentEntry(docTypeName);
        if (documentEntry != null) {
            preRulesCheckClass = documentEntry.getPreRulesCheckClass();
        }

        return preRulesCheckClass;
    }

    // set up a latch with a count of one - one call the countDown will release the latch
    private CountDownLatch ddLoadLatch = new CountDownLatch(1);
    
    private void waitForDDInitCompletion() {
//        try {
////            ddLoadLatch.await();
//        } catch ( InterruptedException ex ) {
//            // do nothing
//        }
        }
    
    public void addDataDictionaryLocation(String location) {
    	dataDictionaryBuilder.addOverrideEntries(location, true);
                }
                
    public void addDataDictionaryLocations(List<String> locations) {
    	for (String location : locations) {
			addDataDictionaryLocation(location);
                }
            }
            
//    /**
//     * @see org.kuali.core.service.DataDictionaryService#completeInitialization()
//     */
//    public void completeInitialization() {
//        try {
//            dataDictionaryBuilder.setKualiConfigurationService(getKualiConfigurationService());
//            dataDictionaryBuilder.setKualiGroupService(getKualiGroupService());
//    
//            LOG.info( "starting background DataDictionary init" );
//            if (baselineDirectories != null) {
//                for ( String dirName : baselineDirectories ) {
//                    dataDictionaryBuilder.addUniqueEntries(dirName, true);
//        }
//
//                List<String> overrideDirectories = convertBaselineToOverrideDirectories( baselineDirectories );
//                for (String dirName : overrideDirectories ) {
//                    dataDictionaryBuilder.addOverrideEntries(dirName, false);
//    }
//            }
//            
//            List<String> moduleDirectories = kualiModuleService.getDataDictionaryPackages();
//            for ( String dirName : moduleDirectories ) {
//                dataDictionaryBuilder.addUniqueEntries(dirName, true);
//            }
//            
//            List<String> overrideDirectories = convertBaselineToOverrideDirectories( moduleDirectories );
//            for (String dirName : overrideDirectories ) {
//                dataDictionaryBuilder.addOverrideEntries(dirName, false);
//            }
//    
//    
//            dataDictionaryBuilder.completeInitialization();
//            // need to pass in the DD to the authorization service since the latch has not been released yet
//            authorizationService.completeInitialization( dataDictionaryBuilder.getDataDictionary() );
//            LOG.info( "completed DataDictionary init - releasing latch" );
//        } finally {
//            // ensure that the latch gets released, even if there is a problem
//            ddLoadLatch.countDown();
//        }
//    }

    
    /**
     * Adds the instution extension to the directory name for use as the institutional override directories.
     * 
     * @param dirList
     * @return
     */
    private List<String> convertBaselineToOverrideDirectories(List<String> dirList) {
        List<String> directoryList = new ArrayList<String>();
        for (String dirName :  dirList ) {
        	//only allow directories to do the institutional overrides.
        	if (dirName.indexOf("xml") < 0) {
        		directoryList.add( dirName + "/" + institutionId );	
        	}
        }

        return directoryList;
    }
    
    public Map getDataDictionaryMap() {
        return dataDictionaryMap;
    }
    

//    public void addUniqueEntries(String sourceName, boolean sourceMustExist) {
//        dataDictionaryBuilder.addUniqueEntries(sourceName, sourceMustExist);
//
//    }

    public void setKualiGroupService(KualiGroupService kualiGroupService) {
        this.kualiGroupService = kualiGroupService;
    }

    public KualiGroupService getKualiGroupService() {
        return kualiGroupService;
    }


    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public AuthorizationService getAuthorizationService() {
        return authorizationService;
    }

    public void setAuthorizationService(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }
}