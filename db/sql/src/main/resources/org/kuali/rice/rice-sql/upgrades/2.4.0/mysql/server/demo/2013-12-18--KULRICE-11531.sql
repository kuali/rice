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


-- KULRICE-11531 View Field and View Group Examples


INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND, VER_NBR)
 VALUES ('KRSAP12000',  UUID(), '56', 'KR-SAP', 'ViewTravelPerDiem_AuthDocId', 'This permissions allows display of the hidden authorization document id', 'Y', 1)
/

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, VER_NBR)
 VALUES ('KRSAP12100', UUID(), 'KRSAP12000', '70', '47', 'LabsInquiry-AuthorizerSecurity', 1)
/

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, VER_NBR)
 VALUES ('KRSAP12200', UUID(), 'KRSAP12000', '70', '6', 'travelAuthorizationDocumentId', 1)
/

INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, ACTV_IND, VER_NBR)
 VALUES ('KRSAP12001', UUID(), 'KRSAP10003', 'KRSAP12000', 'Y', 1)
/

INSERT INTO KRIM_PERM_T (PERM_ID, OBJ_ID, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND, VER_NBR)
 VALUES ('KRSAP13000', UUID(), '58', 'KR-SAP', 'ViewTravelPerDiem_CostEstimates', 'This permissions allows display of the hidden cost estimates section', 'Y', 1)
/

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, VER_NBR)
 VALUES ('KRSAP13100', UUID(), 'KRSAP13000', '70', '47', 'LabsInquiry-AuthorizerSecurity', 1)
/

INSERT INTO KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL, VER_NBR)
 VALUES ('KRSAP13200', UUID(), 'KRSAP13000', '70', '51', 'CostEstimatesSection', 1)
/

INSERT INTO KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, ACTV_IND, VER_NBR)
 VALUES ('KRSAP13001', UUID(), 'KRSAP10003', 'KRSAP13000', 'Y', 1)
/

