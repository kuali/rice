-- kr_kim_entity_ent_type_t 

ALTER TABLE kr_kim_entity_ent_type_t ADD CONSTRAINT kr_kim_entity_ent_type_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_ent_type_t ADD CONSTRAINT kr_kim_entity_ent_type_tr2
	FOREIGN KEY (ent_typ_cd)
	REFERENCES kr_kim_ent_type_t
/

-- kr_kim_entity_name_t

ALTER TABLE kr_kim_entity_name_t ADD CONSTRAINT kr_kim_entity_name_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/

ALTER TABLE kr_kim_entity_name_t ADD CONSTRAINT kr_kim_entity_name_tr2
	FOREIGN KEY (name_typ_cd)
	REFERENCES kr_kim_ent_name_type_t
/

-- kr_kim_entity_bio_t

/* commented out because of the way that OJB seems to handle 1:1 relationships
   - it attempts to persist the child entry first 
ALTER TABLE kr_kim_entity_bio_t ADD CONSTRAINT kr_kim_entity_bio_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
 */

-- kr_kim_entity_priv_pref_t

/* commented out because of the way that OJB seems to handle 1:1 relationships
   - it attempts to persist the child entry first 
ALTER TABLE kr_kim_entity_priv_pref_t ADD CONSTRAINT kr_kim_entity_priv_pref_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
 */

-- kr_kim_entity_ext_key_t

ALTER TABLE kr_kim_entity_ext_id_t ADD CONSTRAINT kr_kim_entity_ext_id_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_ext_id_t ADD CONSTRAINT kr_kim_entity_ext_id_tr2
	FOREIGN KEY (ext_id_typ_cd)
	REFERENCES kr_kim_ext_id_type_t
/

-- kr_kim_entity_addr_t

ALTER TABLE kr_kim_entity_addr_t ADD CONSTRAINT kr_kim_entity_addr_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_addr_t ADD CONSTRAINT kr_kim_entity_addr_tr2
	FOREIGN KEY (addr_type_cd)
	REFERENCES kr_kim_addr_type_t
/

-- kr_kim_entity_email_t

ALTER TABLE kr_kim_entity_email_t ADD CONSTRAINT kr_kim_entity_email_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_email_t ADD CONSTRAINT kr_kim_entity_email_tr2
	FOREIGN KEY (email_typ_cd)
	REFERENCES kr_kim_email_type_t
/

-- kr_kim_entity_phone_t

ALTER TABLE kr_kim_entity_phone_t ADD CONSTRAINT kr_kim_entity_phone_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_phone_t ADD CONSTRAINT kr_kim_entity_phone_tr2
	FOREIGN KEY (phone_typ_cd)
	REFERENCES kr_kim_phone_type_t
/


-- kr_kim_entity_principal_t

ALTER TABLE kr_kim_principal_t ADD CONSTRAINT kr_kim_principal_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/

-- kr_kim_entity_afltn_t

ALTER TABLE kr_kim_entity_afltn_t ADD CONSTRAINT kr_kim_entity_afltn_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/

ALTER TABLE kr_kim_entity_afltn_t ADD CONSTRAINT kr_kim_entity_afltn_tr2
	FOREIGN KEY ( afltn_typ_cd )
	REFERENCES kr_kim_afltn_type_t
/

-- kr_kim_entity_ctznshp_t

ALTER TABLE kr_kim_entity_ctznshp_t ADD CONSTRAINT kr_kim_entity_ctznshp_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/

ALTER TABLE kr_kim_entity_ctznshp_t ADD CONSTRAINT kr_kim_entity_ctznshp_tr2
	FOREIGN KEY ( ctznshp_stat_cd )
	REFERENCES kr_kim_ctznshp_stat_t
/

-- kr_kim_entity_emp_info_t

ALTER TABLE kr_kim_entity_emp_info_t ADD CONSTRAINT kr_kim_entity_emp_info_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_emp_info_t ADD CONSTRAINT kr_kim_entity_emp_info_tr2
	FOREIGN KEY ( emp_stat_cd )
	REFERENCES kr_kim_emp_stat_t
/
ALTER TABLE kr_kim_entity_emp_info_t ADD CONSTRAINT kr_kim_entity_emp_info_tr3
	FOREIGN KEY ( emp_typ_cd )
	REFERENCES kr_kim_emp_type_t
/
ALTER TABLE kr_kim_entity_emp_info_t ADD CONSTRAINT kr_kim_entity_emp_info_tr4
	FOREIGN KEY ( entity_afltn_id )
	REFERENCES kr_kim_entity_afltn_t
/

-- kr_kim_group_group_t


