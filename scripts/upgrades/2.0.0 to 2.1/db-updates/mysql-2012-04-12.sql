-- create a Kim Type wired to the documentRouterRoleTypeService permission-derived role service
INSERT INTO KRIM_TYP_T (KIM_TYP_ID, OBJ_ID, VER_NBR, NM, SRVC_NM, ACTV_IND, NMSPC_CD) values ((select KIM_TYP_ID from (select (max(cast(KIM_TYP_ID as decimal)) + 1) as KIM_TYP_ID from KRIM_TYP_T where KIM_TYP_ID is not NULL and cast(KIM_TYP_ID as decimal) < 10000) as tmptable), uuid(), 1, 'Derived Role: Permission (Route Document)', 'documentRouterRoleTypeService', 'Y', 'KR-WKFLW')
/
-- define the Route Document derived role
INSERT INTO KRIM_ROLE_T (ROLE_ID, OBJ_ID, VER_NBR, ROLE_NM, NMSPC_CD, DESC_TXT, KIM_TYP_ID, ACTV_IND) values ((select ROLE_ID from (select (max(cast(ROLE_ID as decimal)) + 1) as ROLE_ID from KRIM_ROLE_T where ROLE_ID is not NULL and cast(ROLE_ID as decimal) < 10000) as tmptable), uuid(), 1, 'Document Router', 'KR-WKFLW', 'This role derives its members from users with the Route Document permission for a given document type.', (select KIM_TYP_ID from KRIM_TYP_T where NM = 'Derived Role: Permission (Route Document)' and NMSPC_CD = 'KR-WKFLW'), 'Y')
/
