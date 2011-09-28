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
package org.kuali.rice.krad.uif;

/**
 * General constants used within the User Interface Framework
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifConstants {
    public static final String CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME = "methodToCall";
    public static final String DEFAULT_MODEL_NAME = "KualiForm";
    public static final String DEFAULT_VIEW_NAME = "default";
    public static final String SPRING_VIEW_ID = "ApplicationView";

    public static enum Position {
        BOTTOM, LEFT, RIGHT, TOP
    }

    public static enum Order {
        FIRST
    }

    public static enum NavigationType {
        VERTICAL_MENU, HORIZONTAL_TABS
    }

    public static enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public static enum MessageType {
        NORMAL, INSTRUCTIONAL, CONSTRAINT, REQUIRED, HELP_SUMMARY, HELP_DESCRIPTION
    }

    public static enum ViewType {
        DEFAULT, DOCUMENT, INQUIRY, LOOKUP, MAINTENANCE, INCIDENT
    }

    public static enum ControlType {
        CHECKBOX, CHECKBOXGROUP, FILE, GROUP, HIDDEN, RADIOGROUP, SELECT,
        TEXTAREA, TEXT, USER
    }

    public static class MethodToCallNames {
        public static final String NAVIGATE = "navigate";
        public static final String START = "start";
        public static final String SAVE = "save";
        public static final String SEARCH = "search";
        public static final String CLOSE = "close";
        public static final String ADD_LINE = "addLine";
        public static final String DELETE_LINE = "deleteLine";
        public static final String REFRESH = "refresh";
        public static final String CANCEL = "cancel";
        public static final String UPDATE_COMP = "updateComponent";
        public static final String TOGGLE_INACTIVE = "toggleInactiveRecordDisplay";
    }

    public static class LayoutComponentOptions {
        public static final String COLUMN_SPAN = "colSpan";
        public static final String ROW_SPAN = "rowSpan";
    }

    public static class IdSuffixes {
        public static final String ADD_LINE = "_addLine";
        public static final String ATTRIBUTE = "_attribute";
        public static final String CONSTRAINT = "_constraint";
        public static final String DIRECT_INQUIRY = "_directinquiry";
        public static final String DIV = "_div";
        public static final String ERRORS = "_errors";
        public static final String INSTRUCTIONAL = "_instructional";
        public static final String LABEL = "_label";
        public static final String SUGGEST = "_suggest";
        public static final String QUICK_FINDER = "_quickfinder";
    }

    public static class ViewPhases {
        public static final String INITIALIZE = "INITIALIZE";
        public static final String APPLY_MODEL = "APPLY_MODEL";
        public static final String FINALIZE = "FINALIZE";
    }

    public static class ViewStatus {
        public static final String CREATED = "C";
        public static final String INITIALIZED = "I";
        public static final String FINAL = "F";
    }

    public static final String EL_PLACEHOLDER_PREFIX = "@{";
    public static final String EL_PLACEHOLDER_SUFFIX = "}";
    public static final String NO_BIND_ADJUST_PREFIX = "form.";
    public static final String DATA_OBJECT_BIND_ADJUST_PREFIX = "do.";

    public static class ContextVariableNames {
        public static final String COLLECTION_GROUP = "collectionGroup";
        public static final String CONFIG_PROPERTIES = "ConfigProperties";
        public static final String COMPONENT = "component";
        public static final String CONSTANTS = "Constants";
        public static final String DOCUMENT_ENTRY = "DocumentEntry";
        public static final String INDEX = "index";
        public static final String IS_ADD_LINE = "isAddLine";
        public static final String LINE = "line";
        public static final String MANAGER = "manager";
        public static final String NODE = "node";
        public static final String PARENT = "parent";
        public static final String VIEW = "view";
        public static final String VIEW_HELPER = "ViewHelper";
    }

    public static class TableToolsKeys {
        public static final String SDOM = "sDom";
        public static final String LANGUAGE = "oLanguage";
        public static final String EMPTY_TABLE = "sEmptyTable";
        public static final String AO_COLUMNS = "aoColumns";
        public static final String SORT_SKIP_ROWS = "aiSortingSkipRows";
        public static final String SORT_DATA_TYPE = "sSortDataType";
        public static final String SORTABLE = "bSortable";
        public static final String SORT_TYPE = "sType";
        public static final String TABLE_SORT = "bSort";
    }

    public static class TableToolsValues {
        public static final String DOM_TEXT = "dom-text";
        public static final String DOM_SELECT = "dom-select";
        public static final String DOM_CHECK = "dom-checkbox";
        public static final String DOM_RADIO = "dom-radio";
        public static final String NUMERIC = "numeric";
        public static final String DATE = "kuali_date";
        public static final String PERCENT = "kuali_percent";
        public static final String CURRENCY = "kuali_currency";
        public static final String TIMESTAMP = "kuali_timestamp";

        public static final int ADD_ROW_DEFAULT_INDEX = 0;
    }

    public static class TitleAppendTypes {
        public static final String DASH = "dash";
        public static final String PARENTHESIS = "parenthesis";
        public static final String REPLACE = "replace";
        public static final String NONE = "none";
    }

    public static class UrlParams {
        public static final String SHOW_HISTORY = "showHistory";
        public static final String SHOW_HOME = "showHome";
        public static final String FORM_KEY = "formKey";
        public static final String PAGE_ID = "pageId";
        public static final String HISTORY = "history";
    }

    public static class Messages {
        public static final String VALIDATION_MSG_KEY_PREFIX = "validation.";
    }

    public static class MessageKeys {
        public static final String QUERY_DATA_NOT_FOUND = "query.dataNotFound";
    }

    public static class ClientSideVariables {
        public static final String KRAD_IMAGE_LOCATION = "kradImageLocation";
    }

    public static class RefreshCallerTypes {
        public static final String LOOKUP = "LOOKUP";
        public static final String MULTI_VALUE_LOOKUP = "MULTI_VALUE_LOOKUP";
        public static final String QUESTION = "QUESTION";
    }
}
