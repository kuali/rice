/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.core.workflow.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import net.sf.cglib.proxy.Enhancer;

import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.WorkflowUtils;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.workflow.attribute.WorkflowLookupableInvocationHandler;
import org.kuali.workflow.attribute.WorkflowLookupableResult;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.lookupable.LookupForm;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;

/**
 * This is a shim to translate from an org.kuali.core.lookup.Lookupable to edu.iu.uis.eden.plugin.attributes.WorkflowLookupable,
 * since quickfinders specified as part of workflow attribute field definitions have to implement the workflow lookupable interface.
 * 
 * 
 * @deprecated This will go away once workflow supports simple url integration for custom attribute lookups.
 */
public class WorkflowLookupableImpl implements WorkflowLookupable {

    private static final Logger LOG = Logger.getLogger(WorkflowLookupableImpl.class);

    public static final String LOOKUPABLE_IMPL_NAME_PREFIX = "workflow-";

    private static final String LOOKUPABLE_IMPL_NAME_SUFFIX = "-Lookupable";

    private static final String RETURN_LOCATION = "Lookup.do";

    private static KualiConfigurationService configurationService;

    private static BusinessObjectDictionaryService businessObjectDictionaryService;

    private String lookupableImplName;

    private Lookupable lookupable;

    private String fieldConversions;

    private String lookupParameters;

    private List workflowRows;

    private List workflowColumns;

    public WorkflowLookupableImpl() {
        workflowRows = new ArrayList();
        workflowColumns = new ArrayList();
    }

    /**
     * This method is for Spring. It sets the businessObjectClassName attribute value and initizes the Lookupable.
     * 
     * @param businessObjectClassName The businessObjectClassName to set.
     */
    public void setBusinessObjectClassName(String businessObjectClassName) {
        try {
            Class businessObjectClass = Class.forName(businessObjectClassName);
            lookupableImplName = KNSServiceLocator.getBusinessObjectDictionaryService().getLookupableID(businessObjectClass);
            if (lookupableImplName == null) {
                lookupableImplName = Constants.KUALI_LOOKUPABLE_IMPL;
            }
            lookupable = KNSServiceLocator.getLookupable(lookupableImplName);
            if (lookupable == null) {
                throw new RuntimeException("WorkflowLookupableImpl could not find Lookupable for lookup impl name: " + lookupableImplName);
            }
            lookupable.setBusinessObjectClass(businessObjectClass);
            setRows();
            setColumns();
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("WorkflowLookupableImpl could not find business object class: " + businessObjectClassName, e);
        }
    }

    private void setRows() {
        Iterator kualiRowItr = lookupable.getRows().iterator();
        while (kualiRowItr.hasNext()) {
            org.kuali.core.web.ui.Row kualiRow = (org.kuali.core.web.ui.Row) kualiRowItr.next();
            List workflowFields = new ArrayList();
            Iterator kualiFieldItr = kualiRow.getFields().iterator();
            while (kualiFieldItr.hasNext()) {
                org.kuali.core.web.ui.Field kualiField = (org.kuali.core.web.ui.Field) kualiFieldItr.next();
                edu.iu.uis.eden.lookupable.Field workflowField = new edu.iu.uis.eden.lookupable.Field(kualiField.getFieldLabel(), WorkflowUtils.getHelpUrl(kualiField), kualiField.getFieldType(), kualiField.getQuickFinderClassNameImpl() != null, kualiField.getPropertyName(), kualiField.getPropertyValue(), kualiField.getFieldValidValues(), kualiField.getQuickFinderClassNameImpl());
                
                //  dont display KualiUser based fieldTypes ... they fail on the lookups for now
                if (org.kuali.core.web.ui.Field.KUALIUSER.equals(kualiField.getFieldType())) {
                    continue;
                }
                //  convert most other types to simple text
                if (org.kuali.core.web.ui.Field.DROPDOWN.equals(kualiField.getFieldType()) || org.kuali.core.web.ui.Field.DROPDOWN_APC.equals(kualiField.getFieldType()) || org.kuali.core.web.ui.Field.DROPDOWN_REFRESH.equals(kualiField.getFieldType()) || org.kuali.core.web.ui.Field.DROPDOWN_SCRIPT.equals(kualiField.getFieldType())) {
                    workflowField.setFieldType(org.kuali.core.web.ui.Field.TEXT);
                }
                
                //  add a quickfinder icon if one is indicated
                workflowFields.add(workflowField);
                if (kualiField.getQuickFinderClassNameImpl() != null && !org.kuali.core.web.ui.Field.HIDDEN.equals(kualiField.getFieldType())) {
                    edu.iu.uis.eden.lookupable.Field workflowLookupField = new edu.iu.uis.eden.lookupable.Field("", "", edu.iu.uis.eden.lookupable.Field.QUICKFINDER, false, "", "", null, getLookupableName(new StringBuffer(LOOKUPABLE_IMPL_NAME_PREFIX).append(workflowField.getQuickFinderClassNameImpl()).append(LOOKUPABLE_IMPL_NAME_SUFFIX).toString(), kualiField.getFieldConversions(), kualiField.getLookupParameters()));
                    workflowFields.add(workflowLookupField);
                }
            }
            edu.iu.uis.eden.lookupable.Row workflowRow = new edu.iu.uis.eden.lookupable.Row(workflowFields);
            workflowRows.add(workflowRow);
        }
    }

