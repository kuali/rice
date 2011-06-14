/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.kew.docsearch;

import java.util.List;

import org.kuali.rice.krad.web.ui.Row;


/**
 * This interface is used to define pre document search screen display processes for
 * a particular document type.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public interface DocumentSearchCriteriaProcessor {

	public static final String PERSON_LOOKUPABLE = "UserLookupableImplService";
	public static final String WORKGROUP_LOOKUPABLE = "WorkGroupLookupableImplService";
	public static final String DOC_TYP_LOOKUPABLE = "DocumentTypeLookupableImplService";

    public static final String CRITERIA_KEYS_SUFFIX_RANGE_LOWER_BOUND = "From";
    public static final String CRITERIA_KEYS_SUFFIX_RANGE_UPPER_BOUND = "To";

    public static final String CRITERIA_KEY_NAMED_SEARCH = "namedSearch";
    public static final String CRITERIA_KEY_DOC_TYPE_FULL_NAME = "docTypeFullName";
    public static final String CRITERIA_KEY_INITIATOR = "initiator";
    public static final String CRITERIA_KEY_DOCUMENT_ID = "documentId";
    public static final String CRITERIA_KEY_VIEWER_ID = "viewerId";
    public static final String CRITERIA_KEY_APPROVER_ID = "approverId";
    public static final String CRITERIA_KEY_WORKGROUP_VIEWER = "workgroupViewer";
    public static final String CRITERIA_KEY_WORKGROUP_VIEWER_ID = "workgroupViewerId";
    public static final String CRITERIA_KEY_APPLICATION_DOCUMENT_ID = "applicationDocumentId";
    public static final String CRITERIA_KEY_DOCUMENT_TITLE = "documentTitle";
    public static final String CRITERIA_KEY_DOCUMENT_ROUTE_STATUS = "documentRouteStatus";
    public static final String CRITERIA_KEY_DOCUMENT_ROUTE_NODE = "documentRouteNode";
    public static final String CRITERIA_KEY_CREATE_DATE = "createDate";
    public static final String CRITERIA_KEY_LAST_MODIFIED_DATE = "lastModifiedDate";
    public static final String CRITERIA_KEY_FINALIZED_DATE = "finalizedDate";
    public static final String CRITERIA_KEY_APPROVED_DATE = "approvedDate";
    public static final String CRITERIA_KEY_APP_DOC_STATUS = "appDocStatus";
    public static final String CRITERIA_KEY_STATUS_TRANSITION_DATE = "statusTransitionDate";

	public void setSearchingUser(String principalId);

	public void setDocSearchCriteriaDTO(DocSearchCriteriaDTO docSearchCriteriaDTO);

	public DocSearchCriteriaDTO getDocSearchCriteriaDTO();

	public boolean isHeaderBarDisplayed();

    public Boolean isAdvancedSearchCriteriaDisplayed();

    public Boolean isBasicSearchCriteriaDisplayed();

    public List<Row> processSearchableAttributeRowsForBasicSearch(List<Row> searchableAttributeRows);

    public List<Row> processSearchableAttributeRowsForAdvancedSearch(List<Row> searchableAttributeRows);

	public StandardDocSearchCriteriaManager getBasicSearchManager();

	public StandardDocSearchCriteriaManager getAdvancedSearchManager();
}
