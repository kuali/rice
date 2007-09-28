/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali;

import org.kuali.core.JstlConstants;
import org.kuali.core.util.KualiDecimal;

/**
 * This class is used to define global constants.
 */
public class RiceConstants extends JstlConstants {
    private static final long serialVersionUID = 2882277719647128949L;
    
    // special user used in the post-processor
    public static final String SYSTEM_USER = "KULUSER";
    
    public static final String PARAM_MAINTENANCE_VIEW_MODE = "maintenanceViewMode";
    public static final String PARAM_MAINTENANCE_VIEW_MODE_MAINTENANCE = "maintenance";
    public static final String PARAM_MAINTENANCE_VIEW_MODE_LOOKUP = "lookup";
    public static final String PARAM_MAINTENANCE_VIEW_MODE_INQUIRY = "inquiry";
    
    public static final String KNS_NAMESPACE = "KR-NS";
    
    public static class DetailTypes {
        public static final String NA_PARM_DETAIL_TYPE = "N/A";
        public static final String ALL_DETAIL_TYPE = "All";
    	public static final String LOOKUP_PARM_DETAIL_TYPE = "Lookup";
    	public static final String UNIVERSAL_USER_DETAIL_TYPE = "UniversalUser";
    	public static final String KUALI_MODULE_USER_DETAIL_TYPE = "KualiModuleUser";
    	public static final String DOCUMENT_DETAIL_TYPE = "Document";
    	public static final String DOCUMENT_TYPE_DETAIL_TYPE = "DocumentType";
    }
    
    
    
    public static final String CONFIGURATION_FILE_NAME = "configuration";
    public static final String ENVIRONMENT_KEY = "environment";
    public static final String VERSION_KEY = "version";
    public static final String LOG4J_SETTINGS_FILE_KEY = "log4j.settings.file";
    public static final String LOGS_DIRECTORY_KEY = "logs.directory";
    public static final String LOG4J_RELOAD_MINUTES_KEY = "log4j.reload.minutes";
    public static final String STARTUP_STATS_MAILING_LIST_KEY = "startup.stats.mailing.list"; 
    public static final String APPLICATION_URL_KEY = "application.url";
    public static final String ATTACHMENTS_DIRECTORY_KEY = "attachments.directory";
    public static final String ATTACHMENTS_PENDING_DIRECTORY_KEY = "attachments.pending.directory";
    public static final String HTDOCS_LOGS_URL_KEY = "htdocs.logs.url";
    public static final String HTDOCS_STAGING_URL_KEY = "htdocs.staging.url";
    public static final String EXTERNALIZABLE_HELP_URL_KEY = "externalizable.help.url";
    public static final String APPLICATION_EXTERNALIZABLE_IMAGES_URL_KEY = "externalizable.images.url";
    public static final String EXTERNALIZABLE_IMAGES_URL_KEY = "kr.externalizable.images.url";
    public static final String REPORTS_DIRECTORY_KEY = "reports.directory";
    public static final String WORKFLOW_URL_KEY = "workflow.url";
    public static final String PROD_ENVIRONMENT_CODE_KEY = "production.environment.code";
    public static final String MAINTAIN_USERS_LOCALLY_KEY = "maintain.users.locally";
    public static final String DOCHANDLER_DO_URL = "/DocHandler.do?docId=";
    public static final String DOCHANDLER_URL_CHUNK = "&command=displayDocSearchView";
    
    public static final String DATABASE_REPOSITORY_FILES_LIST_NAME = "databaseRepositoryFilePaths";
    public static final String SCRIPT_CONFIGURATION_FILES_LIST_NAME = "scriptConfigurationFilePaths";
    public static final String JOB_NAMES_LIST_NAME = "jobNames";
    public static final String TRIGGER_NAMES_LIST_NAME = "triggerNames";

