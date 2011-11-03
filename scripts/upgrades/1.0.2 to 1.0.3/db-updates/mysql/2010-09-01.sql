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

-- KULRICE-4226
INSERT INTO krns_parm_t(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) VALUES ('KR-NS', 'All', 'SENSITIVE_DATA_PATTERNS_WARNING_IND', 'e7d133f3-b5fe-11df-ad0a-d18f5709259f', 1, 'CONFG', 'N', 'If set to ''Y'' when sensitive data is found the user will be prompted to continue the action or cancel. If this is set to ''N'' the user will be presented with an error message and will not be allowed to continue with the action until the sensitive data is removed.', 'A', 'KUALI')
/ 