ALTER TABLE kr_kim_group_group_t ADD CONSTRAINT kr_kim_group_group_tr1
	FOREIGN KEY (grp_id)
	REFERENCES kr_kim_group_t
	ON DELETE CASCADE
/

ALTER TABLE kr_kim_group_group_t ADD CONSTRAINT kr_kim_group_group_tr2
	FOREIGN KEY (member_grp_id)
	REFERENCES kr_kim_group_t
	ON DELETE CASCADE
/

-- kr_kim_group_principal_t


ALTER TABLE kr_kim_group_principal_t ADD CONSTRAINT kr_kim_group_principal_tr1
	FOREIGN KEY (grp_id)
	REFERENCES kr_kim_group_t
	ON DELETE CASCADE
/

-- kr_kim_group_t

ALTER TABLE kr_kim_group_t ADD CONSTRAINT kr_kim_group_tr1
	FOREIGN KEY (typ_id)
	REFERENCES kr_kim_type_t
/

-- kr_kim_role_t

ALTER TABLE kr_kim_role_t ADD CONSTRAINT kr_kim_role_tr1
	FOREIGN KEY (typ_id)
	REFERENCES kr_kim_type_t
/

-- kr_kim_role_group_t

ALTER TABLE kr_kim_role_group_t ADD CONSTRAINT kr_kim_role_group_tr1
	FOREIGN KEY (role_id)
	REFERENCES kr_kim_role_t
	ON DELETE CASCADE
/

-- kr_kim_role_principal_t


ALTER TABLE kr_kim_role_principal_t ADD CONSTRAINT kr_kim_role_principal_tr1
	FOREIGN KEY (role_id)
	REFERENCES kr_kim_role_t
	ON DELETE CASCADE
/


-- kr_kim_role_rel_t

ALTER TABLE kr_kim_role_rel_t ADD CONSTRAINT kr_kim_role_rel_tr1
	FOREIGN KEY (role_id)
	REFERENCES kr_kim_role_t
	ON DELETE CASCADE
/

ALTER TABLE kr_kim_role_rel_t ADD CONSTRAINT kr_kim_role_rel_tr2
	FOREIGN KEY (contained_role_id)
	REFERENCES kr_kim_role_t
	ON DELETE CASCADE
/


-- New AuthZ


-- kr_kim_type_attribute_t

ALTER TABLE kr_kim_type_attribute_t ADD CONSTRAINT kr_kim_type_attribute_tr1
    FOREIGN KEY (kim_type_id)
    REFERENCES kr_kim_type_t
    ON DELETE CASCADE
/

ALTER TABLE kr_kim_type_attribute_t ADD CONSTRAINT kr_kim_type_attribute_tr2
    FOREIGN KEY (kim_attrib_id)
    REFERENCES kr_kim_attribute_t
    ON DELETE CASCADE
/

-- KIM Attribute Data Tables

ALTER TABLE kr_kim_group_attr_data_t ADD CONSTRAINT kr_kim_group_attr_data_tr1
    FOREIGN KEY (kim_type_id)
    REFERENCES kr_kim_type_t
/

ALTER TABLE kr_kim_group_attr_data_t ADD CONSTRAINT kr_kim_group_attr_data_tr2
    FOREIGN KEY (kim_attrib_id)
    REFERENCES kr_kim_attribute_t
/

ALTER TABLE kr_kim_group_attr_data_t ADD CONSTRAINT kr_kim_group_attr_data_tr3
    FOREIGN KEY (target_primary_key)
    REFERENCES kr_kim_group_t
    ON DELETE CASCADE
/


ALTER TABLE kr_kim_role_mbr_attr_data_t ADD CONSTRAINT kr_kim_role_mbr_attr_data_tr1
    FOREIGN KEY (kim_type_id)
    REFERENCES kr_kim_type_t
/

ALTER TABLE kr_kim_role_mbr_attr_data_t ADD CONSTRAINT kr_kim_role_mbr_attr_data_tr2
    FOREIGN KEY (kim_attrib_id)
    REFERENCES kr_kim_attribute_t
/

ALTER TABLE kr_kim_perm_attr_data_t ADD CONSTRAINT kr_kim_perm_attr_data_tr1
    FOREIGN KEY (kim_type_id)
    REFERENCES kr_kim_type_t
/

ALTER TABLE kr_kim_perm_attr_data_t ADD CONSTRAINT kr_kim_perm_attr_data_tr2
    FOREIGN KEY (kim_attrib_id)
    REFERENCES kr_kim_attribute_t
/

ALTER TABLE kr_kim_perm_attr_data_t ADD CONSTRAINT kr_kim_perm_attr_data_tr3
    FOREIGN KEY (target_primary_key)
    REFERENCES kr_kim_perm_t
    ON DELETE CASCADE