    public static final String ACTION_FORM_UTIL_MAP_METHOD_PARM_DELIMITER = "~";
    public static final String ADD_LINE_METHOD = "addLine";
    public static final String ADD_PREFIX = "add";
    public static final String ACTIVE_INDICATOR = "Y";
    public static final String AMOUNT_PROPERTY_NAME = "amount";
    public static final String APPROVE_METHOD = "approve";
    public static final String NON_ACTIVE_INDICATOR = "N";
    public static final String BLANK_SPACE = " ";
    public static final String BACK_LOCATION = "backLocation";
    public static final String BACKDOOR_PARAMETER = "backdoorId";
    public static final String BLANKET_APPROVE_METHOD = "blanketApprove";
    public static final String BUSINESS_OBJECT_CLASS_ATTRIBUTE = "businessObjectClassName";
    public static final String CALLING_METHOD = "caller";
    public static final String CONFIRMATION_QUESTION = "confirmationQuestion";
    public static final String CONFIGURATION_SERVICE_DATA_FILE_NAME = "configurationServiceData.xml";
    public static final String CONSOLIDATED_SUBACCOUNT = "*ALL*";
    public static final String CONVERSION_FIELDS_PARAMETER = "conversionFields";
    public static final String LOOKUP_READ_ONLY_FIELDS = "readOnlyFields";
    public static final String LOOKUP_AUTO_SEARCH = "autoSearch";
    public static final String COST_SHARE = "CS";
    public static final String CREDIT_AMOUNT_PROPERTY_NAME = "newSourceLineCredit";
    public static final String DEBIT_AMOUNT_PROPERTY_NAME = "newSourceLineDebit";
    public static final String DEFAULT_RETURN_LOCATION = "lookup.do";
    public static final String DELETE_LINE_METHOD = "deleteLine";
    public static final String TOGGLE_INACTIVE_METHOD = "toggleInactiveRecordDisplay";
    public static final String DICTIONARY_BO_NAME = "dictionaryBusinessObjectName";
    public static final String DISPATCH_REQUEST_PARAMETER = "methodToCall";
    public static final String DOC_FORM_KEY = "docFormKey";
    public static final String DOCUMENT_CANCEL_QUESTION = "DocCancel";
    public static final String DOCUMENT_DELETE_QUESTION = "DocDelete";
    public static final String DOCUMENT_DISAPPROVE_QUESTION = "DocDisapprove";
    public static final String DOCUMENT_HEADER_ID = "documentHeaderId";
    public static final String DOCUMENT_HEADER_DOCUMENT_STATUS_CODE_PROPERTY_NAME = "financialDocumentStatusCode";
    public static final String NOTE_TEXT_PROPERTY_NAME = "noteText";
    public static final String DOCUMENT_HEADER_PROPERTY_NAME = "documentHeader";
    public static final String DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION = "DocSaveBeforeClose";
    public static final String EMPLOYEE_ACTIVE_STATUS = "A";
    public static final String EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME = "sourceAccountingLine";
    public static final String EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME = "targetAccountingLine";
    public static final String EXTRA_BUTTON_SOURCE = "extraButtonSource";
    public static final String EXTRA_BUTTON_PARAMS = "extraButtonParams";
    public static final String NEW_DOCUMENT_NOTE_PROPERTY_NAME = "newDocumentNote";
    public static final String NEW_AD_HOC_ROUTE_PERSON_PROPERTY_NAME = "newAdHocRoutePerson";
    public static final String NEW_AD_HOC_ROUTE_WORKGROUP_PROPERTY_NAME = "newAdHocRouteWorkgroup";
    public static final String EXISTING_AD_HOC_ROUTE_PERSON_PROPERTY_NAME = "adHocRoutePerson";
    public static final String EXISTING_AD_HOC_ROUTE_WORKGROUP_PROPERTY_NAME = "adHocRouteWorkgroup";
    public static final String DOCUMENT_PROPERTY_NAME = "document";
    public static final String DOCUMENT_TYPE_NAME = "docTypeName";
    public static final String EDIT_PREFIX = "edit";
    public static final String EMPTY_STRING = "";
    public static final String FIELD_CONVERSION_PAIR_SEPERATOR = ":";
    public static final String FIELD_CONVERSIONS_SEPERATOR = ",";
    public static final String REFERENCES_TO_REFRESH_SEPARATOR = ",";
    public static final String GENERIC_FIELD_NAME = "Field";
    public static final String GENERIC_CODE_PROPERTY_NAME = "code";
    public static final String STAND_IN_BUSINESS_OBJECT_FOR_ATTRIBUTES = "AttributeReferenceDummy";
    public static final String OVERRIDE_KEYS = "overrideKeys";

