/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.kns.uif.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kns.uif.field.GeneratedField;

/**
 * A ViewHelperService that is specifically used for Lookups
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface LookupViewHelperService extends ViewHelperService {

	/**
	 * This method ...
	 * 
	 * @param criteriaFieldsForLookup
	 * @param bounded
	 * @return the list of result BOs, possibly bounded
	 */
	public Collection<?> performSearch(Map<String, String> criteriaFieldsForLookup, boolean bounded);

	/**
	 * 
	 * This method allows for customization of the lookup clear
	 * 
	 */
	public Map<String, String> performClear(Map<String, String> fieldsForLookup);

	/**
	 * Validates the values filled in as search criteria, also checks for required field values.
	 * 
	 * @param fieldValues - Map of property/value pairs
	 */
	public void validateSearchParameters(Map<String, String> fieldValues);

	/**
	 * Gets the readOnlyFieldsList attribute.
	 * 
	 * @return Returns the readOnlyFieldsList.
	 */
	public List<String> getReadOnlyFieldsList();

	/**
	 * Sets the readOnlyFieldsList attribute value.
	 * 
	 * @param readOnlyFieldsList The readOnlyFieldsList to set.
	 */
	public void setReadOnlyFieldsList(List<String> readOnlyFieldsList);

	public void setDataObjectClass(Class dataObjectClass);

	public void setFieldConversions(Map<String, String> fieldConversions);

    /**
     * Builds the HTML string text needed to create a new document for the class represented by this lookup view. By default this includes verifying that the current user has permission to 
     * 
     * @param generatedField - the field that will be used to display the String text
     * @return String representing both the url and the HTML text required for the page
     */
    public String getCreateNewUrl(GeneratedField generatedField);

	/**
	 * Builds the HTML string text needed to perform maintenance actions for the data object represented by the current row in the list. By default this includes checking permissions for which
	 * maintenance actions the user is allowed to perform.
	 * 
	 * @param generatedField
	 *            - the field that will be used to display the String text
	 * @return String representing the HTML text required for any maintenance actions the user is allowed to perform
	 */
	public String getActionUrlsFromField(GeneratedField generatedField);

    /**
     * Initializes the lookup with the given Map of parameters.
     *
     * @param parameters
     */
    public void setParameters(Map parameters);

    /**
     * @return Returns the parameters passed to this lookup
     */
    public Map getParameters();

/*************************************************************************************************/

//    /**
//     * @return String url for the location to return to after the lookup
//     */
//    public String getReturnLocation();
//
//    /**
//     * Determines if there should be more search fields rendered based on already entered search criteria.
//     *
//     * @param fieldValues - Map of property/value pairs
//     * @return boolean
//     */
//    public boolean checkForAdditionalFields(Map fieldValues);
//
//    /**
//     * Builds the return value url.
//     *
//     * @param businessObject - Instance of a business object containing the return values
//     * @param fieldConversions - Map of conversions mapping bo names to caller field names.
//     * @param lookupImpl - Current lookup impl name
//     * @return String url called when selecting a row from the result set
//     */
//    public HtmlData getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl, BusinessObjectRestrictions businessObjectRestrictions);
//
//    /**
//     * Returns whether this search was performed using the values of the primary keys only
//     *
//     * @return
//     */
//    public boolean isSearchUsingOnlyPrimaryKeyValues();
//
//    /**
//     * Returns a comma delimited list of primary key field labels, as defined in the DD
//     *
//     * @return
//     */
//    public String getPrimaryKeyFieldLabels();
//
//    /**
//     *
//     * This method checks whether the header non maint actions should be shown
//     *
//     */
//    public boolean shouldDisplayHeaderNonMaintActions();
//
//    /**
//     *
//     * This method checks whether the criteria should be shown
//     *
//     */
//    public boolean shouldDisplayLookupCriteria();
//
//    /**
//     *
//     * This method is called from a custom action button or script
//     *
//     */
//    // TODO delyea - should be on custom Controller?
//    public boolean performCustomAction(boolean ignoreErrors);
//
//    public void applyFieldAuthorizationsFromNestedLookups(Field field);
//    
//    /**
//     * Performs conditional logic (based on current search values or other parameters) to
//     * override field hidden, read-only, and required attributes previously set.
//     */
//    public void applyConditionalLogicForFieldDisplay();

}
