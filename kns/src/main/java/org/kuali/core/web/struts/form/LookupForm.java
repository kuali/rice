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
package org.kuali.core.web.struts.form;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class is the action form for all lookups.
 */
public class LookupForm extends KualiForm {
    private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LookupForm.class);

    private String formKey;
    private String backLocation;
    private Map fields;
    private Map fieldsForLookup;
    private String lookupableImplServiceName;
    private String conversionFields;
    private Map fieldConversions;
    private String businessObjectClassName;
    private Lookupable lookupable;
    private boolean hideReturnLink = false;
    private boolean suppressActions = false;
    private boolean multipleValues = false;
    private String lookupAnchor;
    private String readOnlyFields;
    private List readOnlyFieldsList;
    private String referencesToRefresh;
    private boolean searchUsingOnlyPrimaryKeyValues;
    private String primaryKeyFieldLabels;
    private boolean showMaintenanceLinks;

    /**
     * Picks out business object name from the request to get retrieve a lookupable and set properties.
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);

        DataDictionaryService ddService = KNSServiceLocator.getDataDictionaryService();

        try {
            Lookupable localLookupable = null;
            if (StringUtils.isBlank(request.getParameter(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME)) && StringUtils.isBlank(getLookupableImplServiceName())) {
                // get the business object class for the lookup
                String localBusinessObjectClassName = request.getParameter(Constants.BUSINESS_OBJECT_CLASS_ATTRIBUTE);
                setBusinessObjectClassName(localBusinessObjectClassName);
                if (StringUtils.isBlank(localBusinessObjectClassName)) {
                    LOG.error("Business object class not passed to lookup.");
                    throw new RuntimeException("Business object class not passed to lookup.");
                }

                // call data dictionary service to get lookup impl for bo class
                String lookupImplID = KNSServiceLocator.getBusinessObjectDictionaryService().getLookupableID(Class.forName(localBusinessObjectClassName));
                if (lookupImplID == null) {
                    lookupImplID = "kualiLookupable";
                }

                setLookupableImplServiceName(lookupImplID);
            }
            localLookupable = KNSServiceLocator.getLookupable(getLookupableImplServiceName());

            if (localLookupable == null) {
                LOG.error("Lookup impl not found for lookup impl name " + getLookupableImplServiceName());
                throw new RuntimeException("Lookup impl not found for lookup impl name " + getLookupableImplServiceName());
            }


            if (request.getParameter(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME) != null) {
                setLookupableImplServiceName(request.getParameter(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME));
            }

            // check the doc form key is empty before setting so we don't override a restored lookup form
            if (request.getAttribute(Constants.DOC_FORM_KEY) != null && StringUtils.isBlank(this.getFormKey())) {
                setFormKey((String) request.getAttribute(Constants.DOC_FORM_KEY));
            }
            else if (request.getParameter(Constants.DOC_FORM_KEY) != null && StringUtils.isBlank(this.getFormKey())) {
                setFormKey(request.getParameter(Constants.DOC_FORM_KEY));
            }

            if (request.getParameter("returnLocation") != null) {
                setBackLocation(request.getParameter("returnLocation"));
            }
            if (request.getParameter("conversionFields") != null) {
                setConversionFields(request.getParameter("conversionFields"));
            }
            if (request.getParameter(Constants.EXTRA_BUTTON_SOURCE) != null) {
                setExtraButtonSource(request.getParameter(Constants.EXTRA_BUTTON_SOURCE));
            }
            if (request.getParameter(Constants.EXTRA_BUTTON_PARAMS) != null) {
                setExtraButtonParams(request.getParameter(Constants.EXTRA_BUTTON_PARAMS));
            }
            String value = request.getParameter("multipleValues");
            if (value != null) {
                if ("YES".equals(value.toUpperCase())) {
                    setMultipleValues(true);
                }
                else {
                    setMultipleValues(new Boolean(request.getParameter("multipleValues")).booleanValue());
                }
            }
            if (request.getParameter(Constants.REFERENCES_TO_REFRESH) != null) {
                setReferencesToRefresh(request.getParameter(Constants.REFERENCES_TO_REFRESH));
            }
        
            if (request.getParameter("readOnlyFields") != null) {
                setReadOnlyFields(request.getParameter("readOnlyFields"));
                setReadOnlyFieldsList(LookupUtils.translateReadOnlyFieldsToList(this.readOnlyFields));
                localLookupable.setReadOnlyFieldsList(getReadOnlyFieldsList());
            }

            // init lookupable with bo class
            localLookupable.setBusinessObjectClass(Class.forName(getBusinessObjectClassName()));
            Map fieldValues = new HashMap();
            Map formFields = getFields();
            Class boClass = Class.forName(getBusinessObjectClassName());

            for (Iterator iter = localLookupable.getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();

                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();

                    // check whether form already has value for field
                    if (formFields != null && formFields.containsKey(field.getPropertyName())) {
                        field.setPropertyValue(formFields.get(field.getPropertyName()));
                    }

                    // override values with request
                    if (request.getParameter(field.getPropertyName()) != null) {
                        field.setPropertyValue(request.getParameter(field.getPropertyName()).trim());
                    }

                    // force uppercase if necessary
                    field.setPropertyValue(LookupUtils.forceUppercase(boClass, field.getPropertyName(), field.getPropertyValue()));
                    fieldValues.put(field.getPropertyName(), field.getPropertyValue());
                }
            }

            if (localLookupable.checkForAdditionalFields(fieldValues)) {
                for (Iterator iter = localLookupable.getRows().iterator(); iter.hasNext();) {
                    Row row = (Row) iter.next();

                    for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                        Field field = (Field) iterator.next();

                        // check whether form already has value for field
                        if (formFields != null && formFields.containsKey(field.getPropertyName())) {
                            field.setPropertyValue(formFields.get(field.getPropertyName()));
                        }

                        // override values with request
                        if (request.getParameter(field.getPropertyName()) != null) {
                            field.setPropertyValue(request.getParameter(field.getPropertyName()).trim());
                        }
                        fieldValues.put(field.getPropertyName(), field.getPropertyValue());
                    }
                }
            }
            fieldValues.put(Constants.DOC_FORM_KEY, this.getFormKey());
            fieldValues.put(Constants.BACK_LOCATION, this.getBackLocation());
            if (StringUtils.isNotBlank(getReferencesToRefresh())) {
                fieldValues.put(Constants.REFERENCES_TO_REFRESH, this.getReferencesToRefresh());
            }
            
            this.setFields(fieldValues);

            setFieldConversions(LookupUtils.translateFieldConversions(this.conversionFields));
            localLookupable.setFieldConversions(getFieldConversions());
            setLookupable(localLookupable);
            setFieldsForLookup(fieldValues);
            
            // only show maintenance links if the lookup was called from the portal
            if (StringUtils.contains(backLocation, KNSServiceLocator.getKualiConfigurationService().getPropertyString(Constants.APPLICATION_URL_KEY) + "/" + Constants.MAPPING_PORTAL)) {
                showMaintenanceLinks = true;
            }
            else {
                showMaintenanceLinks = false;
            }
        }
        catch (ClassNotFoundException e) {
            LOG.error("Business Object class " + getBusinessObjectClassName() + " not found");
            throw new RuntimeException("Business Object class " + getBusinessObjectClassName() + " not found", e);
        }
    }

    /**
     * @return Returns the lookupableImplServiceName.
     */
    public String getLookupableImplServiceName() {
        return lookupableImplServiceName;
    }

    /**
     * @param lookupableImplServiceName The lookupableImplServiceName to set.
     */
    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
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
     * @return Returns the formKey.
     */
    public String getFormKey() {
        return formKey;
    }

    /**
     * @param formKey The formKey to set.
     */
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    /**
     * @return Returns the fields.
     */
    public Map getFields() {
        return fields;
    }

    /**
     * @param fields The fields to set.
     */
    public void setFields(Map fields) {
        this.fields = fields;
    }

    /**
     * @return Returns the conversionFields.
     */
    public String getConversionFields() {
        return conversionFields;
    }

    /**
     * @param conversionFields The conversionFields to set.
     */
    public void setConversionFields(String conversionFields) {
        this.conversionFields = conversionFields;
    }

    /**
     * @return Returns the fieldConversions.
     */
    public Map getFieldConversions() {
        return fieldConversions;
    }

    /**
     * @param fieldConversions The fieldConversions to set.
     */
    public void setFieldConversions(Map fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    /**
     * @return Returns the businessObjectClassName.
     */
    public String getBusinessObjectClassName() {
        return businessObjectClassName;
    }

    /**
     * @param businessObjectClassName The businessObjectClassName to set.
     */
    public void setBusinessObjectClassName(String businessObjectClassName) {
        this.businessObjectClassName = businessObjectClassName;
    }


    /**
     * @return Returns the kualiLookupable.
     */
    public Lookupable getLookupable() {
        return lookupable;
    }


    /**
     * @param kualiLookupable The kualiLookupable to set.
     */
    public void setLookupable(Lookupable lookupable) {
        this.lookupable = lookupable;
    }


    /**
     * @return Returns the hideReturnLink.
     */
    public boolean isHideReturnLink() {
        return hideReturnLink;
    }

    /**
     * @param suppressActions The suppressActions to set.
     */
    public void setSuppressActions(boolean suppressActions) {
        this.suppressActions = suppressActions;
    }

    /**
     * @return Returns the suppressActions.
     */
    public boolean isSuppressActions() {
        return suppressActions;
    }


    /**
     * @param hideReturnLink The hideReturnLink to set.
     */
    public void setHideReturnLink(boolean hideReturnLink) {
        this.hideReturnLink = hideReturnLink;
    }

    // TODO: remove these once DD changes have been made
    public String getExtraButtonParams() {
        return extraButtons.get(0).getExtraButtonParams();
    }

    // TODO: remove these once DD changes have been made
    public void setExtraButtonParams(String extraButtonParams) {
        extraButtons.get(0).setExtraButtonParams( extraButtonParams );
    }

    // TODO: remove these once DD changes have been made
    public String getExtraButtonSource() {
        return extraButtons.get(0).getExtraButtonSource();
    }

    // TODO: remove these once DD changes have been made
    public void setExtraButtonSource(String extraButtonSource) {
        extraButtons.get(0).setExtraButtonSource( extraButtonSource );
    }


    /**
     * 
     * @return whether this form returns multiple values
     */
    public boolean isMultipleValues() {
        return multipleValues;
    }

    /**
     * 
     * @param multipleValues - specify whether this form returns multiple values (i.e. a Collection)
     */
    public void setMultipleValues(boolean multipleValues) {
        this.multipleValues = multipleValues;
    }

    public String getLookupAnchor() {
        return lookupAnchor;
    }

    public void setLookupAnchor(String lookupAnchor) {
        this.lookupAnchor = lookupAnchor;
    }

    /**
     * Gets the fieldsForLookup attribute. 
     * @return Returns the fieldsForLookup.
     */
    public Map getFieldsForLookup() {
        return fieldsForLookup;
    }

    /**
     * Sets the fieldsForLookup attribute value.
     * @param fieldsForLookup The fieldsForLookup to set.
     */
    public void setFieldsForLookup(Map fieldsForLookup) {
        this.fieldsForLookup = fieldsForLookup;
    }

    /**
     * Gets the readOnlyFields attribute. 
     * @return Returns the readOnlyFields.
     */
    public String getReadOnlyFields() {
        return readOnlyFields;
    }

    /**
     * Sets the readOnlyFields attribute value.
     * @param readOnlyFields The readOnlyFields to set.
     */
    public void setReadOnlyFields(String readOnlyFields) {
        this.readOnlyFields = readOnlyFields;
    }

    /**
     * Gets the readOnlyFieldsList attribute. 
     * @return Returns the readOnlyFieldsList.
     */
    public List getReadOnlyFieldsList() {
        return readOnlyFieldsList;
    }

    /**
     * Sets the readOnlyFieldsList attribute value.
     * @param readOnlyFieldsList The readOnlyFieldsList to set.
     */
    public void setReadOnlyFieldsList(List readOnlyFieldsList) {
        this.readOnlyFieldsList = readOnlyFieldsList;
    }

    public String getReferencesToRefresh() {
        return referencesToRefresh;
    }

    public void setReferencesToRefresh(String referencesToRefresh) {
        this.referencesToRefresh = referencesToRefresh;
    }

    public String getPrimaryKeyFieldLabels() {
        return primaryKeyFieldLabels;
    }

    public void setPrimaryKeyFieldLabels(String primaryKeyFieldLabels) {
        this.primaryKeyFieldLabels = primaryKeyFieldLabels;
    }

    public boolean isSearchUsingOnlyPrimaryKeyValues() {
        return searchUsingOnlyPrimaryKeyValues;
    }

    public void setSearchUsingOnlyPrimaryKeyValues(boolean searchUsingOnlyPrimaryKeyValues) {
        this.searchUsingOnlyPrimaryKeyValues = searchUsingOnlyPrimaryKeyValues;
    }

    /**
     * Gets the showMaintenanceLinks attribute. 
     * @return Returns the showMaintenanceLinks.
     */
    public boolean isShowMaintenanceLinks() {
        return showMaintenanceLinks;
    }

    /**
     * Sets the showMaintenanceLinks attribute value.
     * @param showMaintenanceLinks The showMaintenanceLinks to set.
     */
    public void setShowMaintenanceLinks(boolean hideMaintenanceLinks) {
        this.showMaintenanceLinks = hideMaintenanceLinks;
    }
    
    
}