    /**
     * This value denotes that a max length has not been defined for a given lookup results field
     */
    public static final int LOOKUP_RESULT_FIELD_MAX_LENGTH_NOT_DEFINED = -1;
    
    /**
     * The number of levels BusinessObjectDictionaryServiceImpl will recurse.  If this number is high, it may lead to serious
     * performance problems
     */
    public static final int BUSINESS_OBJECT_DICTIONARY_SERVICE_PERFORM_FORCE_UPPERCASE_RECURSION_MAX_DEPTH = 3;
    
    
    /**
     * When checkboxes are rendered on the form, a hidden field will also be rendered corresponding to each checkbox
     * with the checkbox's name suffixed with the value of this constant.  No real fields should have names that contain this suffix,
     * since this may lead to undesired results.
     */
    public static final String CHECKBOX_PRESENT_ON_FORM_ANNOTATION = "{CheckboxPresentOnFormAnnotation}";

    public static final int DOCUMENT_ANNOTATION_MAX_LENGTH = 2000;

    public static final String HIDE_LOOKUP_RETURN_LINK = "hideReturnLink";
    public static final String SUPPRESS_ACTIONS = "suppressActions";
    public static final String REFERENCES_TO_REFRESH = "referencesToRefresh";
    public static final String INITIAL_KUALI_DOCUMENT_STATUS_CD = "?";

    public static final String INQUIRABLE_ATTRIBUTE_NAME = "kualiInquirable";
    public static final String INQUIRY_ACTION = "inquiry.do";
    public static final String DIRECT_INQUIRY_ACTION = "directInquiry.do";
    public static final String INQUIRY_IMPL_ATTRIBUTE_NAME = "inquirableImplServiceName";
    public static final String INQUIRY_PK_VALUE_PASSED_FROM_PREVIOUS_REQUEST_PREFIX = "previousPkValue_";
    public static final String INACTIVE_RECORD_DISPLAY_PARAM_PREFIX = "inactiveRecordDisplay_";
    
