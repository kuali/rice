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

-- KULRICE-11134: samples for I31, I32
-- adding permissions and role assignment to unmask TravelerDetail phoneNumber field


INSERT INTO KRIM_PERM_T (PERM_ID, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND, OBJ_ID, VER_NBR)
VALUES('KRSAP2100', '27', 'KR-SAP', 'Full Unmask TravelerDetail Phone', 'Authorizes users to view the entire Phone Number on the Travel Detail document and inquiry.','Y', sys_guid(), 1)
/

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, OBJ_ID, VER_NBR)
VALUES('KRSAP2101', 'KRSAP2100', '11', '5', 'TravelerDetail', sys_guid(), 1)
/

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, OBJ_ID, VER_NBR)
VALUES('KRSAP2102', 'KRSAP2100', '11', '6', 'phoneNumber', sys_guid(), 1)
/

INSERT INTO KRIM_PERM_T (PERM_ID, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND, OBJ_ID, VER_NBR)
VALUES('KRSAP2200', '28', 'KR-SAP', 'Partial Unmask TravelerDetail Phone', 'Authorizes users to partially view the Phone Number on the Travel Detail document and inquiry.','Y', sys_guid(), 1)
/

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, OBJ_ID, VER_NBR)
VALUES('KRSAP2201', 'KRSAP2200', '11', '5', 'TravelerDetail', sys_guid(), 1)
/

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, OBJ_ID, VER_NBR)
VALUES('KRSAP2202', 'KRSAP2200', '11', '6', 'phoneNumber', sys_guid(), 1)
/

INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
VALUES('KRSAP2000', sys_guid(), 1, 'KRSAP10003', 'KRSAP2100', 'Y')
/

INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
VALUES('KRSAP2001', sys_guid(), 1, 'KRSAP10004', 'KRSAP2200', 'Y')
/

