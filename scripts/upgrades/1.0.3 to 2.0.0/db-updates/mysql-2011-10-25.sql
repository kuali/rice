update KREW_RULE_ATTR_T set RULE_ATTR_TYP_CD='DocumentSecurityAttribute' where RULE_ATTR_TYP_CD='DocumentSearchSecurityFilterAttribute';

update KRCR_PARM_T set CMPNT_CD='DocumentSearch' where CMPNT_CD='DocSearchCriteriaDTO';
insert into KRCR_CMPNT_T (NMSPC_CD, CMPNT_CD, NM, ACTV_IND, OBJ_ID, VER_NBR)
values ('KR-WKFLW', 'DocumentSearch', 'Document Search', 'Y', uuid(), 1);
