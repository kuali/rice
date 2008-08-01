-- kr_kim_entity_ent_type_t 

ALTER TABLE kr_kim_entity_ent_type_t ADD CONSTRAINT kr_kim_entity_ent_type_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_ent_type_t ADD CONSTRAINT kr_kim_entity_ent_type_tr2
	FOREIGN KEY (ent_type_cd)
	REFERENCES kr_kim_ent_type_t
	ON DELETE CASCADE
/

-- kr_kim_entity_name_t

ALTER TABLE kr_kim_entity_name_t ADD CONSTRAINT kr_kim_entity_name_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_name_t ADD CONSTRAINT kr_kim_entity_name_tr2
	FOREIGN KEY (ent_name_type_cd)
	REFERENCES kr_kim_ent_name_type_t
	ON DELETE CASCADE
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

-- kr_kim_entity_ext_key_t

ALTER TABLE kr_kim_entity_ext_key_t ADD CONSTRAINT kr_kim_entity_ext_key_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_ext_key_t ADD CONSTRAINT kr_kim_entity_ext_key_tr2
	FOREIGN KEY (ext_key_type_cd)
	REFERENCES kr_kim_ext_key_type_t
	ON DELETE CASCADE
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
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_addr_t ADD CONSTRAINT kr_kim_entity_addr_tr3
	FOREIGN KEY (postal_state_cd)
	REFERENCES sh_state_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_addr_t ADD CONSTRAINT kr_kim_entity_addr_tr4
	FOREIGN KEY (postal_zip_code)
	REFERENCES sh_zip_code_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_addr_t ADD CONSTRAINT kr_kim_entity_addr_tr5
	FOREIGN KEY (postal_cntry_cd)
	REFERENCES sh_country_t
	ON DELETE CASCADE
/


-- kr_kim_entity_email_t

ALTER TABLE kr_kim_entity_email_t ADD CONSTRAINT kr_kim_entity_email_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_email_t ADD CONSTRAINT kr_kim_entity_email_tr2
	FOREIGN KEY (email_type_cd)
	REFERENCES kr_kim_email_type_t
	ON DELETE CASCADE
/

-- kr_kim_entity_phone_t

ALTER TABLE kr_kim_entity_phone_t ADD CONSTRAINT kr_kim_entity_phone_tr1
	FOREIGN KEY (entity_id)
	REFERENCES kr_kim_entity_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_phone_t ADD CONSTRAINT kr_kim_entity_phone_tr2
	FOREIGN KEY (phone_type_cd)
	REFERENCES kr_kim_phone_type_t
	ON DELETE CASCADE
/
ALTER TABLE kr_kim_entity_phone_t ADD CONSTRAINT kr_kim_entity_phone_tr3
	FOREIGN KEY (postal_cntry_cd)
	REFERENCES sh_country_t
	ON DELETE CASCADE
/
