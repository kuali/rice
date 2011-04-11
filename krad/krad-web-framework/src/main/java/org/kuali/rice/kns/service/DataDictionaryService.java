/*
 * Copyright 2005-2007 The Kuali Foundation
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
package org.kuali.rice.kns.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.AttributeSecurity;
import org.kuali.rice.kns.datadictionary.DataDictionary;
import org.kuali.rice.kns.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.kns.datadictionary.control.ControlDefinition;
import org.kuali.rice.kns.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.rule.PromptBeforeValidation;
import org.kuali.rice.kns.uif.container.View;


/**
 * Defines the API for interacting with the data dictionary.
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
    public void setBaselinePackages(List baselinePackages) throws IOException;

    /**
     * @return current DataDictionary
     */
    public DataDictionary getDataDictionary();

    public void addDataDictionaryLocations(List<String> locations) throws IOException;

//    /**
//     * Hook to allow the dataDictionary service to perform any post-build initialization tasks needed before the dataDictionary
//     * itself will be publicly available.
//     */
//    public void completeInitialization();

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
    public Class<? extends Formatter> getAttributeFormatter(Class businessObjectClass, String attributeName);

    /**
     * indicates whether or not to force input text into uppercase
     */
    public Boolean getAttributeForceUppercase(Class businessObjectClass, String attributeName);

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
    public Class<? extends KeyValuesFinder> getAttributeValuesFinderClass(Class businessObjectClass, String attributeName);

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
     * the min length defined for the given attribute name.
     */
    public Integer getAttributeMinLength(String entryName, String attributeName);
    
    /**
     * the max length defined for the given attribute name.
     */
    public Integer getAttributeMaxLength(String entryName, String attributeName);

    /**
     * @param entryName
     * @param attributeName
     * @return the exclusive minimum for the specified attribute, or <code>null</code> if none.
     */
    public /*BigDecimal*/ String getAttributeExclusiveMin(String entryName, String attributeName);

    /**
     * @param entryName
     * @param attributeName
     * @return the inclusive maximum for the specified attribute, or <code>null</code> if none.
     */
    public /*BigDecimal*/ String getAttributeInclusiveMax(String entryName, String attributeName);


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
    public Class<? extends Formatter> getAttributeFormatter(String entryName, String attributeName);


    /**
     * indicates whether or not to force input text into uppercase
     */
    public Boolean getAttributeForceUppercase(String entryName, String attributeName);

    /**
     * the AttributeSecurity object defined for the attribute's data value
     */
    public AttributeSecurity getAttributeSecurity(String entryName, String attributeName);

    /**
     * short help text for attribute
     */
    public String getAttributeSummary(String entryName, String attributeName);


    /**
     * detailed help text for attribute
     */
    public String getAttributeDescription(String entryName, String attributeName);

    public String getAttributeValidatingErrorMessageKey(String entryName, String attributeName);
    
    public String[] getAttributeValidatingErrorMessageParameters(String entryName, String attributeName);
    
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
    public Class<? extends KeyValuesFinder> getAttributeValuesFinderClass(String entryName, String attributeName);
    
    /**
     * AttributeDefinition associated with the given attributeName within the given entry
     */
    public AttributeDefinition getAttributeDefinition(String entryName, String attributeName);

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
    public Class<? extends BusinessObject> getRelationshipSourceClass(String entryName, String relationshipName);

    /**
     * @param entryName
     * @param relationshipName
     * @return target Class for the given relationship, or null if there is no relationship with that name
     */
    public Class<? extends BusinessObject> getRelationshipTargetClass(String entryName, String relationshipName);

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
    
//    /**
//     * Returns the list of document class names
//     * 
//     * @return
//     */
//    public List getDocumentObjectClassnames();
    
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
     * Returns the document type name declared in the dd for the given document
     * class. If no valid document type is found 'null' is returned.
     * 
     * @param documentClass
     * @return documentTypeName
     */
    public String getDocumentTypeNameByClass(Class documentClass);

    /**
     * Returns the document type name declared in the dd for the given document
     * class. If no valid document type is found an
     * {@link UnknownDocumentTypeException} is thrown.
     * 
     * @param documentClass
     * @return documentTypeName
     */
    public String getValidDocumentTypeNameByClass(Class documentClass);

    /**
     * Returns the document class declared in the dd for the given document type
     * name. If no document entry is found with given document type name, 'null'
     * will be returned.
     * 
     * @param documentTypeName
     * @return document Class
     */
    public Class<? extends Document> getDocumentClassByTypeName(String documentTypeName);

    /**
     * Returns the document class declared in the dd for the given document type
     * name. If no document entry is found with given document type name, and
     * {@link UnknownDocumentTypeException} will be thrown.
     * 
     * @param documentTypeName
     * @return document Class
     */
    public Class<? extends Document> getValidDocumentClassByTypeName(String documentTypeName);

    /**
     * @param document
     * @return preRulesCheckClass associated with the given document's type
     */
    public Class<? extends PromptBeforeValidation> getPromptBeforeValidationClass(String docTypeName);

    public Map getDataDictionaryMap();

    /**
     * Returns all of the inactivation blocks registered for a particular business object
     * 
     * @param inactivationBlockedBusinessObjectClass
     * @return a set of all registered inactivation blocks for a particular business object
     */
    public Set<InactivationBlockingMetadata> getAllInactivationBlockingDefinitions(Class inactivationBlockedBusinessObjectClass);
    
	/**
	 * Returns the View entry identified by the given id
	 * 
	 * @param viewId - unique id for view
	 * @return View instance associated with the id
	 */
	public View getViewById(String viewId);
	
	/**
	 * Returns View instance identified by the view type name and index
	 * 
	 * @param viewTypeName
	 *            - type name for the view
	 * @param indexKey
	 *            - Map of index key parameters, these are the parameters the
	 *            indexer used to index the view initially and needs to identify
	 *            an unique view instance
	 * @return View instance that matches the given index
	 */
	public View getViewByTypeIndex(String viewTypeName, Map<String, String> indexKey);
}
