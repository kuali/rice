
--
-- KULRICE-10175: implementation for allowing application modules to send notifications with custom doc types
--
-- Adding optional document type name field
--

ALTER TABLE KREN_NTFCTN_T ADD DOC_TYP_NM VARCHAR2(64)
/