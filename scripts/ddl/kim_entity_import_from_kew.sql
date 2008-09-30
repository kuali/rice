-- load the principal table

INSERT INTO kr_kim_principal_t ( prncpl_id, prncpl_nm, entity_id, obj_id )
    ( SELECT PRSN_EN_ID, PRSN_NTWRK_ID, kr_kim_entity_id_seq.NEXTVAL, SYS_GUID()
        FROM EN_USR_T
    )
/
COMMIT
/ 

-- use the entity ID in the principal table to convert the UUIDs to entity IDs

-- create the entity records
INSERT INTO kr_kim_entity_t ( ENTITY_ID, OBJ_ID )
    ( SELECT entity_id, SYS_GUID()
        FROM KR_KIM_PRINCIPAL_T )
/
COMMIT
/ 

-- entity type
INSERT INTO KR_KIM_ENTITY_ENT_TYPE_T ( ENTITY_ENT_TYPE_ID, ENTITY_ID, ENT_TYP_CD, OBJ_ID )
    ( SELECT kr_kim_ENTITY_ENT_TYPE_ID_seq.NEXTVAL, entity_id, 'PERSON', SYS_GUID()
        FROM KR_KIM_PRINCIPAL_T )
/
COMMIT
/ 

-- entity name

INSERT INTO KR_KIM_ENTITY_NAME_T ( ENTITY_NAME_ID, OBJ_ID, ENTITY_ID, ENT_TYP_CD, NAME_TYP_CD, 
                FIRST_NM, LAST_NM, DFLT_IND )
   (SELECT kr_kim_entity_name_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'PERSON', 'PREFERRED',
        u.PRSN_GVN_NM, u.PRSN_LST_NM, 'Y'
        FROM KR_KIM_PRINCIPAL_T p, EN_USR_T u
        WHERE u.PRSN_EN_ID = p.PRNCPL_ID
   )
/
COMMIT
/ 

-- email addresses (only if not null)

INSERT INTO KR_KIM_ENTITY_EMAIL_T ( ENTITY_EMAIL_ID, OBJ_ID, ENTITY_ID, ENT_TYP_CD, EMAIL_TYP_CD, 
                EMAIL_ADDR, DFLT_IND )
   (SELECT kr_kim_entity_email_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'PERSON', 'CAMPUS',
        u.PRSN_EMAIL_ADDR, 'Y'
        FROM KR_KIM_PRINCIPAL_T p, EN_USR_T u
        WHERE u.PRSN_EN_ID = p.PRNCPL_ID
          AND u.PRSN_EMAIL_ADDR IS NOT NULL
   )
/
COMMIT
/
