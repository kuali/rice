-- KULRICE-9086 - Adding a parameter for the maximum number of rows that will be displayed in the lookup results

INSERT INTO KRCR_PARM_T (OBJ_ID, NMSPC_CD, CMPNT_CD, PARM_NM, PARM_TYP_CD, VAL, PARM_DESC_TXT, EVAL_OPRTR_CD, APPL_ID)
    SELECT SYS_GUID(), 'KR-KRAD', CMPNT_CD, PARM_NM, PARM_TYP_CD, VAL, PARM_DESC_TXT, EVAL_OPRTR_CD, APPL_ID
      FROM KRCR_PARM_T
     WHERE NMSPC_CD = 'KR-NS'
       AND CMPNT_CD = 'Lookup'
       AND PARM_NM = 'MULTIPLE_VALUE_RESULTS_PER_PAGE'
       AND NOT EXISTS (SELECT '1' FROM KRCR_PARM_T
                        WHERE NMSPC_CD = 'KR-KRAD'
                          AND CMPNT_CD = 'Lookup'
                          AND PARM_NM = 'MULTIPLE_VALUE_RESULTS_PER_PAGE')
/