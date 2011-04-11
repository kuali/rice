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
import java.util.Set;

import org.kuali.rice.kns.uif.core.Component;
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
	 * @param criteriaComponents - Components that are a part of the criteria for the lookup
	 * @param fieldValues - Map of property/value pairs
	 */
	public void validateSearchParameters(List<? extends Component> criteriaComponents, Map<String, String> fieldValues);

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

	public void setDataObjectClass(Class<?> dataObjectClass);

	public void setFieldConversions(Map<String, String> fieldConversions);

	public void setDocNum(String docNum);

    public boolean isHideReturnLink();

    public void setHideReturnLink(boolean hideReturnLink);

    public void setSuppressActions(boolean suppressActions);

    public boolean isSuppressActions();

    public void setShowMaintenanceLinks(boolean showMaintenanceLinks);

   	public boolean isShowMaintenanceLinks();

    public String getReturnLocation();

	public String getReturnFormKey();

	public boolean isAtLeastOneRowReturnable();

	public boolean isAtLeastOneRowHasActions();

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
	 * Build the HTML string text needed to return the object represented by the current row in the list to the source object that called the Lookup.
	 * 
	 * @param generatedField
	 * @return
	 */
	public String getReturnUrlForResults(GeneratedField generatedField);

	/**
	 * @return Set of property names that should be set as read only based on the current search
	 *         contents
	 */
    public Set<String> getConditionallyReadOnlyPropertyNames();

	/**
	 * @return Set of property names that should be set as required based on the current search
	 *         contents
	 */
	public Set<String> getConditionallyRequiredPropertyNames();

	/**
	 * @return Set of property names that should be set as hidden based on the current search
	 *         contents
	 */
	public Set<String> getConditionallyHiddenPropertyNames();

/*************************************************************************************************/

//    /**
//     * Determines if there should be more search fields rendered based on already entered search criteria.
//     *
//     * @param fieldValues - Map of property/value pairs
//     * @return boolean
//     */
//    public boolean checkForAdditionalFields(Map fieldValues);
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

}
