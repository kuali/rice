--
-- Copyright 2005-2016 The Kuali Foundation
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

INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
VALUES ('KRSAP10030', SYS_GUID(), '1', '46', 'KR-SAP', 'View Note / Attachment Travel Document', 'Applies to travel document', 'Y')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
VALUES ('KRSAP13300', SYS_GUID(), '1', 'KRSAP10030', '9', '13', 'TravelDocument')
/
INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
VALUES ('KRSAP13002', SYS_GUID(), '1', 'KRSAP10004', 'KRSAP10030', 'Y')
/
INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
VALUES ('KRSAP10031', SYS_GUID(), '1', '46', 'KR-SAP', 'View Note / Attachment Travel Document Attachment', 'Applies to travel document attachments', 'Y')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
VALUES ('KRSAP13400', SYS_GUID(), '1', 'KRSAP10031', '9', '13', 'TravelDocument')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
VALUES ('KRSAP13401', SYS_GUID(), '1', 'KRSAP10031', '9', '9', 'OTH')
/
INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
VALUES ('KRSAP13003', SYS_GUID(), '1', 'KRSAP10003', 'KRSAP10031', 'Y')
/
