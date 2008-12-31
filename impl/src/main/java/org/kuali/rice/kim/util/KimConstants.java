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

	public static final String NAMESPACE_CODE = "KR-IDM";
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
	public static final String PERMISSION_VIEW_PROPERTY = "View Inquiry or Maintenance Document Field";
	public static final String PERMISSION_EDIT_PROPERTY = "Modify Maintenance Document Field";
	public static final String PERMISSION_VIEW_SECTION = "View Inquiry or Maintenance Document Section";
	public static final String PERMISSION_EDIT_SECTION = "Modify Maintenance Document Section";
	public static final String PERMISSION_CREATE_MAINTAIN_RECORDS = "Create / Maintain Record(s)";
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
	
	public static final String PERMISSION_MODIFY_ENTITY = "Modify Entity";
	public static final String PERMISSION_POPULATE_GROUP = "Populate Group";
	public static final String PERMISSION_ASSIGN_ROLE = "Assign Role";
	public static final String PERMISSION_GRANT_PERMISSION = "Grant Permission";
	public static final String PERMISSION_GRANT_RESPONSIBILITY = "Grant Responsibility";
	
	// JHK: Deprecated constants which need to be moved to KimAttributes or KFS

	public static final String USER_IS_INITIATOR = "userIsInitiator";
	public static final String KIM_ATTRIB_PRINCIPAL_ID = "principalId";
	
	public static final String KIM_ROLE_NAME_USER = "User";
    public static final String KIM_ROLE_NAME_ACTION_REQUEST_RECIPIENT = "Action Request Recipient";
    public static final String KIM_ROLE_NAME_INITIATOR = "Initiator";
    public static final String KIM_ROLE_NAME_INITIATOR_OR_REVIEWER = "Initiator or Reviewer";
    public static final String KIM_ROLE_NAME_ROUTER = "Router";

	
	public static final String DEFAULT_PERMISSION_TYPE_SERVICE = "defaultPermissionTypeService";
	
	public static final String PRE_ROUTING_ROUT_NAME = "PreRoute";
	
	@Deprecated
	public static final String TEMP_GROUP_NAMESPACE = "KFS";
	
	public static final String PERSON_ENTITY_TYPE = "PERSON";

	
}
