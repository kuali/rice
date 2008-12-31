
INSERT INTO KRIM_ENTITY_T(ENTITY_ID, OBJ_ID, ACTV_IND) 
    VALUES('e1', 'e1', 'Y')
/

INSERT INTO KRIM_ENTITY_T(ENTITY_ID, OBJ_ID, ACTV_IND) 
    VALUES('e2', 'e2', 'Y')
/

INSERT INTO KRIM_ENTITY_T(ENTITY_ID, OBJ_ID, ACTV_IND) 
    VALUES('e3', 'e3', 'Y')
/

INSERT INTO KRIM_ENTITY_T(ENTITY_ID, OBJ_ID, ACTV_IND) 
    VALUES('e4', 'e4', 'Y')
/

INSERT INTO KRIM_ENTITY_T(ENTITY_ID, OBJ_ID, ACTV_IND) 
    VALUES('e5', 'e5', 'N')
/

INSERT INTO KRIM_ENTITY_T(ENTITY_ID, OBJ_ID, ACTV_IND) 
    VALUES('e6', 'e6', 'N')
/
commit
/

INSERT INTO KRIM_ENT_TYP_T(ENT_TYP_CD, OBJ_ID, NM, DISPLAY_SORT_CD, ACTV_IND) 
    VALUES('PERSON', 'et1', 'Person', 01, 'Y')
/
COMMIT
/

INSERT INTO KRIM_ENTITY_ENT_TYP_T(ENTITY_ENT_TYPE_ID, OBJ_ID, ENT_TYP_CD, ENTITY_ID, ACTV_IND) 
    VALUES('et1', 'et1', 'PERSON', 'e1', 'Y')
/

INSERT INTO KRIM_ENTITY_ENT_TYP_T(ENTITY_ENT_TYPE_ID, OBJ_ID, ENT_TYP_CD, ENTITY_ID, ACTV_IND) 
    VALUES('et2', 'et2', 'PERSON', 'e2', 'Y')
/

INSERT INTO KRIM_ENTITY_ENT_TYP_T(ENTITY_ENT_TYPE_ID, OBJ_ID, ENT_TYP_CD, ENTITY_ID, ACTV_IND) 
    VALUES('et3', 'et3', 'PERSON', 'e3', 'Y')
/

INSERT INTO KRIM_ENTITY_ENT_TYP_T(ENTITY_ENT_TYPE_ID, OBJ_ID, ENT_TYP_CD, ENTITY_ID, ACTV_IND) 
    VALUES('et4', 'et4', 'PERSON', 'e4', 'Y')
/
COMMIT
/

INSERT INTO KRIM_ENT_NM_TYP_T(ENT_NM_TYP_CD, OBJ_ID, NM, DISPLAY_SORT_CD, ACTV_IND, LAST_UPDT_DT) 
    VALUES('PREFERRED', 'etn1', 'Preferred Name', 01, 'Y', sysdate)
/
INSERT INTO KRIM_ENT_NM_TYP_T(ENT_NM_TYP_CD, OBJ_ID, NM, DISPLAY_SORT_CD, ACTV_IND, LAST_UPDT_DT) 
    VALUES('PRIMARY', 'etn2', 'Primary Name', 02, 'Y', sysdate)
/
COMMIT
/

INSERT INTO KRIM_ENTITY_NM_T(ENTITY_NM_ID, OBJ_ID, ENTITY_ID, NM_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, SUFFIX_NM, TITLE_NM, DFLT_IND, ACTV_IND, LAST_UPDT_DT) 
    VALUES('en1', 'en1', 'e1', 'PREFERRED', 'One', '', 'User', '', '', 'Y', 'Y', sysdate)
/
INSERT INTO KRIM_ENTITY_NM_T(ENTITY_NM_ID, OBJ_ID, ENTITY_ID, NM_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, SUFFIX_NM, TITLE_NM, DFLT_IND, ACTV_IND, LAST_UPDT_DT) 
    VALUES('en2', 'en2', 'e2', 'PREFERRED', 'Two', '', 'User', '', '', 'Y', 'Y', sysdate)
