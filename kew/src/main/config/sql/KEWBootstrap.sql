insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Feature.CheckRouteLogAuthentication.CheckFuture', 'true', 1)
/
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('RouteQueue.maxRetryAttempts', '0', 1)
/
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('BAM', 'true', 1)
/
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Security.HttpInvoker.SignMessages', 'true', 1)
/
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Workflow.AdminWorkgroup', 'WorkflowAdmin', 1)
/
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Routing.ImmediateExceptionRouting', 'true', 1)
/
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Workgroup.IsRouteLogPopup', 'false', 0)
/
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('DocumentType.IsRouteLogPopup', 'false', 0)
/
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('DocumentSearch.IsRouteLogPopup', 'true', 0)
/
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('DocumentSearch.IsDocumentPopup', 'true', 0)
/
insert into EN_APPL_CNST_T (APPL_CNST_NM, APPL_CNST_VAL_TXT, DB_LOCK_VER_NBR) values ('Config.Backdoor.TargetFrameName', 'iframe_51148', 0)
/

insert into en_usr_t (PRSN_EN_ID, PRSN_UNIV_ID, PRSN_NTWRK_ID, PRSN_UNVL_USR_ID, PRSN_EMAIL_ADDR, PRSN_NM, PRSN_GVN_NM, PRSN_LST_NM, USR_CRTE_DT, USR_LST_UPDT_DT, DB_LOCK_VER_NBR) values ('admin','admin','admin','admin','admin@localhost','admin','admin','admin',now(),now(),0)
/
insert into en_wrkgrp_t (WRKGRP_ID, WRKGRP_VER_NBR, WRKGRP_NM, WRKGRP_ACTV_IND, WRKGRP_TYP_CD, WRKGRP_DESC, WRKGRP_CUR_IND, DOC_HDR_ID, DB_LOCK_VER_NBR) values (1,1,'WorkflowAdmin',1,'W','Workflow Administrator Workgroup',1,null,0)
/
insert into EN_WRKGRP_MBR_T (WRKGRP_MBR_PRSN_EN_ID, WRKGRP_ID, WRKGRP_MBR_TYP, WRKGRP_VER_NBR, DB_LOCK_VER_NBR) values ('admin', 1, 'U', 1, 0)
/
