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

-- KULRICE-9252 Configuration Parameter for default help URL for KRAD lookup views

-- fixes config parameter for kitchen sink help test
INSERT INTO KRCR_CMPNT_T (NMSPC_CD, CMPNT_CD, OBJ_ID, VER_NBR, NM, ACTV_IND)
VALUES ('KR-SAP', 'TestComponent', '69A9BABE4A0FBD56E0404F8189D82512', 1, 'Test Component','Y')
/
update KRCR_PARM_T set CMPNT_CD='TestComponent',  VAL='http://site.kuali.org/rice/latest/reference/html/Help.html#document_type'
where NMSPC_CD='KR-SAP' AND PARM_NM='TEST_PARAM'
/
