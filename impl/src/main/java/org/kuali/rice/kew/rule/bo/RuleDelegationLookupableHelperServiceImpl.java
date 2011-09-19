/*
 * Copyright 2007-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kew.rule.bo;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.reflect.ObjectDefinition;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.BooleanFormatter;
import org.kuali.rice.core.web.format.CollectionFormatter;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.lookupable.MyColumns;
import org.kuali.rice.kew.rule.OddSearchAttribute;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.RuleDelegationBo;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.service.RuleDelegationService;
import org.kuali.rice.kew.rule.service.RuleTemplateService;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class RuleDelegationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private List<Row> rows = new ArrayList<Row>();
    //private List<Column> columns = establishColumns();
    //private Long previousRuleTemplateId;
    private static final String PARENT_RESPONSIBILITY_ID_PROPERTY_NAME = "responsibilityId";
    private static final String PARENT_RULE_ID_PROPERTY_NAME = "ruleResponsibility.ruleBaseValues.ruleBaseValuesId";
    private static final String RULE_TEMPLATE_PROPERTY_NAME = "delegationRuleBaseValues.ruleTemplate.name";
    private static final String RULE_ID_PROPERTY_NAME = "delegationRuleBaseValues.ruleBaseValuesId";
    private static final String ACTIVE_IND_PROPERTY_NAME = "delegationRuleBaseValues.active";
    private static final String DELEGATION_PROPERTY_NAME = "delegationType";
    private static final String GROUP_REVIEWER_PROPERTY_NAME = "delegationRuleBaseValues.groupReviewer";
    private static final String GROUP_REVIEWER_NAME_PROPERTY_NAME = "delegationRuleBaseValues.groupReviewerName";
    private static final String GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME = "delegationRuleBaseValues.groupReviewerNamespace";
    private static final String PERSON_REVIEWER_PROPERTY_NAME = "delegationRuleBaseValues.personReviewer";
    private static final String PERSON_REVIEWER_TYPE_PROPERTY_NAME = "delegationRuleBaseValues.personReviewerType";
    private static final String DOC_TYP_NAME_PROPERTY_NAME = "delegationRuleBaseValues.documentType.name";
    private static final String RULE_DESC_PROPERTY_NAME = "delegationRuleBaseValues.description";

    private static final String BACK_LOCATION = "backLocation";
    private static final String DOC_FORM_KEY = "docFormKey";
    private static final String INVALID_WORKGROUP_ERROR = "The Group Reviewer Namespace and Name combination is not valid";
    private static final String INVALID_PERSON_ERROR = "The Person Reviewer is not valid";

    @Override
	public List<Row> getRows() {
        List<Row> superRows = super.getRows();
        List<Row> returnRows = new ArrayList<Row>();
        returnRows.addAll(superRows);
        returnRows.addAll(rows);
        return returnRows;
    }

    @Override
    public boolean checkForAdditionalFields(Map fieldValues) {
        String ruleTemplateNameParam = (String) fieldValues.get(RULE_TEMPLATE_PROPERTY_NAME);

        if (ruleTemplateNameParam != null && !ruleTemplateNameParam.equals("")) {
            rows = new ArrayList<Row>();
            RuleTemplateBo ruleTemplate = null;

            ruleTemplate = getRuleTemplateService().findByRuleTemplateName(ruleTemplateNameParam);

            for (Object element : ruleTemplate.getActiveRuleTemplateAttributes()) {
                RuleTemplateAttributeBo ruleTemplateAttribute = (RuleTemplateAttributeBo) element;
                if (!ruleTemplateAttribute.isWorkflowAttribute()) {
                    continue;
                }
                WorkflowAttribute attribute = ruleTemplateAttribute.getWorkflowAttribute();
                RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
                if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                    ((GenericXMLRuleAttribute) attribute).setRuleAttribute(ruleAttribute);
                }
                // run through the attributes fields once to populate field values we have to do this
                // to allow rows dependent on another row value to populate correctly in the loop below
                List<Row> searchRows = null;
                if (attribute instanceof OddSearchAttribute) {
                    searchRows = ((OddSearchAttribute) attribute).getSearchRows();
                } else {
                    searchRows = attribute.getRuleRows();
                }
                for (Row row : searchRows) {
                    List<Field> fields = new ArrayList<Field>();
                    for (Field field2 : row.getFields()) {
                        Field field = field2;
                        if (fieldValues.get(field.getPropertyName()) != null) {
                            field.setPropertyValue(fieldValues.get(field.getPropertyName()));
                        }
                        fields.add(field);
                        fieldValues.put(field.getPropertyName(), field.getPropertyValue());
                    }
                }

                if (attribute instanceof OddSearchAttribute) {
                    ((OddSearchAttribute) attribute).validateSearchData(fieldValues);
                } else {
                    attribute.validateRuleData(fieldValues);// populate attribute
                }

                if (attribute instanceof OddSearchAttribute) {
                    searchRows = ((OddSearchAttribute) attribute).getSearchRows();
                } else {
                    searchRows = attribute.getRuleRows();
                }
                for (Object element2 : searchRows) {
                    Row row = (Row) element2;
                    List<Field> fields = new ArrayList<Field>();
                    for (Field field : row.getFields()) {
                        if (fieldValues.get(field.getPropertyName()) != null) {
                            field.setPropertyValue(fieldValues.get(field.getPropertyName()));
                        }
                        fields.add(field);
                        fieldValues.put(field.getPropertyName(), field.getPropertyValue());
                    }
                    row.setFields(fields);
                    rows.add(row);

                }

            }

            return true;
        }
        rows.clear();
        return false;
    }

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List errors = new ArrayList();

        String parentRuleBaseValueId = fieldValues.get(PARENT_RULE_ID_PROPERTY_NAME);
        String parentResponsibilityId = fieldValues.get(PARENT_RESPONSIBILITY_ID_PROPERTY_NAME);
        String docTypeNameParam = fieldValues.get(DOC_TYP_NAME_PROPERTY_NAME);
        String ruleTemplateIdParam = null;//(String) fieldValues.get(RULE_TEMPLATE_ID_PROPERTY_NAME);
        String ruleTemplateNameParam = fieldValues.get(RULE_TEMPLATE_PROPERTY_NAME);
        String groupIdParam = fieldValues.get(GROUP_REVIEWER_PROPERTY_NAME);
        String groupNameParam = fieldValues.get(GROUP_REVIEWER_NAME_PROPERTY_NAME);
        String groupNamespaceParam = fieldValues.get(GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME);
        String networkIdParam = fieldValues.get(PERSON_REVIEWER_PROPERTY_NAME);
        String userDirectiveParam = fieldValues.get(PERSON_REVIEWER_TYPE_PROPERTY_NAME);
        String activeParam = fieldValues.get(ACTIVE_IND_PROPERTY_NAME);
        String delegationParam = fieldValues.get(DELEGATION_PROPERTY_NAME);
        String ruleIdParam = fieldValues.get(RULE_ID_PROPERTY_NAME);
        fieldValues.get(KEWConstants.DELEGATION_WIZARD);
        String ruleDescription = fieldValues.get(RULE_DESC_PROPERTY_NAME);

        String docTypeSearchName = null;
        String workflowId = null;
        String workgroupId = null;
        String ruleTemplateId = null;
        Boolean isActive = null;
        String ruleId = null;

        if (ruleIdParam != null && !"".equals(ruleIdParam.trim())) {
            try {
                ruleId = ruleIdParam.trim();
            } catch (NumberFormatException e) {
                // TODO: KULRICE-5201 - verify that this is a reasonable initialization given that ruleId is no longer a Long
            	ruleId = "-1";
            }
        }

        if (!activeParam.equals("")) {
            if (activeParam.equals("Y")) {
                isActive = Boolean.TRUE;
            } else {
                isActive = Boolean.FALSE;
            }
        }

        if (docTypeNameParam != null && !"".equals(docTypeNameParam.trim())) {
            docTypeSearchName = docTypeNameParam.replace('*', '%');
            docTypeSearchName = "%" + docTypeSearchName.trim() + "%";
        }

        if (!org.apache.commons.lang.StringUtils.isEmpty(groupIdParam) || !org.apache.commons.lang.StringUtils.isEmpty(groupNameParam)) {
            Group group = null;
            if (groupIdParam != null && !"".equals(groupIdParam)) {
                group = getGroupService().getGroup(groupIdParam.trim());
            } else {
                if (groupNamespaceParam == null) {
                    groupNamespaceParam = KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE;
                }
                group = getGroupService().getGroupByNameAndNamespaceCode(groupNamespaceParam, groupNameParam.trim());
                if (group == null) {
                    GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME, RiceKeyConstants.ERROR_CUSTOM, INVALID_WORKGROUP_ERROR);
                } else {
                    workgroupId = group.getId();
                }
            }
        }

        Map attributes = null;
        MyColumns myColumns = new MyColumns();
        if (ruleTemplateNameParam != null && !ruleTemplateNameParam.trim().equals("") || ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam) && !"null".equals(ruleTemplateIdParam)) {
            RuleTemplateBo ruleTemplate = null;
            if (ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam)) {
                ruleTemplateId = ruleTemplateIdParam;
                ruleTemplate = getRuleTemplateService().findByRuleTemplateId(ruleTemplateId);
            } else {
                ruleTemplate = getRuleTemplateService().findByRuleTemplateName(ruleTemplateNameParam.trim());
                ruleTemplateId = ruleTemplate.getId();
            }

            if(ruleTemplate == null){
                rows.clear();
                LOG.info("Returning empty result set for Delegation Rule Lookup because a RuleTemplate Name or ID was provided, but no valid RuleTemplates were retrieved by the service.");
                return new ArrayList<RuleDelegationBo>();
            }

            attributes = new HashMap();
            for (Object element : ruleTemplate.getActiveRuleTemplateAttributes()) {
                RuleTemplateAttributeBo ruleTemplateAttribute = (RuleTemplateAttributeBo) element;
                if (!ruleTemplateAttribute.isWorkflowAttribute()) {
                    continue;
                }
                WorkflowAttribute attribute = (WorkflowAttribute)GlobalResourceLoader.getObject(new ObjectDefinition(ruleTemplateAttribute.getRuleAttribute().getResourceDescriptor(), ruleTemplateAttribute.getRuleAttribute().getApplicationId()));//SpringServiceLocator.getExtensionService().getWorkflowAttribute(ruleTemplateAttribute.getRuleAttribute().getClassName());
                RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
                if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                    ((GenericXMLRuleAttribute) attribute).setRuleAttribute(ruleAttribute);
                }
                attribute.setRequired(false);
                List<Row> searchRows = null;
                if (attribute instanceof OddSearchAttribute) {
                    for (WorkflowServiceErrorImpl wsei : (List<WorkflowServiceErrorImpl>)((OddSearchAttribute)attribute).validateSearchData(fieldValues)) {
                        GlobalVariables.getMessageMap().putError(wsei.getMessage(), RiceKeyConstants.ERROR_CUSTOM, wsei.getArg1());
                    }
                    searchRows = ((OddSearchAttribute) attribute).getSearchRows();
                } else {
                    for (WorkflowServiceErrorImpl wsei : (List<WorkflowServiceErrorImpl>)attribute.validateRuleData(fieldValues)) {
                        GlobalVariables.getMessageMap().putError(wsei.getMessage(), RiceKeyConstants.ERROR_CUSTOM, wsei.getArg1());
                    }
                    searchRows = attribute.getRuleRows();
                }
                for (Row row : searchRows) {
                    for (Field field : row.getFields()) {
                        if (fieldValues.get(field.getPropertyName()) != null) {
                            String attributeParam = fieldValues.get(field.getPropertyName());
                            if (!attributeParam.equals("")) {
                                if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                                    attributes.put(field.getPropertyName(), attributeParam.trim());
                                } else {
                                    attributes.put(field.getPropertyName(), attributeParam.trim());
                                }
                            }
                        }
                        if (field.getFieldType().equals(Field.TEXT) || field.getFieldType().equals(Field.DROPDOWN) || field.getFieldType().equals(Field.DROPDOWN_REFRESH) || field.getFieldType().equals(Field.RADIO)) {
                            if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
                                myColumns.getColumns().add(new ConcreteKeyValue(field.getPropertyName(), ruleTemplateAttribute.getId()+""));
                            } else {
                                myColumns.getColumns().add(new ConcreteKeyValue(field.getPropertyName(), ruleTemplateAttribute.getId()+""));
                            }
                        }
                    }
                }
            }
        }

        if (!org.apache.commons.lang.StringUtils.isEmpty(ruleDescription)) {
            ruleDescription = ruleDescription.replace('*', '%');
            ruleDescription = "%" + ruleDescription.trim() + "%";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("errors in search criteria");
        }

        if(!org.apache.commons.lang.StringUtils.isEmpty(networkIdParam)){
        workflowId = networkIdParam;
        workflowId = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(networkIdParam).getPrincipalId();
        }

        Iterator<RuleDelegationBo> rules = getRuleDelegationService().search(parentRuleBaseValueId, parentResponsibilityId, docTypeSearchName, ruleId, ruleTemplateId, ruleDescription, workgroupId, workflowId, delegationParam, isActive, attributes, userDirectiveParam).iterator();
        List<RuleDelegationBo> displayList = new ArrayList<RuleDelegationBo>();

        while (rules.hasNext()) {
            RuleDelegationBo ruleDelegation = rules.next();
            RuleBaseValues record = ruleDelegation.getDelegationRule();

            if (org.apache.commons.lang.StringUtils.isEmpty(record.getDescription())) {
                record.setDescription(KEWConstants.HTML_NON_BREAKING_SPACE);
            }

            if (ruleTemplateNameParam != null && !ruleTemplateNameParam.trim().equals("") || ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam) && !"null".equals(ruleTemplateIdParam)) {
                MyColumns myNewColumns = new MyColumns();
                for (Object element : myColumns.getColumns()) {
                    KeyValue pair = (KeyValue) element;
                    final KeyValue newPair;
                    if (record.getRuleExtensionValue(pair.getKey(), pair.getKey().toString()) != null) {
                    	newPair = new ConcreteKeyValue(pair.getKey(), record.getRuleExtensionValue(pair.getValue(), pair.getKey().toString()).getValue());
                    } else {
                    	newPair = new ConcreteKeyValue(pair.getKey(), KEWConstants.HTML_NON_BREAKING_SPACE);
                    }
                    myNewColumns.getColumns().add(newPair);
                    record.getFieldValues().put(newPair.getKey(), newPair.getValue());
                }
                record.setMyColumns(myNewColumns);
            }

            StringBuffer returnUrl = new StringBuffer("<a href=\"");
            returnUrl.append(fieldValues.get(BACK_LOCATION)).append("?methodToCall=refresh&docFormKey=").append(fieldValues.get(DOC_FORM_KEY)).append("&");

            returnUrl.append(RULE_ID_PROPERTY_NAME);
            returnUrl.append("=").append(record.getId()).append("\">return value</a>");
            record.setReturnUrl(returnUrl.toString());

            String destinationUrl = "<a href=\"Rule.do?methodToCall=report&currentRuleId=" + record.getId() + "\">report</a>";

            record.setDestinationUrl(destinationUrl);

            displayList.add(ruleDelegation);
        }
        return displayList;

    }



    private GroupService getGroupService() {
       return KimApiServiceLocator.getGroupService();
    }

    private RuleTemplateService getRuleTemplateService() {
        return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
    }
    private RuleDelegationService getRuleDelegationService() {
        return (RuleDelegationService) KEWServiceLocator.getService(KEWServiceLocator.RULE_DELEGATION_SERVICE);
    }

    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        // make sure that if we have either groupName or Namespace, that both are filled in
        String groupName = (String)fieldValues.get(GROUP_REVIEWER_NAME_PROPERTY_NAME);
        String groupNamespace = (String)fieldValues.get(GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME);
        String personId = (String)fieldValues.get(PERSON_REVIEWER_PROPERTY_NAME);

        if (org.apache.commons.lang.StringUtils.isEmpty(groupName) && !org.apache.commons.lang.StringUtils.isEmpty(groupNamespace)) {
            String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), GROUP_REVIEWER_NAME_PROPERTY_NAME);
            GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAME_PROPERTY_NAME, RiceKeyConstants.ERROR_REQUIRED, attributeLabel);
        }

        if  (!org.apache.commons.lang.StringUtils.isEmpty(groupName) && org.apache.commons.lang.StringUtils.isEmpty(groupNamespace)) {
            String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME);
            GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME, RiceKeyConstants.ERROR_REQUIRED, attributeLabel);
        }

        if  (!org.apache.commons.lang.StringUtils.isEmpty(groupName) && !org.apache.commons.lang.StringUtils.isEmpty(groupNamespace)) {
            Group group = getGroupService().getGroupByNameAndNamespaceCode(groupNamespace, groupName);
            if (group == null) {
                GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAME_PROPERTY_NAME, RiceKeyConstants.ERROR_CUSTOM, INVALID_WORKGROUP_ERROR);
            }
        }

        if  (!org.apache.commons.lang.StringUtils.isEmpty(personId)) {
            //Person person = KIMServiceLocatorInternal.getPersonService().getPerson(personId);
            Person person = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(personId);/** IU fix EN-1552 */
            if (person == null) {
                GlobalVariables.getMessageMap().putError(PERSON_REVIEWER_PROPERTY_NAME, RiceKeyConstants.ERROR_CUSTOM, INVALID_PERSON_ERROR);
            }
        }
        if (!GlobalVariables.getMessageMap().hasNoErrors()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    @Override
    public Collection performLookup(LookupForm lookupForm,
            Collection resultTable, boolean bounded) {
        // TODO jjhanso - THIS METHOD NEEDS JAVADOCS
        //return super.performLookup(lookupForm, resultTable, bounded);
        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));
        Collection displayList;

        // call search method to get results
        if (bounded) {
            displayList = getSearchResults(lookupForm.getFieldsForLookup());
        }
        else {
            displayList = getSearchResultsUnbounded(lookupForm.getFieldsForLookup());
        }

        HashMap<String,Class> propertyTypes = new HashMap<String, Class>();

        boolean hasReturnableRow = false;

        List returnKeys = getReturnKeys();
        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        Person user = GlobalVariables.getUserSession().getPerson();

        // iterate through result list and wrap rows with return url and action urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();
            if(element instanceof PersistableBusinessObject){
                lookupForm.setLookupObjectId(((PersistableBusinessObject)element).getObjectId());
            }

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            HtmlData returnUrl = getReturnUrl(element, lookupForm, returnKeys, businessObjectRestrictions);

            String actionUrls = getActionUrls(element, pkNames, businessObjectRestrictions);
            //Fix for JIRA - KFSMI-2417
            if("".equals(actionUrls)){
                actionUrls = ACTION_URLS_EMPTY;
            }

            List<Column> columns = getColumns();
            for (Object element2 : columns) {

                Column col = (Column) element2;
                String curPropName = col.getPropertyName();
                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KRADConstants.EMPTY_STRING;
                Object prop = null;
                boolean skipPropTypeCheck = false;
                //try to get value elsewhere
                if (element instanceof RuleDelegationBo) {
                    prop = ((RuleDelegationBo)element).getDelegationRule().getFieldValues().get(curPropName);
                    skipPropTypeCheck = true;
                }
                if (prop == null) {
                    prop = ObjectUtils.getPropertyValue(element, curPropName);
                }

                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(curPropName);
                if ( propClass == null && !skipPropTypeCheck) {
                    try {
                        propClass = ObjectUtils.getPropertyType( element, curPropName, getPersistenceStructureService() );
                        propertyTypes.put( curPropName, propClass );
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot access PropertyType for property " + "'" + curPropName + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
                    }
                }

                // formatters
                if (prop != null) {
                    // for Booleans, always use BooleanFormatter
                    if (prop instanceof Boolean) {
                        formatter = new BooleanFormatter();
                    }

                    // for Dates, always use DateFormatter
                    if (prop instanceof Date) {
                        formatter = new DateFormatter();
                    }

                    // for collection, use the list formatter if a formatter hasn't been defined yet
                    if (prop instanceof Collection && formatter == null) {
                    formatter = new CollectionFormatter();
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

                propValue = maskValueIfNecessary(element.getClass(), curPropName, propValue, businessObjectRestrictions);

                col.setPropertyValue(propValue);

                if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, curPropName));

                }
            }

            ResultRow row = new ResultRow(columns, returnUrl.constructCompleteHtmlTag(), actionUrls);
            row.setRowId(returnUrl.getName());
            row.setReturnUrlHtmlData(returnUrl);
            // because of concerns of the BO being cached in session on the ResultRow,
            // let's only attach it when needed (currently in the case of export)
            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            if(element instanceof PersistableBusinessObject){
                row.setObjectId((((PersistableBusinessObject)element).getObjectId()));
            }


            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }

        lookupForm.setHasReturnableRow(hasReturnableRow);

        return displayList;
    }

    @Override
    public List<Column> getColumns() {
        List<Column> columns = super.getColumns();
        for (Row row : rows) {
            for (Field field : row.getFields()) {
                Column newColumn = new Column();
                newColumn.setColumnTitle(field.getFieldLabel());
                newColumn.setMaxLength(field.getMaxLength());
                newColumn.setPropertyName(field.getPropertyName());
                columns.add(newColumn);
            }
        }
        return columns;
    }

    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject,
            List pkNames) {
        RuleDelegationBo ruleDelegation = (RuleDelegationBo)businessObject;
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(ruleDelegation.getDelegationRule().getRuleTemplateName()) && StringUtils.isNotBlank(getMaintenanceDocumentTypeName())) {
        	if (allowsMaintenanceEditAction(businessObject)) {
        		htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        	}
            if (allowsMaintenanceNewOrCopyAction()) {
                htmlDataList.add(getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
            }
        }
            
        return htmlDataList;
    }



}
