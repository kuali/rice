
--
-- KULRICE-7439: Assignment of "Add Message to Route Log" permission to the KR-SYS technical administrator is missing from bootstrap dataset
--

delete from krim_role_perm_t where
role_id = (select role_id from krim_role_t where role_nm = 'Technical Administrator' and nmspc_cd = 'KR-SYS') AND
perm_id = (select perm_id from krim_perm_t where nm = 'Add Message to Route Log' and nmspc_cd = 'KUALI')
;
