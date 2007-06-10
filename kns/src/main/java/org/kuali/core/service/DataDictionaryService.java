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
package org.kuali.core.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.datadictionary.control.ControlDefinition;
import org.kuali.core.datadictionary.exception.SourceException;
import org.kuali.core.datadictionary.mask.Mask;


/**
 * This interface defines the API for interacting with the data dictionary.
 * 
 * 
 */
public interface DataDictionaryService {
    /**
     * Sequentially adds each package named (as a String) in the given List as a source of unique entries to the DataDictionary
     * being constructed. Duplicate entries among any of the XML files in any of these packages will result in exceptions being
     * thrown, hence service-initialization failure.
     * 
     * @param baselinePackages
     * @throws SourceException if any of the given packages can't be located
     */
    public void setBaselinePackages(List baselinePackages);

    /**
     * @return current DataDictionary
     */
    public DataDictionary getDataDictionary();


    /**
     * Hook to allow the dataDictionary service to perform any post-build initialization tasks needed before the dataDictionary
     * itself will be publicly available.
     */
    public void completeInitialization();

    /**
     * the html control type used to render the field
     */
    public ControlDefinition getAttributeControlDefinition(Class businessObjectClass, String attributeName);


    /**
     * the display size of the field if text control
     */
    public Integer getAttributeSize(Class businessObjectClass, String attributeName);


    /**
     * the max length defined for the given attribute name.
     */
    public Integer getAttributeMaxLength(Class businessObjectClass, String attributeName);


    /**
     * the regular expression defined to validate the given attribute name.
     */
    public Pattern getAttributeValidatingExpression(Class businessObjectClass, String attributeName);


    /**
     * the label to be used for displaying the attribute.
     */
    public String getAttributeLabel(Class businessObjectClass, String attributeName);


    /**
     * the short label to be used for displaying the attribute.
     */
    public String getAttributeShortLabel(Class businessObjectClass, String attributeName);

    
    /**
     * the "label (short label)" used for displaying error messages
     */
    public String getAttributeErrorLabel(Class businessObjectClass, String attributeName);

    /**
     * the formatter class used to format the attribute value
     */
    public Class getAttributeFormatter(Class businessObjectClass, String attributeName);


    /**
     * indicates whether or not to force input text into uppercase
     */
    public Boolean getAttributeForceUppercase(Class businessObjectClass, String attributeName);


    /**
     * the workgroup name (if specified) who has permission to view values for the field
     */
    public String getAttributeDisplayWorkgroup(Class businessObjectClass, String attributeName);


    /**
     * the Mask object defined for masking the attribute's data value
     */
    public Mask getAttributeDisplayMask(Class businessObjectClass, String attributeName);


    /**
     * short help text for attribute
     */
    public String getAttributeSummary(Class businessObjectClass, String attributeName);


    /**
     * detailed help text for attribute
     */
    public String getAttributeDescription(Class businessObjectClass, String attributeName);

    /**
     * indicates whether or not the named attribute is required
     */
    public Boolean isAttributeRequired(Class businessObjectClass, String attributeName);

    /**
     * indicates whether or not the named attribute is defined in the business object xml
     */
    public Boolean isAttributeDefined(Class businessObjectClass, String attributeName);

    /**
     * the Class that returns a values list for this attribute
     */
    public Class getAttributeValuesFinderClass(Class businessObjectClass, String attributeName);

    /**
     * the label to be used for displaying the collection.
     */
    public String getCollectionLabel(Class businessObjectClass, String collectionName);


    /**
     * the short label to be used for displaying the collection.
     */
    public String getCollectionShortLabel(Class businessObjectClass, String collectionName);

    
    /**
     * short help text for collection
     */
    public String getCollectionSummary(Class businessObjectClass, String collectionName);


    /**
     * detailed help text for collection
     */
    public String getCollectionDescription(Class businessObjectClass, String collectionName);

    /**
     * the html control type used to render the field
     */
    public ControlDefinition getAttributeControlDefinition(String entryName, String attributeName);


    /**
     * the display size of the field if text control
     */
    public Integer getAttributeSize(String entryName, String attributeName);


    /**
     * the max length defined for the given attribute name.
     */
    public Integer getAttributeMaxLength(String entryName, String attributeName);

    /**
     * @param entryName
     * @param attributeName
     * @return the exclusive minimum for the specified attribute, or <code>null</code> if none.
     */
    public BigDecimal getAttributeExclusiveMin(String entryName, String attributeName);

    /**
     * @param entryName
     * @param attributeName
     * @return the inclusive maximum for the specified attribute, or <code>null</code> if none.
     */
    public BigDecimal getAttributeInclusiveMax(String entryName, String attributeName);


    /**
     * the regular expression defined to validate the given attribute name.
     */
    public Pattern getAttributeValidatingExpression(String entryName, String attributeName);


    /**
     * the label to be used for displaying the attribute.
     */
    public String getAttributeLabel(String entryName, String attributeName);


    /**
     * the short label to be used for displaying the attribute.
     */
    public String getAttributeShortLabel(String entryName, String attributeName);

    
    /**
     * the "label (short label)" used for displaying error messages
     */
    public String getAttributeErrorLabel(String entryName, String attributeName);
    

    /**
     * the formatter class used to format the attribute value
     */
    public Class getAttributeFormatter(String entryName, String attributeName);


    /**
     * indicates whether or not to force input text into uppercase
     */
    public Boolean getAttributeForceUppercase(String entryName, String attributeName);

    /**
     * the workgroup name (if specified) who has permission to view values for the field
     */
    public String getAttributeDisplayWorkgroup(String entryName, String attributeName);


