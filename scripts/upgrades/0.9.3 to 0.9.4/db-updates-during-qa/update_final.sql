-- 
-- Copyright 2009 The Kuali Foundation
-- 
-- Licensed under the Educational Community License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
-- http://www.opensource.org/licenses/ecl2.php
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- 
-- please update this when you add files to this directory

alter table KREN_MSG_DELIV_T modify (LOCKD_DTTM date)
/
alter table KREN_MSG_T modify (CRTE_DTTM date)
/
alter table KREN_NTFCTN_MSG_DELIV_T modify (LOCKD_DTTM date)
/
alter table KREN_NTFCTN_T modify (CRTE_DTTM date,
SND_DTTM date,
AUTO_RMV_DTTM date,
LOCKD_DTTM date)
/

create table KRIM_PRSN_CACHE_T_bak as select * from KRIM_PRSN_CACHE_T
/

drop table KRIM_PRSN_CACHE_T cascade constraints purge
/

CREATE TABLE krim_prsn_cache_t
    (prncpl_id VARCHAR2(40) NOT NULL,
    prncpl_nm VARCHAR2(40),
    entity_id VARCHAR2(40),
    entity_typ_cd VARCHAR2(40),
    first_nm VARCHAR2(40),
    middle_nm VARCHAR2(40),
    last_nm VARCHAR2(40),
    prsn_nm VARCHAR2(40),
    campus_cd VARCHAR2(40),
    prmry_dept_cd VARCHAR2(40),
    emp_id VARCHAR2(40),
    last_updt_ts DATE,
    obj_id VARCHAR2(36) NOT NULL)
/





-- Constraints for KRIM_PRSN_CACHE_T

ALTER TABLE krim_prsn_cache_t
ADD CONSTRAINT krim_prsn_cache_tp1 PRIMARY KEY (prncpl_id)
/


-- End of DDL Script for Table KULDEV.KRIM_PRSN_CACHE_T

insert into KRIM_PRSN_CACHE_T select * from KRIM_PRSN_CACHE_T_bak
/
commit
/

drop table KRIM_PRSN_CACHE_T_bak
/

-- create Campus KIM type

