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
package edu.iu.uis.eden.workgroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.workflow.attribute.Extension;
import org.kuali.workflow.attribute.ExtensionAttribute;
import org.kuali.workflow.attribute.ExtensionData;
import org.kuali.workflow.workgroup.WorkgroupType;
import org.kuali.workflow.workgroup.WorkgroupTypeAttribute;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.KEWServiceLocator;
import edu.iu.uis.eden.WorkflowServiceErrorException;
import edu.iu.uis.eden.export.ExportDataSet;
import edu.iu.uis.eden.export.ExportFormat;
import edu.iu.uis.eden.export.Exportable;
import edu.iu.uis.eden.lookupable.Column;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.lookupable.MyColumns;
import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.plugin.attributes.WorkflowLookupable;
import edu.iu.uis.eden.user.AuthenticationUserId;
import edu.iu.uis.eden.user.web.WebWorkflowUser;
import edu.iu.uis.eden.util.KeyLabelPair;
import edu.iu.uis.eden.util.Utilities;
import edu.iu.uis.eden.validation.ValidationContext;
import edu.iu.uis.eden.validation.ValidationResults;
import edu.iu.uis.eden.web.UrlResolver;
import edu.iu.uis.eden.workgroup.web.WebWorkgroup;

/**
 * Default implementation of the Workgroup Lookupable.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class WorkGroupLookupableImpl implements WorkflowLookupable, Exportable {

	private List rows;
	private List columns = establishColumns();
	private static final String title = "Search for a WorkGroup";
	private static final String returnLocation = "Lookup.do";

	private static final String WORKGROUP_NAME_FIELD_LABEL = "WorkGroup Name";
	private static final String WORKGROUP_TYPE_FIELD_LABEL = "WorkGroup Type";
	private static final String WORKGROUP_DESCRIPTION_FIELD_LABEL = "WorkGroup Description";
	private static final String ACTIVE_IND_FIELD_LABEL = "Active Indicator";
	private static final String WORKGROUP_MEMBER_FIELD_LABEL = "WorkGroup Member";
	private static final String WORKGROUP_ID_FIELD_LABEL = "WorkGroup Id";

	private static final String WORKGROUP_NAME_FIELD_HELP = "";
	private static final String WORKGROUP_TYPE_FIELD_HELP = "";
	private static final String WORKGROUP_DESCRIPTION_FIELD_HELP = "";
	private static final String ACTIVE_IND_FIELD_HELP = "";
	private static final String WORKGROUP_MEMBER_FIELD_HELP = "";
	private static final String WORKGROUP_ID_FIELD_HELP = "";

	private static final String WORKGROUP_NAME_PROPERTY_NAME = "workgroupName";
	private static final String WORKGROUP_TYPE_PROPERTY_NAME = "workgroupType";
	private static final String WORKGROUP_DESCRIPTION_PROPERTY_NAME = "workgroupDescription";
	private static final String ACTIVE_IND_PROPERTY_NAME = "activeIndicator";
	private static final String WORKGROUP_MEMBER_PROPERTY_NAME = "networkId";
	private static final String WORKGROUP_ID_PROPERTY_NAME = "workgroupId";

	private static final String USER_LOOKUPABLE = "UserLookupableImplService";
	private static final String BACK_LOCATION_KEY_NAME = "backLocation";
	private static final String WORKGROUP_ID = "workgroupId";
	private static final String DOC_FORM_KEY_NAME = "docFormKey";

	/**
	 * WorkGroupLookupableImpl - constructor that sets up the values of what the form on the jsp will look like.
	 */
	public WorkGroupLookupableImpl() {
		rows = new ArrayList();

		List fields = new ArrayList();
		fields.add(new Field(WORKGROUP_NAME_FIELD_LABEL, WORKGROUP_NAME_FIELD_HELP, Field.TEXT, false, WORKGROUP_NAME_PROPERTY_NAME, "", null, null));
		rows.add(new Row(fields));

		fields = new ArrayList();
        fields.add(new Field(WORKGROUP_TYPE_FIELD_LABEL, WORKGROUP_TYPE_FIELD_HELP, Field.DROPDOWN_REFRESH, false, WORKGROUP_TYPE_PROPERTY_NAME, "", getWorkgroupTypeOptions(), null));
        rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field(WORKGROUP_DESCRIPTION_FIELD_LABEL, WORKGROUP_DESCRIPTION_FIELD_HELP, Field.TEXT, false, WORKGROUP_DESCRIPTION_PROPERTY_NAME, "", null, null));
		rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field(WORKGROUP_MEMBER_FIELD_LABEL, WORKGROUP_MEMBER_FIELD_HELP, Field.TEXT, true, WORKGROUP_MEMBER_PROPERTY_NAME, "", null, USER_LOOKUPABLE));
		fields.add(new Field("user", "", Field.QUICKFINDER, false, "", "", null, USER_LOOKUPABLE));
		rows.add(new Row(fields));

		fields = new ArrayList();
		fields.add(new Field(WORKGROUP_ID_FIELD_LABEL, WORKGROUP_ID_FIELD_HELP, Field.TEXT, false, WORKGROUP_ID_PROPERTY_NAME, "", null, null));
		rows.add(new Row(fields));

		List options = new ArrayList();
		options.add(new KeyLabelPair("Y", "Active"));
		options.add(new KeyLabelPair("N", "Inactive"));
		options.add(new KeyLabelPair("ALL", "Show All"));

		fields = new ArrayList();
		fields.add(new Field(ACTIVE_IND_FIELD_LABEL, ACTIVE_IND_FIELD_HELP, Field.RADIO, false, ACTIVE_IND_PROPERTY_NAME, "ALL", options, null));
		rows.add(new Row(fields));

	}

	private List getWorkgroupTypeOptions() {
		List options = new ArrayList();
		options.add(new KeyLabelPair("", ""));
		options.add(new KeyLabelPair("Default", "Default"));
		List<WorkgroupType> workgroupTypes = KEWServiceLocator.getWorkgroupTypeService().findAll();
		for (WorkgroupType workgroupType : workgroupTypes) {
			options.add(new KeyLabelPair(workgroupType.getName(), workgroupType.getLabel()));
		}
		return options;
	}

	private static List establishColumns() {
		List columns = new ArrayList();
		Column column = new Column("WorkGroup Id", Column.COLUMN_IS_SORTABLE_VALUE, "workgroupId");
		column.setType(Column.INTEGER);
		columns.add(column);
		columns.add(new Column("WorkGroup Name", Column.COLUMN_IS_SORTABLE_VALUE, "workgroupName"));
		columns.add(new Column("Type", Column.COLUMN_IS_SORTABLE_VALUE, "workgroupType"));
		columns.add(new Column("Active", Column.COLUMN_IS_SORTABLE_VALUE, "activeIndDisplay"));
		columns.add(new Column("Actions", Column.COLUMN_NOT_SORTABLE_VALUE, "destinationUrl"));
		return columns;
	}

	public List getDefaultReturnType() {
		List returnTypes = new ArrayList();
		returnTypes.add(WORKGROUP_ID);
		return returnTypes;
	}

	public void changeIdToName(Map fieldValues) {

	}

	/**
	 * getSearchResults - searches for work group information based on the criteria passed in by the map.
	 *
	 * @return Returns a list of Workgroup objects that match the result.
	 */
	public List getSearchResults(Map fieldValues, Map fieldConversions) throws Exception {
		String activeIndicator = (String) fieldValues.get(ACTIVE_IND_PROPERTY_NAME);
		String workGroupName = (String) fieldValues.get(WORKGROUP_NAME_PROPERTY_NAME);
		String workGroupType = (String) fieldValues.get(WORKGROUP_TYPE_PROPERTY_NAME);
		String workGroupDescription = (String) fieldValues.get(WORKGROUP_DESCRIPTION_PROPERTY_NAME);
		String workGroupMember = (String) fieldValues.get(WORKGROUP_MEMBER_PROPERTY_NAME);
		String workGroupId = (String) fieldValues.get(WORKGROUP_ID_PROPERTY_NAME);
		String backLocation = (String) fieldValues.get(BACK_LOCATION_KEY_NAME);
		String docFormKey = (String) fieldValues.get(DOC_FORM_KEY_NAME);

		String workGroupReturn = (String) fieldConversions.get(WORKGROUP_ID);
		String workGroupNameReturn = (String) fieldConversions.get(WORKGROUP_NAME_PROPERTY_NAME);

		WorkgroupService workgroupService = (WorkgroupService) KEWServiceLocator.getWorkgroupService();
		WebWorkgroup template = new WebWorkgroup(workgroupService.getBlankWorkgroup());

		if (workGroupId != null && !"".equals(workGroupId.trim())) {
			try {
				template.setWorkflowGroupId(new WorkflowGroupId(new Long(workGroupId.trim())));
			} catch (NumberFormatException e) {
				template.setWorkflowGroupId(new WorkflowGroupId(new Long(-1)));
			}
		}

		if (workGroupName != null && !"".equals(workGroupName.trim())) {
			workGroupName = workGroupName.replace('*', '%');
			template.setGroupNameId(new GroupNameId("%" + workGroupName.trim() + "%"));
		}

		if (!StringUtils.isBlank(workGroupType)) {
			template.setWorkgroupType(workGroupType);
		}

		if (workGroupDescription != null && !"".equals(workGroupDescription.trim())) {
			workGroupDescription = workGroupDescription.replace('*', '%');
			template.setDescription("%" + workGroupDescription.trim() + "%");
		}

		if (activeIndicator == null || activeIndicator.equals("ALL")) {
			template.setActiveInd(null);
		} else if (activeIndicator.equals("Y")) {
			template.setActiveInd(new Boolean(true));
		} else if (activeIndicator.equals("N")) {
			template.setActiveInd(new Boolean(false));
		}

		MyColumns myColumns = new MyColumns();
		Map attributes = handleAttributeSearchFields(myColumns, fieldValues, workGroupType);

		Iterator workGroups = null;
		if (workGroupMember != null && !"".equals(workGroupMember.trim())) {
			WebWorkflowUser userTemplate = new WebWorkflowUser(KEWServiceLocator.getUserService().getBlankUser());
			userTemplate.setAuthenticationUserId(new AuthenticationUserId(workGroupMember.trim()));
			workGroups = workgroupService.search(template, attributes, userTemplate).iterator();
		} else {
			workGroups = workgroupService.search(template, attributes, true).iterator();
		}

		List displayList = new ArrayList();
		while (workGroups.hasNext()) {
			Workgroup prototype = (Workgroup) workGroups.next();
			WebWorkgroup workGroup = new WebWorkgroup(prototype);

			if (!StringUtils.isEmpty(workGroupType) && !"Default".equals(workGroupType)) {
				MyColumns myNewColumns = new MyColumns();
				for (Iterator iter = myColumns.getColumns().iterator(); iter.hasNext();) {
					KeyLabelPair pair = (KeyLabelPair) iter.next();
					KeyLabelPair newPair = new KeyLabelPair();
					newPair.setKey(pair.getKey());
					String extensionValue = getExtensionValue(workGroup, pair.getKey().toString());
					if (!StringUtils.isEmpty(extensionValue)) {
						newPair.setLabel(extensionValue);
					} else {
						newPair.setLabel(EdenConstants.HTML_NON_BREAKING_SPACE);
					}
					myNewColumns.getColumns().add(newPair);
				}
				workGroup.setMyColumns(myNewColumns);
			}


			StringBuffer returnUrl = new StringBuffer("<a href=\"");
			returnUrl.append(backLocation).append("?methodToCall=refresh&docFormKey=").append(docFormKey).append("&");
			if (!Utilities.isEmpty(workGroupReturn)) {
				returnUrl.append(workGroupReturn);
			} else {
				returnUrl.append(WORKGROUP_ID);
			}
			returnUrl.append("=").append(workGroup.getWorkflowGroupId().getGroupId()).append("&");
			if (!Utilities.isEmpty(workGroupNameReturn)) {
				returnUrl.append(workGroupNameReturn);
			} else {
				returnUrl.append(WORKGROUP_NAME_PROPERTY_NAME);
			}
			returnUrl.append("=").append(workGroup.getWorkgroupName());

			returnUrl.append("\">return value</a>");
			workGroup.setReturnUrl(returnUrl.toString());

			WorkgroupCapabilities caps = workgroupService.getCapabilities();
			int actionsAvailable = 0;
			StringBuffer buffer = new StringBuffer();
			if (caps.isReportSupported()) {
				buffer.append("<a href=\"");
				buffer.append(UrlResolver.getInstance().getWorkgroupReportUrl() + "?methodToCall=report&workgroupId=").append(workGroup.getWorkflowGroupId().getGroupId());
				buffer.append("&showEdit=yes").append("\" >").append("report").append("</a>");
				actionsAvailable++;
			}

			if (caps.isEditSupported()) {
				if (actionsAvailable > 0) {
					buffer.append(" | ");
				}
				buffer.append("<a href=\"");
				buffer.append(UrlResolver.getInstance().getWorkgroupUrl() + "?methodToCall=edit&workgroupId=").append(workGroup.getWorkflowGroupId().getGroupId());
				buffer.append("&showEdit=yes").append("\" >").append("edit").append("</a>");
				actionsAvailable++;
			}

			if (actionsAvailable == 0) {
				buffer.append("No actions available");
			}

			workGroup.setDestinationUrl(buffer.toString());
			displayList.add(workGroup);
		}
		return displayList;
	}

	protected String getExtensionValue(Workgroup workgroup, String key) {
		for (Extension extension : workgroup.getExtensions()) {
			for (ExtensionData data : extension.getData()) {
				if (key.equals(data.getKey())) {
					return data.getValue();
				}
			}
		}
		return null;
	}

	protected Map handleAttributeSearchFields(MyColumns myColumns, Map fieldValues, String workgroupTypeName) {
		Map<String, String> attributes = null;
		if (!StringUtils.isBlank(workgroupTypeName) && !"Default".equals(workgroupTypeName)) {
			WorkgroupType workgroupType = KEWServiceLocator.getWorkgroupTypeService().findByName(workgroupTypeName);
			List<Object> errors = new ArrayList<Object>();
			for (WorkgroupTypeAttribute workgroupTypeAttribute : workgroupType.getActiveAttributes()) {
				attributes = new HashMap<String, String>();
				Object attributeObject = workgroupTypeAttribute.loadAttribute();
				if (attributeObject instanceof ExtensionAttribute) {
					ExtensionAttribute attribute = (ExtensionAttribute) attributeObject;
					List<Row> searchRows = attribute.getRows();
					for (Row row : searchRows) {
						for (Field field : row.getFields()) {
							if (fieldValues.get(field.getPropertyName()) != null) {
								String attributeParam = (String) fieldValues.get(field.getPropertyName());
								if (!attributeParam.equals("")) {
									//if (!Utilities.isEmpty(field.getDefaultLookupableName())) {
									//	attributes.put(field.getDefaultLookupableName(), attributeParam.trim());
									//} else {
										attributes.put(field.getPropertyName(), attributeParam.trim());
									//}
								}
							}
							if (field.isInputField() && !Field.HIDDEN.equals(field.getFieldType())) {
								//if (!Utilities.isEmpty(field.getDefaultLookupableName())) {
								//	myColumns.getColumns().add(new KeyLabelPair(field.getDefaultLookupableName(), workgroupTypeAttribute.getWorkgroupTypeAttributeId()+""));
								//} else {
									myColumns.getColumns().add(new KeyLabelPair(field.getPropertyName(), workgroupTypeAttribute.getWorkgroupTypeAttributeId()+""));
								//}
							}
						}
					}
					ValidationContext validationContext = new ValidationContext();
					validationContext.getParameters().put("extensions", attributes);
					validationContext.getParameters().put("operation", "search");
					ValidationResults results = attribute.validate(validationContext);
					if (results != null && !results.getValidationResults().isEmpty()) {
						errors.add(results);
					}
				}
			}
			if (!errors.isEmpty()) {
				throw new WorkflowServiceErrorException("Workgroup search errors", errors);
			}
		}
		return attributes;
	}

	public boolean checkForAdditionalFields(Map fieldValues, HttpServletRequest request) throws Exception {
		String workgroupTypeName = (String) fieldValues.get(WORKGROUP_TYPE_PROPERTY_NAME);

		// copy the field values into a Map for validation
//		Map<String, String> extensions = new HashMap<String, String>();
//		for (Object key : fieldValues.keySet()) {
//			if (key instanceof String) {
//				Object value = fieldValues.get(key);
//				if (value instanceof String) {
//					extensions.put((String)key, (String)value);
//				}
//			}
//		}

		if (!StringUtils.isBlank(workgroupTypeName) && !"Default".equals(workgroupTypeName)) {
			WorkgroupType workgroupType = KEWServiceLocator.getWorkgroupTypeService().findByName(workgroupTypeName);

			int i = 0;
			List<Object> errors = new ArrayList<Object>();
			for (WorkgroupTypeAttribute workgroupTypeAttribute : workgroupType.getActiveAttributes()) {
				Object attributeObject = workgroupTypeAttribute.loadAttribute();
				if (attributeObject instanceof ExtensionAttribute) {
					ExtensionAttribute attribute = (ExtensionAttribute) attributeObject;
					List<Row> searchRows = attribute.getRows();
					for (Row searchRow : searchRows) {
						for (Field searchField : searchRow.getFields()) {
							if (request.getParameter(searchField.getPropertyName()) != null) {
								searchField.setPropertyValue(request.getParameter(searchField.getPropertyName()));
							}
							fieldValues.put(searchField.getPropertyName(), searchField.getPropertyValue());
						}
					}
//					ValidationContext validationContext = new ValidationContext();
//					validationContext.getParameters().put("extensions", extensions);
//					ValidationResults validationResults = attribute.validate(validationContext);
//					if (validationResults != null) {
//						errors.add(validationResults);
//					}

					// new refetch the searchRows (not sure why, modeled after RuleBaseValuesLookupable)
					searchRows = attribute.getRows();

					for (Row searchRow : searchRows) {
						List<Field> fields = new ArrayList<Field>();
						for (Field searchField : searchRow.getFields()) {
							if (request.getParameter(searchField.getPropertyName()) != null) {
								searchField.setPropertyValue(request.getParameter(searchField.getPropertyName()));
							}
							fields.add(searchField);
							fieldValues.put(searchField.getPropertyName(), searchField.getPropertyValue());
						}
						searchRow.setFields(fields);
						rows.add(searchRow);
					}

					if (attribute.getRows() != null) {
						for (Row row : attribute.getRows()) {
							for (Field field : row.getFields()) {
								if (field.isInputField() && !Field.HIDDEN.equals(field.getFieldType())) {
									getColumns().add(i + 4, new Column(field.getFieldLabel(), Column.COLUMN_IS_SORTABLE_VALUE, "myColumns.columns[" + i + "].label"));
									i++;
								}
							}
						}
					}


				}
			}
			if (!errors.isEmpty()) {
				throw new WorkflowServiceErrorException("Workgroup search errors", errors);
			}


			return true;
		}
		return false;
	}

	public String getNoReturnParams(Map fieldConversions) {
		String workGroupReturn = (String) fieldConversions.get(WORKGROUP_ID);
		String workGroupNameReturn = (String) fieldConversions.get(WORKGROUP_NAME_PROPERTY_NAME);
		StringBuffer noReturnParams = new StringBuffer("&");
		if (!Utilities.isEmpty(workGroupReturn)) {
			noReturnParams.append(workGroupReturn);
		} else {
			noReturnParams.append(WORKGROUP_ID);
		}
		noReturnParams.append("=&");
		if (!Utilities.isEmpty(workGroupNameReturn)) {
			noReturnParams.append(workGroupNameReturn);
		} else {
			noReturnParams.append(WORKGROUP_NAME_PROPERTY_NAME);
		}
		noReturnParams.append("=");
		return noReturnParams.toString();
	}

	/**
	 * @return Returns the instructions.
	 */
	public String getLookupInstructions() {
		return Utilities.getApplicationConstant(EdenConstants.WORKGROUP_SEARCH_INSTRUCTION_KEY);
	}

	/**
	 * @return Returns the title.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return Returns the returnLocation.
	 */
	public String getReturnLocation() {
		return returnLocation;
	}

	/**
	 * @return Returns the columns.
	 */
	public List getColumns() {
		return columns;
	}

	public String getHtmlMenuBar() {
		if (!KEWServiceLocator.getWorkgroupService().getCapabilities().isCreateSupported()) {
			return "";
		}
		return "<a href=\"" + UrlResolver.getInstance().getWorkgroupUrl() + "\" >Create new workgroup</a>";
	}

	public List getRows() {
		return rows;
	}

    public ExportDataSet export(ExportFormat format, Object exportCriteria) throws Exception {
        List searchResults = (List)exportCriteria;
        ExportDataSet dataSet = new ExportDataSet(format);
        dataSet.getWorkgroups().addAll(searchResults);
        return dataSet;
    }

    public List getSupportedExportFormats() {
        return EdenConstants.STANDARD_FORMATS;
    }

}