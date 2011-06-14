/*
 * Copyright 2005-2007 The Kuali Foundation
 *
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
package org.kuali.rice.kew.rule.web;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.kuali.rice.core.framework.persistence.jdbc.sql.SQLUtils;
import org.kuali.rice.core.util.RiceConstants;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.rule.*;
import org.kuali.rice.kew.rule.bo.RuleAttribute;
import org.kuali.rice.kew.rule.bo.RuleTemplate;
import org.kuali.rice.kew.rule.bo.RuleTemplateAttribute;
import org.kuali.rice.kew.rule.service.RuleDelegationService;
import org.kuali.rice.kew.rule.service.RuleTemplateService;
import org.kuali.rice.kew.rule.xmlrouting.GenericXMLRuleAttribute;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.util.CodeTranslator;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.krad.web.ui.Field;
import org.kuali.rice.krad.web.ui.Row;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * A decorator around a {@link RuleBaseValues} object which provides some
 * convienance functions for interacting with the bean from the web-tier.
 * This helps to alleviate some of the weaknesses of JSTL.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class WebRuleBaseValues extends RuleBaseValues {

	private static final long serialVersionUID = 5938997470219200474L;
    private static final int TO_DATE_UPPER_LIMIT = 2100;
	private List rows = new ArrayList();
	private List fields = new ArrayList();
	private List roles = new ArrayList();
	private String fromDateValue;
	private String toDateValue;
	private String ruleTemplateName;
	private boolean hasExtensionValueErrors = false;

	public WebRuleBaseValues() {
	}

	public WebRuleBaseValues(RuleBaseValues rule) throws Exception {
		edit(rule);
	}

	private void loadFields() {
		fields.clear();
		if (getRuleTemplateId() != null) {
			RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(getRuleTemplateId());
			if (ruleTemplate != null) {
				List ruleTemplateAttributes = ruleTemplate.getActiveRuleTemplateAttributes();
				Collections.sort(ruleTemplateAttributes);
				for (Iterator iter = ruleTemplateAttributes.iterator(); iter.hasNext();) {
					RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
					if (!ruleTemplateAttribute.isWorkflowAttribute()) {
						continue;
					}
					WorkflowAttribute workflowAttribute = ruleTemplateAttribute.getWorkflowAttribute();
					RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
					if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
						((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
					}
					for (Object element : workflowAttribute.getRuleRows()) {
						Row row = (Row) element;
						for (Object element2 : row.getFields()) {
							Field field = (Field) element2;
							if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
								String fieldValue = "";
								RuleExtensionValue extensionValue = getRuleExtensionValue(ruleTemplateAttribute.getRuleTemplateAttributeId(), field.getPropertyName());
								if (extensionValue != null) {
									fieldValue = extensionValue.getValue();
								} else {
									fieldValue = field.getPropertyValue();
								}
								fields.add(new KeyValueId(field.getPropertyName(), fieldValue, ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
							//} else if (!org.apache.commons.lang.StringUtils.isEmpty(field.getDefaultLookupableName())) {
							//	String fieldValue = "";
							//	RuleExtensionValue extensionValue = getRuleExtensionValue(ruleTemplateAttribute.getRuleTemplateAttributeId(), (String) field.getDefaultLookupableName());
							//	if (extensionValue != null) {
							//		fieldValue = extensionValue.getValue();
							//	} else {
							//		fieldValue = field.getPropertyValue();
							//	}
							//	fields.add(new KeyValueId(field.getDefaultLookupableName(), fieldValue, ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
							} else {
								String fieldValue = "";
								RuleExtensionValue extensionValue = getRuleExtensionValue(ruleTemplateAttribute.getRuleTemplateAttributeId(), field.getPropertyName());
								if (extensionValue != null) {
									fieldValue = extensionValue.getValue();
								} else {
									fieldValue = field.getPropertyValue();
								}
								fields.add(new KeyValueId(field.getPropertyName(), fieldValue, ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
							}

							// if (workflowAttribute.getFieldConversions() !=
							// null &&
							// !workflowAttribute.getFieldConversions().isEmpty())
							// {
							// boolean found = false;
							// for (Iterator iterator4 =
							// workflowAttribute.getFieldConversions().iterator();
							// iterator4.hasNext();) {
							// KeyValue pair = (KeyValue)
							// iterator4.next();
							// if
							// (pair.getLabel().equals(field.getPropertyName()))
							// {
							// String fieldValue = "";
							// RuleExtensionValue extensionValue =
							// getRuleExtensionValue((String) pair.getKey());
							// if (extensionValue != null) {
							// fieldValue = extensionValue.getValue();
							// }
							// fields.add(PropertyKeyValue.create(pair.getKey(),
							// fieldValue));
							// found = true;
							// break;
							// }
							// }
							// if (!found) {
							// String fieldValue = "";
							// RuleExtensionValue extensionValue =
							// getRuleExtensionValue((String)
							// field.getPropertyName());
							// if (extensionValue != null) {
							// fieldValue = extensionValue.getValue();
							// }
							// fields.add(new
							// KeyValue(field.getPropertyName(),
							// fieldValue));
							// }
							// } else {
							// String fieldValue = "";
							// RuleExtensionValue extensionValue =
							// getRuleExtensionValue((String)
							// field.getPropertyName());
							// if (extensionValue != null) {
							// fieldValue = extensionValue.getValue();
							// }
							// fields.add(new
							// KeyValue(field.getPropertyName(),
							// fieldValue));
							// }
						}
					}
				}
			}
		}
	}

	public void loadFieldsWithDefaultValues() {
		fields.clear();
		if (getRuleTemplateId() != null) {
			RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(getRuleTemplateId());
			if (ruleTemplate != null) {
				List ruleTemplateAttributes = ruleTemplate.getActiveRuleTemplateAttributes();
				Collections.sort(ruleTemplateAttributes);
				for (Iterator iter = ruleTemplateAttributes.iterator(); iter.hasNext();) {
					RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
					if (!ruleTemplateAttribute.isWorkflowAttribute()) {
						continue;
					}
					WorkflowAttribute workflowAttribute = ruleTemplateAttribute.getWorkflowAttribute();
					RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
					if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
						((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
					}
					for (Object element : workflowAttribute.getRuleRows()) {
						Row row = (Row) element;
						for (Object element2 : row.getFields()) {
							Field field = (Field) element2;
							if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
								fields.add(new KeyValueId(field.getPropertyName(), field.getPropertyValue(), ruleTemplateAttribute.getRuleTemplateAttributeId() + ""));
							//} else if (!org.apache.commons.lang.StringUtils.isEmpty(field.getDefaultLookupableName())) {
							//	fields.add(new KeyValueId(field.getDefaultLookupableName(), field.getPropertyValue(), ruleTemplateAttribute.getRuleTemplateAttributeId() + ""));
							} else {
								fields.add(new KeyValueId(field.getPropertyName(), field.getPropertyValue(), ruleTemplateAttribute.getRuleTemplateAttributeId() + ""));
							}
						}
					}
				}
			}
		}
	}

	private void loadWebValues() {
		loadRows();
		loadDates();
		loadRuleTemplateName();
	}

	private void loadRows() {
		getRoles().clear();
		if (getRuleTemplateId() != null) {
			RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(getRuleTemplateId());
			if (ruleTemplate != null) {
				setRuleTemplateName(ruleTemplate.getName());
				List ruleTemplateAttributes = ruleTemplate.getActiveRuleTemplateAttributes();
				Collections.sort(ruleTemplateAttributes);
				List rows = new ArrayList();
				for (Iterator iter = ruleTemplateAttributes.iterator(); iter.hasNext();) {
					RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) iter.next();
					if (!ruleTemplateAttribute.isWorkflowAttribute()) {
						continue;
					}
					WorkflowAttribute workflowAttribute = ruleTemplateAttribute.getWorkflowAttribute();

					RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
					if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
						((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
					}
					workflowAttribute.validateRuleData(getFieldMap(ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
					rows.addAll(workflowAttribute.getRuleRows());
					if (workflowAttribute instanceof RoleAttribute) {
						RoleAttribute roleAttribute = (RoleAttribute) workflowAttribute;
						getRoles().addAll(roleAttribute.getRoleNames());
					}
				}
				setRows(rows);
			}
		}
	}

	private void loadDates() {
		if (getFromDate() != null) {
			setFromDateValue(RiceConstants.getDefaultDateFormat().format(getFromDate()));
		}
		if (getToDate() != null) {
			setToDateValue(RiceConstants.getDefaultDateFormat().format(getToDate()));
		}
	}

	private void loadRuleTemplateName() {
		if (org.apache.commons.lang.StringUtils.isEmpty(getRuleTemplateName()) && getRuleTemplateId() != null) {
			RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(getRuleTemplateId());
			if (ruleTemplate != null) {
				setRuleTemplateName(ruleTemplate.getName());
			}
		}
	}

	public List getFields() {
		return fields;
	}

	public void setFields(List fields) {
		this.fields = fields;
	}

	public KeyValueId getField(int index) {
		while (getFields().size() <= index) {
			KeyValueId field = new KeyValueId();
			getFields().add(field);
		}
		return (KeyValueId) getFields().get(index);
	}

	public String getFromDateValue() {
		return fromDateValue;
	}

	public void setFromDateValue(String fromDateValue) {
		this.fromDateValue = fromDateValue;
	}

	public List getRoles() {
		return roles;
	}

	public void setRoles(List roles) {
		this.roles = roles;
	}

	public List getRows() {
		return rows;
	}

	public void setRows(List ruleTemplateAttributes) {
		this.rows = ruleTemplateAttributes;
	}

	public String getToDateValue() {
		return toDateValue;
	}

	public void setToDateValue(String toDateValue) {
		this.toDateValue = toDateValue;
	}

	@Override
	public String getRuleTemplateName() {
		return ruleTemplateName;
	}

	public void setRuleTemplateName(String ruleTemplateName) {
		this.ruleTemplateName = ruleTemplateName;
	}

	public boolean isHasExtensionValueErrors() {
		return hasExtensionValueErrors;
	}

	public void setHasExtensionValueErrors(boolean hasRuleExtensionValueErrors) {
		this.hasExtensionValueErrors = hasRuleExtensionValueErrors;
	}

	/** Web Logic * */

	/**
	 * Populates this WebRuleBaseValues object for editing the given rule.
	 */
	public void edit(RuleBaseValues rule) throws Exception {
		load(rule);
		initialize();
	}

	/**
	 * Loads the given rule into this WebRuleBaseValues.
	 */
	public void load(RuleBaseValues rule) throws Exception {
		PropertyUtils.copyProperties(this, rule);
		injectWebMembers();
	}

	public void initialize() throws Exception {
		loadFields();
		// setPreviousVersionId(getRuleBaseValuesId());
		for (Object element : getResponsibilities()) {
			WebRuleResponsibility responsibility = (WebRuleResponsibility) element;
			responsibility.initialize();
		}
		establishRequiredState();
	}

	private void injectWebMembers() throws Exception {
		List currentResponsibilities = getResponsibilities();
		setResponsibilities(new ArrayList());
		for (Iterator iterator = currentResponsibilities.iterator(); iterator.hasNext();) {
			RuleResponsibility responsibility = (RuleResponsibility) iterator.next();
			WebRuleResponsibility webResponsibility = createNewRuleResponsibility();
			webResponsibility.load(responsibility);
		}
	}

	/**
	 * Establishes any missing and required state in the WebRuleBaseValues.
	 */
	public void establishRequiredState() throws Exception {
		if (getActiveInd() == null) {
			setActiveInd(Boolean.TRUE);
		}
		if (getForceAction() == null) {
			setForceAction(Boolean.FALSE);
		}
		loadWebValues();
		if (getResponsibilities().isEmpty()) {
			createNewRuleResponsibility();
		}
		for (Object element : getResponsibilities()) {
			WebRuleResponsibility responsibility = (WebRuleResponsibility) element;
			responsibility.establishRequiredState();
		}
	}

	@Override
	public RuleResponsibility getResponsibility(int index) {
		while (getResponsibilities().size() <= index) {
			createNewRuleResponsibility();
		}
		return getResponsibilities().get(index);
	}

	public int getResponsibilitiesSize() {
		return getResponsibilities().size();
	}

	public WebRuleResponsibility createNewRuleResponsibility() {
		WebRuleResponsibility responsibility = new WebRuleResponsibility();
		responsibility.setRuleBaseValues(this);
		addRuleResponsibility(responsibility);
		return responsibility;
	}

	public Map getFieldMap(String ruleTemplateAttributeId) {
		Map fieldMap = new HashMap();
		for (Iterator iterator = getFields().iterator(); iterator.hasNext();) {
			KeyValueId field = (KeyValueId) iterator.next();
			if (ruleTemplateAttributeId.equals(field.getId())) {
				fieldMap.put(field.getKey(), field.getValue());
			}
		}
		return fieldMap;
	}

	public void populatePreviousVersionIds() {
		if (getPreviousVersionId() == null) {
			setPreviousVersionId(getRuleBaseValuesId());
		}
		for (Object element : getResponsibilities()) {
			WebRuleResponsibility responsibility = (WebRuleResponsibility) element;
			responsibility.populatePreviousVersionIds();
		}
	}

	/**
	 * This method is used to "materialize" the web rule before it gets saved, if we don't do this then certain fields will be saved as NULL. For example, ruleTemplate.
	 */
	public void materialize() {
		if (getRuleTemplate() == null && getRuleTemplateId() != null) {
			setRuleTemplate(getRuleTemplateService().findByRuleTemplateId(getRuleTemplateId()));
		}
	}

	public void validateRule(String keyPrefix, ActionErrors errors) {
		/** Validate Template * */
		if (getRuleTemplateId() == null) {
			errors.add(keyPrefix + "ruleTemplateId", new ActionMessage("routetemplate.required.html", "rule template"));
		} else {
			List errorList = new ArrayList();
			populateRuleExtensionValues(errorList);
			saveServiceErrors(keyPrefix + "ruleExtensionValues", errorList, errors);
			if (!errorList.isEmpty()) {
				setHasExtensionValueErrors(true);
			}
		}

		/** Validate dates * */

		boolean dateParseProblem = false;
		try {
			setToDate(decodeTimestamp(getToDateValue()));
		} catch (ParseException e) {
			errors.add(keyPrefix + "toDateValue", new ActionMessage("routetemplate.required.html", "to date (MM/DD/YYYY)"));
			dateParseProblem = true;
		}
		try {
			setFromDate(decodeTimestamp(getFromDateValue()));
		} catch (ParseException e) {
			errors.add(keyPrefix + "fromDateValue", new ActionMessage("routetemplate.required.html", "from date (MM/DD/YYYY)"));
			dateParseProblem = true;
		}
        throttleDates();
		if (getFromDate() == null) {
			setFromDate(new Timestamp(new Date().getTime()));
		}
		if (getToDate() == null) {
			try {
				setToDate(new Timestamp(new SimpleDateFormat("MM/dd/yyyy").parse("01/01/2100").getTime()));
			} catch (ParseException e) {
				errors.add(keyPrefix + "toDateValue", new ActionMessage("routetemplate.required.html", "to date"));
				dateParseProblem = true;
			}
		}
		if (!dateParseProblem && getToDate().before(getFromDate())) {
			errors.add(keyPrefix + "toDateValue", new ActionMessage("routetemplate.ruleservice.daterange.fromafterto"));
		}

		if (getActiveInd() == null) {
			errors.add(keyPrefix + "activeInd", new ActionMessage("routetemplate.ruleservice.activeind.required"));
		}

		if (org.apache.commons.lang.StringUtils.isEmpty(getDescription())) {
			errors.add(keyPrefix + "description", new ActionMessage("routetemplate.ruleservice.description.required"));
		}

		if (getForceAction() == null) {
			errors.add(keyPrefix + "forceAction", new ActionMessage("routetemplate.ruleservice.forceAction.required"));
		}

		if (getResponsibilities().isEmpty()) {
			errors.add(keyPrefix + "responsibilities", new ActionMessage("routetemplate.ruleservice.responsibility.required"));
		}

		int respIndex = 0;
		for (Object element : getResponsibilities()) {
			WebRuleResponsibility responsibility = (WebRuleResponsibility) element;
			String respPrefix = keyPrefix + "responsibility[" + respIndex + "].";
			responsibility.validateResponsibility(respPrefix, errors);
			respIndex++;
		}
	}

    /**
     * This will ensure that the toDate is never larger than 2100, currently
     * doesn't do any throttling on the from date
     */
    private void throttleDates() {
        if (getToDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(getToDate());
            if (calendar.get(Calendar.YEAR) > TO_DATE_UPPER_LIMIT) {
                calendar.set(Calendar.YEAR, TO_DATE_UPPER_LIMIT);
                setToDate(new Timestamp(calendar.getTimeInMillis()));
                setToDateValue(new SimpleDateFormat("MM/dd/yyyy").format(getToDate()));
            }
        }
    }

	private void saveServiceErrors(String errorKey, Collection srvErrors, ActionErrors errors) {
		for (Iterator iterator = srvErrors.iterator(); iterator.hasNext();) {
			WorkflowServiceError error = (WorkflowServiceError) iterator.next();
			if (error.getArg1() == null && error.getArg2() == null) {
				errors.add(errorKey, new ActionMessage(error.getKey()));
			} else if (error.getArg1() != null && error.getArg2() == null) {
				errors.add(errorKey, new ActionMessage(error.getKey(), error.getArg1()));
			} else {
				errors.add(errorKey, new ActionMessage(error.getKey(), error.getArg1(), error.getArg2()));
			}
		}
	}

	private Timestamp decodeTimestamp(String dateValue) throws ParseException {
		if (org.apache.commons.lang.StringUtils.isEmpty(dateValue)) {
			return null;
		}
		/*  Not the best solution below but does allow our forcing of the 4 digit year
		 *  until KEW and use the KNS for it's date entry/validation
		 */
		String convertedDate = SQLUtils.getEntryFormattedDate(dateValue);
		if (convertedDate == null) {
		    throw new ParseException("Date entered as '" + dateValue + "' is in invalid format", 0);
		}
		Date date = RiceConstants.getDefaultDateFormat().parse(convertedDate);
		return new Timestamp(date.getTime());
	}

	private void populateRuleExtensionValues(List errorList) {
		RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(getRuleTemplateId());
		setRuleTemplate(ruleTemplate);

		/** Populate rule extension values * */
		List extensions = new ArrayList();
		for (Object element : ruleTemplate.getActiveRuleTemplateAttributes()) {
			RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) element;
			if (!ruleTemplateAttribute.isWorkflowAttribute()) {
				continue;
			}
			WorkflowAttribute workflowAttribute = ruleTemplateAttribute.getWorkflowAttribute();

			RuleAttribute ruleAttribute = ruleTemplateAttribute.getRuleAttribute();
			if (ruleAttribute.getType().equals(KEWConstants.RULE_XML_ATTRIBUTE_TYPE)) {
				((GenericXMLRuleAttribute) workflowAttribute).setRuleAttribute(ruleAttribute);
			}

			List attValidationErrors = workflowAttribute.validateRuleData(getFieldMap(ruleTemplateAttribute.getRuleTemplateAttributeId()+""));
			if (attValidationErrors != null && !attValidationErrors.isEmpty()) {
				errorList.addAll(attValidationErrors);
			} else {
				List ruleExtensionValues = workflowAttribute.getRuleExtensionValues();
				if (ruleExtensionValues != null && !ruleExtensionValues.isEmpty()) {
					RuleExtension ruleExtension = new RuleExtension();
					ruleExtension.setRuleTemplateAttributeId(ruleTemplateAttribute.getRuleTemplateAttributeId());

					ruleExtension.setExtensionValues(ruleExtensionValues);
					extensions.add(ruleExtension);
				}
			}
		}
		setRuleExtensions(extensions);
		setRuleTemplate(ruleTemplate);

		for (Object element : getRuleExtensions()) {
			RuleExtension ruleExtension = (RuleExtension) element;
			ruleExtension.setRuleBaseValues(this);

			for (Object element2 : ruleTemplate.getActiveRuleTemplateAttributes()) {
				RuleTemplateAttribute ruleTemplateAttribute = (RuleTemplateAttribute) element2;
				if (ruleTemplateAttribute.getRuleTemplateAttributeId().longValue() == ruleExtension.getRuleTemplateAttributeId().longValue()) {
					ruleExtension.setRuleTemplateAttribute(ruleTemplateAttribute);
					break;
				}
			}

			for (Object element2 : ruleExtension.getExtensionValues()) {
				RuleExtensionValue ruleExtensionValue = (RuleExtensionValue) element2;
				ruleExtensionValue.setExtension(ruleExtension);
			}
		}

	}

	private RuleTemplateService getRuleTemplateService() {
		return (RuleTemplateService) KEWServiceLocator.getService(KEWServiceLocator.RULE_TEMPLATE_SERVICE);
	}

	/**
	 * @return Returns the actionRequestCodes.
	 */
	public Map getActionRequestCodes() {
		Map actionRequestCodes = new HashMap();
		actionRequestCodes.putAll(CodeTranslator.arLabels);
		if (getRuleTemplateId() != null) {
			RuleTemplate ruleTemplate = getRuleTemplateService().findByRuleTemplateId(getRuleTemplateId());
			if (ruleTemplate != null) {
				if (ruleTemplate.getAcknowledge() != null && "false".equals(ruleTemplate.getAcknowledge().getValue())) {
					actionRequestCodes.remove(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ);
				}
				if (ruleTemplate.getComplete() != null && "false".equals(ruleTemplate.getComplete().getValue())) {
					actionRequestCodes.remove(KEWConstants.ACTION_REQUEST_COMPLETE_REQ);
				}
				if (ruleTemplate.getApprove() != null && "false".equals(ruleTemplate.getApprove().getValue())) {
					actionRequestCodes.remove(KEWConstants.ACTION_REQUEST_APPROVE_REQ);
				}
				if (ruleTemplate.getFyi() != null && "false".equals(ruleTemplate.getFyi().getValue())) {
					actionRequestCodes.remove(KEWConstants.ACTION_REQUEST_FYI_REQ);
				}
			}
		}
		return actionRequestCodes;
	}

	public RuleDelegation getRuleDelegation() {
		if (getDelegateRule().booleanValue()) {
			List ruleDelegations = getRuleDelegationService().findByDelegateRuleId(getRuleBaseValuesId());
			RuleDelegation currentRuleDelegation = (RuleDelegation) ruleDelegations.get(0);
			RuleBaseValues mostRecentRule = currentRuleDelegation.getRuleResponsibility().getRuleBaseValues();

			for (Iterator iter = ruleDelegations.iterator(); iter.hasNext();) {
				RuleDelegation ruleDelegation = (RuleDelegation) iter.next();
				RuleBaseValues parentRule = ruleDelegation.getRuleResponsibility().getRuleBaseValues();

				if (parentRule.getActivationDate().after(mostRecentRule.getActivationDate())) {
					mostRecentRule = ruleDelegation.getRuleResponsibility().getRuleBaseValues();
					currentRuleDelegation = ruleDelegation;
				}
			}
			return currentRuleDelegation;
		}
		return null;
	}

	public Long getParentRuleId() {
		if (getDelegateRule().booleanValue()) {
			List ruleDelegations = getRuleDelegationService().findByDelegateRuleId(getRuleBaseValuesId());
			RuleDelegation currentRuleDelegation = (RuleDelegation) ruleDelegations.get(0);
			RuleBaseValues mostRecentRule = currentRuleDelegation.getRuleResponsibility().getRuleBaseValues();

			for (Iterator iter = ruleDelegations.iterator(); iter.hasNext();) {
				RuleDelegation ruleDelegation = (RuleDelegation) iter.next();
				RuleBaseValues parentRule = ruleDelegation.getRuleResponsibility().getRuleBaseValues();

				if (parentRule.getActivationDate().after(mostRecentRule.getActivationDate())) {
					mostRecentRule = ruleDelegation.getRuleResponsibility().getRuleBaseValues();
				}
			}
			return mostRecentRule.getRuleBaseValuesId();
		}
		return null;
	}

	private RuleDelegationService getRuleDelegationService() {
		return (RuleDelegationService) KEWServiceLocator.getService(KEWServiceLocator.RULE_DELEGATION_SERVICE);
	}
}
