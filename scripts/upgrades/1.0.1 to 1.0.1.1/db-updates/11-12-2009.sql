-- KULRICE-3522 -- Drop application URL from KIM attribute definition table
alter table krim_attr_defn_t drop column appl_url
/ 

-- KULRICE-3685 --  Kim entity tables missing foreign-key relationships 
ALTER TABLE KRIM_ENTITY_ETHNIC_T 
add CONSTRAINT KRIM_ENTITY_ETHNIC_TR1
  FOREIGN KEY (entity_id)
  REFERENCES KRIM_ENTITY_T(entity_id)
/
ALTER TABLE KRIM_ENTITY_RESIDENCY_T  
add CONSTRAINT KRIM_ENTITY_RESIDENCY_TR1
  FOREIGN KEY (entity_id)
  REFERENCES KRIM_ENTITY_T(entity_id)
/
ALTER TABLE KRIM_ENTITY_VISA_T  
add CONSTRAINT KRIM_ENTITY_VISA_TR1
  FOREIGN KEY (entity_id)
  REFERENCES KRIM_ENTITY_T(entity_id)
/
  
