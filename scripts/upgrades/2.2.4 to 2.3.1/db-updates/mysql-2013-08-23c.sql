
--
-- KULRICE-9142: Modify the existing Recall permission to apply to RiceDocument
--

UPDATE krim_perm_attr_data_t SET attr_val='RiceDocument'
WHERE attr_val = '*' AND perm_id =
  (
    SELECT perm_id FROM krim_perm_t WHERE nm='Recall Document' AND nmspc_cd='KR-WKFLW'
  )
;