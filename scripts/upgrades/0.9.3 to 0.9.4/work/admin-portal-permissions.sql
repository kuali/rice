-- give those in the KR-SYS Technical Role the ability to see the admin section in the KEW portal

INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KR-WKFLW',sys_guid(),'832','29',1)
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('880', sys_guid(), 1, '832', '12', '4', 'KR-WKFLW')
/
INSERT INTO KRIM_PERM_ATTR_DATA_T(ATTR_DATA_ID, OBJ_ID, VER_NBR, TARGET_PRIMARY_KEY, KIM_TYP_ID, KIM_ATTR_DEFN_ID, ATTR_VAL)
  VALUES('881', sys_guid(), 1, '832', '12', '2', 'org.kuali.rice.kew.web.backdoor.AdministrationAction')
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('837', sys_guid(), 1, '63', '832', 'Y')
/