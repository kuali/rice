-- This index improves performance of KFS queries which need to fetch entities by employee ID
CREATE INDEX KRIM_ENTITY_EMP_INFO_TI3 ON KRIM_ENTITY_EMP_INFO_T(EMP_ID)
/
