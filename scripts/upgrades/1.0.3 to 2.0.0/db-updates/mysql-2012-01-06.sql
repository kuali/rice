-- KULRICE-6299: New DB index to improve action list performance
create index KREW_ACTN_ITM_TI6 on KREW_ACTN_ITM_T (DLGN_TYP, DLGN_PRNCPL_ID, DLGN_GRP_ID);