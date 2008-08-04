-- KNS Core Data --
-- Core Doc Group Codes --
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('PTYP', '1A6FEB2501C7607EE043814FD111607E', 1, 'Parameter Type','Y')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('PDTP', '1A6FEB2501C7607EE043814FD112607E', 1, 'Parameter Detailed Type', 'Y')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, OBJ_ID, VER_NBR, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('PNMS', '1A6FEB2501C7607EE043814FD113607E', 1, 'Parameter Namespace', 'Y')
/

-- Core Params and Param Types --

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM, ACTIVE_IND)
values ('CONFG', 'Config', 'Y')
/

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM, ACTIVE_IND)
values ('VALID', 'Document Validation', 'Y')
/

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM, ACTIVE_IND)
values ('AUTH', 'Authorization', 'Y')
/

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM, ACTIVE_IND)
values ('HELP', 'Help', 'Y')
/

insert into SH_PARM_NMSPC_T
(SH_PARM_NMSPC_CD, SH_PARM_NMSPC_NM, ACTIVE_IND)
values ('KR-NS', 'Kuali Nervous System', 'Y')
/

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS', 'All', 'All')
/

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS','Batch', 'Batch')
/

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS', 'Document', 'Document')
/

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS', 'Lookup', 'Lookup')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND', 'CONFG', 'Y', 'Flag for enabling/disabling (Y/N) the demonstration encryption check.', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'ENABLE_DIRECT_INQUIRIES_IND', 'CONFG', 'Y', 'Flag for enabling/disabling direct inquiries on screens that are drawn by the nervous system (i.e. lookups and maintenance documents)', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'ENABLE_FIELD_LEVEL_HELP_IND', 'CONFG', 'Y', 'Indicates whether field level help links are enabled on lookup pages and documents.', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'MAX_FILE_SIZE_DEFAULT_UPLOAD', 'CONFG', '5M', 'Maximum file upload size for the application. Used by PojoFormBase. Must be an integer, optionally followed by "K", "M", or "G". Only used if no other upload limits are in effect.', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'DEFAULT_CAN_PERFORM_ROUTE_REPORT_IND', 'CONFG', 'N', 'If Y, the Route Report button will be displayed on the document actions bar if the document is using the default DocumentAuthorizerBase.getDocumentActionFlags to set the canPerformRouteReport property of the returned DocumentActionFlags instance.', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'EXCEPTION_GROUP', 'AUTH', 'WorkflowAdmin', 'The workgroup to which a user must be assigned to perform actions on documents in exception routing status.', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'MAX_FILE_SIZE_ATTACHMENT', 'CONFG', '5M', 'Maximum attachment upload size for the application. Used by KualiDocumentFormBase. Must be an integer, optionally followed by "K", "M", or "G".', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES
('KR-NS', 'Document', 'SEND_NOTE_WORKFLOW_NOTIFICATION_ACTIONS', 'CONFG', 'K', 'Some documents provide the functionality to send notes to another user using a workflow FYI or acknowledge functionality. This parameter specifies the default action that will be used when sending notes. This parameter should be one of the following 2 values: "K" for acknowledge or "F" for fyi. Depending on the notes and workflow service implementation, other values may be possible.', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'SUPERVISOR_GROUP', 'AUTH', 'WorkflowAdmin', 'Workgroup which can perform almost any function within Kuali.', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T 
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'MULTIPLE_VALUE_RESULTS_EXPIRATION_SECONDS', 'CONFG', '86400', 'Lookup results may continue to be persisted in the DB long after they are needed. This parameter represents the maximum amount of time, in seconds, that the results will be allowed to persist in the DB before they are deleted from the DB.', 'A', 'WorkflowAdmin')
/
INSERT INTO SH_PARM_T(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) VALUES('KR-NS', 'Document', 'SESSION_TIMEOUT_WARNING_MESSAGE_TIME', sys_guid(), 1, 'CONFG', '5', 'The number of minutes before a session expires that user should be warned when a document uses pessimistic locking.', 'A', 'KUALI_FMSOPS') 
/
INSERT INTO SH_PARM_T(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) VALUES('KR-NS', 'Document', 'PESSIMISTIC_LOCK_ADMIN_GROUP', sys_guid(), 1, 'AUTH', 'KUALI_ROLE_SUPERVISOR', 'Workgroup which can perform admin deletion and lookup functions for Pessimistic Locks.', 'A', 'KUALI_FMSOPS') 
/

INSERT INTO SH_PARM_T 
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'MULTIPLE_VALUE_RESULTS_PER_PAGE', 'CONFG', '100', 'Maximum number of rows that will be displayed on a look-up results screen.', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'RESULTS_DEFAULT_MAX_COLUMN_LENGTH', 'CONFG', '70', 'If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.', 'A', 'WorkflowAdmin')
/

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'RESULTS_LIMIT', 'CONFG', '200', 'Maximum number of results returned in a look-up query.', 'A', 'WorkflowAdmin')
/

insert into SH_NTE_TYP_T (NTE_TYP_CD, NTE_TYP_DESC, NTE_TYP_ACTV_IND) values ('BO', 'DOCUMENT BUSINESS OBJECT', 'Y')
/
insert into SH_NTE_TYP_T (NTE_TYP_CD, NTE_TYP_DESC, NTE_TYP_ACTV_IND) values ('DH', 'DOCUMENT HEADER', 'Y')
/
