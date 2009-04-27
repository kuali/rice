INSERT INTO KRNS_PARM_T(
CONS_CD,
NMSPC_CD,
OBJ_ID,
PARM_DESC_TXT,
PARM_DTL_TYP_CD,
PARM_NM,
PARM_TYP_CD,TXT,VER_NBR)
VALUES
(
'A',
'KR-NS',
'5a5fbe94-846f-102c-8db0-c405cae621f3',
'A semi-colon delimted list of regular expressions that identify 
potentially sensitive data in strings.  These patterns will be matched 
against notes, document explanations, and routing annotations.',
'All',
'SENSITIVE_DATA_PATTERNS',
'CONFG',
'[0-9]{9};[0-9]{3}-[0-9]{2}-[0-9]{4}',
1
)