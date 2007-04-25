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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.BeanPropertyComparator;

/**
 * This class...
 */
public class KualiLookupableHelperServiceImpl extends AbstractLookupableHelperServiceImpl {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiLookupableHelperServiceImpl.class);
    private boolean searchUsingOnlyPrimaryKeyValues = false;


    /**
     * Uses Lookup Service to provide a basic search.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     *
     * @return List found business objects
     * @see org.kuali.core.lookup.LookupableHelperService#getSearchResults(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {        
        return getSearchResultsHelper(LookupUtils.forceUppercase(getBusinessObjectClass(), fieldValues), false);
    }
    

    /**
     * Uses Lookup Service to provide a basic unbounded search.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     * 
     * @return List found business objects
     * @see org.kuali.core.lookup.LookupableHelperService#getSearchResultsUnbounded(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return getSearchResultsHelper(LookupUtils.forceUppercase(getBusinessObjectClass(), fieldValues), true);
    }

    /**
     * 
     * This method does the actual search, with the parameters specified, and returns the result.
     * 
     * NOTE that it will not do any upper-casing based on the DD forceUppercase. That is handled through an external call to
     * LookupUtils.forceUppercase().
     * 
     * @param fieldValues A Map of the fieldNames and fieldValues to be searched on.
     * @param unbounded Whether the results should be bounded or not to a certain max size.
     * @return A List of search results.
     * 
     */
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        // remove hidden fields
        LookupUtils.removeHiddenCriteriaFields( getBusinessObjectClass(), fieldValues );

        searchUsingOnlyPrimaryKeyValues = getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getBusinessObjectClass(), fieldValues);

        setBackLocation(fieldValues.get(Constants.BACK_LOCATION));
        setDocFormKey(fieldValues.get(Constants.DOC_FORM_KEY));
        setReferencesToRefresh(fieldValues.get(Constants.REFERENCES_TO_REFRESH));
        List searchResults;
        if (UniversalUser.class.equals(getBusinessObjectClass())) {
            searchResults = (List) getUniversalUserService().findUniversalUsers(fieldValues);
        }
        else if (getUniversalUserService().hasUniversalUserProperty(getBusinessObjectClass(), fieldValues)) {
            // TODO WARNING: this does not support nested joins, because i don't have a test case
            searchResults = (List) getUniversalUserService().findWithUniversalUserJoin(getBusinessObjectClass(), fieldValues, unbounded);
        }
        else {
            searchResults = (List) getLookupService().findCollectionBySearchHelper(getBusinessObjectClass(), fieldValues, unbounded);
        }
        // sort list if default sort column given
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(searchResults, new BeanPropertyComparator(getDefaultSortColumns(), true));
        }
        return searchResults;
    }


    /**
     * @see LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     */
    @Override
    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        return searchUsingOnlyPrimaryKeyValues;
}


    /**
     * Returns a comma delimited list of primary key field labels, to be used on the UI to tell the user which fields were used to search
     * 
     * These labels are generated from the DD definitions for the lookup fields
     * 
     * @return a comma separated list of field attribute names.  If no fields found, returns "N/A"
     * @see LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     * @see LookupableHelperService#getPrimaryKeyFieldLabels()
     */
    @Override
    public String getPrimaryKeyFieldLabels() {
        StringBuilder buf = new StringBuilder();
        List primaryKeyFieldNames = getPersistenceStructureService().getPrimaryKeys(getBusinessObjectClass());
        Iterator pkIter = primaryKeyFieldNames.iterator();
        while (pkIter.hasNext()) {
            String pkFieldName = (String) pkIter.next();
            buf.append(getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), pkFieldName));
            if (pkIter.hasNext()) {
                buf.append(", ");
            }
        }
        return buf.length() == 0 ? Constants.NOT_AVAILABLE_STRING : buf.toString();
    }
    
    
}
