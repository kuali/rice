CREATE INDEX krim_role_mbr_attr_data_ti1 ON krim_role_mbr_attr_data_t (role_mbr_id)
/
CREATE INDEX krim_role_mbr_ti1 ON krim_role_mbr_t (mbr_id)
/
CREATE INDEX krim_entity_addr_ti1 ON krim_entity_addr_t (entity_id)
/
CREATE INDEX krim_entity_email_ti1 ON krim_entity_email_t (entity_id)
/
CREATE INDEX krew_rte_node_cfg_parm_ti1 ON krew_rte_node_cfg_parm_t (rte_node_id)
/
CREATE INDEX krim_entity_phone_ti1 ON krim_entity_phone_t (entity_id)
/
CREATE INDEX krim_grp_mbr_ti1 ON krim_grp_mbr_t (mbr_id)
/

CREATE INDEX krim_entity_afltn_ti1 ON krim_entity_afltn_t (entity_id)
/
CREATE INDEX krim_entity_emp_info_ti1 ON krim_entity_emp_info_t (entity_id)
/
CREATE INDEX krim_entity_emp_info_ti2 ON krim_entity_emp_info_t (entity_afltn_id)
/
CREATE INDEX krim_entity_ent_typ_ti1 ON krim_entity_ent_typ_t (entity_id)
/
CREATE INDEX krim_entity_ext_id_ti1 ON krim_entity_ext_id_t (entity_id)
/
CREATE INDEX krim_entity_nm_ti1 ON krim_entity_nm_t (entity_id)
/
CREATE INDEX krim_perm_attr_data_ti1 ON krim_perm_attr_data_t (perm_id)
/
CREATE INDEX krim_role_perm_ti1 ON krim_role_perm_t (perm_id)
/
CREATE INDEX krim_role_rsp_ti1 ON krim_role_rsp_t (rsp_id)
/
CREATE INDEX krim_typ_attribute_ti1 ON krim_typ_attr_t (kim_typ_id) 
/