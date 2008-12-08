insert into trv_acct_fo (acct_fo_id, acct_fo_user_name) values (1, 'fred')
;
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name) values (2, 'fran')
;
insert into trv_acct_fo (acct_fo_id, acct_fo_user_name) values (3, 'frank')
;
insert into TRV_ACCT_TYPE values ('CAT', 'Clearing Account Type')
;
insert into TRV_ACCT_TYPE values ('EAT', 'Expense Account Type')
;
insert into TRV_ACCT_TYPE values ('IAT', 'Income Account Type')
;
insert into TRV_ACCT values ('a1', 'a1', 'CAT', 1)
;
insert into TRV_ACCT values ('a2', 'a2', 'EAT', 2)
;
insert into TRV_ACCT values ('a3', 'a3', 'IAT', 3)
;
insert into TRV_ACCT_EXT values ('a1', 'CAT')
;
insert into TRV_ACCT_EXT values ('a2', 'EAT')
;
insert into TRV_ACCT_EXT values ('a3', 'IAT')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a1')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a2')
;
insert into TRV_DOC_ACCT (DOC_HDR_ID, ACCT_NUM) values (1, 'a3')
;
insert into KRNS_DOC_TYP_T (DOC_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) values ('AMMD', '1A7FEB250342607EE043814FD881607E', 1, 'ACCT MAN', 'Y')
;
insert into KRNS_DOC_TYP_T (DOC_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) values ('ARQ', '1B7FEB250342607EE043814FD881607E', 1, 'ACCT REQ', 'Y')
;
insert into KRNS_DOC_TYP_T (DOC_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) values ('AMD', '1C7FEB250342607EE043814FD881607E', 1, 'ACCT', 'Y')
;

INSERT INTO KRSB_QRTZ_LOCKS values('TRIGGER_ACCESS')
;
INSERT INTO KRSB_QRTZ_LOCKS values('JOB_ACCESS')
;
INSERT INTO KRSB_QRTZ_LOCKS values('CALENDAR_ACCESS')
;
INSERT INTO KRSB_QRTZ_LOCKS values('STATE_ACCESS')
;
INSERT INTO KRSB_QRTZ_LOCKS values('MISFIRE_ACCESS')
;
insert into KRNS_NTE_TYP_T (NTE_TYP_CD, TYP_DESC_TXT, ACTV_IND) values ('BO', 'DOCUMENT BUSINESS OBJECT', 'Y')
;
insert into KRNS_NTE_TYP_T (NTE_TYP_CD, TYP_DESC_TXT, ACTV_IND) values ('DH', 'DOCUMENT HEADER', 'Y')
;
insert into KRNS_PARM_TYP_T ("PARM_TYP_CD","VER_NBR","NM","ACTV_IND") values ('CONFG', 0,'Config','Y')
;
insert into KRNS_PARM_TYP_T ("PARM_TYP_CD","VER_NBR","NM","ACTV_IND") values ('AUTH', 0,'Authorization','Y')
;
insert into KRNS_NMSPC_T ("NMSPC_CD","VER_NBR","NM","ACTV_IND") values ('KR-NS', 0,'Kuali Rice','Y')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","TXT","CONS_CD","PARM_DESC_TXT","PARM_TYP_CD","GRP_NM") VALUES ('KR-NS','All','CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND','Y','A','Flag for enabling/disabling the demonstration encryption check.','CONFG','KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","TXT","CONS_CD","PARM_DESC_TXT","PARM_TYP_CD","GRP_NM") VALUES ('KR-NS','All','ENABLE_FIELD_LEVEL_HELP_IND','N','A','Indicates whether field level help links are enabled on lookup pages and documents.','CONFG','KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","TXT","CONS_CD","PARM_DESC_TXT","PARM_TYP_CD","GRP_NM") VALUES ('KR-NS','Lookup','RESULTS_LIMIT','200','A','Limit of results returned in a lookup query','CONFG','KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","TXT","CONS_CD","PARM_DESC_TXT","PARM_TYP_CD","GRP_NM") VALUES ('KR-NS','Document','SUPERVISOR_GROUP','KUALI_ROLE_SUPERVISOR','A','Workgroup which can perform almost any function within Kuali.','AUTH','KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","TXT","CONS_CD","PARM_DESC_TXT","PARM_TYP_CD","GRP_NM") VALUES ('KR-NS','Document','DEFAULT_CAN_PERFORM_ROUTE_REPORT_IND','N','A','If Y, the Route Report button will be displayed on the document actions bar if the document is using the default DocumentAuthorizerBase.getDocumentActionFlags to set the canPerformRouteReport property of the returned DocumentActionFlags instance.','CONFG','KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","TXT","CONS_CD","PARM_DESC_TXT","PARM_TYP_CD","GRP_NM") VALUES ('KR-NS','Lookup','RESULTS_DEFAULT_MAX_COLUMN_LENGTH','70','A','If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.','CONFG','KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","TXT","CONS_CD","PARM_DESC_TXT","PARM_TYP_CD","GRP_NM") VALUES ('KR-NS','Document','MAX_FILE_SIZE_ATTACHMENT','5M','A','Maximum attachment upload size for the application. Used by KualiDocumentFormBase. Must be an integer, optionally followed by ''K'', ''M'', or ''G''.','CONFG','KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-NS', 'All', 'ENABLE_DIRECT_INQUIRIES_IND', 'CONFG', 'Y', 'Flag for enabling/disabling direct inquiries on screens that are drawn by the nervous system (i.e. lookups and maintenance documents)', 'A', 'KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-NS', 'Document', 'SESSION_TIMEOUT_WARNING_MESSAGE_TIME', 'CONFG', '5', 'The number of minutes before a session expires that user should be warned when a document uses pessimistic locking.', 'A', 'KUALI_FMSOPS') 
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","PARM_TYP_CD","TXT","PARM_DESC_TXT","CONS_CD","GRP_NM") VALUES ('KR-NS', 'Document', 'PESSIMISTIC_LOCK_ADMIN_GROUP', 'AUTH', 'KUALI_ROLE_SUPERVISOR', 'Workgroup which can perform admin deletion and lookup functions for Pessimistic Locks.', 'A', 'KUALI_FMSOPS') 
;
