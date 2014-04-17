


-- ===================================================================================
-- 2014-04-09--KULRICE-12444.sql (MySQL)
-- https://jira.kuali.org/browse/KULRICE-12444
-- ===================================================================================


-- Create a new table for holding attachment content which can be used in place of storing them on the filesystem
CREATE TABLE krns_att_cntnt_t (att_id VARCHAR(36), att_cntnt BLOB)
/
