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
--Alter table statements--;
ALTER TABLE EN_ACTN_RQST_T ADD ACTN_RQST_RTE_NODE_INSTN_ID NUMBER(19);

alter table en_rule_attrib_t ADD RULE_ATTRIB_XML_RTE_TXT LONG NULL;

alter table EN_ACTN_RQST_T modify actn_rqst_rte_typ_nm varchar2(255) null;

ALTER TABLE EN_DOC_TYP_T ADD DOC_TYP_RTE_VER_NBR VARCHAR(2) DEFAULT '1' NOT NULL;

alter table EN_DOC_HDR_EXT_T modify DOC_HDR_EXT_VAL_KEY VARCHAR2(32);
alter table EN_DOC_HDR_EXT_T modify DOC_HDR_EXT_VAL VARCHAR2(256);

insert into EN_DOC_TYP_PLCY_RELN_T select distinct doc_typ_id, 'LOOK_FUTURE', 0, 0 from EN_DOC_TYP_PLCY_RELN_T;
