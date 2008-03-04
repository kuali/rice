INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (1, 'KIM', 'This record represents the actual KIM system and must always be loaded by default in order for the system to work properly.')
/
INSERT INTO KIM_ENTITY_TYPES_T values (1, 'Person', 'This entity represents a person in the system.', SYS_GUID(), 1)
/
INSERT INTO FP_DOC_TYPE_T values ('KNSD', SYS_GUID(), 1, 'KR', 'NAMESPACE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KPMD', SYS_GUID(), 1, 'KR', 'PRINCIPAL', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KGMD', SYS_GUID(), 1, 'KR', 'GROUP', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KRMD', SYS_GUID(), 1, 'KR', 'ROLE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KGAM', SYS_GUID(), 1, 'KR', 'GROUP ATTRIBUTE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KATD', SYS_GUID(), 1, 'KR', 'ATTRIBUTE TYPE', 'N', 'Y', 'N', 0, 'N', 'N')
/
INSERT INTO FP_DOC_TYPE_T values ('KETM', SYS_GUID(), 1, 'KR', 'ENTITY TYPE', 'N', 'Y', 'N', 0, 'N', 'N')
/