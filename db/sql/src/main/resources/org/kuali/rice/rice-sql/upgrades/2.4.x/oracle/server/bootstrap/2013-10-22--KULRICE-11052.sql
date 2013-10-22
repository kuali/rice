-- KULRICE-11052 - Adding a static date to prevent impex from updating it every time

UPDATE KRIM_ROLE_T SET LAST_UPDT_DT = TO_DATE( '20121128143720', 'YYYYMMDDHH24MISS' ) WHERE ROLE_ID = 'KR1001'
/
