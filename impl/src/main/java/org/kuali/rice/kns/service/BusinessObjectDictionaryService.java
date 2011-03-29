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

import java.util.List;

import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.FieldDefinition;
import org.kuali.rice.kns.inquiry.InquiryAuthorizer;
import org.kuali.rice.kns.inquiry.InquiryPresentationController;
import org.kuali.rice.kns.lookup.valueFinder.ValueFinder;


/**
 * This interface defines the API for the interacting with the data dictionary.
 */
public interface BusinessObjectDictionaryService {
	@Deprecated
	public <T extends BusinessObject> InquiryPresentationController getInquiryPresentationController(Class<T> businessObjectClass);
	
	@Deprecated
	public <T extends BusinessObject> InquiryAuthorizer getInquiryAuthorizer(Class<T> businessObjectClass);

    /**
     * the list of business object class names being maintained
     */
    public List getBusinessObjectClassnames();


    /**
     * indicates whether business object has lookup defined
     */
    @Deprecated
    public Boolean isLookupable(Class businessObjectClass);


    /**
     * indicates whether business object has inquiry defined
     */
    @Deprecated
    public Boolean isInquirable(Class businessObjectClass);


    /**
     * indicates whether business object has maintainable defined
     */
    @Deprecated
    public Boolean isMaintainable(Class businessObjectClass);


    /**
     * indicates whether business object has an exporter defined
     */
    public Boolean isExportable(Class businessObjectClass);
    
    /**
     * the list defined as lookup fields for the business object.
     */
    @Deprecated
    public List getLookupFieldNames(Class businessObjectClass);


    /**
     * the text to be displayed for the title of business object lookup.
     */
    @Deprecated
    public String getLookupTitle(Class businessObjectClass);


    /**
     * menu bar html defined for the business object.
     */
    @Deprecated
    public String getLookupMenuBar(Class businessObjectClass);
    

    /**
     * source for optional extra button
     */
    @Deprecated
    public String getExtraButtonSource(Class businessObjectClass);


    /**
     * return parameters for optional extra button
     */
    @Deprecated
    public String getExtraButtonParams(Class businessObjectClass);


    /**
     * the property names of the bo used to sort the initial result set
     */
    @Deprecated
    public List getLookupDefaultSortFieldNames(Class businessObjectClass);


    /**
     * the list defined as lookup result fields for the business object.
     */
    @Deprecated
    public List<String> getLookupResultFieldNames(Class businessObjectClass);

    /**
     * This method returns the maximum display length of the value of the given field in the lookup results.  While the actual value may
     * be longer than the specified length, this value specifies the maximum length substring that should be displayed.
     * It is up to the UI layer to intepret the results of the field
     *
     * @param businessObjectClass
     * @param resultFieldName
     * @return the maximum length of the lookup results field that should be displayed.  Returns null
     * if this value has not been defined.  If negative, denotes that the is maximum length is unlimited.
     */
    @Deprecated
    public Integer getLookupResultFieldMaxLength(Class businessObjectClass, String resultFieldName);

