-- Default Namespaces --
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (1, 'KIM', 'This record represents the actual KIM system and must always be loaded by default in order for the system to work properly.')
/
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (2, 'Global', 'This record represents the global shared namespace that should house entity attributes and permissions global across all namespaces and not specific to any one namespace.')
/

-- Default Entity Types --
INSERT INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (1, 'Person', 'This entity type represents a person.')
/
INSERT INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (2, 'System', 'This entity type represents another system.')
/
INSERT INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (3, 'Service', 'This entity type represents a service.')
/
INSERT INTO KIM_ENTITY_TYPES_T (ID, NAME, DESCRIPTION) values (4, 'Process', 'This entity type represents a process.')
/

-- Default Group Types --
INSERT INTO KIM_GROUP_TYPES_T (ID, NAME, DESCRIPTION, WORKFLOW_DOCUMENT_TYPE) values (1, 'Default', 'This is the standard group type that most groups default to.', 'KIMGroupMaintenanceDocument')
/

-- Required By KNS for Maint. Docs - these can go away once the 0.9.3 KNS extraction tasks are finished --
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('KNSD', 'KR', 'KIM NAMESPACE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('KPMD', 'KR', 'KIM PRINCIPAL', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('KGMD', 'KR', 'KIM GROUP', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('KRMD', 'KR', 'KIM ROLE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('KATD', 'KR', 'KIM ATTRIBUTE TYPE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('KETM', 'KR', 'KIM ENTITY TYPE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('KEMD', 'KR', 'ENTITY', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('KPRD', 'KR', 'KIM PERMISSION', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_GRP_CD, FDOC_NM, FIN_ELIM_ELGBL_CD, FDOC_TYP_ACTIVE_CD, FDOC_RTNG_RULE_CD, FDOC_AUTOAPRV_DAYS, FDOC_BALANCED_CD, TRN_SCRBBR_OFST_GEN_IND) values ('KGTM', 'KR', 'KIM GROUP TYPE', 'N', 'Y', 'N', 0, 'N', 'N')
/