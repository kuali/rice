DELETE FROM SH_PARM_T WHERE SH_PARM_NMSPC_CD='KR-NS' AND SH_PARM_TYP_CD='CONFG';
DELETE FROM SH_PARM_T WHERE SH_PARM_NMSPC_CD='KR-NS' AND SH_PARM_TYP_CD='VALID';
DELETE FROM SH_PARM_T WHERE SH_PARM_NMSPC_CD='KR-NS' AND SH_PARM_TYP_CD='AUTH';
DELETE FROM SH_PARM_T WHERE SH_PARM_NMSPC_CD='KR-NS' AND SH_PARM_TYP_CD='HELP';

DELETE FROM SH_PARM_TYP_T WHERE SH_PARM_TYP_CD='CONFG';
DELETE FROM SH_PARM_TYP_T WHERE SH_PARM_TYP_CD='VALID';
DELETE FROM SH_PARM_TYP_T WHERE SH_PARM_TYP_CD='AUTH';
DELETE FROM SH_PARM_TYP_T WHERE SH_PARM_TYP_CD='HELP';

DELETE FROM SH_PARM_DTL_TYP_T WHERE SH_PARM_DTL_TYP_CD='All';
DELETE FROM SH_PARM_DTL_TYP_T WHERE SH_PARM_DTL_TYP_CD='Batch';
DELETE FROM SH_PARM_DTL_TYP_T WHERE SH_PARM_DTL_TYP_CD='Document';
DELETE FROM SH_PARM_DTL_TYP_T WHERE SH_PARM_DTL_TYP_CD='Lookup';

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM)
values ('CONFG', 'Config');

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM)
values ('VALID', 'Document Validation');

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM)
values ('AUTH', 'Authorization');

insert into SH_PARM_TYP_T
(SH_PARM_TYP_CD, SH_PARM_TYP_NM)
values ('HELP', 'Help');

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS', 'All', 'All');

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS','Batch', 'Batch');

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS', 'Document', 'Document');

INSERT INTO SH_PARM_DTL_TYP_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_DTL_TYP_NM)
VALUES
('KR-NS', 'Lookup', 'Lookup');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND', 'CONFG', 'Y', 'Flag for enabling;disabling (Y;N) the demonstration encryption check.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'ENABLE_DIRECT_INQUIRIES_IND', 'CONFG', 'Y', 'Flag for enabling;disabling direct inquiries on screens that are drawn by the nervous system (i.e. lookups and maintenance documents)', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'ENABLE_FIELD_LEVEL_HELP_IND', 'CONFG', 'Y', 'Indicates whether field level help links are enabled on lookup pages and documents.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'All', 'MAX_FILE_SIZE_DEFAULT_UPLOAD', 'CONFG', '5M', 'Maximum file upload size for the application. Used by PojoFormBase. Must be an integer, optionally followed by "K", "M", or "G". Only used if no other upload limits are in effect.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'DEFAULT_CAN_PERFORM_ROUTE_REPORT_IND', 'CONFG', 'N', 'If Y, the Route Report button will be displayed on the document actions bar if the document is using the default DocumentAuthorizerBase.getDocumentActionFlags to set the canPerformRouteReport property of the returned DocumentActionFlags instance.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'EXCEPTION_GROUP', 'AUTH', 'WorkflowAdmin', 'The workgroup to which a user must be assigned to perform actions on documents in exception routing status.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'MAX_FILE_SIZE_ATTACHMENT', 'CONFG', '5M', 'Maximum attachment upload size for the application. Used by KualiDocumentFormBase. Must be an integer, optionally followed by "K", "M", or "G".', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES
('KR-NS', 'Document', 'SEND_NOTE_WORKFLOW_NOTIFICATION_ACTIONS', 'CONFG', 'K', 'Some documents provide the functionality to send notes to another user using a workflow FYI or acknowledge functionality. This parameter specifies the default action that will be used when sending notes. This parameter should be one of the following 2 values: "K" for acknowledge or "F" for fyi. Depending on the notes and workflow service implementation, other values may be possible.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'SUPERVISOR_GROUP', 'AUTH', 'WorkflowAdmin', 'Workgroup which can perform almost any function within Kuali.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T 
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'MULTIPLE_VALUE_RESULTS_EXPIRATION_SECONDS', 'CONFG', '86400', 'Lookup results may continue to be persisted in the DB long after they are needed. This parameter represents the maximum amount of time, in seconds, that the results will be allowed to persist in the DB before they are deleted from the DB.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T 
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'MULTIPLE_VALUE_RESULTS_PER_PAGE', 'CONFG', '100', 'Maximum number of rows that will be displayed on a look-up results screen.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'RESULTS_DEFAULT_MAX_COLUMN_LENGTH', 'CONFG', '70', 'If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) 
VALUES 
('KR-NS', 'Lookup', 'RESULTS_LIMIT', 'CONFG', '200', 'Maximum number of results returned in a look-up query.', 'A', 'WorkflowAdmin');

INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'UniversalUser', 'UNIVERSAL_USER_EDIT_GROUP', 'AUTH', 'WorkflowAdmin', 'The workgroup to which a user must be assigned to edit the universal user table.', 'A', 'WorkflowAdmin');

