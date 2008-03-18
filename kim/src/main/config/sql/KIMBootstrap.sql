INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (1, 'KIM', 'This record represents the actual KIM system and must always be loaded by default in order for the system to work properly.')
/
INSERT INTO KIM_ENTITY_TYPES_T values (1, 'Person', 'This entity represents a person in the system.', SYS_GUID(), 1)
/
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