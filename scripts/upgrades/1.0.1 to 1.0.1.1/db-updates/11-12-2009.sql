-- create new service def value table
--CREATE TABLE KRSB_SVC_DEF_VAL_T
--(
--      SVC_DEF_ID NUMBER(14) not null,
--      SVC_DEF CLOB,
--      CONSTRAINT KRSB_SVC_DEF_T PRIMARY KEY (SVC_DEF_ID)
--)
--/

--insert into KRSB_SVC_DEF_VAL_T (SVC_DEF_ID, SVC_DEF) (select SVC_DEF_ID, SVC_DEF from KRSB_SVC_DEF_T)
--/
--alter table KRSB_SVC_DEF_T drop COLUMN SVC_DEF
--/

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
  
