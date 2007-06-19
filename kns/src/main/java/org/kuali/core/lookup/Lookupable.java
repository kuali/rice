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
package org.kuali.core.lookup;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.ResultRow;

/**
 * This class defines an interface for lookupables.
 * 
 * They should act as facades for LookupableHelperServices and also expose bean handlers
 * (getCreateNewUrl, getHtmlMenuBar, getTitle, getRows, getExtraButton{Source,Params}, getLookupInstructions)
 * 
 */
public interface Lookupable extends Serializable {

    /**
     * Initializes the lookup with a businss object class
     * 
     * It is required that implementations of this method will initialize the 
     * search area used by the UI to provide the search form.  In particular,
     * it will ensure that getRows() will return valid results
     * 
     * @param boClass
     */
    public void setBusinessObjectClass(Class businessObjectClass);

    /**
     * 
     * @return Returns the businessObjectClass this lookupable is representing
     * 
     */
    public Class getBusinessObjectClass();

    /**
     * @return the html to be displayed as a menu bar
     */
    public String getHtmlMenuBar();

    /**
     * @return List of Row objects used to render the search area
     */
    public List getRows();

    /**
     * @return String displayed as title for the lookup
     */
    public String getTitle();

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
     * 
     * This method performs the lookup and returns a collection of lookup items
     * @param lookupForm
     * @param resultTable
     * @param bounded
     * @return results of lookup
     */
    public Collection performLookup(LookupForm lookupForm, List<ResultRow> resultTable, boolean bounded);
    
    /**
     * Performs a search and returns result list.
     * 
     * @param fieldValues - Map of property/value pairs
     * @return List of business objects found by the search
     * @throws Exception
     */
    public List<BusinessObject> getSearchResults(Map<String, String> fieldValues);

    /**
     * Similar to getSearchResults, but the number of returned rows is not bounded
     * 
     * @param fieldValues
     * @return
     */
    public List<BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues);

    /**
     * @return String providing instructions for using the lookup
     */
    public String getLookupInstructions();

    /**
     * @return String providing source for optional extra button
     */
    public String getExtraButtonSource();

    /**
     * @return String providing return parameters for optional extra button
     */
    public String getExtraButtonParams();

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
     * Builds the Url for a maintenance new document for the lookup business object class
     * @param businessObject
     * @return String rendered on Lookup screen for maintenance new document
     */
    public String getCreateNewUrl();

    /**
     * Sets the requested fields conversions in the lookupable
     * 
     * @param fieldConversions
     */
    public void setFieldConversions(Map fieldConversions);
    
    /**
     * Sets the requested read only fields list in the lookupable
     * 
     * @param readOnlyFieldsList
     */
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList);
    
    /**
     * Sets the helper service for instance
     * @param helper the helper service 
     */
    public void setLookupableHelperService(LookupableHelperService helper);
    
    /**
     * Returns the LookupableHelperService designated to help this lookup
     * @return
     */
    public LookupableHelperService getLookupableHelperService();
    
    /**
     * Returns whether this search was performed using the values of the primary keys only
     * 
     * @return
     */
    public boolean isSearchUsingOnlyPrimaryKeyValues();
    
    /**
     * Returns a comma delimited list of primary key field labels, as defined in the DD
     * 
     * @return
     */
    public String getPrimaryKeyFieldLabels();
    
    /**
     * This method returns a list of the default columns used to sort the result set.  For multiple value lookups,
     * this method does not change when different columns are sorted.
     * 
     * @return
     */
    public List getDefaultSortColumns();
}