    /**
     * the Mask object defined for masking the attribute's data value
     */
    public Mask getAttributeDisplayMask(String entryName, String attributeName);

    /**
     * short help text for attribute
     */
    public String getAttributeSummary(String entryName, String attributeName);


    /**
     * detailed help text for attribute
     */
    public String getAttributeDescription(String entryName, String attributeName);

    /**
     * indicates whether or not the named attribute is required
     */
    public Boolean isAttributeRequired(String entryName, String attributeName);

    /**
     * indicates whether or not the named attribute is defined in the business object xml
     */
    public Boolean isAttributeDefined(String entryName, String attributeName);

    /**
     * the Class that returns a values list for this attribute
     */
    public Class getAttributeValuesFinderClass(String entryName, String attributeName);

    /**
     * the label to be used for displaying the collection.
     */
    public String getCollectionLabel(String entryName, String collectionName);


    /**
     * the short label to be used for displaying the collection.
     */
    public String getCollectionShortLabel(String entryName, String collectionName);

    /**
     * the element label to be used for displaying the collection.
     */
    public String getCollectionElementLabel(String entryName, String collectionName, Class businessObjectClass);

    
    /**
     * short help text for collection
     */
    public String getCollectionSummary(String entryName, String collectionName);


    /**
     * detailed help text for collection
     */
    public String getCollectionDescription(String entryName, String collectionName);


    /**
     * @param entryName
     * @param relationshipName
     * @return source Class for the given relationship, or null if there is no relationship with that name
     */
    public Class getRelationshipSourceClass(String entryName, String relationshipName);

    /**
     * @param entryName
     * @param relationshipName
     * @return target Class for the given relationship, or null if there is no relationship with that name
     */
    public Class getRelationshipTargetClass(String entryName, String relationshipName);

    /**
     * @param entryName
     * @param relationshipName
     * @return List<String> of source attributeNames for the given relationship, or null if there is no relationship with that name
     */
    public List<String> getRelationshipSourceAttributes(String entryName, String relationshipName);

    /**
     * @param entryName
     * @param relationshipName
     * @return List<String> of target attributeNames for the given relationship, or null if there is no relationship with that name
     */
    public List<String> getRelationshipTargetAttributes(String entryName, String relationshipName);

    /**
     * 
     * returns a Map that specifies the attributes of the relationship
     * @param entryName - Name of the Business Object entry
     * @param relationshipName - Name of the relationship
     * @return Map - Target field as key, source field as value
     */
    public Map<String, String> getRelationshipAttributeMap(String entryName, String relationshipName);

    /**
     * returns a list of names for all entries whose source parameter matches the parameter
     * @param entryName Name of the Business Object entry
     * @param sourceAttributeName name of the source attribute
     * @return the names of all entries that use the sourceAttributeName as a primitive attribute
     */
    public List<String> getRelationshipEntriesForSourceAttribute(String entryName, String sourceAttributeName);
    
    /**
     * returns a list of names for all entries whose source parameter matches the parameter
     * @param entryName Name of the Business Object entry
     * @param targetAttributeName name of the target attribute
     * @return the names of all entries that use the targetAttributeName as a primitive attribute
     */
    public List<String> getRelationshipEntriesForTargetAttribute(String entryName, String targetAttributeName);
    
    /**
     * Determines whether there is a relationship defined for the given entry with the given name
     * @param entryName name of the BO entry
     * @param relationshipName name of the relationship for the entry
     * @return true iff there is a relationship with the given name defined for the BO entry in the DD 
     */
    public boolean hasRelationship(String entryName, String relationshipName);
    
    /**
     * Returns all of the relationships defined for a BO in the DD
     * @param name of the BO entry
     * @return a list of all DD defined mappings
     */
    public List<String> getRelationshipNames(String entryName);
    
    /**
     * Returns the list of document class names
     * 
     * @return
     */
    public List getDocumentObjectClassnames();
    
    /**
     * This method returns the user friendly label based on the workflow doc type name
     * @param documentTypeName
     * @return label
     */
    public String getDocumentLabelByTypeName(String documentTypeName);
    
    /**
     * This method returns the user friendly label based on the document or business object class
     * @param documentTypeName
     * @return label
     */
    public String getDocumentLabelByClass(Class documentOrBusinessObjectClass);

    /**
     * Returns the document type name declared in the dd for the given document class.
     * 
     * @param documentClass
     * @return documentTypeName
     */
    public String getDocumentTypeNameByClass(Class documentClass);

    /**
     * Returns the document class declared in the dd for the given document type name.
     * 
     * @param documentTypeName
     * @return document Class
     */
    public Class getDocumentClassByTypeName(String documentTypeName);

    /**
     * Returns the document type code declared in the dd for the given document type name.
     * 
     * @param documentTypeName
     * @return documentTypeCode
     */
    public String getDocumentTypeCodeByTypeName(String documentTypeName);

    /**
     * Returns the document type name declared in the dd for the given document type code.
     * 
     * @param documentTypeName
     * @return documentTypeCode if the documentTypeName is registered, otherwise returns Null
     */
    public String getDocumentTypeNameByTypeCode(String documentTypeCode);

    /**
     * @param document
     * @return preRulesCheckClass associated with the given document's type
     */
    public Class getPreRulesCheckClass(String docTypeName);

    /**
     * Adds entries to the data dictionary.
     * 
     * @return
     */
    public void addUniqueEntries(String sourceName, boolean sourceMustExist);

    public Map getDataDictionaryMap();
}