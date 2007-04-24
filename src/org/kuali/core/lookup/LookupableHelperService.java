/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.lookup;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Row;

public interface LookupableHelperService extends Serializable{
    /**
     * Initializes the lookup with a businss object class
     * 
     * @param businessObjectClass
     */
    public void setBusinessObjectClass(Class businessObjectClass);

    /**
     * 
     * @return Returns the businessObjectClass this lookupable is representing
     * 
     */
    public Class getBusinessObjectClass();

    /**
     * @return String url for the location to return to after the lookup
     */
    public String getReturnLocation();

    /**
     * @return List of Column objects used to render the result table
     */
    public List getColumns();

    /**
     * Validates the values filled in as search criteria, also checks for required field values.
     * 
     * @param fieldValues - Map of property/value pairs
     */
    public void validateSearchParameters(Map fieldValues);

    /**
     * Performs a search and returns result list.
     * 
     * @param fieldValues - Map of property/value pairs
     * @return List of business objects found by the search
     * @throws Exception
     */
    public List getSearchResults(Map<String, String> fieldValues);

    /**
     * Similar to getSearchResults, but the number of returned rows is not bounded
     * 
     * @param fieldValues
     * @return
     */
    public List getSearchResultsUnbounded(Map<String, String> fieldValues);

    /**
     * Determines if there should be more search fields rendered based on already entered search criteria.
     * 
     * @param fieldValues - Map of property/value pairs
     * @return boolean
     */
    public boolean checkForAdditionalFields(Map fieldValues);

    /**
     * Builds the return value url.
     * 
     * @param businessObject - Instance of a business object containing the return values
     * @param fieldConversions - Map of conversions mapping bo names to caller field names.
     * @param lookupImpl - Current lookup impl name
     * @return String url called when selecting a row from the result set
     */
    public String getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl);

    /**
     * Builds string of action urls that can take place for a result row
     * 
     * @param businessObject - Instance of a business object containing the return values
     * @return String rendered in actions column of result set
     */
    public String getActionUrls(BusinessObject businessObject);

    /**
     * Builds string an inquiry url for drill down on a result field
     * 
     * @param businessObject - Instance of a business object containing the return values
     * @param propertyName - Name of the property in the business object
     * @return String url called on selection of the result field
     */
    public String getInquiryUrl(BusinessObject businessObject, String propertyName);
    
    /**
     * Sets the requested fields conversions in the lookupable
     * 
     * @param fieldConversions
     */
    public void setFieldConversions(Map fieldConversions);
    
    /**
     * Gets the readOnlyFieldsList attribute. 
     * @return Returns the readOnlyFieldsList.
     */
    public List<String> getReadOnlyFieldsList();
    
    /**
     * Sets the requested read only fields list in the lookupable
     * 
     * @param readOnlyFieldsList
     */
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList);
    
    /**
     * This method is public because some unit tests depend on it.
     * 
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    public List getReturnKeys();
    
    public String getDocFormKey();
    
    public void setDocFormKey(String docFormKey);
    
    public String getMaintenanceUrl(BusinessObject businessObject, String methodToCall);
    
    /**
     * Determines if underlying lookup bo has associated maintenance document that allows new or copy maintenance actions.
     * 
     * @return true if bo has maint doc that allows new or copy actions
     */
    public boolean allowsMaintenanceNewOrCopyAction();
    
    /**
     * Returns a list of Row objects to be used to generate the search query screen
     * 
     * Generally, setBusinessObjectClass needs to be called with a non-null value for proper operation
     * @return
     */
    public List<Row> getRows();
    
    /**
     * This method returns the DataDictionaryService used to initialize this helper service and is used by Lookupable implementations to 
     * retrieve the proper service.
     * 
     * @return
     */
    public DataDictionaryService getDataDictionaryService();
    
    /**
     * This method returns the BusinessObjectDictionaryService used to initialize this helper service and is used by Lookupable implementations to 
     * retrieve the proper service.
     * 
     * @return
     */
    public BusinessObjectDictionaryService getBusinessObjectDictionaryService();
    
    public void setBackLocation(String backLocation);
    
    public String getBackLocation();
    
    /**
     * 
     * This method performs the lookup and returns a collection of BO items
     * @param lookupForm
     * @param resultTable
     * @param bounded
     * @return the list of result BOs, possibly bounded
     */
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded);
    
    /**
     * This method returns a list of the default columns used to sort the result set.  For multiple value lookups,
     * this method does not change when different columns are sorted.
     * 
     * @return
     */
    public List getDefaultSortColumns();
    
    /**
     * This method returns whether the previously executed getSearchResults used the primary key values to search, ignoring all non key values
     * 
     * @return
     * @see LookupableHelperService#getPrimaryKeyFieldLabels()
     */
    public boolean isSearchUsingOnlyPrimaryKeyValues();
    
    /**
     * Returns a comma delimited list of primary key field labels, to be used on the UI to tell the user which fields were used to search
     * 
     * @return
     * @see LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     */
    public String getPrimaryKeyFieldLabels();
}
