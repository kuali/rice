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

INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('KR-IDM', sys_guid(), 1, 'Identity Management', 'Y')
/
INSERT INTO KRNS_PARM_T(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, GRP_NM)  VALUES('KR-IDM', 'EntityNameImpl', 'PREFIXES', sys_guid(), 1, 'CONFG',  'Ms;Mrs;Mr;Dr', '','A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, GRP_NM)  VALUES('KR-IDM', 'EntityNameImpl', 'SUFFIXES', sys_guid(), 1, 'CONFG', 'Jr;Sr;Mr;Md', '','A', 'WorkflowAdmin')
/
