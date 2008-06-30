-- Default Attribute Types --
INSERT INTO KIM_ATTRIBUTE_TYPES_T (ID, NAME, DESCRIPTION) values (1, 'Text', 'This attribute type is for general text fields.')
/
INSERT INTO KIM_ATTRIBUTE_TYPES_T (ID, NAME, DESCRIPTION) values (2, 'Phone Number', 'This attribute type is for phone number fields.')
/
INSERT INTO KIM_ATTRIBUTE_TYPES_T (ID, NAME, DESCRIPTION) values (3, 'Email Address', 'This attribute type is for email address fields.')
/

-- Default Namespaces --
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (1, 'KIM', 'This record represents the actual KIM system and must always be loaded by default in order for the system to work properly.')
/
INSERT INTO KIM_NAMESPACES_T (ID, NAME, DESCRIPTION) VALUES (2, 'Global', 'This record represents the global shared namespace that should house entity attributes and permissions global across all namespaces and not specific to any one namespace.')
/
INSERT INTO KIM_NAMESPACE_DFLT_ATTRIBS_T (ID, NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, DESCRIPTION, REQUIRED, ACTIVE) values (1, 2, 'First Name', 1, 'This is the first name for this entity.', 'Y', 'Y')
/
INSERT INTO KIM_NAMESPACE_DFLT_ATTRIBS_T (ID, NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, DESCRIPTION, REQUIRED, ACTIVE) values (2, 2, 'Last Name', 1, 'This is the last name for this entity.', 'Y', 'Y')
/
INSERT INTO KIM_NAMESPACE_DFLT_ATTRIBS_T (ID, NAMESPACE_ID, ATTRIBUTE_NAME, ATTRIBUTE_TYPE_ID, DESCRIPTION, REQUIRED, ACTIVE) values (3, 2, 'Email Address', 3, 'This is the email address for this entity.', 'Y', 'Y')
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
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KNSD', 'KIM NAMESPACE', 'Y')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KPMD', 'KIM PRINCIPAL', 'Y')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KGMD', 'KIM GROUP', 'Y')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KRMD', 'KIM ROLE', 'Y')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KATD', 'KIM ATTRIBUTE TYPE', 'Y')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KETM', 'KIM ENTITY TYPE', 'Y')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KEMD', 'ENTITY', 'Y')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KPRD', 'KIM PERMISSION', 'Y')
/
INSERT INTO FP_DOC_TYPE_T (FDOC_TYP_CD, FDOC_NM, FDOC_TYP_ACTIVE_CD) VALUES ('KGTM', 'KIM GROUP TYPE', 'Y')
/