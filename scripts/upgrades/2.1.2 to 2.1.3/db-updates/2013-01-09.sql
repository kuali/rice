

-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
--
-- NOTE: Please do *not* apply this to Rice's master database, this is redundant to a statement
--       that has been added to bootstrap-server-dataset-cleanup.sql
--
-- !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

-- delete the assignment of the recall from routing permission for KULRICE-7687
delete from krim_role_perm_t where
  role_id = (select role_id from krim_role_t where nmspc_cd = 'KR-WKFLW' and role_nm = 'Initiator') and
  perm_id = (select PERM_ID from krim_perm_t where nmspc_cd = 'KR-WKFLW' and nm = 'Recall Document')
/