    private void setColumns() {
        Iterator kualiColumnItr = lookupable.getColumns().iterator();
        while (kualiColumnItr.hasNext()) {
            org.kuali.core.web.ui.Column kualiColumn = (org.kuali.core.web.ui.Column) kualiColumnItr.next();
            workflowColumns.add(new edu.iu.uis.eden.lookupable.Column(kualiColumn.getColumnTitle(), kualiColumn.getSortable(), "workflowLookupableResult(" + kualiColumn.getPropertyName() + ")"));
        }
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getSearchResults(java.util.Map, java.util.Map)
     */
    public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception {
        List searchResults = lookupable.getSearchResults(fieldValues);
        List workflowLookupableResults = new ArrayList();
        Iterator searchResultItr = searchResults.iterator();
        Map realFieldConversions = fieldConversions;
        Map combinedFieldConversions = null;
        if ((this.fieldConversions != null) && !"null".equals(this.fieldConversions.trim())) {
            if (this.fieldConversions.startsWith(LOOKUPABLE_IMPL_NAME_PREFIX)) {
                realFieldConversions = LookupUtils.translateFieldConversions(this.fieldConversions.replaceAll(LOOKUPABLE_IMPL_NAME_PREFIX, ""));
                combinedFieldConversions = new HashMap();
                Iterator realFieldConversionsEntryItr = realFieldConversions.entrySet().iterator();
                while (realFieldConversionsEntryItr.hasNext()) {
                    Entry realFieldConversion = (Entry) realFieldConversionsEntryItr.next();
                    if (fieldConversions.containsKey(realFieldConversion.getValue())) {
                        combinedFieldConversions.put(realFieldConversion.getKey(), fieldConversions.get(realFieldConversion.getValue()));
                    }
                }
            }
            else {
                combinedFieldConversions = LookupUtils.translateFieldConversions(this.fieldConversions);
            }
        }
        else {
            combinedFieldConversions = fieldConversions;
        }
        while (searchResultItr.hasNext()) {
            PersistableBusinessObjectBase businessObject = (PersistableBusinessObjectBase) searchResultItr.next();
            workflowLookupableResults.add(Enhancer.create(businessObject.getClass(), new Class[] { WorkflowLookupableResult.class }, new WorkflowLookupableInvocationHandler(businessObject, new StringBuffer("<a href=\"").append(lookupable.getReturnUrl(businessObject, combinedFieldConversions, lookupableImplName)).append(" \">return value</a>").toString(), getLookupableClassLoader())));
        }
        return workflowLookupableResults;
    }

    protected ClassLoader getLookupableClassLoader() {
        if (Thread.currentThread().getContextClassLoader() == null) {
            return getClass().getClassLoader();
        }
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * This method wraps the business class name in a prefix and suffix that prevents a naming comflict.
     * 
     * @param businessClass
     * @return The lookupableImplName that WorkflowExtensionPoints will use to retrieve the appropriate bean.
     */
    public static String getLookupableImplName(Class businessClass) {
        return new StringBuffer(LOOKUPABLE_IMPL_NAME_PREFIX).append(businessClass.getName()).append(LOOKUPABLE_IMPL_NAME_SUFFIX).toString();
    }

    /**
     * This method appends the fieldConversions passed in surrounded by parentheses.
     * 
     * @param lookupableImplName
     * @param fieldConversions
     * @return The full lookupableName that WorkflowExtensionPoints expects.
     */
    public static String getLookupableName(String lookupableImplName, String fieldConversions) {
        return new StringBuffer(lookupableImplName).append("(").append(fieldConversions).append(")").toString();
    }

    /**
     * This method appends the fieldConversions passed in surrounded by parentheses.
     * 
     * @param lookupableImplName
     * @param fieldConversions
     * @param lookupParameters
     * @return The full lookupableName that WorkflowExtensionPoints expects.
     */
    public static String getLookupableName(String lookupableImplName, String fieldConversions, String lookupParameters) {
        return new StringBuffer(lookupableImplName).append("(").append(fieldConversions).append("|").append(lookupParameters).append(")").toString();
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getReturnLocation()
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getReturnLocation()
     */
    public String getReturnLocation() {
        return RETURN_LOCATION;
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getTitle()
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getTitle()
     */
    public String getTitle() {
        return lookupable.getTitle();
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getHtmlMenuBar()
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getHtmlMenuBar()
     */
    public String getHtmlMenuBar() {
        //  dont show the kuali 'create-new' menu bar in workflow lookups
        return "&nbsp;";
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getLookupInstructions()
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getLookupInstructions()
     */
    public String getLookupInstructions() {
        return lookupable.getLookupInstructions();
    }

    /**
     * We have hackety, hack, hacked in this method. We're getting the prior lookup form out of session, getting field values off it
     * and setting them on our rows, based on our lookupParameters attribute.
     * 
     * @see org.kuali.core.lookup.Lookupable#checkForAdditionalFields(java.util.Map)
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#checkForAdditionalFields(java.util.Map,
     *      javax.servlet.http.HttpServletRequest)
     */
    public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
        edu.iu.uis.eden.web.session.UserSession workflowUserSession = (edu.iu.uis.eden.web.session.UserSession) request.getSession().getAttribute(EdenConstants.USER_SESSION_KEY);
        if (GlobalVariables.getUserSession() == null) {
            GlobalVariables.setUserSession(new org.kuali.core.UserSession(workflowUserSession.getNetworkId()));
        }
        Object priorForm = workflowUserSession.retrieveObject(request.getParameter(Constants.DOC_FORM_KEY));
        if (priorForm instanceof LookupForm) {
            Map lookupParameters = LookupUtils.translateFieldConversions(this.lookupParameters);
            Iterator lookupParameterKeyItr = lookupParameters.keySet().iterator();
            while (lookupParameterKeyItr.hasNext()) {
                String priorLookupFieldName = (String) lookupParameterKeyItr.next();
                setFieldValue((String) lookupParameters.get(priorLookupFieldName), (String) ((LookupForm) priorForm).getFields().get(priorLookupFieldName));
            }
        }
        return lookupable.checkForAdditionalFields(fieldValues);
    }

    private void setFieldValue(String propertyName, String propertyValue) {
        Iterator rowItr = getRows().iterator();
        while (rowItr.hasNext()) {
            edu.iu.uis.eden.lookupable.Row row = (edu.iu.uis.eden.lookupable.Row) rowItr.next();
            Iterator fieldItr = row.getFields().iterator();
            while (fieldItr.hasNext()) {
                edu.iu.uis.eden.lookupable.Field field = (edu.iu.uis.eden.lookupable.Field) fieldItr.next();
                if (field.getPropertyName().equals(propertyName) && edu.iu.uis.eden.lookupable.Field.TEXT.equals(field.getFieldType())) {
                    field.setPropertyValue(propertyValue);
                    break;
                }
            }
        }
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getRows()
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getRows()
     */
    public List getRows() {
        return workflowRows;
    }

    /**
     * @see org.kuali.core.lookup.Lookupable#getColums()
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getColumns()
     */
    public List getColumns() {
        return workflowColumns;
    }

    /**
     * This method does nothing.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#changeIdToName(java.util.Map)
     */
    public void changeIdToName(Map fieldValues) throws Exception {
    }

    /**
     * This method always returns an empty list.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getDefaultReturnType()
     */
    public List getDefaultReturnType() {
        return new ArrayList();
    }

    /**
     * This method always returns the empty string.
     * 
     * @see edu.iu.uis.eden.plugin.attributes.WorkflowLookupable#getNoReturnParams(java.util.Map)
     */
    public String getNoReturnParams(Map fieldConversions) {
        return "";
    }

    /**
     * This method is for WorkflowExtensionPoints. It sets the fieldConversions attribute value.
     * 
     * @param fieldConversions The fieldConversions to set.
     */
    public void setFieldConversions(String fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    /**
     * This method is for WorkflowExtensionPoints. It sets the lookupParameters attribute value.
     * 
     * @param lookupParameters The lookupParameters to set.
     */
    public void setLookupParameters(String lookupParameters) {
        this.lookupParameters = lookupParameters;
    }

    /**
     * This method is for Spring. It sets the configurationService attribute value.
     * 
     * @param configurationService The configurationService to set.
     */
    public void setConfigurationService(KualiConfigurationService configurationService) {
        WorkflowLookupableImpl.configurationService = configurationService;
    }

    /**
     * This method is for Spring. It sets the businessObjectDictionaryService attribute value.
     * 
     * @param businessObjectDictionaryService The businessObjectDictionaryService to set.
     */
    public void setBusinessObjectDictionaryService(BusinessObjectDictionaryService businessObjectDictionaryService) {
        WorkflowLookupableImpl.businessObjectDictionaryService = businessObjectDictionaryService;
    }
}