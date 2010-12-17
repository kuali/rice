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
ALTER TABLE KRIM_DLGN_MBR_T DROP CONSTRAINT KRIM_DLGN_MBR_TR1
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('683', sys_guid(), 1, '59', '334', 'Y')
/

-- this row doesn't do anything, but it is possible that it could cause a potential problem
delete from krim_perm_attr_data_t where perm_id = '163' AND kim_typ_id = '16' AND kim_attr_defn_id = '4' AND attr_val = 'KR-SYS'
/
-- next 3 statements create a Kim Group (Insert member) permission for groups with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('833', '5B4F09744953EF33E0404F8189D84F25', 1, '38', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('203', '5B4F09744A39EF33E0404F8189D84F25', 1, '833', '21', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('838', '5C27A267EF6D7417E0404F8189D830AA', 1, '63', '833', 'Y')
/

-- next 3 statements create a Kim Roles permission for roles with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('834', '5B4F09744953EF33E0404F8189D84F26', 1, '35', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('204', '5B4F09744A39EF33E0404F8189D84F26', 1, '834', '18', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('839', '5C27A267EF6D7417E0404F8189D830AB', 1, '63', '834', 'Y')
/

-- next 3 statements create a Kim Permission permission for permissions with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('835', '5B4F09744953EF33E0404F8189D84F27', 1, '36', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('205', '5B4F09744A39EF33E0404F8189D84F27', 1, '835', '19', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('840', '5C27A267EF6D7417E0404F8189D830AC', 1, '63', '835', 'Y')
/

-- next 3 statements create a Kim Responsibility permission for responsibilities with namespace of KUALI
Insert into KRIM_PERM_T (PERM_ID, OBJ_ID, VER_NBR, PERM_TMPL_ID, NMSPC_CD, NM, DESC_TXT, ACTV_IND) VALUES('836', '5B4F09744953EF33E0404F8189D84F28', 1, '37', 'KR-SYS', Null, Null, 'Y')
/
Insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID, VER_NBR, PERM_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) VALUES('206', '5B4F09744A39EF33E0404F8189D84F28', 1, '836', '20', '4', 'KUALI')
/
Insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND) VALUES('841', '5C27A267EF6D7417E0404F8189D830AD', 1, '63', '835', 'Y')
/
