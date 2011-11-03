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

alter table KREW_DOC_HDR_T add (APP_DOC_STAT varchar2(64), APP_DOC_STAT_MDFN_DT date)
/
create index KREW_DOC_HDR_T10 on KREW_DOC_HDR_T (APP_DOC_STAT)
/
create index KREW_DOC_HDR_T12 on KREW_DOC_HDR_T (APP_DOC_STAT_MDFN_DT)
/
alter table KREW_DOC_TYP_PLCY_RELN_T add PLCY_VAL varchar2(64)
/
alter table KREW_RTE_NODE_T add NEXT_DOC_STAT varchar2(64)
/
CREATE TABLE KREW_DOC_TYP_APP_DOC_STAT_T
(
      DOC_TYP_ID NUMBER(19)
        , DOC_STAT_NM VARCHAR2(64)
        , VER_NBR NUMBER(8) default 0
        , OBJ_ID VARCHAR2(36) NOT NULL    
    , CONSTRAINT KREW_DOC_TYP_APP_DOC_STAT_TP1 PRIMARY KEY(DOC_TYP_ID,DOC_STAT_NM)
    , CONSTRAINT KREW_DOC_TYP_APP_DOC_STAT_TC0 UNIQUE (OBJ_ID)
) 
/
create index KREW_DOC_TYP_APP_DOC_STAT_T1 on KREW_DOC_TYP_APP_DOC_STAT_T(DOC_TYP_ID)
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

create sequence krew_doc_lnk_s increment by 1 start with 2000 cache 20
/
create table krew_doc_lnk_t(
           DOC_LNK_ID NUMBER(19),
           ORGN_DOC_ID NUMBER(14) NOT NULL,
           DEST_DOC_ID NUMBER(14) NOT NULL,          
           CONSTRAINT KREW_DOC_LNK_TP1 PRIMARY KEY (DOC_LNK_ID)
)
/
create INDEX KREW_DOC_LNK_TI1 on krew_doc_lnk_t(ORGN_DOC_ID)
/

delete from KREW_STYLE_T where NM = 'widgets'
/

INSERT INTO krim_perm_t(PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
  VALUES('840','97469975-D110-9A65-5EE5-F21FD1BEB5B2',	'1',	'29',	'KR-BUS',	'Use Screen',	'Allows users to access the Configuration Viewer screen',	'Y')  
/

INSERT INTO krim_perm_attr_data_t(ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('880',	'ECCB8A6C-A0DA-5311-6A57-40F743EA334C',	'1',	'840',	'12',	'2','org.kuali.rice.ksb.messaging.web.ConfigViewerAction')
/

INSERT INTO krim_role_perm_t(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('855',	'E83AB210-EB48-3BDE-2D6F-F6177869AE27',	'1',	'63',	'840',	'Y')  
/