/
INSERT INTO KRIM_ENTITY_NM_T(ENTITY_NM_ID, OBJ_ID, ENTITY_ID, NM_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, SUFFIX_NM, TITLE_NM, DFLT_IND, ACTV_IND, LAST_UPDT_DT) 
    VALUES('en3', 'en3', 'e3', 'PREFERRED', 'Three', '', 'User', '', '', 'Y', 'Y', sysdate)
/
INSERT INTO KRIM_ENTITY_NM_T(ENTITY_NM_ID, OBJ_ID, ENTITY_ID, NM_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, SUFFIX_NM, TITLE_NM, DFLT_IND, ACTV_IND, LAST_UPDT_DT) 
    VALUES('en4', 'en4', 'e4', 'PREFERRED', 'Four', '', 'User', '', '', 'Y', 'Y', sysdate)
/
INSERT INTO KRIM_ENTITY_NM_T(ENTITY_NM_ID, OBJ_ID, ENTITY_ID, NM_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, SUFFIX_NM, TITLE_NM, DFLT_IND, ACTV_IND, LAST_UPDT_DT) 
    VALUES('en5', 'en5', 'e5', 'PREFERRED', 'Five', '', 'User', '', '', 'Y', 'N', sysdate)
/
INSERT INTO KRIM_ENTITY_NM_T(ENTITY_NM_ID, OBJ_ID, ENTITY_ID, NM_TYP_CD, FIRST_NM, MIDDLE_NM, LAST_NM, SUFFIX_NM, TITLE_NM, DFLT_IND, ACTV_IND, LAST_UPDT_DT) 
    VALUES('en6', 'en6', 'e6',  'PREFERRED', 'Six', '', 'User', '', '', 'Y', 'N', sysdate)
/
COMMIT
/

INSERT INTO KRIM_EXT_ID_TYP_T(EXT_ID_TYP_CD, OBJ_ID, NM, DISPLAY_SORT_CD, ENCR_REQ_IND, ACTV_IND, LAST_UPDT_DT) 
    VALUES('EMPLOYEE', 'extid1', 'Employee ID', 01, 'N', 'Y', sysdate)
/
INSERT INTO KRIM_EXT_ID_TYP_T(EXT_ID_TYP_CD, OBJ_ID, NM, DISPLAY_SORT_CD, ENCR_REQ_IND, ACTV_IND, LAST_UPDT_DT) 
    VALUES('TAX', 'extid2', 'Tax ID', 02, 'N', 'Y', sysdate)
/
INSERT INTO KRIM_EXT_ID_TYP_T(EXT_ID_TYP_CD, OBJ_ID, NM, DISPLAY_SORT_CD, ENCR_REQ_IND, ACTV_IND, LAST_UPDT_DT) 
    VALUES('LOGON', 'extid3', 'Logon ID', 03, 'N', 'Y', sysdate)
/
COMMIT
/

INSERT INTO KRIM_ENTITY_EXT_ID_T(ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID) 
    VALUES('eeid1', 'eeid1', 'e1', 'EMPLOYEE', 'EXTID1')
/
INSERT INTO KRIM_ENTITY_EXT_ID_T(ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID) 
    VALUES('eeid2', 'eeid2', 'e2', 'EMPLOYEE', 'EXTID2')
/
INSERT INTO KRIM_ENTITY_EXT_ID_T(ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID) 
    VALUES('eeid3', 'eeid3', 'e3', 'EMPLOYEE', 'EXTID3')
/
INSERT INTO KRIM_ENTITY_EXT_ID_T(ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID) 
    VALUES('eeid4', 'eeid4', 'e4', 'EMPLOYEE', 'EXTID$')
