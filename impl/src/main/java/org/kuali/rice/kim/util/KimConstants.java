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
package org.kuali.rice.kim.util;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class KimConstants {

	public static final String TAX_EXT_ID_TYPE = "TAX";

	public static final String STAFF_AFFILIATION_TYPE = "STAFF";
	public static final String STUDENT_AFFILIATION_TYPE = "STUDENT";
	public static final String AFFILIATE_AFFILIATION_TYPE = "AFFILIATE";
	public static final String FACULTY_AFFILIATION_TYPE = "FACULTY";
	public static final String DOC_STATUS = "Route Status Code";
    public static final String KEY_EDIT_MODE = "keyEditMode";
	public static final String PERMISSION_UNMARK = "Unmark";
	public static final String PERMISSION_UNMASK_PROPERTY = "Full Unmask Field";
	public static final String PERMISSION_PARTIALLY_UNMASK_PROPERTY = "Partial Unmask Field";
	public static final String PERMISSION_VIEW_PROPERTY = "View Inquiry or Maintenance Document Field(s)";
	public static final String PERMISSION_EDIT_PROPERTY = "Modify Maintenance Document Field(s)";
	public static final String PERMISSION_EDIT_DOCUMENT = "Edit Document";
	public static final String PERMISSION_COPY_DOCUMENT = "Copy Document";
	public static final String PERMISSION_OPEN_DOCUMENT = "Open Document";
	public static final String PERMISSION_TAKE_REQUESTED_ACTION = "Take Requested Action";
	public static final String PERMISSION_INITIATE_DOCUMENT = "Initiate Document";
	public static final String PERMISSION_BLANKET_APPROVE_DOCUMENT ="Blanket Approve Document";
	public static final String PERMISSION_CANCEL_DOCUMENT ="Cancel Document";
	public static final String PERMISSION_SAVE_DOCUMENT ="Save Document";
	public static final String PERMISSION_ROUTE_DOCUMENT ="Route Document";
	public static final String PERMISSION_PERFORM_ROUTE_REPORT = "Perform Route Report";
	public static final String PERMISSION_APPROVE_DOCUMENT = "Approve Document";
	public static final String PERMISSION_ADD_NOTE = "Add Note";
	public static final String PERMISSION_ERROR_CORRECT_DOCUMENT = "Error Correct Document";
	public static final String PERMISSION_USE_TRANSACTIONAL_DOCUMENT = "Use Transactional Document";
	public static final String DOCUMENT_STATUS_PERMISSION_TYPE = "DocumentStatusComponentAttributePermissionType";
	public static final String KIM_ATTRIB_EDIT_MODE = "editMode";
	public static final String USER_IS_INITIATOR = "userIsInitiator";
	public static final String KIM_ATTRIB_ACTION_CLASS ="actionClass";	
	public static final String KIM_ATTRIB_ACTION_REQUEST_TYPE_CODE ="actionRequestCd";		
	public static final String KIM_ATTRIB_COMPONENT_CLASS ="componentClass";		
	public static final String KIM_ATTRIB_ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL ="actionDetailsAtRoleMemberLevel";	
	public static final String KIM_ATTRIB_REQUIRED ="required";	
	public static final String KIM_ATTRIB_BEAN_NAME ="beanName";	
	public static final String KIM_ATTRIB_CAMPUS_CODE = "campusCode";
	public static final String KIM_ATTRIB_TYPE_CODE ="typeCd";
	public static final String KIM_ATTRIB_CREATED_SELF_ONLY ="createdBySelfOnly";	
	public static final String KIM_ATTRIB_NAMESPACE_CODE = "Namespace Code";
	public static final String KIM_ATTRIB_DOCUMENT_TYPE_NAME = "name";
	public static final String KIM_ATTRIB_ROUTE_STATUS_CODE = "docRouteStatus";
	public static final String KIM_ATTRIB_ROUTE_NODE_NAME = "docRouteName";
	public static final String KIM_ATTRIB_DOCUMENT_NUMBER = "documentNumber";
	public static final String KIM_ATTRIB_PROPERTY_NAME = "propertyName";
	public static final String KIM_ATTRIB_COMPONENT_NAME = "parameterDetailTypeCode";
	public static final String KIM_ATTRIB_EXISTING_RECORDS_ONLY = "existingRecordsOnly";
	public static final String KIM_ATTRIB_PARAMETER_NAME = "parameterName";
	public static final String KIM_ATTRIB_PRINCIPAL_ID = "principalId";
	public static final String KIM_ATTRIB_DESCEND_HIERARCHY = "descendHierarchy";
	public static final String KIM_ATTRIB_ACTION_REQUEST_CODE = "actionRequestCd";
	public static final String KIM_ATTRIB_FROM_AMOUNT = "fromAmount";
	public static final String KIM_ATTRIB_TO_AMOUNT = "toAmount";
	public static final String KIM_ATTRIB_FIELD_NAME = "fieldName";
	
	public static final String KIM_ATTRIB_ACTION = "action";
	public static final String KIM_ROLE_NAME_USER = "User";
	public static final String KIM_ROLE_NAME_BILLER = "Biller";
	public static final String KIM_ROLE_NAME_PROCESSOR = "Processor";	
	public static final String KIM_ROLE_NAME_ACCOUNT_SUPERVISOR = "Account Supervisor";
	public static final String KIM_ROLE_NAME_FISCAL_OFFICER = "Fiscal Officer";
	public static final String KIM_ROLE_NAME_FISCAL_OFFICER_PRIMARY_DELEGATE = "Fiscal Officer Primary Delegate";
	public static final String KIM_ROLE_NAME_FISCAL_OFFICER_SECONDARY_DELEGATE = "Fiscal Officer Secondary Delegate";
	public static final String KIM_ROLE_NAME_AWARD_SECONDARY_DIRECTOR = "Award Project Director";
	public static final String KIM_ROLE_NAME_ACTIVE_FACULTY_OR_STAFF = "Active Faculty or Staff";
	public static final String KIM_ROLE_NAME_ACTIVE_PROFESSIONAL_EMPLOYEE = "Active Professional Employee";
    public static final String KIM_ROLE_NAME_ROUTING_FORM_ADHOC_ACKNOWLEDGER = "Routing Form Ad Hoc Acknowledger";
    public static final String KIM_ROLE_NAME_PREAWARD_PROJECT_DIRECTOR = "Pre-Award Project Director";
	
	public static final String PERMISSION_TEMPLATE_CLAIM_ELECTRONIC_PAYMENT = "Claim Electronic Payment";
	
	public static final String DEFAULT_PERMISSION_TYPE_SERVICE = "defaultPermissionTypeService";
	public static final String KIM_AD_HOC  ="AdHoc";
	
	@Deprecated
	public static final String TEMP_GROUP_NAMESPACE = "KFS";
	
}
