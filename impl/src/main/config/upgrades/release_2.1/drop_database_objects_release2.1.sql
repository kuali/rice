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
ALTER TABLE EN_ACTN_RQST_T DROP COLUMN ACTN_RQST_RTE_NODE_INSTN_ID;

DROP INDEX EN_ACTN_RQST_TI7;

ALTER TABLE EN_DOC_TYP_T DROP COLUMN RTE_VER_NBR;

DROP TABLE EN_DOC_TYP_PROC_T
DROP TABLE EN_RTE_NODE_T;
DROP TABLE EN_RTE_NODE_LNK_T;
DROP TABLE EN_RTE_BRCH_PROTO_T;
DROP TABLE EN_INIT_RTE_NODE_INSTN_T;
DROP TABLE EN_RTE_NODE_INSTN_T;
DROP TABLE EN_RTE_NODE_INSTN_LNK_T;
DROP TABLE EN_RTE_BRCH_T;
DROP TABLE EN_RTE_BRCH_ST_T;
DROP TABLE EN_RTE_NODE_INSTN_ST_T;

DROP SEQUENCE SEQ_RTE_NODE;
