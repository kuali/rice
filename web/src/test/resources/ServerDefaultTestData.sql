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
insert into KRNS_PARM_TYP_T ("PARM_TYP_CD","VER_NBR","NM","ACTV_IND") values ('CONFG', 0,'Config',1)
;
insert into KRNS_PARM_TYP_T ("PARM_TYP_CD","VER_NBR","NM","ACTV_IND") values ('AUTH', 0,'Authorization',1)
;
insert into KRNS_NMSPC_T ("NMSPC_CD","VER_NBR","NM","ACTV_IND") values ('KR-NS', 0,'Kuali Rice',1)
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
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","OBJ_ID","VER_NBR","PARM_TYP_CD","TXT", "PARM_DESC_TXT", "CONS_CD", "GRP_NM") VALUES('KR-NS', 'All', 'ENABLE_DIRECT_INQUIRIES_IND', sys_guid(), 1, 'CONFG', 'Y', 'Flag for enabling/disabling direct inquiries on screens that are drawn by the nervous system (i.e. lookups and maintenance documents)', 'A', 'KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","OBJ_ID","VER_NBR","PARM_TYP_CD","TXT", "PARM_DESC_TXT", "CONS_CD", "GRP_NM") VALUES('KR-NS', 'Document', 'SESSION_TIMEOUT_WARNING_MESSAGE_TIME', sys_guid(), 1, 'CONFG', '5', 'The number of minutes before a session expires that user should be warned when a document uses pessimistic locking.', 'A', 'KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T ("NMSPC_CD","PARM_DTL_TYP_CD","PARM_NM","OBJ_ID","VER_NBR","PARM_TYP_CD","TXT", "PARM_DESC_TXT", "CONS_CD", "GRP_NM") VALUES('KR-NS', 'Document', 'PESSIMISTIC_LOCK_ADMIN_GROUP', sys_guid(), 1, 'AUTH', 'KFS:KUALI_ROLE_SUPERVISOR', 'Workgroup which can perform admin deletion and lookup functions for Pessimistic Locks.', 'A', 'KUALI_FMSOPS')
;
INSERT INTO KRNS_PARM_T (CONS_CD, NMSPC_CD, OBJ_ID, PARM_DESC_TXT, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD,TXT, VER_NBR) VALUES ('A', 'KR-NS', SYS_GUID(), 'A semi-colon delimted list of strings representing date formats that the DateTimeService will use to parse dates when DateTimeServiceImpl.convertToSqlDate(String) or DateTimeServiceImpl.convertToDate(String) is called. Note that patterns will be applied in the order listed (and the first applicable one will be used). For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',  'All',  'STRING_TO_DATE_FORMATS',  'CONFG',  'MM/dd/yy;MM-dd-yy;MMMM dd, yyyy;MMddyy',  1  )
; 
INSERT INTO KRNS_PARM_T (CONS_CD, NMSPC_CD, OBJ_ID, PARM_DESC_TXT, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD,TXT, VER_NBR) VALUES ('A', 'KR-NS', SYS_GUID(), 'A single date format string that the DateTimeService will use to format dates to be used in a file name when DateTimeServiceImpl.toDateStringForFilename(Date) is called. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',  'All',  'DATE_TO_STRING_FORMAT_FOR_FILE_NAME',  'CONFG',  'yyyyMMdd',  1)  
; 
INSERT INTO KRNS_PARM_T (CONS_CD, NMSPC_CD, OBJ_ID, PARM_DESC_TXT, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD,TXT, VER_NBR) VALUES ('A', 'KR-NS', SYS_GUID(), 'A single date format string that the DateTimeService will use to format a date and time string to be used in a file name when DateTimeServiceImpl.toDateTimeStringForFilename(Date) is called.. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',  'All',  'TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME',  'CONFG',  'yyyyMMdd-HH-mm-ss-S',  1)  
;  
INSERT INTO KRNS_PARM_T (CONS_CD, NMSPC_CD, OBJ_ID, PARM_DESC_TXT, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD,TXT, VER_NBR)  VALUES  ('A', 'KR-NS', SYS_GUID(), 'A single date format string that the DateTimeService will use to format a date to be displayed on a web page. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',  'All',  'DATE_TO_STRING_FORMAT_FOR_USER_INTERFACE',  'CONFG',  'MM/dd/yyyy',  1)
; 
INSERT INTO KRNS_PARM_T (CONS_CD, NMSPC_CD, OBJ_ID, PARM_DESC_TXT, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD,TXT, VER_NBR)  VALUES  ('A', 'KR-NS', SYS_GUID(), 'A single date format string that the DateTimeService will use to format a date and time to be displayed on a web page. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',  'All',  'TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE',  'CONFG',  'MM/dd/yyyy hh:mm a',  1) 
;
INSERT INTO KRNS_PARM_T (CONS_CD, NMSPC_CD, OBJ_ID, PARM_DESC_TXT, PARM_DTL_TYP_CD, PARM_NM, PARM_TYP_CD,TXT, VER_NBR)  VALUES  ('A', 'KR-NS', SYS_GUID(), 'A semi-colon delimted list of strings representing date formats that the DateTimeService will use to parse date and times when DateTimeServiceImpl.convertToDateTime(String) or DateTimeServiceImpl.convertToSqlTimestamp(String) is called. Note that patterns will be applied in the order listed (and the first applicable one will be used). For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',  'All',  'STRING_TO_TIMESTAMP_FORMATS',  'CONFG',  'MM/dd/yyyy hh:mm a',  1 )  
;