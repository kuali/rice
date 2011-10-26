insert into KRCR_CMPNT_T (NMSPC_CD, CMPNT_CD, NM, ACTV_IND, OBJ_ID, VER_NBR)
VALUES ('KR-WKFLW', 'Rule', 'Rule', 'Y', sys_guid(), 1)
/
update KRCR_CMPNT_T set cmpnt_cd='EDocLite' where cmpnt_cd like 'EDocLite%'
/