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
 * General constants used within the User Interface Framework.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifConstants {
    public static final String CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME = "methodToCall";
    public static final String DEFAULT_MODEL_NAME = "KualiForm";
    public static final String COMPONENT_MODEL_NAME = "Component";
    public static final String DEFAULT_VIEW_NAME = "default";
    public static final String COMPONENT_ID_PREFIX = "u";
    public static final String SUFFIX_SEPARATOR = "_";

    public static final String DEFAULT_THEMES_DIRECTORY = "/themes";
    public static final String DEFAULT_IMAGES_DIRECTORY = "images";
    public static final String DEFAULT_SCRIPTS_DIRECTORY = "scripts";
    public static final String DEFAULT_STYLESHEETS_DIRECTORY = "stylesheets";
    public static final String THEME_DERIVED_PROPERTY_FILE = "theme-derived.properties";
    public static final String THEME_CSS_FILES = "themeCssFiles";
    public static final String THEME_JS_FILES = "themeJsFiles";
    public static final String THEME_DEV_JS_FILES = "devJsIncludes";
    public static final String THEME_LESS_FILES = "themeLessFiles";

    public static final String SPRING_VIEW_ID = "/krad/WEB-INF/ftl/uifRender";
    public static final String SPRING_REDIRECT_ID = "/krad/WEB-INF/ftl/redirect";
    public static final String REDIRECT_PREFIX = "redirect:";

    public static final String EL_PLACEHOLDER_PREFIX = "@{";
    public static final String EL_PLACEHOLDER_SUFFIX = "}";
    public static final String NO_BIND_ADJUST_PREFIX = "#form.";
    public static final String DEFAULT_PATH_BIND_ADJUST_PREFIX = "#dp.";
    public static final String FIELD_PATH_BIND_ADJUST_PREFIX = "#fp.";
    public static final String LINE_PATH_BIND_ADJUST_PREFIX = "#lp.";
    public static final String NODE_PATH_BIND_ADJUST_PREFIX = "#np.";
    public static final String STRING_TEMPLATE_PARAMETER_PLACEHOLDER = "@";

    public static final String SPACE = " ";

    public static final String REQUEST_FORM = "requestForm";

    public static final String BLOCKUI_NAVOPTS = "navigation";
    public static final String BLOCKUI_REFRESHOPTS = "refresh";

    public static final String VALIDATE_VIEWS_ONBUILD = "validate.views.onbuild";

    public static final String MESSAGE_VIEW_ID = "Uif-MessageView";
    public static final String SESSION_TIMEOUT_VIEW_ID = "Uif-SessionTimeoutView";
    public static final String LOGGED_OUT_VIEW_ID = "Uif-LoggedOutView";
    public static final String GROUP_VALIDATION_DEFAULTS_MAP_ID = "Uif-GroupValidationMessages-DataDefaults";
    public static final String FIELD_VALIDATION_DEFAULTS_MAP_ID = "Uif-FieldValidationMessages-DataDefaults";
    public static final String ACTION_DEFAULTS_MAP_ID = "Uif-Action-DataDefaults";
    public static final String REQUIRED_INDICATOR_ID = "Uif-RequiredIndicator";
    public static final String REQUIRED_NEXT_STATE_INDICATOR_ID = "Uif-RequiredIndicator-ForNextState";
    public static final String REFERER = "Referer";
    public static final String NO_RETURN = "NO_RETURN";

    public static final String EXPORT_FILE_NAME = "export.xml";
    public static final String KUALI_FORM_ATTR = "KualiForm";

    public static final String ICON_ONLY_PLACEMENT = "ICON_ONLY";
    public static final String PROPERTY_EDITOR_REGISTRY = "rice.krad.uif.propertyEditorRegistry";

    public static enum ReadOnlyListTypes {
        DELIMITED, BREAK, OL, UL
    }

    public static enum Position {
        BOTTOM, LEFT, RIGHT, TOP
    }

    public static enum Order {
        FIRST, LINE_FIRST, NEXT_INPUT, SELF
    }

    public static enum NavigationType {
        VERTICAL_MENU, HORIZONTAL_TABS
    }

    public static enum Orientation {
        HORIZONTAL, VERTICAL
    }

    public static enum ViewType {
        DEFAULT, DOCUMENT, INQUIRY, LOOKUP, MAINTENANCE, INCIDENT, TRANSACTIONAL;
    }

    public static enum ControlType {
        CHECKBOX, CHECKBOXGROUP, FILE, GROUP, HIDDEN, RADIOGROUP, SELECT,
        TEXTAREA, TEXT, USER
    }

    public static enum WorkflowAction {
        SAVE, ROUTE, BLANKETAPPROVE, APPROVE, DISAPPROVE, CANCEL, FYI, ACKNOWLEDGE, COMPLETE, SENDADHOCREQUESTS, RECALL
    }

    public static enum SuperUserWorkflowAction {
        TAKEACTION, APPROVE, DISAPPROVE
    }

    /**
     * Enum of return types. Used to return the type of response being sent by the server to the client.
     */
    public enum AjaxReturnTypes {
        UPDATEPAGE("update-page"), UPDATECOMPONENT("update-component"), REDIRECT("redirect"),
        UPDATEVIEW("update-view"), UPDATENONE("update-none"), DISPLAYLIGHTBOX("display-lightbox"),
        UPDATEDIALOG("update-dialog");

        private String key;

        AjaxReturnTypes(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    public static enum WindowTargets {
        _blank, _self, _parent, _top
    }

    public static enum DialogDismissOption {
        IMMEDIATE, PRESUBMIT, REQUEST
    }

    public enum WizardAction {
        NONE(0), BACK(-1), CONTINUE(1);

        private int step;

        WizardAction(int step) {
            this.step = step;
        }

        public int getStep() {
            return step;
        }

        public void setStep(int step) {
            this.step = step;
        }
    }

    public static class ControllerMappings {
        public static final String EXPORT = "export";
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
        public static final String SESSION_TIMEOUT = "sessionTimeout";
        public static final String TABLE_JSON = "tableJsonRetrieval";
        public static final String TABLE_DATA = "tableDataRetrieval";
        public static final String TABLE_XML = "tableXmlRetrieval";
        public static final String INQUIRY_XML = "inquiryXmlRetrieval";
        public static final String TABLE_CSV = "tableCsvRetrieval";
        public static final String TABLE_XLS = "tableXlsRetrieval";
        public static final String DISPLAY_SUPER_USER_DOCUMENT = "displaySuperUserDocument";
    }

    public static class ActionEvents {
        public static final String ADD_LINE = "addLine";
        public static final String ADD_BLANK_LINE = "addBlankLine";
    }

    public static class LayoutComponentOptions {
        public static final String COLUMN_SPAN = "colSpan";
        public static final String ROW_SPAN = "rowSpan";
    }

    public static class RowSelection {
        public static final String ALL = "all";
        public static final String ODD = "odd";
        public static final String EVEN = "even";
    }

    public static class IdSuffixes {
        public static final String ACTION = "_act";
        public static final String ADD_LINE = "_add";
        public static final String CONTROL = "_control";
        public static final String ATTRIBUTE = "_attribute";
        public static final String COLUMN = "_c";
        public static final String COLUMN_SORTS = "_columnSorts";
        public static final String COMPARE = "_comp";
        public static final String CONSTRAINT = "_constraint";
        public static final String DETAIL_LINK = "_detLink";
        public static final String DETAIL_GROUP = "_detGroup";
        public static final String DIRECT_INQUIRY = "_directinquiry";
        public static final String DISCLOSURE_CONTENT = "_disclosureContent";
        public static final String ERRORS = "_errors";
        public static final String INSTRUCTIONAL = "_instructional";
        public static final String LINE = "_line";
        public static final String LABEL = "_label";
        public static final String FIELDSET = "_fieldset";
        public static final String SUB = "_sub";
        public static final String SUGGEST = "_suggest";
        public static final String QUICK_FINDER = "_quickfinder";
        public static final String SPAN = "_span";
        public static final String TAB = "_tab";
        public static final String HEADER_WRAPPER = "_headerWrapper";
        public static final String HEADER_UPPER_GROUP = "_headerUpperGroup";
        public static final String HEADER_RIGHT_GROUP = "_headerRightGroup";
        public static final String HEADER_LOWER_GROUP = "_headerLowerGroup";
        public static final String HELP_WRAPPER = "_helpWrapper";
        public static final String HELP = "_help";
    }

    public static class ViewPhases {
        public static final String INITIALIZE = "INITIALIZE";
        public static final String APPLY_MODEL = "APPLY_MODEL";
        public static final String FINALIZE = "FINALIZE";
        public static final String RENDER = "RENDER";
        public static final String PRE_PROCESS = "PRE_PROCESS";
    }

    public static class ViewStatus {
        public static final String CACHED = "X";
        public static final String CREATED = "C";
        public static final String INITIALIZED = "I";
        public static final String MODEL_APPLIED = "M";
        public static final String FINAL = "F";
        public static final String RENDERED = "R";
    }

    public static class PostMetadata {
        public static final String INPUT_FIELD_ATTRIBUTE_QUERY = "attributeQuery";
        public static final String INPUT_FIELD_SUGGEST = "suggest";
        public static final String INPUT_FIELD_SUGGEST_QUERY = "suggestQuery";
        public static final String INPUT_FIELD_IS_UPPERCASE = "isUppercase";
        public static final String LABEL = "label";
        public static final String PATH = "path";
        public static final String SIMPLE_CONSTRAINT = "simpleConstraint";
        public static final String VALID_CHARACTER_CONSTRAINT = "validCharacterConstraint";
        public static final String CASE_CONSTRAINT = "caseConstraint";
        public static final String MUST_OCCUR_CONSTRAINTS = "mustOccurConstraints";
        public static final String PREREQ_CONSTSTRAINTS = "prerequisiteConstraints";
        public static final String BINDING_PATH = "bindingPath";
        public static final String BINDING_INFO = "bindingInfo";
        public static final String ADD_LINE_BINDING_INFO = "addLineBindingInfo";
        public static final String ADD_LINE_PLACEMENT = "addLinePlacement";
        public static final String BASE_ID = "BASE_ID";
        public static final String COLL_DISPLAY_START = "displayStart";
        public static final String COLL_DISPLAY_LENGTH = "displayLength";
        public static final String COLL_LABEL = "collectionLabel";
        public static final String COLL_LOOKUP_FIELD_CONVERSIONS = "collectionLookup.fieldConversions";
        public static final String COLL_OBJECT_CLASS = "collectionObjectClass";
        public static final String DUPLICATE_LINE_PROPERTY_NAMES = "duplicateLinePropertyNames";
        public static final String DUPLICATE_LINE_LABEL_STRING = "duplicateLineLabelString";
        public static final String RESET_DATA_ON_REFRESH = "resetDataOnRefresh";
        public static final String STATE_MAPPING = "stateMapping";
        public static final String STATE_OBJECT_BINDING_PATH = "stateObjectBindingPath";
        public static final String SUGGEST = "suggest";
        public static final String QUICKFINDER_FOCUS_ID = "quickfinderFocusId";
        public static final String QUICKFINDER_JUMP_TO_ID = "quickfinderJumpToId";
        public static final String QUICKFINDER_CALLBACK_METHOD_TO_CALL = "quickfinderCallbackMethodToCall";
        public static final String QUICKFINDER_CALLBACK_METHOD = "quickfinderCallbackMethod";
        public static final String QUICKFINDER_CALLBACK_CONTEXT = "quickfinderCallbackContext";
        public static final String QUICKFINDER_CALLBACK_CONTEXT_PROPERTY_LINE_INDEX = "lineIndex";
        public static final String FILTERABLE_LOOKUP_CRITERIA = "filterableLookupCriteria";
    }

    public static class LookupCriteriaPostMetadata {
        public static final String COMPONENT_ID = "componentId";
        public static final String DISABLE_WILDCARDS_AND_OPERATORS = "disableWildcardsAndOperators";
        public static final String HIDDEN = "hidden";
        public static final String REQUIRED = "required";
        public static final String SECURE_VALUE = "secureValue";
        public static final String VALID_CHARACTERS_CONSTRAINT = "validCharactersConstraint";
    }

    public static class ContextVariableNames {
        public static final String COLLECTION_GROUP = "collectionGroup";
        public static final String CONFIG_PROPERTIES = "ConfigProperties";
        public static final String COMPONENT = "component";
        public static final String CONSTANTS = "Constants";
        public static final String DOCUMENT_ENTRY = "DocumentEntry";
        public static final String INDEX = "index";
        public static final String IS_ADD_LINE = "isAddLine";
        public static final String LINE = "line";
        public static final String LINE_SUFFIX = "lineSuffix";
        public static final String READONLY_LINE = "readOnlyLine";
        public static final String MANAGER = "manager";
        public static final String NODE = "node";
        public static final String NODE_PATH = "nodePath";
        public static final String PARENT = "parent";
        public static final String THEME_IMAGES = "ThemeImages";
        public static final String UIF_CONSTANTS = "UifConstants";
        public static final String VIEW = "view";
        public static final String VIEW_HELPER = "ViewHelper";
        public static final String PARENT_LINE = "parentLine";
        public static final String WIZARD = "wizard";
        public static final String USER_SESSION = "userSession";
    }

    public static class TableToolsKeys {
        public static final String AASORTING = "aaSorting";
        public static final String BAUTO_TYPE = "bAutoType";
        public static final String BPROCESSING = "bProcessing";
        public static final String BSERVER_SIDE = "bServerSide";
        public static final String SDOM = "sDom";
        public static final String CELL_CLASS = "sClass";
        public static final String MDATA = "mDataProp";
        public static final String LANGUAGE = "oLanguage";
        public static final String EMPTY_TABLE = "sEmptyTable";
        public static final String AO_COLUMNS = "aoColumns";
        public static final String AO_COLUMN_DEFS = "aoColumnDefs";
        public static final String SORT_SKIP_ROWS = "aiSortingSkipRows";
        public static final String SORT_DATA_TYPE = "sSortDataType";
        public static final String SORTABLE = "bSortable";
        public static final String TARGETS = "aTargets";
        public static final String VISIBLE = "bVisible";
        public static final String SORT_TYPE = "sType";
        public static final String TABLE_SORT = "bSort";
        public static final String SAJAX_SOURCE = "sAjaxSource";
        public static final String FOOTER_CALLBACK = "fnFooterCallback";
        public static final String AA_DATA = "aaData";
        public static final String DEFER_RENDER = "bDeferRender";
        public static final String SDOWNLOAD_SOURCE = "sDownloadSource";
        public static final String SERVER_PARAMS = "fnServerParams";
    }

    public static class TableToolsValues {
        public static final String DOM_TEXT = "dom-text";
        public static final String DOM_SELECT = "dom-select";
        public static final String DOM_CHECK = "dom-checkbox";
        public static final String DOM_RADIO = "dom-radio";

        // sort types:

        public static final String NUMERIC = "numeric";
        public static final String STRING = "string";
        public static final String DATE = "kuali_date";
        public static final String PERCENT = "kuali_percent";
        public static final String CURRENCY = "kuali_currency";
        public static final String TIMESTAMP = "kuali_timestamp";

        public static final String FALSE = "false";
        public static final String TRUE = "true";

        public static final int ADD_ROW_DEFAULT_INDEX = 0;
        public static final String JSON_TEMPLATE = "components/element/dataTablesJson.ftl";
    }

    public static class TableLayoutValues {
        public static final int ACTIONS_COLUMN_LEFT_INDEX = 1;
        public static final int ACTIONS_COLUMN_RIGHT_INDEX = -1;
    }

    public static class PageRequest {
        public static final String PREV = "prev";
        public static final String NEXT = "next";
        public static final String FIRST = "first";
        public static final String LAST = "last";
        public static final String PAGE_NUMBER = "pageNumber";
    }

    public static class TabOptionKeys {
        public static final String ACTIVE = "active";
    }

    public static class TitleAppendTypes {
        public static final String DASH = "dash";
        public static final String PARENTHESIS = "parenthesis";
        public static final String REPLACE = "replace";
        public static final String NONE = "none";
    }

    public static class ComponentProperties {
        public static final String HEADER_TEXT = "headerText";
        public static final String DEFAULT_VALUE = "defaultValue";
        public static final String DEFAULT_VALUES = "defaultValues";
    }

    public static class UrlParams {
        public static final String ACTION_EVENT = "actionEvent";
        public static final String FORM_KEY = "formKey";
        public static final String VIEW_ID = "viewId";
        public static final String PAGE_ID = "pageId";
        public static final String HISTORY = "history";
        public static final String LAST_FORM_KEY = "lastFormKey";
        public static final String LOGIN_USER = "__login_user";
    }

    public static class Messages {
        public static final String VALIDATION_MSG_KEY_PREFIX = "validation.";
        public static final String STATE_PREFIX = "validation.statePrefix";
        public static final String PROPERTY_NAME_PREFIX = "validation.propertyNamePrefix";
    }

    public static class MessageKeys {
        public static final String LOOKUP_RESULT_MESSAGES = "LookupResultMessages";
        public static final String QUERY_DATA_NOT_FOUND = "query.dataNotFound";
        public static final String OPTION_ALL = "option.all";
    }

    public static class ClientSideVariables {
        public static final String KRAD_IMAGE_LOCATION = "kradImageLocation";
        public static final String KRAD_SCRIPT_CLEANUP = "scriptCleanup";
        public static final String KRAD_URL = "kradUrl";
        public static final String APPLICATION_URL = "applicationUrl";
    }

    public static class RefreshCallerTypes {
        public static final String LOOKUP = "LOOKUP";
        public static final String MULTI_VALUE_LOOKUP = "MULTI_VALUE_LOOKUP";
        public static final String QUESTION = "QUESTION";
    }

    public static class RefreshStatus {
        public static final String ERROR = "ERROR";
    }

    public static final class HistoryFlow {
        public static final String HISTORY_MANAGER = "historyManager";
        public static final String FLOW = "flow";
        public static final String START = "start";
        public static final String RETURN_TO_START = "returnToStart";
        public static final String SEPARATOR = "@@";
    }

    public static final class RoleTypes {
        public static final String ACTION = "Action";
        public static final String CONTROL = "Control";
        public static final String DATA_SCRIPT = "dataScript";
        public static final String INPUT_FIELD = "InputField";
        public static final String GROUP = "Group";
        public static final String GROUP_TOTAL = "groupTotal";
        public static final String TOTAL = "total";
        public static final String TOTALS_BLOCK = "totalsBlock";
        public static final String PAGE = "Page";
        public static final String PAGE_TOTAL = "pageTotal";
        public static final String ROW_GROUPING = "RowGrouping";
        public static final String VIEW = "View";
    }

    public static final class DataAttributes {
        public static final String TYPE = "type";
        public static final String ROLE = "role";
        public static final String REQ_INDICATOR = "req_indicator";
        public static final String ONCLICK = "onClick";
        public static final String SUBMIT_DATA = "submit_data";
        public static final String HAS_MESSAGES = "has_messages";
        public static final String SERVER_MESSAGES = "server_messages";
        public static final String VALIDATION_MESSAGES = "validation_messages";
        public static final String ACTION_DEFAULTS = "action_defaults";
        public static final String GROUP_VALIDATION_DEFAULTS = "group_validation_defaults";
        public static final String FIELD_VALIDATION_DEFAULTS = "field_validation_defaults";
        public static final String MESSAGES_FOR = "messages_for";
        public static final String PARENT = "parent";
        public static final String SUMMARIZE = "summarize";
        public static final String DISPLAY_MESSAGES = "displayMessages";
        public static final String CLOSEABLE = "closeable";
        public static final String COLLAPSE_FIELD_MESSAGES = "collapseFieldMessages";
        public static final String DISPLAY_LABEL = "displayLabel";
        public static final String SHOW_PAGE_SUMMARY_HEADER = "showPageSummaryHeader";
        public static final String DISPLAY_HEADER_SUMMARY = "displayHeaderSummary";
        public static final String IS_TABLE_COLLECTION = "isTableCollection";
        public static final String HAS_OWN_MESSAGES = "hasOwnMessages";
        public static final String PAGE_LEVEL = "pageLevel";
        public static final String FORCE_SHOW = "forceShow";
        public static final String SECTIONS = "sections";
        public static final String ORDER = "order";
        public static final String RETURN = "return";
        public static final String SERVER_ERRORS = "serverErrors";
        public static final String SERVER_WARNINGS = "serverWarnings";
        public static final String SERVER_INFO = "serverInfo";
        public static final String VIGNORE = "vignore";
        public static final String TOTAL = "total";
        public static final String SKIP_TOTAL = "skip_total";
        public static final String LABEL = "label";
        public static final String GROUP = "group";
        public static final String LABEL_FOR = "label_for";
        public static final String CONTROL_FOR = "control_for";
        public static final String ADD_CONTROLS = "add_controls";
        public static final String HEADER_FOR = "header_for";
        public static final String STICKY = "sticky";
        public static final String STICKY_FOOTER = "sticky_footer";
        public static final String DETAILS_DEFAULT_OPEN = "details_default_open";
        public static final String TAB_FOR = "tabfor";
        public static final String CHECKED = "checked";
        public static final String ENTER_KEY = "enter_key";
        public static final String DEFAULT_ENTER_KEY_ACTION = "default_enter_key_action";
        public static final String DISMISS = "dismiss";
        public static final String DISMISS_DIALOG_OPTION = "dismissdialogoption";
        public static final String DISMISS_RESPONSE = "response";
        public static final String DIALOG_RESPONSE_HANDLER = "response_handler";
        public static final String DIALOG_SHOW_HANDLER = "show_handler";
        public static final String DIALOG_HIDE_HANDLER = "hide_handler";
        public static final String INLINE_EDIT = "inline_edit";
    }

    public static final String JS_REGEX_SPECIAL_CHARS = new String("$[\\^.|?*+()");

    public static final class ActionDataAttributes {
        public static final String AJAX_SUBMIT = "ajaxSubmit";
        public static final String CONFIRM_DIALOG_ID = "confirmDialogId";
        public static final String CONFIRM_PROMPT_TEXT = "confirm_prompttext";
        public static final String SUCCESS_CALLBACK = "successCallback";
        public static final String ERROR_CALLBACK = "errorCallback";
        public static final String PRE_SUBMIT_CALL = "preSubmitCall";
        public static final String LOADING_MESSAGE = "loadingMessage";
        public static final String DISABLE_BLOCKING = "disableBlocking";
        public static final String AJAX_RETURN_TYPE = "ajaxReturnType";
        public static final String REFRESH_ID = "refreshId";
        public static final String VALIDATE = "validate";
        public static final String DIRTY_ON_ACTION = "dirtyOnAction";
        public static final String CLEAR_DIRTY = "clearDirtyOnAction";
        public static final String PERFORM_DIRTY_VALIDATION = "performDirtyValidation";
        public static final String FOCUS_ID = "focusId";
        public static final String JUMP_TO_ID = "jumpToId";
        public static final String JUMP_TO_NAME = "jumpToName";
        public static final String FIELDS_TO_SEND = "fieldsToSend";
    }

    public static final class AriaAttributes {
        public static final String VALUE_NOW = "valuenow";
        public static final String VALUE_MAX = "valuemax";
        public static final String VALUE_MIN = "valuemin";
        public static final String VALUE_TEXT = "valuetext";
    }

    public static final class AriaRoles {
        public static final String PROGRESS_BAR = "progressbar";
    }

    public static final class HtmlAttributeValues {
        public static final String TARGET_BLANK = "_blank";
    }

    public static final class CaseConstraintOperators {
        public static final String HAS_VALUE = "has_value";
        public static final String EQUALS = "equals";
        public static final String GREATER_THAN_EQUAL = "greater_than_equal";
        public static final String LESS_THAN_EQUAL = "less_than_equal";
        public static final String NOT_EQUAL = "not_equal";
        public static final String NOT_EQUALS = "not_equals";
        public static final String GREATER_THAN = "greater_than";
        public static final String LESS_THAN = "less_than";
    }

    public static final class JsFunctions {
        public static final String COLLECTION_LINE_CHANGED = "collectionLineChanged";
        public static final String SHOW_LOOKUP_DIALOG = "showLookupDialog";
        public static final String HANDLE_SERVER_DIALOG_RESPONSE = "handleServerDialogResponse";
        public static final String INITIALIZE_VIEW_STATE = "initializeViewState";
        public static final String INITIALIZE_SESSION_TIMERS = "initializeSessionTimers";
        public static final String REDIRECT = "redirect";
        public static final String SET_CONFIG_PARM = "setConfigParam";
        public static final String SET_VALUE = "setValue";
        public static final String SHOW_GROWL = "showGrowl";
        public static final String SHOW_DIALOG = "showDialog";
        public static final String VALIDATE_ADD_LINE = "validateAddLine";
        public static final String VALIDATE_LINE = "validateLine";
        public static final String WRITE_CURRENT_PAGE_TO_SESSION = "writeCurrentPageToSession";
    }

    public static final String EVENT_NAMESPACE = "uif";

    public static final String BOX_LAYOUT_HORIZONTAL_ITEM_CSS = "uif-boxLayoutHorizontalItem";
    public static final String BOX_LAYOUT_VERTICAL_ITEM_CSS = "uif-boxLayoutVerticalItem";

    public static final class ConfigProperties {
        public static final String KRAD_IMAGES_URL = "krad.externalizable.images.url";
        public static final String KRAD_URL = "krad.url";
    }

    public static final class FileExtensions {
        public static final String CSS = ".css";
        public static final String JS = ".js";
        public static final String MIN = ".min";
        public static final String LESS = ".less";
    }

    public static final class WrapperTags {
        public static final String DIV = "div";
        public static final String SPAN = "span";
        public static final String P = "p";
        public static final String MAIN = "main";
        public static final String SECTION = "section";
        public static final String HEADER = "header";
        public static final String FOOTER = "footer";
        public static final String NAV = "nav";
    }

    public static final class MultiFileUploadOptions {
        public static final String URL = "url";
        public static final String ACCEPT_FILE_TYPES = "acceptFileTypes";
        public static final String MIN_SIZE = "minFileSize";
        public static final String MAX_SIZE = "maxFileSize";
    }
}
