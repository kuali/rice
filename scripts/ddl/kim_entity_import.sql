-- load the principal table

INSERT INTO kr_kim_principal_t ( prncpl_id, prncpl_nm, entity_id, obj_id )
    ( SELECT PERSON_UNVL_ID, LOWER( PERSON_USER_ID ), kr_kim_entity_id_seq.NEXTVAL, SYS_GUID()
        FROM FS_UNIVERSAL_USR_T
		WHERE PERSON_USER_ID = UPPER( PERSON_USER_ID )
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

INSERT INTO KR_KIM_ENTITY_NAME_T ( ENTITY_NAME_ID, OBJ_ID, ENTITY_ID, NAME_TYP_CD, 
                FIRST_NM, MIDDLE_NM, LAST_NM, DFLT_IND )
   (SELECT kr_kim_entity_name_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'PREFERRED',
        u.PRSN_1ST_NM, u.PRSN_MID_NM, u.PRSN_LST_NM, 'Y'
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
   )
/
COMMIT
/ 

-- email addresses (only if not null)

INSERT INTO KR_KIM_ENTITY_EMAIL_T ( ENTITY_EMAIL_ID, OBJ_ID, ENTITY_ID, ENT_TYP_CD, EMAIL_TYP_CD, 
                EMAIL_ADDR, DFLT_IND )
   (SELECT kr_kim_entity_email_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'PERSON', 'CAMPUS',
        u.PRSN_EMAIL_ADDR, 'Y'
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_EMAIL_ADDR IS NOT NULL
   )
/
COMMIT
/

-- phone numbers (only if not null)

INSERT INTO KR_KIM_ENTITY_PHONE_T ( ENTITY_PHONE_ID, OBJ_ID, ENTITY_ID, ENT_TYP_CD, PHONE_TYP_CD, 
                PHONE_NBR, POSTAL_CNTRY_CD, DFLT_IND )
   (SELECT kr_kim_entity_phone_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'PERSON', 'WORK',
        u.PRSN_LOC_PHN_NBR, 'US', 'Y'
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_LOC_PHN_NBR IS NOT NULL
   )
/
COMMIT
/

-- address (only if not null)

INSERT INTO KR_KIM_ENTITY_ADDR_T ( ENTITY_ADDR_ID, OBJ_ID, ENTITY_ID, ENT_TYP_CD, ADDR_TYPE_CD, 
                ADDR_LINE_1, POSTAL_CNTRY_CD, DFLT_IND )
   (SELECT kr_kim_entity_addr_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'PERSON', 'WORK',
        u.PRSN_CMP_ADDR, 'US', 'Y'
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_CMP_ADDR IS NOT NULL
   )
/
COMMIT
/

-- external identifiers

INSERT INTO KR_KIM_ENTITY_EXT_ID_T ( ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID )
   (SELECT kr_kim_entity_ext_id_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'EMPLOYEE', u.PRSN_PYRL_ID
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_PYRL_ID IS NOT NULL
   )
/
INSERT INTO KR_KIM_ENTITY_EXT_ID_T ( ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID )
   (SELECT kr_kim_entity_ext_id_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'SSN', u.PRSN_TAX_ID
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_TAX_ID IS NOT NULL
          AND u.PRSN_TAX_ID_TYP_CD = 'S'
   )
/
INSERT INTO KR_KIM_ENTITY_EXT_ID_T ( ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID )
   (SELECT kr_kim_entity_ext_id_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'TAX', u.PRSN_TAX_ID
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_TAX_ID IS NOT NULL
          AND u.PRSN_TAX_ID_TYP_CD = 'T'
   )
/
COMMIT
/

-- affiliations

INSERT INTO KR_KIM_ENTITY_AFLTN_T ( ENTITY_AFLTN_ID, OBJ_ID, ENTITY_ID, AFLTN_TYP_CD, CAMPUS_CD, DFLT_IND )
   (SELECT kr_kim_entity_afltn_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'STAFF', u.CAMPUS_CD, 'Y'
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_STAFF_IND = 'Y'
   )
/
INSERT INTO KR_KIM_ENTITY_AFLTN_T ( ENTITY_AFLTN_ID, OBJ_ID, ENTITY_ID, AFLTN_TYP_CD, CAMPUS_CD, DFLT_IND )
   (SELECT kr_kim_entity_afltn_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'FACULTY', u.CAMPUS_CD, 'N'
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_FAC_IND = 'Y'
   )
/
INSERT INTO KR_KIM_ENTITY_AFLTN_T ( ENTITY_AFLTN_ID, OBJ_ID, ENTITY_ID, AFLTN_TYP_CD, CAMPUS_CD, DFLT_IND )
   (SELECT kr_kim_entity_afltn_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'STUDENT', u.CAMPUS_CD, 'N'
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_STU_IND = 'Y'
   )
/
INSERT INTO KR_KIM_ENTITY_AFLTN_T ( ENTITY_AFLTN_ID, OBJ_ID, ENTITY_ID, AFLTN_TYP_CD, CAMPUS_CD, DFLT_IND )
   (SELECT kr_kim_entity_afltn_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, 'AFFILIATE', u.CAMPUS_CD, 'N'
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_AFLT_IND = 'Y'
   )
/
COMMIT
/


-- employment information

INSERT INTO KR_KIM_ENTITY_EMP_INFO_T ( ENTITY_EMP_ID, OBJ_ID, ENTITY_ID, ENTITY_AFLTN_ID, EMP_STAT_CD, EMP_TYP_CD, BASE_SLRY_AMT, PRMRY_IND)
   (SELECT kr_kim_entity_emp_id_seq.NEXTVAL, SYS_GUID(), p.entity_id, a.ENTITY_AFLTN_ID, u.EMP_STAT_CD, u.EMP_TYPE_CD, u.PRSN_BASE_SLRY_AMT, 'Y'
        FROM KR_KIM_PRINCIPAL_T p, FS_UNIVERSAL_USR_T u, KR_KIM_ENTITY_AFLTN_T a
        WHERE u.PERSON_UNVL_ID = p.PRNCPL_ID
          AND u.PRSN_STAFF_IND = 'Y'
          AND a.ENTITY_ID = p.ENTITY_ID
          AND a.AFLTN_TYP_CD = 'STAFF'
   )
/
COMMIT
/

