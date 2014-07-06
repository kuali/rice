/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif;

/**
 * Constants for parameter names that need to be retrieved from general
 * <code>Map</code> implementations (like the request, action parameters map,
 * component template options, component layout options)
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifParameters {

    public static final String AJAX_REQUEST = "ajaxRequest";
    public static final String AJAX_RETURN_TYPE = "ajaxReturnType";
	public static final String AUTO_SEARCH = "autoSearch";
	public static final String BASE_LOOKUP_URL = "baseLookupUrl";
	public static final String METHOD_TO_CALL = "methodToCall";
    public static final String CLIENT_VIEW_STATE = "clientViewState";
    public static final String CONVERSION_FIELDS = "conversionFields";
    public static final String CONFIG_PROPERTIES = "ConfigProperties";
	public static final String DATA_OBJECT_CLASS_NAME = "dataObjectClassName";
	public static final String RENDER_CRITERIA_ACTIONS = "renderCriteriaActions";
    public static final String DOCUMENT_CLASS = "documentClass";
    public static final String DOC_NUM = "docNum";
    public static final String DOC_TYPE_NAME = "docTypeName";
    public static final String FORMAT_TYPE = "formatType";
    public static final String FORM_KEY = "formKey";
    public static final String FORM_MANAGER = "formManager";
	public static final String RENDER_RETURN_LINK = "renderReturnLink";
    public static final String HIDE_CRITERIA_ON_SEARCH = "hideCriteriaOnSearch";
    public static final String JUMP_TO_ID = "jumpToId";
    public static final String FOCUS_ID = "focusId";
	public static final String RENDER_LOOKUP_CRITERIA = "renderlookupCriteria";
    public static final String LABEL = "label";
	public static final String LOOKUP_PARAMETERS = "lookupParameters";
    public static final String LOOKUP_COLLECTION_NAME = "lookupCollectionName";
    public static final String LOOKUP_COLLECTION_ID = "lookupCollectionId";
    public static final String MULTIPLE_VALUES_SELECT = "multipleValuesSelect";
	public static final String READ_ONLY_FIELDS = "readOnlyFields";
    public static final String REDIRECTED_INQUIRY = "redirectedInquiry";
    public static final String REDIRECTED_LOOKUP = "redirectedLookup";
	public static final String REFERENCES_TO_REFRESH = "referencesToRefresh";
    public static final String REFRESH_STATUS = "refreshStatus";
    public static final String RENDER_HELPER_METHODS = "HelperMethods";
    public static final String RETURN_BY_SCRIPT = "returnByScript";
	public static final String RETURN_LOCATION = "returnLocation";
	public static final String RETURN_FORM_KEY = "returnFormKey";
    public static final String RETURN_FROM_DIALOG = "returnFromDialog";
    public static final String REQUESTED_COMPONENT_ID = "reqComponentId";
    public static final String REQUEST = "request";
    public static final String SHOW_INACTIVE_RECORDS = "showInactiveRecords";
	public static final String SELECTED_COLLECTION_PATH = "selectedCollectionPath";
    public static final String SELECTED_COLLECTION_ID = "selectedCollectionId";
	public static final String SELECTED_LINE_INDEX = "selectedLineIndex";
    public static final String SELECTED_LINE_VALUES = "selectedLineValues";
    public static final String MULIT_VALUE_RETURN_FILEDS = "multiValueReturnFields";
    public static final String SESSION_ID = "sessionId";
	public static final String RENDER_MAINTENANCE_LINKS = "renderMaintenanceLinks";
	public static final String RENDER_RESULT_ACTIONS = "renderResultActions";
	public static final String PAGE_ID = "pageId";
    public static final String PERFORM_DIRTY_CHECK = "performDirtyCheck";
	public static final String NAVIGATE_TO_PAGE_ID = "navigateToPageId";
    public static final String VALUE = "value";
    public static final String VIEW = "view";
	public static final String VIEW_ID = "viewId";
	public static final String VIEW_NAME = "viewName";
	public static final String VIEW_TYPE_NAME = "viewTypeName";
	public static final String ACTION_TYPE = "actionType";
	public static final String ADD_LINE = "addLine";
    public static final String QUERY_PARAMETER = "queryParameters";
    public static final String QUERY_FIELD_ID = "queryFieldId";
    public static final String QUERY_TERM = "queryTerm";
    public static final String QUICKFINDER_ID = "quickfinderId";
    public static final String UPDATE_COMPONENT_ID = "updateComponentId";
    public static final String REQUESTED_FORM_KEY = "requestedFormKey";
    public static final String MESSAGE_TO_DISPLAY = "messageToDisplay";

    public static class Attributes {
        public static final String VIEW_LIFECYCLE_COMPLETE = "ViewLifecycleComplete";
    }
}
