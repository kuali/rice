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
package org.kuali.rice.kns.util;

/**
 * Holds error key constants.
 */
public class RiceKeyConstants {
    public static final String ERROR_EXISTENCE = "error.existence";
    public static final String ERROR_EXPIRED = "error.expired";
    public static final String ERROR_INACTIVE = "error.inactive";
    public static final String ERROR_CLOSED = "error.closed";
    public static final String ERROR_DUPLICATE_ELEMENT="error.duplicate.element";
    public static final String ERROR_INVALIDNEGATIVEAMOUNT = "error.invalidNegativeAmount";
    public static final String ERROR_REQUIRED = "error.required";
    public static final String ERROR_REQUIRED_FOR_US = "error.requiredForUs";
    public static final String ERROR_DATE = "error.invalidDate";
    public static final String ERROR_DATE_TIME = "error.invalidDateTime";
    public static final String ERROR_NUMBER = "error.invalidNumber";
    public static final String ERROR_BOOLEAN = "error.invalidBoolean";
    public final static String ERROR_CURRENCY = "error.currency";
    public final static String ERROR_CURRENCY_DECIMAL = "error.currency.decimal";
    public final static String ERROR_BIG_DECIMAL = "error.bigDecimal";
    public final static String ERROR_INTEGER = "error.integer";
    public final static String ERROR_LONG = "error.long";
    public final static String ERROR_PHONE_NUMBER = "error.phonenumber";
    public final static String ERROR_PERCENTAGE = "error.percentage";
    public final static String ERROR_NUMERIC = "error.numeric";
    public static final String ERROR_MIN_LENGTH = "error.minLength";
    public static final String ERROR_MAX_LENGTH = "error.maxLength";
    public static final String ERROR_INVALID_FORMAT = "error.invalidFormat";
    public static final String ERROR_EXCLUSIVE_MIN = "error.exclusiveMin";
    public static final String ERROR_INCLUSIVE_MAX = "error.inclusiveMax";
    
    public static final String ERROR_INACTIVATION_BLOCKED = "error.inactivation.blocked";

    public static final String ERROR_INVALID_ADHOC_PERSON_ID = "error.adhoc.invalid.person";
    public static final String ERROR_MISSING_ADHOC_PERSON_ID = "error.adhoc.missing.person";
    public static final String ERROR_UNAUTHORIZED_ADHOC_PERSON_ID = "error.adhoc.unauthorized.person";
    public static final String ERROR_INACTIVE_ADHOC_PERSON_ID = "error.adhoc.inactive.person";
    public static final String ERROR_INVALID_ADHOC_WORKGROUP_ID = "error.adhoc.invalid.workgroup";
    public static final String ERROR_MISSING_ADHOC_WORKGROUP_ID = "error.adhoc.missing.workgroup";

    public static final String ERROR_SECURE_FIELD = "error.secureField";
    public static final String ERROR_SEND_NOTE_NOTIFICATION_RECIPIENT = "error.send.note.notification.recipient";
    public static final String ERROR_SEND_NOTE_NOTIFICATION_DOCSTATUS = "error.send.note.notification.docStatus";
    public static final String MESSAGE_SEND_NOTE_NOTIFICATION_SUCCESSFUL = "message.send.note.notification.successful";
    public static final String MESSAGE_NOTE_NOTIFICATION_ANNOTATION = "message.note.notification.annotation";

    public static final String UNAUTHORIZED_INQUIRY = "unauthorized.inquiry";
    public static final String UNAUTHORIZED_LOOKUP = "unauthorized.lookup";
    public static final String UNAUTHORIZED_DOCUMENT = "unauthorized.document";
    public static final String UNAUTHORIZED_CUSTOM = "unauthorized.custom";
    
    public static final String MULTIPLE_VALUE_LOOKUP_ICON_LABEL = "multiple.value.lookup.icon.label";
    
