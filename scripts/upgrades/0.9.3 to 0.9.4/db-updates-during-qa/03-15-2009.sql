-- change namespace to KUALI on all groups with namespace of KFS from original EN_WRKGRP_T to KRIM_GRP_T conversion

UPDATE KRIM_GRP_T set NMSPC_CD='KUALI' where NMSPC_CD='KFS'
/