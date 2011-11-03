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

create table KRIM_PRSN_CACHE_T_bak as select * from KRIM_PRSN_CACHE_T
/

drop table KRIM_PRSN_CACHE_T cascade constraints purge
/

CREATE TABLE krim_prsn_cache_t
    (prncpl_id VARCHAR2(40) NOT NULL,
    prncpl_nm VARCHAR2(40),
    entity_id VARCHAR2(40),
    entity_typ_cd VARCHAR2(40),
    first_nm VARCHAR2(40),
    middle_nm VARCHAR2(40),
    last_nm VARCHAR2(40),
    prsn_nm VARCHAR2(40),
    campus_cd VARCHAR2(40),
    prmry_dept_cd VARCHAR2(40),
    emp_id VARCHAR2(40),
    last_updt_ts DATE,
    obj_id VARCHAR2(36) NOT NULL)
/





-- Constraints for KRIM_PRSN_CACHE_T

ALTER TABLE krim_prsn_cache_t
ADD CONSTRAINT krim_prsn_cache_tp1 PRIMARY KEY (prncpl_id)
/


-- End of DDL Script for Table KULDEV.KRIM_PRSN_CACHE_T

insert into KRIM_PRSN_CACHE_T select * from KRIM_PRSN_CACHE_T_bak
/
commit
/

drop table KRIM_PRSN_CACHE_T_bak
/ 

-- create Campus KIM type

INSERT INTO KRIM_TYP_T (KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD)
  VALUES('17', '5ADF18B6D4F77954E0404F8189D85002', 1, 'Campus', 'campusRoleService', 'Y', 'KR-NS')
/
commit
/

-- Enhancement from uu to person doc that we did not get around to implementing�

delete from krim_role_perm_t where perm_id in  ('153', '154')
/
delete from krim_perm_attr_data_t where target_primary_key in  ('153', '154')
/
delete from krim_perm_t where perm_tmpl_id = '39'
/
delete from krim_perm_tmpl_t where perm_tmpl_id = '39'
/
delete from krim_typ_attr_t where kim_typ_id = '22'
/
delete from krim_attr_defn_t where kim_attr_defn_id = '17'
/
delete from krim_typ_t where kim_typ_id = '22'
/
commit
/

