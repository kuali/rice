INSERT INTO KRCR_PARM_T (APPL_ID, NMSPC_CD, CMPNT_CD, PARM_NM, VAL, PARM_DESC_TXT, PARM_TYP_CD, EVAL_OPRTR_CD, OBJ_ID, VER_NBR)
    VALUES ('KUALI', 'KR-KRAD', 'All', 'AUTO_TRUNCATE_COLUMNS', 'N', 'Automatically truncate text that does not fit into table columns.  A tooltip with the non-trucated text on hover over.', 'CONFG', 'A', SYS_GUID(), 1)
/
INSERT INTO KRCR_PARM_T (APPL_ID, NMSPC_CD, CMPNT_CD, PARM_NM, VAL, PARM_DESC_TXT, PARM_TYP_CD, EVAL_OPRTR_CD, OBJ_ID, VER_NBR)
    VALUES ('KUALI', 'KR-KRAD', 'Lookup', 'AUTO_TRUNCATE_COLUMNS', 'N', 'Automatically truncate text that does not fit into lookup result columns.  A tooltip with the non-trucated text on hover over.', 'CONFG', 'A', SYS_GUID(), 1)
/