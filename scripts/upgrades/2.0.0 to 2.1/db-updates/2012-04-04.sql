-- KULRICE-6784 Add index and constraint on KREW_RULE_ATTR_T.NM
alter table KREW_RULE_ATTR_T add constraint KREW_RULE_ATTR_TC1 unique(NM)
/