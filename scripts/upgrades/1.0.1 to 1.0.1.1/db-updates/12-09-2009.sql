--
-- Copyright 2010 The Kuali Foundation
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

create table TST_SEARCH_ATTR_INDX_TST_DOC_T (
    DOC_HDR_ID VARCHAR2(14),
	OBJ_ID VARCHAR2(36),
	VER_NBR NUMBER(14),
	RTE_LVL_CNT NUMBER(14),
	CNSTNT_STR VARCHAR2(50),
    RTD_STR VARCHAR2(50),
    HLD_RTD_STR VARCHAR2(50),
    RD_ACCS_CNT NUMBER(14),
    CONSTRAINT TST_SEARCH_ATTR_INDX_TST_DOC_T PRIMARY KEY (DOC_HDR_ID)
)
