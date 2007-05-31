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

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.datadictionary.mask.Mask;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.BusinessObjectMetaDataService;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.EncryptionService;
import org.kuali.core.service.LookupService;
import org.kuali.core.service.MaintenanceDocumentDictionaryService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.service.SequenceAccessorService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.comparator.CellComparatorHelper;
import org.kuali.core.web.format.BooleanFormatter;
import org.kuali.core.web.format.Formatter;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.ui.Column;
import org.kuali.core.web.ui.ResultRow;
import org.kuali.core.web.ui.Row;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class declares many of the common spring injected properties, the get/set-ers for them,
 * and some common util methods that require the injected services
 */
public abstract class AbstractLookupableHelperServiceImpl implements LookupableHelperService {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractLookupableHelperServiceImpl.class);
    
    private Class businessObjectClass;
    private BusinessObjectDictionaryService businessObjectDictionaryService;
    private BusinessObjectMetaDataService businessObjectMetaDataService;
    private DataDictionaryService dataDictionaryService;
    private PersistenceStructureService persistenceStructureService;
    private EncryptionService encryptionService;
    private List<String> readOnlyFieldsList;
    private String backLocation;
    private String docFormKey;
    private Map fieldConversions;
    private LookupService lookupService;
    private UniversalUserService universalUserService;
    private List<Row> rows;
    private String referencesToRefresh;
    private SequenceAccessorService sequenceAccessorService;
    private BusinessObjectService businessObjectService;
    private LookupResultsService lookupResultsService;
    
    
    public AbstractLookupableHelperServiceImpl() {
        rows = null;
    }
    
    /**
     * This implementation always returns false.
     * 
     * @see org.kuali.core.lookup.LookupableHelperService#checkForAdditionalFields(java.util.Map)
     */
    public boolean checkForAdditionalFields(Map fieldValues) {
        return false;
    }
    
    /**
     * Build a maintenanace url.
     * 
     * @param bo - business object representing the record for maint.
     * @param methodToCall - maintenance action
     * @return
     */
    public String getMaintenanceUrl(BusinessObject businessObject, String methodToCall) {
        // TODO: considering making visibility "protected"
        Properties parameters = new Properties();
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        parameters.put(Constants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, this.businessObjectClass.getName());

        String encryptedList = "";

        List pkNames = getPersistenceStructureService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        for (Iterator iter = pkNames.iterator(); iter.hasNext();) {
            String fieldNm = (String) iter.next();

            Object fieldVal = ObjectUtils.getPropertyValue(businessObject, fieldNm);
            if (fieldVal == null) {
                fieldVal = Constants.EMPTY_STRING;
            }
            if (fieldVal instanceof java.sql.Date) {
                String formattedString = "";
                if (Formatter.findFormatter(fieldVal.getClass()) != null) {
                    Formatter formatter = Formatter.getFormatter(fieldVal.getClass());
                    formattedString = (String) formatter.format(fieldVal);
                    fieldVal = formattedString;
                }
            }


            // Encrypt value if it is a secure field
            String displayWorkgroup = dataDictionaryService.getAttributeDisplayWorkgroup(businessObject.getClass(), fieldNm);
            if (StringUtils.isNotBlank(displayWorkgroup)) {
                try {
                    fieldVal = getEncryptionService().encrypt(fieldVal);
                }
                catch (GeneralSecurityException e) {
                    LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
                    throw new RuntimeException(e);
                }

                // add to parameter list so that KualiInquiryAction can identify which parameters are encrypted
                if (encryptedList.equals("")) {
                    encryptedList = fieldNm;
                }
                else {
                    encryptedList = encryptedList + Constants.FIELD_CONVERSIONS_SEPERATOR + fieldNm;
                }
            }

            parameters.put(fieldNm, fieldVal.toString());
        }

        // if we did encrypt a value (or values), add the list of those that are encrypted to the parameters
        if (!encryptedList.equals("")) {
            parameters.put(Constants.ENCRYPTED_LIST_PREFIX, encryptedList);
        }

        // FIXME: either use UrlFactory or hardcode url
        String url = UrlFactory.parameterizeUrl(Constants.MAINTENANCE_ACTION, parameters);
        url = "<a href=\"" + url + "\">" + methodToCall + "</a>";
        return url;
    }
    
    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getBusinessObjectClass()
     */
    public Class getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#setBusinessObjectClass(java.lang.Class)
     */
    public void setBusinessObjectClass(Class businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
        setRows();
    }
    
    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
    
    /**
     * Gets the businessObjectDictionaryService attribute. 
     * @return Returns the businessObjectDictionaryService.
     */
    public BusinessObjectDictionaryService getBusinessObjectDictionaryService() {
        return businessObjectDictionaryService;
    }

    /**
     * Sets the businessObjectDictionaryService attribute value.
     * @param businessObjectDictionaryService The businessObjectDictionaryService to set.
     */
    public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
        this.businessObjectDictionaryService = businessObjectDictionaryService;
    }

    
    /**
     * Gets the businessObjectMetaDataService attribute. 
     * @return Returns the businessObjectMetaDataService.
     */
    public BusinessObjectMetaDataService getBusinessObjectMetaDataService() {
        return businessObjectMetaDataService;
    }

    /**
     * Sets the businessObjectMetaDataService attribute value.
     * @param businessObjectMetaDataService The businessObjectMetaDataService to set.
     */
    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
        this.businessObjectMetaDataService = businessObjectMetaDataService;
    }

    /**
     * Gets the persistenceStructureService attribute. 
     * @return Returns the persistenceStructureService.
     */
    protected PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Gets the encryptionService attribute. 
     * @return Returns the encryptionService.
     */
    protected EncryptionService getEncryptionService() {
        return encryptionService;
    }

    /**
     * Sets the encryptionService attribute value.
     * @param encryptionService The encryptionService to set.
     */
    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

    /**
     * Determines if underlying lookup bo has associated maintenance document that allows new or copy maintenance actions.
     * 
     * @return true if bo has maint doc that allows new or copy actions
     */
    public boolean allowsMaintenanceNewOrCopyAction() {
        boolean allowsNewOrCopy = false;
        
        String maintDocTypeName = getMaintenanceDocumentTypeName();
        if (StringUtils.isNotBlank(maintDocTypeName)) {
            allowsNewOrCopy = KNSServiceLocator.getMaintenanceDocumentDictionaryService().getAllowsNewOrCopy(maintDocTypeName);
        }
    
        return allowsNewOrCopy;
    }

    /**
     * @returns links to edit and copy maintenance action for the current maintenance record if the business object
     * class has an associated maintenance document. Also checks value of allowsNewOrCopy in maintenance document xml
     * before rendering the copy link.
     *
     * @see org.kuali.core.lookup.LookupableHelperService#getActionUrls(org.kuali.core.bo.BusinessObject)
     */
    public String getActionUrls(BusinessObject businessObject) {
        StringBuffer actions = new StringBuffer();
        if (StringUtils.isNotBlank(getMaintenanceDocumentTypeName())) {
            actions.append(getMaintenanceUrl(businessObject, Constants.MAINTENANCE_EDIT_METHOD_TO_CALL));
        }
    
        if (allowsMaintenanceNewOrCopyAction()) {
            actions.append("&nbsp;&nbsp;");
            actions.append(getMaintenanceUrl(businessObject, Constants.MAINTENANCE_COPY_METHOD_TO_CALL));
        }
    
        return actions.toString();
    }

    /**
     * Returns the maintenance document type associated with the business object class or null if one does not
     * exist.
     * @return String representing the maintenance document type name
     */
    protected String getMaintenanceDocumentTypeName() {
        MaintenanceDocumentDictionaryService dd = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
        String maintDocTypeName = dd.getDocumentTypeName(getBusinessObjectClass());
        return maintDocTypeName;
    }
    
    /**
     * Gets the readOnlyFieldsList attribute.
     * 
     * @return Returns the readOnlyFieldsList.
     */
    public List<String> getReadOnlyFieldsList() {
        return readOnlyFieldsList;
    }


    /**
     * Sets the readOnlyFieldsList attribute value.
     * 
     * @param readOnlyFieldsList The readOnlyFieldsList to set.
     */
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList) {
        this.readOnlyFieldsList = readOnlyFieldsList;
    }
    
    /**
     * Returns the inquiry url for a field if one exist.
     * 
     * @param bo the business object instance to build the urls for
     * @param propertyName the property which links to an inquirable
     * @return String url to inquiry
     */
    public String getInquiryUrl(BusinessObject bo, String propertyName) {
        String inquiryUrl = "";

        if (getBusinessObjectDictionaryService().noLookupResultFieldInquiry(bo.getClass(), propertyName) != null && !(getBusinessObjectDictionaryService().noLookupResultFieldInquiry(bo.getClass(), propertyName)).booleanValue()) {
            inquiryUrl = KualiInquirableImpl.getInquiryUrl(bo, propertyName, (getBusinessObjectDictionaryService().forceLookupResultFieldInquiry(bo.getClass(), propertyName)).booleanValue());
        }

        return inquiryUrl;
    }
    
    /**
     * Constructs the list of columns for the search results. All properties for the column objects come from the DataDictionary.
     */
    public List<Column> getColumns() {
        List<Column> columns = new ArrayList<Column>();

        for (String attributeName : getBusinessObjectDictionaryService().getLookupResultFieldNames(getBusinessObjectClass())) {
            Column column = new Column();
            column.setPropertyName(attributeName);
            String columnTitle = dataDictionaryService.getAttributeLabel(getBusinessObjectClass(), attributeName);
            if (StringUtils.isBlank(columnTitle)) {
                columnTitle = dataDictionaryService.getCollectionLabel(getBusinessObjectClass(), attributeName);
            }
            column.setColumnTitle(columnTitle);
            column.setMaxLength(getBusinessObjectDictionaryService().getLookupResultFieldMaxLength(getBusinessObjectClass(), attributeName));
            
            Class formatterClass = dataDictionaryService.getAttributeFormatter(getBusinessObjectClass(), attributeName);
            if (formatterClass != null) {
                try {
                    column.setFormatter((Formatter) formatterClass.newInstance());
                }
                catch (InstantiationException e) {
                    LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                    throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
                }
                catch (IllegalAccessException e) {
                    LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                    throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
                }
            }

            columns.add(column);
        }
        return columns;
    }
    
    /**
     * @return Returns the backLocation.
     */
    public String getBackLocation() {
        return backLocation;
    }

    /**
     * @param backLocation The backLocation to set.
     */
    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }

    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getReturnLocation()
     */
    public String getReturnLocation() {
        return backLocation;
    }
    
    /**
     * @see org.kuali.core.lookup.LookupableHelperService#getReturnUrl(org.kuali.core.bo.BusinessObject, java.util.Map, java.lang.String)
     */
    public String getReturnUrl(BusinessObject businessObject, Map fieldConversions, String lookupImpl) {
        String url = UrlFactory.parameterizeUrl(backLocation, getParameters(businessObject, fieldConversions, lookupImpl));
        return url;
    }
    
    protected Properties getParameters(BusinessObject bo, Map fieldConversions, String lookupImpl) {
        Properties parameters = new Properties();
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, Constants.RETURN_METHOD_TO_CALL);
        parameters.put(Constants.DOC_FORM_KEY, getDocFormKey());
        parameters.put(Constants.REFRESH_CALLER, lookupImpl);
        if (getReferencesToRefresh() != null) {
            parameters.put(Constants.REFERENCES_TO_REFRESH, getReferencesToRefresh());
        }
        
        String encryptedList = "";

        Iterator returnKeys = getReturnKeys().iterator();
        while (returnKeys.hasNext()) {
            String fieldNm = (String) returnKeys.next();

            Object fieldVal = ObjectUtils.getPropertyValue(bo, fieldNm);
            if (fieldVal == null) {
                fieldVal = Constants.EMPTY_STRING;
            }


            // Encrypt value if it is a secure field
            String displayWorkgroup = dataDictionaryService.getAttributeDisplayWorkgroup(bo.getClass(), fieldNm);

            if (fieldConversions.containsKey(fieldNm)) {
                fieldNm = (String) fieldConversions.get(fieldNm);
            }

            if (StringUtils.isNotBlank(displayWorkgroup) && !GlobalVariables.getUserSession().getUniversalUser().isMember( displayWorkgroup )) {
                try {
                    fieldVal = encryptionService.encrypt(fieldVal);
                }
                catch (GeneralSecurityException e) {
                    LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
                    throw new RuntimeException(e);
                }

                // add to parameter list so that KualiInquiryAction can identify which parameters are encrypted
                if (encryptedList.equals("")) {
                    encryptedList = fieldNm;
                }
                else {
                    encryptedList = encryptedList + Constants.FIELD_CONVERSIONS_SEPERATOR + fieldNm;
                }
            }

            parameters.put(fieldNm, fieldVal.toString());
        }

        // if we did encrypt a value (or values), add the list of those that are encrypted to the parameters
        if (!encryptedList.equals("")) {
            parameters.put(Constants.ENCRYPTED_LIST_PREFIX, encryptedList);
        }

        return parameters;
    }
    
    /**
     * @return a List of the names of fields which are marked in data dictionary as return fields.
     */
    public List getReturnKeys() {
        List returnKeys;
        if (fieldConversions != null && !fieldConversions.isEmpty()) {
            returnKeys = new ArrayList(fieldConversions.keySet());
        }
        else {
            returnKeys = getPersistenceStructureService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        }

        return returnKeys;
    }

    /**
     * Gets the docFormKey attribute. 
     * @return Returns the docFormKey.
     */
    public String getDocFormKey() {
        return docFormKey;
    }

    /**
     * Sets the docFormKey attribute value.
     * @param docFormKey The docFormKey to set.
     */
    public void setDocFormKey(String docFormKey) {
        this.docFormKey = docFormKey;
    }
    
    /**
     * @see org.kuali.core.lookup.LookupableHelperService#setFieldConversions(java.util.Map)
     */
    public void setFieldConversions(Map fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    /**
     * Gets the lookupService attribute. 
     * @return Returns the lookupService.
     */
    protected LookupService getLookupService() {
        return lookupService;
    }

    /**
     * Sets the lookupService attribute value.
     * @param lookupService The lookupService to set.
     */
    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }
    
    /**
     * Uses the DD to determine which is the default sort order.
     * 
     * @return property names that will be used to sort on by default
     */
    public List getDefaultSortColumns() {
        return getBusinessObjectDictionaryService().getLookupDefaultSortFieldNames(getBusinessObjectClass());
    }
    
    /**
     * Checks that any required search fields have value.
     * 
     * @see org.kuali.core.lookup.LookupableHelperService#validateSearchParameters(java.util.Map)
     */
    public void validateSearchParameters(Map fieldValues) {
        List<String> lookupFieldAttributeList = null;
        if(getBusinessObjectMetaDataService().isLookupable(getBusinessObjectClass())) {
            lookupFieldAttributeList = getBusinessObjectMetaDataService().getLookupableFieldNames(getBusinessObjectClass());
        }
        if (lookupFieldAttributeList == null) {
            throw new RuntimeException("Lookup not defined for business object " + getBusinessObjectClass());
        }
        for (Iterator iter = lookupFieldAttributeList.iterator(); iter.hasNext();) {
            String attributeName = (String) iter.next();
            if (fieldValues.containsKey(attributeName)) {
                // get label of attribute for message
                String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), attributeName);

                String attributeValue = (String) fieldValues.get(attributeName);

                boolean isSecureField = !StringUtils.isBlank(getDataDictionaryService().getAttributeDisplayWorkgroup(getBusinessObjectClass(), attributeName));

                // check for required if field does not have value
                if (StringUtils.isBlank(attributeValue)) {
                    if ((getBusinessObjectDictionaryService().getLookupAttributeRequired(getBusinessObjectClass(), attributeName)).booleanValue()) {
                        GlobalVariables.getErrorMap().putError(attributeName, KeyConstants.ERROR_REQUIRED, attributeLabel);
                    }
                }
                else if (isSecureField) {
                    // following loop would be trivial if Constants.QUERY_CHARACTERS would implement CharSequence but not so
                    // sure if that makes sense...
                    for (int i = 0; i < Constants.QUERY_CHARACTERS.length; i++) {
                        String queryCharacter = Constants.QUERY_CHARACTERS[i];

                        if (attributeValue.contains(queryCharacter)) {
                            GlobalVariables.getErrorMap().putError(attributeName, KeyConstants.ERROR_SECURE_FIELD, attributeLabel);
                        }
                    }
                }
            }
        }

        if (!GlobalVariables.getErrorMap().isEmpty()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    protected UniversalUserService getUniversalUserService() {
        return universalUserService;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }
    
    /**
     * Constructs the list of rows for the search fields. All properties for the field objects come from the DataDictionary.
     * To be called by setBusinessObject
     */
    protected void setRows() {
        List localRows = new ArrayList();
        List<String> lookupFieldAttributeList = null;
        if(getBusinessObjectMetaDataService().isLookupable(getBusinessObjectClass())) {
            lookupFieldAttributeList = getBusinessObjectMetaDataService().getLookupableFieldNames(getBusinessObjectClass());
        }
        if (lookupFieldAttributeList == null) {
            throw new RuntimeException("Lookup not defined for business object " + getBusinessObjectClass());
        }

        // construct field object for each search attribute
        List fields = new ArrayList();
        try {
            fields = FieldUtils.createAndPopulateFieldsForLookup(lookupFieldAttributeList, getReadOnlyFieldsList(), getBusinessObjectClass());
            
        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }
        this.rows = FieldUtils.wrapFields(fields);
    }

    public List<Row> getRows() {
        return rows;
    }

    public abstract List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues);

    
    /**
     * This implementation of this method throws an UnsupportedOperationException, since not every implementation
     * may actually want to use this operation.  Subclasses desiring other behaviors
     * will need to override this.
     *  
     * @see org.kuali.core.lookup.LookupableHelperService#getSearchResultsUnbounded(java.util.Map)
     */
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        throw new UnsupportedOperationException("Lookupable helper services do not always support getSearchResultsUnbounded");
    }
    
    /**
     * 
     * This method performs the lookup and returns a collection of lookup items
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        Collection displayList;
        
        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }

        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            String returnUrl = getReturnUrl(element, lookupForm.getFieldConversions(), lookupForm.getLookupableImplServiceName());
            String actionUrls = getActionUrls(element);

            List<Column> columns = getColumns();
            List<Column> rowColumns = new ArrayList<Column>();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                
                Column col = (Column) iterator.next();
                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = Constants.EMPTY_STRING;
                Object prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());
                
                // set comparator and formatter based on property type
                Class propClass = null;
                try {
                	propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
                }
                catch (Exception e) {
                    throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
                }

                // formatters
                if (prop != null) {
                    // for Booleans, always use BooleanFormatter
                    if (prop instanceof Boolean) {
                        formatter = new BooleanFormatter();
                    }

                    if (formatter != null) {
                        propValue = (String) formatter.format(prop);
                    }
                    else {
                        propValue = prop.toString();
                    }
                }

                // comparator
                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));
                
                // check security on field and do masking if necessary
                boolean viewAuthorized = KNSServiceLocator.getAuthorizationService().isAuthorizedToViewAttribute(GlobalVariables.getUserSession().getUniversalUser(), element.getClass().getName(), col.getPropertyName());
                if (!viewAuthorized) {
                    Mask displayMask = getDataDictionaryService().getAttributeDisplayMask(element.getClass().getName(), col.getPropertyName());
                    propValue = displayMask.maskValue(propValue);
                }
                col.setPropertyValue(propValue);


                if (StringUtils.isNotBlank(propValue)) {
                    col.setPropertyURL(getInquiryUrl(element, col.getPropertyName()));
                }

                rowColumns.add(col);
            }

            ResultRow row = new ResultRow(rowColumns, returnUrl, actionUrls);
            if ( element instanceof PersistableBusinessObject ) {
                row.setObjectId(((PersistableBusinessObject)element).getObjectId());
            }
            resultTable.add(row);
        }

        return displayList;
    }

    
    protected void setReferencesToRefresh(String referencesToRefresh) {
        this.referencesToRefresh = referencesToRefresh;
    }

    public String getReferencesToRefresh() {
        return referencesToRefresh;
    }

    protected SequenceAccessorService getSequenceAccessorService() {
        return sequenceAccessorService;
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }
    
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected LookupResultsService getLookupResultsService() {
        return lookupResultsService;
    }

    public void setLookupResultsService(LookupResultsService lookupResultsService) {
        this.lookupResultsService = lookupResultsService;
    }

    /**
     * @return false always, subclasses should override to do something smarter
     * @see org.kuali.core.lookup.LookupableHelperService#isSearchUsingOnlyPrimaryKeyValues()
     */
    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        // by default, this implementation returns false, as lookups may not necessarily support this
        return false;
    }

    /**
     * Returns "N/A"
     * 
     * @return "N/A"
     * @see org.kuali.core.lookup.LookupableHelperService#getPrimaryKeyFieldLabels()
     */
    public String getPrimaryKeyFieldLabels() {
        return Constants.NOT_AVAILABLE_STRING;
    }
}