    public static final String KUALI_WORKFLOW_APPLICATION_CODE = "kuali";
    public static final String LOOKUP_ACTION = "lookup.do";
    public static final String MULTIPLE_VALUE_LOOKUP_ACTION = "multipleValueLookup.do";
    public static final String LOOKUP_RESULTS_SEQUENCE_NUMBER = "lookupResultsSequenceNumber";
    public static final String LOOKUP_RESULTS_BO_CLASS_NAME = "lookupResultsBOClassName";
    public static final String LOOKED_UP_COLLECTION_NAME = "lookedUpCollectionName";
    public static final String MULTIPLE_VALUE_LOOKUP_PREVIOUSLY_SELECTED_OBJ_IDS_PARAM = "previouslySelectedObjectIds";
    public static final String MULTIPLE_VALUE_LOOKUP_OBJ_IDS_SEPARATOR = "||";
    public static final String MULTIPLE_VALUE_LOOKUP_DISPLAYED_OBJ_ID_PARAM_PREFIX = "displayedObjId-";
    public static final String MULTIPLE_VALUE_LOOKUP_SELECTED_OBJ_ID_PARAM_PREFIX = "selectedObjId-";
    public static final String LOOKUP_ANCHOR = "lookupAnchor";
    public static final String LOOKUPABLE_IMPL_ATTRIBUTE_NAME = "lookupableImplServiceName";
    public static final String LOOKUP_RESULTS_SEQUENCE = "LOOKUP_RESULT_SEQUENCE_NBR_SEQ";
    public static final String KUALI_LOOKUPABLE_IMPL = "kualiLookupable";
    public static final String DOC_HANDLER_ACTION = "DocHandler.do";
    public static final String DOC_HANDLER_METHOD = "docHandler";
    public static final String PARAMETER_DOC_ID = "docId";
    public static final String PARAMETER_COMMAND = "command";
    public static final String LOOKUP_METHOD = "performLookup";
    public static final String METHOD_DISPLAY_DOC_SEARCH_VIEW = "displayDocSearchView";
    public static final String MAINTENANCE_ACTION = "maintenance.do";
    public static final String MAINTENANCE_ADD_PREFIX = "add.";
    public static final String MAINTENANCE_COPY_ACTION = "Copy";
    public static final String MAINTENANCE_EDIT_ACTION = "Edit";
    public static final String MAINTENANCE_NEW_ACTION = "New";
    public static final String MAINTENANCE_COPY_METHOD_TO_CALL = "copy";
    public static final String MAINTENANCE_EDIT_METHOD_TO_CALL = "edit";
    public static final String MAINTENANCE_NEW_METHOD_TO_CALL = "start";
    public static final String MAINTENANCE_NEWWITHEXISTING_ACTION = "newWithExisting";
    public static final String MAINTENANCE_NEW_MAINTAINABLE = "document.newMaintainableObject.";
    public static final String MAINTENANCE_OLD_MAINTAINABLE = "document.oldMaintainableObject.";
    public static final String ENCRYPTED_LIST_PREFIX = "encryptedValues";
    public static final String MAPPING_BASIC = "basic";
    public static final String MAPPING_CANCEL = "cancel";
    public static final String MAPPING_CLOSE = "close";
    public static final String MAPPING_DISAPPROVE = "disapprove";
    public static final String MAPPING_DELETE = "delete";
    public static final String MAPPING_ERROR = "error";
    public static final String MAPPING_PORTAL = "portal";
    public static final String MAPPING_MULTIPLE_VALUE_LOOKUP = "multipleValueLookup";
    public static final String MAPPING_ROUTE_REPORT = "route_report";
    public static final String MAXLENGTH_SUFFIX = ".maxLength";
    public static final String METHOD_TO_CALL_ATTRIBUTE = "methodToCallAttribute";
    public static final String METHOD_TO_CALL_PATH = "methodToCallPath";
    public static final String METHOD_TO_CALL_BOPARM_LEFT_DEL = "(!!";
    public static final String METHOD_TO_CALL_BOPARM_RIGHT_DEL = "!!)";
    public static final String METHOD_TO_CALL_PARM1_LEFT_DEL = "(((";
    public static final String METHOD_TO_CALL_PARM1_RIGHT_DEL = ")))";
    public static final String METHOD_TO_CALL_PARM2_LEFT_DEL = "((#";
    public static final String METHOD_TO_CALL_PARM2_RIGHT_DEL = "#))";
    public static final String METHOD_TO_CALL_PARM3_LEFT_DEL = "((<";
    public static final String METHOD_TO_CALL_PARM3_RIGHT_DEL = ">))";
    public static final String METHOD_TO_CALL_PARM4_LEFT_DEL = "(([";
    public static final String METHOD_TO_CALL_PARM4_RIGHT_DEL = "]))";
    public static final String METHOD_TO_CALL_PARM5_LEFT_DEL = "((*";
    public static final String METHOD_TO_CALL_PARM5_RIGHT_DEL = "*))";
    public static final String METHOD_TO_CALL_PARM6_LEFT_DEL = "((%";
    public static final String METHOD_TO_CALL_PARM6_RIGHT_DEL = "%))";
    public static final String METHOD_TO_CALL_PARM7_LEFT_DEL = "((^";
    public static final String METHOD_TO_CALL_PARM7_RIGHT_DEL = "^))";
    public static final String METHOD_TO_CALL_PARM8_LEFT_DEL = "((&";
    public static final String METHOD_TO_CALL_PARM8_RIGHT_DEL = "&))";
    public static final String METHOD_TO_CALL_PARM9_LEFT_DEL = "((~";
    public static final String METHOD_TO_CALL_PARM9_RIGHT_DEL = "~))";
    public static final String METHOD_TO_CALL_PARM10_LEFT_DEL = "((/";
    public static final String METHOD_TO_CALL_PARM10_RIGHT_DEL = "/))";
    public static final String METHOD_TO_CALL_PARM11_LEFT_DEL = "(:;";
    public static final String METHOD_TO_CALL_PARM11_RIGHT_DEL = ";:)";
    public static final String METHOD_TO_CALL_PARM12_LEFT_DEL = "(::;";
    public static final String METHOD_TO_CALL_PARM12_RIGHT_DEL = ";::)";
    public static final String METHOD_TO_CALL_PARM13_LEFT_DEL = "(:::;";
    public static final String METHOD_TO_CALL_PARM13_RIGHT_DEL = ";:::)";
    // if more strings needed, then add more colons to the PARM11 strings above, e.g. (::; (:::;, etc.
    