/
INSERT INTO KRIM_ENTITY_EXT_ID_T(ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID) 
    VALUES('eeid5', 'eeid5', 'e5', 'EMPLOYEE', 'EXTID$')
/
INSERT INTO KRIM_ENTITY_EXT_ID_T(ENTITY_EXT_ID_ID, OBJ_ID, ENTITY_ID, EXT_ID_TYP_CD, EXT_ID) 
    VALUES('eeid6', 'eeid6', 'e6', 'EMPLOYEE', 'EXTID$')
/
COMMIT
/

INSERT INTO KRIM_PRNCPL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p1', 'p1', 'user1', 'e1', 'Y')
/
INSERT INTO KRIM_PRNCPL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p2', 'p2', 'user2', 'e2', 'Y')
/
INSERT INTO KRIM_PRNCPL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p3', 'p3', 'user3', 'e3', 'Y')
/
INSERT INTO KRIM_PRNCPL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p41', 'p41', 'user41', 'e4', 'Y')
/
INSERT INTO KRIM_PRNCPL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p42', 'p42', 'user42', 'e4', 'Y')
/
INSERT INTO KRIM_PRNCPL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p4', 'p4', 'user4', 'e4', 'Y')
/
INSERT INTO KRIM_PRNCPL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p5', 'p5', 'user5', 'e5', 'Y')
/
INSERT INTO KRIM_PRNCPL_T(PRNCPL_ID, OBJ_ID, PRNCPL_NM, ENTITY_ID, ACTV_IND) 
    VALUES('p6', 'p6', 'user6', 'e6', 'Y')
/
COMMIT
/

INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, NM, SRVC_NM,  ACTV_IND, NMSPC_CD) 
    VALUES('roleType1', 'roleType1', 'kim type for r1', 'kimRoleTypeService',  'Y', 'KR-SYS')
/
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, NM, SRVC_NM,  ACTV_IND, NMSPC_CD) 
    VALUES('roleType2', 'roleType2', 'kim type for r2', 'kimRoleTypeService',  'Y', 'KR-SYS')
/
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, NM, SRVC_NM,  ACTV_IND, NMSPC_CD) 
    VALUES('permType1', 'permType1', 'kim type for permTmpl1', 'permTypeService',  'Y', 'KR-SYS')
/
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, NM, SRVC_NM,  ACTV_IND, NMSPC_CD) 
    VALUES('permType2', 'permType2', 'kim type for permTmpl2', 'permTypeService',  'Y', 'KR-SYS')
/
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, NM, SRVC_NM,  ACTV_IND, NMSPC_CD) 
    VALUES('permType3', 'permType3', 'kim type for permTmpl3', 'permTypeService',  'Y', 'KR-SYS')
/
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, NM, SRVC_NM,  ACTV_IND, NMSPC_CD) 
    VALUES('rspType1', 'rspType1', 'kim type for tspTmpl1', 'responsibilityTypeService',  'Y', 'KR-SYS')
/
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, NM, SRVC_NM,  ACTV_IND, NMSPC_CD) 
    VALUES('rspType2', 'rspType2', 'kim type for tspTmpl2', 'responsibilityTypeService',  'Y', 'KR-SYS')
/
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, NM, SRVC_NM,  ACTV_IND, NMSPC_CD) 
    VALUES('groupType1', 'groupType1', 'kim type for Group 1', 'groupTypeService',  'Y', 'KR-SYS')
/
INSERT INTO KRIM_TYP_T(KIM_TYP_ID, OBJ_ID, NM, SRVC_NM,  ACTV_IND, NMSPC_CD) 
    VALUES('groupType2', 'groupType2', 'kim type for Group 2', 'groupTypeService',  'Y', 'KR-SYS')
/
COMMIT 
/

INSERT INTO KRIM_ATTR_DEFN_T(KIM_ATTR_DEFN_ID, OBJ_ID, NM, LBL, NMSPC_CD,  ACTV_IND, CMPNT_NM, APPL_URL) 
    VALUES('kimAttrDefn1', 'kimAttrDefn1', 'attribute1', 'Attrib LBL 1', 'KR-SYS',  'Y','','')
