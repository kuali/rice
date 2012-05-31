-- KULRICE-3409 - Wildcards do not function properly on the Component search field of the Parameter lookup
DELETE FROM krns_parm_dtl_typ_t WHERE nmspc_cd = 'KR-WKFLW' AND parm_dtl_typ_cd IN ('Rule', 'RuleTemplate');
/
