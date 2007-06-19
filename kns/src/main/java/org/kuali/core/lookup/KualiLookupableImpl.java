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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.ResultRow;

/**
 * Kuali lookup implementation. Implements methods necessary to render the lookup and provides search and return methods.
 * 
 * 
 */
public class KualiLookupableImpl implements Lookupable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiLookupableImpl.class);
    private static final String[] IGNORE_LIST = { Constants.DOC_FORM_KEY, Constants.BACK_LOCATION };

    private Class businessObjectClass;
    private LookupableHelperService lookupableHelperService;

    /**
     * Default constructor initializes services from spring
     */
    public KualiLookupableImpl() {
    }


    /**
     * Sets the business object class for the lookup instance, then rows can be set for search render.
     * 
     * @param boClass Class for the lookup business object
     */
    public void setBusinessObjectClass(Class boClass) {
        if (boClass == null) {
            throw new RuntimeException("Business object class is null.");
        }

        this.businessObjectClass = boClass;
        
        // next line initializes the helper to return correct values for getRow();
        getLookupableHelperService().setBusinessObjectClass(boClass);
    }

    /**
     * Constructs the list of columns for the search results. All properties for the column objects come from the DataDictionary.
     */
    public List getColumns() {
        return getLookupableHelperService().getColumns();
    }

    /**
     * Checks that any required search fields have value.
     * 
     * @see org.kuali.core.lookup.Lookupable#validateSearchParameters(java.util.Map)
     */
    public void validateSearchParameters(Map fieldValues) {
        getLookupableHelperService().validateSearchParameters(fieldValues);
    }

    /**
     * Uses Lookup Service to provide a basic unbounded search.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     *
     * @return List found business objects
     */
    public List<BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return getLookupableHelperService().getSearchResultsUnbounded(fieldValues);
    }

    /**
     * Uses Lookup Service to provide a basic search.
     * 
     * @param fieldValues - Map containing prop name keys and search values
     * 
     * @return List found business objects
     */
    public List<BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return getLookupableHelperService().getSearchResults(fieldValues);
    }

    /**
     * @return the return url for each result row.
     */
    public String getReturnUrl(BusinessObject bo, Map fieldConversions, String lookupImpl) {
        return getLookupableHelperService().getReturnUrl(bo, fieldConversions, lookupImpl);
    }

    /**
     * Build a maintenanace url.
     * 
     * @param bo - business object representing the record for maint.
     * @param methodToCall - maintenance action
     * @return
     */
    protected String getMaintenanceUrl(BusinessObject bo, String methodToCall) {
        //TODO: delete me
        return getLookupableHelperService().getMaintenanceUrl(bo, methodToCall);
    }


    /**
     * @see org.kuali.core.lookup.Lookupable#getCreateNewUrl()
     */
    public String getCreateNewUrl() {
        String url = "";

        if (getLookupableHelperService().allowsMaintenanceNewOrCopyAction()) {
            Properties parameters = new Properties();
            parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, Constants.MAINTENANCE_NEW_METHOD_TO_CALL);
            parameters.put(Constants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, this.businessObjectClass.getName());

            url = UrlFactory.parameterizeUrl(Constants.MAINTENANCE_ACTION, parameters);
            url = "<a href=\"" + url + "\"><img src=\"images/tinybutton-createnew.gif\" alt=\"create new\" width=\"70\" height=\"15\"/></a>";
        }

        return url;
    }

  
    /**
     * @see org.kuali.core.lookup.Lookupable#getHtmlMenuBar()
     */
    public String getHtmlMenuBar() {
        return getBusinessObjectDictionaryService().getLookupMenuBar(getBusinessObjectClass());
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getRows()
     */
    public List getRows() {
        return getLookupableHelperService().getRows();
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getTitle()
     */
    public String getTitle() {
        return getBusinessObjectDictionaryService().getLookupTitle(getBusinessObjectClass());
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getReturnLocation()
     */
    public String getReturnLocation() {
        return getLookupableHelperService().getReturnLocation();
    }

    /**
     * @return Returns the businessObjectClass.
     */
    public Class getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    public List getReturnKeys() {
        return getLookupableHelperService().getReturnKeys();
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getLookupInstructions()
     */
    public String getLookupInstructions() {
        return getBusinessObjectDictionaryService().getLookupInstructions(getBusinessObjectClass());
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getExtraButtonSource()
     */
    public String getExtraButtonSource() {
        return getBusinessObjectDictionaryService().getExtraButtonSource(getBusinessObjectClass());
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getExtraButtonParams()
     */
    public String getExtraButtonParams() {
        return getBusinessObjectDictionaryService().getExtraButtonParams(getBusinessObjectClass());
    }

    /**
     * @return property names that will be used to sort on by default
     */
    public List getDefaultSortColumns() {
        return getLookupableHelperService().getDefaultSortColumns();
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#checkForAdditionalFields(java.util.Map)
     */
    public boolean checkForAdditionalFields(Map fieldValues) {
        return getLookupableHelperService().checkForAdditionalFields(fieldValues);
    }

    /**
     * @return Returns the backLocation.
     */
    public String getBackLocation() {
        return getLookupableHelperService().getBackLocation();
    }

    /**
     * @param backLocation The backLocation to set.
     */
    public void setBackLocation(String backLocation) {
        getLookupableHelperService().setBackLocation(backLocation);
    }

    /**
     * @return Returns the docFormKey.
     */
    public String getDocFormKey() {
        return getLookupableHelperService().getDocFormKey();
    }

    /**
     * // this method is public because unit tests depend upon it
     * @param docFormKey The docFormKey to set.
     */
    public void setDocFormKey(String docFormKey) {
        getLookupableHelperService().setDocFormKey(docFormKey);
    }

    /**
     * @return Returns the businessObjectDictionaryService.
     */
    protected BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return getLookupableHelperService().getBusinessObjectDictionaryService();
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#setFieldConversions(java.util.Map)
     */
    public void setFieldConversions(Map fieldConversions) {
        getLookupableHelperService().setFieldConversions(fieldConversions);
    }

    /**
     * @return Returns the dataDictionaryService.
     */
    protected DataDictionaryService getDataDictionaryService() {
        return getLookupableHelperService().getDataDictionaryService();
    }


    /**
     * Sets the readOnlyFieldsList attribute value.
     * 
     * @param readOnlyFieldsList The readOnlyFieldsList to set.
     */
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList) {
        getLookupableHelperService().setReadOnlyFieldsList(readOnlyFieldsList);
    }


    public LookupableHelperService getLookupableHelperService() {
        return lookupableHelperService;
    }


    /**
     * Sets the lookupableHelperService attribute value.
     * @param lookupableHelperService The lookupableHelperService to set.
     */
    public void setLookupableHelperService(LookupableHelperService lookupableHelperService) {
        this.lookupableHelperService = lookupableHelperService;
    }

    /**
     * Performs a lookup that can only return one row.
     * @see org.kuali.core.lookup.Lookupable#performLookup(org.kuali.core.web.struts.form.LookupForm, java.util.List, boolean)
     */
    public Collection performLookup(LookupForm lookupForm, List<ResultRow> resultTable, boolean bounded) {
        return getLookupableHelperService().performLookup(lookupForm, resultTable, bounded);
    }


    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        return getLookupableHelperService().isSearchUsingOnlyPrimaryKeyValues();
    }


    public String getPrimaryKeyFieldLabels() {
        return getLookupableHelperService().getPrimaryKeyFieldLabels();
    }
}