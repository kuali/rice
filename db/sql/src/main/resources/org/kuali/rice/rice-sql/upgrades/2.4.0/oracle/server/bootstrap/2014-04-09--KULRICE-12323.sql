-- This alters the size of the address fields on the address and person maintenance document tables so they are a bit longer and match
ALTER TABLE krim_entity_addr_t MODIFY (addr_line_1 VARCHAR2(128))
/
ALTER TABLE krim_entity_addr_t MODIFY (addr_line_2 VARCHAR2(128))
/
ALTER TABLE krim_entity_addr_t MODIFY (addr_line_3 VARCHAR2(128))
/

ALTER TABLE krim_pnd_addr_mt MODIFY (addr_line_1 VARCHAR2(128))
/
ALTER TABLE krim_pnd_addr_mt MODIFY (addr_line_2 VARCHAR2(128))
/
ALTER TABLE krim_pnd_addr_mt MODIFY (addr_line_3 VARCHAR2(128))
/