    public static final String AUTHORIZATION_ERROR_GENERAL = "error.authorization.general";
    public static final String AUTHORIZATION_ERROR_INACTIVE_DOCTYPE = "error.authorization.inactiveDocumentType";
    public static final String AUTHORIZATION_ERROR_DOCTYPE = "error.authorization.documentType";
    public static final String AUTHORIZATION_ERROR_DOCUMENT = "error.authorization.document";
    public static final String AUTHORIZATION_ERROR_MAINTENANCE_NEWCOPY = "error.authorization.maintenance.newCopy";
    public static final String AUTHORIZATION_ERROR_MODULE = "error.authorization.module";
	public static final String AUTHORIZATION_ERROR_DOCUMENT_WORKGROUP = "error.authorization.workgroupInitiation";

    // Document-specific errors
    public static final String ERROR_DOCUMENT_ANNOTATION_MAX_LENGTH_EXCEEDED = "error.document.annotation.maxLength.exceeded";
    public static final String ERROR_DOCUMENT_DISAPPROVE_REASON_REQUIRED = "error.document.disapprove.reasonRequired";    public static final String ERROR_DOCUMENT_NO_DESCRIPTION = "error.document.noDescription";
    public static final String ERROR_UPLOADFILE_NULL = "error.uploadFile.null";
    public static final String ERROR_UPLOADFILE_EMPTY = "error.uploadFile.empty";
    public static final String ERROR_UNIMPLEMENTED = "error.unimplemented";
    public static final String ERROR_OPTIMISTIC_LOCK = "error.document.optimisticLockException";

    public static final String QUESTION_CONTINUATION_ACCOUNT_SELECTION = "document.question.selectContinuationAccount.text";
    public static final String QUESTION_SAVE_BEFORE_CLOSE = "document.question.saveBeforeClose.text";

    // General Maintenance Document Error Messages
    public static final String ERROR_DOCUMENT_MAINTENANCE_PRIMARY_KEYS_CHANGED_ON_EDIT = "error.document.maintenance.general.primaryKeysChangedOnEdit";
    public static final String ERROR_DOCUMENT_MAINTENANCE_KEYS_ALREADY_EXIST_ON_CREATE_NEW = "error.document.maintenance.general.objectAlreadyExistsByPrimaryKeysOnCreateNew";
    public static final String ERROR_DOCUMENT_AUTHORIZATION_RESTRICTED_FIELD_CHANGED = "error.document.maintenance.authorization.restrictedFieldChanged";
    public static final String ERROR_DOCUMENT_MAINTENANCE_PARTIALLY_FILLED_OUT_REF_FKEYS = "error.document.maintenance.partiallyFilledOutReferenceForeignKeys";
    public static final String ERROR_DOCUMENT_MAINTENANCE_FORMATTING_ERROR = "error.document.maintenance.formattingError";
    public static final String ERROR_DOCUMENT_INVALID_VALUE = "error.document.invalid.value";

    // Person errors
    public static final String ERROR_DOCUMENT_KUALIUSERMAINT_UNIQUE_EMPLID = "error.document.PersonMaintenance.UniqueEmplId";
    

    public static final String ERROR_CUSTOM = "error.custom";
    public static final String ERROR_INQUIRY = "error.inquiry";
    public static final String ERROR_MAINTENANCE_LOCKED = "error.maintenance.locked";
    public static final String ERROR_MAINTENANCE_LOCKED1 = "error.maintenance.locked.1";
    public static final String ERROR_MAINTENANCE_LOCKED2 = "error.maintenance.locked.2";
    public static final String ERROR_MAINTENANCE_LOCKED3 = "error.maintenance.locked.3";
    public static final String ERROR_ZERO_AMOUNT = "error.zeroAmount";
    public static final String ERROR_ZERO_OR_NEGATIVE_AMOUNT = "error.zeroOrNegativeAmount";
    public static final String ERROR_NEGATIVE_AMOUNT = "error.negativeAmount";
    public static final String ERROR_NOT_AMONG = "error.invalidNotAmong";