    /**
     * returns boolean indicating whether lookup result field marked to force an inquiry
     */
    @Deprecated
    public Boolean forceLookupResultFieldInquiry(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether lookup result field marked to not do an inquiry
     */
    @Deprecated
    public Boolean noLookupResultFieldInquiry(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether lookup search field marked to force a lookup
     */
    @Deprecated
    public Boolean forceLookupFieldLookup(Class businessObjectClass, String attributeName);

    /**
     * returns boolean indicating whether lookup search field marked to force an inquiry
     */
    @Deprecated
    public Boolean forceInquiryFieldLookup(Class businessObjectClass, String attributeName);
    
    /**
     * returns boolean indicating whether lookup search field marked to not do a lookup
     */
    @Deprecated
    public Boolean noLookupFieldLookup(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether lookup search field marked to not do a direct inquiry
     */
    @Deprecated
    public Boolean noDirectInquiryFieldLookup(Class businessObjectClass, String attributeName);

    /**
     * returns boolean indicating whether inquiry result field marked to force an inquiry
     */
    @Deprecated
    public Boolean forceInquiryFieldInquiry(Class businessObjectClass, String attributeName);


    /**
     * returns boolean indicating whether inquiry result field marked to not do an inquiry
     */
    @Deprecated
    public Boolean noInquiryFieldInquiry(Class businessObjectClass, String attributeName);

    /**
     * returns boolean indicating whether lookup result field to use shortLabel
     */
    @Deprecated
    public Boolean getLookupResultFieldUseShortLabel(Class businessObjectClass, String attributeName);
    
    /**
     * returns boolean indicating whether lookup result field should be totaled
     */
    @Deprecated
    public Boolean getLookupResultFieldTotal(Class businessObjectClass, String attributeName);

    /**
     * returns String indicating the default search value for the lookup field
     */
    @Deprecated
    public String getLookupFieldDefaultValue(Class businessObjectClass, String attributeName);


    /**
     * returns Class used to generate a lookup field default value
     */
    @Deprecated
    public Class getLookupFieldDefaultValueFinderClass(Class businessObjectClass, String attributeName);

    /**
     * See {@link FieldDefinition#getQuickfinderParameterString()}.
     * returns String indicating the default search value for the lookup field.
     */
    @Deprecated
    public String getLookupFieldQuickfinderParameterString(Class businessObjectClass, String attributeName);


    /**
     * returns Class used to generate quickfinder lookup field default values.
     * See {@link FieldDefinition#getQuickfinderParameterStringBuilderClass()}.
     */
    @Deprecated
    public Class<? extends ValueFinder> getLookupFieldQuickfinderParameterStringBuilderClass(Class businessObjectClass, String attributeName);


    /**
     * returns String indicating the result set limit for the lookup
     */
    @Deprecated
    public Integer getLookupResultSetLimit(Class businessObjectClass);
    
    /**
     * @return number of search columns configured for the lookup associated with the class
     */
    @Deprecated
    public Integer getLookupNumberOfColumns(Class businessObjectClass);

    /**
     * returns String indicating the location of the lookup icon.
     */
    @Deprecated
    public String getSearchIconOverride(Class businessObjectClass);

    /**
     * indicates whether a field is required for a lookup
     */
    @Deprecated
    public Boolean getLookupAttributeRequired(Class businessObjectClass, String attributeName);
    
    /**
     * indicates whether a field is read only for a lookup
     */
    @Deprecated
    public Boolean getLookupAttributeReadOnly(Class businessObjectClass, String attributeName);


    /**
     * the list defined as inquiry fields for the business object and inquiry section.
     */
    @Deprecated
    public List getInquiryFieldNames(Class businessObjectClass, String sectionTitle);


    /**
     * the list defined as inquiry sections for the business object.
     */
    @Deprecated
    public List getInquirySections(Class businessObjectClass);


    /**
     * the text to be displayed for the title of business object inquiry.
     */
    @Deprecated
    public String getInquiryTitle(Class businessObjectClass);


    /**
     * the class to be used for building inquiry pages.
     */
    @Deprecated
    public Class getInquirableClass(Class businessObjectClass);

    /**
     * the text to be displayed for the title of business object maintenance document.
     */
    @Deprecated
    public String getMaintainableLabel(Class businessObjectClass);


    /**
     * the attribute to be associated with for object level markings
     */
    public String getTitleAttribute(Class businessObjectClass);


    /**
     * the Lookupable implementation id for the associated Lookup, if one has been specified
     */
    @Deprecated
    public String getLookupableID(Class businessObjectClass);


    /**
     * This method takes any business object and recursively walks through it checking to see if any attributes need to be forced to
     * uppercase based on settings in the data dictionary
     *
     * @param bo
     */
    public void performForceUppercase(BusinessObject bo);


    public Boolean areNotesSupported(Class businessObjectClass);

    /**
     * returns whether on a lookup, field/attribute values with wildcards and operators should treat them as literal characters
     * 
     * @param businessObjectClass
     * @param attributeName
     * @return
     */
    @Deprecated
    public boolean isLookupFieldTreatWildcardsAndOperatorsAsLiteral(Class businessObjectClass, String attributeName);
    
    /**
     * returns String giving alternate display attribute name for lookup field if configured, or null
     */
    @Deprecated
    public String getLookupFieldAlternateDisplayAttributeName(Class businessObjectClass, String attributeName);

    /**
     * returns String giving alternate display attribute name for inquiry field if configured, or null
     */
    @Deprecated
    public String getInquiryFieldAlternateDisplayAttributeName(Class businessObjectClass, String attributeName);
    
    /**
     * returns String giving additional display attribute name for lookup field if configured, or null
     */
    @Deprecated
    public String getLookupFieldAdditionalDisplayAttributeName(Class businessObjectClass, String attributeName);

    /**
     * returns String giving additional display attribute name for inquiry field if configured, or null
     */
    @Deprecated
    public String getInquiryFieldAdditionalDisplayAttributeName(Class businessObjectClass, String attributeName);
    
     /**
     * @param businessObjectClass - business object class for lookup definition
     * @return Boolean indicating whether translating of codes is configured to true in lookup definition  
     */
    @Deprecated
    public Boolean tranlateCodesInLookup(Class businessObjectClass);

    /**
     * @param businessObjectClass - business object class for inquiry definition
     * @return Boolean indicating whether translating of codes is configured to true in inquiry definition  
     */
    @Deprecated
    public Boolean tranlateCodesInInquiry(Class businessObjectClass);
    
    /**
     * Indicates whether a lookup field has been configured to trigger on value change
     * 
     * @param businessObjectClass - Class for business object to lookup 
     * @param attributeName - name of attribute in the business object to check configuration for
     * @return true if field is configured to trigger on value change, false if not
     */
    @Deprecated
    public boolean isLookupFieldTriggerOnChange(Class businessObjectClass, String attributeName);
    
	/**
	 * Indicates whether the search and clear buttons should be disabled based on the data
	 * dictionary configuration
	 * 
	 * @param businessObjectClass
	 *            - business object class for lookup definition
	 * @return Boolean indicating whether disable search buttons is configured to true in lookup
	 *         definition
	 */
    @Deprecated
	public boolean disableSearchButtonsInLookup(Class businessObjectClass);
	
	/**
	 * Returns the list of attributes that should be used for grouping when determing the current
	 * status of a business object that implements InactivateableFromTo
	 * 
	 * @param businessObjectClass - business object class to get configured list for
	 * @return List of string attribute names that gives the group by list
	 */
	public List<String> getGroupByAttributesForEffectiveDating(Class businessObjectClass);
}
