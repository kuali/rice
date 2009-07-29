--
-- Copyright 2008-2009 The Kuali Foundation
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
-- update various reference tables so that we can build maintenance docs on top of them

-- update various reference tables so that we can build maintenance docs on top of them

alter table KREN_CHNL_T add (OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, VER_NBR NUMBER(8) DEFAULT 0 NOT NULL) /
alter table KREN_CNTNT_TYP_T add (OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, VER_NBR NUMBER(8) DEFAULT 0 NOT NULL) /
alter table KREN_PRODCR_T add (OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, VER_NBR NUMBER(8) DEFAULT 0 NOT NULL) /
alter table KREN_PRIO_T add (OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, VER_NBR NUMBER(8) DEFAULT 0 NOT NULL) /
alter table KREN_RVWER_T add (OBJ_ID VARCHAR2(36) DEFAULT SYS_GUID() NOT NULL, VER_NBR NUMBER(8) DEFAULT 0 NOT NULL) /

ALTER TABLE KREN_MSG_DELIV_T DROP COLUMN VER_NBR
/
ALTER TABLE KREN_MSG_DELIV_T ADD VER_NBR NUMBER(8) DEFAULT 0 NOT NULL
/

ALTER TABLE KREN_MSG_T DROP COLUMN VER_NBR
/
ALTER TABLE KREN_MSG_T ADD VER_NBR NUMBER(8) DEFAULT 0 NOT NULL
/

ALTER TABLE KREN_NTFCTN_MSG_DELIV_T DROP COLUMN VER_NBR
/
ALTER TABLE KREN_NTFCTN_MSG_DELIV_T ADD VER_NBR NUMBER(8) DEFAULT 0 NOT NULL
/

ALTER TABLE KREN_NTFCTN_T DROP COLUMN VER_NBR
/
ALTER TABLE KREN_NTFCTN_T ADD VER_NBR NUMBER(8) DEFAULT 0 NOT NULL
/

ALTER TABLE KREN_RECIP_DELIV_T DROP COLUMN VER_NBR
/
ALTER TABLE KREN_RECIP_DELIV_T ADD VER_NBR NUMBER(8) DEFAULT 0 NOT NULL
/

ALTER TABLE KREN_RECIP_PREFS_T DROP COLUMN VER_NBR
/
ALTER TABLE KREN_RECIP_PREFS_T ADD VER_NBR NUMBER(8) DEFAULT 0 NOT NULL
/

ALTER TABLE KREN_CHNL_SUBSCRP_T modify PRNCPL_ID VARCHAR2(40)
/
ALTER TABLE KREN_CHNL_T modify NM VARCHAR2(200)
/
ALTER TABLE KREN_CNTNT_TYP_T modify NM VARCHAR2(200)
/
ALTER TABLE KREN_MSG_DELIV_T modify TYP_NM VARCHAR2(200)
/
ALTER TABLE KREN_NTFCTN_MSG_DELIV_T modify RECIP_ID VARCHAR2(40)
/
ALTER TABLE KREN_PRODCR_T modify NM VARCHAR2(200)
/
ALTER TABLE KREN_RECIP_DELIV_T modify RECIP_ID VARCHAR2(40)
/
ALTER TABLE KREN_RECIP_DELIV_T modify NM VARCHAR2(200)
/
ALTER TABLE KREN_RECIP_DELIV_T drop CONSTRAINT KCB_RECIP_DELIVS_UK1
/
ALTER TABLE KREN_RECIP_LIST_T modify RECIP_ID VARCHAR2(40)
/
ALTER TABLE KREN_RECIP_PREFS_T modify RECIP_ID VARCHAR2(40)
/
ALTER TABLE KREN_RECIP_PREFS_T modify PROP VARCHAR2(200)
/
ALTER TABLE KREN_RECIP_T modify PRNCPL_ID VARCHAR2(40)
/
ALTER TABLE KREN_RVWER_T modify PRNCPL_ID VARCHAR2(40)
/
ALTER TABLE KREN_SNDR_T modify NM VARCHAR2(200)
/

-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
-- !!! STOP!  Don't put anymore SQL in this file for Rice 1.0. Instead, create files in the !!!
-- !!! 'scripts/upgrades/0.9.3 to 0.9.4/db-updates-during-qa' directory                     !!!
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
