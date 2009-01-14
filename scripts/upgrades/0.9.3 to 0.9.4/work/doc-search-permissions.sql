INSERT INTO KRIM_PERM_TMPL_T (ACTV_IND,KIM_TYP_ID,NM,NMSPC_CD,OBJ_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','1','Unrestricted Document Search','KR-WKFLW',sys_guid(),'71',1)
/

-- "Unrestricted Document Search" restricted to KR-SYS Technical Administrators -- /

INSERT INTO KRIM_PERM_T (ACTV_IND,NMSPC_CD,OBJ_ID,PERM_ID,PERM_TMPL_ID,VER_NBR)
  VALUES ('Y','KR-WKFLW',sys_guid(),'831','71',1)
/
INSERT INTO KRIM_ROLE_PERM_T(ROLE_PERM_ID, OBJ_ID, VER_NBR, ROLE_ID, PERM_ID, ACTV_IND)
  VALUES('836', sys_guid(), 1, '63', '831', 'Y')
/
commit
/





