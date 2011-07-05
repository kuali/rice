/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.krad.lookup;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.util.RiceKeyConstants;
import org.kuali.rice.core.util.type.TypeUtils;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.datadictionary.RelationshipDefinition;
import org.kuali.rice.krad.service.DataObjectAuthorizationService;
import org.kuali.rice.krad.service.DataObjectMetaDataService;
import org.kuali.rice.krad.service.DocumentDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.LookupService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.UifParameters;
import org.kuali.rice.krad.uif.container.LookupView;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.control.HiddenControl;
import org.kuali.rice.krad.uif.field.AttributeField;
import org.kuali.rice.krad.uif.field.GeneratedField;
import org.kuali.rice.krad.uif.field.LookupCriteriaAttributeField;
import org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl;
import org.kuali.rice.krad.uif.util.ObjectPropertyUtils;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.krad.web.controller.MaintenanceDocumentController;
import org.kuali.rice.krad.web.form.LookupForm;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Default implementation of <code>Lookupable</code>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LookupableImpl extends ViewHelperServiceImpl implements Lookupable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupableImpl.class);

    private Class<?> dataObjectClass;

    private Map<String, String> parameters;
    private List<String> defaultSortAttributeNames;

    // TODO delyea: where to take into account the sort ascending value (old KNS appeared to ignore?)
    private boolean sortAscending;

    private Map<String, String> fieldConversions;
    private List<String> readOnlyFieldsList;

    private transient DataObjectAuthorizationService dataObjectAuthorizationService;
    private transient DataObjectMetaDataService dataObjectMetaDataService;
    private transient DocumentDictionaryService documentDictionaryService;
    private transient LookupService lookupService;
    private transient EncryptionService encryptionService;

    /**
     * @see org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl#populateViewFromRequestParameters(org.kuali.rice.krad.uif.container.View,
     *      java.util.Map)
     */
    @Override
    public void populateViewFromRequestParameters(View view, Map<String, String> parameters) {
        super.populateViewFromRequestParameters(view, parameters);
        /* On the old Lookupable and LookupableHelperService in KNS the parameters list used to have multipart form
           * data in it where it may not in the new KRAD. See PojoFormBase.populate() method for more information
           */
        setParameters(parameters);
    }

    /**
     * Initialization of Lookupable requires that the business object class be set for the {@link
     * #initializeAttributeFieldFromDataDictionary(View, org.kuali.rice.krad.uif.field.AttributeField)} method
     *
     * @see org.kuali.rice.krad.uif.service.impl.ViewHelperServiceImpl#performInitialization(org.kuali.rice.krad.uif.container.View)
     */
    @Override
    public void performInitialization(View view) {
        if (!LookupView.class.isAssignableFrom(view.getClass())) {
            throw new IllegalArgumentException(
                    "View class '" + view.getClass() + " is not assignable from the '" + LookupView.class + "'");
        }

        LookupView lookupView = (LookupView) view;
        initializeLookupViewHelperService(lookupView);

        super.performInitialization(view);
    }

    /**
     * Initializes properties on this lookupable from the <code>LookupView</code>
     *
     * @param lookupView - lookup view instance
     */
    protected void initializeLookupViewHelperService(LookupView lookupView) {
        setDefaultSortAttributeNames(lookupView.getDefaultSortAttributeNames());
        setSortAscending(lookupView.isSortAscending());
        setDataObjectClass(lookupView.getDataObjectClassName());
    }

    /**
     * @see org.kuali.rice.krad.lookup.Lookupable#performSearch
     */
    @Override
    public Collection<?> performSearch(LookupForm form, Map<String, String> searchCriteria, boolean bounded) {
        Collection<?> displayList;

        LookupUtils.preprocessDateFields(searchCriteria);

        // TODO: force uppercase will be done in binding at some point
        displayList =
                getSearchResults(form, LookupUtils.forceUppercase(getDataObjectClass(), searchCriteria), !bounded);

        // TODO delyea - is this the best way to set that the entire set has a returnable row?
        List<String> pkNames = getDataObjectMetaDataService().listPrimaryKeyFieldNames(getDataObjectClass());
        Person user = GlobalVariables.getUserSession().getPerson();

        for (Object object : displayList) {
            if (isResultReturnable(object)) {
                form.setAtLeastOneRowReturnable(true);
            }

//            BusinessObjectRestrictions dataObjectRestrictions =
//                    getBusinessObjectAuthorizationService().getLookupResultRestrictions(object, user);
//            String actionUrls = getActionUrls(object, pkNames, dataObjectRestrictions);
//            if (StringUtils.isNotBlank(HtmlUtils.htmlUnescape(actionUrls).replace('\u00A0', '\u0020'))) {
//                setAtLeastOneRowHasActions(true);
//            }
//            if (isAtLeastOneRowReturnable() && isAtLeastOneRowHasActions()) {
//                break;
//            }
        }

        return displayList;
    }

    protected List<?> getSearchResults(LookupForm form, Map<String, String> searchCriteria, boolean unbounded) {
        List<?> searchResults;

        // removed blank search values and decrypt any encrypted search values
        Map<String, String> nonBlankSearchCriteria = processSearchCriteria(form, searchCriteria);

        boolean searchUsingOnlyPrimaryKeyValues =
                getLookupService().allPrimaryKeyValuesPresentAndNotWildcard(getDataObjectClass(), searchCriteria);

        // if this class is an EBO, just call the module service to get the results
        if (ExternalizableBusinessObject.class.isAssignableFrom(getDataObjectClass())) {
            return getSearchResultsForEBO(nonBlankSearchCriteria, unbounded);
        }

        // if any of the properties refer to an embedded EBO, call the EBO
        // lookups first and apply to the local lookup
        try {
            if (LookupUtils.hasExternalBusinessObjectProperty(getDataObjectClass(), nonBlankSearchCriteria)) {
                Map<String, String> eboSearchCriteria = adjustCriteriaForNestedEBOs(nonBlankSearchCriteria, unbounded);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Passing these results into the lookup service: " + eboSearchCriteria);
                }

                // add those results as criteria run the normal search (but with the EBO criteria added)
                searchResults = (List<?>) getLookupService()
                        .findCollectionBySearchHelper(getDataObjectClass(), eboSearchCriteria, unbounded);
            } else {
                searchResults = (List<?>) getLookupService()
                        .findCollectionBySearchHelper(getDataObjectClass(), nonBlankSearchCriteria, unbounded);
            }
        } catch (IllegalAccessException e) {
            LOG.error("Error trying to perform search", e);
            throw new RuntimeException("Error trying to perform search", e);
        } catch (InstantiationException e1) {
            LOG.error("Error trying to perform search", e1);
            throw new RuntimeException("Error trying to perform search", e1);
        }

        if (searchResults == null) {
            searchResults = new ArrayList<Object>();
        }

        // sort list if default sort column given
        List<String> defaultSortColumns = getDefaultSortAttributeNames();
        if ((defaultSortColumns != null) && (defaultSortColumns.size() > 0)) {
            Collections.sort(searchResults, new BeanPropertyComparator(defaultSortColumns, true));
        }

        return searchResults;
    }

    protected Map<String, String> processSearchCriteria(LookupForm lookupForm, Map<String, String> searchCriteria) {
        Map<String, String> nonBlankSearchCriteria = new HashMap<String, String>();
        for (String fieldName : searchCriteria.keySet()) {
            String fieldValue = searchCriteria.get(fieldName);

            // don't add hidden criteria
            LookupView lookupView = (LookupView) lookupForm.getView();
            AttributeField attributeField = lookupView.getViewIndex().getAttributeFieldByPropertyName(fieldName);
            if (attributeField.getControl() instanceof HiddenControl) {
                continue;
            }

            // only add criteria if non blank
            if (StringUtils.isNotBlank(fieldValue)) {
                if (fieldValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
                    String encryptedValue = StringUtils.removeEnd(fieldValue, EncryptionService.ENCRYPTION_POST_PREFIX);
                    try {
                        fieldValue = getEncryptionService().decrypt(encryptedValue);
                    } catch (GeneralSecurityException e) {
                        LOG.error("Error decrypting value for business object class " + getDataObjectClass() +
                                " attribute " + fieldName, e);
                        throw new RuntimeException(
                                "Error decrypting value for business object class " + getDataObjectClass() +
                                        " attribute " + fieldName, e);
                    }
                }

                nonBlankSearchCriteria.put(fieldName, fieldValue);
            }
        }

        return nonBlankSearchCriteria;
    }

    protected List<?> getSearchResultsForEBO(Map<String, String> searchCriteria, boolean unbounded) {
        ModuleService eboModuleService =
                KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(getDataObjectClass());
        BusinessObjectEntry ddEntry =
                eboModuleService.getExternalizableBusinessObjectDictionaryEntry(getDataObjectClass());

        Map<String, String> filteredFieldValues = new HashMap<String, String>();
        for (String fieldName : searchCriteria.keySet()) {
            if (ddEntry.getAttributeNames().contains(fieldName)) {
                filteredFieldValues.put(fieldName, searchCriteria.get(fieldName));
            }
        }

        List<?> searchResults = eboModuleService.getExternalizableBusinessObjectsListForLookup(
                (Class<? extends ExternalizableBusinessObject>) getDataObjectClass(), (Map) filteredFieldValues,
                unbounded);

        return searchResults;
    }

    protected Map<String, String> adjustCriteriaForNestedEBOs(Map<String, String> searchCriteria,
            boolean unbounded) throws InstantiationException, IllegalAccessException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("has EBO reference: " + getDataObjectClass());
            LOG.debug("properties: " + searchCriteria);
        }

        // remove the EBO criteria
        Map<String, String> nonEboFieldValues =
                LookupUtils.removeExternalizableBusinessObjectFieldValues(getDataObjectClass(), searchCriteria);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Non EBO properties removed: " + nonEboFieldValues);
        }

        // get the list of EBO properties attached to this object
        List<String> eboPropertyNames =
                LookupUtils.getExternalizableBusinessObjectProperties(getDataObjectClass(), searchCriteria);
        if (LOG.isDebugEnabled()) {
            LOG.debug("EBO properties: " + eboPropertyNames);
        }

        // loop over those properties
        for (String eboPropertyName : eboPropertyNames) {
            // extract the properties as known to the EBO
            Map<String, String> eboFieldValues =
                    LookupUtils.getExternalizableBusinessObjectFieldValues(eboPropertyName, searchCriteria);
            if (LOG.isDebugEnabled()) {
                LOG.debug("EBO properties for master EBO property: " + eboPropertyName);
                LOG.debug("properties: " + eboFieldValues);
            }

            // run search against attached EBO's module service
            ModuleService eboModuleService = KRADServiceLocatorWeb.getKualiModuleService().getResponsibleModuleService(
                    LookupUtils.getExternalizableBusinessObjectClass(getDataObjectClass(), eboPropertyName));

            // KULRICE-4401 made eboResults an empty list and only filled if
            // service is found.
            List<?> eboResults = Collections.emptyList();
            if (eboModuleService != null) {
                eboResults = eboModuleService.getExternalizableBusinessObjectsListForLookup(
                        LookupUtils.getExternalizableBusinessObjectClass(getDataObjectClass(), eboPropertyName),
                        (Map) eboFieldValues, unbounded);
            } else {
                LOG.debug("EBO ModuleService is null: " + eboPropertyName);
            }
            // get the mapping/relationship between the EBO object and it's
            // parent object
            // use that to adjust the searchCriteria

            // get the parent property type
            Class<?> eboParentClass;
            String eboParentPropertyName;
            if (ObjectUtils.isNestedAttribute(eboPropertyName)) {
                eboParentPropertyName = StringUtils.substringBeforeLast(eboPropertyName, ".");
                try {
                    eboParentClass =
                            PropertyUtils.getPropertyType(getDataObjectClass().newInstance(), eboParentPropertyName);
                } catch (Exception ex) {
                    throw new RuntimeException("Unable to create an instance of the business object class: " +
                            getDataObjectClass().getName(), ex);
                }
            } else {
                eboParentClass = getDataObjectClass();
                eboParentPropertyName = null;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("determined EBO parent class/property name: " + eboParentClass + "/" + eboParentPropertyName);
            }

            // look that up in the DD (BOMDS)
            // find the appropriate relationship
            // CHECK THIS: what if eboPropertyName is a nested attribute -
            // need to strip off the eboParentPropertyName if not null
            RelationshipDefinition rd =
                    getDataObjectMetaDataService().getDictionaryRelationship(eboParentClass, eboPropertyName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Obtained RelationshipDefinition for " + eboPropertyName);
                LOG.debug(rd);
            }

            // copy the needed properties (primary only) to the field values KULRICE-4446 do
            // so only if the relationship definition exists
            // NOTE: this will work only for single-field PK unless the ORM
            // layer is directly involved
            // (can't make (field1,field2) in ( (v1,v2),(v3,v4) ) style
            // queries in the lookup framework
            if (ObjectUtils.isNotNull(rd)) {
                if (rd.getPrimitiveAttributes().size() > 1) {
                    throw new RuntimeException(
                            "EBO Links don't work for relationships with multiple-field primary keys.");
                }
                String boProperty = rd.getPrimitiveAttributes().get(0).getSourceName();
                String eboProperty = rd.getPrimitiveAttributes().get(0).getTargetName();
                StringBuffer boPropertyValue = new StringBuffer();

                // loop over the results, making a string that the lookup
                // DAO will convert into an
                // SQL "IN" clause
                for (Object ebo : eboResults) {
                    if (boPropertyValue.length() != 0) {
                        boPropertyValue.append(SearchOperator.OR.op());
                    }
                    try {
                        boPropertyValue.append(PropertyUtils.getProperty(ebo, eboProperty).toString());
                    } catch (Exception ex) {
                        LOG.warn("Unable to get value for " + eboProperty + " on " + ebo);
                    }
                }

                if (eboParentPropertyName == null) {
                    // non-nested property containing the EBO
                    nonEboFieldValues.put(boProperty, boPropertyValue.toString());
                } else {
                    // property nested within the main searched-for BO that
                    // contains the EBO
                    nonEboFieldValues.put(eboParentPropertyName + "." + boProperty, boPropertyValue.toString());
                }
            }
        }

        return nonEboFieldValues;
    }

    /**
     * @see org.kuali.rice.krad.lookup.Lookupable#performClear
     */
    @Override
    public Map<String, String> performClear(LookupForm form, Map<String, String> searchCriteria) {
        Map<String, String> clearedSearchCriteria = new HashMap<String, String>();
        for (Map.Entry<String, String> searchKeyValue : searchCriteria.entrySet()) {
            String searchPropertyName = searchKeyValue.getKey();
            String searchPropertyValue = searchKeyValue.getValue();

            LookupView lookupView = (LookupView) form.getView();
            AttributeField attributeField =
                    lookupView.getViewIndex().getAttributeFieldByPropertyName(searchPropertyName);
            if (attributeField != null) {
                // TODO: check secure fields
//                                if (field.isSecure()) {
//                    field.setSecure(false);
//                    field.setDisplayMaskValue(null);
//                    field.setEncryptedValue(null);
//                }

                // TODO: need formatting on default value and make sure it works when control converts
                // from checkbox to radio
                clearedSearchCriteria.put(searchPropertyName, attributeField.getDefaultValue());
            } else {
                throw new RuntimeException("Invalid search field sent for property name: " + searchPropertyName);
            }
        }

        return clearedSearchCriteria;
    }

    /**
     * @see org.kuali.rice.krad.lookup.Lookupable#validateSearchParameters
     */
    @Override
    public boolean validateSearchParameters(LookupForm form, Map<String, String> searchCriteria) {
        boolean valid = true;

        if (!getViewDictionaryService().isLookupable(getDataObjectClass())) {
            throw new RuntimeException("Lookup not defined for data object " + getDataObjectClass());
        }

        // validate required
        // TODO: this will be done by the uif validation service at some point
        for (Map.Entry<String, String> searchKeyValue : searchCriteria.entrySet()) {
            String searchPropertyName = searchKeyValue.getKey();
            String searchPropertyValue = searchKeyValue.getValue();

            LookupView lookupView = (LookupView) form.getView();
            AttributeField attributeField =
                    lookupView.getViewIndex().getAttributeFieldByPropertyName(searchPropertyName);
            if (attributeField != null) {
                if (StringUtils.isBlank(searchPropertyValue) && BooleanUtils.isTrue(attributeField.getRequired())) {
                    GlobalVariables.getMessageMap()
                            .putError(attributeField.getPropertyName(), RiceKeyConstants.ERROR_REQUIRED,
                                    attributeField.getLabel());
                }

                validateSearchParameterWildcardAndOperators(attributeField, searchPropertyValue);
            } else {
                throw new RuntimeException("Invalid search field sent for property name: " + searchPropertyName);
            }
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            valid = false;
        }

        return valid;
    }

    /**
     * Validates that any wildcards contained within the search value are valid wilcards and allowed for the
     * property type for which the field is searching
     *
     * @param attributeField - attribute field instance for the field that is being searched
     * @param searchPropertyValue - value given for field to search for
     */
    protected void validateSearchParameterWildcardAndOperators(AttributeField attributeField,
            String searchPropertyValue) {
        if (StringUtils.isBlank(searchPropertyValue))
            return;

        // make sure a wildcard/operator is in the value
        boolean found = false;
        for (SearchOperator op : SearchOperator.QUERY_CHARACTERS) {
            String queryCharacter = op.op();

            if (searchPropertyValue.contains(queryCharacter)) {
                found = true;
            }
        }

        if (!found) {
            return;
        }

        String attributeLabel = attributeField.getLabel();
        if ((LookupCriteriaAttributeField.class.isAssignableFrom(attributeField.getClass())) &&
                (((LookupCriteriaAttributeField) attributeField).isTreatWildcardsAndOperatorsAsLiteral())) {
            Object dataObjectExample = null;
            try {
                dataObjectExample = getDataObjectClass().newInstance();
            } catch (Exception e) {
                LOG.error("Exception caught instantiating " + getDataObjectClass().getName(), e);
                throw new RuntimeException("Cannot instantiate " + getDataObjectClass().getName(), e);
            }

            Class<?> propertyType =
                    ObjectPropertyUtils.getPropertyType(getDataObjectClass(), attributeField.getPropertyName());
            if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType) ||
                    TypeUtils.isTemporalClass(propertyType)) {
                GlobalVariables.getMessageMap().putError(attributeField.getPropertyName(),
                        RiceKeyConstants.ERROR_WILDCARDS_AND_OPERATORS_NOT_ALLOWED_ON_FIELD, attributeLabel);
            }

            if (TypeUtils.isStringClass(propertyType)) {
                GlobalVariables.getMessageMap().putInfo(attributeField.getPropertyName(),
                        RiceKeyConstants.INFO_WILDCARDS_AND_OPERATORS_TREATED_LITERALLY, attributeLabel);
            }
        } else {
            if (getDataObjectAuthorizationService()
                    .attributeValueNeedsToBeEncryptedOnFormsAndLinks(getDataObjectClass(),
                            attributeField.getPropertyName())) {
                if (!searchPropertyValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
                    // encrypted values usually come from the DB, so we don't
                    // need to filter for wildcards
                    // wildcards are not allowed on restricted fields, because
                    // they are typically encrypted, and wildcard searches cannot be performed without
                    // decrypting every row, which is currently not supported by KRAD

                    GlobalVariables.getMessageMap()
                            .putError(attributeField.getPropertyName(), RiceKeyConstants.ERROR_SECURE_FIELD,
                                    attributeLabel);
                }
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.uif.service.LookupViewHelperService#getCreateNewUrl(org.kuali.rice.krad.uif.field.GeneratedField)
     */
    public String getCreateNewUrl(GeneratedField generatedField) {
        String url = "";

        if (allowsMaintenanceNewOrCopyAction()) {
            Properties props = new Properties();
            // TODO delyea - DOCUMENT THE FOLLOWING CHANGES (next 5 lines)
            // props.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.MAINTENANCE_NEW_METHOD_TO_CALL);
            // props.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, getDataObjectClass().getName());
            props.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, MaintenanceDocumentController.METHOD_TO_CALL_NEW);
            props.put(UifParameters.DATA_OBJECT_CLASS_NAME, getDataObjectClass().getName());
            props.put(UifParameters.VIEW_TYPE_NAME, UifConstants.ViewType.MAINTENANCE);

            // TODO delyea - DOCUMENT THE FOLLOWING CHANGES (next 2 lines)
            // url = UrlFactory.parameterizeUrl(KRADConstants.MAINTENANCE_ACTION, props);
            url = UrlFactory.parameterizeUrl(MaintenanceDocumentController.REQUEST_MAPPING_MAINTENANCE, props);
            url = "<a id=\"" + generatedField.getId() + "\" href=\"" + url +
                    "\"><img src=\"images/tinybutton-createnew.gif\" alt=\"create new\" width=\"70\" height=\"15\"/></a>";
        }

        return url;
    }

    /**
     * Determines if underlying lookup bo has associated maintenance document that allows new or copy maintenance
     * actions
     *
     * @return true if object has maintenance doc that allows new or copy actions
     */
    protected boolean allowsMaintenanceNewOrCopyAction() {
        boolean allowsNewOrCopy = false;

        String maintDocTypeName = getMaintenanceDocumentTypeName();

        if (StringUtils.isNotBlank(maintDocTypeName)) {
            allowsNewOrCopy = getDataObjectAuthorizationService()
                    .canCreate(getDataObjectClass(), GlobalVariables.getUserSession().getPerson(), maintDocTypeName);
        }
        return allowsNewOrCopy;
    }

    /**
     * Returns the maintenance document type associated with the business object class or null if one does not exist.
     *
     * @return String representing the maintenance document type name
     */
    protected String getMaintenanceDocumentTypeName() {
        DocumentDictionaryService dd = getDocumentDictionaryService();
        String maintDocTypeName = dd.getMaintenanceDocumentTypeName(getDataObjectClass());

        return maintDocTypeName;
    }

    public String getReturnUrlForResults(GeneratedField generatedField) {
        return "";
    }

    public String getActionUrlsFromField(GeneratedField generatedField) {
		return "";
	}

    /**
     * Determines whether a given data object that's returned as one of the lookup's results is considered returnable,
     * which means that for single-value lookups, a "return value" link may be rendered, and for multiple
     * value lookups, a checkbox is rendered.
     *
     * Note that this can be part of an authorization mechanism, but not the complete authorization mechanism.  The
     * component that invoked the lookup/ lookup caller (e.g. document, nesting lookup, etc.) needs to check
     * that the object that was passed to it was returnable as well because there are ways around this method
     * (e.g. crafting a custom return URL).
     *
     * @param dataObject - an object from the search result set
     * @return true if the row is returnable and false if it is not
     */
    protected boolean isResultReturnable(Object dataObject) {
        return true;
    }

    /**
     * @see org.kuali.rice.krad.lookup.Lookupable#setDataObjectClass
     */
    @Override
    public void setDataObjectClass(Class<?> dataObjectClass) {
        this.dataObjectClass = dataObjectClass;
    }

    /**
     * @see org.kuali.rice.krad.lookup.Lookupable#getDataObjectClass
     */
    @Override
    public Class<?> getDataObjectClass() {
        return this.dataObjectClass;
    }

    /**
     * @see org.kuali.rice.krad.lookup.Lookupable#setFieldConversions
     */
    @Override
    public void setFieldConversions(Map<String, String> fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    /**
     * @see org.kuali.rice.krad.lookup.Lookupable#setReadOnlyFieldsList
     */
    @Override
    public void setReadOnlyFieldsList(List<String> readOnlyFieldsList) {
        this.readOnlyFieldsList = readOnlyFieldsList;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public List<String> getDefaultSortAttributeNames() {
        return defaultSortAttributeNames;
    }

    public void setDefaultSortAttributeNames(List<String> defaultSortAttributeNames) {
        this.defaultSortAttributeNames = defaultSortAttributeNames;
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public List<String> getReadOnlyFieldsList() {
        return readOnlyFieldsList;
    }

    public Map<String, String> getFieldConversions() {
        return fieldConversions;
    }

    protected DataObjectAuthorizationService getDataObjectAuthorizationService() {
        if (dataObjectAuthorizationService == null) {
            this.dataObjectAuthorizationService = KRADServiceLocatorWeb.getDataObjectAuthorizationService();
        }
        return dataObjectAuthorizationService;
    }

    public void setDataObjectAuthorizationService(DataObjectAuthorizationService dataObjectAuthorizationService) {
        this.dataObjectAuthorizationService = dataObjectAuthorizationService;
    }

    protected DataObjectMetaDataService getDataObjectMetaDataService() {
        if (dataObjectMetaDataService == null) {
            this.dataObjectMetaDataService = KRADServiceLocatorWeb.getDataObjectMetaDataService();
        }
        return dataObjectMetaDataService;
    }

    public void setDataObjectMetaDataService(DataObjectMetaDataService dataObjectMetaDataService) {
        this.dataObjectMetaDataService = dataObjectMetaDataService;
    }

    public DocumentDictionaryService getDocumentDictionaryService() {
        if (documentDictionaryService == null) {
            documentDictionaryService = KRADServiceLocatorWeb.getDocumentDictionaryService();
        }
        return documentDictionaryService;
    }

    public void setDocumentDictionaryService(DocumentDictionaryService documentDictionaryService) {
        this.documentDictionaryService = documentDictionaryService;
    }

    protected LookupService getLookupService() {
        if (lookupService == null) {
            this.lookupService = KRADServiceLocatorWeb.getLookupService();
        }
        return lookupService;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    protected EncryptionService getEncryptionService() {
        if (encryptionService == null) {
            this.encryptionService = CoreApiServiceLocator.getEncryptionService();
        }
        return encryptionService;
    }

    public void setEncryptionService(EncryptionService encryptionService) {
        this.encryptionService = encryptionService;
    }

}
