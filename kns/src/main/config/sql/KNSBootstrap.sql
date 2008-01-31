-- KNS Core Data --
-- Core Doc Group Codes --
INSERT INTO FP_DOC_GROUP_T VALUES ('KR', '054EDFB3B260C8D2E043816FD881C8D2', 1, 'Kuali Rice', null)
/
INSERT INTO FP_DOC_GROUP_T VALUES ('MO', '054EDFB3B260C8D2E043816FD881C8EE', 1,	'Obsolete Maintenance Table', null)
/
INSERT INTO FP_DOC_GROUP_T VALUES ('MR', '054EDFB3B260C8D2E043816FD881C8EA', 1,	'Reference Table Maintenance', null)
/
INSERT INTO FP_DOC_TYPE_T VALUES ('PTYP', '1A6FEB2501C7607EE043814FD111607E', 1, 'MO', 'Parameter Type', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T VALUES ('PDTP', '1A6FEB2501C7607EE043814FD112607E', 1, 'MR', 'Parameter Detailed Type', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T VALUES ('PNMS', '1A6FEB2501C7607EE043814FD113607E', 1, 'MR', 'Parameter Namespace', 'N', 'Y', 'N', 0, 'N', 'N')
/

-- Core Params and Param Types --
insert into SH_PARM_TYP_T values ('CONFG', 121223422, 0,'Config',1)
/
insert into SH_PARM_TYP_T values ('AUTH', 121223423, 0,'Authorization',1)
/
insert into SH_PARM_NMSPC_T values ('KR-NS', 121223424, 0, 'Kuali Rice', 1)
/
INSERT INTO sh_parm_t
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS','Lookup','RESULTS_DEFAULT_MAX_COLUMN_LENGTH','CONFG','70','If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.','A','KUALI_FMSOPS')
/
INSERT INTO sh_parm_t
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS','Lookup','RESULTS_LIMIT','CONFG','70','If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.','A','KUALI_FMSOPS')
/
INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, ACTIVE_IND)
VALUES
('KR-NS','Lookup','MULTIPLE_VALUE_RESULTS_EXPIRATION_SECONDS','CONFG','60','Limit results returned for lookup - seconds expiration','A','Y')
/
INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, ACTIVE_IND)
VALUES
('KR-NS','Lookup','MULTIPLE_VALUE_RESULTS_PER_PAGE','CONFG','100','Limit results returned for lookup - page','A','Y')
/

INSERT INTO sh_parm_t ("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TXT","SH_PARM_CONS_CD","SH_PARM_DESC","SH_PARM_TYP_CD","WRKGRP_NM") VALUES ('KR-NS','All','ENABLE_FIELD_LEVEL_HELP_IND','N','A','Indicates whether field level help links are enabled on lookup pages and documents.','CONFG','KUALI_FMSOPS')
/
INSERT INTO sh_parm_t ("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TXT","SH_PARM_CONS_CD","SH_PARM_DESC","SH_PARM_TYP_CD","WRKGRP_NM") VALUES ('KR-NS','All','CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND','Y','A','Flag for enabling/disabling the demonstration encryption check.','CONFG','KUALI_FMSOPS')
/
INSERT INTO sh_parm_t ("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TXT","SH_PARM_CONS_CD","SH_PARM_DESC","SH_PARM_TYP_CD","WRKGRP_NM") VALUES ('KR-NS','Document','SUPERVISOR_GROUP','KUALI_ROLE_SUPERVISOR','A','Workgroup which can perform almost any function within Kuali.','AUTH','KUALI_FMSOPS')
/
INSERT INTO sh_parm_t ("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TXT","SH_PARM_CONS_CD","SH_PARM_DESC","SH_PARM_TYP_CD","WRKGRP_NM") VALUES ('KR-NS','Document','DEFAULT_CAN_PERFORM_ROUTE_REPORT_IND','N','A','If Y, the Route Report button will be displayed on the document actions bar if the document is using the default DocumentAuthorizerBase.getDocumentActionFlags to set the canPerformRouteReport property of the returned DocumentActionFlags instance.','CONFG','KUALI_FMSOPS')
/
INSERT INTO sh_parm_t ("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TXT","SH_PARM_CONS_CD","SH_PARM_DESC","SH_PARM_TYP_CD","WRKGRP_NM") VALUES ('KR-NS','Document','MAX_FILE_SIZE_ATTACHMENT','5M','A','Maximum attachment upload size for the application. Used by KualiDocumentFormBase. Must be an integer, optionally followed by ''K'', ''M'', or ''G''.','CONFG','KUALI_FMSOPS')
/
INSERT INTO SH_PARM_T(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM) VALUES('KR-NS', 'All', 'ENABLE_DIRECT_INQUIRIES_IND', sys_guid(), 1, 'CONFG', 'N', 'Flag for enabling/disabling direct inquiries on screens that are drawn by the nervous system (i.e. lookups and maintenance documents)', 'A', 'KUALI_FMSOPS') 
/

