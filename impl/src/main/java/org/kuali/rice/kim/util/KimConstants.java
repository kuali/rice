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
	public static final String KIM_TYPE_DEFAULT_NAMESPACE = "KUALI";
	public static final String KIM_TYPE_DEFAULT_NAME = "Default";
	public static final String KIM_GROUP_DEFAULT_NAMESPACE_CODE = "KFS";
	public static final String KIM_GROUP_KFS_NAMESPACE_CODE = "KFS";
	public static final String KIM_GROUP_WORKFLOW_NAMESPACE_CODE = "KR-WKFLW";

	public class PropertyNames {
		public static final String PRINCIPAL_ID = "principalId";
		public static final String PRINCIPAL_NAME = "principalName";
	}

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

    	public static final String MODIFY_ENTITY = "Modify Entity";
    	public static final String POPULATE_GROUP = "Populate Group";
    	public static final String ASSIGN_ROLE = "Assign Role";
    	public static final String GRANT_PERMISSION = "Grant Permission";
    	public static final String GRANT_RESPONSIBILITY = "Grant Responsibility";

    	public static final String LOOK_UP_RECORDS = "Look Up Records";
    	public static final String INQUIRE_INTO_RECORDS = "Inquire Into Records";
    	public static final String USE_SCREEN = "Use Screen";

    	public static final String UPLOAD_BATCH_INPUT_FILES = "Upload Batch Input File(s)";
    	public static final String MODIFY_BATCH_JOB = "Modify Batch Job";
    	public static final String PERFORM_CUSTOM_MAINTENANCE_DOCUMENT_FUNCTION ="Perform Custom Maintenance Document Function";
    }
	
	public static class PermissionNames {
		public static final String LOG_IN = "Log In";
	}

	@Deprecated
	public static final String TEMP_GROUP_NAMESPACE = "KFS";
	
	public static final String NAME_VALUE_SEPARATOR = " : ";
	public static final String COMMA_SEPARATOR = ", ";
	public static final String OR_OPERATOR = "|";

}
