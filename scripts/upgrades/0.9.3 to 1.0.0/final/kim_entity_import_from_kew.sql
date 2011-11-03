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

-- load the principal table

INSERT INTO KRIM_PRNCPL_T ( prncpl_id, prncpl_nm, entity_id, obj_id )
    ( SELECT PRSN_EN_ID, PRSN_NTWRK_ID, kr_kim_entity_id_seq.NEXTVAL, SYS_GUID()
        FROM EN_USR_T
    )
/

-- use the entity ID in the principal table to convert the UUIDs to entity IDs

-- create the entity records
INSERT INTO KRIM_ENTITY_T ( ENTITY_ID, OBJ_ID )
    ( SELECT entity_id, SYS_GUID()
        FROM KRIM_PRNCPL_T )
/

-- entity type
INSERT INTO KRIM_ENTITY_ENT_TYP_T (ENTITY_ID, ENT_TYP_CD, OBJ_ID )
    ( SELECT entity_id, 'PERSON', SYS_GUID()
        FROM KRIM_PRNCPL_T )
/

-- entity name

INSERT INTO KRIM_ENTITY_NM_T ( ENTITY_NM_ID, OBJ_ID, ENTITY_ID, NM_TYP_CD,
                FIRST_NM, LAST_NM, DFLT_IND )
   (SELECT KRIM_ENTITY_NM_ID_S.NEXTVAL, SYS_GUID(), p.entity_id, 'PREFERRED',
        u.PRSN_GVN_NM, u.PRSN_LST_NM, 'Y'
        FROM KRIM_PRNCPL_T p, EN_USR_T u
        WHERE u.PRSN_EN_ID = p.PRNCPL_ID
   )
/

-- email addresses (only if not null)

INSERT INTO KRIM_ENTITY_EMAIL_T ( ENTITY_EMAIL_ID, OBJ_ID, ENTITY_ID, ENT_TYP_CD, EMAIL_TYP_CD,
                EMAIL_ADDR, DFLT_IND )
   (SELECT KRIM_ENTITY_EMAIL_ID_S.NEXTVAL, SYS_GUID(), p.entity_id, 'PERSON', 'CAMPUS',
        u.PRSN_EMAIL_ADDR, 'Y'
        FROM KRIM_PRNCPL_T p, EN_USR_T u
        WHERE u.PRSN_EN_ID = p.PRNCPL_ID
          AND u.PRSN_EMAIL_ADDR IS NOT NULL
   )
/