/

ALTER TABLE kr_kim_resp_attr_data_t ADD CONSTRAINT kr_kim_resp_attr_data_tr1
    FOREIGN KEY (kim_type_id)
    REFERENCES kr_kim_type_t
/

ALTER TABLE kr_kim_resp_attr_data_t ADD CONSTRAINT kr_kim_resp_attr_data_tr2
    FOREIGN KEY (kim_attrib_id)
    REFERENCES kr_kim_attribute_t
/

ALTER TABLE kr_kim_resp_attr_data_t ADD CONSTRAINT kr_kim_resp_attr_data_tr3
    FOREIGN KEY (target_primary_key)
    REFERENCES kr_kim_resp_t
    ON DELETE CASCADE
/

ALTER TABLE kr_kim_role_resp_t ADD CONSTRAINT kr_kim_role_resp_tr1
    FOREIGN KEY (resp_id)
    REFERENCES kr_kim_resp_t
/

ALTER TABLE kr_kim_resp_t ADD CONSTRAINT kr_kim_resp_tr1
    FOREIGN KEY (resp_tmpl_id)
    REFERENCES kr_kim_resp_tmpl_t
/

ALTER TABLE kr_kim_role_perm_t ADD CONSTRAINT kr_kim_role_perm_tr1
    FOREIGN KEY (perm_id)
    REFERENCES kr_kim_perm_t
/

ALTER TABLE kr_kim_perm_t ADD CONSTRAINT kr_kim_perm_tr1
    FOREIGN KEY (perm_tmpl_id)
    REFERENCES kr_kim_perm_tmpl_t
/

ALTER TABLE kr_kim_resp_tmpl_t ADD CONSTRAINT kr_kim_resp_tmpl_tr1
    FOREIGN KEY (kim_type_id)
    REFERENCES kr_kim_type_t
/
ALTER TABLE kr_kim_perm_tmpl_t ADD CONSTRAINT kr_kim_perm_tmpl_tr1
    FOREIGN KEY (kim_type_id)
    REFERENCES kr_kim_type_t
/

-- Delegation Tables

ALTER TABLE kr_kim_dele_t 
    ADD CONSTRAINT kr_kim_dele_tr1
    FOREIGN KEY ( role_id ) REFERENCES kr_kim_role_t
/

ALTER TABLE kr_kim_dele_t 
    ADD CONSTRAINT kr_kim_dele_tr2
    FOREIGN KEY ( typ_id ) REFERENCES kr_kim_type_t
/


ALTER TABLE kr_kim_dele_role_t 
    ADD CONSTRAINT kr_kim_dele_role_tr1
    FOREIGN KEY ( dele_id ) REFERENCES kr_kim_dele_t
/

ALTER TABLE kr_kim_dele_role_t 
    ADD CONSTRAINT kr_kim_dele_role_tr2
    FOREIGN KEY ( role_id ) REFERENCES kr_kim_role_t
/


ALTER TABLE kr_kim_dele_principal_t
    ADD CONSTRAINT kr_kim_dele_principal_tr1
    FOREIGN KEY ( dele_id ) REFERENCES kr_kim_dele_t
/


ALTER TABLE kr_kim_dele_group_t
    ADD CONSTRAINT kr_kim_dele_group_tr1
    FOREIGN KEY ( dele_id ) REFERENCES kr_kim_dele_t
/


ALTER TABLE kr_kim_dele_attr_data_t ADD CONSTRAINT kr_kim_dele_attr_data_tr1
    FOREIGN KEY (kim_type_id)
    REFERENCES kr_kim_type_t
/

ALTER TABLE kr_kim_dele_attr_data_t ADD CONSTRAINT kr_kim_dele_attr_data_tr2
    FOREIGN KEY (kim_attrib_id)
    REFERENCES kr_kim_attribute_t
/

ALTER TABLE kr_kim_dele_attr_data_t ADD CONSTRAINT kr_kim_dele_attr_data_tr3
    FOREIGN KEY (target_primary_key)
    REFERENCES kr_kim_dele_t
    ON DELETE CASCADE
/



ALTER TABLE kr_kim_dele_mbr_attr_data_t ADD CONSTRAINT kr_kim_dele_mbr_attr_data_tr1
    FOREIGN KEY (kim_type_id)
    REFERENCES kr_kim_type_t
/

ALTER TABLE kr_kim_dele_mbr_attr_data_t ADD CONSTRAINT kr_kim_dele_mbr_attr_data_tr2
    FOREIGN KEY (kim_attrib_id)
    REFERENCES kr_kim_attribute_t
/