    public static final String ANCHOR = "anchor";
    public static final String ANCHOR_TOP_OF_FORM = "topOfForm";
    public static final String QUESTION_ANCHOR = "questionAnchor";
    public static final String NOT_AVAILABLE_STRING = "N/A";
    public static final int    NEGATIVE_ONE = -1;
    public static final String CONTEXT_PATH = "contextPath";
    public static final String QUESTION_ACTION = "questionPrompt.do";
    public static final String QUESTION_CLICKED_BUTTON = "buttonClicked";
    public static final String QUESTION_ERROR_KEY = "questionErrorKey";
    public static final String QUESTION_ERROR_PROPERTY_NAME = "questionErrorPropertyName";
    public static final String QUESTION_ERROR_PARAMETER = "questionErrorParameter";
    public static final String QUESTION_IMPL_ATTRIBUTE_NAME = "questionType";
    public static final String QUESTION_INST_ATTRIBUTE_NAME = "questionIndex";
    public static final String QUESTION_PAGE_TITLE = "Question Dialog Page";
    public static final String QUESTION_REFRESH = "QuestionRefresh";
    public static final String QUESTION_CONTEXT = "context";
    public static final String QUESTION_TEXT_ATTRIBUTE_NAME = "questionText";
    public static final String QUESTION_REASON_ATTRIBUTE_NAME = "reason";
    public static final String QUESTION_SHOW_REASON_FIELD = "showReasonField";
    public static final String RELOAD_METHOD_TO_CALL = "reload";
    public static final String REFRESH_CALLER = "refreshCaller";
    public static final String REFRESH_MAPPING_PREFIX = "/Refresh";
    public static final String REQUIRED_FIELD_SYMBOL = "*";
    public static final String RETURN_LOCATION_PARAMETER = "returnLocation";
    public static final String RETURN_METHOD_TO_CALL = "refresh";
    public static final String ROUTE_METHOD = "route";
    public static final String SAVE_METHOD = "save";
    public static final String START_METHOD = "start";
    public static final String SEARCH_METHOD = "search";
    public static final String COPY_METHOD = "copy";
    public static final String ERRORCORRECT_METHOD = "correct";
    public static final String SOURCE = "Source";
    public static final String SQUARE_BRACKET_LEFT = "[";
    public static final String SQUARE_BRACKET_RIGHT = "]";
    public static final String TARGET = "Target";
    public static final String TO = "To";
    public static final String USER_SESSION_KEY = "UserSession";
    public static final String VERSION_NUMBER = "versionNumber";
    public static final KualiDecimal ZERO = new KualiDecimal("0.00");

    public static final String SEARCH_LIST_KEY_PREFIX = "searchResults";
    public static final String SEARCH_LIST_REQUEST_KEY = "searchResultKey";

    public static final String CORRECTION_FORM_KEY = "correctionFormKey";
    public static final int CORRECTION_RECENT_GROUPS_DAY = 10;

    public static final String SEARCH_DATA_KEY_PREFIX = "dataSearchResults";
    public static final String SEARCH_DATA_REQUEST_KEY = "searchResultDataKey";


    public static final String GLOBAL_ERRORS = "GLOBAL_ERRORS";
    public static final String GLOBAL_MESSAGES = "GlobalMessages";
    public static final String AD_HOC_ROUTE_PERSON_ERRORS = "newAdHocRoutePerson*,adHocRoutePerson*";
    public static final String AD_HOC_ROUTE_WORKGROUP_ERRORS = "newAdHocRouteWorkgroup*,adHocRouteWorkgroup*";
    public static final String DOCUMENT_DOCUMENT_ERRORS = "document.document*";
    public static final String DOCUMENT_EXPLANATION_ERRORS = "document.explanation*";
    public static final String DOCUMENT_REVERSAL_ERRORS = "document.reversal*";
    public static final String DOCUMENT_SELECTED_ERRORS = "document.selected*";
    public static final String DOCUMENT_HEADER_ERRORS = "document.header*";
    public static final String DOCUMENT_ERRORS_LESS_DOCUMENT = DOCUMENT_EXPLANATION_ERRORS + "," + DOCUMENT_REVERSAL_ERRORS + "," + DOCUMENT_SELECTED_ERRORS + "," + DOCUMENT_HEADER_ERRORS;
    public static final String DOCUMENT_ERRORS = DOCUMENT_DOCUMENT_ERRORS + "," + DOCUMENT_EXPLANATION_ERRORS + "," + DOCUMENT_REVERSAL_ERRORS + "," + DOCUMENT_SELECTED_ERRORS + "," + DOCUMENT_HEADER_ERRORS;
    public static final String DOCUMENT_NOTES_ERRORS = "newDocumentNote*";

