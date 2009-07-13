
-- KULRICE-3126 This will turn off field level help by default.
UPDATE KRNS_PARM_T SET TXT = 'N' WHERE NMSPC_CD = 'KR-NS' AND PARM_DTL_TYP_CD = 'All' AND PARM_NM = 'ENABLE_FIELD_LEVEL_HELP_IND'