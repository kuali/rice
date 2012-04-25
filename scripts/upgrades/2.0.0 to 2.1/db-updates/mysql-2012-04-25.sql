update krms_attr_defn_t set nm='actionTypeCode' where attr_defn_id=1004;
update krms_attr_defn_t set nm='actionMessage' where attr_defn_id=1005;
update krms_attr_defn_t set nm='ruleTypeCode' where attr_defn_id=1001;

delete from krms_typ_attr_t where ATTR_DEFN_ID = 1002;
delete from krms_typ_attr_t where ATTR_DEFN_ID = 1003;
delete from krms_attr_defn_t where ATTR_DEFN_ID = 1002;
delete from krms_attr_defn_t where ATTR_DEFN_ID = 1003;