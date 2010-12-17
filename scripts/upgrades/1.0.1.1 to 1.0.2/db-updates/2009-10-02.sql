--
-- Copyright 2009-2010 The Kuali Foundation
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
-- DO NOT add comments before the blank line below, or they will disappear.
alter table KREW_DOC_HDR_T add (APP_DOC_STATUS varchar2(30), APP_DOC_STAT_MDFN_DT date);
/
create index KREW_DOC_HDR_T10 on KREW_DOC_HDR_T (APP_DOC_STATUS);
/
create index KREW_DOC_HDR_T12 on KREW_DOC_HDR_T (APP_DOC_STAT_MDFN_DT);
/
alter table KREW_DOC_TYP_PLCY_RELN_T add PLCY_DESC varchar2(20)
/
alter table KREW_RTE_NODE_T add NEXT_DOC_STATUS varchar2(30)
/
CREATE TABLE KREW_DOC_TYP_APP_DOC_STAT_T
(
      DOC_TYP_ID NUMBER(19)
        , DOC_STAT_NM VARCHAR2(20)
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL    
    , CONSTRAINT KREW_DOC_TYP_APP_DOC_STAT_TP1 PRIMARY KEY(DOC_TYP_ID,DOC_STAT_NM)
    , CONSTRAINT KREW_DOC_TYP_APP_DOC_STAT_TC0 UNIQUE (OBJ_ID)
) 
/
create index KREW_DOC_TYP_APP_DOC_STAT_T1 on KREW_DOC_TYP_APP_DOC_STAT_T(DOC_TYP_ID)
/

ALTER TABLE KREW_DOC_HDR_T RENAME COLUMN APP_DOC_STATUS TO APP_DOC_STAT
/
ALTER TABLE KREW_DOC_HDR_T MODIFY APP_DOC_STAT VARCHAR2(64)
/
ALTER TABLE KREW_DOC_TYP_APP_DOC_STAT_T MODIFY DOC_STAT_NM VARCHAR2(64)
/
ALTER TABLE KREW_RTE_NODE_T RENAME COLUMN NEXT_DOC_STATUS TO NEXT_DOC_STAT
/
ALTER TABLE KREW_RTE_NODE_T MODIFY NEXT_DOC_STAT VARCHAR2(64)
/
ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T RENAME COLUMN PLCY_DESC TO PLCY_VAL
/
ALTER TABLE KREW_DOC_TYP_PLCY_RELN_T MODIFY PLCY_VAL VARCHAR2(64)
/
alter table KREW_RTE_NODE_T add NEXT_DOC_STATUS varchar2(30)
/
CREATE TABLE KREW_APP_DOC_STAT_TRAN_T
(
      APP_DOC_STAT_TRAN_ID NUMBER(19) PRIMARY KEY,
      DOC_HDR_ID NUMBER(14),
      APP_DOC_STAT_FROM VARCHAR2(64),
      APP_DOC_STAT_TO VARCHAR2(64),
      STAT_TRANS_DATE DATE,
      VER_NBR NUMBER(8) default 0,
      OBJ_ID VARCHAR2(36) NOT NULL,
      CONSTRAINT KREW_APP_DOC_STAT_TRAN_TC0 UNIQUE (OBJ_ID)
)
/
CREATE INDEX KREW_APP_DOC_STAT_TI1 ON KREW_APP_DOC_STAT_TRAN_T (DOC_HDR_ID, STAT_TRANS_DATE)
/
CREATE INDEX KREW_APP_DOC_STAT_TI2 ON KREW_APP_DOC_STAT_TRAN_T (DOC_HDR_ID, APP_DOC_STAT_FROM)
/
CREATE INDEX KREW_APP_DOC_STAT_TI3 ON KREW_APP_DOC_STAT_TRAN_T (DOC_HDR_ID, APP_DOC_STAT_TO)
/ 
