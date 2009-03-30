-- change namespace to KUALI on all groups with namespace of KFS from original EN_WRKGRP_T to KRIM_GRP_T conversion

UPDATE KRIM_GRP_T set NMSPC_CD='KUALI' where NMSPC_CD='KFS'
/

alter table KRNS_ATT_T modify MIME_TYP VARCHAR2(150)
/

DROP TABLE KRNS_DOC_TYP_ATTR_T
/