/
INSERT INTO KRIM_ATTR_DEFN_T(KIM_ATTR_DEFN_ID, OBJ_ID, NM, LBL, NMSPC_CD,  ACTV_IND, CMPNT_NM, APPL_URL) 
    VALUES('kimAttrDefn2', 'kimAttrDefn2', 'campusCode', 'Attrib LBL 2', 'KR-SYS',  'Y','org.kuali.rice.kim.bo.impl.KimAttributes','')
/
INSERT INTO KRIM_ATTR_DEFN_T(KIM_ATTR_DEFN_ID, OBJ_ID, NM, LBL, NMSPC_CD,  ACTV_IND, CMPNT_NM, APPL_URL) 
    VALUES('kimAttrDefn3', 'kimAttrDefn3', 'namespaceCode', 'Attrib LBL 3', 'KR-SYS',  'Y','org.kuali.rice.kim.bo.impl.KimAttributes','')
/
INSERT INTO KRIM_ATTR_DEFN_T(KIM_ATTR_DEFN_ID, OBJ_ID, NM, LBL, NMSPC_CD,  ACTV_IND, CMPNT_NM, APPL_URL) 
    VALUES('kimAttrDefn4', 'kimAttrDefn4', 'attribute4', 'Attrib LBL 4', 'KR-SYS',  'Y','','')
/
INSERT INTO KRIM_ATTR_DEFN_T(KIM_ATTR_DEFN_ID, OBJ_ID, NM, LBL, NMSPC_CD,  ACTV_IND, CMPNT_NM, APPL_URL) 
    VALUES('kimAttrDefn5', 'kimAttrDefn5', 'attribute5', 'Attrib LBL 5', 'KR-SYS',  'Y','','')
/
COMMIT
/

INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, SORT_CD,  ACTV_IND) 
    VALUES('kimAttr1', 'kimAttr1', 'permType1', 'kimAttrDefn1', 'a',  'Y')
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, SORT_CD,  ACTV_IND) 
    VALUES('kimAttr2', 'kimAttr2', 'permType1', 'kimAttrDefn2', 'b',  'Y')
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, SORT_CD,  ACTV_IND) 
    VALUES('kimAttr3', 'kimAttr3', 'roleType1', 'kimAttrDefn2', 'a',  'Y')
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, SORT_CD,  ACTV_IND) 
    VALUES('kimAttr4', 'kimAttr4', 'roleType1', 'kimAttrDefn3', 'b',  'Y')
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, SORT_CD,  ACTV_IND) 
    VALUES('kimAttr5', 'kimAttr5', 'rspType1', 'kimAttrDefn3', 'a',  'Y')
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, SORT_CD,  ACTV_IND) 
    VALUES('kimAttr6', 'kimAttr6', 'rspType1', 'kimAttrDefn4', 'b',  'Y')
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, SORT_CD,  ACTV_IND) 
    VALUES('kimAttr7', 'kimAttr7', 'groupType1', 'kimAttrDefn4', 'a',  'Y')
/
INSERT INTO KRIM_TYP_ATTR_T(KIM_TYP_ATTR_ID, OBJ_ID, KIM_TYP_ID, KIM_ATTR_DEFN_ID, SORT_CD,  ACTV_IND) 
    VALUES('kimAttr8', 'kimAttr8', 'groupType1', 'kimAttrDefn5', 'b',  'Y')
/

COMMIT
/


INSERT INTO KRIM_GRP_T(GRP_ID, OBJ_ID, GRP_NM, NMSPC_CD, GRP_DESC, KIM_TYP_ID, ACTV_IND) 
    VALUES('g1', 'g1', 'topgroup', 'TEST', 'Top-level group', 'groupType1', 'Y')
