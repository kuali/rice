-- KULRICE-11052 - Adding a static date to prevent impex from updating it every time

UPDATE KRIM_ROLE_T SET LAST_UPDT_DT = STR_TO_DATE( '20121128143720', '%Y%m%d%H%i%s' ) WHERE ROLE_ID = 'KR1001'
/
