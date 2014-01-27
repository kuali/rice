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


-- KULRICE-11009 Adding Roles for Travel Approval


INSERT INTO KRIM_ROLE_T (ACTV_IND,DESC_TXT, KIM_TYP_ID, LAST_UPDT_DT, NMSPC_CD, OBJ_ID, ROLE_ID, ROLE_NM)
 VALUES ('Y', 'Travel Approver for KRAD Sample App', '1', SYSDATE, 'KR-SAP', sys_guid(), 'KRSAP10005', 'Travel Approver')
/

 INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, RSP_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND)
    VALUES('KRSAP10000', sys_guid(), '1', 'KR-SAP', 'Review Travel Authorization Document', '', 'Y')
/

INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, ROLE_ID, RSP_ID, ACTV_IND)
    VALUES('KRSAP10000', sys_guid(), 'KRSAP10005', 'KRSAP10000', 'Y')
/

INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, RSP_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('KRSAP10000', sys_guid(), 'KRSAP10000', '7', '13', 'TravelAuthorizationDocument')
/

INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, RSP_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('KRSAP10001', sys_guid(), 'KRSAP10000', '7', '16', 'TravelApproval')
/

INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, RSP_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('KRSAP10002', sys_guid(), 'KRSAP10000', '7', '40', 'N')
/

INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, RSP_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
    VALUES('KRSAP10003', sys_guid(), 'KRSAP10000', '7', '41', 'N')
/