insert into SH_NTE_TYP_T values ('BO', '2D3C44FE49415102E043814FD8815102',  1,  'DOCUMENT BUSINESS OBJECT', 'Y')
/
insert into SH_NTE_TYP_T values ('DH', '2D3C44FE49425102E043814FD8815102',  1,  'DOCUMENT HEADER', 'Y')
/
INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','All','DEFAULT_MAX_UPLOAD_FILE_SIZE','CONFG','5M','Maximum file upload size for the application. Used by PojoFormBase. Must be an integer, optionally followed by "K", "M", or "G". Only used if no other upload limits are in effect.','A','KUALI_FMSOPS')
/
INSERT INTO sh_parm_t
("SH_PARM_NMSPC_CD","SH_PARM_DTL_TYP_CD","SH_PARM_NM","SH_PARM_TYP_CD","SH_PARM_TXT","SH_PARM_DESC","SH_PARM_CONS_CD","WRKGRP_NM")
VALUES
('KR-NS','Document','ATTACHMENT_MAX_FILE_SIZE','CONFG','5M','Maximum attachment upload size for the application. Used by KualiDocumentFormBase. Must be an integer, optionally followed by "K", "M", or "G".','A','KUALI_FMSOPS')
/
INSERT INTO SH_PARM_T
(SH_PARM_NMSPC_CD, SH_PARM_DTL_TYP_CD, SH_PARM_NM, OBJ_ID, VER_NBR, SH_PARM_TYP_CD, SH_PARM_TXT, SH_PARM_DESC, SH_PARM_CONS_CD, WRKGRP_NM)
VALUES
('KR-NS', 'Document', 'SEND_NOTE_WORKFLOW_NOTIFICATION_ACTIONS', sys_guid(), 0, 'CONFG', 'K', 'Some documents provide the functionality to send notes to another user using a workflow FYI or acknowledge functionality. This parameter specifies the default action that will be used when sending notes. This parameter should be one of the following 2 values: "K" for acknowledge or "F" for fyi. Depending on the notes and workflow service implementation, other values may be possible (see edu.iu.uis.eden.EdenConstants javadocs for details).', 'A', 'KUALI_FMSOPS')
/

insert into FP_DOC_STATUS_T values ('A',    '2E0671732A684002E043814FD8814002', 1,  'Approved')
/
insert into FP_DOC_STATUS_T values ('C',    '2E0671732A694002E043814FD8814002', 1,  'Cancelled')
/
insert into FP_DOC_STATUS_T values ('E',    '2E0671732A6A4002E043814FD8814002', 1,  'Extracted')
/
insert into FP_DOC_STATUS_T values ('I',    '2E0671732A6B4002E043814FD8814002', 1,  'In Process')
/
insert into FP_DOC_STATUS_T values ('II',   '2E0671732A6C4002E043814FD8814002', 1,  'In Process')
/
insert into FP_DOC_STATUS_T values ('O',    '2E0671732A6D4002E043814FD8814002', 1,  'Pend Org')
/
insert into FP_DOC_STATUS_T values ('OO',   '2E0671732A6E4002E043814FD8814002', 1,  'Pend Org')
/
insert into FP_DOC_STATUS_T values ('P',    '2E0671732A6F4002E043814FD8814002', 1,  'Pend Acct')
/
insert into FP_DOC_STATUS_T values ('PP',   '2E0671732A704002E043814FD8814002', 1,  'Pend Acct')
/
insert into FP_DOC_STATUS_T values ('R',    '2E0671732A714002E043814FD8814002', 1,  'Pend Specl')
/
insert into FP_DOC_STATUS_T values ('RR',   '2E0671732A724002E043814FD8814002', 1,  'Pend Specl')
/
insert into FP_DOC_STATUS_T values ('S',    '2E0671732A734002E043814FD8814002', 1,  'Pend CG')
/
insert into FP_DOC_STATUS_T values ('V',    '2E0671732A744002E043814FD8814002', 1,  'Validation')
/
insert into FP_DOC_STATUS_T values ('Q',    '2E0671732A754002E043814FD8814002', 1,  'Doc Specif')
/
