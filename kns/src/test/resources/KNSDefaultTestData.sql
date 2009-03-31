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
insert into KRNS_NTE_TYP_T (NTE_TYP_CD, TYP_DESC_TXT, ACTV_IND, OBJ_ID) values ('BO', 'DOCUMENT BUSINESS OBJECT', 'Y', '13369942-59be-102c-bdf6-89088ca7a02d')
;
insert into KRNS_NTE_TYP_T (NTE_TYP_CD, TYP_DESC_TXT, ACTV_IND, OBJ_ID) values ('DH', 'DOCUMENT HEADER', 'Y', '201993e4-59be-102c-bdf6-89088ca7a02d')
;
insert into KRNS_PARM_TYP_T (PARM_TYP_CD, VER_NBR, NM, ACTV_IND, OBJ_ID) values ('CONFG', 0,'Config','Y','28c6237c-59be-102c-bdf6-89088ca7a02d')
;
insert into KRNS_PARM_TYP_T (PARM_TYP_CD, VER_NBR, NM, ACTV_IND, OBJ_ID) values ('AUTH', 0,'Authorization','Y','4ea7f322-59be-102c-bdf6-89088ca7a02d')
;
insert into KRNS_NMSPC_T (NMSPC_CD,VER_NBR,NM,ACTV_IND, OBJ_ID) values ('KR-NS', 0,'Kuali Rice','Y','567c6e3e-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRNS_PARM_T (NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM,TXT,CONS_CD,PARM_DESC_TXT,PARM_TYP_CD,GRP_NM, OBJ_ID) VALUES ('KR-NS','All','CHECK_ENCRYPTION_SERVICE_OVERRIDE_IND','Y','A','Flag for enabling/disabling the demonstration encryption check.','CONFG','KUALI_FMSOPS','70884bc2-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRNS_PARM_T (NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM,TXT,CONS_CD,PARM_DESC_TXT,PARM_TYP_CD,GRP_NM, OBJ_ID) VALUES ('KR-NS','All','ENABLE_FIELD_LEVEL_HELP_IND','N','A','Indicates whether field level help links are enabled on lookup pages and documents.','CONFG','KUALI_FMSOPS','7873f2a0-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRNS_PARM_T (NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM,TXT,CONS_CD,PARM_DESC_TXT,PARM_TYP_CD,GRP_NM, OBJ_ID) VALUES ('KR-NS','Lookup','RESULTS_LIMIT','200','A','Limit of results returned in a lookup query','CONFG','KUALI_FMSOPS','841ccadc-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRNS_PARM_T (NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM,TXT,CONS_CD,PARM_DESC_TXT,PARM_TYP_CD,GRP_NM, OBJ_ID) VALUES ('KR-NS','Document','SUPERVISOR_GROUP','KUALI_ROLE_SUPERVISOR','A','Workgroup which can perform almost any function within Kuali.','AUTH','KUALI_FMSOPS','8e85c46a-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRNS_PARM_T (NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM,TXT,CONS_CD,PARM_DESC_TXT,PARM_TYP_CD,GRP_NM, OBJ_ID) VALUES ('KR-NS','Document','DEFAULT_CAN_PERFORM_ROUTE_REPORT_IND','N','A','If Y, the Route Report button will be displayed on the document actions bar if the document is using the default DocumentAuthorizerBase.getDocumentActionFlags to set the canPerformRouteReport property of the returned DocumentActionFlags instance.','CONFG','KUALI_FMSOPS','96a5ef1c-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRNS_PARM_T (NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM,TXT,CONS_CD,PARM_DESC_TXT,PARM_TYP_CD,GRP_NM, OBJ_ID) VALUES ('KR-NS','Lookup','RESULTS_DEFAULT_MAX_COLUMN_LENGTH','70','A','If a maxLength attribute has not been set on a lookup result field in the data dictionary, then the result column''s max length will be the value of this parameter. Set this parameter to 0 for an unlimited default length or a positive value (i.e. greater than 0) for a finite max length.','CONFG','KUALI_FMSOPS','9f0f5ec2-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRNS_PARM_T (NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM,TXT,CONS_CD,PARM_DESC_TXT,PARM_TYP_CD,GRP_NM, OBJ_ID) VALUES ('KR-NS','Document','MAX_FILE_SIZE_ATTACHMENT','5M','A','Maximum attachment upload size for the application. Used by KualiDocumentFormBase. Must be an integer, optionally followed by ''K'', ''M'', or ''G''.','CONFG','KUALI_FMSOPS','a9b7d278-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRNS_PARM_T (NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM,PARM_TYP_CD,TXT,PARM_DESC_TXT,CONS_CD,GRP_NM, OBJ_ID) VALUES ('KR-NS', 'All', 'ENABLE_DIRECT_INQUIRIES_IND', 'CONFG', 'Y', 'Flag for enabling/disabling direct inquiries on screens that are drawn by the nervous system (i.e. lookups and maintenance documents)', 'A', 'KUALI_FMSOPS', 'aff49108-59be-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRNS_PARM_T (NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM,PARM_TYP_CD,TXT,PARM_DESC_TXT,CONS_CD,GRP_NM, OBJ_ID) VALUES ('KR-NS', 'Document', 'SESSION_TIMEOUT_WARNING_MESSAGE_TIME', 'CONFG', '5', 'The number of minutes before a session expires that user should be warned when a document uses pessimistic locking.', 'A', 'KUALI_FMSOPS', '0198a95e-59bf-102c-bdf6-89088ca7a02d')
;
INSERT INTO KRNS_PARM_T (NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM,PARM_TYP_CD,TXT,PARM_DESC_TXT,CONS_CD,GRP_NM, OBJ_ID) VALUES ('KR-NS', 'Document', 'PESSIMISTIC_LOCK_ADMIN_GROUP', 'AUTH', 'KFS:KUALI_ROLE_SUPERVISOR', 'Workgroup which can perform admin deletion and lookup functions for Pessimistic Locks.', 'A', 'KUALI_FMSOPS', '0c40fce4-59bf-102c-bdf6-89088ca7a02d')
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
INSERT INTO KRIM_TYP_T (KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD) VALUES('1', '5ADF18B6D4827954E0404F8189D85002', 1, 'Default', Null, 'Y', 'KUALI')
;
INSERT INTO KRIM_TYP_T (KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD) VALUES('3', '5ADF18B6D4AC7954E0404F8189D85002', 1, 'Document Type (Permission)', 'documentTypePermissionTypeService', 'Y', 'KR-SYS')
;
insert into KRIM_TYP_T (KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD) VALUES('7', '5ADF18B6D4C07954E0404F8189D85002', 1, 'TestType', 'responsibilityResponsibilityTypeService', 'Y', 'KR-WKFLW')
;
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES('2', '5ADF18B6D4837954E0404F8189D85002', 1, 'Derived Role: Principal', 'activePrincipalRoleTypeService', 'Y', 'KR-IDM')
;
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES('45', '5B6013B3AD131A9CE0404F8189D87094', 1, 'Derived Role: Permission', 'documentEditorRoleTypeService', 'Y', 'KR-NS')
;
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES('54', sys_guid(), 1, 'Document Type (Responsibility)', 'documentTypeResponsibilityTypeService', 'Y', 'KR-KEW')
;

insert into KRIM_ATTR_DEFN_T (KIM_ATTR_DEFN_ID, OBJ_ID, VER_NBR, NM, LBL, SRVC_NM, ACTV_IND, NMSPC_CD, CMPNT_NM, APPL_URL) VALUES('13', '5ADF18B6D4947954E0404F8189D85002', 1, 'documentTypeName', Null, Null, 'Y', 'KR-WKFLW', 'org.kuali.rice.kim.bo.impl.KimAttributes', Null)
;
insert into KRIM_ATTR_DEFN_T (KIM_ATTR_DEFN_ID, OBJ_ID, VER_NBR, NM, LBL, SRVC_NM, ACTV_IND, NMSPC_CD, CMPNT_NM, APPL_URL) VALUES('16', '5ADF18B6D4977954E0404F8189D85002', 1, 'routeNodeName', Null, Null, 'Y', 'KR-WKFLW', 'org.kuali.rice.kim.bo.impl.KimAttributes', Null)
;
insert into KRIM_ATTR_DEFN_T (KIM_ATTR_DEFN_ID, OBJ_ID, VER_NBR, NM, LBL, SRVC_NM, ACTV_IND, NMSPC_CD, CMPNT_NM, APPL_URL) VALUES('40', '5C4970B2B2DF8277E0404F8189D80B30', 1, 'required', Null, Null, 'Y', 'KR-WKFLW', 'org.kuali.rice.kim.bo.impl.KimAttributes', Null)
;
insert into KRIM_ATTR_DEFN_T (KIM_ATTR_DEFN_ID, OBJ_ID, VER_NBR, NM, LBL, SRVC_NM, ACTV_IND, NMSPC_CD, CMPNT_NM, APPL_URL) VALUES('41', '5C4970B2B2E08277E0404F8189D80B30', 1, 'actionDetailsAtRoleMemberLevel', Null, Null, 'Y', 'KR-WKFLW', 'org.kuali.rice.kim.bo.impl.KimAttributes', Null)
;
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND, SORT_CD) VALUES('1', '5ADF18B6D4AD7954E0404F8189D85002', 1, '3', '13', 'Y', 'a')
;
insert into KRIM_TYP_ATTR_T (KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) VALUES('7', '5ADF18B6D4C17954E0404F8189D85002', 1, 'a', '7', '13', 'Y')
;
insert into KRIM_TYP_ATTR_T (KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) VALUES('8', '5ADF18B6D4C27954E0404F8189D85002', 1, 'b', '7', '16', 'Y')
;
insert into KRIM_TYP_ATTR_T (KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) VALUES('80', '5C4970B2B2E18277E0404F8189D80B30', 1, 'c', '7', '40', 'Y')
;
insert into KRIM_TYP_ATTR_T (KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, SORT_CD, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND) VALUES('81', '5C4970B2B2E28277E0404F8189D80B30', 1, 'd', '7', '41', 'Y')
;
INSERT INTO KRIM_TYP_ATTR_T (KIM_TYP_ATTR_ID, OBJ_ID, VER_NBR, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ACTV_IND, SORT_CD) VALUES('107', sys_guid(), 1, '54', '13', 'Y', 'a')
;

INSERT INTO KRIM_ROLE_T (ACTV_IND,KIM_TYP_ID,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','1','KR-SYS','5B31640F0105ADF1E0404F8189D84647','63','Technical Administrator',1)
;
INSERT INTO KRIM_ROLE_T (ACTV_IND,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','2',TO_DATE( '20081104143710', 'YYYYMMDDHH24MISS' ),'KUALI','5ADF18B6D4847954E0404F8189D85002','1','User',1)
;
INSERT INTO KRIM_ROLE_T (ACTV_IND,KIM_TYP_ID,LAST_UPDT_DT,NMSPC_CD,OBJ_ID,ROLE_ID,ROLE_NM,VER_NBR)
  VALUES ('Y','45',TO_DATE( '20081114141017', 'YYYYMMDDHH24MISS' ),'KR-NS','5BABFACC4F61A8EEE0404F8189D8770F','66','Document Editor',1)
;

-- assign to 'WorkflowAdmin'
INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, VER_NBR, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD)
VALUES('1282', 1, '5B4B421E43857717E0404F8189D821F7', '63', '2', 'G')
;


INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','1','Default','KUALI','5ADF18B6D4857954E0404F8189D85002','1',1)
;
INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR) VALUES ('Y','3','Initiate Document','KR-SYS','5ADF18B6D4BF7954E0404F8189D85002','10',1)
;
INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR) VALUES ('Y','3','Copy Document','KR-NS','5ADF18B6D4AF7954E0404F8189D85002','2',1)
;
INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR) VALUES ('Y','3','Administer Routing for Document','KR-WKFLW','5ADF18B6D4B07954E0404F8189D85002','3',1)
;
INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR) VALUES ('Y','3','Blanket Approve Document','KR-WKFLW','5ADF18B6D4B17954E0404F8189D85002','4',1)
;
INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR) VALUES ('Y','3','Open Document','KR-NS','5ADF18B6D4AE7954E0404F8189D85002','40',1)
;
INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR) VALUES ('Y','3','Route Document','KR-WKFLW','5ADF18B6D4B27954E0404F8189D85002','5',1)
;
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('299', '686ffd94-5848-102c-9db5-0bee8ae9eff4', 1, '1', 'Unrestricted Document Search', null, 'Y', 'KR-WKFLW')
;
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KR-SYS','5B4F0974494DEF33E0404F8189D84F24','149','10',1)
;
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KR-SYS','5B4F09744954EF33E0404F8189D84F24','156','2',1)
;
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KR-SYS','5B4F0974494BEF33E0404F8189D84F24','147','3',1)
;
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KR-SYS','5B4F0974494CEF33E0404F8189D84F24','148','4',1)
;
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KR-SYS','5B4F0974495DEF33E0404F8189D84F24','165','40',1)
;
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KUALI','5B4F09744960EF33E0404F8189D84F24','168','5',1)
;
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('1651', '5BAF0974495DEF33E0404F8189D84F24', 1, '1', 'Administer Pessimistic Locking', null, 'Y', 'KR-NS')
;
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('566', '95d7e616-5848-102c-9db5-0bee8ae9eff4', 1, '63', '299', 'Y')
;
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('193', '5C27A267EF667417E0404F8189D830A9', 1, '1', '149', 'Y')
;
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('201', '5C27A267EF6E7417E0404F8189D830A9', 1, '1', '156', 'Y')
;
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('190', '5C27A267EF637417E0404F8189D830A9', 1, '63', '147', 'Y')
;
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('191', '5C27A267EF647417E0404F8189D830A9', 1, '63', '148', 'Y')
;
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('211', '5C27A267EF787417E0404F8189D830A9', 1, '1', '165', 'Y')
;
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('213', '5C27A267EF7A7417E0404F8189D830A9', 1, '66', '168', 'Y')
;
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('2111', '5C27A26EJFD787417E0404F8189D830A', 1, '63', '1651', 'Y')
;
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('193', '5B4F09744A31EF33E0404F8189D84F24', 1, '147', '3', '13', 'RiceDocument')
;
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('194', '5B4F09744A32EF33E0404F8189D84F24', 1, '148', '3', '13', 'RiceDocument')
;
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('195', '5B4F09744A33EF33E0404F8189D84F24', 1, '149', '3', '13', 'RiceDocument')
;
--INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
--  VALUES('195', '5B4F09744A33EF33E0404F8189D84F24', 1, '149', '3', '13', 'AccountRequest')
--;
--INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
--  VALUES('222', '5B4F09744A4DEF33E0404F8189D84F25', 1, '149', '3', '13', 'AccountMaintenanceDocument')
--;
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('202', '5B4F09744A3AEF33E0404F8189D84F24', 1, '156', '3', '13', 'RiceDocument')
;
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('218', '5B4F09744A4AEF33E0404F8189D84F24', 1, '165', '3', '13', 'RiceDocument')
;
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('221', '5B4F09744A4DEF33E0404F8189D84F24', 1, '168', '3', '13', 'KualiDocument')
;
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('223', '5B4F09744A4AEF3E02345F8189D84F24', 1, '1651', '3', '13', 'RiceDocument')
;

INSERT INTO KRIM_RSP_TMPL_T(RSP_TMPL_ID, OBJ_ID, VER_NBR, NM, KIM_TYP_ID, DESC_TXT, ACTV_IND, NMSPC_CD)
  VALUES('2', '5B4F09744A4DEF33ED404F8189D44F24', 1, 'Resolve Exception', '54', null, 'Y', 'KR-WKFLW')
;
INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, RSP_TMPL_ID, nm, DESC_TXT, nmspc_cd, ACTV_IND)
  VALUES('93', '5B4F0974284DEF33ED404F8189D44F24', '2', null, null, 'KR-SYS', 'Y')
;
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, VER_NBR, ROLE_ID, RSP_ID, ACTV_IND)
  VALUES('1080', '5DF45238F5528846E0404F8189D840B8', 1, '63', '93', 'Y')
;