INSERT INTO KRIM_TYP_T (KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES('17', '5ADF18B6D4F77954E0404F8189D85002', 1, 'Campus', 'campusRoleService', 'Y', 'KR-NS')
/
commit
/

-- Enhancement from uu to person doc that we did not get around to implementingÉ

delete from krim_role_perm_t where perm_id in  ('153', '154')
/
delete from krim_perm_attr_data_t where target_primary_key in  ('153', '154')
/
delete from krim_perm_t where perm_tmpl_id = '39'
/
delete from krim_perm_tmpl_t where perm_tmpl_id = '39'
/
delete from krim_typ_attr_t where kim_typ_id = '22'
/
delete from krim_attr_defn_t where kim_attr_defn_id = '17'
/
delete from krim_typ_t where kim_typ_id = '22'
/
commit
/

alter table KREN_CHNL_T DROP COLUMN OBJ_ID
/
alter table KREN_CNTNT_TYP_T DROP COLUMN OBJ_ID
/
alter table KREN_PRODCR_T DROP COLUMN OBJ_ID
/
alter table KREN_PRIO_T DROP COLUMN OBJ_ID
/
alter table KREN_RVWER_T DROP COLUMN OBJ_ID
/
-- need to remember to go back to "action-list-helpdesk-permissions.sql" and "doc-search-permissions.sql"
-- and reconcile related changes with these when we build the final upgrade scripts
-- (essentially, those old scripts can be removed)
--
-- UPDATE - 3/21/2009 - i've removed these scripts
-- so the deletions below won't need to be included in the finla upgrade script!!!

-- first of all, delete the original setup

DELETE FROM KRIM_ROLE_PERM_T WHERE PERM_ID IN (
	SELECT PERM_ID FROM KRIM_PERM_T WHERE PERM_TMPL_ID IN (
		SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='View Other Action List'
	)
)
/
DELETE FROM KRIM_ROLE_PERM_T WHERE PERM_ID IN (
	SELECT PERM_ID FROM KRIM_PERM_T WHERE PERM_TMPL_ID IN (
		SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='Unrestricted Document Search'
	)
)
/
DELETE FROM KRIM_PERM_T where PERM_TMPL_ID IN (SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='View Other Action List')
/
DELETE FROM KRIM_PERM_T where PERM_TMPL_ID IN (SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='Unrestricted Document Search')
/
DELETE FROM KRIM_PERM_TMPL_T where NM='View Other Action List'
/
DELETE FROM KRIM_PERM_TMPL_T where NM='Unrestricted Document Search'
/
commit
/

-- now, create as default permissions

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('298', sys_guid(), 1, '1', 'View Other Action List', null, 'Y', 'KR-WKFLW')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('564', sys_guid(), 1, '63', '298', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('299', sys_guid(), 1, '1', 'Unrestricted Document Search', null, 'Y', 'KR-WKFLW')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('566', sys_guid(), 1, '63', '299', 'Y')
/

commit
/
-- Update kim group table so kim type is set to the default value instead of null
-- Alter tables so null kim_type_ids are not allowed
UPDATE KRIM_GRP_T SET KIM_TYP_ID = '1' WHERE KIM_TYP_ID IS NULL
/
ALTER TABLE KRIM_DLGN_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_DLGN_MBR_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_GRP_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_GRP_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_PERM_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_PERM_TMPL_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_ROLE_MBR_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_ROLE_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_RSP_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_RSP_TMPL_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_TYP_ATTR_T MODIFY KIM_TYP_ID NOT NULL
/

-- Add a new column to the namespace table

alter table KRNS_NMSPC_T add (APPL_NMSPC_CD VARCHAR2(20))
/

-- Add some missing namespace entries to the table

INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KR-NTFCN', '5B960CFDBB360FDFE0404F8189D83CBD', 1, 'Notification', 'Y', NULL)
/
INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KUALI', '5ADF18B6D4817954E0404F8189D85002', 1, 'Kuali Systems', 'Y', NULL)
/
INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KR-BUS', '5B960CFDBB370FDFE0404F8189D83CBD', 1, 'Service Bus', 'Y', NULL)
/
INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KR-SYS', '5B960CFDBB390FDFE0404F8189D83CBD', 1, 'Enterprise Infrastructure', 'Y', NULL)
/

-- KULRICE-2625 - make KREW_DOC_TYP_T.RTE_VER_NBR nullable

alter table KREW_DOC_TYP_T modify RTE_VER_NBR NULL
/

-- KFSMI-2892 - missing ver_nbr defaults

alter table KRIM_DLGN_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_DLGN_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_GRP_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_ROLE_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
-- the float doc header extensions table really needs to be of type NUMBER which has a precision of 38
-- this results in proper conversion to mysql as part of the impex process
ALTER TABLE KREW_DOC_HDR_EXT_FLT_T MODIFY VAL NUMBER
/-- change namespace to KUALI on all groups with namespace of KFS from original EN_WRKGRP_T to KRIM_GRP_T conversion

UPDATE KRIM_GRP_T set NMSPC_CD='KUALI' where NMSPC_CD='KFS'
/

alter table KRNS_ATT_T modify MIME_TYP VARCHAR2(150)
/

DROP TABLE KRNS_DOC_TYP_ATTR_T
/-- KULRICE-2870 - generate names for all existing rules that don't have one
-- if I was better at PL/SQL i could probably figure out a better way to do
-- this, but it will basically set up and generate rule names for rules with
-- up to 1000 different versions, if you have rules with more versions than
-- that, you can update the loop condition

DECLARE
CURSOR cursor1 IS
	SELECT RULE_ID FROM KREW_RULE_T WHERE NM IS NULL AND CUR_IND=1;
BEGIN
	FOR r IN cursor1 LOOP
        execute immediate 'UPDATE KREW_RULE_T SET NM=SYS_GUID() WHERE RULE_ID='||r.RULE_ID;
    END LOOP;
END;
/

DECLARE
CURSOR cursor1 IS
	SELECT PREV.RULE_ID, RULE.NM FROM KREW_RULE_T PREV, KREW_RULE_T RULE
    WHERE PREV.RULE_ID=RULE.PREV_RULE_VER_NBR AND RULE.NM IS NOT NULL;
cnt NUMBER := 0;
BEGIN
    LOOP
        FOR r IN cursor1 LOOP
            UPDATE KREW_RULE_T SET NM=r.NM WHERE RULE_ID=r.RULE_ID;
        END LOOP;
        cnt := cnt + 1;
        IF cnt > 1000 THEN EXIT; END IF;
    END LOOP;
END;
/

UPDATE KREW_RULE_T RULE
SET (NM) =
(
  SELECT PREV.NM
  FROM KREW_RULE_T PREV
  WHERE PREV.RULE_ID=RULE.PREV_RULE_VER_NBR
  AND PREV.NM IS NOT NULL
)
WHERE RULE.NM IS NULL

ALTER TABLE KREW_RULE_T MODIFY NM NOT NULL
/alter table krns_att_t modify att_typ_cd VARCHAR2(40)
/

UPDATE KRIM_ATTR_DEFN_T set APPL_URL='${application.url}'
/DROP TABLE KREW_APP_CNST_T
/
DROP TABLE KRNS_DOC_TYP_T
/DROP TABLE krim_role_rel_t
/
DROP SEQUENCE KRIM_ROLE_REL_ID_S
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A semi-colon delimted list of strings representing date formats that the DateTimeService will use to parse dates when DateTimeServiceImpl.convertToSqlDate(String) or DateTimeServiceImpl.convertToDate(String) is called. Note that patterns will be applied in the order listed (and the first applicable one will be used). For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'STRING_TO_DATE_FORMATS',
'CONFG',
'MM/dd/yy;MM-dd-yy;MMMM dd, yyyy;MMddyy',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format dates to be used in a file name when DateTimeServiceImpl.toDateStringForFilename(Date) is called. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'DATE_TO_STRING_FORMAT_FOR_FILE_NAME',
'CONFG',
'yyyyMMdd',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date and time string to be used in a file name when DateTimeServiceImpl.toDateTimeStringForFilename(Date) is called.. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME',
'CONFG',
'yyyyMMdd-HH-mm-ss-S',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date to be displayed on a web page. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'DATE_TO_STRING_FORMAT_FOR_USER_INTERFACE',
'CONFG',
'MM/dd/yyyy',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date and time to be displayed on a web page. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE',
'CONFG',
'MM/dd/yyyy hh:mm a',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A semi-colon delimted list of strings representing date formats that the DateTimeService will use to parse date and times when DateTimeServiceImpl.convertToDateTime(String) or DateTimeServiceImpl.convertToSqlTimestamp(String) is called. Note that patterns will be applied in the order listed (and the first applicable one will be used). For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'STRING_TO_TIMESTAMP_FORMATS',
'CONFG',
'MM/dd/yyyy hh:mm a',
1
)
/UPDATE KREW_DOC_TYP_T
SET DOC_TYP_NM = 'NamespaceMaintenanceDocument',
    LBL = 'Namespace'
WHERE DOC_TYP_NM = 'ParameterNamespaceMaintenanceDocument'update KRIM_ENTITY_NM_T set NM_TYP_CD = 'PRFR' where NM_TYP_CD = 'PFRD'
/
update KRIM_ENTITY_EMAIL_T set EMAIL_TYP_CD = 'WRK' where EMAIL_TYP_CD = 'CAMPUS'
/
alter table KRIM_PND_DLGN_MT rename to KRIM_PND_DLGN_T
/
alter table KRIM_PND_ROLE_RSP_ACTN_MT add IGNORE_PREV_IND varchar2(1) NULL;
/
CREATE TABLE KRIM_PND_ROLE_PERM_T (
    FDOC_NBR        VARCHAR2(14) 	NOT NULL,
    ROLE_PERM_ID	VARCHAR2(40) 	NOT NULL,
    OBJ_ID      	VARCHAR2(36) 	DEFAULT SYS_GUID() NOT NULL,
    VER_NBR     	NUMBER(8,0)  	DEFAULT 1 NOT NULL,
    ROLE_ID    		VARCHAR2(40) 	NOT NULL,
    PERM_ID    		VARCHAR2(40) 	NOT NULL,
    ACTV_IND    	VARCHAR2(1)  	DEFAULT 'Y' NULL,
    PRIMARY KEY(ROLE_PERM_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_ROLE_RSP_T (
    FDOC_NBR        VARCHAR2(14) 	NOT NULL,
    ROLE_RSP_ID		VARCHAR2(40) 	NOT NULL,
    OBJ_ID      	VARCHAR2(36) 	DEFAULT SYS_GUID() NOT NULL,
    VER_NBR     	NUMBER(8,0)  	DEFAULT 1 NOT NULL,
    ROLE_ID    		VARCHAR2(40) 	NOT NULL,
    RSP_ID    		VARCHAR2(40) 	NOT NULL,
    ACTV_IND    	VARCHAR2(1)  	DEFAULT 'Y' NULL,
    PRIMARY KEY(ROLE_RSP_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_DLGN_MBR_T
(
    FDOC_NBR        VARCHAR2(14) NOT NULL,
    DLGN_MBR_ID  	VARCHAR2(40) NOT NULL,
    OBJ_ID      	VARCHAR2(36) NOT NULL,
    VER_NBR     	NUMBER(8,0)  DEFAULT 1 NOT NULL,
    DLGN_ID      	VARCHAR2(40) NOT NULL,
    MBR_ID   		VARCHAR2(40),
    MBR_NM			VARCHAR2(40),
    MBR_TYP_CD		VARCHAR2(40) NOT NULL,
    ACTV_IND    	VARCHAR2(1)  DEFAULT 'Y' NULL,
    ACTV_FRM_DT  	DATE NULL,
    ACTV_TO_DT  	DATE NULL,
    PRIMARY KEY(DLGN_MBR_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_DLGN_MBR_ATTR_DATA_T (
    FDOC_NBR          VARCHAR2(14)  NOT NULL,
    ATTR_DATA_ID      	VARCHAR2(40) NOT NULL,
    OBJ_ID            	VARCHAR2(36) NOT NULL,
    VER_NBR           	NUMBER(8,0) DEFAULT 1 NOT NULL,
    TARGET_PRIMARY_KEY	VARCHAR2(40) NULL,
    KIM_TYP_ID        	VARCHAR2(40) NULL,
    KIM_ATTR_DEFN_ID  	VARCHAR2(40) NULL,
    ATTR_VAL          	VARCHAR2(400) NULL,
    ACTV_IND    	VARCHAR2(1) DEFAULT 'Y' NULL,
    EDIT_FLAG    	VARCHAR2(1) DEFAULT 'N' NULL,
    PRIMARY KEY(ATTR_DATA_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_GRP_DOCUMENT_T
(
    FDOC_NBR          VARCHAR2(14) 		NOT NULL,
    GRP_ID            VARCHAR2(40) 		NOT NULL,
    OBJ_ID            VARCHAR2(36) 		DEFAULT SYS_GUID() NOT NULL,
    VER_NBR           NUMBER(8,0)  		DEFAULT 1 NOT NULL,
    KIM_TYP_ID 	  	  VARCHAR2(40) 	 	NOT NULL,
    GRP_NMSPC         VARCHAR2(100)  	NOT NULL,
    GRP_NM       	  VARCHAR2(400),
    GRP_DESC       	  VARCHAR2(400),
    ACTV_IND          VARCHAR2(1) 		DEFAULT 'Y',
    CONSTRAINT krim_grp_document_tp1 PRIMARY KEY ( FDOC_NBR )
)
/
CREATE TABLE KRIM_PND_GRP_MBR_T (
    FDOC_NBR        VARCHAR2(14) NOT NULL,
    GRP_MBR_ID  	VARCHAR2(40) NOT NULL,
    OBJ_ID      	VARCHAR2(36) NOT NULL,
    VER_NBR     	NUMBER(8,0)  DEFAULT 1 NOT NULL,
    GRP_ID      	VARCHAR2(40) NOT NULL,
    MBR_ID   		VARCHAR2(40),
    MBR_NM			VARCHAR2(40),
    MBR_TYP_CD		VARCHAR2(40) NOT NULL,
    ACTV_FRM_DT  	DATE NULL,
    ACTV_TO_DT  	DATE NULL,
    PRIMARY KEY(GRP_MBR_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_GRP_ATTR_DATA_T (
    FDOC_NBR          	VARCHAR2(14)  NOT NULL,
    ATTR_DATA_ID      	VARCHAR2(40) NOT NULL,
    OBJ_ID            	VARCHAR2(36) NOT NULL,
    VER_NBR           	NUMBER(8,0) DEFAULT 1 NOT NULL,
    TARGET_PRIMARY_KEY	VARCHAR2(40) NULL,
    KIM_TYP_ID        	VARCHAR2(40) NULL,
    KIM_ATTR_DEFN_ID  	VARCHAR2(40) NULL,
    ATTR_VAL          	VARCHAR2(400) NULL,
    PRIMARY KEY(ATTR_DATA_ID,FDOC_NBR)
)
/
alter table KRIM_DLGN_MBR_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to DLGN_MBR_ID
/
alter table KRIM_GRP_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to GRP_ID
/
alter table KRIM_PERM_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to PERM_ID
/
alter table KRIM_ROLE_MBR_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to ROLE_MBR_ID
/
alter table KRIM_PND_ROLE_MBR_ATTR_DATA_MT rename column TARGET_PRIMARY_KEY to ROLE_MBR_ID
/
alter table KRIM_RSP_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to RSP_ID
/
alter table KRIM_PND_DLGN_MBR_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to DLGN_MBR_ID
/
alter table KRIM_PND_GRP_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to GRP_ID
/DROP VIEW KRIM_GRP_MBR_V
/
DROP VIEW KRIM_GRP_V
/
DROP VIEW KRIM_PERM_ATTR_V
/
DROP VIEW KRIM_PERM_V
/
DROP VIEW KRIM_PRNCPL_V
/
DROP VIEW KRIM_ROLE_GRP_V
/
DROP VIEW KRIM_ROLE_PERM_V
/
DROP VIEW KRIM_ROLE_PRNCPL_V
/
DROP VIEW KRIM_ROLE_ROLE_V
/
DROP VIEW KRIM_ROLE_V
/
DROP VIEW KRIM_RSP_ROLE_ACTN_V
/
DROP VIEW KRIM_RSP_ROLE_V
/
    ALTER TABLE KREW_DOC_HDR_EXT_FLT_T MODIFY VAL NUMBER(30, 15)
/-- KULRICE-2981 - drop unused tables

DROP TABLE KR_QRTZ_PAUSED_TRIGGERS_GRPS
/

-- KULRICE-2982

ALTER TABLE krns_parm_t DROP COLUMN grp_nm
/
ALTER TABLE krns_parm_t DROP COLUMN actv_ind
/
ALTER TABLE krns_parm_t MODIFY parm_nm VARCHAR2(255)
/
ALTER TABLE krew_rule_t MODIFY nm NULL
/
ALTER TABLE krns_parm_t DROP CONSTRAINT KRNS_PARM_TP1
/
ALTER TABLE krns_parm_t ADD CONSTRAINT KRNS_PARM_TP1 PRIMARY KEY(NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM)
/
ALTER TABLE KRIM_ROLE_DOCUMENT_T DROP COLUMN role_typ_name
/
ALTER TABLE KRIM_ATTR_DEFN_T DROP COLUMN srvc_nm
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN rsp_id
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN role_id
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN grp_id
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN prncpl_id
/
ALTER TABLE krim_role_rsp_actn_t MODIFY role_rsp_id NULL
/
DROP TABLE KRIM_GRP_GRP_T
/
DROP TABLE KRIM_GRP_PRNCPL_T
/
DROP TABLE KRIM_DLGN_PRNCPL_T
/
DROP TABLE KRIM_DLGN_GRP_T
/
DROP TABLE KRIM_DLGN_ROLE_T
/
DROP TABLE KRIM_ROLE_PRNCPL_T
/
DROP TABLE KRIM_ROLE_GRP_T
/
DROP TABLE KRIM_ROLE_ROLE_T
/
update krns_parm_t set   parm_nm = 'EMAIL_NOTIFICATION_TEST_ADDRESS' where parm_nm = 'EMAIL_NOTIFICATION_TEST_ADDRESS '
/
CREATE OR REPLACE VIEW KRIM_GRP_MBR_V ( NMSPC_CD, GRP_NM, GRP_ID, PRNCPL_NM, PRNCPL_ID, MBR_GRP_NM, MBR_GRP_ID )
AS
SELECT g.NMSPC_CD
, g.grp_nm
, g.GRP_ID
, p.PRNCPL_NM
, p.PRNCPL_ID
, mg.GRP_NM AS mbr_grp_nm
, mg.GRP_ID AS mbr_grp_id
FROM KRIM_GRP_MBR_T gm
LEFT JOIN krim_grp_t g
ON g.GRP_ID = gm.GRP_ID
LEFT OUTER JOIN krim_grp_t mg
ON mg.GRP_ID = gm.MBR_ID
AND gm.MBR_TYP_CD = 'G'
LEFT OUTER JOIN krim_prncpl_t p
ON p.PRNCPL_ID = gm.MBR_ID
AND gm.MBR_TYP_CD = 'P'
LEFT OUTER JOIN krim_entity_nm_t en
ON en.ENTITY_ID = p.ENTITY_ID
AND en.DFLT_IND = 'Y'
AND en.ACTV_IND = 'Y'
ORDER BY nmspc_cd, grp_nm, prncpl_nm
/
CREATE OR REPLACE VIEW KRIM_GRP_V ( NMSPC_CD, GRP_NM, GRP_ID, GRP_TYP_NM, ATTR_NM, ATTR_VAL )
AS
SELECT g.NMSPC_CD
, g.grp_nm
, g.GRP_ID
, t.NM AS grp_typ_nm
, a.NM AS attr_nm
, d.ATTR_VAL AS attr_val
FROM krim_grp_t g
LEFT OUTER JOIN KRIM_GRP_ATTR_DATA_T d
ON d.grp_id = g.GRP_ID
LEFT OUTER JOIN KRIM_ATTR_DEFN_T a
ON a.KIM_ATTR_DEFN_ID = d.KIM_ATTR_DEFN_ID
LEFT OUTER JOIN KRIM_TYP_T t
ON g.KIM_TYP_ID = t.KIM_TYP_ID
/
CREATE OR REPLACE VIEW KRIM_PERM_ATTR_V ( TMPL_NMSPC_CD, TMPL_NM, PERM_TMPL_ID, PERM_NMSPC_CD, PERM_NM, PERM_ID, ATTR_NM, ATTR_VAL )
AS
SELECT
t.nmspc_cd AS tmpl_nmspc_cd
, t.NM AS tmpl_nm
, t.PERM_TMPL_ID
, p.nmspc_cd AS perm_nmspc_cd
, p.NM AS perm_nm
, p.PERM_ID
, a.NM AS attr_nm
, ad.ATTR_VAL AS attr_val
FROM KRIM_PERM_T p
LEFT JOIN KRIM_PERM_TMPL_T t
ON p.PERM_TMPL_ID = t.PERM_TMPL_ID
LEFT OUTER JOIN KRIM_PERM_ATTR_DATA_T ad
ON p.PERM_ID = ad.perm_id
LEFT OUTER JOIN KRIM_ATTR_DEFN_T a
ON ad.KIM_ATTR_DEFN_ID = a.KIM_ATTR_DEFN_ID
ORDER BY tmpl_nmspc_cd, tmpl_nm, perm_nmspc_cd, perm_id, attr_nm
/
CREATE OR REPLACE VIEW KRIM_PERM_V ( TMPL_NMSPC_CD, TMPL_NM, PERM_TMPL_ID, PERM_NMSPC_CD, PERM_NM, PERM_ID, PERM_TYP_NM, SRVC_NM )
AS
SELECT
t.nmspc_cd AS tmpl_nmspc_cd
, t.NM AS tmpl_nm
, t.PERM_TMPL_ID
, p.nmspc_cd AS perm_nmspc_cd
, p.NM AS perm_nm
, p.PERM_ID
, typ.NM AS perm_typ_nm
, typ.SRVC_NM
FROM KRIM_PERM_T p
INNER JOIN KRIM_PERM_TMPL_T t
ON p.PERM_TMPL_ID = t.PERM_TMPL_ID
LEFT OUTER JOIN KRIM_TYP_T typ
ON t.KIM_TYP_ID = typ.KIM_TYP_ID
/
CREATE OR REPLACE VIEW KRIM_PRNCPL_V ( PRNCPL_ID, PRNCPL_NM, FIRST_NM, LAST_NM, AFLTN_TYP_CD, CAMPUS_CD, EMP_STAT_CD, EMP_TYP_CD )
AS
SELECT
p.PRNCPL_ID
,p.PRNCPL_NM
,en.FIRST_NM
,en.LAST_NM
,ea.AFLTN_TYP_CD
,ea.CAMPUS_CD
,eei.EMP_STAT_CD
,eei.EMP_TYP_CD
FROM krim_prncpl_t p
LEFT OUTER JOIN krim_entity_emp_info_t eei
ON eei.ENTITY_ID = p.ENTITY_ID
LEFT OUTER JOIN krim_entity_afltn_t ea
ON ea.ENTITY_ID = p.ENTITY_ID
LEFT OUTER JOIN krim_entity_nm_t en
ON p.ENTITY_ID = en.ENTITY_ID
AND 'Y' = en.DFLT_IND
/
CREATE OR REPLACE VIEW KRIM_ROLE_GRP_V ( NMSPC_CD, ROLE_NM, ROLE_ID, GRP_NMSPC_CD, GRP_NM, ROLE_MBR_ID, ATTR_NM, ATTR_VAL )
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.role_id
, g.NMSPC_CD AS grp_nmspc_cd
, g.GRP_NM
, rm.ROLE_MBR_ID
, a.NM AS attr_nm
, d.ATTR_VAL AS attr_val
FROM KRIM_ROLE_MBR_T rm
LEFT JOIN KRIM_ROLE_T r
ON r.ROLE_ID = rm.ROLE_ID
LEFT JOIN KRIM_GRP_T g
ON g.GRP_ID = rm.MBR_ID
LEFT OUTER JOIN KRIM_ROLE_MBR_ATTR_DATA_T d
ON d.role_mbr_id = rm.ROLE_MBR_ID
LEFT OUTER JOIN KRIM_ATTR_DEFN_T a
ON a.KIM_ATTR_DEFN_ID = d.KIM_ATTR_DEFN_ID
WHERE rm.MBR_TYP_CD = 'G'
ORDER BY nmspc_cd, role_nm, grp_nmspc_cd, grp_nm, role_mbr_id, attr_nm
/
CREATE OR REPLACE VIEW KRIM_ROLE_PERM_V ( NMSPC_CD, ROLE_NM, ROLE_ID, TMPL_NMSPC_CD, TMPL_NM, PERM_TMPL_ID, PERM_NMPSC_CD, PERM_NM, PERM_ID, ATTR_NM, ATTR_VAL )
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.role_id
, pt.NMSPC_CD AS tmpl_nmspc_cd
, pt.NM AS tmpl_nm
, pt.PERM_TMPL_ID
, p.NMSPC_CD AS perm_nmpsc_cd
, p.NM AS perm_nm
, p.PERM_ID
, a.NM AS attr_nm
, ad.ATTR_VAL AS attr_val
FROM KRIM_PERM_T p
LEFT JOIN KRIM_PERM_TMPL_T pt
ON p.PERM_TMPL_ID = pt.PERM_TMPL_ID
LEFT OUTER JOIN KRIM_PERM_ATTR_DATA_T ad
ON p.PERM_ID = ad.perm_id
LEFT OUTER JOIN KRIM_ATTR_DEFN_T a
ON ad.KIM_ATTR_DEFN_ID = a.KIM_ATTR_DEFN_ID
LEFT OUTER JOIN KRIM_ROLE_PERM_T rp
ON rp.PERM_ID = p.PERM_ID
LEFT OUTER JOIN KRIM_ROLE_T r
ON rp.ROLE_ID = r.ROLE_ID
ORDER BY NMSPC_CD, role_nm, tmpl_nmspc_cd, tmpl_nm, perm_id, attr_nm
/
CREATE OR REPLACE VIEW KRIM_ROLE_PRNCPL_V ( NMSPC_CD, ROLE_NM, ROLE_ID, PRNCPL_NM, PRNCPL_ID, FIRST_NM, LAST_NM, ROLE_MBR_ID, ATTR_NM, ATTR_VAL )
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.ROLE_ID
, p.PRNCPL_NM
, p.PRNCPL_ID
, en.FIRST_NM
, en.LAST_NM
, rm.ROLE_MBR_ID
, ad.NM AS attr_nm
, rmad.ATTR_VAL AS attr_val
FROM KRIM_ROLE_T r
LEFT OUTER JOIN KRIM_ROLE_MBR_T rm
ON r.ROLE_ID = rm.ROLE_ID
LEFT OUTER JOIN KRIM_ROLE_MBR_ATTR_DATA_T rmad
ON rm.ROLE_MBR_ID = rmad.role_mbr_id
LEFT OUTER JOIN KRIM_ATTR_DEFN_T ad
ON rmad.KIM_ATTR_DEFN_ID = ad.KIM_ATTR_DEFN_ID
LEFT OUTER JOIN KRIM_PRNCPL_T p
ON rm.MBR_ID = p.PRNCPL_ID
AND rm.mbr_typ_cd = 'P'
LEFT OUTER JOIN KRIM_ENTITY_NM_T en
ON p.ENTITY_ID = en.ENTITY_ID
WHERE (en.DFLT_IND = 'Y')
ORDER BY nmspc_cd, role_nm, prncpl_nm, rm.ROLE_MBR_ID, attr_nm
/
CREATE OR REPLACE VIEW KRIM_ROLE_ROLE_V ( NMSPC_CD, ROLE_NM, ROLE_ID, MBR_ROLE_NMSPC_CD, MBR_ROLE_NM, MBR_ROLE_ID, ROLE_MBR_ID, ATTR_NM, ATTR_VAL )
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.role_id
, mr.NMSPC_CD AS mbr_role_nmspc_cd
, mr.role_NM AS mbr_role_nm
, mr.role_id AS mbr_role_id
, rm.role_mbr_id
, a.NM AS attr_nm
, d.ATTR_VAL AS attr_val
FROM KRIM_ROLE_MBR_T rm
LEFT JOIN KRIM_ROLE_T r
ON r.ROLE_ID = rm.ROLE_ID
LEFT JOIN KRIM_role_T mr
ON mr.role_ID = rm.MBR_ID
LEFT OUTER JOIN KRIM_ROLE_MBR_ATTR_DATA_T d
ON d.role_mbr_id = rm.ROLE_MBR_ID
LEFT OUTER JOIN KRIM_ATTR_DEFN_T a
ON a.KIM_ATTR_DEFN_ID = d.KIM_ATTR_DEFN_ID
WHERE rm.MBR_TYP_CD = 'R'
ORDER BY nmspc_cd, role_nm, mbr_role_nmspc_cd, mbr_role_nm, role_mbr_id, attr_nm
/
CREATE OR REPLACE VIEW KRIM_ROLE_V ( NMSPC_CD, ROLE_NM, ROLE_ID, ROLE_TYP_NM, SRVC_NM, KIM_TYP_ID )
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.ROLE_ID
, t.nm AS role_typ_nm
, t.SRVC_NM
, t.KIM_TYP_ID
FROM KRIM_ROLE_T r
, KRIM_TYP_T t
WHERE t.KIM_TYP_ID = r.KIM_TYP_ID
AND r.ACTV_IND = 'Y'
ORDER BY nmspc_cd
, role_nm
/
CREATE OR REPLACE VIEW KRIM_RSP_ATTR_V ( RESPONSIBILITY_TYPE_NAME, RSP_TEMPLATE_NAME, RSP_NAMESPACE_CODE, RSP_NAME, RSP_ID, ATTRIBUTE_NAME, ATTRIBUTE_VALUE )
AS
SELECT
krim_typ_t.NM AS responsibility_type_name
, KRIM_rsp_TMPL_T.NM AS rsp_TEMPLATE_NAME
, KRIM_rsp_T.nmspc_cd AS rsp_namespace_code
, KRIM_rsp_T.NM AS rsp_NAME
, krim_rsp_t.RSP_ID AS rsp_id
, KRIM_ATTR_DEFN_T.NM AS attribute_name
, KRIM_rsp_ATTR_DATA_T.ATTR_VAL AS attribute_value
FROM KRIM_rsp_T KRIM_rsp_T
INNER JOIN KRIM_rsp_ATTR_DATA_T KRIM_rsp_ATTR_DATA_T
ON KRIM_rsp_T.rsp_ID = KRIM_rsp_ATTR_DATA_T.rsp_id
INNER JOIN KRIM_ATTR_DEFN_T KRIM_ATTR_DEFN_T
ON KRIM_rsp_ATTR_DATA_T.KIM_ATTR_DEFN_ID = KRIM_ATTR_DEFN_T.KIM_ATTR_DEFN_ID
INNER JOIN KRIM_rsp_TMPL_T KRIM_rsp_TMPL_T
ON KRIM_rsp_T.rsp_TMPL_ID = KRIM_rsp_TMPL_T.rsp_TMPL_ID
INNER JOIN KRIM_TYP_T KRIM_TYP_T
ON KRIM_rsp_TMPL_T.KIM_TYP_ID = KRIM_TYP_T.KIM_TYP_ID
ORDER BY rsp_TEMPLATE_NAME, rsp_NAME, attribute_name
/
CREATE OR REPLACE VIEW KRIM_RSP_ROLE_ACTN_V ( RSP_NMSPC_CD, RSP_ID, NMSPC_CD, ROLE_NM, ROLE_ID, MBR_ID, MBR_TYP_CD, ROLE_MBR_ID, ACTN_TYP_CD, ACTN_PLCY_CD, IGNORE_PREV_IND, PRIORITY_NBR )
AS
select
rsp.nmspc_cd as rsp_nmspc_cd
, rsp.rsp_id
, r.NMSPC_CD
, r.ROLE_NM
, rr.ROLE_ID
, rm.MBR_ID
, rm.MBR_TYP_CD
, rm.ROLE_MBR_ID
, actn.ACTN_TYP_CD
, actn.ACTN_PLCY_CD
, actn.IGNORE_PREV_IND
, actn.PRIORITY_NBR
from krim_rsp_t rsp
left join krim_rsp_tmpl_t rspt
on rsp.rsp_tmpl_id = rspt.rsp_tmpl_id
left outer join krim_role_rsp_t rr
on rr.rsp_id = rsp.rsp_id
left outer join KRIM_ROLE_MBR_T rm
ON rm.ROLE_ID = rr.ROLE_ID
left outer join KRIM_ROLE_RSP_ACTN_T actn
ON actn.ROLE_RSP_ID = rr.ROLE_RSP_ID
AND (actn.ROLE_MBR_ID = rm.ROLE_MBR_ID OR actn.ROLE_MBR_ID = '*')
left outer join krim_role_t r
on rr.role_id = r.role_id
order by rsp_nmspc_cd
, rsp_id
, role_id
, role_mbr_id
/
CREATE OR REPLACE VIEW KRIM_RSP_ROLE_V ( RSP_TMPL_NMSPC_CD, RSP_TMPL_NM, RSP_NMSPC_CD, RSP_NM, RSP_ID, ATTR_NM, ATTR_VAL, NMSPC_CD, ROLE_NM, ROLE_ID )
AS
select
rspt.nmspc_cd as rsp_tmpl_nmspc_cd
, rspt.nm as rsp_tmpl_nm
, rsp.nmspc_cd as rsp_nmspc_cd
, rsp.nm as rsp_nm
, rsp.rsp_id
, a.nm as attr_nm
, d.attr_val
, r.NMSPC_CD
, r.ROLE_NM
, rr.ROLE_ID
from krim_rsp_t rsp
left join krim_rsp_tmpl_t rspt
on rsp.rsp_tmpl_id = rspt.rsp_tmpl_id
left outer join krim_rsp_attr_data_t d
on rsp.rsp_id = d.rsp_id
left outer join krim_attr_defn_t a
on d.kim_attr_defn_id = a.kim_attr_defn_id
left outer join krim_role_rsp_t rr
on rr.rsp_id = rsp.rsp_id
left outer join krim_role_t r
on rr.role_id = r.role_id
order by rsp_tmpl_nmspc_cd, rsp_tmpl_nm, rsp_nmspc_cd, rsp_nm, rsp_id, attr_nm, attr_val
/
-- KULRICE-2970: Renamed some table columns to reflect the change from "ignore previous" to "force action".

ALTER TABLE KREW_ACTN_RQST_T RENAME COLUMN IGN_PREV_ACTN_IND TO FRC_ACTN
/
ALTER TABLE KREW_RULE_T RENAME COLUMN IGNR_PRVS TO FRC_ACTN
/
ALTER TABLE KRIM_PND_ROLE_RSP_ACTN_MT RENAME COLUMN IGNORE_PREV_IND TO FRC_ACTN
/
ALTER TABLE KRIM_ROLE_RSP_ACTN_T RENAME COLUMN IGNORE_PREV_IND TO FRC_ACTN
/
CREATE OR REPLACE VIEW KRIM_RSP_ROLE_ACTN_V ( RSP_NMSPC_CD, RSP_ID, NMSPC_CD, ROLE_NM, ROLE_ID, MBR_ID, MBR_TYP_CD, ROLE_MBR_ID, ACTN_TYP_CD, ACTN_PLCY_CD, FRC_ACTN, PRIORITY_NBR )
AS
select
rsp.nmspc_cd as rsp_nmspc_cd
, rsp.rsp_id
, r.NMSPC_CD
, r.ROLE_NM
, rr.ROLE_ID
, rm.MBR_ID
, rm.MBR_TYP_CD
, rm.ROLE_MBR_ID
, actn.ACTN_TYP_CD
, actn.ACTN_PLCY_CD
, actn.FRC_ACTN
, actn.PRIORITY_NBR
from krim_rsp_t rsp
left join krim_rsp_tmpl_t rspt
on rsp.rsp_tmpl_id = rspt.rsp_tmpl_id
left outer join krim_role_rsp_t rr
on rr.rsp_id = rsp.rsp_id
left outer join KRIM_ROLE_MBR_T rm
ON rm.ROLE_ID = rr.ROLE_ID
left outer join KRIM_ROLE_RSP_ACTN_T actn
ON actn.ROLE_RSP_ID = rr.ROLE_RSP_ID
AND (actn.ROLE_MBR_ID = rm.ROLE_MBR_ID OR actn.ROLE_MBR_ID = '*')
left outer join krim_role_t r
on rr.role_id = r.role_id
order by rsp_nmspc_cd
, rsp_id
, role_id
, role_mbr_id
/UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='notsys' WHERE PRNCPL_NM='NotSys'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='notsysadm' WHERE PRNCPL_NM='NotSysAdm'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testadmin1' WHERE PRNCPL_NM='TestAdmin1'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testadmin2' WHERE PRNCPL_NM='TestAdmin2'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser1' WHERE PRNCPL_NM='TestUser1'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser2' WHERE PRNCPL_NM='TestUser2'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser3' WHERE PRNCPL_NM='TestUser3'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser4' WHERE PRNCPL_NM='TestUser4'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser5' WHERE PRNCPL_NM='TestUser5'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser6' WHERE PRNCPL_NM='TestUser6'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='newacctusr' WHERE PRNCPL_NM='newAcctUsr'
/

UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='notsys' WHERE PRNCPL_ID='NotSys'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='notsysadm' WHERE PRNCPL_ID='NotSysAdm'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testadmin1' WHERE PRNCPL_ID='TestAdmin1'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testadmin2' WHERE PRNCPL_ID='TestAdmin2'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser1' WHERE PRNCPL_ID='TestUser1'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser2' WHERE PRNCPL_ID='TestUser2'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser3' WHERE PRNCPL_ID='TestUser3'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser4' WHERE PRNCPL_ID='TestUser4'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser5' WHERE PRNCPL_ID='TestUser5'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser6' WHERE PRNCPL_ID='TestUser6'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='newaccountuser' WHERE PRNCPL_ID='newAccountUser'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='kuluser' WHERE PRNCPL_ID='KULUSER'
/

CREATE SEQUENCE TRV_FO_ID_S INCREMENT BY 1 START WITH 1000
/

DELETE FROM KRIM_ENTITY_EMAIL_T where EMAIL_ADDR='KULUSER@indiana.edu'
/

CREATE TABLE KR_KIM_TEST_BO (
    pk VARCHAR2(40),
    prncpl_id VARCHAR2(40)
)
/INSERT INTO KRIM_PERM_TMPL_T(PERM_TMPL_ID, OBJ_ID, VER_NBR, NM, DESC_TXT, KIM_TYP_ID, ACTV_IND, NMSPC_CD) values ('49',	'662384B381B867A1E0404F8189D868A6','1','Send Ad Hoc Request','','5','Y','KR-NS')
/


INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) values ('332','662384B381B967A1E0404F8189D868A6',1,'49','Send Ad Hoc Request','','Y','KR-SYS')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) values ('333','662384B381BD67A1E0404F8189D868A6',1,'49','Send Ad Hoc Request','','Y','KR-SYS')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) values ('334','662384B381C167A1E0404F8189D868A6',1,'49','Send Ad Hoc Request','','Y','KR-SYS')
/


INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values ('618','662384B381BC67A1E0404F8189D868A6',1,'83','332','Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values ('616','662384B381C067A1E0404F8189D868A6',1,'83','333','Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values ('617','662384B381C467A1E0404F8189D868A6',1,'66','334','Y')
/


INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('478','662384B381BA67A1E0404F8189D868A6','1','332','5','13','KualiDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('479','662384B381BB67A1E0404F8189D868A6','1','332','5','14','F')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('480','662384B381BE67A1E0404F8189D868A6','1','333','5','13','KualiDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('481','662384B381BF67A1E0404F8189D868A6','1','333','5','14','K')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('482','662384B381C267A1E0404F8189D868A6','1','334','5','13','KualiDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('483','662384B381C367A1E0404F8189D868A6','1','334','5','14','A')
/


UPDATE KRIM_GRP_MBR_T SET MBR_ID='notsys' WHERE MBR_ID='NotSys' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='notsysadm' WHERE MBR_ID='NotSysAdm' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testadmin1' WHERE MBR_ID='TestAdmin1' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testadmin2' WHERE MBR_ID='TestAdmin2' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser1' WHERE MBR_ID='TestUser1' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser2' WHERE MBR_ID='TestUser2' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser3' WHERE MBR_ID='TestUser3' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser4' WHERE MBR_ID='TestUser4' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser5' WHERE MBR_ID='TestUser5' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser6' WHERE MBR_ID='TestUser6' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='newaccountuser' WHERE MBR_ID='newAccountUser' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='kuluser' WHERE MBR_ID='KULUSER' AND MBR_TYP_CD = 'P'
/INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
'5a5fbe94-846f-102c-8db0-c405cae621f3',
'A semi-colon delimted list of regular expressions that identify
potentially sensitive data in strings.  These patterns will be matched
against notes, document explanations, and routing annotations.',
'All',
'SENSITIVE_DATA_PATTERNS',
'CONFG',
'[0-9]{9};[0-9]{3}-[0-9]{2}-[0-9]{4}',
1
)
/

CREATE SEQUENCE KRNS_MAINT_LOCK_S START WITH 2000 INCREMENT BY 1
/
ALTER TABLE KRNS_MAINT_LOCK_T ADD MAINT_LOCK_ID VARCHAR2(14)
/
ALTER TABLE KRNS_MAINT_LOCK_T DROP PRIMARY KEY
/

DECLARE
CURSOR cursor1 IS
	SELECT MAINT_LOCK_REP_TXT FROM KRNS_MAINT_LOCK_T;
BEGIN
	FOR r IN cursor1 LOOP
        execute immediate 'UPDATE KRNS_MAINT_LOCK_T SET MAINT_LOCK_ID=KRNS_MAINT_LOCK_S.nextval';
    END LOOP;
END;
/

ALTER TABLE KRNS_MAINT_LOCK_T ADD PRIMARY KEY (MAINT_LOCK_ID)
/

ALTER TABLE KRNS_MAINT_LOCK_T MODIFY MAINT_LOCK_REP_TXT VARCHAR2(500)
/

alter table krns_sesn_doc_t
ADD content_encrypted_ind CHAR(1) DEFAULT 'N'
/update krew_doc_typ_t set parnt_id = '2681' where doc_typ_nm = 'NamespaceMaintenanceDocument'
/
update krew_doc_typ_t set parnt_id = '2681' where doc_typ_nm = 'ParameterDetailTypeMaintenanceDocument'
/
update krew_doc_typ_t set parnt_id = '2681' where doc_typ_nm = 'ParameterMaintenanceDocument'
/
update krew_doc_typ_t set parnt_id = '2681' where doc_typ_nm = 'ParameterTypeMaintenanceDocument'
/
update KRIM_PERM_ATTR_DATA_T Set KIM_ATTR_DEFN_ID='4' where PERM_ID = '163' AND ATTR_VAL = 'KR*'
/INSERT INTO krns_parm_t(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD)
  VALUES('KR-NS', 'All', 'DEFAULT_COUNTRY', '64B87B4C5E3B8F4CE0404F8189D8291A', 1, 'CONFG', 'US', 'Used as the default country code when relating records that do not have a country code to records that do have a country code, e.g. validating a zip code where the country is not collected.', 'A')
/

UPDATE krim_typ_t
    SET srvc_nm = 'rolePermissionTypeService'
WHERE nm = 'Role'
/

UPDATE krim_typ_t
    SET srvc_nm = 'groupPermissionTypeService'
WHERE nm = 'Group'
/
UPDATE krim_typ_t
    SET nm = 'Edit Mode & Document Type'
      , srvc_nm = 'documentTypeAndEditModePermissionTypeService'
      WHERE nm = 'Edit Mode'
/
UPDATE krew_doc_typ_t
    SET POST_PRCSR = 'org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor'
    WHERE DOC_TYP_NM = 'KualiDocument'
/
UPDATE krew_doc_typ_t
    SET POST_PRCSR = NULL
    WHERE DOC_TYP_NM = 'IdentityManagementPersonDocument'
/
COMMIT
/
INSERT INTO krns_parm_t ( NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, TXT, PARM_TYP_CD, PARM_DESC_TXT, CONS_CD, OBJ_ID )
    VALUES (
          'KR-IDM'
        , 'Document'
        , 'MAX_MEMBERS_PER_PAGE'
        , '20'
        , 'CONFG'
        , 'The maximum number of role or group members to display at once on their documents. If the number is above this value, the document will switch into a paging mode with only this many rows displayed at a time.'
        , 'A'
        , '2238b58e-8fb9-102c-9461-def224dad9b3'
      )
/
COMMIT
/
INSERT INTO krns_parm_dtl_typ_t
    ( SELECT 'KR-IDM', parm_dtl_typ_cd, SYS_GUID(), 1, nm, 'Y'
        FROM krns_parm_dtl_typ_t
        WHERE NMSPC_CD = 'KR-NS'
    )
/
COMMIT
/ TRUNCATE TABLE KRNS_SESN_DOC_T
/

DROP TABLE KRNS_SESN_DOC_T
/

CREATE TABLE KRNS_SESN_DOC_T
(SESN_DOC_ID                   VARCHAR2(40) NOT NULL,
DOC_HDR_ID                     VARCHAR2(14) NOT NULL,
PRNCPL_ID                             VARCHAR2(40) NOT NULL,
IP_ADDR                                 VARCHAR2(60) NOT NULL,
SERIALZD_DOC_FRM               BLOB,
LAST_UPDT_DT                   DATE,
CONTENT_ENCRYPTED_IND          CHAR(1) DEFAULT 'N')
/

CREATE INDEX KRNS_SESN_DOC_TI1 ON KRNS_SESN_DOC_T
(
LAST_UPDT_DT                    ASC
)
/

ALTER TABLE KRNS_SESN_DOC_T
ADD CONSTRAINT KRNS_SESN_DOC_TP1
PRIMARY KEY (SESN_DOC_ID, DOC_HDR_ID, PRNCPL_ID, IP_ADDR)
/ALTER TABLE KRIM_ROLE_RSP_ACTN_T
DROP CONSTRAINT KRIM_ROLE_RSP_ACTN_TR2
/
ALTER TABLE KRIM_PND_DLGN_MBR_T
ADD ROLE_MBR_ID VARCHAR(40)
/
ALTER TABLE KRIM_DLGN_MBR_T DROP CONSTRAINT KRIM_DLGN_MBR_TR1
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('683', sys_guid(), 1, '59', '334', 'Y')
/

-- this row doesn't do anything, but it is possible that it could cause a potential problem
delete from krim_perm_attr_data_t where perm_id = '163' AND kim_typ_id = '16' AND kim_attr_defn_id = '4' AND attr_val = 'KR-SYS'
/
-- next 3 statements create a Kim Group (Insert member) permission for groups with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('833', '5B4F09744953EF33E0404F8189D84F25', 1, '38', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('203', '5B4F09744A39EF33E0404F8189D84F25', 1, '833', '21', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('838', '5C27A267EF6D7417E0404F8189D830AA', 1, '63', '833', 'Y')
/

-- next 3 statements create a Kim Roles permission for roles with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('834', '5B4F09744953EF33E0404F8189D84F26', 1, '35', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('204', '5B4F09744A39EF33E0404F8189D84F26', 1, '834', '18', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('839', '5C27A267EF6D7417E0404F8189D830AB', 1, '63', '834', 'Y')
/

-- next 3 statements create a Kim Permission permission for permissions with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('835', '5B4F09744953EF33E0404F8189D84F27', 1, '36', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('205', '5B4F09744A39EF33E0404F8189D84F27', 1, '835', '19', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('840', '5C27A267EF6D7417E0404F8189D830AC', 1, '63', '835', 'Y')
/

-- next 3 statements create a Kim Responsibility permission for responsibilities with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('836', '5B4F09744953EF33E0404F8189D84F28', 1, '37', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('206', '5B4F09744A39EF33E0404F8189D84F28', 1, '836', '20', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('841', '5C27A267EF6D7417E0404F8189D830AD', 1, '63', '835', 'Y')
/
ALTER TABLE KRIM_DLGN_MBR_T DROP CONSTRAINT KRIM_DLGN_MBR_TR2
/
commit
/
ALTER TABLE KRIM_DLGN_MBR_T
    ADD CONSTRAINT KRIM_DLGN_MBR_TR2 FOREIGN KEY (DLGN_ID)
    REFERENCES KRIM_DLGN_T (DLGN_ID)
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2708,2681,'CampusMaintenanceDocument',0,1,1,'CampusMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'616D94CA-D08D-D036-E77D-4B53DB34CD95');
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2709,2681,'CampusTypeMaintenanceDocument',0,1,1,'CampusTypeMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'DE0B8588-E459-C07A-87B8-6ACD693AE70C');
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2710,2681,'CountryMaintenanceDocument',0,1,1,'CountryMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'82EDB593-97BA-428E-C6E7-A7F3031CFAEB');
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2711,2681,'CountyMaintenanceDocument',0,1,1,'CountyMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'C972E260-5552-BB63-72E6-A514301B0326');
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2712,2681,'PostalCodeMaintenanceDocument',0,1,1,'PostalCodeMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'B79D1104-BC48-1597-AFBE-773EED31A110');
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2713,2681,'StateMaintenanceDocument',0,1,1,'StateMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'EF2378F6-E770-D7BF-B7F1-C18881E3AFF0');
/ALTER TABLE KRIM_PERSON_DOCUMENT_T
MODIFY TAX_ID VARCHAR2(100)
/INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
VALUES ('378', '5B4F09744953EF33E0404F8189D84F29', '1', '1', 'KR-IDM', 'Override Entity Privacy Preferences', 'Allows a user to override entity privacy preferences', 'Y')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('230', '5C27A267EF5C7417E0404F8189D830AA', 1, '63', '378', 'Y')
/ -- Fixes an issue with our sample eDoc Lite document

UPDATE KREW_DOC_TYP_T SET SVC_NMSPC=NULL WHERE SVC_NMSPC='FooBar'
/DROP TABLE KRIM_PERM_RQRD_ATTR_T
/
DROP TABLE KRIM_RSP_RQRD_ATTR_T
/

-- KULRICE-3287

DELETE FROM KRNS_PARM_T where NMSPC_CD='KR-WKFLW' AND PARM_DTL_TYP_CD='All' AND PARM_NM='APPLICATION_CONTEXT'
/

-- KULRICE-3278

DECLARE

    ref_perm_id VARCHAR2(40);

BEGIN

    SELECT perm_id INTO ref_perm_id FROM krim_perm_attr_data_t WHERE attr_val = 'org.kuali.rice.kew.web.backdoor.AdministrationAction';
    DELETE FROM krim_perm_attr_data_t WHERE perm_id = ref_perm_id;
    DELETE FROM krim_role_perm_t WHERE perm_id = ref_perm_id;
    DELETE FROM krim_perm_t WHERE perm_id = ref_perm_id;

END;
/ -- KULRICE-3126 This will turn off field level help by default.
UPDATE KRNS_PARM_T SET TXT = 'N' WHERE NMSPC_CD = 'KR-NS' AND PARM_DTL_TYP_CD = 'All' AND PARM_NM = 'ENABLE_FIELD_LEVEL_HELP_IND'
/
-- KULRICE-3349: Add the doc handler URL to the Campus, Campus Type, Country, County, Postal Code, and State document types.
UPDATE KREW_DOC_TYP_T SET DOC_HDLR_URL='${kr.url}/maintenance.do?methodToCall=docHandler' WHERE DOC_TYP_NM='CampusMaintenanceDocument' OR DOC_TYP_NM='CampusTypeMaintenanceDocument' OR DOC_TYP_NM='CountryMaintenanceDocument' OR DOC_TYP_NM='CountyMaintenanceDocument' OR DOC_TYP_NM='PostalCodeMaintenanceDocument' OR DOC_TYP_NM='StateMaintenanceDocument'
/
--KULRICE-3283
ALTER TABLE KRNS_PARM_T ADD APPL_NMSPC_CD  varchar2(20) default 'KUALI' not null
/
ALTER TABLE KRNS_PARM_T DROP CONSTRAINT KRNS_PARM_TP1
/
ALTER TABLE KRNS_PARM_T ADD CONSTRAINT KRNS_PARM_TP1 PRIMARY KEY(NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM, APPL_NMSPC_CD)
/-- KULRICE-3212
ALTER TABLE KREW_DOC_HDR_T MODIFY (APP_DOC_ID VARCHAR2(255))
/

-- KULRICE-3015 - Standardize length of document type name and lbl columns
ALTER TABLE KREW_ACTN_ITM_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_DOC_TYP_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_RULE_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_EDL_ASSCTN_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_EDL_DMP_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_DOC_TYP_T MODIFY (LBL VARCHAR2(128))
/
ALTER TABLE KREW_ACTN_ITM_T MODIFY (DOC_TYP_LBL VARCHAR2(128))
/
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY (DOC_TYP_LBL VARCHAR2(128))
/


alter table KREN_MSG_DELIV_T modify (LOCKD_DTTM date)
/
alter table KREN_MSG_T modify (CRTE_DTTM date)
/
alter table KREN_NTFCTN_MSG_DELIV_T modify (LOCKD_DTTM date)
/
alter table KREN_NTFCTN_T modify (CRTE_DTTM date,
SND_DTTM date,
AUTO_RMV_DTTM date,
LOCKD_DTTM date)
/

create table KRIM_PRSN_CACHE_T_bak as select * from KRIM_PRSN_CACHE_T
/

drop table KRIM_PRSN_CACHE_T cascade constraints purge
/

CREATE TABLE krim_prsn_cache_t
    (prncpl_id VARCHAR2(40) NOT NULL,
    prncpl_nm VARCHAR2(40),
    entity_id VARCHAR2(40),
    entity_typ_cd VARCHAR2(40),
    first_nm VARCHAR2(40),
    middle_nm VARCHAR2(40),
    last_nm VARCHAR2(40),
    prsn_nm VARCHAR2(40),
    campus_cd VARCHAR2(40),
    prmry_dept_cd VARCHAR2(40),
    emp_id VARCHAR2(40),
    last_updt_ts DATE,
    obj_id VARCHAR2(36) NOT NULL)
/





-- Constraints for KRIM_PRSN_CACHE_T

ALTER TABLE krim_prsn_cache_t
ADD CONSTRAINT krim_prsn_cache_tp1 PRIMARY KEY (prncpl_id)
/


-- End of DDL Script for Table KULDEV.KRIM_PRSN_CACHE_T

insert into KRIM_PRSN_CACHE_T select * from KRIM_PRSN_CACHE_T_bak
/
commit
/

drop table KRIM_PRSN_CACHE_T_bak
/

-- create Campus KIM type

INSERT INTO KRIM_TYP_T (KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES('17', '5ADF18B6D4F77954E0404F8189D85002', 1, 'Campus', 'campusRoleService', 'Y', 'KR-NS')
/
commit
/

-- Enhancement from uu to person doc that we did not get around to implementingÉ

delete from krim_role_perm_t where perm_id in  ('153', '154')
/
delete from krim_perm_attr_data_t where target_primary_key in  ('153', '154')
/
delete from krim_perm_t where perm_tmpl_id = '39'
/
delete from krim_perm_tmpl_t where perm_tmpl_id = '39'
/
delete from krim_typ_attr_t where kim_typ_id = '22'
/
delete from krim_attr_defn_t where kim_attr_defn_id = '17'
/
delete from krim_typ_t where kim_typ_id = '22'
/
commit
/

alter table KREN_CHNL_T DROP COLUMN OBJ_ID
/
alter table KREN_CNTNT_TYP_T DROP COLUMN OBJ_ID
/
alter table KREN_PRODCR_T DROP COLUMN OBJ_ID
/
alter table KREN_PRIO_T DROP COLUMN OBJ_ID
/
alter table KREN_RVWER_T DROP COLUMN OBJ_ID
/
-- need to remember to go back to "action-list-helpdesk-permissions.sql" and "doc-search-permissions.sql"
-- and reconcile related changes with these when we build the final upgrade scripts
-- (essentially, those old scripts can be removed)
--
-- UPDATE - 3/21/2009 - i've removed these scripts
-- so the deletions below won't need to be included in the finla upgrade script!!!

-- first of all, delete the original setup

DELETE FROM KRIM_ROLE_PERM_T WHERE PERM_ID IN (
	SELECT PERM_ID FROM KRIM_PERM_T WHERE PERM_TMPL_ID IN (
		SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='View Other Action List'
	)
)
/
DELETE FROM KRIM_ROLE_PERM_T WHERE PERM_ID IN (
	SELECT PERM_ID FROM KRIM_PERM_T WHERE PERM_TMPL_ID IN (
		SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='Unrestricted Document Search'
	)
)
/
DELETE FROM KRIM_PERM_T where PERM_TMPL_ID IN (SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='View Other Action List')
/
DELETE FROM KRIM_PERM_T where PERM_TMPL_ID IN (SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM='Unrestricted Document Search')
/
DELETE FROM KRIM_PERM_TMPL_T where NM='View Other Action List'
/
DELETE FROM KRIM_PERM_TMPL_T where NM='Unrestricted Document Search'
/
commit
/

-- now, create as default permissions

INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('298', sys_guid(), 1, '1', 'View Other Action List', null, 'Y', 'KR-WKFLW')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('564', sys_guid(), 1, '63', '298', 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD)
    VALUES('299', sys_guid(), 1, '1', 'Unrestricted Document Search', null, 'Y', 'KR-WKFLW')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
    VALUES('566', sys_guid(), 1, '63', '299', 'Y')
/

commit
/
-- Update kim group table so kim type is set to the default value instead of null
-- Alter tables so null kim_type_ids are not allowed
UPDATE KRIM_GRP_T SET KIM_TYP_ID = '1' WHERE KIM_TYP_ID IS NULL
/
ALTER TABLE KRIM_DLGN_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_DLGN_MBR_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_GRP_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_GRP_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_PERM_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_PERM_TMPL_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_ROLE_MBR_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_ROLE_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_RSP_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_RSP_TMPL_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_TYP_ATTR_T MODIFY KIM_TYP_ID NOT NULL
/

-- Add a new column to the namespace table

alter table KRNS_NMSPC_T add (APPL_NMSPC_CD VARCHAR2(20))
/

-- Add some missing namespace entries to the table

INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KR-NTFCN', '5B960CFDBB360FDFE0404F8189D83CBD', 1, 'Notification', 'Y', NULL)
/
INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KUALI', '5ADF18B6D4817954E0404F8189D85002', 1, 'Kuali Systems', 'Y', NULL)
/
INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KR-BUS', '5B960CFDBB370FDFE0404F8189D83CBD', 1, 'Service Bus', 'Y', NULL)
/
INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KR-SYS', '5B960CFDBB390FDFE0404F8189D83CBD', 1, 'Enterprise Infrastructure', 'Y', NULL)
/

-- KULRICE-2625 - make KREW_DOC_TYP_T.RTE_VER_NBR nullable

alter table KREW_DOC_TYP_T modify RTE_VER_NBR NULL
/

-- KFSMI-2892 - missing ver_nbr defaults

alter table KRIM_DLGN_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_DLGN_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_GRP_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_ROLE_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
-- the float doc header extensions table really needs to be of type NUMBER which has a precision of 38
-- this results in proper conversion to mysql as part of the impex process
ALTER TABLE KREW_DOC_HDR_EXT_FLT_T MODIFY VAL NUMBER
/-- change namespace to KUALI on all groups with namespace of KFS from original EN_WRKGRP_T to KRIM_GRP_T conversion

UPDATE KRIM_GRP_T set NMSPC_CD='KUALI' where NMSPC_CD='KFS'
/

alter table KRNS_ATT_T modify MIME_TYP VARCHAR2(150)
/

DROP TABLE KRNS_DOC_TYP_ATTR_T
/-- KULRICE-2870 - generate names for all existing rules that don't have one
-- if I was better at PL/SQL i could probably figure out a better way to do
-- this, but it will basically set up and generate rule names for rules with
-- up to 1000 different versions, if you have rules with more versions than
-- that, you can update the loop condition

DECLARE
CURSOR cursor1 IS
	SELECT RULE_ID FROM KREW_RULE_T WHERE NM IS NULL AND CUR_IND=1;
BEGIN
	FOR r IN cursor1 LOOP
        execute immediate 'UPDATE KREW_RULE_T SET NM=SYS_GUID() WHERE RULE_ID='||r.RULE_ID;
    END LOOP;
END;
/

DECLARE
CURSOR cursor1 IS
	SELECT PREV.RULE_ID, RULE.NM FROM KREW_RULE_T PREV, KREW_RULE_T RULE
    WHERE PREV.RULE_ID=RULE.PREV_RULE_VER_NBR AND RULE.NM IS NOT NULL;
cnt NUMBER := 0;
BEGIN
    LOOP
        FOR r IN cursor1 LOOP
            UPDATE KREW_RULE_T SET NM=r.NM WHERE RULE_ID=r.RULE_ID;
        END LOOP;
        cnt := cnt + 1;
        IF cnt > 1000 THEN EXIT; END IF;
    END LOOP;
END;
/

UPDATE KREW_RULE_T RULE
SET (NM) =
(
  SELECT PREV.NM
  FROM KREW_RULE_T PREV
  WHERE PREV.RULE_ID=RULE.PREV_RULE_VER_NBR
  AND PREV.NM IS NOT NULL
)
WHERE RULE.NM IS NULL

ALTER TABLE KREW_RULE_T MODIFY NM NOT NULL
/alter table krns_att_t modify att_typ_cd VARCHAR2(40)
/

UPDATE KRIM_ATTR_DEFN_T set APPL_URL='${application.url}'
/DROP TABLE KREW_APP_CNST_T
/
DROP TABLE KRNS_DOC_TYP_T
/DROP TABLE krim_role_rel_t
/
DROP SEQUENCE KRIM_ROLE_REL_ID_S
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A semi-colon delimted list of strings representing date formats that the DateTimeService will use to parse dates when DateTimeServiceImpl.convertToSqlDate(String) or DateTimeServiceImpl.convertToDate(String) is called. Note that patterns will be applied in the order listed (and the first applicable one will be used). For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'STRING_TO_DATE_FORMATS',
'CONFG',
'MM/dd/yy;MM-dd-yy;MMMM dd, yyyy;MMddyy',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format dates to be used in a file name when DateTimeServiceImpl.toDateStringForFilename(Date) is called. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'DATE_TO_STRING_FORMAT_FOR_FILE_NAME',
'CONFG',
'yyyyMMdd',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date and time string to be used in a file name when DateTimeServiceImpl.toDateTimeStringForFilename(Date) is called.. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'TIMESTAMP_TO_STRING_FORMAT_FOR_FILE_NAME',
'CONFG',
'yyyyMMdd-HH-mm-ss-S',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date to be displayed on a web page. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'DATE_TO_STRING_FORMAT_FOR_USER_INTERFACE',
'CONFG',
'MM/dd/yyyy',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A single date format string that the DateTimeService will use to format a date and time to be displayed on a web page. For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'TIMESTAMP_TO_STRING_FORMAT_FOR_USER_INTERFACE',
'CONFG',
'MM/dd/yyyy hh:mm a',
1
)
/

INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
SYS_GUID(),
'A semi-colon delimted list of strings representing date formats that the DateTimeService will use to parse date and times when DateTimeServiceImpl.convertToDateTime(String) or DateTimeServiceImpl.convertToSqlTimestamp(String) is called. Note that patterns will be applied in the order listed (and the first applicable one will be used). For a more technical description of how characters in the parameter value will be interpreted, please consult the javadocs for java.text.SimpleDateFormat. Any changes will be applied when the application is restarted.',
'All',
'STRING_TO_TIMESTAMP_FORMATS',
'CONFG',
'MM/dd/yyyy hh:mm a',
1
)
/UPDATE KREW_DOC_TYP_T
SET DOC_TYP_NM = 'NamespaceMaintenanceDocument',
    LBL = 'Namespace'
WHERE DOC_TYP_NM = 'ParameterNamespaceMaintenanceDocument'update KRIM_ENTITY_NM_T set NM_TYP_CD = 'PRFR' where NM_TYP_CD = 'PFRD'
/
update KRIM_ENTITY_EMAIL_T set EMAIL_TYP_CD = 'WRK' where EMAIL_TYP_CD = 'CAMPUS'
/
alter table KRIM_PND_DLGN_MT rename to KRIM_PND_DLGN_T
/
alter table KRIM_PND_ROLE_RSP_ACTN_MT add IGNORE_PREV_IND varchar2(1) NULL;
/
CREATE TABLE KRIM_PND_ROLE_PERM_T (
    FDOC_NBR        VARCHAR2(14) 	NOT NULL,
    ROLE_PERM_ID	VARCHAR2(40) 	NOT NULL,
    OBJ_ID      	VARCHAR2(36) 	DEFAULT SYS_GUID() NOT NULL,
    VER_NBR     	NUMBER(8,0)  	DEFAULT 1 NOT NULL,
    ROLE_ID    		VARCHAR2(40) 	NOT NULL,
    PERM_ID    		VARCHAR2(40) 	NOT NULL,
    ACTV_IND    	VARCHAR2(1)  	DEFAULT 'Y' NULL,
    PRIMARY KEY(ROLE_PERM_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_ROLE_RSP_T (
    FDOC_NBR        VARCHAR2(14) 	NOT NULL,
    ROLE_RSP_ID		VARCHAR2(40) 	NOT NULL,
    OBJ_ID      	VARCHAR2(36) 	DEFAULT SYS_GUID() NOT NULL,
    VER_NBR     	NUMBER(8,0)  	DEFAULT 1 NOT NULL,
    ROLE_ID    		VARCHAR2(40) 	NOT NULL,
    RSP_ID    		VARCHAR2(40) 	NOT NULL,
    ACTV_IND    	VARCHAR2(1)  	DEFAULT 'Y' NULL,
    PRIMARY KEY(ROLE_RSP_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_DLGN_MBR_T
(
    FDOC_NBR        VARCHAR2(14) NOT NULL,
    DLGN_MBR_ID  	VARCHAR2(40) NOT NULL,
    OBJ_ID      	VARCHAR2(36) NOT NULL,
    VER_NBR     	NUMBER(8,0)  DEFAULT 1 NOT NULL,
    DLGN_ID      	VARCHAR2(40) NOT NULL,
    MBR_ID   		VARCHAR2(40),
    MBR_NM			VARCHAR2(40),
    MBR_TYP_CD		VARCHAR2(40) NOT NULL,
    ACTV_IND    	VARCHAR2(1)  DEFAULT 'Y' NULL,
    ACTV_FRM_DT  	DATE NULL,
    ACTV_TO_DT  	DATE NULL,
    PRIMARY KEY(DLGN_MBR_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_DLGN_MBR_ATTR_DATA_T (
    FDOC_NBR          VARCHAR2(14)  NOT NULL,
    ATTR_DATA_ID      	VARCHAR2(40) NOT NULL,
    OBJ_ID            	VARCHAR2(36) NOT NULL,
    VER_NBR           	NUMBER(8,0) DEFAULT 1 NOT NULL,
    TARGET_PRIMARY_KEY	VARCHAR2(40) NULL,
    KIM_TYP_ID        	VARCHAR2(40) NULL,
    KIM_ATTR_DEFN_ID  	VARCHAR2(40) NULL,
    ATTR_VAL          	VARCHAR2(400) NULL,
    ACTV_IND    	VARCHAR2(1) DEFAULT 'Y' NULL,
    EDIT_FLAG    	VARCHAR2(1) DEFAULT 'N' NULL,
    PRIMARY KEY(ATTR_DATA_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_GRP_DOCUMENT_T
(
    FDOC_NBR          VARCHAR2(14) 		NOT NULL,
    GRP_ID            VARCHAR2(40) 		NOT NULL,
    OBJ_ID            VARCHAR2(36) 		DEFAULT SYS_GUID() NOT NULL,
    VER_NBR           NUMBER(8,0)  		DEFAULT 1 NOT NULL,
    KIM_TYP_ID 	  	  VARCHAR2(40) 	 	NOT NULL,
    GRP_NMSPC         VARCHAR2(100)  	NOT NULL,
    GRP_NM       	  VARCHAR2(400),
    GRP_DESC       	  VARCHAR2(400),
    ACTV_IND          VARCHAR2(1) 		DEFAULT 'Y',
    CONSTRAINT krim_grp_document_tp1 PRIMARY KEY ( FDOC_NBR )
)
/
CREATE TABLE KRIM_PND_GRP_MBR_T (
    FDOC_NBR        VARCHAR2(14) NOT NULL,
    GRP_MBR_ID  	VARCHAR2(40) NOT NULL,
    OBJ_ID      	VARCHAR2(36) NOT NULL,
    VER_NBR     	NUMBER(8,0)  DEFAULT 1 NOT NULL,
    GRP_ID      	VARCHAR2(40) NOT NULL,
    MBR_ID   		VARCHAR2(40),
    MBR_NM			VARCHAR2(40),
    MBR_TYP_CD		VARCHAR2(40) NOT NULL,
    ACTV_FRM_DT  	DATE NULL,
    ACTV_TO_DT  	DATE NULL,
    PRIMARY KEY(GRP_MBR_ID,FDOC_NBR)
)
/
CREATE TABLE KRIM_PND_GRP_ATTR_DATA_T (
    FDOC_NBR          	VARCHAR2(14)  NOT NULL,
    ATTR_DATA_ID      	VARCHAR2(40) NOT NULL,
    OBJ_ID            	VARCHAR2(36) NOT NULL,
    VER_NBR           	NUMBER(8,0) DEFAULT 1 NOT NULL,
    TARGET_PRIMARY_KEY	VARCHAR2(40) NULL,
    KIM_TYP_ID        	VARCHAR2(40) NULL,
    KIM_ATTR_DEFN_ID  	VARCHAR2(40) NULL,
    ATTR_VAL          	VARCHAR2(400) NULL,
    PRIMARY KEY(ATTR_DATA_ID,FDOC_NBR)
)
/
alter table KRIM_DLGN_MBR_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to DLGN_MBR_ID
/
alter table KRIM_GRP_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to GRP_ID
/
alter table KRIM_PERM_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to PERM_ID
/
alter table KRIM_ROLE_MBR_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to ROLE_MBR_ID
/
alter table KRIM_PND_ROLE_MBR_ATTR_DATA_MT rename column TARGET_PRIMARY_KEY to ROLE_MBR_ID
/
alter table KRIM_RSP_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to RSP_ID
/
alter table KRIM_PND_DLGN_MBR_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to DLGN_MBR_ID
/
alter table KRIM_PND_GRP_ATTR_DATA_T rename column TARGET_PRIMARY_KEY to GRP_ID
/DROP VIEW KRIM_GRP_MBR_V
/
DROP VIEW KRIM_GRP_V
/
DROP VIEW KRIM_PERM_ATTR_V
/
DROP VIEW KRIM_PERM_V
/
DROP VIEW KRIM_PRNCPL_V
/
DROP VIEW KRIM_ROLE_GRP_V
/
DROP VIEW KRIM_ROLE_PERM_V
/
DROP VIEW KRIM_ROLE_PRNCPL_V
/
DROP VIEW KRIM_ROLE_ROLE_V
/
DROP VIEW KRIM_ROLE_V
/
DROP VIEW KRIM_RSP_ROLE_ACTN_V
/
DROP VIEW KRIM_RSP_ROLE_V
/
    ALTER TABLE KREW_DOC_HDR_EXT_FLT_T MODIFY VAL NUMBER(30, 15)
/-- KULRICE-2981 - drop unused tables

DROP TABLE KR_QRTZ_PAUSED_TRIGGERS_GRPS
/

-- KULRICE-2982

ALTER TABLE krns_parm_t DROP COLUMN grp_nm
/
ALTER TABLE krns_parm_t DROP COLUMN actv_ind
/
ALTER TABLE krns_parm_t MODIFY parm_nm VARCHAR2(255)
/
ALTER TABLE krew_rule_t MODIFY nm NULL
/
ALTER TABLE krns_parm_t DROP CONSTRAINT KRNS_PARM_TP1
/
ALTER TABLE krns_parm_t ADD CONSTRAINT KRNS_PARM_TP1 PRIMARY KEY(NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM)
/
ALTER TABLE KRIM_ROLE_DOCUMENT_T DROP COLUMN role_typ_name
/
ALTER TABLE KRIM_ATTR_DEFN_T DROP COLUMN srvc_nm
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN rsp_id
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN role_id
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN grp_id
/
ALTER TABLE krim_role_rsp_actn_t DROP COLUMN prncpl_id
/
ALTER TABLE krim_role_rsp_actn_t MODIFY role_rsp_id NULL
/
DROP TABLE KRIM_GRP_GRP_T
/
DROP TABLE KRIM_GRP_PRNCPL_T
/
DROP TABLE KRIM_DLGN_PRNCPL_T
/
DROP TABLE KRIM_DLGN_GRP_T
/
DROP TABLE KRIM_DLGN_ROLE_T
/
DROP TABLE KRIM_ROLE_PRNCPL_T
/
DROP TABLE KRIM_ROLE_GRP_T
/
DROP TABLE KRIM_ROLE_ROLE_T
/
update krns_parm_t set   parm_nm = 'EMAIL_NOTIFICATION_TEST_ADDRESS' where parm_nm = 'EMAIL_NOTIFICATION_TEST_ADDRESS '
/
CREATE OR REPLACE VIEW KRIM_GRP_MBR_V ( NMSPC_CD, GRP_NM, GRP_ID, PRNCPL_NM, PRNCPL_ID, MBR_GRP_NM, MBR_GRP_ID )
AS
SELECT g.NMSPC_CD
, g.grp_nm
, g.GRP_ID
, p.PRNCPL_NM
, p.PRNCPL_ID
, mg.GRP_NM AS mbr_grp_nm
, mg.GRP_ID AS mbr_grp_id
FROM KRIM_GRP_MBR_T gm
LEFT JOIN krim_grp_t g
ON g.GRP_ID = gm.GRP_ID
LEFT OUTER JOIN krim_grp_t mg
ON mg.GRP_ID = gm.MBR_ID
AND gm.MBR_TYP_CD = 'G'
LEFT OUTER JOIN krim_prncpl_t p
ON p.PRNCPL_ID = gm.MBR_ID
AND gm.MBR_TYP_CD = 'P'
LEFT OUTER JOIN krim_entity_nm_t en
ON en.ENTITY_ID = p.ENTITY_ID
AND en.DFLT_IND = 'Y'
AND en.ACTV_IND = 'Y'
ORDER BY nmspc_cd, grp_nm, prncpl_nm
/
CREATE OR REPLACE VIEW KRIM_GRP_V ( NMSPC_CD, GRP_NM, GRP_ID, GRP_TYP_NM, ATTR_NM, ATTR_VAL )
AS
SELECT g.NMSPC_CD
, g.grp_nm
, g.GRP_ID
, t.NM AS grp_typ_nm
, a.NM AS attr_nm
, d.ATTR_VAL AS attr_val
FROM krim_grp_t g
LEFT OUTER JOIN KRIM_GRP_ATTR_DATA_T d
ON d.grp_id = g.GRP_ID
LEFT OUTER JOIN KRIM_ATTR_DEFN_T a
ON a.KIM_ATTR_DEFN_ID = d.KIM_ATTR_DEFN_ID
LEFT OUTER JOIN KRIM_TYP_T t
ON g.KIM_TYP_ID = t.KIM_TYP_ID
/
CREATE OR REPLACE VIEW KRIM_PERM_ATTR_V ( TMPL_NMSPC_CD, TMPL_NM, PERM_TMPL_ID, PERM_NMSPC_CD, PERM_NM, PERM_ID, ATTR_NM, ATTR_VAL )
AS
SELECT
t.nmspc_cd AS tmpl_nmspc_cd
, t.NM AS tmpl_nm
, t.PERM_TMPL_ID
, p.nmspc_cd AS perm_nmspc_cd
, p.NM AS perm_nm
, p.PERM_ID
, a.NM AS attr_nm
, ad.ATTR_VAL AS attr_val
FROM KRIM_PERM_T p
LEFT JOIN KRIM_PERM_TMPL_T t
ON p.PERM_TMPL_ID = t.PERM_TMPL_ID
LEFT OUTER JOIN KRIM_PERM_ATTR_DATA_T ad
ON p.PERM_ID = ad.perm_id
LEFT OUTER JOIN KRIM_ATTR_DEFN_T a
ON ad.KIM_ATTR_DEFN_ID = a.KIM_ATTR_DEFN_ID
ORDER BY tmpl_nmspc_cd, tmpl_nm, perm_nmspc_cd, perm_id, attr_nm
/
CREATE OR REPLACE VIEW KRIM_PERM_V ( TMPL_NMSPC_CD, TMPL_NM, PERM_TMPL_ID, PERM_NMSPC_CD, PERM_NM, PERM_ID, PERM_TYP_NM, SRVC_NM )
AS
SELECT
t.nmspc_cd AS tmpl_nmspc_cd
, t.NM AS tmpl_nm
, t.PERM_TMPL_ID
, p.nmspc_cd AS perm_nmspc_cd
, p.NM AS perm_nm
, p.PERM_ID
, typ.NM AS perm_typ_nm
, typ.SRVC_NM
FROM KRIM_PERM_T p
INNER JOIN KRIM_PERM_TMPL_T t
ON p.PERM_TMPL_ID = t.PERM_TMPL_ID
LEFT OUTER JOIN KRIM_TYP_T typ
ON t.KIM_TYP_ID = typ.KIM_TYP_ID
/
CREATE OR REPLACE VIEW KRIM_PRNCPL_V ( PRNCPL_ID, PRNCPL_NM, FIRST_NM, LAST_NM, AFLTN_TYP_CD, CAMPUS_CD, EMP_STAT_CD, EMP_TYP_CD )
AS
SELECT
p.PRNCPL_ID
,p.PRNCPL_NM
,en.FIRST_NM
,en.LAST_NM
,ea.AFLTN_TYP_CD
,ea.CAMPUS_CD
,eei.EMP_STAT_CD
,eei.EMP_TYP_CD
FROM krim_prncpl_t p
LEFT OUTER JOIN krim_entity_emp_info_t eei
ON eei.ENTITY_ID = p.ENTITY_ID
LEFT OUTER JOIN krim_entity_afltn_t ea
ON ea.ENTITY_ID = p.ENTITY_ID
LEFT OUTER JOIN krim_entity_nm_t en
ON p.ENTITY_ID = en.ENTITY_ID
AND 'Y' = en.DFLT_IND
/
CREATE OR REPLACE VIEW KRIM_ROLE_GRP_V ( NMSPC_CD, ROLE_NM, ROLE_ID, GRP_NMSPC_CD, GRP_NM, ROLE_MBR_ID, ATTR_NM, ATTR_VAL )
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.role_id
, g.NMSPC_CD AS grp_nmspc_cd
, g.GRP_NM
, rm.ROLE_MBR_ID
, a.NM AS attr_nm
, d.ATTR_VAL AS attr_val
FROM KRIM_ROLE_MBR_T rm
LEFT JOIN KRIM_ROLE_T r
ON r.ROLE_ID = rm.ROLE_ID
LEFT JOIN KRIM_GRP_T g
ON g.GRP_ID = rm.MBR_ID
LEFT OUTER JOIN KRIM_ROLE_MBR_ATTR_DATA_T d
ON d.role_mbr_id = rm.ROLE_MBR_ID
LEFT OUTER JOIN KRIM_ATTR_DEFN_T a
ON a.KIM_ATTR_DEFN_ID = d.KIM_ATTR_DEFN_ID
WHERE rm.MBR_TYP_CD = 'G'
ORDER BY nmspc_cd, role_nm, grp_nmspc_cd, grp_nm, role_mbr_id, attr_nm
/
CREATE OR REPLACE VIEW KRIM_ROLE_PERM_V ( NMSPC_CD, ROLE_NM, ROLE_ID, TMPL_NMSPC_CD, TMPL_NM, PERM_TMPL_ID, PERM_NMPSC_CD, PERM_NM, PERM_ID, ATTR_NM, ATTR_VAL )
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.role_id
, pt.NMSPC_CD AS tmpl_nmspc_cd
, pt.NM AS tmpl_nm
, pt.PERM_TMPL_ID
, p.NMSPC_CD AS perm_nmpsc_cd
, p.NM AS perm_nm
, p.PERM_ID
, a.NM AS attr_nm
, ad.ATTR_VAL AS attr_val
FROM KRIM_PERM_T p
LEFT JOIN KRIM_PERM_TMPL_T pt
ON p.PERM_TMPL_ID = pt.PERM_TMPL_ID
LEFT OUTER JOIN KRIM_PERM_ATTR_DATA_T ad
ON p.PERM_ID = ad.perm_id
LEFT OUTER JOIN KRIM_ATTR_DEFN_T a
ON ad.KIM_ATTR_DEFN_ID = a.KIM_ATTR_DEFN_ID
LEFT OUTER JOIN KRIM_ROLE_PERM_T rp
ON rp.PERM_ID = p.PERM_ID
LEFT OUTER JOIN KRIM_ROLE_T r
ON rp.ROLE_ID = r.ROLE_ID
ORDER BY NMSPC_CD, role_nm, tmpl_nmspc_cd, tmpl_nm, perm_id, attr_nm
/
CREATE OR REPLACE VIEW KRIM_ROLE_PRNCPL_V ( NMSPC_CD, ROLE_NM, ROLE_ID, PRNCPL_NM, PRNCPL_ID, FIRST_NM, LAST_NM, ROLE_MBR_ID, ATTR_NM, ATTR_VAL )
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.ROLE_ID
, p.PRNCPL_NM
, p.PRNCPL_ID
, en.FIRST_NM
, en.LAST_NM
, rm.ROLE_MBR_ID
, ad.NM AS attr_nm
, rmad.ATTR_VAL AS attr_val
FROM KRIM_ROLE_T r
LEFT OUTER JOIN KRIM_ROLE_MBR_T rm
ON r.ROLE_ID = rm.ROLE_ID
LEFT OUTER JOIN KRIM_ROLE_MBR_ATTR_DATA_T rmad
ON rm.ROLE_MBR_ID = rmad.role_mbr_id
LEFT OUTER JOIN KRIM_ATTR_DEFN_T ad
ON rmad.KIM_ATTR_DEFN_ID = ad.KIM_ATTR_DEFN_ID
LEFT OUTER JOIN KRIM_PRNCPL_T p
ON rm.MBR_ID = p.PRNCPL_ID
AND rm.mbr_typ_cd = 'P'
LEFT OUTER JOIN KRIM_ENTITY_NM_T en
ON p.ENTITY_ID = en.ENTITY_ID
WHERE (en.DFLT_IND = 'Y')
ORDER BY nmspc_cd, role_nm, prncpl_nm, rm.ROLE_MBR_ID, attr_nm
/
CREATE OR REPLACE VIEW KRIM_ROLE_ROLE_V ( NMSPC_CD, ROLE_NM, ROLE_ID, MBR_ROLE_NMSPC_CD, MBR_ROLE_NM, MBR_ROLE_ID, ROLE_MBR_ID, ATTR_NM, ATTR_VAL )
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.role_id
, mr.NMSPC_CD AS mbr_role_nmspc_cd
, mr.role_NM AS mbr_role_nm
, mr.role_id AS mbr_role_id
, rm.role_mbr_id
, a.NM AS attr_nm
, d.ATTR_VAL AS attr_val
FROM KRIM_ROLE_MBR_T rm
LEFT JOIN KRIM_ROLE_T r
ON r.ROLE_ID = rm.ROLE_ID
LEFT JOIN KRIM_role_T mr
ON mr.role_ID = rm.MBR_ID
LEFT OUTER JOIN KRIM_ROLE_MBR_ATTR_DATA_T d
ON d.role_mbr_id = rm.ROLE_MBR_ID
LEFT OUTER JOIN KRIM_ATTR_DEFN_T a
ON a.KIM_ATTR_DEFN_ID = d.KIM_ATTR_DEFN_ID
WHERE rm.MBR_TYP_CD = 'R'
ORDER BY nmspc_cd, role_nm, mbr_role_nmspc_cd, mbr_role_nm, role_mbr_id, attr_nm
/
CREATE OR REPLACE VIEW KRIM_ROLE_V ( NMSPC_CD, ROLE_NM, ROLE_ID, ROLE_TYP_NM, SRVC_NM, KIM_TYP_ID )
AS
SELECT r.NMSPC_CD
, r.ROLE_NM
, r.ROLE_ID
, t.nm AS role_typ_nm
, t.SRVC_NM
, t.KIM_TYP_ID
FROM KRIM_ROLE_T r
, KRIM_TYP_T t
WHERE t.KIM_TYP_ID = r.KIM_TYP_ID
AND r.ACTV_IND = 'Y'
ORDER BY nmspc_cd
, role_nm
/
CREATE OR REPLACE VIEW KRIM_RSP_ATTR_V ( RESPONSIBILITY_TYPE_NAME, RSP_TEMPLATE_NAME, RSP_NAMESPACE_CODE, RSP_NAME, RSP_ID, ATTRIBUTE_NAME, ATTRIBUTE_VALUE )
AS
SELECT
krim_typ_t.NM AS responsibility_type_name
, KRIM_rsp_TMPL_T.NM AS rsp_TEMPLATE_NAME
, KRIM_rsp_T.nmspc_cd AS rsp_namespace_code
, KRIM_rsp_T.NM AS rsp_NAME
, krim_rsp_t.RSP_ID AS rsp_id
, KRIM_ATTR_DEFN_T.NM AS attribute_name
, KRIM_rsp_ATTR_DATA_T.ATTR_VAL AS attribute_value
FROM KRIM_rsp_T KRIM_rsp_T
INNER JOIN KRIM_rsp_ATTR_DATA_T KRIM_rsp_ATTR_DATA_T
ON KRIM_rsp_T.rsp_ID = KRIM_rsp_ATTR_DATA_T.rsp_id
INNER JOIN KRIM_ATTR_DEFN_T KRIM_ATTR_DEFN_T
ON KRIM_rsp_ATTR_DATA_T.KIM_ATTR_DEFN_ID = KRIM_ATTR_DEFN_T.KIM_ATTR_DEFN_ID
INNER JOIN KRIM_rsp_TMPL_T KRIM_rsp_TMPL_T
ON KRIM_rsp_T.rsp_TMPL_ID = KRIM_rsp_TMPL_T.rsp_TMPL_ID
INNER JOIN KRIM_TYP_T KRIM_TYP_T
ON KRIM_rsp_TMPL_T.KIM_TYP_ID = KRIM_TYP_T.KIM_TYP_ID
ORDER BY rsp_TEMPLATE_NAME, rsp_NAME, attribute_name
/
CREATE OR REPLACE VIEW KRIM_RSP_ROLE_ACTN_V ( RSP_NMSPC_CD, RSP_ID, NMSPC_CD, ROLE_NM, ROLE_ID, MBR_ID, MBR_TYP_CD, ROLE_MBR_ID, ACTN_TYP_CD, ACTN_PLCY_CD, IGNORE_PREV_IND, PRIORITY_NBR )
AS
select
rsp.nmspc_cd as rsp_nmspc_cd
, rsp.rsp_id
, r.NMSPC_CD
, r.ROLE_NM
, rr.ROLE_ID
, rm.MBR_ID
, rm.MBR_TYP_CD
, rm.ROLE_MBR_ID
, actn.ACTN_TYP_CD
, actn.ACTN_PLCY_CD
, actn.IGNORE_PREV_IND
, actn.PRIORITY_NBR
from krim_rsp_t rsp
left join krim_rsp_tmpl_t rspt
on rsp.rsp_tmpl_id = rspt.rsp_tmpl_id
left outer join krim_role_rsp_t rr
on rr.rsp_id = rsp.rsp_id
left outer join KRIM_ROLE_MBR_T rm
ON rm.ROLE_ID = rr.ROLE_ID
left outer join KRIM_ROLE_RSP_ACTN_T actn
ON actn.ROLE_RSP_ID = rr.ROLE_RSP_ID
AND (actn.ROLE_MBR_ID = rm.ROLE_MBR_ID OR actn.ROLE_MBR_ID = '*')
left outer join krim_role_t r
on rr.role_id = r.role_id
order by rsp_nmspc_cd
, rsp_id
, role_id
, role_mbr_id
/
CREATE OR REPLACE VIEW KRIM_RSP_ROLE_V ( RSP_TMPL_NMSPC_CD, RSP_TMPL_NM, RSP_NMSPC_CD, RSP_NM, RSP_ID, ATTR_NM, ATTR_VAL, NMSPC_CD, ROLE_NM, ROLE_ID )
AS
select
rspt.nmspc_cd as rsp_tmpl_nmspc_cd
, rspt.nm as rsp_tmpl_nm
, rsp.nmspc_cd as rsp_nmspc_cd
, rsp.nm as rsp_nm
, rsp.rsp_id
, a.nm as attr_nm
, d.attr_val
, r.NMSPC_CD
, r.ROLE_NM
, rr.ROLE_ID
from krim_rsp_t rsp
left join krim_rsp_tmpl_t rspt
on rsp.rsp_tmpl_id = rspt.rsp_tmpl_id
left outer join krim_rsp_attr_data_t d
on rsp.rsp_id = d.rsp_id
left outer join krim_attr_defn_t a
on d.kim_attr_defn_id = a.kim_attr_defn_id
left outer join krim_role_rsp_t rr
on rr.rsp_id = rsp.rsp_id
left outer join krim_role_t r
on rr.role_id = r.role_id
order by rsp_tmpl_nmspc_cd, rsp_tmpl_nm, rsp_nmspc_cd, rsp_nm, rsp_id, attr_nm, attr_val
/
-- KULRICE-2970: Renamed some table columns to reflect the change from "ignore previous" to "force action".

ALTER TABLE KREW_ACTN_RQST_T RENAME COLUMN IGN_PREV_ACTN_IND TO FRC_ACTN
/
ALTER TABLE KREW_RULE_T RENAME COLUMN IGNR_PRVS TO FRC_ACTN
/
ALTER TABLE KRIM_PND_ROLE_RSP_ACTN_MT RENAME COLUMN IGNORE_PREV_IND TO FRC_ACTN
/
ALTER TABLE KRIM_ROLE_RSP_ACTN_T RENAME COLUMN IGNORE_PREV_IND TO FRC_ACTN
/
CREATE OR REPLACE VIEW KRIM_RSP_ROLE_ACTN_V ( RSP_NMSPC_CD, RSP_ID, NMSPC_CD, ROLE_NM, ROLE_ID, MBR_ID, MBR_TYP_CD, ROLE_MBR_ID, ACTN_TYP_CD, ACTN_PLCY_CD, FRC_ACTN, PRIORITY_NBR )
AS
select
rsp.nmspc_cd as rsp_nmspc_cd
, rsp.rsp_id
, r.NMSPC_CD
, r.ROLE_NM
, rr.ROLE_ID
, rm.MBR_ID
, rm.MBR_TYP_CD
, rm.ROLE_MBR_ID
, actn.ACTN_TYP_CD
, actn.ACTN_PLCY_CD
, actn.FRC_ACTN
, actn.PRIORITY_NBR
from krim_rsp_t rsp
left join krim_rsp_tmpl_t rspt
on rsp.rsp_tmpl_id = rspt.rsp_tmpl_id
left outer join krim_role_rsp_t rr
on rr.rsp_id = rsp.rsp_id
left outer join KRIM_ROLE_MBR_T rm
ON rm.ROLE_ID = rr.ROLE_ID
left outer join KRIM_ROLE_RSP_ACTN_T actn
ON actn.ROLE_RSP_ID = rr.ROLE_RSP_ID
AND (actn.ROLE_MBR_ID = rm.ROLE_MBR_ID OR actn.ROLE_MBR_ID = '*')
left outer join krim_role_t r
on rr.role_id = r.role_id
order by rsp_nmspc_cd
, rsp_id
, role_id
, role_mbr_id
/UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='notsys' WHERE PRNCPL_NM='NotSys'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='notsysadm' WHERE PRNCPL_NM='NotSysAdm'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testadmin1' WHERE PRNCPL_NM='TestAdmin1'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testadmin2' WHERE PRNCPL_NM='TestAdmin2'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser1' WHERE PRNCPL_NM='TestUser1'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser2' WHERE PRNCPL_NM='TestUser2'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser3' WHERE PRNCPL_NM='TestUser3'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser4' WHERE PRNCPL_NM='TestUser4'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser5' WHERE PRNCPL_NM='TestUser5'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='testuser6' WHERE PRNCPL_NM='TestUser6'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_NM='newacctusr' WHERE PRNCPL_NM='newAcctUsr'
/

UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='notsys' WHERE PRNCPL_ID='NotSys'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='notsysadm' WHERE PRNCPL_ID='NotSysAdm'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testadmin1' WHERE PRNCPL_ID='TestAdmin1'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testadmin2' WHERE PRNCPL_ID='TestAdmin2'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser1' WHERE PRNCPL_ID='TestUser1'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser2' WHERE PRNCPL_ID='TestUser2'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser3' WHERE PRNCPL_ID='TestUser3'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser4' WHERE PRNCPL_ID='TestUser4'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser5' WHERE PRNCPL_ID='TestUser5'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='testuser6' WHERE PRNCPL_ID='TestUser6'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='newaccountuser' WHERE PRNCPL_ID='newAccountUser'
/
UPDATE KRIM_PRNCPL_T SET PRNCPL_ID='kuluser' WHERE PRNCPL_ID='KULUSER'
/

CREATE SEQUENCE TRV_FO_ID_S INCREMENT BY 1 START WITH 1000
/

DELETE FROM KRIM_ENTITY_EMAIL_T where EMAIL_ADDR='KULUSER@indiana.edu'
/

CREATE TABLE KR_KIM_TEST_BO (
    pk VARCHAR2(40),
    prncpl_id VARCHAR2(40)
)
/INSERT INTO KRIM_PERM_TMPL_T(PERM_TMPL_ID, OBJ_ID, VER_NBR, NM, DESC_TXT, KIM_TYP_ID, ACTV_IND, NMSPC_CD) values ('49',	'662384B381B867A1E0404F8189D868A6','1','Send Ad Hoc Request','','5','Y','KR-NS')
/


INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) values ('332','662384B381B967A1E0404F8189D868A6',1,'49','Send Ad Hoc Request','','Y','KR-SYS')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) values ('333','662384B381BD67A1E0404F8189D868A6',1,'49','Send Ad Hoc Request','','Y','KR-SYS')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NM, DESC_TXT, ACTV_IND, NMSPC_CD) values ('334','662384B381C167A1E0404F8189D868A6',1,'49','Send Ad Hoc Request','','Y','KR-SYS')
/


INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values ('618','662384B381BC67A1E0404F8189D868A6',1,'83','332','Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values ('616','662384B381C067A1E0404F8189D868A6',1,'83','333','Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) values ('617','662384B381C467A1E0404F8189D868A6',1,'66','334','Y')
/


INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('478','662384B381BA67A1E0404F8189D868A6','1','332','5','13','KualiDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('479','662384B381BB67A1E0404F8189D868A6','1','332','5','14','F')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('480','662384B381BE67A1E0404F8189D868A6','1','333','5','13','KualiDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('481','662384B381BF67A1E0404F8189D868A6','1','333','5','14','K')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('482','662384B381C267A1E0404F8189D868A6','1','334','5','13','KualiDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) values ('483','662384B381C367A1E0404F8189D868A6','1','334','5','14','A')
/


UPDATE KRIM_GRP_MBR_T SET MBR_ID='notsys' WHERE MBR_ID='NotSys' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='notsysadm' WHERE MBR_ID='NotSysAdm' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testadmin1' WHERE MBR_ID='TestAdmin1' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testadmin2' WHERE MBR_ID='TestAdmin2' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser1' WHERE MBR_ID='TestUser1' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser2' WHERE MBR_ID='TestUser2' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser3' WHERE MBR_ID='TestUser3' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser4' WHERE MBR_ID='TestUser4' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser5' WHERE MBR_ID='TestUser5' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='testuser6' WHERE MBR_ID='TestUser6' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='newaccountuser' WHERE MBR_ID='newAccountUser' AND MBR_TYP_CD = 'P'
/
UPDATE KRIM_GRP_MBR_T SET MBR_ID='kuluser' WHERE MBR_ID='KULUSER' AND MBR_TYP_CD = 'P'
/INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
'5a5fbe94-846f-102c-8db0-c405cae621f3',
'A semi-colon delimted list of regular expressions that identify
potentially sensitive data in strings.  These patterns will be matched
against notes, document explanations, and routing annotations.',
'All',
'SENSITIVE_DATA_PATTERNS',
'CONFG',
'[0-9]{9};[0-9]{3}-[0-9]{2}-[0-9]{4}',
1
)
/

CREATE SEQUENCE KRNS_MAINT_LOCK_S START WITH 2000 INCREMENT BY 1
/
ALTER TABLE KRNS_MAINT_LOCK_T ADD MAINT_LOCK_ID VARCHAR2(14)
/
ALTER TABLE KRNS_MAINT_LOCK_T DROP PRIMARY KEY
/

DECLARE
CURSOR cursor1 IS
	SELECT MAINT_LOCK_REP_TXT FROM KRNS_MAINT_LOCK_T;
BEGIN
	FOR r IN cursor1 LOOP
        execute immediate 'UPDATE KRNS_MAINT_LOCK_T SET MAINT_LOCK_ID=KRNS_MAINT_LOCK_S.nextval';
    END LOOP;
END;
/

ALTER TABLE KRNS_MAINT_LOCK_T ADD PRIMARY KEY (MAINT_LOCK_ID)
/

ALTER TABLE KRNS_MAINT_LOCK_T MODIFY MAINT_LOCK_REP_TXT VARCHAR2(500)
/

alter table krns_sesn_doc_t
ADD content_encrypted_ind CHAR(1) DEFAULT 'N'
/update krew_doc_typ_t set parnt_id = '2681' where doc_typ_nm = 'NamespaceMaintenanceDocument'
/
update krew_doc_typ_t set parnt_id = '2681' where doc_typ_nm = 'ParameterDetailTypeMaintenanceDocument'
/
update krew_doc_typ_t set parnt_id = '2681' where doc_typ_nm = 'ParameterMaintenanceDocument'
/
update krew_doc_typ_t set parnt_id = '2681' where doc_typ_nm = 'ParameterTypeMaintenanceDocument'
/
update KRIM_PERM_ATTR_DATA_T Set KIM_ATTR_DEFN_ID='4' where PERM_ID = '163' AND ATTR_VAL = 'KR*'
/INSERT INTO krns_parm_t(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD)
  VALUES('KR-NS', 'All', 'DEFAULT_COUNTRY', '64B87B4C5E3B8F4CE0404F8189D8291A', 1, 'CONFG', 'US', 'Used as the default country code when relating records that do not have a country code to records that do have a country code, e.g. validating a zip code where the country is not collected.', 'A')
/

UPDATE krim_typ_t
    SET srvc_nm = 'rolePermissionTypeService'
WHERE nm = 'Role'
/

UPDATE krim_typ_t
    SET srvc_nm = 'groupPermissionTypeService'
WHERE nm = 'Group'
/
UPDATE krim_typ_t
    SET nm = 'Edit Mode & Document Type'
      , srvc_nm = 'documentTypeAndEditModePermissionTypeService'
      WHERE nm = 'Edit Mode'
/
UPDATE krew_doc_typ_t
    SET POST_PRCSR = 'org.kuali.rice.kns.workflow.postprocessor.KualiPostProcessor'
    WHERE DOC_TYP_NM = 'KualiDocument'
/
UPDATE krew_doc_typ_t
    SET POST_PRCSR = NULL
    WHERE DOC_TYP_NM = 'IdentityManagementPersonDocument'
/
COMMIT
/
INSERT INTO krns_parm_t ( NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, TXT, PARM_TYP_CD, PARM_DESC_TXT, CONS_CD, OBJ_ID )
    VALUES (
          'KR-IDM'
        , 'Document'
        , 'MAX_MEMBERS_PER_PAGE'
        , '20'
        , 'CONFG'
        , 'The maximum number of role or group members to display at once on their documents. If the number is above this value, the document will switch into a paging mode with only this many rows displayed at a time.'
        , 'A'
        , '2238b58e-8fb9-102c-9461-def224dad9b3'
      )
/
COMMIT
/
INSERT INTO krns_parm_dtl_typ_t
    ( SELECT 'KR-IDM', parm_dtl_typ_cd, SYS_GUID(), 1, nm, 'Y'
        FROM krns_parm_dtl_typ_t
        WHERE NMSPC_CD = 'KR-NS'
    )
/
COMMIT
/ TRUNCATE TABLE KRNS_SESN_DOC_T
/

DROP TABLE KRNS_SESN_DOC_T
/

CREATE TABLE KRNS_SESN_DOC_T
(SESN_DOC_ID                   VARCHAR2(40) NOT NULL,
DOC_HDR_ID                     VARCHAR2(14) NOT NULL,
PRNCPL_ID                             VARCHAR2(40) NOT NULL,
IP_ADDR                                 VARCHAR2(60) NOT NULL,
SERIALZD_DOC_FRM               BLOB,
LAST_UPDT_DT                   DATE,
CONTENT_ENCRYPTED_IND          CHAR(1) DEFAULT 'N')
/

CREATE INDEX KRNS_SESN_DOC_TI1 ON KRNS_SESN_DOC_T
(
LAST_UPDT_DT                    ASC
)
/

ALTER TABLE KRNS_SESN_DOC_T
ADD CONSTRAINT KRNS_SESN_DOC_TP1
PRIMARY KEY (SESN_DOC_ID, DOC_HDR_ID, PRNCPL_ID, IP_ADDR)
/ALTER TABLE KRIM_ROLE_RSP_ACTN_T
DROP CONSTRAINT KRIM_ROLE_RSP_ACTN_TR2
/
ALTER TABLE KRIM_PND_DLGN_MBR_T
ADD ROLE_MBR_ID VARCHAR(40)
/
ALTER TABLE KRIM_DLGN_MBR_T DROP CONSTRAINT KRIM_DLGN_MBR_TR1
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('683', sys_guid(), 1, '59', '334', 'Y')
/

-- this row doesn't do anything, but it is possible that it could cause a potential problem
delete from krim_perm_attr_data_t where perm_id = '163' AND kim_typ_id = '16' AND kim_attr_defn_id = '4' AND attr_val = 'KR-SYS'
/
-- next 3 statements create a Kim Group (Insert member) permission for groups with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('833', '5B4F09744953EF33E0404F8189D84F25', 1, '38', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('203', '5B4F09744A39EF33E0404F8189D84F25', 1, '833', '21', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('838', '5C27A267EF6D7417E0404F8189D830AA', 1, '63', '833', 'Y')
/

-- next 3 statements create a Kim Roles permission for roles with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('834', '5B4F09744953EF33E0404F8189D84F26', 1, '35', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('204', '5B4F09744A39EF33E0404F8189D84F26', 1, '834', '18', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('839', '5C27A267EF6D7417E0404F8189D830AB', 1, '63', '834', 'Y')
/

-- next 3 statements create a Kim Permission permission for permissions with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('835', '5B4F09744953EF33E0404F8189D84F27', 1, '36', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('205', '5B4F09744A39EF33E0404F8189D84F27', 1, '835', '19', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('840', '5C27A267EF6D7417E0404F8189D830AC', 1, '63', '835', 'Y')
/

-- next 3 statements create a Kim Responsibility permission for responsibilities with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('836', '5B4F09744953EF33E0404F8189D84F28', 1, '37', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('206', '5B4F09744A39EF33E0404F8189D84F28', 1, '836', '20', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('841', '5C27A267EF6D7417E0404F8189D830AD', 1, '63', '835', 'Y')
/
ALTER TABLE KRIM_DLGN_MBR_T DROP CONSTRAINT KRIM_DLGN_MBR_TR2
/
commit
/
ALTER TABLE KRIM_DLGN_MBR_T
    ADD CONSTRAINT KRIM_DLGN_MBR_TR2 FOREIGN KEY (DLGN_ID)
    REFERENCES KRIM_DLGN_T (DLGN_ID)
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2708,2681,'CampusMaintenanceDocument',0,1,1,'CampusMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'616D94CA-D08D-D036-E77D-4B53DB34CD95');
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2709,2681,'CampusTypeMaintenanceDocument',0,1,1,'CampusTypeMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'DE0B8588-E459-C07A-87B8-6ACD693AE70C');
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2710,2681,'CountryMaintenanceDocument',0,1,1,'CountryMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'82EDB593-97BA-428E-C6E7-A7F3031CFAEB');
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2711,2681,'CountyMaintenanceDocument',0,1,1,'CountyMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'C972E260-5552-BB63-72E6-A514301B0326');
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2712,2681,'PostalCodeMaintenanceDocument',0,1,1,'PostalCodeMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'B79D1104-BC48-1597-AFBE-773EED31A110');
/
Insert into KREW_DOC_TYP_T (DOC_TYP_ID,PARNT_ID,DOC_TYP_NM,DOC_TYP_VER_NBR,ACTV_IND,CUR_IND,LBL,PREV_DOC_TYP_VER_NBR,DOC_HDR_ID,DOC_TYP_DESC,DOC_HDLR_URL,POST_PRCSR,JNDI_URL,ADV_DOC_SRCH_URL,CSTM_ACTN_LIST_ATTRIB_CLS_NM,CSTM_ACTN_EMAIL_ATTRIB_CLS_NM,CSTM_DOC_NTE_ATTRIB_CLS_NM,VER_NBR,RTE_VER_NBR,NOTIFY_ADDR,SVC_NMSPC,EMAIL_XSL,BLNKT_APPR_PLCY,SEC_XML,BLNKT_APPR_GRP_ID,RPT_GRP_ID,GRP_ID,HELP_DEF_URL,OBJ_ID) values (2713,2681,'StateMaintenanceDocument',0,1,1,'StateMaintenanceDocument',null,null,null,null,null,null,null,null,null,null,1,'2',null,null,null,null,null,null,null,null,null,'EF2378F6-E770-D7BF-B7F1-C18881E3AFF0');
/ALTER TABLE KRIM_PERSON_DOCUMENT_T
MODIFY TAX_ID VARCHAR2(100)
/INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
VALUES ('378', '5B4F09744953EF33E0404F8189D84F29', '1', '1', 'KR-IDM', 'Override Entity Privacy Preferences', 'Allows a user to override entity privacy preferences', 'Y')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('230', '5C27A267EF5C7417E0404F8189D830AA', 1, '63', '378', 'Y')
/ -- Fixes an issue with our sample eDoc Lite document

UPDATE KREW_DOC_TYP_T SET SVC_NMSPC=NULL WHERE SVC_NMSPC='FooBar'
/DROP TABLE KRIM_PERM_RQRD_ATTR_T
/
DROP TABLE KRIM_RSP_RQRD_ATTR_T
/

-- KULRICE-3287

DELETE FROM KRNS_PARM_T where NMSPC_CD='KR-WKFLW' AND PARM_DTL_TYP_CD='All' AND PARM_NM='APPLICATION_CONTEXT'
/

-- KULRICE-3278

DECLARE

    ref_perm_id VARCHAR2(40);

BEGIN

    SELECT perm_id INTO ref_perm_id FROM krim_perm_attr_data_t WHERE attr_val = 'org.kuali.rice.kew.web.backdoor.AdministrationAction';
    DELETE FROM krim_perm_attr_data_t WHERE perm_id = ref_perm_id;
    DELETE FROM krim_role_perm_t WHERE perm_id = ref_perm_id;
    DELETE FROM krim_perm_t WHERE perm_id = ref_perm_id;

END;
/ -- KULRICE-3126 This will turn off field level help by default.
UPDATE KRNS_PARM_T SET TXT = 'N' WHERE NMSPC_CD = 'KR-NS' AND PARM_DTL_TYP_CD = 'All' AND PARM_NM = 'ENABLE_FIELD_LEVEL_HELP_IND'
/
-- KULRICE-3349: Add the doc handler URL to the Campus, Campus Type, Country, County, Postal Code, and State document types.
UPDATE KREW_DOC_TYP_T SET DOC_HDLR_URL='${kr.url}/maintenance.do?methodToCall=docHandler' WHERE DOC_TYP_NM='CampusMaintenanceDocument' OR DOC_TYP_NM='CampusTypeMaintenanceDocument' OR DOC_TYP_NM='CountryMaintenanceDocument' OR DOC_TYP_NM='CountyMaintenanceDocument' OR DOC_TYP_NM='PostalCodeMaintenanceDocument' OR DOC_TYP_NM='StateMaintenanceDocument'
/
--KULRICE-3283
ALTER TABLE KRNS_PARM_T ADD APPL_NMSPC_CD  varchar2(20) default 'KUALI' not null
/
ALTER TABLE KRNS_PARM_T DROP CONSTRAINT KRNS_PARM_TP1
/
ALTER TABLE KRNS_PARM_T ADD CONSTRAINT KRNS_PARM_TP1 PRIMARY KEY(NMSPC_CD,PARM_DTL_TYP_CD,PARM_NM, APPL_NMSPC_CD)

-- 07-14-09

/-- KULRICE-3212
ALTER TABLE KREW_DOC_HDR_T MODIFY (APP_DOC_ID VARCHAR2(255))
/

-- KULRICE-3015 - Standardize length of document type name and lbl columns
ALTER TABLE KREW_ACTN_ITM_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_DOC_TYP_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_RULE_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_EDL_ASSCTN_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_EDL_DMP_T MODIFY (DOC_TYP_NM VARCHAR2(64))
/
ALTER TABLE KREW_DOC_TYP_T MODIFY (LBL VARCHAR2(128))
/
ALTER TABLE KREW_ACTN_ITM_T MODIFY (DOC_TYP_LBL VARCHAR2(128))
/
ALTER TABLE KREW_OUT_BOX_ITM_T MODIFY (DOC_TYP_LBL VARCHAR2(128))
/


alter table KREN_MSG_DELIV_T modify (LOC
