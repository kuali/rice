-- Create a new table for holding attachment content which can be used in place of storing them on the filesystem
CREATE TABLE krns_att_cntnt_t (att_id VARCHAR2(36), att_cntnt BLOB)
/
