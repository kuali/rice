/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a description of what this class does - jonathan don't forget to fill this in.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KimConstants {

	public static final String NAMESPACE_CODE = "KR-IDM";
	public static final String KIM_TYPE_DEFAULT_NAMESPACE = "KUALI";
	public static final String KIM_TYPE_DEFAULT_NAME = "Default";
	public static final String KIM_GROUP_DEFAULT_NAMESPACE_CODE = "KUALI";
	public static final String KIM_GROUP_WORKFLOW_NAMESPACE_CODE = "KR-WKFLW";
	public static final String RESTRICTED_DATA_MASK = "Xxxxxx";

	//Kim services constants for API
	public static final String KIM_IDENTITY_MANAGEMENT_SERVICE = "kimIdentityManagementService";
	public static final String KIM_PERSON_SERVICE = "personService";
	public static final String DEFAULT_KIM_TYPE_SERVICE = "kimTypeService";

	public class EntityTypes {
		public static final String PERSON = "PERSON";
		public static final String SYSTEM = "SYSTEM";
	}

	public class PersonExternalIdentifierTypes {
		public static final String TAX = "TAX";
	}
	public class PersonAffiliationTypes {
		public static final String STAFF_AFFILIATION_TYPE = "STAFF";
		public static final String STUDENT_AFFILIATION_TYPE = "STUDENT";
		public static final String AFFILIATE_AFFILIATION_TYPE = "AFFILIATE";
		public static final String FACULTY_AFFILIATION_TYPE = "FACULTY";
	}

	public class PermissionTemplateNames {
        public static final String FULL_UNMASK_FIELD = "Full Unmask Field";
    	public static final String PARTIAL_UNMASK_FIELD = "Partial Unmask Field";
    	public static final String VIEW_FIELD = "View Inquiry or Maintenance Document Field";
    	public static final String MODIFY_FIELD = "Modify Maintenance Document Field";
    	public static final String VIEW_SECTION = "View Inquiry or Maintenance Document Section";
    	public static final String MODIFY_SECTION = "Modify Maintenance Document Section";
    	public static final String CREATE_MAINTAIN_RECORDS = "Create / Maintain Record(s)";

    	public static final String EDIT_DOCUMENT = "Edit Document";
    	public static final String COPY_DOCUMENT = "Copy Document";
    	public static final String OPEN_DOCUMENT = "Open Document";
    	public static final String TAKE_REQUESTED_ACTION = "Take Requested Action";
    	public static final String INITIATE_DOCUMENT = "Initiate Document";
    	public static final String BLANKET_APPROVE_DOCUMENT ="Blanket Approve Document";
    	public static final String CANCEL_DOCUMENT ="Cancel Document";
    	public static final String SAVE_DOCUMENT ="Save Document";
    	public static final String ROUTE_DOCUMENT ="Route Document";
    	public static final String APPROVE_DOCUMENT = "Approve Document";
    	public static final String AD_HOC_REVIEW_DOCUMENT = "Ad Hoc Review Document";

    	public static final String ADD_NOTE_ATTACHMENT = "Add Note / Attachment";
    	public static final String VIEW_NOTE_ATTACHMENT = "View Note / Attachment";
    	public static final String DELETE_NOTE_ATTACHMENT = "Delete Note / Attachment";

    	public static final String USE_TRANSACTIONAL_DOCUMENT = "Use Transactional Document";

    	public static final String POPULATE_GROUP = "Populate Group";
    	public static final String ASSIGN_ROLE = "Assign Role";
    	public static final String GRANT_PERMISSION = "Grant Permission";
    	public static final String GRANT_RESPONSIBILITY = "Grant Responsibility";

    	public static final String LOOK_UP_RECORDS = "Look Up Records";
    	public static final String INQUIRE_INTO_RECORDS = "Inquire Into Records";
    	public static final String USE_SCREEN = "Use Screen";

    	public static final String PERFORM_CUSTOM_MAINTENANCE_DOCUMENT_FUNCTION ="Perform Custom Maintenance Document Function";
    	public static final String MAINTAIN_SYSTEM_PARAMETER = "Maintain System Parameter";
    	public static final String SEND_AD_HOC_REQUEST ="Send Ad Hoc Request";
	}

	public static class PermissionNames {
		public static final String LOG_IN = "Log In";
		public static final String ADMIN_PESSIMISTIC_LOCKING = "Administer Pessimistic Locking";
		public static final String OVERRIDE_ENTITY_PRIVACY_PREFERENCES = "Override Entity Privacy Preferences";
    	public static final String MODIFY_ENTITY = "Modify Entity";
	}

	public static class KimUIConstants {
		public static final String NAME_VALUE_SEPARATOR = " : ";
		public static final String COMMA_SEPARATOR = ", ";
		public static final String OR_OPERATOR = "|";
		public static final String URL_SEPARATOR = "/";
		public static final String PARAMETERIZED_URL_SEPARATOR = "%2F";
		public static final String KIM_URL_KEY = "kim.url";
		public static final String KIM_APPLICATION = "kim";
		public static final String MEMBER_NAME = "memberName";
		public static final String MEMBER_NAMESPACE_CODE = "memberNamespaceCode";
		public static final String MEMBER_TYPE_PRINCIPAL_CODE = "P";
		public static final String MEMBER_TYPE_GROUP_CODE = "G";
		public static final String MEMBER_TYPE_ROLE_CODE = "R";
		public static final String MEMBER_TYPE_PRINCIPAL = "Principal";
		public static final String MEMBER_TYPE_GROUP = "Group";
		public static final String MEMBER_TYPE_ROLE = "Role";
		public static final Map<String, String> KIM_MEMBER_TYPES_MAP = getKimMemberTypesMap();
	    private static Map<String, String> getKimMemberTypesMap() {
	    	Map<String, String> kimDocumentActionsMap = new HashMap<String, String>();
	    	kimDocumentActionsMap.put(MEMBER_TYPE_PRINCIPAL_CODE, MEMBER_TYPE_PRINCIPAL);
	    	kimDocumentActionsMap.put(MEMBER_TYPE_GROUP_CODE, MEMBER_TYPE_GROUP);
	    	kimDocumentActionsMap.put(MEMBER_TYPE_ROLE_CODE, MEMBER_TYPE_ROLE);
	        return kimDocumentActionsMap;
	    }

		public static final String KIM_ROLE_DOCUMENT_TYPE_NAME = "IdentityManagementRoleDocument";
		public static final String KIM_GROUP_DOCUMENT_TYPE_NAME = "IdentityManagementGroupDocument";
		public static final String KIM_PERSON_DOCUMENT_TYPE_NAME = "IdentityManagementPersonDocument";
		public static final String KIM_REVIEW_RESPONSIBILITY_DOCUMENT_TYPE_NAME = "IdentityManagementReviewResponsibilityMaintenanceDocument";
		public static final String KIM_ROLE_DOCUMENT_SHORT_KEY = "IMRD";
		public static final String KIM_GROUP_DOCUMENT_SHORT_KEY = "IMGD";
		public static final String KIM_PERSON_DOCUMENT_SHORT_KEY = "IMPD";
		public static final String KIM_ROLE_DOCUMENT_ACTION = "identityManagementRoleDocument.do";
		public static final String KIM_ROLE_INQUIRY_ACTION = "identityManagementRoleInquiry.do";
		public static final String KIM_PERSON_DOCUMENT_ACTION = "identityManagementPersonDocument.do";
		public static final String KIM_PERSON_INQUIRY_ACTION = "identityManagementPersonInquiry.do";
		public static final String KIM_GROUP_DOCUMENT_ACTION = "identityManagementGroupDocument.do";
		public static final String KIM_GROUP_INQUIRY_ACTION = "identityManagementGroupInquiry.do";
		public static final Map<String, String> KIM_DOCUMENTS_ACTIONS_MAP = getDocumentActionsMap();
	    private static Map<String, String> getDocumentActionsMap() {
	    	Map<String, String> kimDocumentActionsMap = new HashMap<String, String>();
	    	kimDocumentActionsMap.put(KIM_ROLE_DOCUMENT_SHORT_KEY, KIM_ROLE_DOCUMENT_ACTION);
	    	kimDocumentActionsMap.put(KIM_GROUP_DOCUMENT_SHORT_KEY, KIM_GROUP_DOCUMENT_ACTION);
	    	kimDocumentActionsMap.put(KIM_PERSON_DOCUMENT_SHORT_KEY, KIM_PERSON_DOCUMENT_ACTION);
	        return kimDocumentActionsMap;
	    }

		public static final Map<String, String> KIM_DOCUMENT_TYPE_NAMES_MAP = getDocumentTypeNamesMap();
	    private static Map<String, String> getDocumentTypeNamesMap() {
	    	Map<String, String> kimDocumentTypeNamesMap = new HashMap<String, String>();
			kimDocumentTypeNamesMap.put(KIM_ROLE_DOCUMENT_SHORT_KEY, KIM_ROLE_DOCUMENT_TYPE_NAME);
			kimDocumentTypeNamesMap.put(KIM_GROUP_DOCUMENT_SHORT_KEY, KIM_GROUP_DOCUMENT_TYPE_NAME);
			kimDocumentTypeNamesMap.put(KIM_PERSON_DOCUMENT_SHORT_KEY, KIM_PERSON_DOCUMENT_TYPE_NAME);
			return kimDocumentTypeNamesMap;
	    }

	    public static final String DELEGATION_PRIMARY = "P";
	    public static final String DELEGATION_SECONDARY = "S";
	    public static final String DELEGATION_PRIMARY_LABEL = "Primary";
	    public static final String DELEGATION_SECONDARY_LABEL = "Secondary";

	    public static final Map<String, String> DELEGATION_TYPES = getDelegationTypesMap();
	    private static Map<String, String> getDelegationTypesMap() {
	    	Map<String, String> delegationTypesMap = new HashMap<String, String>();
	    	delegationTypesMap.put(DELEGATION_PRIMARY, DELEGATION_PRIMARY_LABEL);
	    	delegationTypesMap.put(DELEGATION_SECONDARY, DELEGATION_SECONDARY_LABEL);
	    	return delegationTypesMap;
	    }

	    public static final String ROLE_LOOKUPABLE_IMPL = "roleLookupable";
	    public static final String KIM_DOCUMENT_ROLE_MEMBER_LOOKUPABLE_IMPL = "kimDocumentRoleMemberLookupable";
	    public static final String ROLE_MEMBER_LOOKUPABLE_IMPL = "roleMemberImplLookupable";
	    public static final String ROLE_MEMBERS_COLLECTION_NAME = "roleMembers";
	}

	public static class PrimaryKeyConstants {
		public static final String ENTITY_ID = "entityId";
		public static final String PRINCIPAL_ID = "principalId";
		public static final String ROLE_ID = "roleId";
		public static final String GROUP_ID = "groupId";
		public static final String KIM_TYPE_ID = "kimTypeId";
		public static final String RESPONSIBILITY_ID = "responsibilityId";
		public static final String PERMISSION_ID = "permissionId";
		public static final String DELEGATION_ID = "delegationId";
		public static final String RESPONSIBILITY_TEMPLATE_ID = "responsibilityTemplateId";
		public static final String PERMISSION_TEMPLATE_ID = "permissionTemplateId";
		public static final String MEMBER_ID = "memberId";
		public static final String DELEGATION_MEMBER_ID = "delegationMemberId";
		public static final String ROLE_MEMBER_ID = "roleMemberId";
		public static final String ROLE_RESPONSIBILITY_ID = "roleResponsibilityId";
		public static final String ROLE_RESPONSIBILITY_ACTION_ID = "roleResponsibilityActionId";
		public static final String KIM_PERMISSION_REQUIRED_ATTR_ID = "kimPermissionRequiredAttributeId";
		public static final String KIM_ATTRIBUTE_ID = "kimAttributeId";
		public static final String KIM_TYPE_CODE ="code";
	}

	public static class UniqueKeyConstants {
		public static final String NAMESPACE_CODE = "namespaceCode";
		public static final String PRINCIPAL_NAME = "principalName";
		public static final String GROUP_NAME = "groupName";
		public static final String ROLE_NAME = "roleName";
		public static final String PERMISSION_NAME = "name";
		public static final String RESPONSIBILITY_NAME = "name";
		public static final String PERMISSION_TEMPLATE_NAME = "name";
		public static final String RESPONSIBILITY_TEMPLATE_NAME = "name";
	}

	public static class SequenceNames {
		public static final String KRIM_PRNCPL_ID_S = "KRIM_PRNCPL_ID_S";
		public static final String KRIM_ENTITY_ID_S = "KRIM_ENTITY_ID_S";
		public static final String KRIM_ROLE_ID_S = "KRIM_ROLE_ID_S";
		public static final String KRIM_GROUP_ID_S = "KRIM_GRP_ID_S";
		public static final String KRIM_ROLE_PERM_ID_S = "KRIM_ROLE_PERM_ID_S";
		public static final String KRIM_ROLE_RSP_ID_S = "KRIM_ROLE_RSP_ID_S";
		public static final String KRIM_ROLE_MBR_ID_S = "KRIM_ROLE_MBR_ID_S";
		public static final String KRIM_DLGN_MBR_ID_S = "KRIM_DLGN_MBR_ID_S";
		public static final String KRIM_ROLE_RSP_ACTN_ID_S = "KRIM_ROLE_RSP_ACTN_ID_S";
		public static final String KRIM_DLGN_ID_S = "KRIM_DLGN_ID_S";
		public static final String KRIM_PERM_ID_S = "KRIM_PERM_ID_S";
		public static final String KRIM_RSP_ID_S = "KRIM_RSP_ID_S";
		public static final String KRIM_ATTR_DATA_ID_S = "KRIM_ATTR_DATA_ID_S";
	}

	/**
	 *
	 * KimGroupS can contain other KimGroupS and KimPrincipalS.
	 * Use these constants to flags their members with the appropriate member
	 * type code.
	 *
	 * @author Kuali Rice Team (rice.collab@kuali.org)
	 */
	public static class KimGroupMemberTypes {

		/**
		 * For group members that are themselves groups
		 */
		public static final String GROUP_MEMBER_TYPE = "G";

		/**
		 * For group members that are principals
		 */
		public static final String PRINCIPAL_MEMBER_TYPE = "P";
	}

}
