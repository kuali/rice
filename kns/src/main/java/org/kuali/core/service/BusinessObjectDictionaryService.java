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

import java.util.List;

import org.kuali.core.bo.BusinessObject;


/**
 * This interface defines the API for the interacting with the data dictionary.
 */
public interface BusinessObjectDictionaryService {

    /**
     * the list of business object class names being maintained
     */
    public List getBusinessObjectClassnames();


    /**
     * indicates whether business object has lookup defined
     */
    public Boolean isLookupable(Class businessObjectClass);


    /**
     * indicates whether business object has inquiry defined
     */
    public Boolean isInquirable(Class businessObjectClass);


    /**
     * indicates whether business object has maintainable defined
     */
    public Boolean isMaintainable(Class businessObjectClass);

    
    /**
     * the list defined as lookup fields for the business object.
     */
    public List getLookupFieldNames(Class businessObjectClass);


    /**
     * the text to be displayed for the title of business object lookup.
     */
    public String getLookupTitle(Class businessObjectClass);


    /**
     * menu bar html defined for the business object.
     */
    public String getLookupMenuBar(Class businessObjectClass);


    /**
     * the text to be displayed for the instructions of business object lookup.
     */
    public String getLookupInstructions(Class businessObjectClass);

    /**
     * source for optional extra button
     */
    public String getExtraButtonSource(Class businessObjectClass);


    /**
     * return parameters for optional extra button
     */
    public String getExtraButtonParams(Class businessObjectClass);


    /**
     * the property names of the bo used to sort the initial result set
     */
    public List getLookupDefaultSortFieldNames(Class businessObjectClass);


    /**
     * the list defined as lookup result fields for the business object.
     */
    public List<String> getLookupResultFieldNames(Class businessObjectClass);

    /**
     * This method returns the maximum display length of the value of the given field in the lookup results.  While the actual value may
     * be longer than the specified length, this value specifies the maximum length substring that should be displayed.
     * It is up to the UI layer to intepret the results of the field
     * 
     * @param businessObjectClass
     * @param resultFieldName
     * @return the maximum length of the lookup results field that should be displayed.  Returns {@link org.kuali.RiceConstants#LOOKUP_RESULT_FIELD_MAX_LENGTH_NOT_DEFINED}
     * if this value has not been defined
     */
    public int getLookupResultFieldMaxLength(Class businessObjectClass, String resultFieldName);

    /**
     * returns boolean indicating whether lookup result field marked to force an inquiry
     */
    public Boolean forceLookupResultFieldInquiry(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether lookup result field marked to not do an inquiry
     */
    public Boolean noLookupResultFieldInquiry(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether lookup search field marked to force a lookup
     */
    public Boolean forceLookupFieldLookup(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether lookup search field marked to not do a lookup
     */
    public Boolean noLookupFieldLookup(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether inquiry result field marked to force an inquiry
     */
    public Boolean forceInquiryFieldInquiry(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether inquiry result field marked to not do an inquiry
     */
    public Boolean noInquiryFieldInquiry(Class businessObjectClass, String attributeName);


    /**
     * returns String indicating the default search value for the lookup field
     */
    public String getLookupFieldDefaultValue(Class businessObjectClass, String attributeName);


    /**
     * returns Class used to generate a lookup field default value
     */
    public Class getLookupFieldDefaultValueFinderClass(Class businessObjectClass, String attributeName);


    /**
     * indicates whether a field is required for a lookup
     */
    public Boolean getLookupAttributeRequired(Class businessObjectClass, String attributeName);


    /**
     * the list defined as inquiry fields for the business object and inquiry section.
     */
    public List getInquiryFieldNames(Class businessObjectClass, String sectionTitle);

    
    /**
     * the list defined as inquiry sections for the business object.
     */
    public List getInquirySections(Class businessObjectClass);

    
    /**
     * the text to be displayed for the title of business object inquiry.
     */
    public String getInquiryTitle(Class businessObjectClass);


    /**
     * the class to be used for building inquiry pages.
     */
    public Class getInquirableClass(Class businessObjectClass);
    
    /**
     * the text to be displayed for the title of business object maintenance document.
     */
    public String getMaintainableLabel(Class businessObjectClass);


    /**
     * the attribute to be associated with for object level markings
     */
    public String getTitleAttribute(Class businessObjectClass);


    /**
     * the Lookupable implementation id for the associated Lookup, if one has been specified
     */
    public String getLookupableID(Class businessObjectClass);


    /**
     * This method takes any business object and recursively walks through it checking to see if any attributes need to be forced to
     * uppercase based on settings in the data dictionary
     * 
     * @param bo
     */
    public void performForceUppercase(BusinessObject bo);


    public Boolean areNotesSupported(Class businessObjectClass);

}