    public enum NoteTypeEnum {
        BUSINESS_OBJECT_NOTE_TYPE ("BO","documentBusinessObject"),
        DOCUMENT_HEADER_NOTE_TYPE ("DH","documentHeader");
        private String noteTypeCode;
        private String noteTypePath;
        private NoteTypeEnum(String noteTypeCode,String noteTypePath) {
            this.noteTypeCode = noteTypeCode;
            this.noteTypePath = noteTypePath;
        }
        public String getCode() {
            return this.noteTypeCode;
        }
        public String getPath() {
            return this.noteTypePath;
        }
        public String getFullPath() {
            return RiceConstants.DOCUMENT_PROPERTY_NAME+"."+getPath();
        }
    }
    
    public static final String AND_LOGICAL_OPERATOR = "&&";
    public static final String OR_LOGICAL_OPERATOR = "|";
    public static final String NOT_LOGICAL_OPERATOR = "!";
    // add AND operator to thest if it is uncommented below
    public static final String[] LOGICAL_OPERATORS = { OR_LOGICAL_OPERATOR, NOT_LOGICAL_OPERATOR };
    public static final String[] QUERY_CHARACTERS = { "*", "?", "%", ">", "<", "..", OR_LOGICAL_OPERATOR, NOT_LOGICAL_OPERATOR, "=" };
    public static final String AUDIT_ERRORS = "AuditErrors";

    // Header Tab navigation constant values
    public static final String NAVIGATE_TO = "navigateTo.";
    public static final String HEADER_DISPATCH = "headerDispatch.";

    public static final String APC_ALLOWED_OPERATOR = "A";
    public static final String APC_DENIED_OPERATOR = "D";
    // country
    public static final String COUNTRY_CODE_UNITED_STATES = "US";

    public static final String MULTIPLE_VALUE = "multipleValues";
    public static final String MULTIPLE_VALUE_LABEL = "Lookup initial values";
    public static final String MULTIPLE_VALUE_NAME = "Multiple Value Name";

    // Agency type codes
    public static final String AGENCY_TYPE_CODE_FEDERAL = "F";

    // special chars that I don't know how to put into string literals in JSP expression language
    public static final String NEWLINE = "\n";

    // Workflow constants
    public static final String WORKFLOW_FYI_REQUEST = "F";
    public static final String WORKFLOW_APPROVE_REQUEST = "A";
    
    // Permission codes
    public static final String PERMISSION_READ_CODE = "R";
    public static final String PERMISSION_READ_DESCRIPTION = "READ";
    public static final String PERMISSION_MOD_CODE = "M";
    public static final String PERMISSION_MOD_DESCRIPTION = "MOD";
    public static final String PERMISSION_MODIFY = "modify";
    public static final String PERMISSION_VIEW = "view";
    // websession
    public static final String DOCUMENT_WEB_SCOPE = "documentWebScope";
    public static final String SESSION_SCOPE = "session";

    public static class DocumentStatusCodes {
        public static final String INITIATED = "?";
        public static final String CANCELLED = "X";
        public static final String ENROUTE = "R";
        public static final String DISAPPROVED = "D";
        public static final String APPROVED = "A";

        public static class CashReceipt {
            // once a CashReceipt gets approved, its financialDocumentStatus goes to "verified"
            public static final String VERIFIED = "V";