/
INSERT INTO KRIM_GRP_T(GRP_ID, OBJ_ID, GRP_NM, NMSPC_CD, GRP_DESC, KIM_TYP_ID, ACTV_IND) 
    VALUES('g2', 'g2', 'middlegroup', 'TEST', 'middle-level group', 'groupType2', 'Y')
/
INSERT INTO KRIM_GRP_T(GRP_ID, OBJ_ID, GRP_NM, NMSPC_CD, GRP_DESC, KIM_TYP_ID, ACTV_IND) 
    VALUES('g3', 'g3', 'bottomgroup', 'TEST', 'Bottom-level group', 'groupType1', 'Y')
/
INSERT INTO KRIM_GRP_T(GRP_ID, OBJ_ID, GRP_NM, NMSPC_CD, GRP_DESC, KIM_TYP_ID, ACTV_IND) 
    VALUES('g4', 'g4', 'bottominactivegroup', 'TEST', 'Bottom-level group (inactive', 'groupType2', 'N')
/
COMMIT
/


INSERT INTO KRIM_GRP_MBR_T(GRP_MBR_ID, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('gg1', 'gg1', 'g1', 'g2', 'G',1)
/
INSERT INTO KRIM_GRP_MBR_T(GRP_MBR_ID, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('gg2', 'gg2', 'g2', 'g3', 'G',1)
/
INSERT INTO KRIM_GRP_MBR_T(GRP_MBR_ID, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('gg3', 'gg3', 'g2', 'g4', 'G',1)
/
INSERT INTO KRIM_GRP_MBR_T(GRP_MBR_ID, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('gp1', 'gp1', 'g2', 'p1', 'P',1)
/
INSERT INTO KRIM_GRP_MBR_T(GRP_MBR_ID, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('gp2', 'gp2', 'g3', 'p2', 'P',1)
/
INSERT INTO KRIM_GRP_MBR_T(GRP_MBR_ID, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('gp3', 'gp3', 'g3', 'p3', 'P',1)
/
INSERT INTO KRIM_GRP_MBR_T(GRP_MBR_ID, OBJ_ID, GRP_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('gp4', 'gp4', 'g4', 'p4', 'P',1)
/
COMMIT
/


INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND) 
    VALUES('r1', 'r1', 'poweruserrole', 'TEST', 'high level role that implies other roles', 'roleType1', 'Y')
/
INSERT INTO KRIM_ROLE_T(ROLE_ID, OBJ_ID, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND) 
    VALUES('r2', 'r2', 'generalrole', 'TEST', 'role granted to large number of users', 'roleType2', 'Y')
/

COMMIT
/


INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('rp1', 'rp1', 'r2', 'p3','P',1)
/
INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('rp2', 'rp2', 'r1', 'p1', 'P',1)
/

INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('rg1', 'rg1', 'r2', 'g3', 'G',1)
/
INSERT INTO KRIM_ROLE_MBR_T(ROLE_MBR_ID, OBJ_ID, ROLE_ID, MBR_ID, MBR_TYP_CD,VER_NBR) 
    VALUES('rg2', 'rg2', 'r1', 'g2', 'G',1)
/
COMMIT
/


INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('rmad1', 'rmad1', 'rp1', 'roleType1', 'kimAttrDefn1', 'PHYS')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('rmad2', 'rmad2', 'rp2', 'roleType1', 'kimAttrDefn2', 'CHEM')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('rmad3', 'rmad4', 'rg1', 'groupType1', 'kimAttrDefn4', 'ROLEMEMBERATTR4')
/
INSERT INTO KRIM_ROLE_MBR_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('rmad4', 'rmad5', 'rg2', 'groupType1', 'kimAttrDefn5', 'ROLEMEMBERATTR5')
/
COMMIT
/

INSERT INTO KRIM_GRP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('gad1', 'gad1', 'g1', 'groupType1', 'kimAttrDefn4', 'GROUPATTR1')
/
INSERT INTO KRIM_GRP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('gad2', 'gad2', 'g2', 'groupType1', 'kimAttrDefn5', 'GROUPATTR2')
/
COMMIT
/

INSERT INTO KRIM_PERM_TMPL_T(PERM_TMPL_ID, OBJ_ID, NM, nmspc_cd,  DESC_TXT, KIM_TYP_ID, ACTV_IND) 
    VALUES('permTmpl1', 'permTmpl1', 'Perm template name1', 'KFS', 'Perm template description1', 'permType1', 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T(PERM_TMPL_ID, OBJ_ID, NM, nmspc_cd,  DESC_TXT, KIM_TYP_ID, ACTV_IND) 
    VALUES('permTmpl2', 'permTmpl2', 'Perm template name2', 'KFS', 'Perm template description2', 'permType2', 'Y')
/
INSERT INTO KRIM_PERM_TMPL_T(PERM_TMPL_ID, OBJ_ID, NM, nmspc_cd,  DESC_TXT, KIM_TYP_ID, ACTV_IND) 
    VALUES('permTmpl3', 'permTmpl3', 'Perm template name3', 'KFS', 'Perm template description3', 'permType3', 'Y')
/
COMMIT
/


INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, PERM_TMPL_ID, nm, nmspc_cd,  DESC_TXT, ACTV_IND) 
    VALUES('perm1', 'perm1', 'permTmpl1', 'Permission One', 'KFS', NULL,  'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, PERM_TMPL_ID, nm, nmspc_cd,  DESC_TXT, ACTV_IND) 
    VALUES('perm2', 'perm2', 'permTmpl2', 'Permission Two', 'KFS', NULL, 'Y')
/
INSERT INTO KRIM_PERM_T(PERM_ID, OBJ_ID, PERM_TMPL_ID, nm, nmspc_cd,  DESC_TXT, ACTV_IND) 
    VALUES('perm3', 'perm3', 'permTmpl3', 'Permission Two', 'KFS', null, 'Y')
/
COMMIT
/

INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('rperm1', 'rperm1', 'r1', 'perm1', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('rperm2', 'rperm2', 'r1', 'perm2', 'Y')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, ROLE_ID, PERM_ID, ACTV_IND) 
    VALUES('rperm3', 'rperm3', 'r2', 'perm3', 'Y')
/
COMMIT
/

INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('pad1', 'pad1', 'perm1', 'permType1', 'kimAttrDefn1', 'PERMATTR1')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('pad2', 'pad2', 'perm2', 'permType2', 'kimAttrDefn2', 'PERMATTR2')
/
COMMIT
/

INSERT INTO KRIM_RSP_TMPL_T(RSP_TMPL_ID, OBJ_ID, nm, KIM_TYP_ID, DESC_TXT, nmspc_cd, ACTV_IND) 
    VALUES('rsptpl1', 'rsptpl1', 'Resp template 1', 'rspType1', 'desc1', 'KFS', 'Y')
/
INSERT INTO KRIM_RSP_TMPL_T(RSP_TMPL_ID, OBJ_ID, nm, KIM_TYP_ID, DESC_TXT, nmspc_cd, ACTV_IND) 
    VALUES('rsptpl2', 'rsptpl2', 'Resp template 2', 'rspType2', 'desc2', 'KFS', 'Y')
/
COMMIT
/

INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, RSP_TMPL_ID, nm, DESC_TXT, nmspc_cd, ACTV_IND) 
    VALUES('rsp1', 'rsp1', 'rsptpl1', 'Resp 1',  'desc1', 'KFS', 'Y')
/
INSERT INTO KRIM_RSP_T(RSP_ID, OBJ_ID, RSP_TMPL_ID, nm, DESC_TXT, nmspc_cd, ACTV_IND) 
    VALUES('rsp2', 'rsp2', 'rsptpl2', 'Resp 2',  'desc2', 'KFS', 'Y')
/
COMMIT
/

INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, ROLE_ID, RSP_ID,  ACTV_IND) 
    VALUES('rrsp1', 'rrsp1', 'r1', 'rsp1', 'Y')
/
INSERT INTO KRIM_ROLE_RSP_T(ROLE_RSP_ID, OBJ_ID, ROLE_ID, RSP_ID,  ACTV_IND) 
    VALUES('rrsp2', 'rrsp2', 'r2', 'rsp2', 'Y')
/
COMMIT
/

INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('rad1', 'rad1', 'rsp1', 'rspType1', 'kimAttrDefn3', 'RESPATTR1')
/
INSERT INTO KRIM_RSP_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL) 
    VALUES('rad2', 'rad2', 'rsp2', 'rspType1', 'kimAttrDefn4', 'RESPATTR2')
/
COMMIT
/


INSERT INTO  KRIM_PHONE_TYP_T (PHONE_TYP_CD, OBJ_ID,VER_NBR,PHONE_TYP_NM,ACTV_IND,DISPLAY_SORT_CD)
  values ('HM','ptp1', 1,'Home','Y','b')
/
INSERT INTO  KRIM_PHONE_TYP_T (PHONE_TYP_CD, OBJ_ID,VER_NBR,PHONE_TYP_NM,ACTV_IND,DISPLAY_SORT_CD)
  values ('WRK','ptp2', 1,'Work','Y','a')
/

INSERT INTO  KRIM_EMAIL_TYP_T (EMAIL_TYP_CD, OBJ_ID,VER_NBR,NM,ACTV_IND,DISPLAY_SORT_CD)
  values ('HM','etp1', 1,'Home','Y','b')
/

INSERT INTO  KRIM_EMAIL_TYP_T (EMAIL_TYP_CD, OBJ_ID,VER_NBR,NM,ACTV_IND,DISPLAY_SORT_CD)
  values ('WRK','etp2', 1,'Work','Y','a')
/

INSERT INTO  KRIM_ADDR_TYP_T (ADDR_TYP_CD, OBJ_ID,VER_NBR,NM,ACTV_IND,DISPLAY_SORT_CD)
  values ('HM','atp1', 1,'Home','Y','b')
/

INSERT INTO  KRIM_ADDR_TYP_T (ADDR_TYP_CD, OBJ_ID,VER_NBR,NM,ACTV_IND,DISPLAY_SORT_CD)
  values ('WRK','atp2', 1,'Work','Y','a')
/
INSERT INTO  KRIM_AFLTN_TYP_T (AFLTN_TYP_CD, OBJ_ID,VER_NBR,NM,ACTV_IND,DISPLAY_SORT_CD,EMP_AFLTN_TYP_IND)
  values ('AFLT','afltp1', 1,'Affiliate','Y','a','N')
/
INSERT INTO  KRIM_AFLTN_TYP_T (AFLTN_TYP_CD, OBJ_ID,VER_NBR,NM,ACTV_IND,DISPLAY_SORT_CD,EMP_AFLTN_TYP_IND)
  values ('FCLTY','afltp2', 1,'Faculty','Y','b','Y')
/
INSERT INTO  KRIM_EMP_STAT_T (EMP_STAT_CD, OBJ_ID,VER_NBR,NM,ACTV_IND,DISPLAY_SORT_CD)
  values ('A','esp1', 1,'Active','Y','a')
/
INSERT INTO  KRIM_EMP_STAT_T (EMP_STAT_CD, OBJ_ID,VER_NBR,NM,ACTV_IND,DISPLAY_SORT_CD)
  values ('P','esp2', 1,'Processing','Y','b')
/

INSERT INTO  KRIM_EMP_TYP_T (EMP_TYP_CD, OBJ_ID,VER_NBR,NM,ACTV_IND,DISPLAY_SORT_CD)
  values ('P','emtp1', 1,'Professional','Y','a')
/
