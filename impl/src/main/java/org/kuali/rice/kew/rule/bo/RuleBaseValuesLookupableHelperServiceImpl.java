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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.reflect.ObjectDefinition;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.exception.WorkflowServiceErrorImpl;
import org.kuali.rice.kew.lookupable.MyColumns;
import org.kuali.rice.kew.rule.OddSearchAttribute;
import org.kuali.rice.kew.rule.RuleBaseValues;
import org.kuali.rice.kew.rule.WorkflowAttribute;
import org.kuali.rice.kew.rule.service.RuleService;
import org.kuali.rice.kew.rule.service.RuleTemplateService;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kew.util.Utilities;
import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.format.BooleanFormatter;
import org.kuali.rice.kns.web.format.CollectionFormatter;
import org.kuali.rice.kns.web.format.DateFormatter;
import org.kuali.rice.kns.web.format.Formatter;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.KeyLabelPair;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This is a description of what this class does - jjhanso don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class RuleBaseValuesLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private List<Row> rows = new ArrayList<Row>();
    //private List<Column> columns = establishColumns();
    //private Long previousRuleTemplateId;

    private static final String RULE_TEMPLATE_PROPERTY_NAME = "ruleTemplate.name";
    private static final String RULE_ID_PROPERTY_NAME = "ruleBaseValuesId";
    private static final String RULE_TEMPLATE_ID_PROPERTY_NAME = "ruleTemplateId";
    private static final String ACTIVE_IND_PROPERTY_NAME = "activeInd";
    private static final String DELEGATE_RULE_PROPERTY_NAME = "delegateRule";
    private static final String GROUP_REVIEWER_PROPERTY_NAME = "groupReviewer";
    private static final String GROUP_REVIEWER_NAME_PROPERTY_NAME = "groupReviewerName";
    private static final String GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME = "groupReviewerNamespace";
    private static final String PERSON_REVIEWER_PROPERTY_NAME = "personReviewer";
    private static final String ROLE_REVIEWER_PROPERTY_NAME = "roleReviewer";
    private static final String PERSON_REVIEWER_TYPE_PROPERTY_NAME = "personReviewerType";
    private static final String DOC_TYP_NAME_PROPERTY_NAME = "documentType.name";
    private static final String RULE_DESC_PROPERTY_NAME = "description";

    private static final String DOC_TYP_LOOKUPABLE = "DocumentTypeLookupableImplService";
    private static final String RULE_TEMPLATE_LOOKUPABLE = "RuleTemplateLookupableImplService";
    private static final String WORKGROUP_LOOKUPABLE = "WorkGroupLookupableImplService";
    private static final String PERSON_LOOKUPABLE = "UserLookupableImplService";

    private static final String WORKGROUP_ID_PROPERTY_NAME = "workgroupId";

    private static final String BACK_LOCATION = "backLocation";
    private static final String DOC_FORM_KEY = "docFormKey";
    private static final String INVALID_WORKGROUP_ERROR = "The Group Reviewer Namespace and Name combination is not valid";
    private static final String INVALID_PERSON_ERROR = "The Person Reviewer is not valid";

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
            RuleTemplate ruleTemplate = null;

            ruleTemplate = getRuleTemplateService().findByRuleTemplateName(ruleTemplateNameParam);

            int i = 0;
            for (Iterator iter = ruleTemplate.getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {
                RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
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
                for (Iterator<Row> iterator = searchRows.iterator(); iterator.hasNext();) {
                    Row row = iterator.next();
                    List<Field> fields = new ArrayList<Field>();
                    for (Iterator<Field> iterator2 = row.getFields().iterator(); iterator2.hasNext();) {
                        Field field = (Field) iterator2.next();
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
                for (Iterator iterator = searchRows.iterator(); iterator.hasNext();) {
                    Row row = (Row) iterator.next();
                    List<Field> fields = new ArrayList<Field>();
                    for (Iterator<Field> iterator2 = row.getFields().iterator(); iterator2.hasNext();) {
                        Field field = iterator2.next();
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

        String docTypeNameParam = (String) fieldValues.get(DOC_TYP_NAME_PROPERTY_NAME);
        String ruleTemplateIdParam = (String) fieldValues.get(RULE_TEMPLATE_ID_PROPERTY_NAME);
        String ruleTemplateNameParam = (String) fieldValues.get(RULE_TEMPLATE_PROPERTY_NAME);
        String groupIdParam = (String) fieldValues.get(GROUP_REVIEWER_PROPERTY_NAME);
        String groupNameParam = (String) fieldValues.get(GROUP_REVIEWER_NAME_PROPERTY_NAME);
        String groupNamespaceParam = (String) fieldValues.get(GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME);
        String networkIdParam = (String) fieldValues.get(PERSON_REVIEWER_PROPERTY_NAME);
        String userDirectiveParam = (String) fieldValues.get(PERSON_REVIEWER_TYPE_PROPERTY_NAME);
        String activeParam = (String) fieldValues.get(ACTIVE_IND_PROPERTY_NAME);
        String ruleIdParam = (String) fieldValues.get(RULE_ID_PROPERTY_NAME);
        String ruleDescription = (String) fieldValues.get(RULE_DESC_PROPERTY_NAME);

        String docTypeSearchName = null;
        String workflowId = null;
        String workgroupId = null;
        Long ruleTemplateId = null;
        Boolean isDelegateRule = null;
        Boolean isActive = null;
        Long ruleId = null;

        if (ruleIdParam != null && !"".equals(ruleIdParam.trim())) {
            try {
                ruleId = new Long(ruleIdParam.trim());
            } catch (NumberFormatException e) {
                ruleId = new Long(-1);
            }
        }

        if (!activeParam.equals("")) {
            if (activeParam.equals("Y")) {
                isActive = new Boolean(true);
            } else {
                isActive = new Boolean(false);
            }
        }

        if (docTypeNameParam != null && !"".equals(docTypeNameParam.trim())) {
            docTypeSearchName = docTypeNameParam.replace('*', '%');
            docTypeSearchName = "%" + docTypeSearchName.trim() + "%";
        }

        if (!Utilities.isEmpty(groupIdParam) || !Utilities.isEmpty(groupNameParam)) {
            Group group = null;
            if (groupIdParam != null && !"".equals(groupIdParam)) {
                group = getIdentityManagementService().getGroup(groupIdParam.trim());
            } else {
                if (groupNamespaceParam == null) {
                    groupNamespaceParam = KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE;
                }
                group = getIdentityManagementService().getGroupByName(groupNamespaceParam, groupNameParam.trim());
                if (group == null) {
                    String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), GROUP_REVIEWER_NAME_PROPERTY_NAME) + ":" + getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME);
                    GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME, RiceKeyConstants.ERROR_CUSTOM, INVALID_WORKGROUP_ERROR);
                } else {
                    workgroupId = group.getGroupId();
                }
            }
        }

        Map attributes = null;
        MyColumns myColumns = new MyColumns();
        if (ruleTemplateNameParam != null && !ruleTemplateNameParam.trim().equals("") || ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam) && !"null".equals(ruleTemplateIdParam)) {
            RuleTemplate ruleTemplate = null;
            if (ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam)) {
                ruleTemplateId = new Long(ruleTemplateIdParam);
                ruleTemplate = getRuleTemplateService().findByRuleTemplateId(ruleTemplateId);
            } else {
                ruleTemplate = getRuleTemplateService().findByRuleTemplateName(ruleTemplateNameParam.trim());
                ruleTemplateId = new Long(ruleTemplate.getRuleTemplateId().longValue());
            }

            attributes = new HashMap();
            for (Iterator iter = ruleTemplate.getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {
                RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
                if (!ruleTemplateAttribute.isWorkflowAttribute()) {
                    continue;
                }
                WorkflowAttribute attribute = (WorkflowAttribute)GlobalResourceLoader.getObject(new ObjectDefinition(ruleTemplateAttribute.getRuleAttribute().getClassName(), ruleTemplateAttribute.getRuleAttribute().getServiceNamespace()));//SpringServiceLocator.getExtensionService().getWorkflowAttribute(ruleTemplateAttribute.getRuleAttribute().getClassName());
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
                            String attributeParam = (String) fieldValues.get(field.getPropertyName());
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
                                myColumns.getColumns().add(new KeyLabelPair(field.getPropertyName(), ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
                            } else {
                                myColumns.getColumns().add(new KeyLabelPair(field.getPropertyName(), ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
                            }
                        }
                    }
                }
            }
        }

        if (!Utilities.isEmpty(ruleDescription)) {
            ruleDescription = ruleDescription.replace('*', '%');
            ruleDescription = "%" + ruleDescription.trim() + "%";
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("errors in search criteria");
        }

        Iterator rules = getRuleService().search(docTypeSearchName, ruleId, ruleTemplateId, ruleDescription, workgroupId, workflowId, isDelegateRule, isActive, attributes, userDirectiveParam).iterator();
        List displayList = new ArrayList();

        while (rules.hasNext()) {
            RuleBaseValues record = (RuleBaseValues) rules.next();

            if (Utilities.isEmpty(record.getDescription())) {
                record.setDescription(KEWConstants.HTML_NON_BREAKING_SPACE);
            }

            if (ruleTemplateNameParam != null && !ruleTemplateNameParam.trim().equals("") || ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam) && !"null".equals(ruleTemplateIdParam)) {
                MyColumns myNewColumns = new MyColumns();
                for (Iterator iter = myColumns.getColumns().iterator(); iter.hasNext();) {
                    KeyLabelPair pair = (KeyLabelPair) iter.next();
                    KeyLabelPair newPair = new KeyLabelPair();
                    newPair.setKey(pair.getKey());
                    if (record.getRuleExtensionValue(new Long(pair.getLabel()), pair.getKey().toString()) != null) {
                        newPair.setLabel(record.getRuleExtensionValue(new Long(pair.getLabel()), pair.getKey().toString()).getValue());
                    } else {
                        newPair.setLabel(KEWConstants.HTML_NON_BREAKING_SPACE);
                    }
                    myNewColumns.getColumns().add(newPair);
                    record.getFieldValues().put((String)newPair.key, newPair.label);
                }
                record.setMyColumns(myNewColumns);
            }

            StringBuffer returnUrl = new StringBuffer("<a href=\"");
            returnUrl.append(fieldValues.get(BACK_LOCATION)).append("?methodToCall=refresh&docFormKey=").append(fieldValues.get(DOC_FORM_KEY)).append("&");

            returnUrl.append(RULE_ID_PROPERTY_NAME);
            returnUrl.append("=").append(record.getRuleBaseValuesId()).append("\">return value</a>");
            record.setReturnUrl(returnUrl.toString());

            String destinationUrl = "<a href=\"Rule.do?methodToCall=report&currentRuleId=" + record.getRuleBaseValuesId() + "\">report</a>";

            record.setDestinationUrl(destinationUrl);

            displayList.add(record);
        }
        return displayList;

    }



    private IdentityManagementService getIdentityManagementService() {
       return (IdentityManagementService) KIMServiceLocator.getService(KIMServiceLocator.KIM_IDENTITY_MANAGEMENT_SERVICE);
    }

    private RuleTemplateService getRuleTemplateService() {
        return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
    }
    private RuleService getRuleService() {
        return (RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE);
    }

    @Override
    public void validateSearchParameters(Map fieldValues) {
        super.validateSearchParameters(fieldValues);

        // make sure that if we have either groupName or Namespace, that both are filled in
        String groupName = (String)fieldValues.get(GROUP_REVIEWER_NAME_PROPERTY_NAME);
        String groupNamespace = (String)fieldValues.get(GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME);
        String personId = (String)fieldValues.get(PERSON_REVIEWER_PROPERTY_NAME);

        if (Utilities.isEmpty(groupName) && !Utilities.isEmpty(groupNamespace)) {
            String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), GROUP_REVIEWER_NAME_PROPERTY_NAME);
            GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAME_PROPERTY_NAME, RiceKeyConstants.ERROR_REQUIRED, attributeLabel);
        }

        if  (!Utilities.isEmpty(groupName) && Utilities.isEmpty(groupNamespace)) {
            String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME);
            GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME, RiceKeyConstants.ERROR_REQUIRED, attributeLabel);
        }

        if  (!Utilities.isEmpty(groupName) && !Utilities.isEmpty(groupNamespace)) {
            Group group = KIMServiceLocator.getIdentityManagementService().getGroupByName(groupNamespace, groupName);
            if (group == null) {
                String attributeLabel =  getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), GROUP_REVIEWER_NAMESPACE_PROPERTY_NAME) + ":" + getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), GROUP_REVIEWER_NAME_PROPERTY_NAME);
                GlobalVariables.getMessageMap().putError(GROUP_REVIEWER_NAME_PROPERTY_NAME, RiceKeyConstants.ERROR_CUSTOM, INVALID_WORKGROUP_ERROR);
            }
        }

        if  (!Utilities.isEmpty(personId)) {
            Person person = KIMServiceLocator.getPersonService().getPerson(personId);
            if (person == null) {
                String attributeLabel = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), PERSON_REVIEWER_PROPERTY_NAME) + ":" + getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), PERSON_REVIEWER_PROPERTY_NAME);
                GlobalVariables.getMessageMap().putError(PERSON_REVIEWER_PROPERTY_NAME, RiceKeyConstants.ERROR_CUSTOM, INVALID_PERSON_ERROR);
            }
        }
        if (!GlobalVariables.getMessageMap().isEmpty()) {
            throw new ValidationException("errors in search criteria");
        }
    }

    @Override
    public Collection performLookup(LookupForm lookupForm,
            Collection resultTable, boolean bounded) {
        // TODO jjhanso - THIS METHOD NEEDS JAVADOCS
        //return super.performLookup(lookupForm, resultTable, bounded);
        setBackLocation((String) lookupForm.getFieldsForLookup().get(KNSConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KNSConstants.DOC_FORM_KEY));
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
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {

                Column col = (Column) iterator.next();
                Formatter formatter = col.getFormatter();

                // pick off result column from result list, do formatting
                String propValue = KNSConstants.EMPTY_STRING;
                Object prop = null;
                boolean skipPropTypeCheck = false;
                //try to get value elsewhere
                if (element instanceof RuleBaseValues) {
                    prop = ((RuleBaseValues)element).getFieldValues().get(col.getPropertyName());
                    skipPropTypeCheck = true;
                }
                if (prop == null) {
                    prop = ObjectUtils.getPropertyValue(element, col.getPropertyName());
                }

                // set comparator and formatter based on property type
                Class propClass = propertyTypes.get(col.getPropertyName());
                if ( propClass == null && !skipPropTypeCheck) {
                    try {
                        propClass = ObjectUtils.getPropertyType( element, col.getPropertyName(), getPersistenceStructureService() );
                        propertyTypes.put( col.getPropertyName(), propClass );
                    } catch (Exception e) {
                        throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", e);
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

                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);

                col.setPropertyValue(propValue);

                if (StringUtils.isNotBlank(propValue)) {
                    col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));

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
        RuleBaseValues ruleBaseValues = (RuleBaseValues)businessObject;
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        if (StringUtils.isNotBlank(ruleBaseValues.getRuleTemplateName()) && StringUtils.isNotBlank(getMaintenanceDocumentTypeName())) {
        	if (allowsMaintenanceEditAction(businessObject)) {
        		htmlDataList.add(getUrlData(businessObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames));
        	}
        	if (allowsMaintenanceNewOrCopyAction()) {
                htmlDataList.add(getUrlData(businessObject, KNSConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames));
            }
        }
        
        return htmlDataList;
    }



}