            // when a CashReceipt associated with a Deposit, its financialDocumentStatus changes to "interim" or "final"
            public static final String INTERIM = "I";
            public static final String FINAL = "F";

            // when the CMDoc is finalized, the CRs of its Deposits change to status "approved"
        }
    }

    public static final String ALLOWED_EMPLOYEE_STATUS_RULE = "ACTIVE_EMPLOYEE_STATUSES";

    public static class CoreApcParms {
        
        public static final String UNIVERSAL_USER_EDIT_WORKGROUP = "UNIVERSAL_USER_EDIT_GROUP";
        public static final String WORKFLOW_EXCEPTION_WORKGROUP = "EXCEPTION_GROUP";
        public static final String SUPERVISOR_WORKGROUP = "SUPERVISOR_GROUP";
    }
    

    public static class SystemGroupParameterNames {
        public static final String CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND = "CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND";


        public static final String LOOKUP_RESULTS_LIMIT = "RESULTS_LIMIT";
        public static final String MULTIPLE_VALUE_LOOKUP_RESULTS_PER_PAGE = "MULTIPLE_VALUE_RESULTS_PER_PAGE";
        public static final String MULTIPLE_VALUE_LOOKUP_RESULTS_EXPIRATION_AGE = "MULTIPLE_VALUE_RESULTS_EXPIRATION_SECONDS";

        public static final String DEFAULT_CAN_PERFORM_ROUTE_REPORT = "DEFAULT_CAN_PERFORM_ROUTE_REPORT";

        /**
         * Used by PurgePendingAttachmentsJob to compute the maximum amount of time a pending attachment is allowed to
         * persist on the file system before being deleted.
         */
        public static final String PURGE_PENDING_ATTACHMENTS_STEP_MAX_AGE = "purgePendingAttachmentsStepMaxAge";
    }

    public static class GeneralLedgerApplicationParameterKeys {
        public static final String INCOME_OBJECT_TYPE_CODES = "INCOME_OBJECT_TYPE_CODES";
        public static final String INCOME_TRANSFER_OBJECT_TYPE_CODES = "INCOME_TRANSFER_OBJECT_TYPE_CODES";
        public static final String EXPENSE_OBJECT_TYPE_CODES = "EXPENSE_OBJECT_TYPE_CODES";
        public static final String EXPENSE_TRANSFER_OBJECT_TYPE_CODES = "EXPENSE_TRANSFER_OBJECT_TYPE_CODES";
    }
    
    public static class GeneralLedgerCorrectionProcessApplicationParameterKeys {
        public static final String RECORD_COUNT_FUNCTIONALITY_LIMIT = "RECORD_COUNT_FUNCTIONALITY_LIMIT";
        public static final String RECORDS_PER_PAGE = "RECORDS_PER_PAGE";
    }

    public static class ParameterValues {
        public static final String YES = "Y";
        public static final String NO = "N";
    }

    public static class Maintenance {
        public static final String AFTER_CLASS_DELIM = "!!";
        public static final String AFTER_FIELDNAME_DELIM = "^^";
        public static final String AFTER_VALUE_DELIM = "::";
    }

    public static final String REQUEST_SEARCH_RESULTS = "reqSearchResults";
    public static final String REQUEST_SEARCH_RESULTS_SIZE = "reqSearchResultsSize";

    public static final int DEFAULT_NUM_OF_COLUMNS = 1;
    
    public static final String EMPLOYEE_LOOKUP_ERRORS = "document.employeeLookups";
        
    public static class OperationType {
        public static final String READ = "read";
        public static final String REPORT_ERROR = "with error";
        public static final String INSERT = "insert";
        public static final String UPDATE = "update";
        public static final String DELETE = "delete";
        public static final String SELECT = "select";
    }
    
    public static class TableRenderConstants {
        public static final String SWITCH_TO_PAGE_METHOD = "switchToPage";
        public static final String SORT_METHOD = "sort";
        public static final String SELECT_ALL_METHOD = "selectAll";
        public static final String UNSELECT_ALL_METHOD = "unselectAll";
        
        public static final String PREVIOUSLY_SORTED_COLUMN_INDEX_PARAM = "previouslySortedColumnIndex";
        public static final String VIEWED_PAGE_NUMBER = "viewedPageNumber";
    }
}