    public static final String MESSAGE_RELOADED = "message.document.reloaded";
    public static final String MESSAGE_ROUTE_SUCCESSFUL = "message.route.successful";
    public static final String MESSAGE_SAVED = "message.saved";
    public static final String MESSAGE_ROUTE_APPROVED = "message.route.approved";
    public static final String MESSAGE_ROUTE_DISAPPROVED = "message.route.disapproved";
    public static final String MESSAGE_ROUTE_CANCELED = "message.route.canceled";
    public static final String MESSAGE_ROUTE_ACKNOWLEDGED = "message.route.acknowledged";
    public static final String MESSAGE_ROUTE_FYIED = "message.route.fyied";
    public static final String MESSAGE_NO_HELP_TEXT = "message.nohelp";
    public static final String MESSAGE_REVERT_SUCCESSFUL = "message.revert.successful";
    public static final String MESSAGE_REVERT_UNNECESSARY = "message.revert.unnecessary";
    public static final String MESSAGE_DISAPPROVAL_NOTE_TEXT_INTRO = "message.disapprove.noteTextIntro";

    public static final String QUESTION_DISAPPROVE_DOCUMENT = "document.question.disapprove.text";

    public static final String ERROR_MISSING = "error.missing";
    
    // Application Parameter Component errors
    public static final String ERROR_APPLICATION_PARAMETERS_ALLOWED_RESTRICTION = "error.applicationParametersAllowedRestriction";
    public static final String ERROR_APPLICATION_PARAMETERS_DENIED_RESTRICTION = "error.applicationParametersDeniedRestriction";
    public static final String ERROR_PAYMENT_REASON_ALLOWED_RESTRICTION = "error.paymentReasonAllowedRestriction";
    public static final String ERROR_PAYMENT_REASON_DENIED_RESTRICTION = "error.paymentReasonDeniedRestriction";
    public static final String ERROR_APC_INDIRECT_DENIED_MULTIPLE = "error.apc.indirectDeniedMultiple";
    public static final String ERROR_APC_INDIRECT_ALLOWED_MULTIPLE = "error.apc.indirectAllowedMultiple";

    public static final Object WARNING_LINE_IMPORT_LENGTH_MISMATCH = "warning.core.bo.AccountImportLengthMismatch";

    // kim person document 
    public static final String ERROR_MULTIPLE_DEFAULT_SELETION = "error.multiple.default.selection";
    public static final String ERROR_MULTIPLE_PRIMARY_EMPLOYMENT = "error.multiple.primary.employment";
    public static final String ERROR_DUPLICATE_ENTRY = "error.duplicate.entry";
    public static final String ERROR_EMPTY_ENTRY = "error.empty.entry";
    public static final String ERROR_EXIST_PRINCIPAL_NAME = "error.exist.principalName";
    public static final String ERROR_ASSIGN_ROLE = "error.assign.role";
    public static final String ERROR_ASSIGN_PERMISSION = "error.assign.perimssion";
    public static final String ERROR_ASSIGN_RESPONSIBILITY = "error.assign.responsibility";
    public static final String ERROR_POPULATE_GROUP = "error.populate.group";
    public static final String ERROR_ACTIVE_TO_DATE_BEFORE_FROM_DATE = "error.active.todate.before.fromdate";
    public static final String ERROR_NOT_EMPLOYMENT_AFFILIATION_TYPE = "error.not.employment.affilationType";
    public static final String ERROR_NOT_UNIQUE_AFFILIATION_TYPE_PER_CAMPUE = "error.not.unique.affilationType.per.campus";
    public static final String ERROR_ROLE_QUALIFIER_REQUIRED = "error.role.qualifier.required";
    public static final String MESSAGE_SEND_AD_HOC_REQUESTS_SUCCESSFUL = "message.sendAdHocRequests.successful";
    
    //parameter document
    public static final String AUTHORIZATION_ERROR_PARAMETER = "error.authorization.parameter";
}

