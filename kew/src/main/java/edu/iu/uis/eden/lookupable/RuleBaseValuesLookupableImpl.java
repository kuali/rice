/*
 * Copyright 2005-2006 The Kuali Foundation.
 *
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
package edu.iu.uis.eden.lookupable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.definition.ObjectDefinition;
import org.kuali.rice.resourceloader.GlobalResourceLoader;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.WorkflowServiceErrorImpl;
import edu.iu.uis.eden.exception.EdenUserNotFoundException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.export.Exportable;
import edu.iu.uis.eden.plugin.attributes.OddSearchAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowAttribute;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.routetemplate.RuleAttribute;
import edu.iu.uis.eden.routetemplate.RuleBaseValues;
import edu.iu.uis.eden.routetemplate.RuleService;
import edu.iu.uis.eden.routetemplate.RuleTemplate;
import edu.iu.uis.eden.routetemplate.RuleTemplateAttribute;
import edu.iu.uis.eden.routetemplate.RuleTemplateService;
import edu.iu.uis.eden.routetemplate.xmlrouting.GenericXMLRuleAttribute;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.UserService;
import edu.iu.uis.eden.user.WorkflowUser;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.workgroup.GroupNameId;
import edu.iu.uis.eden.workgroup.WorkflowGroupId;
import edu.iu.uis.eden.workgroup.Workgroup;
import edu.iu.uis.eden.workgroup.WorkgroupService;

/**
 * A {@link WorkflowLookupable} implementation for {@link RuleBaseValues}.
 *
 * @see RuleBaseValues
 * @see RuleService
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class RuleBaseValuesLookupableImpl implements WorkflowLookupable, Exportable {

	private List rows;
	private List columns = establishColumns();
	private static final String title = "Rule Lookup";
	private static final String returnLocation = "Lookup.do";

	private static final String DOC_TYP_NAME_FIELD_LABEL = "Document Type Name";
	private static final String RULE_TEMPLATE_FIELD_LABEL = "Rule Template";
	private static final String WORKGROUP_FIELD_LABEL = "Workgroup Reviewer";
	private static final String PERSON_FIELD_LABEL = "Person Reviewer";
	private static final String PERSON_REVIEWER_TYPE_FIELD_LABEL = "Person Reviewer on Rule is";
	private static final String ROLE_FIELD_LABEL = "Role";
	private static final String ACTIVE_IND_FIELD_LABEL = "Active Indicator";
	private static final String DELEGATE_RULE_FIELD_LABEL = "Delegate Rule";
	private static final String RULE_ID_FIELD_LABEL = "Rule Id";
	private static final String RULE_DESC_FIELD_LABEL = "Rule Description";

	private static final String DOC_TYP_NAME_FIELD_HELP = "";
	private static final String RULE_TEMPLATE_FIELD_HELP = "";
	private static final String WORKGROUP_FIELD_HELP = "";
	private static final String PERSON_FIELD_HELP = "";
	private static final String PERSON_REVIEWER_TYPE_FIELD_HELP = "";
	private static final String ROLE_FIELD_HELP = "";
	private static final String ACTIVE_IND_FIELD_HELP = "";
	private static final String DELEGATE_RULE_FIELD_HELP = "";
	private static final String RULE_ID_FIELD_HELP = "";
	private static final String RULE_DESC_FIELD_HELP = "";

	private static final String DOC_TYP_NAME_PROPERTY_NAME = "docTypeFullName";
	private static final String RULE_TEMPLATE_PROPERTY_NAME = "ruleTemplateName";
	private static final String WORKGROUP_PROPERTY_NAME = "workgroupName";
	private static final String PERSON_PROPERTY_NAME = "networkId";
	private static final String PERSON_REVIEWER_TYPE_NAME = "userDirective";
	private static final String ROLE_PROPERTY_NAME = "roleName";
	private static final String DELEGATE_RULE_PROPERTY_NAME = "delegateRuleSearch";
	private static final String RULE_DESC_PROPERTY_NAME = "ruleDescription";
	private static final String RULE_DELEGATE_ONLY_PROPERTY_NAME = "ruleDelegationOnly";

	private static final String DOC_TYP_LOOKUPABLE = "DocumentTypeLookupableImplService";
	private static final String RULE_TEMPLATE_LOOKUPABLE = "RuleTemplateLookupableImplService";
	private static final String WORKGROUP_LOOKUPABLE = "WorkGroupLookupableImplService";
	private static final String PERSON_LOOKUPABLE = "UserLookupableImplService";
	private static final String ROLE_LOOKUPABLE = "RoleLookupableImplService";

	private static final String RULE_ID_PROPERTY_NAME = "ruleBaseValuesId";
	private static final String RULE_TEMPLATE_ID_PROPERTY_NAME = "ruleTemplate.ruleTemplateId";
	private static final String WORKGROUP_ID_PROPERTY_NAME = "workgroupId";

	private static final String ACTIVE_IND_PROPERTY_NAME = "activeInd";

	private static final String BACK_LOCATION = "backLocation";
	private static final String DOC_FORM_KEY = "docFormKey";

	public RuleBaseValuesLookupableImpl() {
		rows = new ArrayList();

		List fields = new ArrayList();
		fields.add(new Field(DOC_TYP_NAME_FIELD_LABEL, DOC_TYP_NAME_FIELD_HELP, Field.TEXT, true, DOC_TYP_NAME_PROPERTY_NAME, "", null, DOC_TYP_LOOKUPABLE));
		fields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, DOC_TYP_LOOKUPABLE));
		rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field("", "", Field.HIDDEN, true, RULE_TEMPLATE_PROPERTY_NAME, "", null, RULE_TEMPLATE_LOOKUPABLE));
		fields.add(new Field(RULE_TEMPLATE_FIELD_LABEL, RULE_TEMPLATE_FIELD_HELP, Field.QUICKFINDER, false, RULE_TEMPLATE_PROPERTY_NAME, "", null, RULE_TEMPLATE_LOOKUPABLE));
		fields.add(new Field("", "", Field.LOOKUP_RESULT_ONLY, true, RULE_TEMPLATE_ID_PROPERTY_NAME, "", null, RULE_TEMPLATE_LOOKUPABLE));
		rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field(RULE_DESC_FIELD_LABEL, RULE_DESC_FIELD_HELP, Field.TEXT, false, RULE_DESC_PROPERTY_NAME, "", null, null));
		rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field(WORKGROUP_FIELD_LABEL, WORKGROUP_FIELD_HELP, Field.TEXT, true, WORKGROUP_PROPERTY_NAME, "", null, WORKGROUP_LOOKUPABLE));
		fields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, WORKGROUP_LOOKUPABLE));
		fields.add(new Field("", "", Field.HIDDEN, false, RULE_DELEGATE_ONLY_PROPERTY_NAME, "", null, null));
		fields.add(new Field("", "", Field.LOOKUP_RESULT_ONLY, true, WORKGROUP_ID_PROPERTY_NAME, "", null, ""));
		rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field(PERSON_FIELD_LABEL, PERSON_FIELD_HELP, Field.TEXT, true, PERSON_PROPERTY_NAME, "", null, PERSON_LOOKUPABLE));
		fields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, PERSON_LOOKUPABLE));
		rows.add(new Row(fields));

		List options = new ArrayList();
	        options.add(new KeyLabelPair("user", "User"));
	        options.add(new KeyLabelPair("workgroup", "Workgroup Member"));
	        options.add(new KeyLabelPair("both", "Either"));

	        fields = new ArrayList();
	        fields.add(new Field(PERSON_REVIEWER_TYPE_FIELD_LABEL, PERSON_REVIEWER_TYPE_FIELD_HELP, Field.RADIO, false, PERSON_REVIEWER_TYPE_NAME, "user", options, null));
	        rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field(ROLE_FIELD_LABEL, ROLE_FIELD_HELP, Field.TEXT, true, ROLE_PROPERTY_NAME, "", null, ROLE_LOOKUPABLE));
		fields.add(new Field("", "", Field.QUICKFINDER, false, "", "", null, ROLE_LOOKUPABLE));
		rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field(RULE_ID_FIELD_LABEL, RULE_ID_FIELD_HELP, Field.TEXT, false, RULE_ID_PROPERTY_NAME, "", null, null));
		fields.add(new Field("", "", Field.HIDDEN, false, EdenConstants.DELEGATION_WIZARD, "", null, null));
		rows.add(new Row(fields));

		options = new ArrayList();
		options.add(new KeyLabelPair("true", "Active"));
		options.add(new KeyLabelPair("false", "Inactive"));
		options.add(new KeyLabelPair("ALL", "Show All"));

		fields = new ArrayList();
		fields.add(new Field(ACTIVE_IND_FIELD_LABEL, ACTIVE_IND_FIELD_HELP, Field.RADIO, false, ACTIVE_IND_PROPERTY_NAME, "true", options, null));
		rows.add(new Row(fields));

		options = new ArrayList();
		options.add(new KeyLabelPair("false", "Non-Delegates"));
		options.add(new KeyLabelPair("true", "Delegates"));
		options.add(new KeyLabelPair("ALL", "Show All"));

		fields = new ArrayList();
		fields.add(new Field(DELEGATE_RULE_FIELD_LABEL, DELEGATE_RULE_FIELD_HELP, Field.RADIO, true, DELEGATE_RULE_PROPERTY_NAME, "false", options, null));
		rows.add(new Row(fields));

	}

	private List establishColumns() {
		List columnList = new ArrayList();
		columnList.add(new Column("Rule Id", Column.COLUMN_IS_SORTABLE_VALUE, "ruleBaseValuesId"));
		columnList.add(new Column("Document Type Name", Column.COLUMN_IS_SORTABLE_VALUE, "docTypeName"));
		columnList.add(new Column("Rule Template Name", Column.COLUMN_IS_SORTABLE_VALUE, "ruleTemplateName"));
		columnList.add(new Column("Description", Column.COLUMN_IS_SORTABLE_VALUE, "description"));
		columnList.add(new Column("Active", Column.COLUMN_IS_SORTABLE_VALUE, "activeIndDisplay"));
		columnList.add(new Column("Delegate Rule", Column.COLUMN_IS_SORTABLE_VALUE, "delegateRule"));
		columnList.add(new Column("Actions", Column.COLUMN_NOT_SORTABLE_VALUE, "destinationUrl"));
		return columnList;
	}

	private void setFieldValue(String name, String value) {
		for (Iterator iter = getRows().iterator(); iter.hasNext();) {
			Row row = (Row) iter.next();
			for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
				Field field = (Field) iterator.next();
				if (field.getPropertyName() != null && !field.getPropertyName().equals("")) {
					if (name.equals(field.getPropertyName())) {
						field.setPropertyValue(value);
					}
				}
			}
		}
	}

	public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
		String ruleTemplateNameParam = (String) fieldValues.get(RULE_TEMPLATE_PROPERTY_NAME);
		String ruleTemplateIdParam = (String) fieldValues.get(RULE_TEMPLATE_ID_PROPERTY_NAME);

		if (ruleTemplateNameParam != null && !ruleTemplateNameParam.equals("") || ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam) && !"null".equals(ruleTemplateIdParam)) {
			RuleTemplate ruleTemplate = null;
			if (ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam) && !"null".equals(ruleTemplateIdParam)) {
				ruleTemplate = getRuleTemplateService().findByRuleTemplateId(new Long(ruleTemplateIdParam));
			} else {
				ruleTemplate = getRuleTemplateService().findByRuleTemplateName(ruleTemplateNameParam);
			}

			int i = 0;
			for (Iterator iter = ruleTemplate.getActiveRuleTemplateAttributes().iterator(); iter.hasNext();) {
				RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
				if (!ruleTemplateAttribute.isWorkflowAttribute()) {
					continue;
				}
				WorkflowAttribute attribute = ruleTemplateAttribute.getWorkflowAttribute();

				RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
				if (ruleAttribute.getType().equals(EdenConstants.RULE_XML_ATTRIBUTE_TYPE)) {
					((GenericXMLRuleAttribute) attribute).setRuleAttribute(ruleAttribute);
				}
				// run through the attributes fields once to populate field values we have to do this
				// to allow rows dependent on another row value to populate correctly in the loop below
				List searchRows = null;
				if (attribute instanceof OddSearchAttribute) {
					searchRows = ((OddSearchAttribute) attribute).getSearchRows();
				} else {
					searchRows = attribute.getRuleRows();
				}
				for (Iterator iterator = searchRows.iterator(); iterator.hasNext();) {
					Row row = (Row) iterator.next();
					List fields = new ArrayList();
					for (Iterator iterator2 = row.getFields().iterator(); iterator2.hasNext();) {
						Field field = (Field) iterator2.next();
						if (request.getParameter(field.getPropertyName()) != null) {
							field.setPropertyValue(request.getParameter(field.getPropertyName()));
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
					List fields = new ArrayList();
					for (Iterator iterator2 = row.getFields().iterator(); iterator2.hasNext();) {
						Field field = (Field) iterator2.next();
						if (request.getParameter(field.getPropertyName()) != null) {
							field.setPropertyValue(request.getParameter(field.getPropertyName()));
						}
						fields.add(field);
						fieldValues.put(field.getPropertyName(), field.getPropertyValue());
					}
					row.setFields(fields);
					rows.add(row);

				}

				if (attribute.getRuleRows() != null) {
					for (Iterator iterator = attribute.getRuleRows().iterator(); iterator.hasNext();) {
						Row row = (Row) iterator.next();
						for (Iterator iterator2 = row.getFields().iterator(); iterator2.hasNext();) {
							Field field = (Field) iterator2.next();
							if (field.getFieldType().equals(Field.TEXT) || field.getFieldType().equals(Field.DROPDOWN) || field.getFieldType().equals(Field.DROPDOWN_REFRESH) || field.getFieldType().equals(Field.RADIO)) {
								getColumns().add(i + 6, new Column(field.getFieldLabel(), Column.COLUMN_IS_SORTABLE_VALUE, "myColumns.columns[" + i + "].label"));
								i++;
							}
						}
					}
				}
			}

			return true;
		}
		return false;
	}

	public void changeIdToName(Map fieldValues) {
		String ruleTemplateIdParam = (String) fieldValues.get(RULE_TEMPLATE_ID_PROPERTY_NAME);
		String ruleTemplateNameParam = (String) fieldValues.get(RULE_TEMPLATE_PROPERTY_NAME);
		String workgroupIdParam = (String) fieldValues.get(WORKGROUP_ID_PROPERTY_NAME);
		String workgroupNameParam = (String) fieldValues.get(WORKGROUP_PROPERTY_NAME);

		if (workgroupNameParam != null && !workgroupNameParam.trim().equals("") || workgroupIdParam != null && !"".equals(workgroupIdParam) && !"null".equals(workgroupIdParam)) {
			Workgroup workgroup = null;
			if (workgroupIdParam != null && !"".equals(workgroupIdParam)) {
				workgroup = getWorkgroupService().getWorkgroup(new WorkflowGroupId(new Long(workgroupIdParam.trim())));
			} else {
				workgroup = getWorkgroupService().getWorkgroup(new GroupNameId(workgroupNameParam.trim()));
			}
			if (workgroup != null) {
				setFieldValue(WORKGROUP_PROPERTY_NAME, workgroup.getGroupNameId().getNameId());
			}
		} else {
			setFieldValue(WORKGROUP_PROPERTY_NAME, "");
		}

		if (ruleTemplateNameParam != null && !ruleTemplateNameParam.trim().equals("") || ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam) && !"null".equals(ruleTemplateIdParam)) {
			RuleTemplate ruleTemplate = null;
			if (ruleTemplateIdParam != null && !"".equals(ruleTemplateIdParam)) {
				ruleTemplate = getRuleTemplateService().findByRuleTemplateId(new Long(ruleTemplateIdParam));
			} else {
				ruleTemplate = getRuleTemplateService().findByRuleTemplateName(ruleTemplateNameParam.trim());
			}
			if (ruleTemplate != null) {
				setFieldValue(RULE_TEMPLATE_PROPERTY_NAME, ruleTemplate.getName());
			}
		} else {
			setFieldValue(RULE_TEMPLATE_PROPERTY_NAME, "");
		}
	}

	/**
	 * getSearchResults - searches for a RuleBaseValues information based on the criteria passed in by the map.
	 *
	 * @return Returns a list of RuleBaseValues objects that match the result.
	 */
	public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception {
		List errors = new ArrayList();

		String docTypeNameParam = (String) fieldValues.get(DOC_TYP_NAME_PROPERTY_NAME);
		String ruleTemplateIdParam = (String) fieldValues.get(RULE_TEMPLATE_ID_PROPERTY_NAME);
		String ruleTemplateNameParam = (String) fieldValues.get(RULE_TEMPLATE_PROPERTY_NAME);
		String workgroupIdParam = (String) fieldValues.get(WORKGROUP_ID_PROPERTY_NAME);
		String workgroupNameParam = (String) fieldValues.get(WORKGROUP_PROPERTY_NAME);
		String networkIdParam = (String) fieldValues.get(PERSON_PROPERTY_NAME);
		String userDirectiveParam = (String) fieldValues.get(PERSON_REVIEWER_TYPE_NAME);
		String roleNameParam = (String) fieldValues.get(ROLE_PROPERTY_NAME);
		String activeParam = (String) fieldValues.get(ACTIVE_IND_PROPERTY_NAME);
		String delegateRuleParam = (String) fieldValues.get(DELEGATE_RULE_PROPERTY_NAME);
		String ruleIdParam = (String) fieldValues.get(RULE_ID_PROPERTY_NAME);
		String delegationWizard = (String) fieldValues.get(EdenConstants.DELEGATION_WIZARD);
		String ruleDescription = (String) fieldValues.get(RULE_DESC_PROPERTY_NAME);

		String ruleBaseValueIdReturn = (String) fieldConversions.get(RULE_ID_PROPERTY_NAME);
		String ruleDelegationOnly = (String) fieldValues.get(RULE_DELEGATE_ONLY_PROPERTY_NAME);

		String docTypeSearchName = null;
		String workflowId = null;
		Long workgroupId = null;
		Long ruleTemplateId = null;
		Boolean isDelegateRule = null;
		Boolean isActive = null;
		Long ruleId = null;

		if (!delegateRuleParam.equals("ALL")) {
			isDelegateRule = new Boolean(delegateRuleParam);
		}

		if (ruleIdParam != null && !"".equals(ruleIdParam.trim())) {
			try {
				ruleId = new Long(ruleIdParam.trim());
			} catch (NumberFormatException e) {
				ruleId = new Long(-1);
			}
		}

		if (!activeParam.equals("ALL")) {
			isActive = new Boolean(activeParam);
		}

		if (docTypeNameParam != null && !"".equals(docTypeNameParam.trim())) {
			docTypeSearchName = docTypeNameParam.replace('*', '%');
			docTypeSearchName = "%" + docTypeSearchName.trim() + "%";
		}

		if (workgroupNameParam != null && !workgroupNameParam.trim().equals("") || workgroupIdParam != null && !"".equals(workgroupIdParam) && !"null".equals(workgroupIdParam)) {
			Workgroup workgroup = null;
			if (workgroupIdParam != null && !"".equals(workgroupIdParam)) {
				workgroupId = new Long(workgroupIdParam.trim());
				workgroup = getWorkgroupService().getWorkgroup(new WorkflowGroupId(workgroupId));
			} else {
				workgroup = getWorkgroupService().getWorkgroup(new GroupNameId(workgroupNameParam.trim()));
				if (workgroup == null) {
					errors.add(new WorkflowServiceErrorImpl("Document Type Invalid", "routetemplate.ruleservice.workgroup.invalid"));
				} else {
					workgroupId = new Long(workgroup.getWorkflowGroupId().getGroupId().longValue());
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
				WorkflowAttribute attribute = (WorkflowAttribute)GlobalResourceLoader.getObject(new ObjectDefinition(ruleTemplateAttribute.getRuleAttribute().getClassName(), ruleTemplateAttribute.getRuleAttribute().getMessageEntity()));//SpringServiceLocator.getExtensionService().getWorkflowAttribute(ruleTemplateAttribute.getRuleAttribute().getClassName());
				RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
				if (ruleAttribute.getType().equals(EdenConstants.RULE_XML_ATTRIBUTE_TYPE)) {
					((GenericXMLRuleAttribute) attribute).setRuleAttribute(ruleAttribute);
				}
				attribute.setRequired(false);
				List searchRows = null;
				if (attribute instanceof OddSearchAttribute) {
					errors.addAll(((OddSearchAttribute) attribute).validateSearchData(fieldValues));
					searchRows = ((OddSearchAttribute) attribute).getSearchRows();
				} else {
					errors.addAll(attribute.validateRuleData(fieldValues));
					searchRows = attribute.getRuleRows();
				}
				for (Iterator iterator = searchRows.iterator(); iterator.hasNext();) {
					Row row = (Row) iterator.next();
					for (Iterator iterator2 = row.getFields().iterator(); iterator2.hasNext();) {
						Field field = (Field) iterator2.next();
						if (fieldValues.get(field.getPropertyName()) != null) {
							String attributeParam = (String) fieldValues.get(field.getPropertyName());
							if (!attributeParam.equals("")) {
								if (ruleAttribute.getType().equals(EdenConstants.RULE_XML_ATTRIBUTE_TYPE)) {
									attributes.put(field.getPropertyName(), attributeParam.trim());
								} else if (!Utilities.isEmpty(field.getDefaultLookupableName())) {
									attributes.put(field.getDefaultLookupableName(), attributeParam.trim());
								} else {
									attributes.put(field.getPropertyName(), attributeParam.trim());
								}
							}
						}
						if (field.getFieldType().equals(Field.TEXT) || field.getFieldType().equals(Field.DROPDOWN) || field.getFieldType().equals(Field.DROPDOWN_REFRESH) || field.getFieldType().equals(Field.RADIO)) {
							if (ruleAttribute.getType().equals(EdenConstants.RULE_XML_ATTRIBUTE_TYPE)) {
								myColumns.getColumns().add(new KeyLabelPair(field.getPropertyName(), ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
							} else if (!Utilities.isEmpty(field.getDefaultLookupableName())) {
								myColumns.getColumns().add(new KeyLabelPair(field.getDefaultLookupableName(), ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
							} else {
								myColumns.getColumns().add(new KeyLabelPair(field.getPropertyName(), ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
							}
						}
					}
				}
			}
		}

		if (networkIdParam != null && !"".equals(networkIdParam.trim())) {
			try {
				WorkflowUser user = getUserService().getWorkflowUser(new AuthenticationUserId(networkIdParam.trim()));
				workflowId = user.getWorkflowUserId().getWorkflowId();
			} catch (EdenUserNotFoundException e) {
				errors.add(new WorkflowServiceErrorImpl("User Invalid", "routetemplate.ruleservice.user.invalid"));
			}
		}
		if (roleNameParam != null && !"".equals(roleNameParam)) {
			roleNameParam = roleNameParam.replace('*', '%');
			roleNameParam = "%" + roleNameParam.trim() + "%";
		}

		if (!Utilities.isEmpty(ruleDescription)) {
			ruleDescription = ruleDescription.replace('*', '%');
			ruleDescription = "%" + ruleDescription.trim() + "%";
		}

		if (!errors.isEmpty()) {
			throw new WorkflowServiceErrorException("RuleBaseValues validation errors", errors);
		}

		Iterator rules = getRuleService().search(docTypeSearchName, ruleId, ruleTemplateId, ruleDescription, workgroupId, workflowId, roleNameParam, isDelegateRule, isActive, attributes, userDirectiveParam).iterator();
		List displayList = new ArrayList();

		while (rules.hasNext()) {
			RuleBaseValues record = (RuleBaseValues) rules.next();

			if (Utilities.isEmpty(record.getDescription())) {
				record.setDescription(EdenConstants.HTML_NON_BREAKING_SPACE);
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
						newPair.setLabel(EdenConstants.HTML_NON_BREAKING_SPACE);
					}
					myNewColumns.getColumns().add(newPair);
				}
				record.setMyColumns(myNewColumns);
			}

			StringBuffer returnUrl = new StringBuffer("<a href=\"");
			returnUrl.append(fieldValues.get(BACK_LOCATION)).append("?methodToCall=refresh&docFormKey=").append(fieldValues.get(DOC_FORM_KEY)).append("&");
			if (!Utilities.isEmpty(ruleBaseValueIdReturn)) {
				returnUrl.append(ruleBaseValueIdReturn);
			} else {
				returnUrl.append(RULE_ID_PROPERTY_NAME);
			}
			returnUrl.append("=").append(record.getRuleBaseValuesId()).append("\">return value</a>");
			record.setReturnUrl(returnUrl.toString());

			Boolean isDelegationWizardry = new Boolean(delegationWizard);
			String destinationUrl = "<a href=\"Rule.do?methodToCall=report&currentRuleId=" + record.getRuleBaseValuesId() + "\">report</a> |";
			if (!isDelegationWizardry.booleanValue()) {
				destinationUrl += " <a href=\"Rule.do?methodToCall=edit&currentRuleId=" + record.getRuleBaseValuesId() + "\" >edit</a>";
			} else {
				destinationUrl += " <a href=\"DelegateRule.do?methodToCall=start" + "&parentRule.getDocTypeName=" + record.getDocTypeName();
				if (record.getRuleTemplate().getDelegationTemplateId() != null) {
					destinationUrl += "&ruleCreationValues.ruleTemplateId=" + record.getRuleTemplate().getDelegationTemplateId();
				}
				if (record.getRuleTemplate().getDelegationTemplate() != null) {
					destinationUrl += "&ruleCreationValues.ruleTemplateName=" + record.getRuleTemplate().getDelegationTemplate().getName();
				}
				destinationUrl += "&ruleCreationValues.ruleId=" + record.getRuleBaseValuesId() + "\" >next</a>";
			}
			record.setDestinationUrl(destinationUrl);
			if (!Utilities.isEmpty(ruleDelegationOnly) && ruleDelegationOnly.equals("true")) {
				if (record.getRuleTemplate() != null && record.getRuleTemplate().getDelegationTemplate() != null) {
					displayList.add(record);
				}
			} else {
				displayList.add(record);
			}
		}
		return displayList;
	}

	public List getDefaultReturnType() {
		List returnTypes = new ArrayList();
		returnTypes.add(RULE_ID_PROPERTY_NAME);
		return returnTypes;
	}

	public String getLookupInstructions() {
		return Utilities.getApplicationConstant(EdenConstants.RULE_SEARCH_INSTRUCTION_KEY);
	}

	public String getNoReturnParams(Map fieldConversions) {
		String ruleBaseValueIdReturn = (String) fieldConversions.get(RULE_ID_PROPERTY_NAME);
		StringBuffer noReturnParams = new StringBuffer("&");
		if (!Utilities.isEmpty(ruleBaseValueIdReturn)) {
			noReturnParams.append(ruleBaseValueIdReturn);
		} else {
			noReturnParams.append(RULE_ID_PROPERTY_NAME);
		}
		noReturnParams.append("=");

		return noReturnParams.toString();
	}

	public String getTitle() {
		return title;
	}

	public String getReturnLocation() {
		return returnLocation;
	}

	public List getColumns() {
		return columns;
	}

	public String getHtmlMenuBar() {
		String menuBar = "<a href=\"Rule.do\" >Create new Rule</a>";
		menuBar += " | <a href=\"DelegateRule.do\">Create Delegation</a>";
		return menuBar;
	}

	public List getRows() {
		return rows;
	}

	public ExportDataSet export(ExportFormat format, Object exportCriteria) throws Exception {
        List searchResults = (List)exportCriteria;
        ExportDataSet dataSet = new ExportDataSet(format);
        dataSet.getRules().addAll(searchResults);
        return dataSet;
    }

    public List getSupportedExportFormats() {
        return EdenConstants.STANDARD_FORMATS;
    }

    private RuleTemplateService getRuleTemplateService() {
		return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
	}

	private WorkgroupService getWorkgroupService() {
		return (WorkgroupService) KEWServiceLocator.getService(KEWServiceLocator.WORKGROUP_SRV);
	}

	private UserService getUserService() {
		return (UserService) KEWServiceLocator.getService(KEWServiceLocator.USER_SERVICE);
	}

	private RuleService getRuleService() {
		return (RuleService) KEWServiceLocator.getService(KEWServiceLocator.RULE_SERVICE);
	}

}