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
	public static final String PERMISSION_AD_HOC_REVIEW_DOCUMENT = "Ad Hoc Review Document";

	public static final String PERMISSION_USE_TRANSACTIONAL_DOCUMENT = "Use Transactional Document";
	public static final String DOCUMENT_STATUS_PERMISSION_TYPE = "DocumentStatusComponentAttributePermissionType";
	
	// JHK: Deprecated constants which need to be moved to KimAttributes or KFS
	
    @Deprecated
	public static final String KIM_ATTRIB_EDIT_MODE = "editMode";
	public static final String USER_IS_INITIATOR = "userIsInitiator";
	@Deprecated
	public static final String KIM_ATTRIB_ACTION_CLASS ="actionClass";	
    @Deprecated
	public static final String KIM_ATTRIB_ACTION_REQUEST_TYPE_CODE ="actionRequestCd";		
    @Deprecated
	public static final String KIM_ATTRIB_COMPONENT_CLASS ="componentClass";		
    @Deprecated
	public static final String KIM_ATTRIB_ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL ="actionDetailsAtRoleMemberLevel";	
    @Deprecated
	public static final String KIM_ATTRIB_REQUIRED ="required";	
    @Deprecated
	public static final String KIM_ATTRIB_BEAN_NAME ="beanName";	
    @Deprecated
	public static final String KIM_ATTRIB_CAMPUS_CODE = "campusCode";
    @Deprecated
	public static final String KIM_ATTRIB_TYPE_CODE ="typeCd";
    @Deprecated
	public static final String KIM_ATTRIB_CREATED_SELF_ONLY ="createdBySelfOnly";	
    @Deprecated
	public static final String KIM_ATTRIB_NAMESPACE_CODE = "Namespace Code";
    @Deprecated
	public static final String KIM_ATTRIB_DOCUMENT_TYPE_NAME = "name";
    @Deprecated
	public static final String KIM_ATTRIB_ROUTE_STATUS_CODE = "docRouteStatus";
    @Deprecated
	public static final String KIM_ATTRIB_ROUTE_NODE_NAME = "docRouteName";
    @Deprecated
	public static final String KIM_ATTRIB_DOCUMENT_NUMBER = "documentNumber";
    @Deprecated
	public static final String KIM_ATTRIB_PROPERTY_NAME = "propertyName";
    @Deprecated
	public static final String KIM_ATTRIB_COMPONENT_NAME = "parameterDetailTypeCode";
    @Deprecated
	public static final String KIM_ATTRIB_EXISTING_RECORDS_ONLY = "existingRecordsOnly";
    @Deprecated
	public static final String KIM_ATTRIB_PARAMETER_NAME = "parameterName";
    @Deprecated
	public static final String KIM_ATTRIB_PRINCIPAL_ID = "principalId";
    @Deprecated
	public static final String KIM_ATTRIB_DESCEND_HIERARCHY = "descendHierarchy";
    @Deprecated
	public static final String KIM_ATTRIB_ACTION_REQUEST_CODE = "actionRequestCd";
    @Deprecated
	public static final String KIM_ATTRIB_FROM_AMOUNT = "fromAmount";
    @Deprecated
	public static final String KIM_ATTRIB_TO_AMOUNT = "toAmount";
    @Deprecated
	public static final String KIM_ATTRIB_FIELD_NAME = "fieldName";
	
    @Deprecated
	public static final String KIM_ATTRIB_ACTION = "action";
	public static final String KIM_ROLE_NAME_USER = "User";
    @Deprecated
	public static final String KIM_ROLE_NAME_ACCOUNT_SUPERVISOR = "Account Supervisor";
    @Deprecated
	public static final String KIM_ROLE_NAME_FISCAL_OFFICER = "Fiscal Officer";
    @Deprecated
	public static final String KIM_ROLE_NAME_FISCAL_OFFICER_PRIMARY_DELEGATE = "Fiscal Officer Primary Delegate";
    @Deprecated
	public static final String KIM_ROLE_NAME_FISCAL_OFFICER_SECONDARY_DELEGATE = "Fiscal Officer Secondary Delegate";
    @Deprecated
	public static final String KIM_ROLE_NAME_AWARD_SECONDARY_DIRECTOR = "Award Project Director";
    @Deprecated
	public static final String KIM_ROLE_NAME_ACTIVE_FACULTY_OR_STAFF = "Active Faculty or Staff";
    @Deprecated
	public static final String KIM_ROLE_NAME_ACTIVE_PROFESSIONAL_EMPLOYEE = "Active Professional Employee";
    @Deprecated
    public static final String KIM_ROLE_NAME_ROUTING_FORM_ADHOC_ACKNOWLEDGER = "Routing Form Ad Hoc Acknowledger";
    @Deprecated
    public static final String KIM_ROLE_NAME_PREAWARD_PROJECT_DIRECTOR = "Pre-Award Project Director";
    public static final String KIM_ROLE_NAME_ACTION_REQUEST_RECIPIENT = "Action Request Recipient";
    public static final String KIM_ROLE_NAME_INITIATOR = "Initiator";
    public static final String KIM_ROLE_NAME_INITIATOR_OR_REVIEWER = "Initiator or Reviewer";
    public static final String KIM_ROLE_NAME_ROUTER = "Router";
    
    @Deprecated
    public static final String PERMISSION_TEMPLATE_CLAIM_ELECTRONIC_PAYMENT = "Claim Electronic Payment";
	
	public static final String DEFAULT_PERMISSION_TYPE_SERVICE = "defaultPermissionTypeService";
	
	public static final String PRE_ROUTING_DOC_STATUS = "PreRoute";
	
	@Deprecated
	public static final String TEMP_GROUP_NAMESPACE = "KFS";
	
}
