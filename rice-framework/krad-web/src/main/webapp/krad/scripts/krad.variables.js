/*
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
/**
 * Common constants and variables for KRAD
 */
function JavascriptKradVariables() {
}

JavascriptKradVariables.prototype = {
    ACTION_DEFAULTS: "action_defaults",
    ACTION_ONCLICK_DATA: "onclick",
    ACTION_FIELD_CLASS: "uif-actionField",
    ACTIVE_CLASS: "active",
    APPLICATION_HEADER_WRAPPER: "Uif-ApplicationHeader-Wrapper",
    APPLICATION_FOOTER_WRAPPER: "Uif-ApplicationFooter-Wrapper",
    APP_ID: "Uif-Application",
    APPLICATION_URL: "applicationUrl",
    ATTRIBUTES: {
        ID: "id",
        DATA_OPEN: "data-open",
        DATA_RETURN: "data-return",
        DATA_RESPONSE: "data-response",
        DATA_RESPONSE_HANDLER: "data-response_handler",
        DATA_SHOW_HANDLER: "data-show_handler",
        DATA_HIDE_HANDLER: "data-hide_handler",
        DATA_ROLE: "data-role",
        DISMISS_DIALOG_OPTION: "data-dismissdialogoption",
        DIALOG_ID: "data-dismissdialogid"
    },

    CACHE_KEY: "cacheKey",
    CHANGED_HEADER_ICON_CLASS: "uif-changedHeaderIcon",
    CHANGE_COMPONENT_PROPERTIES: "changeProperties",
    CLASSES: {
        PLACEHOLDER: "uif-placeholder",
        MODAL: "modal",
        IN: "in"
    },

    CLEAR_FORM_METHOD_TO_CALL: "clearForm",
    CLIENT_MESSAGE_ITEMS_CLASS: "uif-clientMessageItems",
    CLIENT_ERROR_DIV_CLASS: "alert-danger",
    CLIENT_WARNING_DIV_CLASS: "alert-warning",
    CLIENT_INFO_DIV_CLASS: "alert-info",
    COLLAPSED_ERRORS_CLASS: "uif-collapsedErrors",
    COLLAPSED_INFO_CLASS: "uif-collapsedInfo",
    COLLAPSED_WARNINGS_CLASS: "uif-collapsedWarnings",
    COLLECTION_ITEM_CLASS: "uif-collectionItem",
    COLLECTION_ACTION_CLASS: "uif-collection-column-action",
    CONTROL_CLASS: "Uif-Application",
    COUNTDOWN_CLASS: "hasCountdown",

    // constants for data role attribute values
    DATA_ROLES: {
        DISCLOSURE_LINK: "disclosureLink",
        PLACEHOLDER: "placeholder",
        ACTION: "Action",
        PROMPTTEXT: "prompttext",
        DIALOGHEADER: "dialogheader"
    },

    DIALOG_DISMISS_OPTIONS: {
        IMMEDIATE: "IMMEDIATE",
        PRESUBMIT: "PRESUBMIT",
        REQUEST: "REQUEST"
    },

    DIALOG_SELECTOR: "[role='dialog']",
    DIRTY_CLASS: "dirty",
    DIRTY_FORM: "dirtyForm",
    DISABLE_BROWSER_CACHE: "view.disableBrowserCache",
    DISABLED_CLASS: "disabled",
    DIALOG_PLACEHOLDER: "_dialogPlaceholder",
    ENTER_KEY_SUFFIX: "enter_key",
    ENTER_KEY_DEFAULT: "@DEFAULT",
    ERROR_HIGHLIGHT_SECTION_CLASS: "uif-errorHighlight-section",
    ERROR_MESSAGE_ITEM_CLASS: "uif-errorMessageItem-field",
    FIELD_CLASS: "uif-field",
    FORM_KEY: "formKey",
    FLOW_KEY: "flowKey",
    FOCUS_ID: "focusid",
    GRID_LAYOUT_CELL_CLASS: "uif-gridLayoutCell",
    HAS_ERROR_CLASS: "uif-hasError",
    HAS_INFO_CLASS: "uif-hasInfo",
    HAS_MODIFIED_ERROR_CLASS: "uif-hasError-modified",
    HAS_WARNING_CLASS: "uif-hasWarning",
    HEADER_TEXT_CLASS: "uif-headerText",
    // constants for element ids
    IDS: {
        DIALOGS: "Uif-Dialogs",
        DIALOG_OKCANCEL: "Uif-DialogGroup-OkCancel",
        DIALOG_YESNO: "Uif-DialogGroup-YesNo"
    },

    // constants for id suffixes
    ID_SUFFIX: {
        DISCLOSURE_CONTENT: "_disclosureContent",
        DISCLOSURE_TOGGLE: "_toggle",
        ADD_LINE_INPUT_FIELD: "_add_control"
    },
    IMAGE_LOCATION: "kradImageLocation",
    INLINE_EDIT: {
        EDIT_SUFFIX: "_edit",
        INLINE_EDIT: "_inlineEdit",
        INLINE_EDIT_VIEW: "_inlineEdit_view",
        VIEW_SUFFIX: "_view",
        VIEW_CLASS: ".uif-inlineEdit-view"
    },
    JUMP_TO_ID: "jumptoid",
    JUMP_TO_NAME: "jumptoname",
    PAGE_ID: "pageId",
    PORTAL_IFRAME_ID: "iframeportlet",
    INCIDENT_REPORT_VIEW_CLASS: "Uif-IncidentReportView",
    INFO_HIGHLIGHT_SECTION_CLASS: "uif-infoHighlight-section",
    INFO_MESSAGE_ITEM_CLASS: "uif-infoMessageItem-field",
    INPUT_FIELD_SELECTOR: "[data-role:'InputField']",
    KEEP_SESSION_ALIVE_METHOD_TO_CALL: "keepSessionAlive",
    KRAD_URL: "kradUrl",
    KUALI_FORM: "kualiForm",
    LIGHTBOX_PARAM: "lightbox",
    MENU_COLLAPSE_ACTION: "sidebar-collapse",
    MENU_COLLAPSE_ICON_RIGHT: "icon-angle-right",
    MENU_COLLAPSE_ICON_LEFT: "icon-angle-left",
    MENU_COLLAPSED: "sidebar-collapsed",
    MESSAGE_COUNT_CLASS: "uif-messageCount",
    MESSAGE_KEY_DIRTY_FIELDS: "message.dirtyFields",
    MESSAGE_ERROR: "message.error",
    MESSAGE_ERROR_FIELD_MODIFIED: "message.errorFieldModified",
    MESSAGE_WARNING: "message.warning",
    MESSAGE_INFORMATION: "message.information",
    MESSAGE_DETAILS: "message.details",
    MESSAGE_CLOSE_DETAILS: "message.closeDetails",
    MESSAGE_LOADING: "message.loading",
    MESSAGE_CHANGE: "message.change",
    MESSAGE_FORM_CONTAINS_ERRORS: "message.formContainsErrors",
    MESSAGE_BEFORE: "message.before",
    MESSAGE_AFTER: "message.after",
    MESSAGE_PLEASE_ENTER_VALUE: "message.pleaseEnterValue",
    MESSAGE_EXPAND: "message.expand",
    MESSAGE_COLLAPSE: "message.collapse",
    MESSAGE_SERVER_RESPONSE_ERROR: "message.serverResponseError",
    MESSAGE_STATUS_ERROR: "message.statusError",
    MESSAGE_TOTAL_ERROR: "message.totalError",
    MESSAGE_TOTAL_ERRORS: "message.totalErrors",
    MESSAGE_TOTAL_OTHER_MESSAGES: "message.totalOtherMessages",
    MESSAGE_TOTAL_WARNING: "message.totalWarning",
    MESSAGE_TOTAL_WARNINGS: "message.totalWarnings",
    MESSAGE_TOTAL_MESSAGE: "message.totalMessage",
    MESSAGE_TOTAL_MESSAGES: "message.totalMessages",
    MESSAGE_THE: "message.the",
    MESSAGE_THE_SECTION_HAS_COUNT: "message.theSectionHasCount",
    NAVIGATION_ID: "Uif-Navigation",
    NAVIGATION_MENU_CLASS: "uif-navigationMenu",
    NAVIGATE_METHOD_TO_CALL: "navigate",
    PAGE_NUMBER_DATA: "num",
    PAGE_CONTENT_WRAPPER: "Uif-PageContentWrapper",
    PAGE_VALIDATION_HEADER_CLASS: "uif-pageValidationHeader",
    PAGE_VALIDATION_MESSAGE_ERROR_CLASS: "alert-danger",
    PAGE_VALIDATION_MESSAGE_INFO_CLASS: "alert-info",
    PAGE_VALIDATION_MESSAGE_SUCCESS_CLASS: "alert-success",
    PAGE_VALIDATION_MESSAGE_WARNING_CLASS: "alert-warning",
    PARENT_DATA_ATTRIBUTE: "parent",
    PERFORM_DIRTY_VALIDATION: "performdirtyvalidation",
    PROGRESSIVE_DISCLOSURE_HIGHLIGHT_CLASS: "uif-progressiveDisclosure-highlight",
    POPOVER_DATA: "bs.popover",
    RENDERED_IN_LIGHTBOX: "renderedInLightBox",
    REFRESH_METHOD_TO_CALL: "refresh",
    RETRIEVE_MESSAGE_METHOD_TO_CALL: "retrieveMessage",
    RETRIEVE_COLLECTION_PAGE_METHOD_TO_CALL: "retrieveCollectionPage",
    RETURN_TYPE_UPDATE_COMPONENT: "update-component",
    RETURN_FROM_LIGHTBOX_METHOD_TO_CALL: "returnFromLightbox",
    RETURN_SELECTED_ACTION_CLASS: "uif-returnSelectedAction",
    REQUIRED_MESSAGE_CLASS: "uif-requiredMessage",
    SAVE_LINE_ACTION_CLASS: "uif-saveLineAction",
    SELECT_FIELD_STYLE_CLASS: "uif-select-line",
    LOOKUP_COLLECTION_ID: "uLookupResults",
    SELF: "SELF",
    SERVER_MESSAGE_ITEMS_CLASS: "uif-serverMessageItems",
    SESSION_TIMEOUT_WARNING_DIALOG: "Uif-SessionTimeoutWarning-DialogGroup",
    SESSION_TIMEOUT_DIALOG: "Uif-SessionTimeout-DialogGroup",
    SESSION_TIMEOUT_WARNING_TIMER: "sessionTimeoutWarningTimer",
    SESSION_TIMEOUT_TIMER: "sessionTimeoutTimer",
    SHOW_DIALOG_EVENT: "showdialog.uif",
    SINGLE_PAGE_VIEW: "view.singlePageView",
    STACKED_COLLECTION_LAYOUT_CLASS: "uif-stackedCollectionLayout",
    STICKY_CLASS: "uif-sticky",
    SUCCESS_RESPONSE: "success",
    VIEW_HEADER_UPDATE: "Uif-ViewHeaderUpdate",
    TOP_GROUP_UPDATE: "Uif-TopGroupUpdate",
    TABLE_COLLECTION_LAYOUT_CLASS: "uif-tableCollectionLayout",
    TAB_GROUP_CLASS: "Uif-TabGroup",
    TOGGLE_ARROW_CLASS: "arrow",
    TOOLTIP_CLASS: "uif-tooltip",
    VALIDATION_IMAGE_CLASS: "uif-validationImage",
    SERVER_MESSAGES: "server_messages",
    VIEW_ID: "viewId",
    VIEW_CONTENT_WRAPPER: "Uif-ViewContentWrapper",
    VALIDATE_DIRTY: "view.applyDirtyCheck",
    VALIDATION_MESSAGES: "validation_messages",
    VALIDATION_MESSAGES_CLASS: "uif-validationMessagesList",
    VALIDATION_PAGE_HEADER_CLASS: "uif-pageValidationHeader",
    VALIDATION_SETUP_EVENT: "validationSetup",
    GROUP_VALIDATION_DEFAULTS: "group_validation_defaults",
    FIELD_VALIDATION_DEFAULTS: "field_validation_defaults",
    PAGE_LOAD_EVENT: "pageLoad",
    VIEW_CONTENT_HEADER_CLASS: "Uif-ViewContentWrapper",
    VIEW_STATE: "ViewState",
    WARNING_HIGHLIGHT_SECTION_CLASS: "uif-warningHighlight-section",
    WARNING_MESSAGE_ITEM_CLASS: "uif-warningMessageItem-field",
    GROUP_CLASS: "uif-group",
    ROW_DETAILS_CLASS: "uif-rowDetails",
    NEXT_INPUT: "NEXT_INPUT:",
    SKIP_TOTAL: "skip_total",
    ADD_CONTROLS: "add_controls",
    SUBMIT_DATA: "submit_data",
    DETAILS_DEFAULT_OPEN: "details_default_open",
    EVENT_NAMESPACE: "uif",
    EVENTS: {
        ADJUST_PAGE_MARGIN : "adjustpagemargin.uif",
        ADJUST_STICKY : "adjuststicky.uif",
        DIALOG_RESPONSE : "dialogresponse.uif",
        UPDATE_CONTENT : "updatecontent.uif",
        PAGE_UPDATE_COMPLETE : "pageUpdateComplete.uif",
        SHOW_MODAL : "show.bs.modal",
        HIDE_MODAL : "hide.bs.modal",
        HIDDEN_MODAL : "hidden.bs.modal"
    }
}

var kradVariables = new JavascriptKradVariables();