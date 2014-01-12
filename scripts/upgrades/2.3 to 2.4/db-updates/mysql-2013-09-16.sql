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

INSERT INTO KRCR_PARM_T (OBJ_ID, NMSPC_CD, CMPNT_CD, PARM_NM, PARM_TYP_CD, VAL, PARM_DESC_TXT, EVAL_OPRTR_CD, APPL_ID)
    SELECT UUID(), 'KR-KRAD', CMPNT_CD, PARM_NM, PARM_TYP_CD, VAL, PARM_DESC_TXT, EVAL_OPRTR_CD, APPL_ID
      FROM KRCR_PARM_T
     WHERE NMSPC_CD = 'KR-NS'
       AND CMPNT_CD = 'Lookup'
       AND PARM_NM = 'RESULTS_LIMIT'
       AND APPL_ID NOT IN (SELECT APPL_ID FROM KRCR_PARM_T
                        WHERE NMSPC_CD = 'KR-KRAD'
                          AND CMPNT_CD = 'Lookup'
                          AND PARM_NM = 'RESULTS_LIMIT')
;