-- 
-- Copyright 2009 The Kuali Foundation
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
-- Update kim group table so kim type is set to the default value instead of null
-- Alter tables so null kim_type_ids are not allowed
UPDATE KRIM_GRP_T SET KIM_TYP_ID = '1' WHERE KIM_TYP_ID IS NULL
/
ALTER TABLE KRIM_DLGN_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_DLGN_MBR_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_GRP_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_GRP_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_PERM_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_PERM_TMPL_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_ROLE_MBR_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_ROLE_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_RSP_ATTR_DATA_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_RSP_TMPL_T MODIFY KIM_TYP_ID NOT NULL
/
ALTER TABLE KRIM_TYP_ATTR_T MODIFY KIM_TYP_ID NOT NULL
/

-- Add a new column to the namespace table

alter table KRNS_NMSPC_T add (APPL_NMSPC_CD VARCHAR2(20))
/

-- Add some missing namespace entries to the table

INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KR-NTFCN', '5B960CFDBB360FDFE0404F8189D83CBD', 1, 'Notification', 'Y', NULL)
/
INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KUALI', '5ADF18B6D4817954E0404F8189D85002', 1, 'Kuali Systems', 'Y', NULL)
/
INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KR-BUS', '5B960CFDBB370FDFE0404F8189D83CBD', 1, 'Service Bus', 'Y', NULL)
/
INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD)
  VALUES('KR-SYS', '5B960CFDBB390FDFE0404F8189D83CBD', 1, 'Enterprise Infrastructure', 'Y', NULL)
/

-- KULRICE-2625 - make KREW_DOC_TYP_T.RTE_VER_NBR nullable

alter table KREW_DOC_TYP_T modify RTE_VER_NBR NULL
/

-- KFSMI-2892 - missing ver_nbr defaults

alter table KRIM_DLGN_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_DLGN_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_GRP_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
alter table KRIM_ROLE_MBR_T modify (ver_nbr NUMBER(8,0) default 1)
/
