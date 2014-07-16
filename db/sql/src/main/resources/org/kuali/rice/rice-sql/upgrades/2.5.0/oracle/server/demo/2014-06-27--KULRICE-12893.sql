--
-- Copyright 2005-2014 The Kuali Foundation
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

INSERT INTO KRIM_PERM_T (PERM_ID, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND, VER_NBR, OBJ_ID)
  VALUES (843, (SELECT PERM_TMPL_ID FROM KRIM_PERM_TMPL_T WHERE NM = 'KRMS Agenda Permission' AND NMSPC_CD = 'KR-RULE'),
  'KR-RULE-TEST', 'Maintain KRMS Agenda', 'Allows creation and modification of agendas via the agenda editor', 'Y', 1, SYS_GUID())
/

INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, PERM_ID, ROLE_ID, ACTV_IND, OBJ_ID, VER_NBR)
  VALUES ('858',
  (SELECT PERM_ID FROM KRIM_PERM_T WHERE NMSPC_CD = 'KR-RULE-TEST' AND NM = 'Maintain KRMS Agenda'),
  (SELECT ROLE_ID FROM KRIM_ROLE_T WHERE NMSPC_CD = 'KR-RULE' AND ROLE_NM = 'Kuali Rules Management System Administrator'),
  'Y', 'B7DBFABEFD2F8CBFE0402E0AA9D757C9', 1)
/
