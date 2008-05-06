-- Default Namespaces --
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (1, 'KIM', 'This record represents the actual KIM system and must always be loaded by default in order for the system to work properly.')
/
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (2, 'Global', 'This record represents the global shared namespace that should house entity attributes and permissions global across all namespaces and not specific to any one namespace.')
/

-- Default Entity Types --
INSERT INTO KIM_ENTITY_TYPES_T values (1, 'Person', 'This entity type represents a person.', SYS_GUID(), 1)
/
INSERT INTO KIM_ENTITY_TYPES_T values (2, 'System', 'This entity type represents another system.', SYS_GUID(), 1)
/
INSERT INTO KIM_ENTITY_TYPES_T values (3, 'Service', 'This entity type represents a service.', SYS_GUID(), 1)
/
INSERT INTO KIM_ENTITY_TYPES_T values (4, 'Process', 'This entity type represents a process.', SYS_GUID(), 1)
/

-- Default Group Types --
INSERT INTO KIM_GROUP_TYPES_T values (1, 'Default', 'This is the standard group type that most groups default to.', 'KIMGroupMaintenanceDocument', SYS_GUID(), 1)
/

-- Required By KNS for Maint. Docs - these can go away once the 0.9.3 KNS extraction tasks are finished --
INSERT INTO FP_DOC_TYPE_T values ('KNSD', SYS_GUID(), 1, 'KR', 'KIM NAMESPACE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KPMD', SYS_GUID(), 1, 'KR', 'KIM PRINCIPAL', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KGMD', SYS_GUID(), 1, 'KR', 'KIM GROUP', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KRMD', SYS_GUID(), 1, 'KR', 'KIM ROLE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KATD', SYS_GUID(), 1, 'KR', 'KIM ATTRIBUTE TYPE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KETM', SYS_GUID(), 1, 'KR', 'KIM ENTITY TYPE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KEMD', SYS_GUID(), 1, 'KR', 'ENTITY', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KPRD', SYS_GUID(), 1, 'KR', 'KIM PERMISSION', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KGTM', SYS_GUID(), 1, 'KR', 'KIM GROUP TYPE', 'N', 'Y', 'N', 0, 'N', 'N')
/