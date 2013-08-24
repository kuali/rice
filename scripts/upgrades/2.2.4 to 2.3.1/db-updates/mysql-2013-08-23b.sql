
--
-- KULRICE-10251: Maintain KRMS Agenda permission has confusing and unused permission detail
--

DELETE FROM krim_perm_attr_data_t
WHERE attr_val = 'KRMS_TEST' AND perm_id =
  (
    SELECT
      perm_id
    FROM krim_perm_t
    WHERE nm = 'Maintain KRMS Agenda' AND nmspc_cd = 'KR-RULE-TEST'
  )
;
