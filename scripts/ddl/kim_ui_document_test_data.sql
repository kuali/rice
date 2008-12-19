-- these data are not in master db, so may have to run it if db is refreshed.
-- this is tried for testing person document in kulcnv

-- 1. ingest IdentituManagementPersonDocument.xml 

-- 2. run kim_document_tables.sql to create pending tables

-- set up title and suffix drop downs as sys params
INSERT INTO KRNS_PARM_T(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, GRP_NM)  VALUES('KR-IDM', 'EntityNameImpl', 'PREFIXES', sys_guid(), 1, 'CONFG',  'Ms;Mrs;Mr;Dr', '','A', 'WorkflowAdmin')
/
INSERT INTO KRNS_PARM_T(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, GRP_NM)  VALUES('KR-IDM', 'EntityNameImpl', 'SUFFIXES', sys_guid(), 1, 'CONFG', 'Jr;Sr;Mr;Md', '','A', 'WorkflowAdmin')
/



-- set up initiate document perm
INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KR-SYS',SYS_GUID(),'501','10',1)
/
insert into KRIM_ROLE_MBR_T (ROLE_MBR_ID, OBJ_ID ,VER_NBR, ROLE_ID, MBR_ID, MBR_TYP_CD  )
  values (2001,2001,1,1,'6162502038','P');
insert into KRIM_ROLE_PERM_T (ROLE_PERM_ID, OBJ_ID ,VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  values (501,501,1,1,501,'Y');

-- set up documentTypeName
insert into KRIM_PERM_ATTR_DATA_T (ATTR_DATA_ID, OBJ_ID,VER_NBR,TARGET_PRIMARY_KEY, KIM_TYP_ID,KIM_ATTR_DEFN_ID, ATTR_VAL)     
 values (501,501,1,501,3,13,'IdentityManagementPersonDocument');


-- set up modify entity perm
-- it looks like khuntley already connect to role 45 which is assigned to role44
insert into KRIM_ROLE_MBR_T (ROLE_MBR_ID, OBJ_ID ,VER_NBR, ROLE_ID, MBR_ID, MBR_TYP_CD  )
  values (2002,2002,1,44,'6162502038','P');

