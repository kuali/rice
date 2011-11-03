--
-- Copyright 2005-2011 The Kuali Foundation
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
