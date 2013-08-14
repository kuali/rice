--
-- KULRICE-9034: KR-KRAD - RESULTS_LIMIT parameter should be added and the code should be changed to use it
--

INSERT INTO KRCR_PARM_T
  (NMSPC_CD, CMPNT_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, VAL, PARM_DESC_TXT, EVAL_OPRTR_CD, APPL_ID)
  VALUES ('KR-KRAD', 'Lookup', 'RESULTS_LIMIT', SYS_GUID(), '200', 'CONFG', '1',
          'Maximum number of results returned in a look-up query.', 'A', 'KUALI')
/