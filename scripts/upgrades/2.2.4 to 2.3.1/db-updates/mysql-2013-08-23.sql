
--
-- KULRICE-9887: KRMS Attribute with name 'peopleFlowName' has invalid namespace
--

UPDATE krms_attr_defn_t SET nmspc_cd = 'KR-RULE' WHERE nm = 'peopleFlowName' AND nmspc_cd = 'KR_RULE'
;